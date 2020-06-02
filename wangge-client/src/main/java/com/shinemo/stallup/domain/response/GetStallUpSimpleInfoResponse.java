package com.shinemo.stallup.domain.response;

import com.shinemo.stallup.domain.model.StallUpBizTotal;
import com.shinemo.stallup.domain.model.StallUpDetailVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取简要信息
 *
 * @author Chenzhe Mao
 * @date 2020-04-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetStallUpSimpleInfoResponse {
	private StallUpBizTotal weekDetail;
	private StallUpDetailVO startedDetail;
	private Long todayToDo;
	private Long weekToDo;
	private Long monthDone;
}
