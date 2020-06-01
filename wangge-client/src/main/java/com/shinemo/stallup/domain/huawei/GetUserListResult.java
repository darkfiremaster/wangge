package com.shinemo.stallup.domain.huawei;

import lombok.Data;

import java.util.List;

/**
 * @author Chenzhe Mao
 * @date 2020-05-08
 */
@Data
public class GetUserListResult {
	private Integer code;
	private String message;
	private List<DataBean> data;

	@Data
	public static class DataBean {
		private String userTel;
	}
}
