package com.shinemo.stallup.domain.response;

import com.shinemo.stallup.domain.model.GridRoleDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 网格用户信息响应
 *
 * @author Chenzhe Mao
 * @date 2020-04-03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GridUserInfoResponse {
	/**
	 * 是否是网格系统用户
	 */
	private boolean isGridUser;
	/**
	 * 详情
	 */
	private List<GridRoleDetail> detailList;
}
