package com.shinemo.wangge.core.service.sweepfloor;

import com.shinemo.client.common.ListVO;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.sweepfloor.domain.query.SignRecordQuery;
import com.shinemo.sweepfloor.domain.query.SweepFloorBuildingQuery;
import com.shinemo.sweepfloor.domain.query.UpdateSweepFloorStatusQuery;
import com.shinemo.sweepfloor.domain.response.HuaWeiAddTenantsResponse;
import com.shinemo.sweepfloor.domain.response.IndexInfoResponse;
import com.shinemo.sweepfloor.domain.vo.*;

import java.util.Date;
import java.util.List;

public interface SweepFloorService {

    ApiResult<Long> create(SweepFloorActivityVO request);

    ApiResult<Void> updateSweepFloorStatus(UpdateSweepFloorStatusQuery request);

    ApiResult<SweepFloorBusinessCountAndHouseCountVO> getBusinessCountAndHouseCount(Integer type);

    ApiResult<Void> enterMarketingNumber(SweepFloorMarketingNumberVO request);

    ApiResult<SweepFloorMarketingNumberVO> getMarketingNumber(Long activityId);

    ApiResult<Void> enterVisitRecording(SweepFloorVisitRecordingVO request);

    ApiResult<List<SweepFloorVisitRecordingVO>> getVisitRecording(String houseId, Long activityId);

    ApiResult<List<SweepFloorVisitRecordingVO>> getVisitRecordingByActivityId(Long activityId);

    ApiResult<Void> satrtSign(SignRecordQuery request);

    ApiResult<Void> endSign(SignRecordQuery request);

    ApiResult<ListVO<SweepFloorActivityVO>> getSweepFloorActivity(Integer status, Integer pageSize,
                                                                  Integer currentPage, Long satrtTime, Long endTime);

    ApiResult<List<BuildingVO>> getBuildings(SweepFloorBuildingQuery request);

    ApiResult<Void> addBuilding(BuildingVO request);

    ApiResult<Void> updateBuilding(BuildingVO request);

    ApiResult<Void> addHousehold(HouseholdVO request);

    ApiResult<Void> updateHousehold(HouseholdVO request);

    ApiResult<List<BuildingDetailsVO>> getBuildingPlan(SweepFloorBuildingQuery request);

    ApiResult<List<HouseholdVO>> getBuildingHouseholds(SweepFloorBuildingQuery request);

    ApiResult<IndexInfoResponse> getIndexInfo(long uid);

    ApiResult<List<String>> queryBizType();

    ApiResult<List<FamilyMember>> getFamilyMembers(String buildingId, String unitId, String houseNumber);

    /**
     * 后门刷新SmartGridActivity表数据
     * @param mobile
     */
    void refreshSmartGridActivity(String mobile);

    ApiResult<ListVO<SmartGridVO>> getSweepFloorActivityByGridIds(List<String> gridIds, boolean page, Integer pageSize, Integer currentPage);


    ApiResult<ListVO<SweepFloorActivityVO>> getOutsideActivityList(Integer status, Integer pageSize, Integer currentPage, Long startTime,
                                                                   Long endTime,
                                                                   String mobile,
                                                                   String gridId);

    ApiResult<SweepFloorActivityVO> getOutsideActivityDetail(Long id);

}
