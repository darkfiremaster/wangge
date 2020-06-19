package com.shinemo.wangge.core.service.stallup.impl;

import com.google.common.collect.Lists;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.CommonHuaweiResponse;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.domain.UrlRedirectHandlerRequest;
import com.shinemo.smartgrid.http.HttpConnectionUtils;
import com.shinemo.smartgrid.http.HttpResult;
import com.shinemo.smartgrid.utils.AESUtil;
import com.shinemo.smartgrid.utils.SmartGridUtils;
import com.shinemo.smartgrid.utils.ThreadPoolUtil;
import com.shinemo.stallup.common.enums.HuaweiStallUpUrlEnum;
import com.shinemo.stallup.common.error.StallUpErrorCodes;
import com.shinemo.stallup.domain.huawei.*;
import com.shinemo.stallup.domain.model.*;
import com.shinemo.stallup.domain.request.HuaWeiRequest;
import com.shinemo.stallup.domain.response.CommunityResponse;
import com.shinemo.stallup.domain.response.GetCustDetailResponse;
import com.shinemo.stallup.domain.response.GridUserListResponse;
import com.shinemo.stallup.domain.response.SearchResponse;
import com.shinemo.stallup.domain.utils.DistanceUtils;
import com.shinemo.stallup.domain.utils.SubTableUtils;
import com.shinemo.sweepfloor.common.enums.HuaweiSweepFloorUrlEnum;
import com.shinemo.sweepfloor.common.error.SweepFloorErrorCodes;
import com.shinemo.sweepfloor.domain.model.HuaweiApiLogDO;
import com.shinemo.sweepfloor.domain.request.HuaweiBuildingDetailListRequest;
import com.shinemo.sweepfloor.domain.request.HuaweiBuildingRequest;
import com.shinemo.sweepfloor.domain.request.HuaweiCellAndBuildingsRequest;
import com.shinemo.sweepfloor.domain.request.HuaweiTenantRequest;
import com.shinemo.sweepfloor.domain.response.*;
import com.shinemo.wangge.core.config.StallUpConfig;
import com.shinemo.wangge.core.handler.UrlRedirectHandler;
import com.shinemo.wangge.core.service.stallup.HuaWeiService;
import com.shinemo.wangge.dal.mapper.HuaweiApiLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 华为接口服务
 *
 * @author Chenzhe Mao
 * @date 2020-04-02
 */
@Service("huaWeiService")
@Slf4j
public class HuaWeiServiceImpl implements HuaWeiService {

    private static final DecimalFormat DF = new DecimalFormat("#.0");
    private static final Function<String, Integer> string2Integer = v -> org.springframework.util.StringUtils.hasText(v) ? Integer.valueOf(v) : 0;
    private static final Function<String, Long> string2Long = v -> org.springframework.util.StringUtils.hasText(v) ? Long.valueOf(v) : 0L;

    @Resource
    private StallUpConfig stallUpConfig;

    @Resource
    private HuaweiApiLogMapper huaweiApiLogMapper;

    @Value("${smartgrid.huawei.domain}")
    public String domain;
    @Value("${smartgrid.huawei.signkey}")
    public String signkey;
    @Value("${smartgrid.huawei.aesKey}")
    public String aeskey;

    @Override
    public ApiResult<List<String>> getUserList(HuaWeiRequest request) {
        String method = HuaweiStallUpUrlEnum.GET_USER_LIST.getMethod();
        String param = SmartGridUtils.buildRequestParam(method, null, signkey);

        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + HuaweiStallUpUrlEnum.GET_USER_LIST.getUrl(), param, new HashMap<>());
        insertBigApiLog(HuaweiStallUpUrlEnum.GET_USER_LIST.getUrl(), httpResult, param, request.getMobile());
        if (httpResult != null && httpResult.success()) {
            GetUserListResult result = httpResult.getResult(GetUserListResult.class);
            if (result == null || result.getCode() != 200) {
                log.error("[getUserList] huawei getUserList error,request={},param={},httpResult = {}",
                        request, param, httpResult);
                return ApiResultWrapper.fail(StallUpErrorCodes.BASE_ERROR);
            }
            List<GetUserListResult.DataBean> data = result.getData();
            Set<String> mobileSet = new HashSet<>();
            if (data != null || data.size() > 0) {
                for (GetUserListResult.DataBean v : data) {
                    String userTel = v.getUserTel();
                    if (org.springframework.util.StringUtils.hasText(userTel)) {
                        String mobile = AESUtil.decrypt(userTel, aeskey);
                        if (mobile != null) {
                            mobileSet.add(mobile);
                        }
                    }
                }
            }
            return ApiResult.of(0, new ArrayList<>(mobileSet));
        }
        log.error("[getUserList] http error,request = {},param = {}", request, param);
        return ApiResultWrapper.fail(StallUpErrorCodes.BASE_ERROR);
    }

    @Override
    public ApiResult<SearchResponse> search(HuaWeiRequest request) {

        SearchMapRequest searchMapRequest = new SearchMapRequest();
        searchMapRequest.setCellName(request.getKeywords());
        String method = HuaweiSweepFloorUrlEnum.QUERY_CELL_LIST.getMethod();
        String param = SmartGridUtils.buildRequestParam(method, searchMapRequest, signkey);

        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + HuaweiSweepFloorUrlEnum.QUERY_CELL_LIST.getUrl(), param, new HashMap<>());

        insertApiLog(HuaweiSweepFloorUrlEnum.QUERY_CELL_LIST.getUrl(), httpResult, param, request.getMobile());
        if (httpResult != null && httpResult.success()) {
            SearchMapResult result = httpResult.getResult(SearchMapResult.class);
            if (result == null || result.getCode() != 200) {
                log.error("[search] huawei search error,request={},param={},httpResult = {}",
                        request, param, httpResult);
                return ApiResultWrapper.fail(SweepFloorErrorCodes.BASE_ERROR);
            }
            List<SearchMapResult.Cell> cells = result.getData();
            if (CollectionUtils.isEmpty(cells)) {
                return ApiResult.of(0, SearchResponse.builder().list(new ArrayList<>()).build());
            }
            List<SearchDetail> details = new ArrayList<>();
            for (SearchMapResult.Cell cell : cells) {
                SearchDetail detail = new SearchDetail();
                detail.setId(cell.getCellId());
                detail.setAddress(cell.getCellAddr());
                detail.setLocation(cell.getCenterPoint());
                detail.setName(cell.getCellName());
                details.add(detail);
            }
            SearchResponse response = new SearchResponse();
            response.setList(details.stream()
                    .map(v -> {
                        Integer distance = DistanceUtils.getDistance(request.getLocation(), v.getLocation());
                        v.setIDistance(distance);
                        if (distance >= 1000) {
                            v.setDistance(DF.format(distance / 1000.0) + "公里");
                        } else {
                            v.setDistance(((int) distance) + "米");
                        }
                        return v;
                    }).sorted()
                    .collect(Collectors.toList()));

            return ApiResult.of(0, response);
        }
        log.error("[search] http error,request = {},param = {}", request, param);
        return ApiResultWrapper.fail(SweepFloorErrorCodes.BASE_ERROR);
    }

    @Override
    public ApiResult<CommunityResponse> queryCommunity(HuaWeiRequest request) {
        QueryCommunityRequest queryCommunityRequest = new QueryCommunityRequest();
        queryCommunityRequest.setCellId(request.getCommunityId());

        String method = HuaweiStallUpUrlEnum.QUERY_CELL_LOCATION.getMethod();
        String param = SmartGridUtils.buildRequestParam(method, queryCommunityRequest, signkey);

        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + HuaweiStallUpUrlEnum.QUERY_CELL_LOCATION.getUrl(), param, new HashMap<>());
        //todo
//	    HttpResult httpResult = HttpResult.builder()
//		    .code(200)
//		    .content("{\"code\":200,\"message\":\"success\",\"page\":0,\"size\":0,\"total\":null,\"data\":{\"cellId\":null,\"cellName\":null,\"cellAddr\":null,\"centerPoint\":\"110.985554,22.918302\",\"border\":null,\"opportunityNum\":null,\"warningNum\":null,\"custGoupList\":[{\"custGroupId\":\"K0120190926051736\",\"custGroupName\":\"移动热门业务\"},{\"custGroupId\":\"G0120190710211225\",\"custGroupName\":\"移动流量业务\"},{\"custGroupId\":\"K0120190902104127\",\"custGroupName\":\"资费扩容\"},{\"custGroupId\":\"K0120191213095155\",\"custGroupName\":\"超套客户营销\"},{\"custGroupId\":\"M0120170101080000\",\"custGroupName\":\"魔百和业务_IOP模板吧\"},{\"custGroupId\":\"K0120190417080141\",\"custGroupName\":\"流量合约业务\"},{\"custGroupId\":\"K0120191231102513\",\"custGroupName\":\"资费到期续订\"},{\"custGroupId\":\"B0120170609080000\",\"custGroupName\":\"移动宽带业务\"},{\"custGroupId\":\"D0120200102030328\",\"custGroupName\":\"高危客户融合宽带\"},{\"custGroupId\":\"Y0120190507062935\",\"custGroupName\":\"语音包业务\"},{\"custGroupId\":\"K0120200103062525\",\"custGroupName\":\"5G资费合约\"},{\"custGroupId\":\"K0720170213110359\",\"custGroupName\":\"移动资费业务\"},{\"custGroupId\":\"Y0120181224071918\",\"custGroupName\":\"移动语音业务\"}]},\"pageResult\":null}")
//		    .costTime(1)
//		    .build();
        insertApiLog(HuaweiStallUpUrlEnum.QUERY_CELL_LOCATION.getUrl(), httpResult, param, request.getMobile());
        if (httpResult != null && httpResult.success()) {
            QueryCommunityResult result = httpResult.getResult(QueryCommunityResult.class);
            if (result == null || result.getCode() != 200) {
                log.error("[queryCommunity] huawei queryCommunity error,request={},param={},httpResult = {}",
                        request, param, httpResult);
                return ApiResultWrapper.fail(StallUpErrorCodes.BASE_ERROR);
            }
            QueryCommunityResult.DataBean data = result.getData();
            CommunityResponse response = CommunityResponse.builder()
                    .boundary(data.getBorder() == null ? new ArrayList<>() : Arrays.stream(data.getBorder().split(";")).collect(Collectors.toList()))
                    .centre(data.getCenterPoint())
                    .opportunity(string2Integer.apply(data.getOpportunityNum()))
                    .warning(string2Integer.apply(data.getWarningNum()))
                    .build();
            List<QueryCommunityResult.DataBean.CustGoupListBean> custGoupList = data.getCustGoupList();
            if (!CollectionUtils.isEmpty(custGoupList)) {
                response.setList(custGoupList.stream().map(v -> {
                    CustDetail custDetail = new CustDetail();
                    custDetail.setId(v.getCustGroupId());
                    custDetail.setName(v.getCustGroupName());
                    custDetail.setCount(v.getCount());
                    return custDetail;
                }).collect(Collectors.toList()));
            }
            return ApiResult.of(0, response);
        }
        log.error("[queryCommunity] http error,request = {},param = {}", request, param);
        return ApiResultWrapper.fail(StallUpErrorCodes.BASE_ERROR);
    }

    @Override
    public ApiResult<List<GridUserRoleDetail>> getGridUserInfo(HuaWeiRequest request) {

        //todo mock待删除
        //List<String> list = Arrays.asList("15958032925", "18790513853","15226536886", "15000001171", "18850583991", "13396631940", "18790513853", "13588039023","13107701611");
        //if (list.contains(request.getMobile())) {
        //    List<GridUserRoleDetail> detailList = getMockGridUser();
        //    return ApiResult.of(0, detailList);
        //}

        GetGridUserInfoRequest getGridUserInfoRequest = new GetGridUserInfoRequest();
        getGridUserInfoRequest.setUserTel(request.getMobile());
        String method = HuaweiStallUpUrlEnum.AUTH_USER.getMethod();
        String param = SmartGridUtils.buildRequestParam(method, getGridUserInfoRequest, signkey);

        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + HuaweiStallUpUrlEnum.AUTH_USER.getUrl(), param, new HashMap<>());

/*	    HttpResult httpResult = HttpResult.builder()
		    .code(200)
		    .content("{\"code\":200,\"message\":\"success\",\"data\":{\"isAuthSuccess\":\"true\",\"userId\":\"luzhong\",\"userName\":\"陆忠\",\"gridList\":[{\"areaCode\":\"771_A2107_XX\",\"areaName\":\"陆忠网格1\",\"roleList\":[{\"id\":1,\"name\":\"网格长\"},{\"id\":2,\"name\":\"网格经理\"}]},{\"areaCode\":\"771_A2107_XX\",\"areaName\":\"陆忠网格2\",\"roleList\":[{\"id\":1,\"name\":\"网格长\"},{\"id\":2,\"name\":\"网格经理\"}]}]}}")
		    .costTime(1)
		    .build();*/

        insertApiLog(HuaweiStallUpUrlEnum.AUTH_USER.getUrl(), httpResult, param, request.getMobile());
        if (httpResult != null && httpResult.success()) {
            GetGridUserInfoResult result = httpResult.getResult(GetGridUserInfoResult.class);
            if (result == null || result.getCode() != 200) {
                log.error("[getGridUserInfo] huawei getGridUserInfo error,request={},param={},httpResult = {}",
                        request, param, httpResult);
                return ApiResultWrapper.fail(StallUpErrorCodes.BASE_ERROR);
            }
            GetGridUserInfoResult.DataBean data = result.getData();
            List<GridUserRoleDetail> detailList = new ArrayList<>();
            boolean authSuccess = Boolean.parseBoolean(data.getIsAuthSuccess());
            if (!authSuccess) {
                log.error("[getGridUserInfo] user is not in grid, request:{}", request);
                throw new ApiException(StallUpErrorCodes.GRID_ERROR);
            }
            List<GetGridUserInfoResult.DataBean.GridListBean> gridList = data.getGridList();
            if (!CollectionUtils.isEmpty(gridList)) {
                gridList.forEach(v -> {
                    GridUserRoleDetail detail = new GridUserRoleDetail();
                    detail.setId(v.getAreaCode());
                    detail.setName(v.getAreaName());
                    detail.setCityCode(v.getCityCode());
                    detail.setCityName(v.getCityName());
                    detail.setCountyName(v.getCountyName());
                    detail.setCountyCode(v.getCountyCode());
                    List<GetGridUserInfoResult.DataBean.GridListBean.RoleListBean> roleList = v.getRoleList();
                    if (!CollectionUtils.isEmpty(roleList)) {
                        detail.setRoleList(roleList.stream().map(v1 -> {
                            GridUserRoleDetail.GridRole role = new GridUserRoleDetail.GridRole();
                            role.setId(v1.getId());
                            role.setName(v1.getName());
                            return role;
                        }).collect(Collectors.toList()));
                    }
                    detailList.add(detail);
                });
            }
            return ApiResult.of(0, detailList);
        }

        log.error("[getGridUserInfo] http error,request = {},param = {}", request, param);
        return ApiResultWrapper.fail(StallUpErrorCodes.BASE_ERROR);
    }

    private List<GridUserRoleDetail> getMockGridUser() {

        return Arrays.asList(
                GridUserRoleDetail.builder()
                        .id("shinemo1")
                        .name("测试网格1")
                        .cityCode("hangzhou")
                        .cityName("杭州")
                        .countyCode("xihu")
                        .countyName("西湖")
                        .roleList(Lists.newArrayList(
                        		new GridUserRoleDetail.GridRole("1", "网格长")))
										.build(),
				GridUserRoleDetail.builder()
						.id("shinemo2")
						.name("测试网格2")
						.cityCode("hangzhou")
						.cityName("杭州")
						.countyCode("yuhang")
						.countyName("余杭")
                        .roleList(Lists.newArrayList(
                                new GridUserRoleDetail.GridRole("2", "网格经理")))
                        .build()
        );
    }

    @Override
    public ApiResult<GetGridUserInfoResult.DataBean> getGridUserInfoDetail(HuaWeiRequest request) {
        GetGridUserInfoRequest getGridUserInfoRequest = new GetGridUserInfoRequest();
        getGridUserInfoRequest.setUserTel(request.getMobile());
        String method = HuaweiStallUpUrlEnum.AUTH_USER.getMethod();
        String param = SmartGridUtils.buildRequestParam(method, getGridUserInfoRequest, signkey);

        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + HuaweiStallUpUrlEnum.AUTH_USER.getUrl(), param, new HashMap<>());

/*	    HttpResult httpResult = HttpResult.builder()
		    .code(200)
		    .content("{\"code\":200,\"message\":\"success\",\"data\":{\"isAuthSuccess\":\"true\",\"userId\":\"luzhong\",\"userName\":\"陆忠\",\"gridList\":[{\"areaCode\":\"771_A2107_XX\",\"areaName\":\"陆忠网格1\",\"roleList\":[{\"id\":1,\"name\":\"网格长\"},{\"id\":2,\"name\":\"网格经理\"}]},{\"areaCode\":\"771_A2107_XX\",\"areaName\":\"陆忠网格2\",\"roleList\":[{\"id\":1,\"name\":\"网格长\"},{\"id\":2,\"name\":\"网格经理\"}]}]}}")
		    .costTime(1)
		    .build();*/

        insertApiLog(HuaweiStallUpUrlEnum.AUTH_USER.getUrl(), httpResult, param, request.getMobile());
        if (httpResult != null && httpResult.success()) {
            GetGridUserInfoResult result = httpResult.getResult(GetGridUserInfoResult.class);
            if (result == null || result.getCode() != 200) {
                log.error("[getGridUserInfo] huawei getGridUserInfo error,request={},param={},httpResult = {}",
                        request, param, httpResult);
                return ApiResultWrapper.fail(StallUpErrorCodes.BASE_ERROR);
            }
            GetGridUserInfoResult.DataBean data = result.getData();
            return ApiResult.of(0, data);
        }

        log.error("[getGridUserInfo] http error,request = {},param = {}", request, param);
        return ApiResultWrapper.fail(StallUpErrorCodes.BASE_ERROR);
    }

    @Override
    public ApiResult<GridUserListResponse> getGridUserList(HuaWeiRequest request) {
        GetGridUserListRequest getGridUserListRequest = new GetGridUserListRequest();
        getGridUserListRequest.setGridId(request.getGridId());

        String method = HuaweiStallUpUrlEnum.QUERY_BELONG_GRID_USER.getMethod();
        String param = SmartGridUtils.buildRequestParam(method, getGridUserListRequest, signkey);

        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + HuaweiStallUpUrlEnum.QUERY_BELONG_GRID_USER.getUrl(), param, new HashMap<>());
        //todo mock
/*	    HttpResult httpResult = HttpResult.builder()
		    .code(200)
		    .content("{\"code\":200,\"message\":\"success\",\"page\":0,\"size\":0,\"total\":null,\"data\":{\"userList\":[{\"userName\":\"测试15958032925\",\"userRole\":\"网格经理\",\"userTel\":\"303c144963b434ab098c97655d94f846aa5de19622ad9fadbc04c4c61c6ae203b75bb707f10870\"},{\"userName\":\"测试15226536886\",\"userRole\":\"网格经理\",\"userTel\":\"9140ff69b5c5b9d915a2fad6e4b7541261078c19f977dc30e145707c2daf3720d814144954c17d\"},{\"userName\":\"测试15000001170\",\"userRole\":\"网格经理\",\"userTel\":\"6140b4ff9b010aec4d3221c43b8b532c7bc8fb54a66ec73277de08643fe48b4fd8357ca3856d2c\"},{\"userName\":\"测试15000001171\",\"userRole\":\"网格经理\",\"userTel\":\"8c0e56a4f02cdeb9ed31af7529ea85047f7ef546de113880af4c1dc23fc7cd9bbbf5d2c313b272\"},{\"userName\":\"测试15000001172\",\"userRole\":\"网格经理\",\"userTel\":\"0c3b2aa05df1fbb9e2ea89abee71bf7eb31e3246454e756afb3f05e78434379881a34e613eb3ae\"},{\"userName\":\"测试15000001173\",\"userRole\":\"网格经理\",\"userTel\":\"8c949c9375995c2942186e7a55681e5e75a658c921872e21d7fe8a82725020b3eda2ae23cab329\"},{\"userName\":\"测试15000001175\",\"userRole\":\"网格经理\",\"userTel\":\"fc305a18a0069f45cffaa201acb16cbc7a81b755a0fb4fe08b63ff4398ea93094efbd4f338c982\"},{\"userName\":\"测试15000001176\",\"userRole\":\"网格经理\",\"userTel\":\"fd37ccb1fc05ba6085ee87cf91cf3643eefd27de3731772c18854d7649d4f91d248b732f9b5fa1\"}],\"pageResult\":null}}")
		    .costTime(1)
		    .build();*/

        insertApiLog(HuaweiStallUpUrlEnum.QUERY_BELONG_GRID_USER.getUrl(), httpResult, param, request.getMobile());
        if (httpResult != null && httpResult.success()) {
            GetGridUserListResult result = httpResult.getResult(GetGridUserListResult.class);
            if (result == null || result.getCode() != 200) {
                log.error("[getGridUserList] huawei getGridUserList error,request={},param={},httpResult = {}",
                        request, param, httpResult);
                return ApiResultWrapper.fail(StallUpErrorCodes.BASE_ERROR);
            }
            List<GetGridUserListResult.DataBean.UserListBean> list = Optional.ofNullable(result)
                    .map(v -> v.getData())
                    .map(v -> v.getUserList())
                    .orElse(null);
            GridUserListResponse response = new GridUserListResponse();
            Map<String, GridUserDetail> gridUserDetailMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(list)) {
                list.forEach(v -> {
                    String userTel = v.getUserTel();
                    String mobile = AESUtil.decrypt(userTel, aeskey);
                    GridUserDetail gridUserDetail = gridUserDetailMap.get(mobile);
                    if (gridUserDetail == null) {
                        GridUserDetail detail = GridUserDetail.builder()
                                .seMobile(userTel)
                                .name(v.getUserName())
                                .role(v.getUserRole())
                                .order(Integer.MAX_VALUE)
                                .build();

                        if (request.getMobile().equals(mobile)) {
                            detail.setOrder(Integer.MIN_VALUE);
                            //todo
                            detail.setName(detail.getName());
                        }
                        gridUserDetailMap.put(mobile, detail);
                    } else {
                        gridUserDetail.setRole(gridUserDetail.getRole() + "、" + v.getUserRole());
                    }
                });
            }
            if (gridUserDetailMap.get(request.getMobile()) == null) {
                gridUserDetailMap.put(
                        request.getMobile(),
                        GridUserDetail.builder().seMobile(AESUtil.encrypt(request.getMobile(), aeskey)).name(SmartGridContext.getUserName()).order(Integer.MIN_VALUE).role("").build()
                );
            }
            response.setGetGridUserList(
                    gridUserDetailMap.values().stream().sorted(Comparator.comparing(GridUserDetail::getOrder)).collect(Collectors.toList())
            );
            return ApiResult.of(0, response);
        }
        log.error("[getGridUserList] http error,request = {},param = {}", request, param);
        return ApiResultWrapper.fail(StallUpErrorCodes.BASE_ERROR);
    }

    @Override
    public ApiResult<GetCustDetailResponse> getCustDetail(HuaWeiRequest request) {
        GetCustDetailRequest getCustDetailRequest = new GetCustDetailRequest();
        getCustDetailRequest.setCellId(request.getCommunityId());
        getCustDetailRequest.setCustGroupIdList(request.getCustIdList());
        getCustDetailRequest.setPage(request.getPage());
        getCustDetailRequest.setPageSize(request.getPageSize());
        String method = HuaweiStallUpUrlEnum.QUERY_CELL_CUST_GROUP_DETAIL.getMethod();
        String param = SmartGridUtils.buildRequestParam(method, getCustDetailRequest, signkey);

        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + HuaweiStallUpUrlEnum.QUERY_CELL_CUST_GROUP_DETAIL.getUrl(), param, new HashMap<>());
        //todo
//	    HttpResult httpResult = HttpResult.builder()
//		    .code(200)
//		    .content("{\"code\":200,\"message\":\"success\",\"data\":{\"page\":1,\"size\":100,\"total\":32,\"pageResult\":[{\"custTel\":\"fa98792d8584067903ce6375aa3fd6c11b566368a51ec372c536017834ba0882dec0ca9d75bc79\",\"custName\":null},{\"custTel\":\"d022eab6e95baf0c381c136e6b2f218651c76cd06e7f392fd7c8b9c3e1b832de207378e5c4b53d\",\"custName\":null},{\"custTel\":\"34e66d914a44fe227e476fa01504c1119e9fe49ed5ccd1dffc5fd1aca918aec85a5524fecdfa68\",\"custName\":null},{\"custTel\":\"74f542e635571772819e3acc5dffc1d03b149dd4acb42607e0e2742fece9fa26b3d4e3cf381424\",\"custName\":null},{\"custTel\":\"6bc101e3906e5a9d8215b8ceb0429f297e9d45480a6297b295d2d8f4660b9e0757c70ad67b8c16\",\"custName\":null},{\"custTel\":\"2cb472589cee211cfbff1cdeadb6e18ce1e81f91f69be7fff7f2321fd44a005bb4444e3de7363e\",\"custName\":null},{\"custTel\":\"9cd9419b09beea031ff0ebdcd91950f7c0927e3afba2e07a76fd61b31d714e4de2b94d397c05ba\",\"custName\":null},{\"custTel\":\"8a858b9f4cef0d2ed5fd1421bc7e8025750261f9d533e51b08956aba027c50a2c6ec9c57a006d2\",\"custName\":null},{\"custTel\":\"12089979f1a612d528f108c9edb0f4d707dfe0a070f908c0913a5a20bad8fef3fff13f7578f7d4\",\"custName\":null},{\"custTel\":\"c7ab3593118111d161636a4dd474cc0091af7f438f0521fdb515886744bb360c0808ec56729fb2\",\"custName\":null},{\"custTel\":\"fb3d248307987a3531636d3d4d9d3a8cb313458116b59316201059be4a059f256f5606bea55f4c\",\"custName\":null},{\"custTel\":\"6113d9aaaa12c2a0123c708b24da474d9e510c0d68ce33d6c714be6d0a4fbb2b1ea97ecbd94b12\",\"custName\":null},{\"custTel\":\"e5ea5950b07cf84995d8141b55c2fbde3ef2dac5abb20b9a625632af9030d77dc45472e73ea2e9\",\"custName\":null},{\"custTel\":\"ae7bf4c01fb844f35af45ae7eb41210036d04e227d69294ce88d127f3844d072cb48a6bdddcb9a\",\"custName\":null},{\"custTel\":\"4792abb7b7f0723513a88851f563ea9783506db2248e2cabedb3e6c8b8aa844f5dbae1d2d4ec09\",\"custName\":null},{\"custTel\":\"fdf2ee0655347bec8e760b16587fb730eed55f50f93c84344b438cae88bd20e726ac4ca9c3b7b2\",\"custName\":null},{\"custTel\":\"9959a2daf0c1031ef194cb5602f27b102386a14fc7b39f2a22e9222f2329e43cb6e2fc9d79b9ba\",\"custName\":null},{\"custTel\":\"1e9771f3cb194034b106b58de974cd88ee59e7291b736c3860f0abcaa32a0a845a03fe72648428\",\"custName\":null},{\"custTel\":\"b5103291344ef2d5bdc2cb70ad706cd708dce10a82540f37e96aaa43ab52bcb15eedf48811d06e\",\"custName\":null},{\"custTel\":\"440722a99b5e0459023a00a4255b0fcad2997794217332f96b67de00012d8e12b9b4259287063a\",\"custName\":null},{\"custTel\":\"248624fa63afa899641f26bb1c70a35b6084c267f5c7dc0dc4d311286d8e96da3920f2bb07ecfb\",\"custName\":null},{\"custTel\":\"a8f818575cd61283513ad088b2e5d51a15c298d27d2980650dde1d5e96f22e646ea499aa8a7e5a\",\"custName\":null},{\"custTel\":\"52aea4f99dfa8465d7ff2d53708014c55a3b1e6c9987d4d6bfdf4ced37254272b89d04898c4925\",\"custName\":null},{\"custTel\":\"569294cfd8e097cd49d861e9995e99c0dc383658c6484b05ea9d8921d28590866e1dca59d5e8e4\",\"custName\":null},{\"custTel\":\"9c7b588b11297fd163e933336365565c23bc4a3733e9dcf2b550dd597f97f7f319e551e5f940ea\",\"custName\":null},{\"custTel\":\"fb169b21b5f55f43729e8183a6ef2d5f67855f1394d6a191a7478958a75de8f8134733dadac441\",\"custName\":null},{\"custTel\":\"b7ce9cbec17e00b92afefc90ac066d0df631d50970f7ebc8a943081f1e4916fc00a2e02a854988\",\"custName\":null},{\"custTel\":\"5bae3f3fd672af7b89470eb1eb51a5256e12525240ee281f3318e0c6b841daffc92a6fbc070d28\",\"custName\":null},{\"custTel\":\"cdd718059170c3bc74ee51a6826cc71cae172305dd1a0bc2ccedb67609f782071d93a5bed805dc\",\"custName\":null},{\"custTel\":\"2d52cf4c509047720b1a3b696b9aad25ad552b712e5973934ee7328f3b3dc6f549855e3ea6741d\",\"custName\":null},{\"custTel\":\"9d72ea65e881f94c986f020385df349f641ac0f690b7ab2e6c24c44e2c4f6f6de76cb31eafe125\",\"custName\":null},{\"custTel\":\"6eddb6b7e502ca2371886ef0c7674c7658e07c0fee346555626e5eb36bdee9de63268b95617086\",\"custName\":null}]}}")
//		    .costTime(1)
//		    .build();

        insertApiLog(HuaweiStallUpUrlEnum.QUERY_CELL_CUST_GROUP_DETAIL.getUrl(), httpResult, param, request.getMobile());
        if (httpResult != null && httpResult.success()) {
            GetCustDetailResult result = httpResult.getResult(GetCustDetailResult.class);
            if (result == null || result.getCode() != 200) {
                log.error("[getCustDetail] huawei getCustDetail error,request={},param={},httpResult = {}",
                        request, param, httpResult);
                return ApiResultWrapper.fail(StallUpErrorCodes.BASE_ERROR);
            }

            GetCustDetailResponse response = new GetCustDetailResponse();
            GetCustDetailResult.DataBean data = result.getData();
            response.setTotal(data.getTotal());
            List<GetCustDetailResult.DataBean.PageResultBean> pageResult = data.getPageResult();
            if (!CollectionUtils.isEmpty(pageResult)) {
                response.setCustList(pageResult.stream().map(v -> {
                    CustUserDetail detail = new CustUserDetail();
                    String seMobile = v.getCustTel();
                    String mobile = AESUtil.decrypt(seMobile, aeskey);
                    detail.setName(org.springframework.util.StringUtils.hasText(v.getCustName()) ? v.getCustName().substring(0, 1) + "**" : "***");
                    detail.setMobile(mobile.substring(0, 3) + "****" + mobile.substring(7, 11));
                    detail.setSeMobile(seMobile);
                    return detail;
                }).collect(Collectors.toList()));
            }
            return ApiResult.of(0, response);
        }
        log.error("[getCustDetail] http error,request = {},param = {}", request, param);
        return ApiResultWrapper.fail(StallUpErrorCodes.BASE_ERROR);
    }

    @Override
    public ApiResult<String> getRedirectUrl(UrlRedirectHandlerRequest request) {
        StallUpConfig.ConfigDetail config = stallUpConfig.getConfig();
        Map<Long, StallUpBizType> map = config.getMap();
        Map<Integer, UrlRedirectHandler> urlMap = config.getUrlMap();
        Long id = request.getId();
        StallUpBizType stallUpBizType = map.get(id);
        if (stallUpBizType == null) {
            log.error("[getRedirectUrl] stallUpBizType is null, id:{}", id);
            throw new ApiException(StallUpErrorCodes.BIZ_TYPE_ERROR);
        }
        UrlRedirectHandler handler = urlMap.get(stallUpBizType.getType());
        if (handler == null) {
            log.error("[getRedirectUrl] handler is null, type:{}", stallUpBizType.getType());
            throw new ApiException(StallUpErrorCodes.BIZ_TYPE_ERROR);
        }
        String seMobile = request.getQueryMobile();
        if (org.springframework.util.StringUtils.hasText(seMobile)) {
            String queryMobile = AESUtil.decrypt(seMobile, aeskey);
            request.setQueryMobile(queryMobile);
        }
        request.setBizParams(stallUpBizType.getBizParams());
        request.setUrl(stallUpBizType.getUrl());
        String returnUrl = handler.getUrl(request);
        log.info("[getRedirectUrl] request:{}, bizType:{}, url:{}", request, stallUpBizType, returnUrl);
        return ApiResult.of(0, returnUrl);
    }

    @Override
    public ApiResult<List<HuaweiCellsAndBuildingsResponse>> getCellsAndBuildings(HuaweiCellAndBuildingsRequest request) {
        String method = HuaweiSweepFloorUrlEnum.QUERY_BUILDING_LIST.getMethod();
        String param = SmartGridUtils.buildRequestParam(method, request, signkey);
        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + HuaweiSweepFloorUrlEnum.QUERY_BUILDING_LIST.getUrl(), param, new HashMap<>());

        insertBigApiLog(HuaweiSweepFloorUrlEnum.QUERY_BUILDING_LIST.getUrl(), httpResult, param, SmartGridContext.getMobile());
        if (httpResult != null && httpResult.success()) {
            HuaweiCellsAndBuildingsResponseWrapper huaweiResponse = httpResult.getResult(HuaweiCellsAndBuildingsResponseWrapper.class);
            if (huaweiResponse == null || huaweiResponse.getCode() != 200) {
                log.error("[getCellsAndBuildings] huawei getCellsAndBuildings error,request={},param={},httpResult = {}",
                        request, param, httpResult);
                return ApiResultWrapper.fail(SweepFloorErrorCodes.BASE_ERROR);
            }
            List<HuaweiCellsAndBuildingsResponse> cellBuildingList = huaweiResponse.getData().getCellBuildingList();
            if (CollectionUtils.isEmpty(cellBuildingList)) {
                return ApiResult.of(0, new ArrayList<>());
            }
            return ApiResult.of(0, cellBuildingList);
        }
        log.error("[getCellsAndBuildings] http error,request = {},param = {}", request, param);
        return ApiResultWrapper.fail(SweepFloorErrorCodes.BASE_ERROR);
    }

    @Override
    public ApiResult<Void> addBuilding(HuaweiBuildingRequest request) {
        String method = HuaweiSweepFloorUrlEnum.ADD_BUILDING.getMethod();
        String param = SmartGridUtils.buildRequestParam(method, request, signkey);
        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + HuaweiSweepFloorUrlEnum.ADD_BUILDING.getUrl(), param, new HashMap<>());

        //todo手机号记录
        insertApiLog(HuaweiSweepFloorUrlEnum.ADD_BUILDING.getUrl(), httpResult, param, SmartGridContext.getMobile());
        if (httpResult != null && httpResult.success()) {
            CommonHuaweiResponse huaweiResponse = httpResult.getResult(CommonHuaweiResponse.class);
            if (huaweiResponse != null && huaweiResponse.getCode() == 301) {
                log.error("[addBuilding] buildingName duplicate,request={},param={},httpResult = {}",
                        request, param, httpResult);
                return ApiResultWrapper.fail(SweepFloorErrorCodes.SWEEP_FLOOR_BUILDING_NAME_DUPLICATE);
            }
            if (huaweiResponse == null || huaweiResponse.getCode() != 200) {
                log.error("[addBuilding] huawei addBuiling error,request={},param={},httpResult = {}",
                        request, param, httpResult);
                return ApiResultWrapper.fail(SweepFloorErrorCodes.BASE_ERROR);
            }
            return ApiResult.success();
        }
        log.error("[addBuilding] http error,request = {},param = {}", request, param);
        return ApiResultWrapper.fail(SweepFloorErrorCodes.BASE_ERROR);

    }

    @Override
    public ApiResult<Void> updateBuilding(HuaweiBuildingRequest request) {
        String method = HuaweiSweepFloorUrlEnum.EDIT_BUILDING.getMethod();
        String param = SmartGridUtils.buildRequestParam(method, request, signkey);
        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + HuaweiSweepFloorUrlEnum.EDIT_BUILDING.getUrl(), param, new HashMap<>());

        //todo手机号记录
        insertApiLog(HuaweiSweepFloorUrlEnum.EDIT_BUILDING.getUrl(), httpResult, param, SmartGridContext.getMobile());
        if (httpResult != null && httpResult.success()) {
            CommonHuaweiResponse huaweiResponse = httpResult.getResult(CommonHuaweiResponse.class);

            if (huaweiResponse != null && huaweiResponse.getCode() == 301) {
                log.error("[updateBuilding] buildingName duplicate,request={},param={},httpResult = {}",
                        request, param, httpResult);
                return ApiResultWrapper.fail(SweepFloorErrorCodes.SWEEP_FLOOR_BUILDING_NAME_DUPLICATE);
            }

            if (huaweiResponse == null || huaweiResponse.getCode() != 200) {
                log.error("[updateBuilding] huawei updateBuilding error,request={},param={},httpResult = {}",
                        request, param, httpResult);
                return ApiResultWrapper.fail(SweepFloorErrorCodes.BASE_ERROR);
            }
            return ApiResult.success();
        }
        log.error("[updateBuilding] http error,request = {},param = {}", request, param);
        return ApiResultWrapper.fail(SweepFloorErrorCodes.BASE_ERROR);
    }

    @Override
    public ApiResult<Void> addBuildingTenants(HuaweiTenantRequest request) {
        String method = HuaweiSweepFloorUrlEnum.ADD_BUILDING_TENANTS.getMethod();
        String param = SmartGridUtils.buildRequestParam(method, request, signkey);
        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + HuaweiSweepFloorUrlEnum.ADD_BUILDING_TENANTS.getUrl(), param, new HashMap<>());

        //todo手机号记录
        insertApiLog(HuaweiSweepFloorUrlEnum.ADD_BUILDING_TENANTS.getUrl(), httpResult, param, SmartGridContext.getMobile());
        if (httpResult != null && httpResult.success()) {
            CommonHuaweiResponse huaweiResponse = httpResult.getResult(CommonHuaweiResponse.class);

            if (huaweiResponse != null && huaweiResponse.getCode() == 302) {
                log.error("[addBuildingTenants] houseNumber duplicate,request={},param={},httpResult = {}",
                        request, param, httpResult);
                return ApiResultWrapper.fail(SweepFloorErrorCodes.SWEEP_FLOOR_HOUSE_NUMBER_DUPLICATE);
            }

            if (huaweiResponse == null || huaweiResponse.getCode() != 200) {
                log.error("[addBuildingTenants] huawei addBuildingTenants error,request={},param={},httpResult = {}",
                        request, param, httpResult);
                return ApiResultWrapper.fail(SweepFloorErrorCodes.BASE_ERROR);
            }
            return ApiResult.success();
        }
        log.error("[addBuildingTenants] http error,request = {},param = {}", request, param);
        return ApiResultWrapper.fail(SweepFloorErrorCodes.BASE_ERROR);
    }

    @Override
    public ApiResult<Void> updateBuildingTenants(HuaweiTenantRequest request) {
        String method = HuaweiSweepFloorUrlEnum.EDIT_BUILDING_TENANTS.getMethod();
        String param = SmartGridUtils.buildRequestParam(method, request, signkey);
        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + HuaweiSweepFloorUrlEnum.EDIT_BUILDING_TENANTS.getUrl(), param, new HashMap<>());

        //todo手机号记录
        insertApiLog(HuaweiSweepFloorUrlEnum.EDIT_BUILDING_TENANTS.getUrl(), httpResult, param, SmartGridContext.getMobile());
        if (httpResult != null && httpResult.success()) {
            CommonHuaweiResponse huaweiResponse = httpResult.getResult(CommonHuaweiResponse.class);

            if (huaweiResponse != null && huaweiResponse.getCode() == 302) {
                log.error("[updateBuildingTenants] houseNumber duplicate,request={},param={},httpResult = {}",
                        request, param, httpResult);
                return ApiResultWrapper.fail(SweepFloorErrorCodes.SWEEP_FLOOR_HOUSE_NUMBER_DUPLICATE);
            }

            if (huaweiResponse == null || huaweiResponse.getCode() != 200) {
                log.error("[updateBuildingTenants] huawei updateBuildingTenants error,request={},param={},httpResult = {}",
                        request, param, httpResult);
                return ApiResultWrapper.fail(SweepFloorErrorCodes.BASE_ERROR);
            }
            return ApiResult.success();
        }
        log.error("[updateBuildingTenants] http error,request = {},param = {}", request, param);
        return ApiResultWrapper.fail(SweepFloorErrorCodes.BASE_ERROR);
    }

    @Override
    public ApiResult<List<HuaweiBuildingDetailsListResponse>> queryBuildingDetailList(HuaweiBuildingDetailListRequest request) {
        String method = HuaweiSweepFloorUrlEnum.QUERY_BUILDING_DETAIL_LIST.getMethod();
        String param = SmartGridUtils.buildRequestParam(method, request, signkey);
        HttpResult httpResult = HttpConnectionUtils.httpPost(domain + HuaweiSweepFloorUrlEnum.QUERY_BUILDING_DETAIL_LIST.getUrl(), param, new HashMap<>());

        insertBigApiLog(HuaweiSweepFloorUrlEnum.QUERY_BUILDING_DETAIL_LIST.getUrl(), httpResult, param, SmartGridContext.getMobile());

        if (httpResult != null && httpResult.success()) {
            HuaweiBuildingDetailsListResponseWrapper responseWrapper = httpResult.getResult(HuaweiBuildingDetailsListResponseWrapper.class);
            if (responseWrapper == null || responseWrapper.getCode() != 200) {
                log.error("[queryBuildingDetailList] huawei queryBuildingDetailList error,request={},param={},httpResult = {}",
                        request, param, httpResult);
                return ApiResultWrapper.fail(SweepFloorErrorCodes.BASE_ERROR);
            }
            HuaweiBuildingDetailsListResponseWrapper.HuaweiBuildingDetailsList data = responseWrapper.getData();
            if (data == null) {
                return ApiResult.of(0, new ArrayList<>());
            }
            List<HuaweiBuildingDetailsListResponse> buildingList = data.getBuildingList();

            if (CollectionUtils.isEmpty(buildingList)) {
                return ApiResult.of(0, new ArrayList<>());
            }
            return ApiResult.of(0, buildingList);
        }
        log.error("[queryBuildingDetailList] http error,request = {},param = {}", request, param);
        return ApiResultWrapper.fail(SweepFloorErrorCodes.BASE_ERROR);
    }


    /**
     * 记录调用日志
     *
     * @param url
     * @param httpResult
     * @param requestJson
     * @param mobile
     */
    private void insertApiLog(String url, HttpResult httpResult, String requestJson, String mobile) {
        ThreadPoolUtil.getApiLogPool().execute(() -> {
            try {
                HuaweiApiLogDO apiLogDO = new HuaweiApiLogDO();
                apiLogDO.setTableIndex(SubTableUtils.getTableIndexByMonth());
                apiLogDO.setRequest(requestJson);
                apiLogDO.setMobile(mobile);
                apiLogDO.setUrl(url);
                if (httpResult != null) {
                    apiLogDO.setCostTime(httpResult.getCostTime());
                    apiLogDO.setStatus(httpResult.getCode());
                    if (!StringUtils.isBlank(httpResult.getContent())) {
                        apiLogDO.setResponse(httpResult.getContent());
                    }
                }
                huaweiApiLogMapper.insert(apiLogDO);
            } catch (Exception e) {
                log.error("api log insert error, e:", e);
            }
        });

    }

    /**
     * 记录较大的调用日志
     *
     * @param url
     * @param httpResult
     * @param requestJson
     * @param mobile
     */
    private void insertBigApiLog(String url, HttpResult httpResult, String requestJson, String mobile) {
        HttpResult newHttpResult = new HttpResult();
        BeanUtils.copyProperties(httpResult, newHttpResult);
        newHttpResult.setContent(null);
        insertApiLog(url, newHttpResult, requestJson, mobile);
    }

}
