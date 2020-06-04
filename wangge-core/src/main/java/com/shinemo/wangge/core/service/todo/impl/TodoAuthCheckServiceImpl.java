package com.shinemo.wangge.core.service.todo.impl;

import com.shinemo.smartgrid.utils.SmartGridUtils;
import com.shinemo.todo.enums.ThirdTodoTypeEnum;
import com.shinemo.todo.vo.TodoThirdRequest;
import com.shinemo.wangge.core.service.todo.TodoAuthCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author shangkaihui
 * @Date 2020/6/4 16:02
 * @Desc
 */
@Slf4j
@Service
public class TodoAuthCheckServiceImpl implements TodoAuthCheckService {

    //秘钥map
    private Map<String, String> signKeyMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void initSignKeyMap() {
        signKeyMap.put("华为", "34b18faa-0424-41ad-b73b-80fc02d4be55");
        signKeyMap.put("亚信", "7095f283-4d05-4c5d-ad7c-0bb8879ce506");
        signKeyMap.put("装维", "e0d349cb-e98f-45e5-bf98-3a89d46d0671");
        signKeyMap.put("督导", "4313e55f-8234-4a0c-8cc6-6ed084a61df9");
    }


    @Override
    public Boolean checkSign(TodoThirdRequest todoThirdRequest) {
        //根据类型id,获取signKey
        Integer thirdTypeId = todoThirdRequest.getPostBody().getThirdType();
        String company = ThirdTodoTypeEnum.getById(thirdTypeId).getCompany();
        String signKey = signKeyMap.get(company);

        TreeMap<String, Object> map = new TreeMap<>();
        map.put("timeStamp", todoThirdRequest.getTimeStamp());
        map.put("method", todoThirdRequest.getMethod());
        map.put("postBody", todoThirdRequest.getPostBody());
        map.put("key", signKey);
        String sign = SmartGridUtils.genSign(map);
        if (!Objects.equals(sign, todoThirdRequest.getSign())) {
            log.error("[checkSign] 签名校验错误, request:{},result:{}", todoThirdRequest, sign);
            return false;
        }

        return true;
    }
}
