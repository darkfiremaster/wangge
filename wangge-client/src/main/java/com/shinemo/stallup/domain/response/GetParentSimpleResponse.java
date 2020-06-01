package com.shinemo.stallup.domain.response;

import lombok.Data;

import java.util.Map;

/**
 * @author Chenzhe Mao
 * @date 2020-05-26
 */
@Data
public class GetParentSimpleResponse {
	Map<String, Long> prepareMap;
	Map<String, Long> startMap;
	Map<String, Long> endMap;
}
