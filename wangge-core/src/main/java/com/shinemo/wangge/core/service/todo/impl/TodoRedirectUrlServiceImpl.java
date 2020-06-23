package com.shinemo.wangge.core.service.todo.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.utils.EncryptUtil;
import com.shinemo.stallup.domain.utils.Md5Util;
import com.shinemo.todo.dto.TodoRedirectDTO;
import com.shinemo.todo.dto.TodoRedirectDetailDTO;
import com.shinemo.todo.enums.ThirdTodoTypeEnum;
import com.shinemo.todo.error.TodoErrorCodes;
import com.shinemo.todo.query.TodoUrlQuery;
import com.shinemo.wangge.core.config.properties.DaosanjiaoPropertity;
import com.shinemo.wangge.core.config.properties.YujingPropertity;
import com.shinemo.wangge.core.service.todo.TodoRedirectUrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
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

    @Autowired
    private DaosanjiaoPropertity daosanjiaoPropertity;

    @Autowired
    private YujingPropertity yujingPropertity;

    private Map<Integer, String> seedMap = new ConcurrentHashMap<>();

    @NacosValue(value = "${index.url}")
    private String indexUrl;

    @NacosValue(value = "${createStallup.url}")
    private String createStallupUrl;


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
    public ApiResult<Void> redirectPage(TodoRedirectDTO todoRedirectDTO, HttpServletResponse response) {
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

        //todo 校验token
        String token = todoRedirectDetailDTO.getToken();
        //todo 写cookie

        //页面跳转
        try {
            if (redirectPage == null || redirectPage.equals(0)) {
                log.info("[redirectPage] 跳转首页:{}", indexUrl);
                response.sendRedirect(indexUrl);
            } else if (redirectPage.equals(1)) {
                log.info("[redirectPage] 跳新建摆摊页面:{}", createStallupUrl);
                response.sendRedirect(createStallupUrl);
            } else {
                log.info("[redirectPage] 跳转首页:{}", indexUrl);
                response.sendRedirect(indexUrl);
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
        //todo 加上token

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
            //todo 从cookie中获取当前登录人选择的网格和角色id
            formData.put("gridid", "");
            formData.put("roleid", "");
        }
        //todo 加上token

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
}
