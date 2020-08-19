package com.shinemo.stallup.domain.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.shinemo.client.common.BaseDO;


/**
 * 实体类
 * @ClassName: StallUpImportantRegion
 * @author cqh
 * @Date 2020-08-19 14:02:23
 */
@Getter
@Setter
public class StallUpImportantRegion extends BaseDO {
	private Long id;
	/**
	* 城市
	*/
	private String city;
	/**
	* 区县
	*/
	private String county;
	/**
	* 乡镇
	*/
	private String town;
	/**
	* 行政村
	*/
	private String villager;
	/**
	* 路巷街
	*/
	private String street;
	/**
	* 门牌编号
	*/
	private String houseNumber;
	/**
	* 小区编号
	*/
	private String communityId;
	/**
	* 小区名称
	*/
	private String communityName;
	/**
	* 小区属性编码（比如：1-城市，2-县城，3-乡镇，4-农村）
	*/
	private String regionAttribute;
	/**
	* 是否两高一弱承包小区
	*/
	private String ontractingFlag;
	/**
	* 地域属性
	*/
	private String placeAttribute;
	/**
	* 中心坐标
	*/
	private String location;
	/**
	* 位置（小区经纬度）（边界）
	*/
	private String locationBoundary;
	/**
	* 小区归属网格id
	*/
	private Long regionWanggeId;
	/**
	* 小区归属地市编码CITY_ID
	*/
	private Long regionCityId;
	/**
	* 小区归属区县编码COUNTY_ID
	*/
	private Long regionCountyId;
	/**
	* 入网时间
	*/
	private Date gmtCreate;
	/**
	* 扩展字段
	*/
	private String extend;

}
