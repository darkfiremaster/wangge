package com.shinemo.sweepstreet.domain.model;

import com.shinemo.client.common.BaseDO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


/**
 * 实体类
 * @ClassName: ParentSweepStreetActivityDO
 * @author Zeng Peng
 * @Date 2020-08-13 15:35:21
 */
@Getter
@Setter
public class ParentSweepStreetActivityDO extends BaseDO {
	private Long id;
	/**
	* 创建时间
	*/
	private Date gmtCreate;
	/**
	* 修改时间
	*/
	private Date gmtModified;
	/**
	* 扫街活动标题
	*/
	private String title;
	/**
	* 创建人id
	*/
	private Long creatorId;
	/**
	* 创建人组织id
	*/
	private Long creatorOrgId;
	/**
	* 创建人名称
	*/
	private String creatorName;
	/**
	* 手机号
	*/
	private String mobile;
	/**
	* 计划开始时间
	*/
	private Date planStartTime;
	/**
	* 计划结束时间
	*/
	private Date planEndTime;
	/**
	* 实际开始时间
	*/
	private Date realStartTime;
	/**
	* 实际结束时间
	*/
	private Date realEndTime;
	/**
	* 坐标
	*/
	private String location;
	/**
	* 参与人详情
	*/
	private String partner;
	/**
	* 状态 0-已取消 1-待开始 2-已开始 3-已结束 4-超时自动结束 5-异常结束
	*/
	private Integer status;
	/**
	* 网格id
	*/
	private String gridId;
	/**
	* 扩展字段
	*/
	private String extend;

}
