package com.shinemo.stallup.domain.response;

import lombok.Data;

import java.util.List;

/**
 * @author Chenzhe Mao
 * @date 2020-05-25
 */
@Data
public class GetGridUserTree {

	/**
	 * text : 网格长
	 * children : [{"text":"张三1","seMobile":"cc128b78b781eb9614d79af2f88abad3044bd53e8bc1123d0009fc1fe99868d7c929e2a3c015cb"}]
	 */
	private String text;
	private List<ChildrenBean> children;

	@Data
	public static class ChildrenBean {
		/**
		 * text : 张三1
		 * seMobile : cc128b78b781eb9614d79af2f88abad3044bd53e8bc1123d0009fc1fe99868d7c929e2a3c015cb
		 */
		private String text;
		private String seMobile;
	}
}
