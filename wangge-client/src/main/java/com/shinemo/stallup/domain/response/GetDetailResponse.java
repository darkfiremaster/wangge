package com.shinemo.stallup.domain.response;

import com.shinemo.stallup.domain.model.StallUpBizType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 摆摊情况
 *
 * @author Chenzhe Mao
 * @date 2020-04-08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDetailResponse {
	/**
	 * 业务办理
	 *
	 * @see StallUpBizType
	 */
	private List<StallUpBizType> bizList;
	/**
	 * 营销工具
	 */
	private List<StallUpBizType> marketList;
	private List<String> custIdList;
	private String bizRemark;
	/**
	 * 计划摆摊地址
	 */
	private String location;
	/**
	 * 小区id
	 */
	private String communityId;
	/**
	 * 摆摊名
	 */
	private String title;
	/**
	 * 地址
	 */
	private String address;
}
