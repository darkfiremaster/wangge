package com.shinemo.wangge.core.service.auth.impl;

import com.google.common.cache.CacheBuilder;
import com.google.gson.reflect.TypeToken;
import com.shinemo.Aace.MutableBoolean;
import com.shinemo.Aace.MutableString;
import com.shinemo.Aace.RetCode;
import com.shinemo.Aace.context.AaceContext;
import com.shinemo.auth.AuthErrors;
import com.shinemo.client.ace.Imlogin.IMLoginService;
import com.shinemo.client.ace.user.UserProfileServiceWrapper;
import com.shinemo.client.ace.user.domain.UserProfileInfo;
import com.shinemo.client.common.Result;
import com.shinemo.client.order.AppTypeEnum;
import com.shinemo.client.token.Token;
import com.shinemo.client.token.TokenUtil;
import com.shinemo.client.util.GsonUtil;
import com.shinemo.client.util.UrlUtils;
import com.shinemo.wangge.core.service.auth.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;


/**
 * 类说明:
 *
 * @author zengpeng
 */
@Slf4j
@Service("authService")
public class AuthServiceImpl implements AuthService {

    private final com.google.common.cache.Cache<String, String> cache = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(10, TimeUnit.MINUTES).initialCapacity(100).build();


    private static Type longTokenType = new TypeToken<Map<String, String>>() {}.getType();

    private static final String KEY_BIZ_TOKEN_FORMAT = "key_%s_%s_biz_token";
    private final Long scopeId = 0L;

    @Resource
    private IMLoginService aaceIMLoginService;

    @Resource
    private UserProfileServiceWrapper userProfileServiceWrapper;

    private final AppTypeEnum appType = AppTypeEnum.CAIYUN;

    @Override
    public Result<Token> validateToken(String tokenStr) {

        /*
         * 1.解析TOKEN 2.缓存获取TOKEN - 存在 - 通过校验 - 返回用户相关数据 - 不存在 - verify验证 - 验证成功 - 更新缓存 -
         * 验证失败 - 返回异常
         */
        tokenStr = UrlUtils.decodeValue(tokenStr);
        boolean isShortToken = isShortToken(tokenStr);
        Token token = null;

        //2.获取token对象
        if(isShortToken) {
            token = buildShortToken(tokenStr);
        }else {
            token = TokenUtil.buildLongToken(tokenStr);
        }

        if (null == token) {
            log.error("[validateToken] error,raw:{},appType:{}",tokenStr, appType.getName());
            return Result.error(AuthErrors.TOKEN_ERROR);
        }

        boolean verifyTokenRet = false;
        if(isShortToken) {
            //验证 uid,token,timestamp,appType
            verifyTokenRet = verifySelf(token.getUid(), token.getToken(), token.getTimestamp(), appType);
        }else {
            verifyTokenRet = verifyLongToken(token.getMap(), appType, token.getUid());
        }

        if(!verifyTokenRet) {
            log.error("[validateToken] verify error,raw:{},appType:{}",tokenStr,appType);
            return Result.error(AuthErrors.TOKEN_ERROR);
        }

        // 补全Token参数
        assmblyToken(token, appType);
        return Result.success(token);
    }

    @Override
    public String generateToken(Long uid , Long orgId , TreeMap<String,Object> map) {
        try {
            String bizToken = getUserBizToken(uid, appType);
            return TokenUtil.generateLongToken(uid, orgId, appType.getId(), scopeId,bizToken,map);
        } catch (Exception e) {
            log.error("[generateToken] appType={}, orgId={}, uid={}, exception: {}", appType, orgId, uid, e);
            return null;
        }
    }


    private boolean isShortToken(String raw) {
        boolean longToken = TokenUtil.isLongToken(raw);
        if(longToken) {
            //base64解码
            byte[] bytes = Base64.decodeBase64(raw);
            String temp = new String(bytes);
            Map<String, String> map = GsonUtil.fromGson2Obj(temp, longTokenType);
            //token中含有uid,timestamp,token的值
            if(map!=null&&map.containsKey("uid")&&map.containsKey("timestamp")&&map.containsKey("token")) {
                return true;
            }
        }else {
            log.error("[isShortToken] error, not long token:"+raw);
        }
        return false;
    }


    private Token buildShortToken(String raw) {
        byte[] bytes = Base64.decodeBase64(raw);
        String temp = new String(bytes);
        Map<String, String> map = GsonUtil.fromGson2Obj(temp, longTokenType);
        Token tokenObj = new Token();
        tokenObj.setUid(Long.parseLong(map.get("uid")));
        tokenObj.setTimestamp(Long.parseLong(map.get("timestamp")));
        tokenObj.setToken(map.get("token"));
        return tokenObj;
    }



    private Boolean verifySelf(long userId, String token, long timestamp, AppTypeEnum appType) {
        //根据key获得bizToken 本地缓存
        String bizTokenKey = String.format(KEY_BIZ_TOKEN_FORMAT, userId,appType.getId());
        //根据key获得bizToken
        String bizToken = cache.asMap().get(bizTokenKey);
        if(StringUtils.isNotBlank(bizToken)) {
            //生成token md5hex加密
            String realToken = genToken(bizToken, userId, timestamp);
            //判断生成的realToken是否和传参token相同
            if(token.equals(realToken)) {
                return true;
            }
        }

        //从缓存获取token失效，再通过aace获取token信息
        bizToken = getUserBizToken(userId, appType);
        if(StringUtils.isNotBlank(bizToken)) {
            //放入缓存
            cache.put(bizTokenKey, bizToken);
            //判断从acce获取的bizToken生成的token和用户传来的是否相同 md5hex加密
            String realToken = genToken(bizToken, userId, timestamp);
            if(token.equals(realToken)) {
                return true;
            }
        }
        return verify(userId+"",token,timestamp,appType) ;
    }

    private boolean verifyLongToken(TreeMap<String, Object> map, AppTypeEnum appType, long userId) {
        if(map==null||map.size()<=0) {
            log.error("[verifyLongToken] error,map is null,userId:{},appType:{}",userId,appType);
            return false;
        }
        String bizTokenKey = String.format(KEY_BIZ_TOKEN_FORMAT, userId,appType.getId());
        String bizToken = cache.asMap().get(bizTokenKey);
        boolean verifyRet = false;
        if(StringUtils.isNotBlank(bizToken)) {
            if(map.get("signature")!=null&&StringUtils.isNotBlank(String.valueOf(map.get("signature")))) {
                verifyRet = TokenUtil.verifyLongToken(new TreeMap<>(map), bizToken);
                if(verifyRet) {
                    return verifyRet;
                }
            }else {
                return true;
            }
        }
        bizToken = getUserBizToken(userId, appType);
        if(StringUtils.isNotBlank(bizToken)) {
            cache.put(bizTokenKey, bizToken);
            if(map.get("signature")!=null&&StringUtils.isNotBlank(String.valueOf(map.get("signature")))) {
                return TokenUtil.verifyLongToken(new TreeMap<>(map), bizToken);
            }else {
                return true;
            }
        }else {
            log.error("[verifyLongToken] error,bizToken is null,map:{},appType:{},userId:{}",GsonUtil.toJson(map),appType,userId);
            return false;
        }
    }


    private Boolean verify(String userId, String token, long timestamp, AppTypeEnum appType) {
        MutableBoolean result = new MutableBoolean();
        try {
            int error = aaceIMLoginService.verifyToken(userId, token, timestamp, result, new AaceContext(String.valueOf(appType.getId())));
            return !(error != 0 || !result.get());
        } catch (Exception e) {
            // 校验失败,异常
            log.info("[verify token]: userId={}, token={}, appType={}, exception={}", userId, token, appType.getName(), e);
            return false;
        }
    }

    /**
     * 补全token参数
     * @param token
     * @param appTypeEnum
     */
    private void assmblyToken(Token token, AppTypeEnum appTypeEnum) {
        token.setScopeId(scopeId);
        //用户简要信息:名称，手机号，年龄，别名
        UserProfileInfo userProfileInfo = userProfileServiceWrapper.getUserProfileInfo(String.valueOf(token.getUid()),new AaceContext(appType.getId()+""));
        if(userProfileInfo!=null) {
            token.setUserName(userProfileInfo.getName());
            token.setPhone(userProfileInfo.getMobile());
            token.setSite(appType.getId());
        }else {
            log.error("[assmblyToken] userProfileInfo is null"+token.toString());
        }

    }

    private String getUserBizToken(long userId,AppTypeEnum appType) {
        //MutableString:可变的字符串
        MutableString bizTokenRet = new MutableString();
        //构建上下文
        AaceContext ctx = new AaceContext(appType.getId()+"");
        //设置uid的值
        ctx.set("uid", userId+"");
        //将上下文作为参数通过aace获取token
        int ret = aaceIMLoginService.getBusinessToken(bizTokenRet, ctx);

        if(ret== RetCode.RET_SUCCESS) {
            //返回获得bizToken
            return bizTokenRet.get();
        }else {
            log.error("[getUserBizToken] getBusinessToken error,ret:{},userId:{},appType:{}",ret,userId,appType);
            return null;
        }
    }

    /**
     * 获得bizToken+userId+timestamp的md5串
     * @param bizToken
     * @param userId
     * @param timestamp
     * @return
     */
    private String genToken(String bizToken,long userId,long timestamp) {
        //md5加密
        return DigestUtils.md5Hex(bizToken+userId+timestamp);
    }
}
