package com.shinemo.wangge.core.service.sweepvillage;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.sweepvillage.domain.vo.SweepVillageVisitRecordingVO;

public interface VisitRecordingService {

    ApiResult<Void> addVisitRecording(SweepVillageVisitRecordingVO request);
}
