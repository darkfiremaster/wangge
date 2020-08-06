package com.shinemo.groupserviceday.domain.query;

import com.shinemo.client.common.QueryBase;
import lombok.Data;

import java.util.Date;

/**
 * 查询类
 * @ClassName: TParentGroupServiceDayQuery
 * @author skh
 * @Date 2020-08-03 15:30:45
 */
@Data
public class ParentGroupServiceDayQuery extends QueryBase {
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
    * 集团服务日标题
    */
	private String title;
    /**
    * 集团id
    */
	private String groupId;
    /**
    * 集团名称
    */
	private String groupName;
    /**
    * 集团地址
    */
	private String groupAddress;
    /**
    * 集团详情
    */
	private String groupDetail;
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