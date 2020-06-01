package com.shinemo.stallup.domain.request;

import com.shinemo.stallup.domain.model.StallUpBizDetail;
import lombok.Data;

import java.util.List;

/**
 * 结束摆摊请求
 *
 * @author Chenzhe Mao
 * @date 2020-04-01
 */
@Data
public class StallUpEndRequest extends StallUpRequest {
	private String remark;
	private List<String> imageList;
	private String location;
	private String address;
	private List<StallUpBizDetail> bizDetailList;
	private String bizRemark;
}
