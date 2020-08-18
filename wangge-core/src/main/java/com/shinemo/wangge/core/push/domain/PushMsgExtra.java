package com.shinemo.wangge.core.push.domain;

import lombok.Builder;
import lombok.Data;


/**
 * @Date: Created by Zeng Pingping on 12 / 12 / 2018
 * @Author: zengpp@shinemo.com
 * @Description:
 */
@Data
@Builder
public class PushMsgExtra {

    private String action;
    private String title;
    private String content;
    private String icon;
    private Long orgId;
    private String image;
    private Integer isShare;

    // 外部标题下的展示内容
    private String messageTitle;
}
