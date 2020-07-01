package com.shinemo.stallup.domain.model;

import lombok.Data;

import java.util.Date;
import java.util.List;


/**
 * 实体类
 *
 * @author Chenzhe Mao
 * @ClassName: ParenstallUpActivity
 * @Date 2020-05-22 22:08:12
 */
@Data
public class ParentStallUpActivity {
	private Long id;
	private Date gmtCreate;
	private Date gmtModified;
	/**
	 * 小区名(存储摆摊具体地址，有可能是非小区名)
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
	 * 创建人手机号
	 */
	private String mobile;
	/**
	 * 创建人名字
	 */
	private String name;
	/**
	 * 摆摊标题
	 */
	private String title;
	/**
	 * 计划开始时间
	 */
	private Date startTime;
	/**
	 * 计划结束时间
	 */
	private Date endTime;
	/**
	 * 实际开始时间
	 */
	private Date realStartTime;
	/**
	 * 实际结束时间
	 */
	private Date realEndTime;
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
	 * 标位，第一位表示子摆摊有取消、打卡异常、自动签退
	 */
	private Integer flag;
	/**
	 * 扩展字段
	 */
	private String extend;
	private Long count;
	private String orderId;

	//查询条件
	private Date ltCreateTime;
	private Date gtCreateTime;
	private Date gtRealEndTime;
	private Date ltStartTime;
	private List<String> gridIds;


	public void addExceptionFlag() {
		if (flag == null) {
			flag = 0;
		}
		flag = flag | 1;
	}

	public boolean hasException() {
		if(flag==null){
			return false;
		}
		return (flag & 1) == 1;
	}
}
