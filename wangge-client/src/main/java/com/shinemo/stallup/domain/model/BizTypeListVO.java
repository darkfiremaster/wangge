package com.shinemo.stallup.domain.model;

import lombok.Data;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/7/13 13:47
 * @Desc
 */
@Data
public class BizTypeListVO {

    private List<BizTypeBean> bizTypeListBean;

    @Data
    public static class BizTypeBean {

        private String bizGroupName;

        private List<StallUpBizType> bizTypeList;
    }
}
