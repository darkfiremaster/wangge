package com.shinemo.stallup.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;


/**
 * 实体类
 *
 * @author Chenzhe Mao
 * @ClassName: StallUpActivity
 * @Date 2020-04-01 16:20:55
 */
@Data
@Builder
public class StallUpActivityVO {
	private Long id;
	/**
	 * 详细地址
	 */
	private String address;
	/**
	 * 摆摊标题
	 */
	private String title;
	/**
	 * 开始时间
	 */
	private Date startTime;
	/**
	 * 结束时间
	 */
	private Date endTime;
	/**
	 * 摆摊地址经纬度
	 */
	private String location;
}
