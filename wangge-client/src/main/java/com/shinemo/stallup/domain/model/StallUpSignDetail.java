package com.shinemo.stallup.domain.model;

import lombok.Builder;
import lombok.Data;

/**
 * 摆摊打卡签到详情
 *
 * @author Chenzhe Mao
 * @date 2020-04-07
 */
@Data
@Builder
public class StallUpSignDetail {
	private String location;
	private String address;
}
