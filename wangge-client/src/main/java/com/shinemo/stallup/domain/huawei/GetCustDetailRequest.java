package com.shinemo.stallup.domain.huawei;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/4/26 17:43
 * @Desc
 */
@ToString
@Setter
@Getter
public class GetCustDetailRequest{


    /**
     * 小区id
     */
    private String cellId;

    /**
     * 客户群id列表
     */
    private List<String> custGroupIdList;


    private Integer page;

    private Integer pageSize;

}
