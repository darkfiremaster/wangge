package com.shinemo.stallup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 网格用户详情
 *
 * @author Chenzhe Mao
 * @date 2020-04-03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GridRoleDetail {
	/**
	 * 角色id
	 */
	private Long roleId;
	/**
	 * 角色
	 */
	private String role;
	/**
	 * 角色详情
	 */
	private List<GridUserRoleDetail> list;
}
