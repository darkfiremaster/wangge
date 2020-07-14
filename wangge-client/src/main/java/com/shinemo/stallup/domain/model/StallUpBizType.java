package com.shinemo.stallup.domain.model;

import com.shinemo.stallup.domain.params.BizParams;
import lombok.Data;

/**
 * 摆摊业务类型
 *
 * @author Chenzhe Mao
 * @date 2020-04-08
 */
@Data
public class StallUpBizType implements Comparable<StallUpBizType> {
	private Long id;
	private String name;
	/**
	 * 按order升序
	 */
	private Integer order;
	/**
	 * 是否展示
	 */
	private Boolean isDisplay;
	/**
	 * 0-不跳转 1-链接跳转 2-码店业务办理 3-码店大数据工具 4-短信预热 5-码店智慧查询 6-随身行
	 * 7-倒三角 8-码店大数据工具带手机 9-码店智慧查询带手机 10-督导 11-督导查询接口 12-装维 13-全景
	 */
	private Integer type;
	/**
	 * 图标
	 */
	private String icon;
	/**
	 * 业务参数
	 */
	private BizParams bizParams;
	/**
	 * 跳转连接
	 */
	private String url;
	/**
	 * 办理量
	 */
	private Integer num;
	/**
	 * 分类
	 */
	private Integer category;

	/**
	 * 分组
	 */
	private Integer groupId;

	@Override
	public int compareTo(StallUpBizType o) {
		if (this.order == null || o.getOrder() == null) {
			return 0;
		}
		return this.order - o.getOrder();
	}
}
