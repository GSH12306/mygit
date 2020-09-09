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
    String home(String body) {
    	System.out.println(String.valueOf(body));
        return String.valueOf(body);
    }
/*    @PostMapping("/abc")
    @ResponseBody 
    public String receivedfee( String body){
    	
    	System.out.println(String.valueOf(body));
    	return String.valueOf(body);
    }*/
    /*@ResponseBody
	@PostMapping("/postID")
	public Integer getInteger( Integer jsonObject){
	String a=jsonObject.toString();
	Integer id=Integer.parseInt(a);
	return id;
	}*/
    
}
