package com.shinemo.stallup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Chenzhe Mao
 * @date 2020-04-07
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustUserDetail {
	private String name;
	private String mobile;
	private String seMobile;
}
