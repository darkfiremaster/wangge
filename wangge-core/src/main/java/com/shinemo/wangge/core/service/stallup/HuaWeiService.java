package com.shinemo.wangge.core.service.stallup;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.UrlRedirectHandlerRequest;
import com.shinemo.stallup.domain.huawei.GetGridUserInfoResult;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;
import com.shinemo.stallup.domain.request.HuaWeiRequest;
import com.shinemo.stallup.domain.response.CommunityResponse;
import com.shinemo.stallup.domain.response.GetCustDetailResponse;
import com.shinemo.stallup.domain.response.GridUserListResponse;
import com.shinemo.stallup.domain.response.SearchResponse;
import com.shinemo.sweepfloor.domain.request.HuaweiBuildingDetailListRequest;
import com.shinemo.sweepfloor.domain.request.HuaweiBuildingRequest;
import com.shinemo.sweepfloor.domain.request.HuaweiCellAndBuildingsRequest;
import com.shinemo.sweepfloor.domain.request.HuaweiTenantRequest;
import com.shinemo.sweepfloor.domain.response.HuaWeiAddTenantsResponse;
import com.shinemo.sweepfloor.domain.response.HuaweiBuildingDetailsListResponse;
import com.shinemo.sweepfloor.domain.response.HuaweiCellsAndBuildingsResponse;

import java.util.List;

/**
 * 华为接口服务
 *
 * @author Chenzhe Mao
 * @date 2020-04-02
 */
public interface HuaWeiService {
	/**
	 * 获取网格系统的用户列表
	 *
	 * @param request
	 * @return
	 */
	ApiResult<List<String>> getUserList(HuaWeiRequest request);

	/**
	 * 华为地图搜索
	 *
	 * @param request
	 * @return
	 */
	ApiResult<SearchResponse> search(HuaWeiRequest request);

	/**
	 * 小区信息查询
	 *
	 * @param request
	 * @return
	 */
	ApiResult<CommunityResponse> queryCommunity(HuaWeiRequest request);

	/**
	 * 获取网格用户信息
	 *
	 * @param request
	 * @return
	 */
	ApiResult<List<GridUserRoleDetail>> getGridUserInfo(HuaWeiRequest request);

	/**
	 * 获取网格用户信息(透传华为返回值)
	 *
	 * @param request
	 * @return
	 */
	ApiResult<GetGridUserInfoResult.DataBean> getGridUserInfoDetail(HuaWeiRequest request);

	/**
	 * 获取网格用户列表
	 *
	 * @param request
	 * @return
	 */
	ApiResult<GridUserListResponse> getGridUserList(HuaWeiRequest request);

	/**
	 * 获取客户群详情
	 *
	 * @param request
	 * @return
	 */
	ApiResult<GetCustDetailResponse> getCustDetail(HuaWeiRequest request);

	/**
	 * 获取免登url
	 *
	 * @param request
	 * @return
	 */
	ApiResult<String> getRedirectUrl(UrlRedirectHandlerRequest request);

	/**
	 * 查询华为小区、、楼栋数据
	 * @return
	 */
	ApiResult<List<HuaweiCellsAndBuildingsResponse>> getCellsAndBuildings(HuaweiCellAndBuildingsRequest request);

	/**
	 * 添加楼栋
	 * @param request
	 * @return
	 */
	ApiResult<Void> addBuilding(HuaweiBuildingRequest request);

	/**
	 * 编辑楼栋
	 * @param request
	 * @return
	 */
	ApiResult<Void> updateBuilding(HuaweiBuildingRequest request);

	/**
	 * 添加住户
	 * @param request
	 * @return
	 */
	ApiResult<Void> addBuildingTenants(HuaweiTenantRequest request);
	/**
	 * 编辑住户
	 * @param request
	 * @return
	 */
	ApiResult<Void> updateBuildingTenants(HuaweiTenantRequest request);

	/**
	 * 楼栋信息查询（楼栋、单元、住户）
	 * @return
	 */
	ApiResult<List<HuaweiBuildingDetailsListResponse>> queryBuildingDetailList(HuaweiBuildingDetailListRequest request);
}
