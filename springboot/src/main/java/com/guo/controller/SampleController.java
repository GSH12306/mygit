package com.guo.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableAutoConfiguration
@RequestMapping("/sample")
public class SampleController {
 
    @RequestMapping("/home")
    @ResponseBody
    String home() {
        return "Hello World!";
    }
}
