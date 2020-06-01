package com.shinemo.stallup.domain.response;

import lombok.Data;

import java.util.List;

/**
 * @author Chenzhe Mao
 * @date 2020-05-25
 */
@Data
public class GetParentDetailResponse {

	/**
	 * id : 1
	 * title : 金海小区摆摊
	 * address : 南宁市XXXX路108号
	 * createName : 张三
	 * createTime : 2020年4月13日 08:30
	 * realStartTime : 2020年4月13日 09:30
	 * realEndTime : 2020年4月13日 10:30
	 * partnerDetailList : [{"name":"程天天1","realStartTime":"20200512 8:30:30","realEndTime":"20200512 8:30:30","hasException":true,"ExceptionMsg":"超出打卡范围"}]
	 * status : 1
	 * bizList : [{"bizName":"新入网","userList":[{"name":"张三","num":2}],"total":2}]
	 */
	private Long id;
	private String title;
	private String address;
	private String createName;
	private String createTime;
	private String realStartTime;
	private String realEndTime;
	private int status;
	private List<PartnerDetailListBean> partnerDetailList;
	private List<BizListBean> bizList;

	@Data
	public static class PartnerDetailListBean {
		/**
		 * name : 程天天1
		 * realStartTime : 20200512 8:30:30
		 * realEndTime : 20200512 8:30:30
		 * hasException : true
		 * ExceptionMsg : 超出打卡范围
		 */
		private String name;
		private String realStartTime;
		private String realEndTime;
		private boolean hasException;
		private String ExceptionMsg;
	}

	@Data
	public static class BizListBean {
		/**
		 * bizName : 新入网
		 * userList : [{"name":"张三","num":2}]
		 * total : 2
		 */
		private String bizName;
		private int total;
		private List<UserListBean> userList;

		@Data
		public static class UserListBean {
			/**
			 * name : 张三
			 * num : 2
			 */
			private String name;
			private int num;

		}
	}
}
