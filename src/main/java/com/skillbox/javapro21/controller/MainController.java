package com.skillbox.javapro21.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class MainController {

    @RequestMapping("/api/v1")
    public String redirectToIndex() {
        return "index";
    }
}
