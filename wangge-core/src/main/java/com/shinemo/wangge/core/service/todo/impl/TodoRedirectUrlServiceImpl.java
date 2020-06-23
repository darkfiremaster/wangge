package com.shinemo.wangge.core.service.todo.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.shinemo.client.common.Result;
import com.shinemo.client.token.Token;
import com.shinemo.client.util.WebUtil;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.Utils;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.constants.SmartGridConstant;
import com.shinemo.smartgrid.domain.GridInfoToken;
import com.shinemo.smartgrid.domain.SmartGridContext;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author shangkaihui
 * @Date 2020/6/22 11:22
 * @Desc
 */
@Service
@Slf4j
public class TodoRedirectUrlServiceImpl implements TodoRedirectUrlService {

    @Autowired
    private DaosanjiaoPropertity daosanjiaoPropertity;

    @Autowired
    private YujingPropertity yujingPropertity;

    @Autowired
    private SmartGridUrlPropertity smartGridUrlPropertity;

    @Autowired
    private AuthService authService;

    @NacosValue(value = "${domain}", autoRefreshed = true)
    private String domain = "127.0.0.1";

    public static final int EXPIRE_TIME = 7 * 60 * 60 * 24;

    private Map<Integer, String> seedMap = new ConcurrentHashMap<>();


    @PostConstruct
    public void init() {
        seedMap.put(ThirdTodoTypeEnum.DAO_SAN_JIAO_ORDER.getId(), daosanjiaoPropertity.getSeed());
        seedMap.put(ThirdTodoTypeEnum.YU_JING_ORDER.getId(), yujingPropertity.getSeed());
    }


    @Override
    public ApiResult<String> getRedirectUrl(TodoUrlQuery todoUrlQuery) {
        Assert.notNull(todoUrlQuery, "request is null");
        Assert.notNull(todoUrlQuery.getThirdType(), "thirdType is null");
        Assert.notNull(todoUrlQuery.getOperatorMobile(), "mobile is null");
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
        String sign = Md5Util.getMD5Str(encryptData + "," + seed + "," + todoRedirectDTO.getTimestamp());
        if (!sign.equals(todoRedirectDTO.getSign())) {
            log.error("[redirectPage]签名校验失败,请求签名:{},计算后的签名:{}", todoRedirectDTO.getSign(), sign);
            throw new RuntimeException("签名不正确");
        }

        String decryptData = EncryptUtil.decrypt(todoRedirectDTO.getParamData(), seed);
        TodoRedirectDetailDTO todoRedirectDetailDTO = GsonUtils.fromGson2Obj(decryptData, TodoRedirectDetailDTO.class);
        log.info("[redirectPage]解密后的参数:{},{}", decryptData, todoRedirectDetailDTO);
        Integer redirectPage = todoRedirectDetailDTO.getRedirectPage();

        if (redirectPage.equals(1)) {
            //跳转摆摊页面 工单id不能为空
            Assert.notNull(todoRedirectDetailDTO.getThirdId(), "thirdId is null");
        }

        //校验token
        String token = todoRedirectDetailDTO.getToken();
        Result<Token> tokenResult = authService.validateToken(token);
        if (!tokenResult.isSuccess()) {
            log.error("[redirectPage] token校验失败,token:{}", token);
            throw new RuntimeException(tokenResult.getError().getMsg());
        }
        //生成短token
        Token longToken = tokenResult.getValue();
        long uid = longToken.getUid();
        long orgId = longToken.getOrgId();
        String orgName = longToken.getOrgName();
        String phone = longToken.getPhone();
        String userName = longToken.getUserName();
        long timestamp = System.currentTimeMillis();
        String shortToken = authService.generateShortToken(uid, timestamp);
        //生成userInfo
        HashMap<String, Object> userInfoMap = new HashMap<>();
        userInfoMap.put("orgId", orgId);
        userInfoMap.put("mobile", phone);
        userInfoMap.put("orgName", orgName);
        userInfoMap.put("username", userName);
        userInfoMap.put("name", userName);
        String userInfo = Utils.encodeUrl(GsonUtils.toJson(userInfoMap));
        //写cookie
        WebUtil.addCookie(request, response, "token", shortToken,
                domain, "/", Integer.MAX_VALUE, false);

        WebUtil.addCookie(request, response, "timeStamp", String.valueOf(timestamp),
                domain, "/", Integer.MAX_VALUE, false);

        WebUtil.addCookie(request, response, "uid", String.valueOf(uid),
                domain, "/", Integer.MAX_VALUE, false);

        WebUtil.addCookie(request, response, "orgId", String.valueOf(orgId),
                domain, "/", Integer.MAX_VALUE, false);

        WebUtil.addCookie(request, response, "userInfo", userInfo,
                domain, "/", Integer.MAX_VALUE, false);

        //获取token中的网格信息
        TreeMap<String, Object> map = longToken.getMap();
        String selectGridInfo = (String)map.get("selectGridInfo");
        String gridInfo = (String)map.get("gridInfo");
        WebUtil.addCookie(request, response, SmartGridConstant.ALL_GRID_INFO_COOKIE, gridInfo,
                domain, "/", EXPIRE_TIME, false);

        WebUtil.addCookie(request, response, SmartGridConstant.SELECT_GRID_INFO_COOKIE, selectGridInfo,
                domain, "/", EXPIRE_TIME, false);

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

        return ApiResult.of(0);
    }

    //获取预警工单详情页url
    private ApiResult<String> getYujingTodoDetailUrl(TodoUrlQuery todoUrlQuery) {
        String seed = yujingPropertity.getSeed();
        String domain = yujingPropertity.getDomain();
        String path = yujingPropertity.getTodoDetailUrl();

        long timestamp = System.currentTimeMillis();
        Map<String, Object> formData = new HashMap<>();
        formData.put("mobile", todoUrlQuery.getOperatorMobile());
        formData.put("timestamp", timestamp);
        //orderId为空,是跳列表页,不为空,是跳详情页
        if (StrUtil.isNotBlank(todoUrlQuery.getThirdId())) {
            formData.put("orderId", todoUrlQuery.getThirdId());
        }
        String token = getToken();
        formData.put("token", token);
        log.info("[getYujingTodoDetailUrl] formData:{}", formData);

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
        formData.put("mobile", todoUrlQuery.getOperatorMobile());
        formData.put("timestamp", timestamp);
        //如果id为空,则跳列表页,不为空则跳详情页
        if (StrUtil.isNotBlank(todoUrlQuery.getThirdId())) {
            formData.put("id", todoUrlQuery.getThirdId());
        } else {
            //获取当前登录人选择的网格和角色id
            String selectGridInfo = SmartGridContext.getSelectGridInfo();
            GridInfoToken gridInfoToken = GsonUtils.fromGson2Obj(selectGridInfo, GridInfoToken.class);
            GridUserRoleDetail gridDetail = gridInfoToken.getGridDetail();
            String gridId = gridDetail.getId();
            String roleId = gridDetail.getRoleList().get(0).getId();
            if (gridId.equals("0")) {
                formData.put("gridid", "9990");
                formData.put("roleid", "9990");
            } else {
                formData.put("gridid", gridId);
                formData.put("roleid", roleId);
            }
        }
        String token = getToken();
        formData.put("token", token);
        log.info("[getDaosanjiaoTodoDetailUrl] formData:{}", formData);

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

    private String getToken() {
        String uid = SmartGridContext.getUid();
        String orgId = SmartGridContext.getOrgId();
        TreeMap<String, Object> map = new TreeMap<>();
        String selectGridInfo = SmartGridContext.getSelectGridInfo();
        String gridInfo = SmartGridContext.getGridInfo();
        map.put("selectGridInfo", selectGridInfo);
        map.put("gridInfo", gridInfo);
        log.info("[getToken] 生成token的参数, uid:{},orgId:{},map:{}", uid, orgId, map);
        return authService.generateToken(Long.valueOf(uid), Long.valueOf(orgId), map);
    }
}
