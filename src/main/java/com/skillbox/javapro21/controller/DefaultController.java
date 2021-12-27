package com.skillbox.javapro21.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class DefaultController {

    @GetMapping("/")
    public String index() {
        return "static/index.html";
    }
}

