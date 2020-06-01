package com.shinemo.stallup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 办理量详情
 *
 * @author Chenzhe Mao
 * @date 2020-04-07
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StallUpBizDetail {
	private Long id;
	private Integer num;
}
