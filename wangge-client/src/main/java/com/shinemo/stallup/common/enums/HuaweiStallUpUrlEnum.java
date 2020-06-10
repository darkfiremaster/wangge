package com.shinemo.stallup.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 摆摊url枚举
 *
 * @author Chenzhe Mao
 * @date 2020-04-30
 */
@AllArgsConstructor
@Getter
public enum HuaweiStallUpUrlEnum {
	GET_USER_LIST("/CMCC_GX_market/api/v2/getUserList", "获取网格系统的用户列表", "getUserList"),
    AUTH_USER("/CMCC_GX_market/api/v2/authUser", "查询用户信息","authUser"),
    QUERY_CELL_LOCATION("/CMCC_GX_market/api/v2/queryCellLocation", "小区地理信息查询、商机和预警查询","queryCellLocation"),
    QUERY_BELONG_GRID_USER("/CMCC_GX_market/api/v2/queryBelongGridUser", "查询网格人员信息","queryBelongGridUser"),
    QUERY_CELL_CUST_GROUP_DETAIL("/CMCC_GX_market/api/v2/queryCellCustGroupDetail", "获取客户群详情","queryCellCustGroupDetail"),
    SYNCHRONIZE_STALL_DATA("/CMCC_GX_market/api/v2/synchronizeStallData", "创建摆摊，同步华为","synchronizeStallData"),
    ;
    private String url;
    private String desc;
    private String method;
}