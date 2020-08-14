package com.shinemo.wangge.core.service.sweepstreet;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.sweepstreet.domain.model.SweepStreetVisitRecordingDO;
import com.shinemo.sweepstreet.domain.query.SweepStreetVisitRecordingQuery;
import com.shinemo.sweepstreet.domain.vo.SweepStreetBusinessVO;
import com.shinemo.sweepstreet.domain.vo.SweepStreetVisitRecordingVO;

import java.util.List;

/**
 * @author ChenQinHai
 * @date 2020/8/13
 */
public interface VisitStreetService {
    /**
     * 新增走访记录
     * @param request
     * @return
     */
    ApiResult<Void> add(SweepStreetVisitRecordingVO request);

    /**
     * 根据商户id或活动id查询走访记录
     * @param query
     * @return
     */
    ApiResult<List<SweepStreetVisitRecordingVO>> getVisitStreetByMerchantIdOrActivityId(SweepStreetVisitRecordingQuery query);


    /**
     * 查询走访详情
     * @param id
     * @return
     */
    ApiResult<SweepStreetVisitRecordingVO> getVisitStreetDetail(Long id);

    /**
     * 获取商户业务详情
     * @param merchantId
     * @return
     */
    ApiResult<SweepStreetBusinessVO> getBusinessDetail(String merchantId);
}
