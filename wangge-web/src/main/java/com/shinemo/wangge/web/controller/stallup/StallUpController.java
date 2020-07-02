package com.shinemo.wangge.web.controller.stallup;

import com.shinemo.common.annotation.SmIgnore;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.domain.UrlRedirectHandlerRequest;
import com.shinemo.smartgrid.utils.AESUtil;
import com.shinemo.stallup.common.error.StallUpErrorCodes;
import com.shinemo.stallup.common.statemachine.InvalidStateTransitionException;
import com.shinemo.stallup.domain.enums.StallUpStatusEnum;
import com.shinemo.stallup.domain.event.StallUpEvent;
import com.shinemo.stallup.domain.model.CommunityVO;
import com.shinemo.stallup.domain.model.GridUserDetail;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;
import com.shinemo.stallup.domain.model.StallUpActivity;
import com.shinemo.stallup.domain.request.*;
import com.shinemo.stallup.domain.response.*;
import com.shinemo.wangge.core.config.StallUpConfig;
import com.shinemo.wangge.core.config.StallUpStateMachine;
import com.shinemo.wangge.core.service.stallup.HuaWeiService;
import com.shinemo.wangge.core.service.stallup.StallUpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 摆摊
 *
 * @author Chenzhe Mao
 * @date 2020-04-02
 */
@RestController
@RequestMapping("stallUp")
@Slf4j
public class StallUpController {

	private static final Integer IMAGE_MAX_NUM = 4;
	private static final Integer TITLE_MAX_LENGTH = 20;

	@Resource
	private HuaWeiService huaWeiService;

	@Resource
	private StallUpService stallUpService;

	@Value("${smartgrid.huawei.aesKey}")
	public String aeskey;

	@GetMapping("search")
	@SmIgnore
	public ApiResult<SearchResponse> search(@RequestParam String keywords,
                                            @RequestParam String location) {
		if (!StringUtils.hasText(keywords) || !StringUtils.hasText(location)) {
			return ApiResult.of(0);
		}
		return huaWeiService.search(HuaWeiRequest.builder()
			.keywords(keywords)
			.location(location)
			.mobile(getMobile())
			.build()
		);
	}

	@GetMapping("getCommunityDetail")
	@SmIgnore
	public ApiResult<CommunityResponse> getCommunityDetail(@RequestParam String id) {
		if (id == null) {
			return ApiResult.of(0);
		}
		return huaWeiService.queryCommunity(HuaWeiRequest.builder()
			.communityId(id)
			.mobile(getMobile())
			.build());
	}

	@GetMapping("getGridDetail")
	@SmIgnore
	public ApiResult<List<GridUserRoleDetail>> getGridDetail() {
		String mobile = getMobile();
		return huaWeiService.getGridUserInfo(HuaWeiRequest.builder()
			.mobile(mobile)
			.build());
	}

	@GetMapping("getGridList")
	@SmIgnore
	public ApiResult<GridUserListResponse> getGridList(@RequestParam(required = false) String id) {
		if (id == null) {
			GridUserListResponse response = new GridUserListResponse();
			response.setGetGridUserList(Arrays.asList(GridUserDetail.builder().seMobile(AESUtil.encrypt(getMobile(), aeskey)).name(SmartGridContext.getUserName()).role("").build()));
			return ApiResult.of(0, response);
		}
		return huaWeiService.getGridUserList(HuaWeiRequest.builder()
			.gridId(id)
			.mobile(getMobile())
			.build());
	}

	@PostMapping("create")
	@SmIgnore
	public ApiResult<Boolean> create(@RequestBody StallUpCreateRequest request) {

		Assert.notNull(request, "request is null");
		Assert.notNull(request.getStartTime(), "request's startTime is null");
		Assert.notNull(request.getEndTime(), "request's endTime is null");
		Assert.notNull(request.getTitle(), "request's title is null");
		Assert.notNull(request.getCommunityName(), "request's communityName is null");
		Assert.notNull(request.getAddress(), "request's address is null");
		Assert.notNull(request.getLocation(), "request's location is null");
		Assert.isTrue(!CollectionUtils.isEmpty(request.getPartnerList()), "request's partnerList is invalid");
		Assert.isTrue(!CollectionUtils.isEmpty(request.getCommunityVOS()), "request's communityVOS is invalid");
		if (request.getTitle().length() > TITLE_MAX_LENGTH) {
			log.error("[create] title length error, request:{}", request);
			throw new ApiException(StallUpErrorCodes.TITLE_LENGTH_ERROR);
		}
		if(request.getStartTime() >= request.getEndTime()){
			log.error("[create] time error, request:{}", request);
			throw new ApiException(StallUpErrorCodes.TIME_ERROR);
		}
		if (request.getCommunityVOS().size() > 5) {
			log.error("[create] communityVOS size error, request:{}", request);
			throw new ApiException(StallUpErrorCodes.COMMUNITY_SIZE_ERROR);
		}
		request.setUid(getUid());
		request.setOrgId(getOrgId());
		request.setMobile(getMobile());
		request.setStatus(StallUpStatusEnum.NOT_EXIST.getId());
		try {
			StallUpStateMachine.handler(request, new StallUpEvent(StallUpEvent.StallUpEventEnum.CREATE));
			return ApiResult.of(0, true);
		} catch (InvalidStateTransitionException e) {
			log.error("[create] StallUpStateMachine.handler error, request:{}", request, e);
			throw new ApiException(StallUpErrorCodes.BASE_ERROR);
		}
	}

	@PostMapping("cancel")
	@SmIgnore
	public ApiResult<Boolean> cancel(@RequestBody StallUpCancelRequest request) {

		Assert.notNull(request, "request is null");
		Long id = request.getId();
		Assert.notNull(id, "request's id is null");
		String mobile = getMobile();
		StallUpActivity activity = stallUpService.getStallUp(id);
		if (activity == null || !mobile.equals(activity.getMobile())) {
			log.error("[cancel] id and mobile not match, id:{}, mobile:{}", id, mobile);
			throw new ApiException(StallUpErrorCodes.BASE_ERROR);
		}
		if (StallUpStatusEnum.CANCELED.getId() == activity.getStatus()) {
			log.error("[cancel] already cancel,request:{}", request);
			throw new ApiException(StallUpErrorCodes.ALREADY_CANCEL_ERROR);
		}
		request.setUid(getUid());
		request.setStatus(activity.getStatus());
		request.setMobile(mobile);
		request.setActivity(activity);
		try {
			StallUpStateMachine.handler(request, new StallUpEvent(StallUpEvent.StallUpEventEnum.CANCEL));
			return ApiResult.of(0, null);
		} catch (InvalidStateTransitionException e) {
			log.error("[cancel] StallUpStateMachine.handler error, request:{}", request, e);
			throw new ApiException(StallUpErrorCodes.BASE_ERROR);
		}
	}

	@PostMapping("sign")
	@SmIgnore
	public ApiResult<Boolean> sign(@RequestBody StallUpSignRequest request) {

		Assert.notNull(request, "request is null");
		Long id = request.getId();
		Assert.notNull(id, "request's id is null");
		Assert.notNull(request.getLocation(), "request's location is null");
		Assert.notNull(request.getAddress(), "request's address is null");
		String mobile = getMobile();
		StallUpActivity activity = stallUpService.getStallUp(id);
		if (activity == null || !mobile.equals(activity.getMobile())) {
			log.error("[sign] id and mobile not match, id:{}, mobile:{}", id, mobile);
			throw new ApiException(StallUpErrorCodes.BASE_ERROR);
		}
		if (StallUpStatusEnum.STARTED.getId() == activity.getStatus()) {
			log.error("[sign] already sign,request:{}", request);
			throw new ApiException(StallUpErrorCodes.ALREADY_SIGN_ERROR);
		}
		request.setUid(getUid());
		request.setStatus(activity.getStatus());
		request.setMobile(mobile);
		request.setActivity(activity);
		try {
			StallUpStateMachine.handler(request, new StallUpEvent(StallUpEvent.StallUpEventEnum.SIGN));
			return ApiResult.of(0, true);
		} catch (InvalidStateTransitionException e) {
			log.error("[sign] StallUpStateMachine.handler error, request:{}", request, e);
			throw new ApiException(StallUpErrorCodes.BASE_ERROR);
		}
	}

	@PostMapping("end")
	@SmIgnore
	public ApiResult<Boolean> end(@RequestBody StallUpEndRequest request) {

		Assert.notNull(request, "request is null");
		Long id = request.getId();
		Assert.notNull(id, "request's id is null");
		Assert.notNull(request.getLocation(), "request's location is null");
		Assert.notNull(request.getAddress(), "request's address is null");
		if (request.getImageList() == null || request.getImageList().size() == 0) {
			log.error("[end] image is null, request:{}", request);
			throw new ApiException(StallUpErrorCodes.NO_IMAGE_ERROR);
		}
		if (request.getImageList().size() > IMAGE_MAX_NUM) {
			log.error("[end] image size error, request:{}", request);
			throw new ApiException(StallUpErrorCodes.IMAGE_NUM_ERROR);
		}
		String mobile = getMobile();
		StallUpActivity activity = stallUpService.getStallUp(id);
		if (activity == null || !mobile.equals(activity.getMobile())) {
			log.error("[end] id and mobile not match, id:{}, mobile:{}", id, mobile);
			throw new ApiException(StallUpErrorCodes.BASE_ERROR);
		}
		if (StallUpStatusEnum.END.getId() == activity.getStatus() ||
			StallUpStatusEnum.AUTO_END.getId() == activity.getStatus() ||
			StallUpStatusEnum.ABNORMAL_END.getId() == activity.getStatus()) {
			log.error("[end] already end,request:{}", request);
			throw new ApiException(StallUpErrorCodes.ALREADY_END_ERROR);
		}
		request.setUid(getUid());
		request.setStatus(activity.getStatus());
		request.setMobile(mobile);
		request.setActivity(activity);
		try {
			StallUpStateMachine.handler(request, new StallUpEvent(StallUpEvent.StallUpEventEnum.END));
			return ApiResult.of(0, true);
		} catch (InvalidStateTransitionException e) {
			log.error("[end] StallUpStateMachine.handler error, request:{}", request, e);
			throw new ApiException(StallUpErrorCodes.BASE_ERROR);
		}
	}

	@GetMapping("getList")
	@SmIgnore
	public ApiResult<GetStallUpListResponse> getList() {
		return stallUpService.getList(getMobile());
	}

	@GetMapping("getSimpleInfo")
	@SmIgnore
	public ApiResult<GetStallUpSimpleInfoResponse> getSimpleInfo() {
		return stallUpService.getSimpleInfo(getMobile());
	}

	@GetMapping("getEndList")
	@SmIgnore
	public ApiResult<GetEndListResponse> getEndList(@RequestParam(required = false) Integer page,
                                                    @RequestParam(required = false) Integer pageSize,
                                                    @RequestParam(required = false) Long startTime,
                                                    @RequestParam(required = false) Long endTime
	                                                ) {
		page = page == null ? 1 : page;
		pageSize = pageSize == null ? 20 : pageSize;
		return stallUpService.getEndList(getMobile(), page, pageSize, startTime, endTime);
	}

	@PostMapping("getCustDetail")
	@SmIgnore
	public ApiResult<GetCustDetailResponse> getCustDetail(@RequestBody GetCustDetailRequest request) {
		Assert.notNull(request, "request is null");
		if (request.getIdList() == null || request.getIdList().size() == 0 || !StringUtils.hasText(request.getCommunityId())) {
			return ApiResult.of(0, GetCustDetailResponse.builder()
				.custList(new ArrayList<>())
				.total(0L)
				.build());
		}
		Integer page = request.getPage() == null ? 1 : request.getPage();
		Integer pageSize = request.getPageSize() == null ? 100 : request.getPageSize();

		return huaWeiService.getCustDetail(
			HuaWeiRequest.builder()
				.custIdList(request.getIdList())
				.communityId(request.getCommunityId())
				.page(page)
				.pageSize(pageSize)
				.mobile(getMobile())
				.build()
		);
	}

	@GetMapping("getDetail")
	@SmIgnore
	public ApiResult<GetDetailResponse> getDetail(@RequestParam Long id) {
		Assert.notNull(id, "id is null");
		return stallUpService.getDetail(id, getMobile());
	}

	@GetMapping("getConfig")
	@SmIgnore
	public ApiResult<StallUpConfig.ConfigDetail> getConfig() {
		return stallUpService.getConfig();
	}

	@PostMapping("redirect")
	@SmIgnore
	public ApiResult<String> redirect(@RequestBody UrlRedirectHandlerRequest request) {
		request.setUserPhone(getMobile());
		return huaWeiService.getRedirectUrl(request);
	}

	@PostMapping("saveBiz")
	@SmIgnore
	public ApiResult<Boolean> saveBiz(@RequestBody StallUpEndRequest request) {

		Assert.notNull(request, "request is null");
		Assert.notNull(request.getId(), "request's id is null");
		request.setUid(SmartGridContext.getLongUid());
		request.setMobile(getMobile());
		return stallUpService.saveBiz(request);
	}

	@GetMapping("getActivityDetail")
	@SmIgnore
	public ApiResult<GetDetailResponse> getActivityDetail(@RequestParam Long id) {
		Assert.notNull(id, "id is null");
		return stallUpService.getStallUpActivity(id, getMobile());
	}

	@GetMapping("getGridUserTree")
	@SmIgnore
	public ApiResult<List<GetGridUserTree>> getGridUserTree(@RequestParam String gridId) {
		Assert.notNull(gridId, "gridId is null");
		String mobile = getMobile();
		return stallUpService.getGridUserTree(mobile,gridId);
	}

	@GetMapping("getParentList")
	@SmIgnore
	public ApiResult<GetParentListResponse> getParentList(@RequestParam(required = false) Long startTime,
                                                          @RequestParam(required = false) Long endTime,
                                                          @RequestParam(required = false) String seMobile,
                                                          @RequestParam(required = false) Integer status,
                                                          @RequestParam(required = false) Integer pageSize,
                                                          @RequestParam(required = false) Integer curentPage,
                                                          @RequestParam String gridId) {
		Assert.notNull(gridId, "gridId is null");
		curentPage = curentPage == null ? 1 : curentPage;
		pageSize = pageSize == null ? 20 : pageSize;
		GetParentListRequest request = new GetParentListRequest();
		request.setStartTime(startTime);
		request.setEndTime(endTime);
		request.setSeMobile(seMobile);
		request.setPageSize(pageSize);
		request.setCurrentPage(curentPage);
		request.setGridId(gridId);
		request.setStatus(status);
		return stallUpService.getParentList(request);
	}

	@GetMapping("getParentDetail")
	@SmIgnore
	public ApiResult<GetParentDetailResponse> getParentDetail(@RequestParam Long id) {
		Assert.notNull(id, "id is null");
		return stallUpService.getParentDetail(id);
	}

	@GetMapping("getSmsHot")
	public ApiResult<SmsHotResponse> getSmsHot(@RequestParam Long activityId) {
		Assert.notNull(activityId, "id is null");
		return stallUpService.getSmsHot(activityId);
	}

	@GetMapping("getRedirctSmsHotUrl")
	@SmIgnore
	public ApiResult<String> redirctSmsHot(@RequestParam Long activityId) {
		Assert.notNull(activityId, "id is null");
		return stallUpService.getRedirctSmsHotUrl(activityId);
	}


	@GetMapping("getRecentCommunity")
	public ApiResult<List<CommunityVO>> getRecentCommunity() {

		return stallUpService.getRecentCommunity(getMobile());
	}

	private Long getUid() {
		Long uid = SmartGridContext.getLongUid();
		Assert.notNull(uid, "uid is null");
		return uid;
	}

	private String getMobile() {
		String mobile = SmartGridContext.getMobile();
		Assert.notNull(mobile, "mobile is null");
		return mobile;
	}

	private Long getOrgId() {
		long orgId = SmartGridContext.getLongOrgId();
		Assert.notNull(orgId, "orgId is null");
		return orgId;
	}

}
