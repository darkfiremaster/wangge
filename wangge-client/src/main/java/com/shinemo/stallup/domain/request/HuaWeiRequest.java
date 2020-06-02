package com.shinemo.stallup.domain.request;

import lombok.Builder;
import lombok.Data;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.util.List;

/**
 * @author Chenzhe Mao
 * @date 2020-04-02
 */
@Data
@Builder
public class HuaWeiRequest {
	private String keywords;
	private String location;
	private String communityId;
	private String mobile;
	private String gridId;
	private List<String> custIdList;
	private Integer page;
	private Integer pageSize;
}
