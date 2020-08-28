package com.shinemo.wangge.core.service.groupserviceday.impl;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.groupserviceday.domain.constant.GroupServiceDayConstants;
import com.shinemo.groupserviceday.domain.model.GroupServiceDayDO;
import com.shinemo.groupserviceday.domain.query.GroupServiceDayQuery;
import com.shinemo.groupserviceday.domain.request.GroupServiceDayRequest;
import com.shinemo.groupserviceday.error.GroupServiceDayErrorCodes;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.utils.EncryptUtil;
import com.shinemo.stallup.domain.utils.Md5Util;
import com.shinemo.wangge.core.service.groupserviceday.GroupSerDayRedirctService;
import com.shinemo.wangge.dal.mapper.GroupServiceDayMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 类说明:
 *
 * @author zengpeng
 */
@Service
@Slf4j
public class GroupSerDayRedirctServiceImpl implements GroupSerDayRedirctService {


    @NacosValue(value = "${groupSer.seed}", autoRefreshed = true)
    private String groupSeed;
    @NacosValue(value = "${huawei.groupSer.url}", autoRefreshed = true)
    private String groupSerUrl;
    @NacosValue(value = "${yujing.seed}", autoRefreshed = true)
    private String seed;
    @NacosValue(value = "${huawei.smshot.url}", autoRefreshed = true)
    private String smsHotUrl;

    @Resource
    private GroupServiceDayMapper groupServiceDayMapper;


    /**
     * 集团服务日跳转企业信息页面url拼接
     * @param groupId
     * @return
     */
    @Override
    public ApiResult<String> getRedirctGrouSerUrl(String groupId) {
        long timestamp = System.currentTimeMillis();

        Map<String, Object> formData = new HashMap<>();
        formData.put("mobile", SmartGridContext.getMobile());
        formData.put("groupid",groupId);
        formData.put("timestamp",timestamp);
        formData.put("menuid","groupinfo");

        log.info("[getRedirctGrouSerUrl] 请求参数 formData:{}", formData);
        String paramStr = EncryptUtil.buildParameterString(formData);

        //1、加密
        String encryptData = EncryptUtil.encrypt(paramStr, groupSeed);

        //2、生成签名
        String sign = Md5Util.getMD5Str(encryptData + "," + groupSeed + "," + timestamp);

        String url = groupSerUrl + "?";

        StringBuilder sb = new StringBuilder(url);
        sb.append("paramData=").append(encryptData)
                .append("&timestamp=").append(timestamp)
                .append("&sign=").append(sign);

        String groupInfoUrl = sb.toString();
        log.info("[getRedirctGrouSerUrl]企业信息 groupId:{},生成企业信息url:{}", groupId, groupInfoUrl);

        return ApiResult.of(0, groupInfoUrl);
    }

    @Override
    public ApiResult<String> getRedirctSmsHotUrl(Long activityId) {

        GroupServiceDayQuery serviceDayQuery = new GroupServiceDayQuery();
        serviceDayQuery.setId(activityId);
        GroupServiceDayDO serviceDayDO = groupServiceDayMapper.get(serviceDayQuery);
        if (serviceDayDO == null) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.ACTIVITY_NOT_EXIT);
        }
        String extend = serviceDayDO.getExtend();
        Map<String, Object> formData = new HashMap<>();

        if (!StringUtils.isBlank(extend)) {
            GroupServiceDayRequest.PartnerBean partnerBean = GsonUtils.fromGson2Obj(extend, GroupServiceDayRequest.PartnerBean.class);
            String gridId = partnerBean.getGridId();
            String gridName = partnerBean.getGridName();
            String cityId = partnerBean.getCityId();
            String cityName = partnerBean.getCityName();
            String countryId = partnerBean.getCountryId();
            String countryName = partnerBean.getCountryName();
            if (!StringUtils.isBlank(gridId) && !StringUtils.isBlank(gridName)) {
                formData.put("gridId", gridId);
                formData.put("gridName", gridName);
            }else if (!StringUtils.isBlank(cityId) && !StringUtils.isBlank(cityName)) {
                formData.put("gridId", cityId);
                formData.put("gridName", cityName);
            }else if (!StringUtils.isBlank(countryId) && !StringUtils.isBlank(countryName)) {
                formData.put("gridId", countryId);
                formData.put("gridName", countryName);
            }else {
                log.error("[getRedirctSmsHotUrl] grid info is null,partnerBean = {}",partnerBean);
            }
        }

        Map<String, String> map = new LinkedHashMap<>();
        map.put(serviceDayDO.getGroupId(),serviceDayDO.getGroupName());
        long timestamp = System.currentTimeMillis();

        formData.put("mobile", SmartGridContext.getMobile());

        formData.put("timestamp", timestamp);
        formData.put("building", map);
        formData.put("prehotObjectType", 8);
        formData.put("activityId", GroupServiceDayConstants.ID_PREFIX + activityId);

        log.info("[redirctSmsHot] 请求参数formData:{}", formData);
        String paramStr = EncryptUtil.buildParameterString(formData);

        //1、加密
        String encryptData = EncryptUtil.encrypt(paramStr, seed);

        //2、生成签名
        String sign = Md5Util.getMD5Str(encryptData + "," + seed + "," + timestamp);

        String url = smsHotUrl + "?";

        StringBuilder sb = new StringBuilder(url);
        sb.append("paramData=").append(encryptData)
                .append("&timestamp=").append(timestamp)
                .append("&sign=").append(sign);

        String smsHotUrl = sb.toString();
        log.info("[getRedirctSmsHotUrl]集团服务日活动id:{},生成短信预热url:{}", activityId, smsHotUrl);

        return ApiResult.of(0, smsHotUrl);
    }

}
