package com.shinemo.stallup.domain.response;

import com.shinemo.stallup.domain.model.StallUpDetailVO;
import lombok.Data;

import java.util.List;

/**
 * 已结束摆摊列表
 *
 * @author Chenzhe Mao
 * @date 2020-04-07
 */
@Data
public class GetEndListResponse {
	private Long total;
	private List<StallUpDetailVO> endList;
}
