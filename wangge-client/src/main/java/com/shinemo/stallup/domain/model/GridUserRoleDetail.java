package com.shinemo.stallup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 网格角色详情
 *
 * @author Chenzhe Mao
 * @date 2020-04-03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GridUserRoleDetail {
	private String id;
	private String name;
	private String cityName;
	private String cityCode;
	private String countyName;
	private String countyCode;
	private List<GridRole> roleList;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class GridRole{
		private String id;
		private String name;
	}
}
