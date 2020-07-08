package com.shinemo.wangge.core.service.stallup;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.stallup.domain.model.CommunityVO;
import com.shinemo.stallup.domain.model.StallUpActivity;
import com.shinemo.stallup.domain.request.GetParentListRequest;
import com.shinemo.stallup.domain.request.StallUpEndRequest;
import com.shinemo.stallup.domain.request.StallUpRequest;
import com.shinemo.stallup.domain.response.*;
import com.shinemo.wangge.core.config.StallUpConfig;

import java.util.List;

/**
 * 智慧网格 摆摊活动服务
 *
 * @author Chenzhe Mao
 * @date 2020-04-02
 */
public interface StallUpService {

	/**
	 * 创建摆摊
	 *
	 * @param request
	 * @return
	 */
	void create(StallUpRequest request);

	/**
	 * 取消摆摊
	 *
	 * @param request
	 * @return
	 */
	void cancel(StallUpRequest request);

	/**
	 * 摆摊打卡
	 *
	 * @param request
	 * @return
	 */
	void sign(StallUpRequest request);

	/**
	 * 结束摆摊
	 *
	 * @param request
	 * @return
	 */
	void end(StallUpRequest request);

	/**
	 * 自动结束摆摊
	 *
	 * @param request
	 * @return
	 */
	void autoEnd(StallUpRequest request);

	/**
	 * 查询摆摊计划
	 *
	 * @param id
	 * @return
	 */
	StallUpActivity getStallUp(Long id);

	/**
	 * 查询摆摊详情
	 *
	 * @param id
	 * @param mobile
	 * @return
	 */
	ApiResult<GetDetailResponse> getStallUpActivity(Long id, String mobile);

	/**
	 * 获取已结束摆摊列表
	 *
	 * @param mobile
	 * @param page
	 * @param pageSize
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	ApiResult<GetEndListResponse> getEndList(String mobile, Integer page, Integer pageSize, Long startTime, Long endTime);

	/**
	 * 获取已开始和待开始列表
	 *
	 * @param mobile
	 * @return
	 */
	ApiResult<GetStallUpListResponse> getList(String mobile);

	/**
	 * 获取简要摆摊信息
	 *
	 * @param mobile
	 * @return
	 */
	ApiResult<GetStallUpSimpleInfoResponse> getSimpleInfo(String mobile);

	/**
	 * 获取摆摊情况
	 *
	 * @param id
	 * @param mobile
	 * @return
	 */
	ApiResult<GetDetailResponse> getDetail(Long id, String mobile);

	/**
	 * 获取摆摊配置
	 *
	 * @return
	 */
	ApiResult<StallUpConfig.ConfigDetail> getConfig();

	/**
	 * 保存办理量
	 *
	 * @param request
	 * @return
	 */
	ApiResult<Boolean> saveBiz(StallUpEndRequest request);

	/**
	 * 获取网格角色人员树
	 *
	 * @param mobile
	 * @param gridId
	 * @return
	 */
	ApiResult<List<GetGridUserTree>> getGridUserTree(String mobile, String gridId);

	/**
	 * 获取父摆摊列表
	 *
	 * @param request
	 * @return
	 */
	ApiResult<GetParentListResponse> getParentList(GetParentListRequest request);

	/**
	 * 获取父摆摊详情
	 *
	 * @param id
	 * @return
	 */
	ApiResult<GetParentDetailResponse> getParentDetail(Long id);

	/**
	 * 获取父摆摊今日统计
	 *
	 * @param gridIds
	 * @return
	 */
	ApiResult<GetParentSimpleResponse> getParentSimple(List<String> gridIds);
	
	List<Long> getGridBiz(String uid);
	
	ApiResult<Void> updateHomePageGridBiz(String uid, List<Long> gridBiz);

	ApiResult<SmsHotResponse> getSmsHot(Long activityId);

	ApiResult<List<CommunityVO>> getRecentCommunity(String mobile);

	ApiResult<String> getRedirctSmsHotUrl(Long activityId);

	ApiResult<String> getRedirctSmsHotUrlNotStallUp(String communityId,String communityName);
}