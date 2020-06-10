package com.shinemo.operate.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Author shangkaihui
 * @Date 2020/6/9 19:32
 * @Desc
 */
@Data
public class LoginInfoResultDO {

    private Long id;
    private Date gmtCreate;
    private Date gmtModified;

    private LocalDateTime statisticsTime;
    private String cityName;
    private String cityCode;
    private String countyName;
    private String countyCode;


    /**
     * 网格长登录人数
     */
    private Integer gridCaptainLoginPersonCount;

    /**
     * 网格长登录次数
     */
    private Integer gridCaptainLoginCount;

    /**
     * 网格经理登录人数
     */
    private Integer gridManagerLoginPersonCount;

    /**
     * 网格经理登录次数
     */
    private Integer gridManagerLoginCount;

    /**
     * 直销员登录人数
     */
    private Integer directSellerLoginPersonCount;

    /**
     * 直销员登录次数
     */
    private Integer directSellerLoginCount;

    /**
     * 装维人员登录人数
     */
    private Integer decoratorLoginPersonCount;

    /**
     * 装维人员登录次数
     */
    private Integer decoratorLoginCount;

    /**
     * 营业厅人员登录人数
     */
    private Integer businessHallLoginPersonCount;

    /**
     * 营业厅人员登录次数
     */
    private Integer businessHallLoginCount;

    /**
     * 代理商人登录人数
     */
    private Integer agentBusinessLoginPersonCount;

    /**
     * 代理商人登录次数
     */
    private Integer agentBusinessLoginCount;

    /**
     * 运营人员登录人数
     */
    private Integer operatingPersonnelLoginPersonCount;

    /**
     * 运营人员登录次数
     */
    private Integer operatingPersonnelLoginCount;

    /**
     * 登录人数小记
     */
    private Integer loginPersonTotalCount;

    /**
     * 登录次数小记
     */
    private Integer loginTotalCount;

    /**
     * 登录人数日环比
     */
    private Double loginPersonDayPercent;

    /**
     * 登录次数日环比
     */
    private Double loginCountDayPercent;
}
