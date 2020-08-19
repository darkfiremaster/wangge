package com.shinemo.wangge.core.service.sweepstreet;

import com.shinemo.client.common.ListVO;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.sweepstreet.domain.model.SweepStreetActivityDO;
import com.shinemo.sweepstreet.domain.request.HuaweiMerchantRequest;
import com.shinemo.sweepstreet.domain.request.SweepStreetActivityRequest;
import com.shinemo.sweepstreet.domain.request.SweepStreetListRequest;
import com.shinemo.sweepstreet.domain.request.SweepStreetSignRequest;
import com.shinemo.sweepstreet.domain.vo.SweepStreetActivityFinishedVO;
import com.shinemo.sweepstreet.domain.vo.SweepStreetActivityVO;
import com.shinemo.sweepstreet.domain.vo.SweepStreetMerchantListVO;

import java.text.ParseException;
import java.util.Map;

public interface SweepStreetService {
    /** 查詢活動列表 */
    ApiResult<ListVO<SweepStreetActivityVO>> getSweepStreetList(SweepStreetListRequest request);
    /** 打卡 */
    ApiResult startSign(SweepStreetSignRequest request);

    /**
     * 结束打卡
     * @param request
     * @return
     */
    ApiResult endSign(SweepStreetSignRequest request);

    ApiResult<Void> autoEnd(SweepStreetActivityDO streetActivityDO);

    /**
     * 本周，本月统计
     * @param type
     * @return
     */
    ApiResult<SweepStreetActivityFinishedVO> getFinishedCount(Integer type);


    /**
     * 新建扫街计划
     */
    ApiResult<Void> createSweepStreet(SweepStreetActivityRequest sweepStreetActivityRequest);


    /**
     * 获取商户列表 透传华为
     */
    ApiResult<SweepStreetMerchantListVO> getMerchantList(HuaweiMerchantRequest request);

}
