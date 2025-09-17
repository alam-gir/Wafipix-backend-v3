package com.wafipix.wafipix.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class controller {
    @GetMapping
    String home () {
        return System.getProperty("user.home");
    }
}
