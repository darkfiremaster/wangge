package com.shinemo.wangge.test.web;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.groupserviceday.domain.enums.HuaweiGroupServiceDayUrlEnum;
import com.shinemo.groupserviceday.enums.GroupServiceDayStatusEnum;
import com.shinemo.groupserviceday.error.GroupServiceDayErrorCodes;
import com.shinemo.smartgrid.http.HttpConnectionUtils;
import com.shinemo.smartgrid.http.HttpResult;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.smartgrid.utils.SmartGridUtils;
import com.shinemo.stallup.domain.utils.DistanceUtils;
import com.shinemo.sweepstreet.domain.contants.SweepStreetActivityConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import java.util.*;

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



    private void printResult(Map<String, Object> header, HashMap<String, Object> requestData, HttpResult httpResult) {
        HashMap<String,Object> requestMap = new LinkedHashMap<>();
        requestMap.put("header", header);
        requestMap.put("body",requestData);
        requestMap.put("result", JSONUtil.parseObj(httpResult.getContent()));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        log.info("result:{}", gson.toJson(requestMap));
    }

    @Test
    public void testGetGroupList() {
        String mobile = "13607713224";
        String huaweiUrl = "/SGCoreMarketing/groupService/getGroupList";

        Map<String, Object> header = SmartGridUtils.buildHeader(mobile, accessKeyId, secretKey);
        log.info("header:{}", header);

        HashMap<String, Object> requestData = new HashMap<>();
        //requestData.put("groupName", "银行");
        //requestData.put("mobile", "13607713224");
        String param = GsonUtils.toJson(requestData);
        log.info("param:{}", param);

        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + huaweiUrl, param, header);
        log.info("httpResult:{}", httpResult);

        printResult(header, requestData, httpResult);
    }

    @Test
    public void testCreateGroupServiceDay() {
        String mobile = "13607713224";
        String huaweiUrl = HuaweiGroupServiceDayUrlEnum.CREATE_GROUP_SERVICE_DAY.getUrl();

        Map<String, Object> header = SmartGridUtils.buildHeader(mobile, accessKeyId, secretKey);
        log.info("header:{}", header);

        //HashMap<String, Object> requestData = new HashMap<>();
        //requestData.put("groupName", "银行");
        //String param = GsonUtils.toJson(requestData);

        String param = "{\n" +
                "    \"parentActivityId\":\"GSD_ACTIVITY_20\",\n" +
                "    \"title\":\"测试活动\",\n" +
                "    \"startTime\":\"1970-01-19 19:43:07\",\n" +
                "    \"endTime\":\"1970-01-19 19:43:10\",\n" +
                "    \"status\":\"0\",\n" +
                "    \"groupId\":\"G7717362086\",\n" +
                "    \"childrenList\":[\n" +
                "        {\n" +
                "            \"activityId\":\"GSD_ACTIVITY_29\",\n" +
                "            \"participantList\":[\n" +
                "                {\n" +
                "                    \"userSource\":\"2\",\n" +
                "                    \"userName\":\"吴健\",\n" +
                "                    \"userPhone\":\"13607713224\",\n" +
                "                    \"userType\":\"1\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        log.info("param:{}", param);

        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + huaweiUrl, param, header);
        log.info("httpResult:{}", httpResult);

        //printResult(header, requestData, httpResult);
    }

    @Test
    public void testUpdateGroupServiceDay() {
        String mobile = "13607713224";
        String huaweiUrl = HuaweiGroupServiceDayUrlEnum.UPDATE_GROUP_SERVICE_DAY.getUrl();

        Map<String, Object> header = SmartGridUtils.buildHeader(mobile, accessKeyId, secretKey);
        log.info("header:{}", header);

        Map<String, Object> map = new HashMap<>();
        map.put("activityId", "GSD_ACTIVITY_29");
        map.put("parentActivityId","GSD_ACTIVITY_20");
        map.put("status", GroupServiceDayStatusEnum.AUTO_END.getId());
        //String location = "120.0687009006076,30.28182427300347";
        //String[] locations = StrUtil.split(location, ",");
        //map.put("startLongitude", locations[0]);
        //map.put("startLatitude", locations[1]);
        //map.put("startAddress", "浙江省杭州市西湖区文二西路靠近西溪壹号");
        //map.put("startTime", DateUtil.formatDateTime(new Date()));
        String param = GsonUtils.toJson(map);
        log.info("param:{}", param);

        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + huaweiUrl, param, header);
        log.info("httpResult:{}", httpResult);

        //printResult(header, requestData, httpResult);
    }

    @Test
    public void testGetGridUserInfo() {
        String mobile = "13607713224";
        String huaweiUrl = "/CMCC_GX_market/CMCC_GX_SmartGridAuth/auth/getUserInfo.do";

        Map<String, Object> header = SmartGridUtils.buildHeader(mobile, accessKeyId, secretKey);
        log.info("header:{}", header);

        HashMap<String, Object> requestData = new HashMap<>();
        String param = GsonUtils.toJson(requestData);
        log.info("param:{}", param);

        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + huaweiUrl, param, header);
        log.info("httpResult:{}", httpResult);

        printResult(header, requestData, httpResult);

    }

    @Test
    public void test2() {
        String mobile = "15978197192";
        String huaweiUrl = "/SGCoreMarketing/groupService/getGroupPlanParticipant";

        Map<String, Object> header = SmartGridUtils.buildHeader(mobile, accessKeyId, secretKey);
        log.info("header:{}", header);

        HashMap<String, Object> requestData = new HashMap<>();
        requestData.put("areaId","0");
        requestData.put("areaLevel","1");
        requestData.put("pageNum","1");
        requestData.put("pageSize","100");
        //requestData.put("userName","李化");
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

    @Test
    public void test4() {
        String mobile = "15978197192";
        String huaweiUrl = "/SGCoreCommon/common/config/getAreaInformation.do";

        Map<String, Object> header = SmartGridUtils.buildHeader(mobile, accessKeyId, secretKey);
        log.info("header:{}", header);

        HashMap<String, Object> requestData = new HashMap<>();
        requestData.put("startLevel","3");
        requestData.put("endLevel","3");
        List<String> list = new ArrayList<>();
        list.add("771");
        requestData.put("cityIds",list);
        String param = GsonUtils.toJson(requestData);
        log.info("param:{}", param);

        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + huaweiUrl, param, header);
        log.info("httpResult:{}", httpResult);
    }

    @Test
    public void test5() {
        String mobile = "15978197192";
        String huaweiUrl = "/SGCoreCommon/common/config/getAreaInformation.do";

        Map<String, Object> header = SmartGridUtils.buildHeader(mobile, accessKeyId, secretKey);
        log.info("header:{}", header);

        HashMap<String, Object> requestData = new HashMap<>();
        requestData.put("startLevel","4");
        requestData.put("endLevel","4");
        List<String> list = new ArrayList<>();
        list.add("A2107");
        requestData.put("countryIds",list);
        String param = GsonUtils.toJson(requestData);
        log.info("param:{}", param);

        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + huaweiUrl, param, header);
        log.info("httpResult:{}", httpResult);
    }

    @Test
    public void test6() {
        String mobile = "15978197192";
        String huaweiUrl = "/SGCoreMarketing/groupService/getGroupPlanParticipant";

        Map<String, Object> header = SmartGridUtils.buildHeader(mobile, accessKeyId, secretKey);
        log.info("header:{}", header);

        HashMap<String, Object> requestData = new HashMap<>();
        requestData.put("areaId","0");
        requestData.put("areaLevel","1");
        requestData.put("pageNum","1");
        requestData.put("pageSize","20");
        requestData.put("userName","马正军");
        String param = GsonUtils.toJson(requestData);
        log.info("param:{}", param);

        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + huaweiUrl, param, header);
        log.info("httpResult:{}", httpResult);
    }


    @Test
    public void test7() {
       String location = "120.06385674071397,30.284102815819203";
       String location2 = "120.06369053851603,30.284089616624556";
        ApiResult apiResult = checkDistaneWhencSign(location, location2);
        System.out.println(GsonUtils.toJson(apiResult));
        System.out.println(5 % 60);
    }


    @Test
    public void test8() {
        String mobile = "15978197192";
        String huaweiUrl = "/SGCoreMarketing/groupService/addVisitRecord";

        Map<String, Object> header = SmartGridUtils.buildHeader(mobile, accessKeyId, secretKey);
        log.info("header:{}", header);

        Map<String,Object> map = new HashMap<>();
        map.put("visitId","127");
        map.put("activityId", "123");
        map.put("groupId","123");
        map.put("successFlag","Y");
        map.put("complaintFlag","y");
//        map.put("bizType","qwe");
        map.put("tvBoxExpireTime",133);
        String param = GsonUtils.toJson(map);
        log.info("param:{}", param);

        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + huaweiUrl, param, header);
        log.info("httpResult:{}", httpResult);
    }

    private ApiResult checkDistaneWhencSign(String dbLocation, String reqLocation) {
        String[] dbSplit = dbLocation.split(",");
        String[] reqSplit = reqLocation.split(",");
        double Lat1 = Double.parseDouble(dbSplit[1]);
        double Lon1 = Double.parseDouble(dbSplit[0]);
        double Lat2 = Double.parseDouble(reqSplit[1]);
        double Lon2 = Double.parseDouble(reqSplit[0]);
        double distance = DistanceUtils.getDistanceFromCoordinates(Lat1, Lon1, Lat2, Lon2);
        if (distance > 5000) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.GROUP_SERVICE_SIGN_DISTANCE_ERROR);
        }
        return ApiResult.of(0);
    }

    @Test
    public void updateSweepStreet() {
        String startTime = "2020-08-24 16:52:00";
        long time = DateUtil.parseDateTime(startTime).getTime();
        System.out.println("time = " + time);
        DateTime date = DateUtil.date(time);
        System.out.println("date = " + date);

        long l = System.currentTimeMillis();
        System.out.println("l = " + l);
    }

}
