package com.payconfirm.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

  private static final Logger logger = LoggerFactory.getLogger(WebController.class);

  @GetMapping(value="/")
  public String homepage(){
    logger.info("[Homepage]: Index page for personalization");
    return "index";
  }

}
