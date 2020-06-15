package com.shinemo.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Author shangkaihui
 * @Date 2020/6/10 10:10
 * @Desc
 */
@Data
public class UserGridRoleDO {

    private Long id;
    private Date gmtCreate;
    private Date gmtModified;

    private String mobile;

    private String cityName;

    private String cityCode;

    private String countyName;

    private String countyCode;

    private String gridName;

    private String gridId;

    private String roleName;

    private String roleId;
}
