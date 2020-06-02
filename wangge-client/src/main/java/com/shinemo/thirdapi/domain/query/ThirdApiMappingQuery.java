package com.shinemo.thirdapi.domain.query;

import com.shinemo.smartgrid.domain.QueryBase;
import lombok.Data;

/**
 * @Author shangkaihui
 * @Date 2020/5/8 10:25
 * @Desc
 */
@Data
public class ThirdApiMappingQuery extends QueryBase {

    private Long id;

    private String apiName;
}
