package com.payconfirm.demo.service.impl;

import com.payconfirm.demo.database.Customer;
import com.payconfirm.demo.exception.CustomException;
import com.payconfirm.demo.exception.InternalServerError;
import com.payconfirm.demo.model.CreateAliasResponse;
import com.payconfirm.demo.model.GetPCUserAliasResponse;
import com.payconfirm.demo.model.GetPCUserAliasRequest;
import com.payconfirm.demo.repository.CustomerRepository;
import com.payconfirm.demo.service.PCRequest;
import com.payconfirm.demo.service.PersoAliasService;
import com.payconfirm.demo.service.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PersoAliasServiceImpl implements PersoAliasService {

    private static final Logger logger = LoggerFactory.getLogger(PersoAliasServiceImpl.class);

    @Value("${payconfirm.pc_url}")
    private String pc_url;

    @Value("${payconfirm.system_id}")
    private String system_id;

    @Value("${payconfirm.persistent_alias}")
    private boolean persistent_alias;

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Code to create a new "Alias" to get PC User in JSON format
     *
     * Personalization by Alias consists of following steps:
     *  - create alias and store them in database (in real application you should use database)
     *    done by this code
     *  - provide the Alias value to a user
     *  - a user types this alias into the mobile app
     *  - mobile app makes a request to this sample to /pers/alias/get_pc_user
     *    and provides the Alias
     *  - /pers/alias/get_pc_user calls PC Server to create a PC User and export them in JSON-format
     *    to encrypt a keys this code uses "activation_code" value
     *    see https://repo.payconfirm.org/server/doc/v5/rest-api/#json-export-key
     *  - mobile app imports a PC Users and asks from a user activation_code value
     *  - done
     */
    public CreateAliasResponse genCredential() {

        // create random Alias
        String al = Utils.generateRandomString(8,"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        logger.info("Alias: " + al);

        // create activation_code for this Alias
        //
        // !!! WARNING - we create Activation Code here for DEMO PURPOSES ONLY
        //     You should create Activation Code at the moment when you requests key JSON from PC Server
        //     After you have created Activation Code you should send it to a user with another channel
        //     It can be email, SMS, push or something else
        //
        //     We can not send SMS or something here in demo, that's why we create activation code here
        String ac = Utils.generateRandomString(10, "0123456789abcdefghijklmnopqrstuvwxyz");
        logger.info("Activation code: " + ac);

        // store Alias, activation code and PC User ID for the alias to database
        //   In real Application you should
        //     - use database
        //     - DO NOT STORE ACTIVATION CODE, see comment above
        /**
         * Function to store Alias in database
         *
         * @param: String alias Alias object
         */
        Customer customer = new Customer();
        customer.setAlias(al);
        customer.setActivation_code(ac);
        customer.setUser_id(null); // null, because of there is no PC User ID for now. It will be created in get_pc_user
        customerRepository.save(customer);

        logger.info(customer.toString());

        // Format and return the result
        CreateAliasResponse createAliasResponse = new CreateAliasResponse();
        createAliasResponse.setAlias(al);

        // !!! WARNING - we create Activation Code here for DEMO PURPOSES ONLY
        //     You should create Activation Code at the moment when you requests key JSON from PC Server
        //     After you have created Activation Code you should send it to a user with another channel
        //     It can be email, SMS, push or something else
        //
        //     We can not send SMS or something here in demo, that's why we create activation code here
        createAliasResponse.setActivation_code(ac);

        logger.info("Response: " + new JSONObject(createAliasResponse));

        return createAliasResponse;
    }

    /**
     * Code to get PC User in JSON format by created Alias
     *
     * Personalization by Alias consists of following steps:
     *  - create alias and store them in database (in real application you should use database)
     *    done by /pers/alias/create_alias
     *  - provide the Alias value to a user
     *  - a user types this alias into the mobile app
     *  - mobile app makes a request to this sample to /pers/alias/get_pc_user (this code)
     *    and provides the Alias
     *  - /pers/alias/get_pc_userr calls PC Server to create a PC User and export them in JSON-format
     *    to encrypt a keys this code uses "activation_code" value
     *    see https://repo.payconfirm.org/server/doc/v5/rest-api/#json-export-key
     *  - mobile app imports a PC Users and asks from a user activation_code value
     *  - done
     *
     * Input JSON sample:
     *    {"alias":"F33P27ON"}
     */
    public GetPCUserAliasResponse getPCUserAlias(GetPCUserAliasRequest request){

        logger.info("Request: " + new JSONObject(request));

        // read input JSON
        String al = request.getAlias();

        logger.info("Alias: " + al);

        // check if alias is specified in the request
        if((al.isEmpty()) || (al == "") || (al == null)) {
            String er = "{\"error_description\":\"" + "alias value not specified" + "\"}";
            logger.info("Throw exception: " + er);
            throw new CustomException(er);
        }

        Customer customer;

        // check if alias value was created earlier (stored in database)
        /**
         * Function to get Alias from database
         *
         * param: String alias Alias string
         *
         * return: Object Alias object
         */
        customer = customerRepository.findByAlias(al);
        if(customer == null) {
            String er = "{\"error_description\":\"" + "alias not found" + "\"}";
            logger.info("Throw exception: " + er);
            throw new CustomException(er);
        }

        logger.info(customer.toString());

        String ac = customer.getActivation_code();

        logger.info("Activation Code: " + ac);

        String pc_request_url;  // create PC User endpoint
        JSONObject jsonObject = new JSONObject();

        String uid = customer.getUser_id();

        String expectedAnswer;

        // check if the alias already has been used and it has corresponded PC User ID
        if (uid != null) {

            logger.info("PC User Id " + customer.getUser_id() + " from Alias " + customer.getAlias() + " already exist. Update PC User Id.");

            // if pc_user_id has been set, then we call PC User Update
            pc_request_url = pc_url + system_id + "/users/" + uid + "/key"; // update PC User endpoint

            // Update user params
            JSONObject keyParam = new JSONObject();
            keyParam.put("with_finger_print", false);
            keyParam.put("collect_events", true);
            keyParam.put("collect_device_info", true);
            keyParam.put("collect_device_SIM_info", true);
            keyParam.put("collect_device_location", true);
            keyParam.put("pass_policy", 0);
            keyParam.put("deny_store_with_OS_protection", true);
            keyParam.put("deny_renew_public_key", false);
            keyParam.put("scoring_enabled", true);
            keyParam.put("autosign_enabled", true);
            keyParam.put("remote_update_enabled", true);
            keyParam.put("server_signer", false);

            jsonObject.put("is_delayed",false);
            jsonObject.put("key_params",keyParam);
            jsonObject.put("key_encryption_password", ac); // activation code
            jsonObject.put("return_key_method", "KEY_JSON");

            expectedAnswer = "key_updated"; // expected answer from PC Server
        }
        else {
            // if pc_user_id has been not set, then we create PC User

            pc_request_url = pc_url + system_id + "/users"; // create PC User endpoind

            // Create user params
            JSONObject keyParam = new JSONObject();
            keyParam.put("with_finger_print", false);
            keyParam.put("collect_events", true);
            keyParam.put("collect_device_info", true);
            keyParam.put("collect_device_SIM_info", true);
            keyParam.put("collect_device_location", true);
            keyParam.put("pass_policy", 0);
            keyParam.put("deny_store_with_OS_protection", true);
            keyParam.put("deny_renew_public_key", false);
            keyParam.put("scoring_enabled", true);
            keyParam.put("autosign_enabled", true);
            keyParam.put("remote_update_enabled", true);
            keyParam.put("server_signer", false);

            jsonObject.put("id_prefix","rest-api-test-");
            jsonObject.put("key_params",keyParam);
            jsonObject.put("key_encryption_password", ac); // activation code
            jsonObject.put("return_key_method", "KEY_JSON");

            expectedAnswer = "user_created"; // expected answer from PC Server
        }

        String result = "";
        String errDesc = "";
        int errCode = 0;

        logger.info("PC request URL: " + pc_request_url);

        // !!! WARNING - we use stored Activation Code for DEMO PURPOSES ONLY
        //     You should create Activation Code at the moment when you requests key JSON from PC Server
        //     It means - here
        //     After you have created Activation Code you should send it to a user with another channel
        //     It can be email, SMS, push or something else
        //
        //     We can not send SMS or something here in demo, that's why we use stored activation code

        // encode params to JSON
        logger.info("PC request message: " + jsonObject.toString());
        logger.info("PC request expected answer: " + expectedAnswer);

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

        // store PC User ID to alias value
        if(persistent_alias){

            logger.info("Persistent alias: " + persistent_alias);

            JSONObject answerObj = new JSONObject(pcRequest.getResult());
            JSONObject createdObj = answerObj.getJSONObject(expectedAnswer);
            String userid = createdObj.getString("user_id");

            /**
             * Function to store Alias in database
             *
             * @param: String alias Alias object
             */
            customer.setAlias(al);
            customer.setActivation_code(ac);
            customer.setUser_id(userid);
            customerRepository.save(customer);

            logger.info(customer.toString());
        }

        // Format and return the result
        JSONObject answerObj = new JSONObject(pcRequest.getResult());
        JSONObject createdObj = answerObj.getJSONObject(expectedAnswer);
        String keyjson = createdObj.getString("key_json");

        GetPCUserAliasResponse getPcUserAliasResponse = new GetPCUserAliasResponse();
        getPcUserAliasResponse.setKey_json(keyjson);

        logger.info("Response: " + new JSONObject(getPcUserAliasResponse));

        return getPcUserAliasResponse;
    }
}
