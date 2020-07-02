package com.shinemo.stallup.domain.request;

import com.shinemo.stallup.domain.model.CommunityVO;
import com.shinemo.stallup.domain.model.GridUserDetail;
import lombok.Data;

import java.util.List;

/**
 * 新建摆摊请求
 *
 * @author Chenzhe Mao
 * @date 2020-04-01
 */
@Data
public class StallUpCreateRequest extends StallUpRequest{
	private Long orgId;
	private String title;
	private Long startTime;
	private Long endTime;
	private String communityId;
	/** 摆摊地址名 */
	private String communityName;
	/** 摆摊详细地址 */
	private String address;
	private String gridId;
	private List<GridUserDetail> partnerList;
	private List<Long> bizTypeList;
	private List<String> custList;
	/** 摆摊坐标地址 */
	private String location;
	private String orderId;
	/** 摆摊活动小区集合 */
	private List<CommunityVO> communityVOS;
}
