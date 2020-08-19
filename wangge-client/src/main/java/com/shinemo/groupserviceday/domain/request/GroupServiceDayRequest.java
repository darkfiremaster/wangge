package com.shinemo.groupserviceday.domain.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/8/3 17:04
 * @Desc
 */
@Data
public class GroupServiceDayRequest {

    private Long id;
    /**
     * 集团服务日标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;
    /**
     * 集团id
     */
    @NotBlank(message = "集团id不能为空")
    private String groupId;
    /**
     * 集团名称
     */
    @NotBlank(message = "集团名称不能为空")
    private String groupName;
    /**
     * 集团地址
     */
    private String groupAddress;

    /**
     * 集团归属市id
     */
    private String groupCityId;

    private String groupCityName;

    /**
     * 集团归属县id
     */
    private String groupCountryId;

    private String groupCountryName;
    /**
     * 集团归属网格id
     */
    private String groupGridId;

    private String groupGridName;

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
     * 集团坐标
     */
    @NotBlank(message = "集团坐标不能为空")
    private String location;
    /**
     * 参与人详情
     */
    @NotEmpty(message = "参与人不能为空")
    @Valid
    private List<PartnerBean> partner;

    @Data
    public static class PartnerBean {

        @NotBlank(message = "参与人手机号不能为空")
        private String mobile;
        @NotBlank(message = "参与人名字不能为空")
        private String name;

        /**
         * 参与人id:华为侧的用户ID
         */
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
        private String countryId;

        /**
         * 参与人归属区县名称
         */
        private String countryName;

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
