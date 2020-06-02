package com.shinemo.stallup.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.Ordered;

/**
 * 网格内人员信息
 *
 * @author Chenzhe Mao
 * @date 2020-04-03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GridUserDetail implements Ordered {
	private String seMobile;
	private String mobile;
	private String name;
	private String role;
	@Expose(serialize = false, deserialize = false)
	@JsonIgnore
	private Integer order;

	@Override
	public int getOrder() {
		return order;
	}
}
