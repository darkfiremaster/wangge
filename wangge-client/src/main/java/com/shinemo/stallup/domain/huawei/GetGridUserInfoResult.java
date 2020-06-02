package com.shinemo.stallup.domain.huawei;

import lombok.Data;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/4/26 19:11
 * @Desc
 */
@Data
public class GetGridUserInfoResult {


	/**
	 * code : 200
	 * message : success
	 * data : {"isAuthSuccess":"true","userId":"luzhong","userName":"陆忠","gridList":[{"areaCode":"771_A2107_XX","areaName":"陆忠网格1","roleList":[{"id":1,"name":"网格长"},{"id":2,"name":"网格经理"}]},{"areaCode":"771_A2107_XX","areaName":"陆忠网格2","roleList":[{"id":1,"name":"网格长"},{"id":2,"name":"网格经理"}]}]}
	 */

	private int code;
	private String message;
	private DataBean data;

	@Data
	public static class DataBean {
		/**
		 * isAuthSuccess : true
		 * userId : luzhong
		 * userName : 陆忠
		 * gridList : [{"areaCode":"771_A2107_XX","areaName":"陆忠网格1","roleList":[{"id":1,"name":"网格长"},{"id":2,"name":"网格经理"}]},{"areaCode":"771_A2107_XX","areaName":"陆忠网格2","roleList":[{"id":1,"name":"网格长"},{"id":2,"name":"网格经理"}]}]
		 */

		private String isAuthSuccess;
		private String userId;
		private String userName;
		private List<GridListBean> gridList;

		@Data
		public static class GridListBean {
			/**
			 * areaCode : 771_A2107_XX
			 * areaName : 陆忠网格1
			 * roleList : [{"id":1,"name":"网格长"},{"id":2,"name":"网格经理"}]
			 */

			private String areaCode;
			private String areaName;
			private List<RoleListBean> roleList;

			@Data
			public static class RoleListBean {
				/**
				 * id : 1
				 * name : 网格长
				 */

				private String id;
				private String name;

			}
		}
	}
}
