package com.shinemo.wangge.core.service.sweepstreet;

import com.shinemo.client.common.ListVO;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.sweepstreet.domain.model.SweepStreetActivityDO;
import com.shinemo.sweepstreet.domain.request.SweepStreetListRequest;
import com.shinemo.sweepstreet.domain.request.SweepStreetSignRequest;
import com.shinemo.sweepstreet.domain.vo.SweepStreetActivityVO;
import org.springframework.web.bind.annotation.RequestBody;

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
}
