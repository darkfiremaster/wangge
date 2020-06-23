package com.shinemo.smartgrid.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author shangkaihui
 * @Date 2020/6/23 16:17
 * @Desc
 */
@Data
public class UserInfoCache implements Serializable {
    protected static final long serialVersionUID = 4753210862863386037L;

    private String uid;

    private String userName;

    private String orgId;

    private String orgName;

    private String mobile;

    private String selectGridInfo;

    private String gridInfo;

}
