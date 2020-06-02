package com.shinemo.stallup.domain.response;

import com.shinemo.stallup.domain.model.GridUserDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 网格人员列表
 *
 * @author Chenzhe Mao
 * @date 2020-04-03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GridUserListResponse {
	private List<GridUserDetail> getGridUserList;
}
