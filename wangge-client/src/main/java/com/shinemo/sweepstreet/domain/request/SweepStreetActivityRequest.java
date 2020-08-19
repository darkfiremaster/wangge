package com.shinemo.sweepstreet.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 类说明:
 *
 * @author zengpeng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SweepStreetActivityRequest {
    private Long id;
    /**
     * 扫村活动标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 计划开始时间
     */
    @NotNull(message = "开始时间不能为空")
    private Long planStartTime;
    /**
     * 计划结束时间
     */
    @NotNull(message = "结束时间不能为空")
    private Long planEndTime;
    /**
     * 创建人创建活动时的坐标
     */
    private String location;

    /**
     * 参与人详情
     */
    @NotEmpty(message = "参与人不能为空")
    @Valid
    private List<SweepStreetActivityRequest.PartnerBean> partner;

    @Data
    public static class PartnerBean {

        @NotBlank(message = "参与人手机号不能为空")
        private String mobile;
        @NotBlank(message = "参与人名字不能为空")
        private String name;

        private String userId;

        /**
         * 参与人归属地市ID
         */
        private String cityId;

        /**
         * 参与人归属地市名称
         */
        private String cityName;

        /**
         * 参与人归属区县ID
         */
        private String countyId;

        /**
         * 参与人归属区县名称
         */
        private String countyName;

        /**
         * 参与人归属网格ID，如果有多个值，以英文逗号分隔
         */
        private String gridId;

        /**
         * 参与人归属网格名称，如果有多个归属网格，以逗号,分隔
         */
        private String gridName;
    }

}
