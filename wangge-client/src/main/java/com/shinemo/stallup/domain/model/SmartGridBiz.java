package com.shinemo.stallup.domain.model;

import lombok.Data;

import java.util.Date;


/**
 * 实体类
 *
 * @author Chenzhe Mao
 * @ClassName: SmartGridBiz
 * @Date 2020-04-27 12:32:16
 */
@Data
public class SmartGridBiz {
	private Long id;
	private Date gmtCreate;
	private Date gmtModified;
	private String name;
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
	 * 配置
	 */
	private String config;
	/**
	 * 跳转链接
	 */
	private String url;
	/**
	 * 状态
	 */
	private Integer status;
	/**
	 * 分类 0-其他 1-业务办理 2-办公工具 3-营销辅助
	 */
	private Integer category;
}
