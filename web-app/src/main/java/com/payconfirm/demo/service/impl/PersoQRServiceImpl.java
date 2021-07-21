package com.payconfirm.demo.service.impl;

import com.payconfirm.demo.exception.InternalServerError;
import com.payconfirm.demo.model.CreatePCUserQRResponse;
import com.payconfirm.demo.service.PCRequest;
import com.payconfirm.demo.service.PersoQRService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PersoQRServiceImpl implements PersoQRService {

    private static final Logger logger = LoggerFactory.getLogger(PersoQRServiceImpl.class);

    @Value("${payconfirm.pc_url}")
    private String pc_url;

    @Value("${payconfirm.system_id}")
    private String system_id;

    /**
     *      Code to create a new PC User
     *      see https://repo.payconfirm.org/server/doc/v5/rest-api/#create-user
     */
    public CreatePCUserQRResponse createPCUserQR() {

        // Create user params
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id_prefix","sample-");
        jsonObject.put("return_key_method", "FULL_QR");

        String expectedAnswer = "user_created";
        String result = "";
        String errDesc = "";
        int errCode = 0;

        // build request url
        String pc_request_url = pc_url + system_id + "/users";   // create PC User endpoint

        logger.info("PC request URL: " + pc_request_url);

        // encode params to JSON
        logger.info("PC request message: " + jsonObject.toString());
        logger.info("PC request expected answer: " + expectedAnswer);

        // make a request
        PCRequest pcRequest = new PCRequest(pc_request_url,jsonObject.toString(), expectedAnswer, result, errDesc, errCode);

        // if error - throw exception
        if(!pcRequest.send()) {
            String er = "Call to PC failed. " + "{\"error_code\":\"" + pcRequest.getErrCode() + "\"},{\"error_description\":\""+ pcRequest.getErrDesc() + "\"}";
            logger.info("Throw exception: " + er);
            throw new InternalServerError(er);
        }

        logger.info("PC Result message: " + pcRequest.getResult());
        logger.info("PC Result error description: " + pcRequest.getErrDesc());
        logger.info("PC Result error code: " + pcRequest.getErrCode());

        // Format and return the result
        JSONObject answerObj = new JSONObject(pcRequest.getResult());
        JSONObject usercreatedObj = answerObj.getJSONObject("user_created");
        String userid = usercreatedObj.getString("user_id");
        String keyqr = usercreatedObj.getString("key_QR");

        CreatePCUserQRResponse createPCUserQRResponse = new CreatePCUserQRResponse();
        createPCUserQRResponse.setUser_id(userid);
        createPCUserQRResponse.setUser_qr(keyqr);

        logger.info("Response: " + new JSONObject(createPCUserQRResponse));

        return createPCUserQRResponse;
    }

}
