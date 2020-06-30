package com.shinemo.wangge.web.controller.common;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.shinemo.client.util.GsonUtil;
import com.shinemo.client.util.WebUtil;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.operate.vo.UserOperateLogVO;
import com.shinemo.smartgrid.constants.SmartGridConstant;
import com.shinemo.smartgrid.domain.GridInfoToken;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;
import com.shinemo.wangge.core.service.gridinfo.SmartGridInfoService;
import com.shinemo.wangge.core.service.operate.OperateService;
import com.shinemo.wangge.core.service.stallup.HuaWeiService;
import com.shinemo.wangge.core.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.shinemo.util.WebUtils.getValueFromCookies;

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
    private UserService userService;

    @Resource
    private SmartGridInfoService smartGridInfoService;

    @NacosValue(value = "${domain}", autoRefreshed = true)
    private String domain = "developer.e.uban360.com";

    public static final int EXPIRE_TIME = 60 * 60;

    @PostMapping("/addUserOperateLog")
    public ApiResult<Void> addUserOperateLog(@RequestBody UserOperateLogVO userOperateLogVO,
                                             HttpServletRequest request, HttpServletResponse response) {
        userOperateLogVO.setMobile(SmartGridContext.getMobile());
        userOperateLogVO.setUid(SmartGridContext.getUid());
        userOperateLogVO.setUserName(SmartGridContext.getUserName());
        userOperateLogVO.setGridInfo(SmartGridContext.getGridInfo());
        userOperateLogVO.setSelectGridInfo(SmartGridContext.getSelectGridInfo());
        asyncServiceExecutor.submit(() -> operateService.addUserOperateLog(userOperateLogVO));

        //刷新用户所有网格信息
        if (userOperateLogVO.getType() == 1) {
            ApiResult<String> stringApiResult = operateService.genGridInfoToken(null);
            WebUtil.addCookie(request, response, SmartGridConstant.ALL_GRID_INFO_COOKIE, stringApiResult.getData(), domain, "/", EXPIRE_TIME, false);

            String tempCookies = getValueFromCookies(SmartGridConstant.TEMP_SELECT_GRID_INFO_COOKIE, request.getCookies());
            if (StringUtils.isBlank(tempCookies)) {
                String selectCookie = getValueFromCookies(SmartGridConstant.SELECT_GRID_INFO_COOKIE, request.getCookies());
                if (StringUtils.isBlank(selectCookie)) {
                    GridInfoToken selectToken = getToken(stringApiResult.getData());
                    GridUserRoleDetail detail = selectToken.getGridList().get(0);
                    ApiResult<String> stringApiResult1 = operateService.genGridInfoToken(detail);
                    tempCookies = stringApiResult1.getData();
                }else {
                    GridInfoToken selectToken = getToken(selectCookie);
                    GridUserRoleDetail gridDetail = selectToken.getGridDetail();
                    List<GridUserRoleDetail> gridList = getToken(stringApiResult.getData()).getGridList();
                    List<String> idList = gridList.stream().map(GridUserRoleDetail::getId).collect(Collectors.toList());
                    if (!idList.contains(gridDetail.getId())) {
                        GridUserRoleDetail detail = gridList.get(0);
                        ApiResult<String> stringApiResult1 = operateService.genGridInfoToken(detail);
                        tempCookies = stringApiResult1.getData();
                    }else {
                        tempCookies = selectCookie;
                    }
                }
            }

            WebUtil.addCookie(request, response, SmartGridConstant.TEMP_SELECT_GRID_INFO_COOKIE, tempCookies,
                    domain, "/", 0, false);
            WebUtil.addCookie(request, response, SmartGridConstant.SELECT_GRID_INFO_COOKIE, tempCookies, domain, "/", EXPIRE_TIME, false);

            //String token = new String(Base64.decodeBase64(stringApiResult.getData()), StandardCharsets.UTF_8);
            //GridInfoToken gridInfoToken = GsonUtil.fromGson2Obj(token, GridInfoToken.class);
            //List<GridUserRoleDetail> gridList = gridInfoToken.getGridList();
            //if (!CollectionUtils.isEmpty(gridList) && !Objects.equals(gridList.get(0).getId(), "0")) {
            //    //存在网格信息
            //    log.info("[addUserOperateLog]  start update gridinfo,mobile:{}", SmartGridContext.getMobile());
            //    String finalMobile = SmartGridContext.getMobile();
            //    asyncServiceExecutor.submit(() -> {
            //        userService.updateUserGridRoleRelation(gridInfoToken.getGridList(), finalMobile);
            //    });
            //}

        }

        return ApiResult.of(0);
    }

    @PostMapping("/refreshSelectGridInfo")
    public ApiResult<Void> refreshSelectGridInfo(@RequestBody GridUserRoleDetail gridDetail, HttpServletRequest request, HttpServletResponse response) {

        Assert.notNull(gridDetail, "gridDetail is null");

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
        } else {
            if (gridUserRoleDetails.size() == 1) {
                GridUserRoleDetail detai2 = gridUserRoleDetails.get(0);
                if (detai2.getId().equals("0")) {
                    return ApiResult.of(0, new ArrayList<>());
                }
            }
            GridUserRoleDetail detail = GsonUtils.fromGson2Obj(selectGridInfo, GridUserRoleDetail.class);
            for (GridUserRoleDetail detail2 : gridUserRoleDetails) {
                if (detail.getId().equals(detail2.getId())) {
                    detail2.setType(1);
                }
            }
        }

        return ApiResult.of(0, gridUserRoleDetails);
    }

    private GridInfoToken getToken(String gridInfo) {
        String usableToken = new String(Base64.decodeBase64(gridInfo), StandardCharsets.UTF_8);
        GridInfoToken gridInfoToken = GsonUtil.fromGson2Obj(usableToken, GridInfoToken.class);
        return gridInfoToken;
    }


}
