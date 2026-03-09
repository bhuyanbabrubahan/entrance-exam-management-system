package com.jee.publicapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping("/")
    public String home() {
    	System.out.println("Application Started......");
        return "Backend is running!";
        
    }
}

