package com.guo.controller;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
@RestController
@RequestMapping("/sample")
public class SampleController {
	private static Logger logger = Logger.getLogger(SampleController.class);
    @PostMapping("/home")
    public String home(@RequestBody String jsonBody) {
    	//JSONObject json = JSON.parseObject(jsonBody);
    	System.out.println(String.valueOf(jsonBody));
        return "接收成功";
    }
    @RequestMapping("/hello")
    @ResponseBody
    public String web(){
    	System.out.println("arrive");
    	return "你好 少年";
    }
/*    @PostMapping("/abc")
    @ResponseBody 
    public String receivedfee( String body){
    	
    	System.out.println(String.valueOf(body));
    	return String.valueOf(body);
    }*/
    @PostMapping("/fee")
    @ResponseBody
    public static void onFeeEvent(String jsonBody) {
        // 封装JSON请求
        JSONObject json = JSON.parseObject(jsonBody);
        System.out.println(json.toString());
        String eventType = json.getString("eventType"); // 通知事件类型
        
        if (!("fee".equalsIgnoreCase(eventType))) {
            logger.info("EventType error: " + eventType);
            return;
        }

        if (!(json.containsKey("feeLst"))) {
            logger.info("param error: no feeLst.");
            return;
        }
        JSONArray feeLst = json.getJSONArray("feeLst"); // 呼叫话单事件信息
      
        //短时间内有多个通话结束时RTC业务平台会将话单合并推送,每条消息最多携带50个话单
        if (feeLst.size() > 1) {
            for (Object loop : feeLst) {
                if (((JSONObject)loop).containsKey("sessionId")) {
                    logger.info("sessionId: " + ((JSONObject)loop).getString("sessionId"));
                }
            }
        } else if (feeLst.size() == 1) {
            if (feeLst.getJSONObject(0).containsKey("sessionId")) {
                logger.info("sessionId: " + feeLst.getJSONObject(0).getString("sessionId"));
            }
        } else {
            logger.info("feeLst error: no element.");
        }
    }
    
}
