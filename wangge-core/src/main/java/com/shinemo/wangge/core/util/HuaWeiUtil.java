package com.shinemo.wangge.core.util;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.stallup.domain.huawei.GetGridUserInfoResult;
import com.shinemo.stallup.domain.request.HuaWeiRequest;
import com.shinemo.wangge.core.service.stallup.HuaWeiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @Author shangkaihui
 * @Date 2020/7/15 14:47
 * @Desc
 */
@Slf4j
@Component
public class HuaWeiUtil {

    private static HuaWeiUtil huaWeiUtil;

    @Resource
    private HuaWeiService huaWeiService;


    @PostConstruct
    public void init() {
        huaWeiUtil = this;
    }

    /**
     * 获取华为的用户姓名
     *
     * @param mobile
     * @return
     */
    public static String getHuaweiUsername(String mobile) {
        String name = null;
        ApiResult<GetGridUserInfoResult.DataBean> result = null;
        try {
            result = huaWeiUtil.huaWeiService
                    .getGridUserInfoDetail(HuaWeiRequest.builder().mobile(mobile).build());
            if (result.isSuccess()) {
                name = result.getData().getUserName();
            }
        } catch (Exception e) {
            log.error("[getHuaweiUsername] failed call huaWeiService.getGridUserInfoDetail, mobile:{}, result:{}", mobile, result,e);
        }
        if (name == null) {
            name = SmartGridContext.getUserName();
            log.error("[getHuaweiUsername] 获取华为用户姓名为空,返回当前登录人姓名, mobile:{},name:{}", mobile, name);
        }
        return name;
    }


}
