package com.wafipix.wafipix.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class controller {
    @GetMapping
    String home () {
        return "Demo Api Called";
    }
}
