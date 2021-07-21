package com.payconfirm.demo.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class PCRequest {

    private static final Logger logger = LoggerFactory.getLogger(PCRequest.class);

    private String url;
    private String request;
    private String expAnswer;
    private String result;
    private String errDesc;
    private int errCode;

    /**
     * Function to make regular call to PC
     * see https://repo.payconfirm.org/server/doc/v5/rest-api/#introduction
     *
     * @param: String url URL to make a call, should be exact URL of required method
     * @param: String request JSON with the request, can be null
     * @param: String expected_answer Expected answer type. For example, for create user it will be `user_created`
     *
     * @return: PCRequest object
     * @return: String result Variable to return an answer in JSON format
     * @return: String error_description Variable to return error description if happened
     * @return: Int error_code Variable to return error code if happened
     */
    public PCRequest(String url, String request, String expAnswer, String result, String errDesc, int errCode) {
        this.url = url;
        this.request = request;
        this.expAnswer = expAnswer;
        this.result = result;
        this.errDesc = errDesc;
        this.errCode = errCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getExpAnswer() {
        return expAnswer;
    }

    public void setExpAnswer(String expAnswer) {
        this.expAnswer = expAnswer;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrDesc() {
        return errDesc;
    }

    public void setErrDesc(String errDesc) {
        this.errDesc = errDesc;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    /**
     * Function to make regular call to PC
     * see https://repo.payconfirm.org/server/doc/v5/rest-api/#introduction
     *
     * @return: Boolean true if request was success, false if not
     */
    public boolean send() {

        try {

            if ((this.url.isEmpty()) || (this.url == "")) {
                return false;
            }

            logger.info("Connecting to: " + this.url);
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);

            if (this.request.isEmpty()){
                connection.setRequestMethod("GET");
            }
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-type", "application/json");
            // 20 secs to connect, 20 secs to read
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(20000);
            // Write body
            logger.info("Sending data: " + this.request);
            if (!this.request.isEmpty()) {
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
                writer.write(this.request);
                writer.close();
                wr.flush();
                wr.close();
            }
            // Get response
            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();
            logger.info("Received response: " + responseCode + " " + responseMessage);

            // Save response body
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[4 * 1024];
            int length;
            InputStream is = responseCode == 200 ? connection.getInputStream() : connection.getErrorStream();
            while ((length = is.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String result_string = result.toString("UTF-8");
            logger.info("Response body: " + result_string);

            if (responseCode == -1) {
                return false;
            }

            JSONObject output_json = new JSONObject(result_string);
            JSONObject pc_answer = output_json.getJSONObject("answer");
            JSONObject pc_result = pc_answer.getJSONObject("result");

            if (pc_result.getInt("error_code") != 0) {
                this.result =  pc_answer.toString();
                this.errDesc = pc_result.getString("error_message");
                this.errCode = pc_result.getInt("error_code");
                return false;
            }

            else if ((this.expAnswer.isEmpty()) || (this.expAnswer.equals(""))) {
                this.result = pc_answer.toString();
                this.errDesc = pc_result.getString("error_message");
                this.errCode = pc_result.getInt("error_code");
                return true;
            }

            else if (!output_json.getString("answer_type").equals(this.expAnswer)) {
                this.result = pc_answer.toString();
                this.errDesc = "Non expected answer from PC Server";
                this.errCode = pc_result.getInt("error_code");
                return false;
            }

            if (output_json.getString("answer_type").equals(this.expAnswer) || result_string != null) {
                this.result = pc_answer.toString();
                this.errDesc = pc_result.getString("error_message");
                this.errCode = pc_result.getInt("error_code");
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Caught " + e.getClass().getSimpleName()
                    + " while trying to perform post request: " + e.getMessage());
            return false;
        }
        return true;
    }
}
