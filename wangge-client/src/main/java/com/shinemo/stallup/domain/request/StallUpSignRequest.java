package com.shinemo.stallup.domain.request;

import lombok.Data;

/**
 * 摆摊打卡请求
 *
 * @author Chenzhe Mao
 * @date 2020-04-01
 */
@Data
public class StallUpSignRequest extends StallUpRequest {
	private String address;
	private String location;
}
