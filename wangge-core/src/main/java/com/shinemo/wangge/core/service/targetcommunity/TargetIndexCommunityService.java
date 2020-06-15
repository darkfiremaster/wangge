package com.shinemo.wangge.core.service.targetcommunity;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.targetcustomer.domain.model.TargetIndexCommunityDO;

import java.util.List;

public interface TargetIndexCommunityService {

    /**
     * 通过indexId获取小区列表(根据潜客数量排序)
     * @param indexId
     * @return
     */
    ApiResult<List<TargetIndexCommunityDO>> findByIndexId(Long indexId);
}
