package com.shinemo.stallup.domain.response;

import lombok.Data;

import java.util.List;

/**
 * @author Chenzhe Mao
 * @date 2020-05-25
 */
@Data
public class GetParentListResponse {

	/**
	 * count : 2
	 * list : [{"id":1,"title":"金海小区摆摊","createTime":"2020年4月13日 08:30","partnerList":["程天天1","程天天2","程天天3","程天天4","程天天5","程天天6"],"realStartTime":"2020年4月13日 09:30","realEndTime":"2020年4月13日 10:30","status":1}]
	 */
	private Long totalCount;
	private List<ListBean> rows;

	@Data
	public static class ListBean {
		/**
		 * id : 1
		 * title : 金海小区摆摊
		 * createTime : 2020年4月13日 08:30
		 * partnerList : ["程天天1","程天天2","程天天3","程天天4","程天天5","程天天6"]
		 * realStartTime : 2020年4月13日 09:30
		 * realEndTime : 2020年4月13日 10:30
		 * status : 1
		 */
		private Long id;
		private String title;
		private String createTime;
		private String realStartTime;
		private String realEndTime;
		private int status;
		private List<String> partnerList;
	}
}
