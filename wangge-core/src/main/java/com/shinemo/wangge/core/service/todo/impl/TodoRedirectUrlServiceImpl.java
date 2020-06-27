package com.shinemo.wangge.core.service.todo.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;
import com.shinemo.client.util.WebUtil;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.Utils;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.my.redis.service.RedisService;
import com.shinemo.smartgrid.constants.SmartGridConstant;
import com.shinemo.smartgrid.domain.GridInfoToken;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.domain.UserInfoCache;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;
import com.shinemo.stallup.domain.utils.EncryptUtil;
import com.shinemo.stallup.domain.utils.Md5Util;
import com.shinemo.todo.dto.TodoRedirectDTO;
import com.shinemo.todo.dto.TodoRedirectDetailDTO;
import com.shinemo.todo.enums.ThirdTodoTypeEnum;
import com.shinemo.todo.error.TodoErrorCodes;
import com.shinemo.todo.query.TodoUrlQuery;
import com.shinemo.wangge.core.config.properties.DaosanjiaoPropertity;
import com.shinemo.wangge.core.config.properties.SmartGridUrlPropertity;
import com.shinemo.wangge.core.config.properties.YujingPropertity;
import com.shinemo.wangge.core.service.auth.AuthService;
import com.shinemo.wangge.core.service.todo.TodoRedirectUrlService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author shangkaihui
 * @Date 2020/6/22 11:22
 * @Desc
 */
@Service
@Slf4j
public class TodoRedirectUrlServiceImpl implements TodoRedirectUrlService {

    @Resource
    private DaosanjiaoPropertity daosanjiaoPropertity;

    @Resource
    private YujingPropertity yujingPropertity;

    @Resource
    private SmartGridUrlPropertity smartGridUrlPropertity;

    @Resource
    private AuthService authService;

    @Resource
    private RedisService redisService;

    //@NacosValue(value = "${domain}", autoRefreshed = true)
    //private String domain = "127.0.0.1";

    public static final int EXPIRE_TIME = 7 * 60 * 60 * 24;

    private Map<Integer, String> seedMap = new ConcurrentHashMap<>();

    private static final String USER_INFO_KEY = "sm_smartgrid_user_info_%s";
    private static final Integer EXPIRE_TIME_ONE_DAY = 24 * 60 * 60;

    @PostConstruct
    public void init() {
        seedMap.put(ThirdTodoTypeEnum.DAO_SAN_JIAO_ORDER.getId(), daosanjiaoPropertity.getSeed());
        seedMap.put(ThirdTodoTypeEnum.YU_JING_ORDER.getId(), yujingPropertity.getSeed());
    }


    @Override
    public ApiResult<String> getRedirectUrl(TodoUrlQuery todoUrlQuery) {
        Assert.notNull(todoUrlQuery, "request is null");
        Assert.notNull(todoUrlQuery.getThirdType(), "thirdType is null");
        if (todoUrlQuery.getThirdType().equals(ThirdTodoTypeEnum.DAO_SAN_JIAO_ORDER.getId())) {
            return getDaosanjiaoTodoDetailUrl(todoUrlQuery);
        } else if (todoUrlQuery.getThirdType().equals(ThirdTodoTypeEnum.YU_JING_ORDER.getId())) {
            return getYujingTodoDetailUrl(todoUrlQuery);
        } else {
            return ApiResultWrapper.fail(TodoErrorCodes.TODO_TYPE_ERROR);
        }
    }

    @Override
    public ApiResult<Void> redirectPage(TodoRedirectDTO todoRedirectDTO, HttpServletRequest request, HttpServletResponse response) {
        Assert.notNull(todoRedirectDTO, "request is null");
        String encryptData = todoRedirectDTO.getParamData();
        Assert.notNull(encryptData, "paramData is null");
        Assert.notNull(todoRedirectDTO.getSign(), "sign is null");
        Assert.notNull(todoRedirectDTO.getTimestamp(), "timestamp is null");
        Assert.notNull(todoRedirectDTO.getThirdType(), "thirdType is null");

        String seed = seedMap.get(todoRedirectDTO.getThirdType());
        if (StrUtil.isBlank(seed)) {
            throw new ApiException(TodoErrorCodes.TODO_TYPE_ERROR);

        }
        String sign = Md5Util.getMD5Str(encryptData + "," + seed + "," + todoRedirectDTO.getTimestamp());
        if (!sign.equalsIgnoreCase(todoRedirectDTO.getSign())) {
            log.error("[redirectPage]签名校验失败,请求签名:{},计算后的签名:{}", todoRedirectDTO.getSign(), sign);
            throw new ApiException(TodoErrorCodes.SIGN_ERROR);
        }

        String decryptData = EncryptUtil.decrypt(todoRedirectDTO.getParamData(), seed);
        log.info("[redirectPage]解密后的参数decryptData:{}", decryptData);
        Map<String, String> map = HttpUtil.decodeParamMap(decryptData, CharsetUtil.charset("UTF-8"));
        TodoRedirectDetailDTO todoRedirectDetailDTO = BeanUtil.mapToBean(map, TodoRedirectDetailDTO.class, false);
        log.info("[redirectPage]map转化为bean后的的参数todoRedirectDetailDTO:{}", todoRedirectDetailDTO);

        Integer redirectPage = todoRedirectDetailDTO.getRedirectpage();

        if (redirectPage.equals(1)) {
            //跳转摆摊页面 工单id不能为空
            Assert.notNull(todoRedirectDetailDTO.getThirdid(), "thirdId is null");
        }

        UserInfoCache userInfoCache = redisService.get(USER_INFO_KEY + todoRedirectDetailDTO.getMobile(), UserInfoCache.class);
        log.info("[redirectPage]从缓存中获取到的用户信息:{}", userInfoCache);
        if (userInfoCache != null) {
            //设置用户信息cookie
            setUserInfoCookie(request, response, userInfoCache);

            //设置网格信息cookie
            setUserGridInfoCookie(request, response, userInfoCache);
        }

        //页面跳转
        try {
            if (redirectPage == null || redirectPage.equals(0)) {
                log.info("[redirectPage] 跳转首页:{}", smartGridUrlPropertity.getIndexUrl());
                response.sendRedirect(smartGridUrlPropertity.getIndexUrl());
            } else if (redirectPage.equals(1)) {
                log.info("[redirectPage] 跳新建摆摊页面:{}", smartGridUrlPropertity.getCreateStallupUrl());
                response.sendRedirect(smartGridUrlPropertity.getCreateStallupUrl());
            } else {
                log.info("[redirectPage] 跳转首页:{}", smartGridUrlPropertity.getIndexUrl());
                response.sendRedirect(smartGridUrlPropertity.getIndexUrl());
            }
        } catch (Exception e) {
            log.error("[redirectPage] 页面跳转异常,request:{},异常原因:{}", todoRedirectDTO, e.getMessage(), e);
        }
        log.info("[redirectPage] 跳转成功");
        return ApiResult.of(0);
    }

    private void setUserInfoCookie(HttpServletRequest request, HttpServletResponse response, UserInfoCache userInfoCache) {
        log.info("[redirectPage] 用户信息不为空, 用户信息:{}", userInfoCache);
        String uid = userInfoCache.getUid();
        String orgId = userInfoCache.getOrgId();
        String mobile = userInfoCache.getMobile();
        String orgName = userInfoCache.getOrgName();
        String userName = userInfoCache.getUserName();
        long timestamp = System.currentTimeMillis();

        //生成短token
        String shortToken = authService.generateShortToken(Long.parseLong(uid), timestamp);

        //生成userInfo
        HashMap<String, Object> userInfoMap = new HashMap<>();
        userInfoMap.put("orgId", orgId);
        userInfoMap.put("mobile", mobile);
        userInfoMap.put("orgName", orgName);
        userInfoMap.put("username", userName);
        userInfoMap.put("name", userName);
        String userInfo = Utils.encodeUrl(GsonUtils.toJson(userInfoMap));

        //设置用户信息cookie
        WebUtil.addCookie(request, response, "token", shortToken,
                null, "/", Integer.MAX_VALUE, false);

        WebUtil.addCookie(request, response, "timeStamp", String.valueOf(timestamp),
                null, "/", Integer.MAX_VALUE, false);

        WebUtil.addCookie(request, response, "uid", String.valueOf(uid),
                null, "/", Integer.MAX_VALUE, false);

        WebUtil.addCookie(request, response, "orgId", String.valueOf(orgId),
                null, "/", Integer.MAX_VALUE, false);

        WebUtil.addCookie(request, response, "userInfo", userInfo,
                null, "/", Integer.MAX_VALUE, false);
    }

    private void setUserGridInfoCookie(HttpServletRequest request, HttpServletResponse response, UserInfoCache userInfoCache) {
        String selectGridInfo = userInfoCache.getSelectGridInfo();
        String gridInfo = userInfoCache.getGridInfo();

        GridInfoToken selectGridInfoToken = new GridInfoToken();
        selectGridInfoToken.setGridDetail(GsonUtils.fromGson2Obj(selectGridInfo, GridUserRoleDetail.class));
        selectGridInfo = Base64.encodeBase64URLSafeString(GsonUtils.toJson(selectGridInfoToken).getBytes(StandardCharsets.UTF_8));

        GridInfoToken gridInfoToken = new GridInfoToken();
        gridInfoToken.setGridList(GsonUtils.fromJsonToList(gridInfo, GridUserRoleDetail[].class));
        gridInfo = Base64.encodeBase64URLSafeString(GsonUtils.toJson(gridInfoToken).getBytes(StandardCharsets.UTF_8));

        WebUtil.addCookie(request, response, SmartGridConstant.ALL_GRID_INFO_COOKIE, gridInfo,
                null, "/", EXPIRE_TIME, false);

        WebUtil.addCookie(request, response, SmartGridConstant.SELECT_GRID_INFO_COOKIE, selectGridInfo,
                null, "/", EXPIRE_TIME, false);
    }

    //获取预警工单详情页url
    private ApiResult<String> getYujingTodoDetailUrl(TodoUrlQuery todoUrlQuery) {
        String seed = yujingPropertity.getSeed();
        String domain = yujingPropertity.getDomain();
        String path = yujingPropertity.getTodoDetailUrl();

        long timestamp = System.currentTimeMillis();
        Map<String, Object> formData = new HashMap<>();
        if (!StringUtils.isEmpty(todoUrlQuery.getOperatorMobile())) {
            formData.put("mobile", todoUrlQuery.getOperatorMobile());
        } else {
            formData.put("mobile", SmartGridContext.getMobile());
        }
        formData.put("timestamp", timestamp);
        //orderId为空,是跳列表页,不为空,是跳详情页
        if (StrUtil.isNotBlank(todoUrlQuery.getThirdId())) {
            formData.put("thirdid", todoUrlQuery.getThirdId());
        }

        String token = getToken();
        formData.put("token", token);
        log.info("[getYujingTodoDetailUrl] formData:{}", formData);

        //设置用户参数到redis
        saveUserInfo();

        String paramStr = EncryptUtil.buildParameterString(formData);
        //1、加密
        String encryptData = EncryptUtil.encrypt(paramStr, seed);
        //2、生成签名
        String sign = Md5Util.getMD5Str(encryptData + "," + seed + "," + timestamp);
        String url = domain + path + "?";
        StringBuilder sb = new StringBuilder(url);
        url = sb.append("paramData=").append(encryptData)
                .append("&timestamp=").append(timestamp)
                .append("&sign=").append(sign).toString();

        log.info("[getYujingTodoDetailUrl] 生成跳转url:{}", url);
        return ApiResult.of(0, url);
    }

    //获取倒三角工单详情页url
    private ApiResult<String> getDaosanjiaoTodoDetailUrl(TodoUrlQuery todoUrlQuery) {
        String seed = daosanjiaoPropertity.getSeed();
        String domain = daosanjiaoPropertity.getDomain();
        String path = daosanjiaoPropertity.getTodoDetailUrl();

        long timestamp = System.currentTimeMillis();
        Map<String, Object> formData = new HashMap<>();
        if (!StringUtils.isEmpty(todoUrlQuery.getOperatorMobile())) {
            formData.put("mobile", todoUrlQuery.getOperatorMobile());
        } else {
            formData.put("mobile", SmartGridContext.getMobile());
        }
        formData.put("timestamp", timestamp);
        //如果id为空,则跳列表页,不为空则跳详情页
        if (StrUtil.isNotBlank(todoUrlQuery.getThirdId())) {
            //跳详情页
            formData.put("id", todoUrlQuery.getThirdId());
        } else {
            //跳列表页
            //获取当前登录人选择的网格和角色id
            String selectGridInfo = SmartGridContext.getSelectGridInfo();
            GridUserRoleDetail gridUserRoleDetail = GsonUtils.fromGson2Obj(selectGridInfo, GridUserRoleDetail.class);
            String gridId = gridUserRoleDetail.getId();
            if (gridId.equals("0")) {
                //与倒三角约定:如果网格或角色不存在,则传特殊值9990
                formData.put("gridid", "9990");
                formData.put("roleid", "9990");
            } else {
                String roleId = gridUserRoleDetail.getRoleList().get(0).getId();
                formData.put("gridid", gridId);
                formData.put("roleid", roleId);
            }
        }

        String token = getToken();
        formData.put("token", token);
        log.info("[getDaosanjiaoTodoDetailUrl] formData:{}", formData);

        //设置用户参数到redis
        saveUserInfo();

        String paramStr = EncryptUtil.buildParameterString(formData);
        //1、加密
        String encryptData = EncryptUtil.encrypt(paramStr, seed);
        //2、生成签名
        String sign = Md5Util.getMD5Str(encryptData + "," + seed + "," + timestamp);
        String url = domain + path + "?";
        StringBuilder sb = new StringBuilder(url);
        url = sb.append("paramData=").append(encryptData)
                .append("&timestamp=").append(timestamp)
                .append("&sign=").append(sign).toString();
        log.info("[getDaosanjiaoTodoDetailUrl] 生成跳转url:{}", url);
        return ApiResult.of(0, url);
    }

    private void saveUserInfo() {
        String uid = SmartGridContext.getUid();
        String orgId = SmartGridContext.getOrgId();
        String orgName = SmartGridContext.getOrgName();
        String mobile = SmartGridContext.getMobile();
        String userName = SmartGridContext.getUserName();
        String selectGridInfo = SmartGridContext.getSelectGridInfo();
        String gridInfo = SmartGridContext.getGridInfo();
        UserInfoCache userInfoCache = new UserInfoCache();
        userInfoCache.setUid(uid);
        userInfoCache.setUserName(userName);
        userInfoCache.setOrgId(orgId);
        userInfoCache.setOrgName(orgName);
        userInfoCache.setMobile(mobile);
        userInfoCache.setSelectGridInfo(selectGridInfo);
        userInfoCache.setGridInfo(gridInfo);
        log.info("[saveUserInfo] 缓存用户信息:{}", userInfoCache);
        redisService.set(USER_INFO_KEY + mobile, GsonUtils.toJson(userInfoCache), EXPIRE_TIME_ONE_DAY);
    }

    private String getToken() {

        return IdUtil.simpleUUID();

        //String uid = SmartGridContext.getUid();
        //String orgId = SmartGridContext.getOrgId();
        //TreeMap<String, Object> map = new TreeMap<>();
        //String selectGridInfo = SmartGridContext.getSelectGridInfo();
        //String gridInfo = SmartGridContext.getGridInfo();
        //map.put("selectGridInfo", selectGridInfo);
        //map.put("gridInfo", gridInfo);
        //log.info("[getToken] 生成token的参数, uid:{},orgId:{},map:{}", uid, orgId, map);
        //return authService.generateToken(Long.valueOf(uid), Long.valueOf(orgId), map);
    }

    public static void main(String[] args) {
        String data = "yLTEzpz49P5ohfRyZ_MFGUlSoqmI_4zFa-TTL-QzJza7Oexg3tJh7tUrs1bYIw5YmpJNm3e8W4SKJqm6bdR5s5pGHpFA7T8oxfUuXBtuv8Dr1xZbdWIO9agfvYLan25c";
        String sign = Md5Util.getMD5Str(data + "," + "ffd40e661eb946f48fd3c759e6b8ef0b" + "," + 1592979156924L);
        System.out.println("sign = " + sign);
        String s = DigestUtil.md5Hex(data + "," + "ffd40e661eb946f48fd3c759e6b8ef0b" + "," + 1592979156924L);
        System.out.println("s = " + s);
    }
}
