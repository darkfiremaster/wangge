package com.shinemo.stallup.domain.huawei;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author shangkaihui
 * @Date 2020/4/26 18:59
 * @Desc
 */
@ToString
@Setter
@Getter
public class GetGridUserListRequest {

    /**
     * 网格id
     */
    private String gridId;
}
