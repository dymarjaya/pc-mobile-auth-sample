package com.payconfirm.demo.service.impl;

import com.payconfirm.demo.service.EventsPostService;
import com.payconfirm.demo.service.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EventsPostServiceImpl implements EventsPostService {

    private static final Logger logger = LoggerFactory.getLogger(EventsPostServiceImpl.class);

    @Value("${payconfirm.events_post_url_log}")
    private String events_post_url_log;

    public void eventPostCallback(String request){

        logger.info("Events Post callback message: " + request);

        JSONObject json = new JSONObject(request); // Convert text to object
        String jsonBeautify = json.toString(4);
        Utils.appendStrToFile(events_post_url_log, jsonBeautify);

    }
}
