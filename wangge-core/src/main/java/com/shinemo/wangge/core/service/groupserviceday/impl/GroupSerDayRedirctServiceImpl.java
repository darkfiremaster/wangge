package com.shinemo.wangge.core.service.groupserviceday.impl;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.stallup.domain.utils.EncryptUtil;
import com.shinemo.stallup.domain.utils.Md5Util;
import com.shinemo.wangge.core.service.groupserviceday.GroupSerDayRedirctService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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

    /**
     * 集团服务日跳转企业信息页面url拼接
     * @param groupId
     * @return
     */
    @Override
    public ApiResult<String> getRedirctGrouSerUrl(Long groupId) {
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

}
