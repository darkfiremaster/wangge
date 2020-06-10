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
 * @author zz
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GridUserDetailSimple {

	private String mobile;
	private String name;
	private String role;
}
