package com.shinemo.stallup.domain.response;

import com.shinemo.stallup.domain.model.CustUserDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Chenzhe Mao
 * @date 2020-04-07
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCustDetailResponse {
	private List<CustUserDetail> custList;
	private Long total;
}
