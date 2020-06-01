package com.shinemo.stallup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


/**
 * 实体类
 *
 * @author Chenzhe Mao
 * @ClassName: StallUpActivity
 * @Date 2020-04-01 16:20:55
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StallUpActivity {
	private Long id;
	private Date gmtCreate;
	private Date gmtModified;
	/**
	 * 小区名
	 */
	private String communityName;
	/**
	 * 小区id
	 */
	private String communityId;
	/**
	 * 详细地址
	 */
	private String address;
	/**
	 * 创建人id
	 */
	private Long creatorId;
	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 创建人组织id
	 */
	private Long creatorOrgId;
	/**
	 * 摆摊标题
	 */
	private String title;
	/**
	 * 开始时间
	 */
	private Date startTime;
	/**
	 * 结束时间
	 */
	private Date endTime;
	/**
	 * 摆摊地址经纬度
	 */
	private String location;
	/**
	 * 状态
	 */
	private Integer status;
	/**
	 * 参与人详情
	 */
	private String partner;
	/**
	 * 网格id
	 */
	private String gridId;
	/**
	 * 客户群id列表
	 */
	private String custIdList;
	/**
	 * 业务类型列表
	 */
	private String bizList;
	/**
	 * 父摆摊id
	 */
	private Long parentId;
	/**
	 * 用户姓名
	 */
	private String name;

	//附加查询参数
	private List<Integer> statusList;
	private Integer realEndDays;
	private Date startOfNextWeek;
	private Date ltRealEndTime;
	private Date gtRealEndTime;
	/**
	 * 1-扫楼 2-摆摊
	 */
	private Integer bizType;
}
