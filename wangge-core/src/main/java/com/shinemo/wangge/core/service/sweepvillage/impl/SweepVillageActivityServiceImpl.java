package com.shinemo.wangge.core.service.sweepvillage.impl;

import com.google.common.collect.Lists;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.sweepfloor.domain.query.SweepFloorActivityQuery;
import com.shinemo.sweepvillage.domain.model.SweepVillageActivityDO;
import com.shinemo.sweepvillage.domain.request.SweepVillageActivityQueryRequest;
import com.shinemo.sweepvillage.enums.SweepVillageStatusEnum;
import com.shinemo.sweepvillage.error.SweepVillageErrorCodes;
import com.shinemo.sweepvillage.domain.query.SweepVillageActivityQuery;
import com.shinemo.sweepvillage.domain.vo.SweepVillageActivityFinishVO;
import com.shinemo.sweepvillage.domain.vo.SweepVillageActivityResultVO;
import com.shinemo.sweepvillage.domain.vo.SweepVillageActivityVO;
import com.shinemo.sweepvillage.domain.vo.VillageVO;
import com.shinemo.wangge.core.service.sweepvillage.SweepVillageActivityService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingService;
import com.shinemo.wangge.dal.mapper.SweepVillageActivityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 17:32
 * @Desc
 */
@Service
@Slf4j
public class SweepVillageActivityServiceImpl implements SweepVillageActivityService {

    @Resource
    private SweepVillageActivityMapper sweepVillageActivityMapper;

    @Autowired
    private ThirdApiMappingService thirdApiMappingService;

    @Override
    public ApiResult<Void> createSweepVillageActivity(SweepVillageActivityVO sweepVillageActivityVO) {
        Assert.notNull(sweepVillageActivityVO, "sweepVillageActivityVO is null");
        //todo 校验参数

        //todo 判断用户是否已有进行中的扫村活动

        SweepVillageActivityDO sweepFloorActivityDO = getSweepVillageActivityDO(sweepVillageActivityVO);
        sweepVillageActivityMapper.insert(sweepFloorActivityDO);

        //todo 同步华为
        return ApiResult.of(0);
    }


    @Override
    public ApiResult<List<VillageVO>> getVillageList() {
        //todo 透传华为
        return null;
    }


    @Override
    public ApiResult<Map<String, Object>> createVillage(VillageVO villageVO) {
        //todo 透传华为

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", "skh");
        ApiResult<Map<String, Object>> result = thirdApiMappingService.dispatch(map, "wahaha");

        return result;
    }




    @Override
    public ApiResult<Void> finishActivity(SweepVillageActivityFinishVO sweepVillageActivityFinishVO) {
        //todo 校验参数
        Assert.notNull(sweepVillageActivityFinishVO.getId(), "id is null");

        SweepVillageActivityQuery sweepVillageActivityQuery = new SweepVillageActivityQuery();
        sweepVillageActivityQuery.setId(sweepVillageActivityFinishVO.getId());
        SweepVillageActivityDO sweepVillageActivityDO = sweepVillageActivityMapper.get(sweepVillageActivityQuery);
        if (sweepVillageActivityDO == null) {
            return ApiResultWrapper.fail(SweepVillageErrorCodes.SWEEP_VILLAGE_ACTIVITY_NOT_EXIST);
        }
        if (!sweepVillageActivityDO.getStatus().equals(SweepVillageStatusEnum.PROCESSING.getId())) {
            return ApiResultWrapper.fail(SweepVillageErrorCodes.SWEEP_VILLAGE_STATUS_ERROR);
        }
        //todo 判断打卡距离

        sweepVillageActivityDO.setEndTime(new Date());
        sweepVillageActivityDO.setStatus(SweepVillageStatusEnum.END.getId());
        sweepVillageActivityMapper.update(sweepVillageActivityDO);

        //todo 插入签到表

        //todo 透传华为
        return ApiResult.of(0);
    }

    @Override
    public ApiResult<List<SweepVillageActivityVO>> getSweepVillageActivityList(SweepVillageActivityQueryRequest sweepVillageActivityQueryRequest) {
        //校验参数
        Assert.notNull(sweepVillageActivityQueryRequest, "request is null");
        Assert.notNull(sweepVillageActivityQueryRequest.getQueryType(), "queryType is null");

        if (sweepVillageActivityQueryRequest.getQueryType().equals(1)) {
            //查进行中的活动
            SweepVillageActivityQuery sweepVillageActivityQuery = new SweepVillageActivityQuery();
            sweepVillageActivityQuery.setMobile(SmartGridContext.getMobile());
            sweepVillageActivityQuery.setStatus(SweepVillageStatusEnum.PROCESSING.getId());
            log.info("[getSweepVillageActivityList] 获取进行中的扫村活动列表,query:{}", sweepVillageActivityQuery);
            List<SweepVillageActivityDO> sweepVillageActivityDOS = sweepVillageActivityMapper.find(sweepVillageActivityQuery);
            //todo 转为vo
        } else if (sweepVillageActivityQueryRequest.getQueryType().equals(2)) {
            //查已结束的活动
            SweepVillageActivityQuery sweepVillageActivityQuery = new SweepVillageActivityQuery();
            sweepVillageActivityQuery.setMobile(SmartGridContext.getMobile());
            sweepVillageActivityQuery.setStatusList(Lists.newArrayList(
                    SweepVillageStatusEnum.END.getId(),
                    SweepVillageStatusEnum.END.getId(),
                    SweepVillageStatusEnum.END.getId()
            ));

        } else {
            throw new ApiException("queryType error");
        }



        //todo 转化为vo

        return ApiResult.of(0, null);
    }

    @Override
    public ApiResult<SweepVillageActivityResultVO> getFinshResultInfo(SweepFloorActivityQuery sweepFloorActivityQuery) {
        //todo 等产品确认
        return null;
    }

    private SweepVillageActivityDO getSweepVillageActivityDO(SweepVillageActivityVO sweepVillageActivityVO) {
        SweepVillageActivityDO sweepVillageActivityDO = new SweepVillageActivityDO();
        sweepVillageActivityDO.setTitle(sweepVillageActivityVO.getTitle());
        sweepVillageActivityDO.setVillageId(sweepVillageActivityVO.getVillageId());
        sweepVillageActivityDO.setVillageName(sweepVillageActivityVO.getVillageName());
        sweepVillageActivityDO.setArea(sweepVillageActivityVO.getArea());
        sweepVillageActivityDO.setAreaCode(sweepVillageActivityVO.getAreaCode());
        sweepVillageActivityDO.setLocation(sweepVillageActivityVO.getLocation());
        sweepVillageActivityDO.setRgsLocation(sweepVillageActivityVO.getRgsLocation());
        sweepVillageActivityDO.setMobile(SmartGridContext.getMobile());
        sweepVillageActivityDO.setStatus(SweepVillageStatusEnum.NOT_START.getId());

        //TODO
        return null;
    }
}


