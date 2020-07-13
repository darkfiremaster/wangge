package com.shinemo.wangge.dal.mapper;

import com.shinemo.common.db.dao.BaseMapper;
import com.shinemo.sweepvillage.domain.model.SweepVillageVisitRecordingDO;
import com.shinemo.sweepvillage.domain.query.SweepVillageVisitRecordingQuery;


public interface SweepVillageVisitRecordingMapper extends BaseMapper<SweepVillageVisitRecordingQuery, SweepVillageVisitRecordingDO> {

    public SweepVillageVisitRecordingDO getRecentVisit(SweepVillageVisitRecordingQuery query);
}
