package com.shinemo.stallup.domain.response;

import com.shinemo.stallup.domain.model.SearchDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 华为地图搜索请求
 *
 * @author Chenzhe Mao
 * @date 2020-04-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {
	private List<SearchDetail> list;
}
