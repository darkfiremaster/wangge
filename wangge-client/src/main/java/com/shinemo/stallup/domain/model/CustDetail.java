package com.shinemo.stallup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户群详情
 *
 * @author Chenzhe Mao
 * @date 2020-04-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustDetail {
	private String id;
	private String name;
	private String count;
}
