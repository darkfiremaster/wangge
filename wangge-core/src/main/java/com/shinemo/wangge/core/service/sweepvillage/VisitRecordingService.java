package com.shinemo.wangge.core.service.sweepvillage;

import com.shinemo.client.common.ListVO;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.sweepvillage.domain.request.VisitRecordingListRequest;
import com.shinemo.sweepvillage.domain.vo.SweepVillageVisitRecordingVO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface VisitRecordingService {

    ApiResult<Void> add(SweepVillageVisitRecordingVO request);

    ApiResult<Void> update(SweepVillageVisitRecordingVO request);

    ApiResult<List<SweepVillageVisitRecordingVO>> getVisitRecordingByTenantsId(VisitRecordingListRequest request);

    ApiResult<List<SweepVillageVisitRecordingVO>> getVisitRecordingByActivityId( VisitRecordingListRequest request);

    ApiResult<SweepVillageVisitRecordingVO> getVisitRecordingDetail(Long id);

}
