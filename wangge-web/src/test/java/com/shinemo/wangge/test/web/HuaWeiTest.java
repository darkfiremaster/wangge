package com.shinemo.wangge.test.web;

import cn.hutool.json.JSONUtil;
import com.shinemo.smartgrid.http.HttpConnectionUtils;
import com.shinemo.smartgrid.http.HttpResult;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.smartgrid.utils.SmartGridUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author shangkaihui
 * @Date 2020/8/13 17:37
 * @Desc
 */
@Slf4j
public class HuaWeiTest {

    //@Value("${smartgrid.huawei.accessKeyId}")
    public String accessKeyId = "xmsystem";
    //@Value("${smartgrid.huawei.secretKey}")
    public String secretKey = "7CD36D02A911BFDF5AFA1C91C0A319B8";
    //@Value("${smartgrid.huawei.domain}")
    public String domain = "http://112.54.48.61:13003";

    @Test
    public void testGetGroupList() {
        String mobile = "15978197192";
        String huaweiUrl = "/SGCoreMarketing/groupService/getGroupList";

        Map<String, Object> header = SmartGridUtils.buildHeader(mobile, accessKeyId, secretKey);
        log.info("header:{}", header);

        HashMap<String, Object> requestData = new HashMap<>();
        requestData.put("groupName", "银行");
        String param = GsonUtils.toJson(requestData);
        log.info("param:{}", param);

        HashMap<String,Object> requestMap = new HashMap<>();
        requestMap.put("header", header);
        requestMap.put("body",requestData);
        System.out.println("requestMap = " + requestMap);
        log.info("httpRequest:{}", JSONUtil.toJsonStr(requestMap));

        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + huaweiUrl, param, header);
        log.info("httpResult:{}", httpResult);
    }

    @Test
    public void testGetGridUserInfo() {
        String mobile = "15978197192";
        String huaweiUrl = "/CMCC_GX_market/CMCC_GX_SmartGridAuth/auth/getUserInfo.do";

        Map<String, Object> header = SmartGridUtils.buildHeader(mobile, accessKeyId, secretKey);
        log.info("header:{}", header);

        HashMap<String, Object> requestData = new HashMap<>();
        String param = GsonUtils.toJson(requestData);
        log.info("param:{}", param);

        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + huaweiUrl, param, header);
        log.info("httpResult:{}", httpResult);
    }

    @Test
    public void test2() {
        String mobile = "17377273810";
        String huaweiUrl = "/SGCoreMarketing/groupService/getGroupPlanParticipant";

        Map<String, Object> header = SmartGridUtils.buildHeader(mobile, accessKeyId, secretKey);
        log.info("header:{}", header);

        HashMap<String, Object> requestData = new HashMap<>();
        requestData.put("areaId","771");
        requestData.put("areaLevel","2");
        requestData.put("pageNum","1");
        requestData.put("pageSize","20");
        String param = GsonUtils.toJson(requestData);
        log.info("param:{}", param);

        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + huaweiUrl, param, header);
        log.info("httpResult:{}", httpResult);
    }

    @Test
    public void test3() {
        String mobile = "17377273810";
        String huaweiUrl = "/SGCoreCommon/common/config/getAreaInformation.do";

        Map<String, Object> header = SmartGridUtils.buildHeader(mobile, accessKeyId, secretKey);
        log.info("header:{}", header);

        HashMap<String, Object> requestData = new HashMap<>();
        requestData.put("startLevel","2");
        requestData.put("endLevel","2");
        String param = GsonUtils.toJson(requestData);
        log.info("param:{}", param);

        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + huaweiUrl, param, header);
        log.info("httpResult:{}", httpResult);
    }
}
