package com.shinemo.wangge.test.web;

import com.shinemo.Aace.MutableBoolean;
import com.shinemo.Aace.context.AaceContext;
import com.shinemo.client.ace.Imlogin.IMLoginService;
import com.shinemo.client.common.Result;
import com.shinemo.client.order.AppTypeEnum;
import com.shinemo.client.token.Token;
import com.shinemo.common.tools.Utils;
import com.shinemo.smartgrid.domain.UserInfoCache;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.wangge.core.service.auth.AuthService;
import com.shinemo.wangge.web.MainApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.TreeMap;

import static com.shinemo.Aace.RetCode.RET_SUCCESS;

/**
 * 类说明:
 *
 * @author zengpeng
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainApplication.class)
@Slf4j
public class AuthServiceTest {
    @Resource
    private AuthService authService;

    @Test
    public void testGenToken() {
        TreeMap<String,Object> map = new TreeMap<>();
        map.put("roleId",1L);
        map.put("gridId",1L);

        String s = authService.generateToken(69553048L, 1111L, map);

        System.out.println("authService.genToken = " + s);
    }


    @Test
    public void testValidateToken(){
        String token = "eyJhcHBJZCI6MCwiZ3JpZElkIjoxLCJvcmdJZCI6MTExMSwicm9sZUlkIjoxLCJzY29wZUlkIjowLCJzaWduYXR1cmUiOiI4NGYyNmZlODMyMTI3Njk5YzY3ZjI4YjMxZjAyZGJkNSIsInNpdGVJZCI6MSwidGltZXN0YW1wIjoxNTkyODc5OTkzMzY4LCJ1aWQiOjY5NTUzMDQ4fQ";
        Result<Token> tokenResult = authService.validateToken(token);
        System.out.println("orgId:" + tokenResult.getValue().getOrgId());
        System.out.println("phone:" + tokenResult.getValue().getPhone());
        System.out.println("tokenResult = " + tokenResult);

    }

    @Resource
    private IMLoginService aaceIMLoginService;

    @Test
    public void test() {
        UserInfoCache userInfoCache = new UserInfoCache();
        userInfoCache.setUid(String.valueOf(115180015645232L));
        userInfoCache.setUserName("尚凯辉");
        userInfoCache.setOrgId(String.valueOf(168));
        userInfoCache.setOrgName("彩牛科技有限公司");
        userInfoCache.setMobile("13588039023");
        //userInfoCache.setSelectGridInfo();
        //userInfoCache.setGridInfo();


        log.info("[redirectPage] 用户信息不为空, 用户信息:{}", userInfoCache);
        String uid = userInfoCache.getUid();
        String orgId = userInfoCache.getOrgId();
        String mobile = userInfoCache.getMobile();
        String orgName = userInfoCache.getOrgName();
        String userName = userInfoCache.getUserName();
        long timestamp = System.currentTimeMillis();

        //生成短token
        String shortToken = authService.generateShortToken(Long.parseLong(uid), timestamp);
        System.out.println("shortToken = " + shortToken);
        //生成userInfo
        HashMap<String, Object> userInfoMap = new HashMap<>();
        userInfoMap.put("orgId", orgId);
        userInfoMap.put("mobile", mobile);
        userInfoMap.put("orgName", orgName);
        userInfoMap.put("username", userName);
        userInfoMap.put("name", userName);
        String userInfo = Utils.encodeUrl(GsonUtils.toJson(userInfoMap));
        System.out.println("userInfo = " + userInfo);

        int ret = RET_SUCCESS;
        MutableBoolean isSuccess = new MutableBoolean();
        AaceContext ctx = new AaceContext(AppTypeEnum.GUANGXI.getId() + "");
        ctx.set("uid", uid + "");
        try {
            ret = aaceIMLoginService.verifyToken(uid, shortToken, timestamp, isSuccess, ctx);
            System.out.println("ret = " + ret);
        } catch (Exception ex) {
            log.error("check token fail, ret={}, token={}", ret, shortToken, ex);
        }

    }
}
