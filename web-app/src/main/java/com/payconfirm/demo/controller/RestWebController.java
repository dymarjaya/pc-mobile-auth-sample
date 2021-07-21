package com.payconfirm.demo.controller;

import com.payconfirm.demo.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.payconfirm.demo.service.AuthenticationService;
import com.payconfirm.demo.service.EventsPostService;
import com.payconfirm.demo.service.PersoAliasService;
import com.payconfirm.demo.service.PersoQRService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Configuration
@RestController
public class RestWebController {

    private static final Logger logger = LoggerFactory.getLogger(RestWebController.class);

    @Autowired
    private PersoAliasService persoAliasService;

    @Autowired
    private PersoQRService persoQRService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private EventsPostService eventsPostService;

    @PostMapping(value = "/pers/alias/create_alias")
    public CreateAliasResponse create_alias() {
        logger.info("[Personalization]: Create Alias and Activation Code");
        return persoAliasService.genCredential();
    }

    @PostMapping(value = "/pers/alias/get_pc_user", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public GetPCUserAliasResponse get_pc_user_alias(@RequestBody GetPCUserAliasRequest request) {
        logger.info("[Personalization]: Get Alias and Activation Code");
        return persoAliasService.getPCUserAlias(request);
    }

    @PostMapping(value = "/pers/qr/create_pc_user_qr")
    public CreatePCUserQRResponse create_pc_user_qr() {
        logger.info("[Personalization]: Create PC User QR");
        return persoQRService.createPCUserQR();
    }

    @PostMapping(value = "/auth/start_authentication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public AuthResponse start_authentication(@RequestBody AuthRequest request) {
        logger.info("[Authentication]: Start authentication");
        return authenticationService.startAuthentication(request);
    }

    @PostMapping(value = "/auth/finish_authentication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public FinishAuthResponse finish_authentication(@RequestBody AuthRequest request) {
        logger.info("[Authentication]: Finish authentication");
        return authenticationService.finishAuthentication(request);
    }

    @PostMapping(value = "/pc_callback_reciever", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void pc_callback_reciever(@RequestBody String request) {
        logger.info("[Authentication]: Callback receiver");
        authenticationService.callbackReceiver(request);
    }

    @PostMapping(value = "/fds", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void events_post_callback(@RequestBody String request) throws JsonProcessingException {
        logger.info("[FDS]: Events Post Callback receiver");
        eventsPostService.eventPostCallback(request);
    }

}
