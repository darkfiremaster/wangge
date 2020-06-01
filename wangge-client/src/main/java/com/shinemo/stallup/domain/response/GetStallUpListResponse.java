package com.shinemo.stallup.domain.response;

import com.shinemo.stallup.domain.model.StallUpBizTotal;
import com.shinemo.stallup.domain.model.StallUpDetailVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 获取摆摊计划列表
 *
 * @author Chenzhe Mao
 * @date 2020-04-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetStallUpListResponse {
	private StallUpBizTotal weekDetail;
	private StallUpBizTotal monthDetail;
	private StallUpDetailVO startedDetail;
	private List<StallUpDetailVO> prepareDetail;
}
