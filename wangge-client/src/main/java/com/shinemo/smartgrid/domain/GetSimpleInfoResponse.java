package com.shinemo.smartgrid.domain;

import com.google.gson.JsonElement;
import com.shinemo.stallup.domain.model.StallUpBizTotal;
import com.shinemo.stallup.domain.model.StallUpBizType;
import com.shinemo.stallup.domain.model.StallUpDetailVO;
import com.shinemo.stallup.domain.model.SweepFloorBizTotal;
import com.shinemo.sweepfloor.domain.vo.SweepFloorActivityVO;
import lombok.Data;

import java.util.List;

/**
 * 获取简要信息
 *
 * @author Chenzhe Mao
 * @date 2020-04-07
 */
@Data
public class GetSimpleInfoResponse {
	//摆摊
	private StallUpBizTotal stallUpWeekDetail;
	private StallUpDetailVO stallUpStartedDetail;
	//todo 扫楼
	private SweepFloorActivityVO sweepFloorActivityVO;
	private SweepFloorBizTotal sweepFloorBizTotal;
	private Long todayToDo;
	private Long weekToDo;
	private Long monthDone;
	//首页业务办理
	private List<StallUpBizType> indexList;
	//首页业务办理
	private List<StallUpBizType> quickAccessList;
	//首页业务办理，7个长在首页的应用
	private List<StallUpBizType> homePageBizList;
	private DuDaoQueryResultVO duDaoResult;
	private Long duDaoTopId;
	private Long duDaoBottomId;
	@Data
	public static class DuDaoQueryResult{
		private Integer taskTotal;
		private Integer status;
		private String resultDesc;
		private JsonElement taskInfo;
	}
	@Data
	public static class DuDaoQueryResultVO{
		private Integer taskTotal;
		private TaskInfo taskInfo;
	}
	@Data
	public static class TaskInfo{
		private String taskId;
		private String taskName;
	}

}