package com.shinemo.wangge.web.controller.common;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.shinemo.client.util.WebUtil;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.operate.vo.UserOperateLogVO;
import com.shinemo.smartgrid.constants.SmartGridConstant;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.common.error.StallUpErrorCodes;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;
import com.shinemo.wangge.core.service.gridinfo.SmartGridInfoService;
import com.shinemo.wangge.core.service.operate.OperateService;
import com.shinemo.wangge.core.service.stallup.HuaWeiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/9 10:16
 * @Desc
 */
@RestController
@Slf4j
@RequestMapping("/operate")
public class OperateController {

    @Resource
    private OperateService operateService;

    @Resource
    private ThreadPoolTaskExecutor asyncServiceExecutor;

    @Resource
    private HuaWeiService huaWeiService;

    @Resource
    private SmartGridInfoService smartGridInfoService;

    @NacosValue(value = "${domain}", autoRefreshed = true)
    private String domain = "127.0.0.1";

    public static final int EXPIRE_TIME = 7 * 60 * 60 * 24;

    @PostMapping("/addUserOperateLog")
    public ApiResult<Void> addUserOperateLog(@RequestBody UserOperateLogVO userOperateLogVO) {
        userOperateLogVO.setMobile(SmartGridContext.getMobile());
        userOperateLogVO.setUid(SmartGridContext.getUid());
        userOperateLogVO.setUserName(SmartGridContext.getUserName());
        asyncServiceExecutor.submit(() -> operateService.addUserOperateLog(userOperateLogVO));
        //刷新用户所有网格信息
//        ApiResult<String> stringApiResult = operateService.genGridInfoToken(null);
//
//        WebUtil.addCookie(request, response, SmartGridConstant.ALL_GRID_INFO_COOKIE, stringApiResult.getData(),
//                domain, "/", EXPIRE_TIME, false);

        return ApiResult.of(0);
    }

    @PostMapping("/refreshSelectGridInfo")
    public ApiResult<Void> refreshSelectGridInfo(@RequestBody GridUserRoleDetail gridDetail,
                                                 HttpServletRequest request, HttpServletResponse response) {

        Assert.notNull(gridDetail,"gridDetail is null");

        ApiResult<String> result = operateService.genGridInfoToken(gridDetail);

        WebUtil.addCookie(request, response, SmartGridConstant.SELECT_GRID_INFO_COOKIE, result.getData(),
                domain, "/", EXPIRE_TIME, false);


        return ApiResult.of(0);
    }

    @GetMapping("/getSmartGridList")
    public ApiResult<List<GridUserRoleDetail>> getSmartGridList() {

        String gridInfo = SmartGridContext.getGridInfo();
        List<GridUserRoleDetail> gridUserRoleDetails = GsonUtils.fromJsonToList(gridInfo, GridUserRoleDetail[].class);
        String selectGridInfo = SmartGridContext.getSelectGridInfo();
        if (StringUtils.isBlank(selectGridInfo)) {
            gridUserRoleDetails.get(0).setType(1);
        }else {
            GridUserRoleDetail detail = GsonUtils.fromGson2Obj(selectGridInfo, GridUserRoleDetail.class);
            for (GridUserRoleDetail detail2: gridUserRoleDetails) {
                if (detail.getId().equals(detail2.getId())) {
                    detail2.setType(1);
                }
            }
        }


        return ApiResult.of(0,gridUserRoleDetails);
    }


}
