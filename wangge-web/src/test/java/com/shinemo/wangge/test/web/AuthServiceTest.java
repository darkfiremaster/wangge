package com.shinemo.wangge.test.web;

import com.shinemo.client.common.Result;
import com.shinemo.client.token.Token;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.operate.excel.LoginInfoExcelDTO;
import com.shinemo.wangge.core.service.auth.AuthService;
import com.shinemo.wangge.core.service.operate.LoginStatisticsService;
import com.shinemo.wangge.web.MainApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.TreeMap;

/**
 * 类说明:
 *
 * @author zengpeng
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainApplication.class)
public class AuthServiceTest {
    @Resource
    private AuthService authService;

    @Test
    public void testGenToken() {
        TreeMap<String,Object> map = new TreeMap<>();
        map.put("roleId",1L);
        map.put("gridId",1L);

        String s = authService.generateToken(101010013817072L, 1111L, map);

        System.out.println("authService.genToken = " + s);
    }


    @Test
    public void testValidateToken(){
        String token = "eyJhcHBJZCI6MCwiZ3JpZElkIjoxLCJvcmdJZCI6MTExMSwicm9sZUlkIjoxLCJzY29wZUlkIjowLCJzaWduYXR1cmUiOiI4ZTUyZjU1NDNlODg0ZGM2OTI0YzQ5NDNhYjFjZGY3OCIsInNpdGVJZCI6MTgsInRpbWVzdGFtcCI6MTU5Mjg3ODMwMTY4OSwidWlkIjo2OTU1MzA0OH0";
        Result<Token> tokenResult = authService.validateToken(token);
        System.out.println("orgId:" + tokenResult.getValue().getOrgId());
        System.out.println("phone:" + tokenResult.getValue().getPhone());

    }
}
