package com.shinemo.stallup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * 实体类
 *
 * @author Chenzhe Mao
 * @ClassName: StallUpMarketingNumber
 * @Date 2020-04-01 16:20:07
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StallUpMarketingNumber {
	private Long id;
	private Date gmtCreate;
	private Date gmtModified;
	/**
	 * 用户id
	 */
	private Long userId;
	/**
	 * 摆摊活动id
	 */
	private Long activityId;
	/**
	 * 办理量
	 */
	private Integer count;
	/**
	 * 办理量详情，json
	 */
	private String detail;
	/**
	 * 备注
	 */
	private String remark;
}
