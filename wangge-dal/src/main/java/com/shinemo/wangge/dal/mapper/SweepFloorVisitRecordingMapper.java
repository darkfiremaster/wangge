package com.shinemo.wangge.dal.mapper;

import com.shinemo.common.db.dao.BaseMapper;
import com.shinemo.sweepfloor.domain.model.SweepFloorVisitRecordingDO;
import com.shinemo.sweepfloor.domain.query.SweepFloorVisitRecordingQuery;

import java.util.List;

public interface SweepFloorVisitRecordingMapper extends BaseMapper<SweepFloorVisitRecordingQuery, SweepFloorVisitRecordingDO> {

    List<SweepFloorVisitRecordingDO> findNewLatest(SweepFloorVisitRecordingQuery query);
}
