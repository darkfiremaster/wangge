package com.shinemo.stallup.domain.response;

import com.shinemo.stallup.domain.model.CustDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 小区信息查询响应
 *
 * @author Chenzhe Mao
 * @date 2020-04-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityResponse {
	/**
	 * 客户群列表
	 */
	private List<CustDetail> list;
	/**
	 * 小区边界经纬度数组
	 */
	private List<String> boundary;
	/**
	 * 小区中心点经纬度坐标
	 */
	private String centre;
	/**
	 * 商机个数
	 */
	private Integer opportunity;
	/**
	 * 预警个数
	 */
	private Integer warning;
}
