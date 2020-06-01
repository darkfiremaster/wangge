package com.shinemo.stallup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 摆摊业务量统计
 *
 * @author Chenzhe Mao
 * @date 2020-04-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StallUpBizTotal {
	private Integer stallUpNum;
	private Integer bizNum;
}
