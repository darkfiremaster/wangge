package com.shinemo.stallup.domain.huawei;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author shangkaihui
 * @Date 2020/4/26 16:20
 * @Desc 地图搜索请求参数
 */
@Setter
@Getter
@ToString
public class SearchMapRequest{

    private String cellName;

}
