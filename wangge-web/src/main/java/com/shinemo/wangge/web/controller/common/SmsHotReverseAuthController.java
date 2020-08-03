package com.shinemo.wangge.web.controller.common;


import com.shinemo.common.annotation.SmIgnore;
import com.shinemo.my.redis.service.RedisService;
import com.shinemo.stallup.domain.request.SmsHotReverseAuthRequest;
import com.shinemo.stallup.domain.response.SmsHotReverseAuthResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/smartgrid")
public class SmsHotReverseAuthController {

    @Resource
    private RedisService redisService;


    @PostMapping("/reverse/auth")
    @SmIgnore
    public SmsHotReverseAuthResponse reverseAuth(@RequestBody SmsHotReverseAuthRequest request) {
        log.info("[reverseAuth] reverseAuth start,request = {}",request);


        SmsHotReverseAuthResponse response = new SmsHotReverseAuthResponse();
        response.setCode(200);

        //if (request.getMobile().equals("13396631940") ||
        //        request.getMobile().equals("18790513853") ||
        //        request.getMobile().equals("18817350871") || request.getMobile().equals("13557710513")
        //        || request.getMobile().equals("13607713224")) {
        //    response.setMobile(request.getMobile());
        //    return response;
        //}

        try {
            if (request == null) {
                response.setCode(400);
                response.setMsg("request is null");
                return response;
            }
            if (StringUtils.isBlank(request.getAuthKey())) {
                response.setCode(400);
                response.setMsg("authKey is null");
                return response;
            }
            if (StringUtils.isBlank(request.getMobile())) {
                response.setCode(400);
                response.setMsg("mobile is null");
                return response;
            }

            if (StringUtils.isBlank(request.getSourceKey())) {
                response.setCode(400);
                response.setMsg("sourceKey is null");
                return response;
            }
            String redisValue = redisService.get(request.getAuthKey());
            if (StringUtils.isBlank(redisValue)) {
                response.setCode(403);
                response.setMsg("authKey is error");
                return response;
            }
            String[] splits = redisValue.split(",");
            String mobile = splits[0];
            String sourceKey = splits[1];
            String requestSourceKey = new String(Base64.decodeBase64(request.getSourceKey()), StandardCharsets.UTF_8);
            if (!sourceKey.equals(requestSourceKey)) {
                log.error("[reverseAuth]reverseAuth failed,requestSourceKey={},sourceKey={}",requestSourceKey,sourceKey);
                response.setCode(403);
                response.setMsg("sourceKey is error");
                return response;
            }
            if (!mobile.equals(request.getMobile())) {
                log.error("[reverseAuth]reverseAuth failed,requestMobile={},mobile={}",request.getMobile(),sourceKey);
                response.setCode(403);
                response.setMsg("mobile is error");
                return response;
            }
            response.setMobile(request.getMobile());
            return response;
        }catch (Exception e) {
            log.error("[reverseAuth]reverseAuth error,request = {},error = {}",request,e);
            response.setCode(500);
            response.setMsg("服务器异常");
            return response;
        }
    }
}
