package com.shinemo.wangge.dal.mapper;

import com.shinemo.common.db.dao.BaseMapper;
import com.shinemo.sweepvillage.domain.model.SweepVillageVisitRecordingDO;
import com.shinemo.sweepvillage.domain.query.SweepVillageVisitRecordingQuery;

import java.util.List;


public interface SweepVillageVisitRecordingMapper extends BaseMapper<SweepVillageVisitRecordingQuery, SweepVillageVisitRecordingDO> {

    public SweepVillageVisitRecordingDO getRecentVisit(SweepVillageVisitRecordingQuery query);

    public List<SweepVillageVisitRecordingDO> refreshCreateMobile(SweepVillageVisitRecordingQuery query);
}
