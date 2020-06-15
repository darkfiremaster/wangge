package com.shinemo.smartgrid.domain.model;

import java.util.Date;

import com.shinemo.client.common.BaseDO;

import lombok.Getter;
import lombok.Setter;

/**
 * @author htdong
 * @date 2020年6月15日 上午11:46:51
 */
@Setter
@Getter
public class BackdoorLoginDO extends BaseDO {

    private static final long serialVersionUID = 7446713457368746954L;

    private Long id;
    private Date gmtCreate;
    private Date gmtModified;
    private String mobile;
    private String cUid;
    private String cOrgId;
    private String cOrgName;
    private String cUserName;
    private String cMobile;
}