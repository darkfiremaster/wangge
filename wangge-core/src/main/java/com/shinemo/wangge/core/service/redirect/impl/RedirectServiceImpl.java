package com.shinemo.wangge.core.service.redirect.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.ShowTabVO;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.enums.SmartGridRoleEnum;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;
import com.shinemo.stallup.domain.utils.EncryptUtil;
import com.shinemo.stallup.domain.utils.Md5Util;
import com.shinemo.wangge.core.config.properties.YujingPropertity;
import com.shinemo.wangge.core.config.properties.ZhuangweiPropertity;
import com.shinemo.wangge.core.service.redirect.RedirectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author shangkaihui
 * @Date 2020/7/15 16:43
 * @Desc
 */
@Service
@Slf4j
public class RedirectServiceImpl implements RedirectService {

    @Resource
    private ZhuangweiPropertity zhuangweiPropertity;

    @Resource
    private YujingPropertity yujingPropertity;

    /**
     * 是否展示装移数据看板
     */
    @NacosValue(value = "${show.zhuangwei.databoard}", autoRefreshed = true)
    private Boolean showZhuangWeiDataBoard;

    /**
     * 是否展示智慧小屏
     */
    @NacosValue(value = "${show.smart.report}", autoRefreshed = true)
    private Boolean showSmartReport;

    /**
     * 能看到装移数据看板的角色
     */
    @NacosValue(value = "${show.zhuangwei.databoard.roleIdList}", autoRefreshed = true)
    private String showZhuangWeiDataBoardRoleIdList;

    /**
     * 能看到智慧小屏的角色
     */
    @NacosValue(value = "${show.smart.report.roleIdList}", autoRefreshed = true)
    private String showSmartReportRoleIdList;


    @Override
    public ApiResult<String> getRedirectUrl(Integer type) {
        if (type == 1) {
            return getZhuangyiDataBroadUrl();
        } else if (type == 2) {
            return getSmartReportUrl();
        }
        throw new ApiException("type类型错误");
    }


    @Override
    public ApiResult<ShowTabVO> showTab() {
        ShowTabVO showTabVO = new ShowTabVO();
        showTabVO.setShowZhuangWeiDataBoard(showZhuangWeiDataBoard);
        showTabVO.setShowSmartReport(showSmartReport);
        return ApiResult.of(0, showTabVO);
    }

    private ApiResult<String> getSmartReportUrl() {
        //判断角色权限
        //List<GridUserRoleDetail.GridRole> roleList = SmartGridContext.getSelectGridUserRoleDetail().getRoleList();
        //List<String> roleIdList = roleList.stream().map(GridUserRoleDetail.GridRole::getId).collect(Collectors.toList());
        //List<String> showRoleIdList = StrUtil.split(showSmartReportRoleIdList, ',');
        //boolean canShow = CollUtil.containsAny(roleIdList, showRoleIdList);
        //if (!canShow) {
        //    log.error("[showTab] 该用户无权查看管理视图,mobile:{},roleIdList:{}", SmartGridContext.getMobile(), roleIdList);
        //    throw new ApiException("您当前无权限查看");
        //}

        String seed = yujingPropertity.getSeed();
        String domain = yujingPropertity.getDomain();
        String path = yujingPropertity.getSmartReportUrl();
        long timestamp = System.currentTimeMillis();
        Map<String, Object> formData = new HashMap<>();
        formData.put("mobile", SmartGridContext.getMobile());
        formData.put("menuId", 1);
        formData.put("timestamp", timestamp);
        String paramData = EncryptUtil.buildParameterString(formData);
        log.info("[getSmartReportUrl] 加密前参数paramData:{}", paramData);
        //1、加密
        String encryptData = EncryptUtil.encrypt(paramData, seed);

        //2、生成签名
        String sign = Md5Util.getMD5Str(encryptData + "," + seed + "," + timestamp);

        String url = domain + path + "?";
        StringBuilder sb = new StringBuilder(url);
        url = sb.append("paramData=").append(encryptData)
                .append("&timestamp=").append(timestamp)
                .append("&sign=").append(sign).toString();


        log.info("[getSmartReportUrl] 生成智慧小屏报表跳转url:{}", url);
        return ApiResult.of(0, url);
    }

    private ApiResult<String> getZhuangyiDataBroadUrl() {
        //判断角色权限
        List<GridUserRoleDetail.GridRole> roleList = SmartGridContext.getSelectGridUserRoleDetail().getRoleList();
        List<String> roleIdList = roleList.stream().map(GridUserRoleDetail.GridRole::getId).collect(Collectors.toList());
        List<String> showRoleIdList = StrUtil.split(showZhuangWeiDataBoardRoleIdList, ',');
        boolean canShow = CollUtil.containsAny(roleIdList, showRoleIdList);
        if (!canShow) {
            log.error("[showTab] 该用户无权查看装维数据看板,mobile:{},roleIdList:{}", SmartGridContext.getMobile(), roleIdList);
            throw new ApiException("您当前无权限查看");
        }

        String seed = zhuangweiPropertity.getSeed();
        String domain = zhuangweiPropertity.getDomain();
        String path = zhuangweiPropertity.getDataBoardUrl();
        long timestamp = System.currentTimeMillis();
        Map<String, Object> formData = new HashMap<>();
        formData.put("mobileTel", SmartGridContext.getMobile());
        formData.put("gridName", SmartGridContext.getSelectGridUserRoleDetail().getId());
        formData.put("areaName", SmartGridContext.getSelectGridUserRoleDetail().getCityCode());
        formData.put("countyName", SmartGridContext.getSelectGridUserRoleDetail().getCountyCode());
        //todo 将我们的角色转化为装移那边的角色名称
        String roleName = "";
        String roleId = SmartGridContext.getSelectGridUserRoleDetail().getRoleList().get(0).getId();
        if (roleId.equals(SmartGridRoleEnum.GRID_CAPTAIN.getId())
                || roleId.equals(SmartGridRoleEnum.GRID_MANAGER.getId())
                || roleId.equals(SmartGridRoleEnum.BUSINESS_HALL.getId())) {
            roleName = "网格长";
        } else if (roleId.equals(SmartGridRoleEnum.DECORATOR.getId())) {
            roleName = "一线装维";
        } else {
            throw new ApiException("角色错误");
        }
        formData.put("roleName", roleName);
        formData.put("timestamp", timestamp);
        String paramData = EncryptUtil.buildParameterString(formData, Boolean.FALSE);
        try {
            log.info("[getZhuangyiDataBroadUrl] 加密前参数paramData:{}", URLDecoder.decode(paramData, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //1、加密
        String encryptData = EncryptUtil.encrypt(paramData, seed);

        //2、生成签名
        String sign = Md5Util.getMD5Str(encryptData + "," + seed + "," + timestamp);

        String url = domain + path + "?";
        StringBuilder sb = new StringBuilder(url);
        url = sb.append("paramData=").append(encryptData)
                .append("&timestamp=").append(timestamp)
                .append("&sign=").append(sign).toString();


        log.info("[getZhuangyiDataBroadUrl] 生成装移数据看板跳转url:{}", url);
        return ApiResult.of(0, url);
    }
}
