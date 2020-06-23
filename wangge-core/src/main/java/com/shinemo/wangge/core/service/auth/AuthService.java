package com.shinemo.wangge.core.service.auth;

import com.shinemo.client.common.Result;
import com.shinemo.client.order.AppTypeEnum;
import com.shinemo.client.token.Token;

import java.util.TreeMap;

/**
 *token校验
 */
public interface AuthService {

    /**
     * 验证/解析token
     * @param tokenStr
     * @return
     */
    Result<Token> validateToken(String tokenStr);

    /**
     * 生成token
     * uid,appType,orgId
     * @param uid
     * @param orgId
     * @param map 自定义属性
     * @return
     */
    String generateToken(Long uid , Long orgId , TreeMap<String,Object> map);

    /**
     * 生成shortToken
     * @param userId
     * @param timestamp
     * @return
     */
    String generateShortToken(long userId,long timestamp);
}
