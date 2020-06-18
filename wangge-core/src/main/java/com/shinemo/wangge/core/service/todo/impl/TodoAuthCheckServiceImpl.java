package com.shinemo.wangge.core.service.todo.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.utils.SmartGridUtils;
import com.shinemo.todo.enums.ThirdTodoTypeEnum;
import com.shinemo.todo.error.TodoErrorCodes;
import com.shinemo.todo.vo.TodoDTO;
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
        signKeyMap.put("浩鲸", "e0d349cb-e98f-45e5-bf98-3a89d46d0671");
        signKeyMap.put("年华", "4313e55f-8234-4a0c-8cc6-6ed084a61df9");
    }


    @Override
    public ApiResult<TodoDTO> checkSign(TreeMap<String, Object> treeMap) {
        //校验签名
        TodoDTO todoDTO = MapUtil.get(treeMap, "postBody", TodoDTO.class);
        if (todoDTO == null || todoDTO.getThirdType() == null) {
            throw new ApiException("thirdType is null", 500);
        }

        Boolean ignoreCheckSign = MapUtil.getBool(treeMap, "ignoreCheckSign");
        //本地方法不需要校验签名
        if (ignoreCheckSign != null && ignoreCheckSign) {
            return ApiResult.of(0, todoDTO);
        }

        String oldSign = MapUtil.getStr(treeMap, "sign");
        String method = MapUtil.getStr(treeMap, "method");
        Long timeStamp = MapUtil.getLong(treeMap, "timeStamp");
        Assert.notNull(oldSign, "sign is null");
        Assert.notNull(method, "method is null");
        Assert.notNull(timeStamp, "timeStamp is null");

        //根据类型id,获取signKey
        Integer thirdTypeId = todoDTO.getThirdType();
        String company = ThirdTodoTypeEnum.getById(thirdTypeId).getCompany();
        String signKey = signKeyMap.get(company);
        treeMap.remove("sign");
        treeMap.put("key", signKey);
        String newSign = SmartGridUtils.genSign(treeMap);

        if (!Objects.equals(newSign, oldSign)) {
            log.error("[checkSign] 签名校验错误, request:{},oldSign:{},newSign:{}", treeMap, oldSign, newSign);
            throw new ApiException(TodoErrorCodes.SIGN_ERROR);
        }

        return ApiResult.of(0, todoDTO);
    }
}
