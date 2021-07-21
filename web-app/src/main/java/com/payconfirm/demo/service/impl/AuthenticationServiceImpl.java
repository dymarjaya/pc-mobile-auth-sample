package com.payconfirm.demo.service.impl;

import com.payconfirm.demo.database.Transaction;
import com.payconfirm.demo.exception.CustomException;
import com.payconfirm.demo.exception.InternalServerError;
import com.payconfirm.demo.model.AuthRequest;
import com.payconfirm.demo.model.AuthResponse;
import com.payconfirm.demo.model.FinishAuthResponse;
import com.payconfirm.demo.repository.TransactionRepository;
import com.payconfirm.demo.service.AuthenticationService;
import com.payconfirm.demo.service.PCRequest;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import static com.payconfirm.demo.service.Utils.bytesToHex;
import static com.payconfirm.demo.service.Utils.randomBytes;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Value("${payconfirm.pc_url}")
    private String pc_url;

    @Value("${payconfirm.system_id}")
    private String system_id;

    @Value("${payconfirm.callback_receiver_url}")
    private String callback_receiver_url;

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Code to start authentication process with PC
     *
     * This code
     *   - receives PC User ID
     *   - creates a PC transaction (see https://repo.payconfirm.org/server/doc/v5/rest-api/#create-transaction)
     *   - remembers transaction ID for this user in database to process it
     *     after the transaction will be confirmed by a user with mobile phone
     *
     * Notes:
     *   - Real back-end Application should use own user identifiers for a users
     *   - PC User ID should be stored with Application's User ID after Personalization process
     *   - to handle PC Transaction it's better to use database, sessions and all of this stuff
     *   - to store transaction IDs in database - it's just for sample purposes
     *
     * After this code has been called, Mobile Device with PC SDK and personalized with specified PC User ID
     * should confirm (digitally sign) created transaction
     *
     * It will lead to callback from PC Server to /pc_callback_reciever and changing status to
     * 'confirmed' or 'declined'
     *
     * If status will be 'confirmed', then a user can be authorized (authentication successful)
     *
     * Authorization process (grant to a user rights to acceess) is handled
     * by /auth/finish_authentication code
     *
     *
     * Input JSON sample:
     *    {"pc_user_id":"sample-e7315c7d-7176-4431-a3f7-5343f2f86146"}
     */
    public AuthResponse startAuthentication(AuthRequest request) {

        logger.info("Request: " + new JSONObject(request));

        // read input JSON
        String user_id = request.getPc_user_id();

        // check if pc_user_id is specified in the request
        if((user_id.isEmpty()) || (user_id == "") || (user_id == null)) {
            String er = "{\"error_description\":\"" + "pc_user_id not specified" + "\"}";
            logger.info("Throw exception: " + er);
            throw new CustomException(er);
        }

        // Get PC User ID
        logger.info("PC User Id: " + user_id);

        // Params to create PC Transaction, see https://repo.payconfirm.org/server/doc/v5/rest-api/#create-transaction
        JSONObject jsonObject = new JSONObject();
        JSONObject item = new JSONObject();
        item.put("text", bytesToHex(randomBytes(32))); // to authenticate a user we can use some random transaction content
        jsonObject.put("transaction_data", item);
        jsonObject.put("callback_url", callback_receiver_url);

        String expectedAnswer = "transaction_created";
        String result = "";
        String errDesc = "";
        int errCode = 0;

        // encode params to JSON
        logger.info("PC request message: " + jsonObject.toString());
        logger.info("PC request expected answer: " + expectedAnswer);

        // build request url
        String pc_request_url = pc_url + system_id + "/users/" + user_id + "/transactions";   // create PC User endpoint
        logger.info("PC request URL: " + pc_request_url);

        // make a request
        PCRequest pcRequest = new PCRequest(pc_request_url,jsonObject.toString(),expectedAnswer, result, errDesc, errCode);

        // if error - throw exception
        if(!pcRequest.send()) {
            String er = "Call to PC failed. " + "{\"error_code\":\"" + pcRequest.getErrCode() + "\"},{\"error_description\":\""+ pcRequest.getErrDesc() + "\"}";
            logger.info("Throw exception: " + er);
            throw new InternalServerError(er);
        }

        logger.info("PC Result message: " + pcRequest.getResult());
        logger.info("PC Result error description: " + pcRequest.getErrDesc());
        logger.info("PC Result error code: " + pcRequest.getErrCode());

        JSONObject answerObj = new JSONObject(pcRequest.getResult());
        JSONObject transactcreatedObj = answerObj.getJSONObject("transaction_created");

        // get transaction id
        String transactionId = transactcreatedObj.getString("transaction_id");

        // store the transaction id in database
        /**
         * Function to store transaction info in database
         *
         * @param: String user_id PC User ID
         * @param: String transaction_id PC Transaction ID
         * @param: String status Status to store, can be 'created', 'confirmed', 'declined'
         */
        Transaction transaction = new Transaction();
        transaction.setUserid(user_id);
        transaction.setTransactionid(transactionId);
        transaction.setStatus("created");
        transactionRepository.save(transaction);

        logger.info(transaction.toString());

        // Format and return the result
        AuthResponse authResponse = new AuthResponse();
        authResponse.setTransaction_id(transactionId);

        logger.info("Response: " + new JSONObject(authResponse));

        return authResponse;
    }

    /**
     * Code to Authorize a user (grant to a user rights to access)
     *
     * This is just a sample code
     *
     * It returns true in case of authentication successful, false in other case
     * See description in /auth/start_authentication process
     *
     * Your real authorization code should issue something like access-token
     * or set specialized cookie
     *
     * Input JSON sample:
     *    {"pc_user_id":"sample-e7315c7d-7176-4431-a3f7-5343f2f86146"}
     *
     */
    public FinishAuthResponse finishAuthentication(AuthRequest request) {

        logger.info("Request: " + new JSONObject(request));

        // read input JSON
        String user_id = request.getPc_user_id();

        // check if pc_user_id is specified in the request
        if((user_id.isEmpty()) || (user_id == "") || (user_id == null)) {
            String er = "{\"error_description\":\"" + "pc_user_id not specified" + "\"}";
            logger.info("Throw exception: " + er);
            throw new CustomException(er);
        }

        // Get PC User ID
        logger.info("PC User Id: " + user_id);

        Transaction transaction;
        transaction = transactionRepository.findTopByOrderByIdDesc();

        logger.info(transaction.toString());

        FinishAuthResponse finishAuthResponse = new FinishAuthResponse();

        // Set result, false by default
        /**
         * Function get transaction info from database
         *
         * @param: String user_id PC User ID
         * @param: String transaction_id PC Transaction ID
         *
         * @return: String Status, can be 'created', 'confirmed', 'declined'
         */
        if((transaction.getStatus()).equals("confirmed")){
            finishAuthResponse.setAuthentication_successfull(true);
        }
        else {
            finishAuthResponse.setAuthentication_successfull(false);
        }

        logger.info("Response: " + new JSONObject(finishAuthResponse));

        return finishAuthResponse;
    }

    /**
     * Code to receive callback from PC Server with information about transactions
     *
     * This code handles transaction status changing after user actions: confirm or decline
     * See https://repo.payconfirm.org/server/doc/v5/rest-api/#transactions-endpoint
     *
     * If status will be 'confirmed', then a user can be authorized (authentication successful)
     *
     * Authorization process (grant to a user rights to access) is handled
     * by /auth/finish_authentication
     *
     */
    public void callbackReceiver(String request){

        logger.info("Authentication callback message: " + request);

        // read input JSON
        JSONObject answerObj = new JSONObject(request);

        JSONObject pc_callback = answerObj.getJSONObject("pc_callback");
        String type = pc_callback.getString("type");
        int version = pc_callback.getInt("version");

        // check if we can not parse the callback
        if( type.isEmpty() || (version < 1)) {
            String er = "{\"error_description\":\"" + "Cannot parse the callback" + "\"}";
            logger.info("Throw exception: " + er);
            throw new CustomException(er);
        }

        // if there is not our callback
        if( !type.equals("transaction_callback") || (version != 3)){
            String er = "{\"error_description\":\"" + "There is no PC callback" + "\"}";
            logger.info("Throw exception: " + er);
            throw new CustomException(er);
        }

        JSONObject transaction_resultObj = pc_callback.getJSONObject("result");
        int error_code = transaction_resultObj.getInt("error_code");

        // check if there was a error
        if( error_code != 0) {
            String er = "{\"error_description\":\"" + "error_code is not 0" + "\"}";
            logger.info("Throw exception: " + er);
            throw new CustomException(er);
        }

        JSONObject transaction_callbackObj = pc_callback.getJSONObject("transaction_callback");
        JSONObject confirmation = transaction_callbackObj.getJSONObject("confirmation");

        // get new status
        String status = "declined";  // Declined by default
        if (!confirmation.isEmpty()){
            status = "confirmed"; // change to declined if there was declanation
        }

        String transaction_id = transaction_callbackObj.getString("transaction_id");

         /**
         * Function get transaction info from database
         *
         * @param: String user_id PC User ID
         * @param: String transaction_id PC Transaction ID
         *
         * @return: String Status, can be 'created', 'confirmed', 'declined'
         */
        Transaction transaction;
        transaction = transactionRepository.findByTransactionid(transaction_id);

        logger.info(transaction.toString());

        // store the status
        /**
         * Function to store transaction info in database
         *
         * @param: String user_id PC User ID
         * @param: String transaction_id PC Transaction ID
         * @param: String status Status to store, can be 'created', 'confirmed', 'declined'
         */
        transaction.setUserid(transaction.getUserid());
        transaction.setTransactionid(transaction_id);
        transaction.setStatus(status);
        transactionRepository.save(transaction);

        logger.info(transaction.toString());
    }
}
