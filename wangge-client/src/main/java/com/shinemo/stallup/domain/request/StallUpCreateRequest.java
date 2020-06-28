package com.shinemo.stallup.domain.request;

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
	private String communityName;
	private String address;
	private String gridId;
	private List<GridUserDetail> partnerList;
	private List<Long> bizTypeList;
	private List<String> custList;
	private String location;
	private String orderId;
}
