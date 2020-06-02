package com.shinemo.stallup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 华为地图搜索结果详情
 *
 * @author Chenzhe Mao
 * @date 2020-04-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchDetail implements Comparable<SearchDetail> {
	/**
	 * 小区id
	 */
	private String id;
	/**
	 * 小区名
	 */
	private String name;
	/**
	 * 小区详细地址
	 */
	private String address;
	/**
	 * 经纬度
	 */
	private String location;
	/**
	 * 距离带单位
	 */
	private String distance;
	/**
	 * 距离不带单位
	 */
	private Integer iDistance;

	@Override
	public int compareTo(SearchDetail detail) {
		return iDistance - detail.getIDistance();
	}
}
