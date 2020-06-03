package com.shinemo.smartgrid.domain.model;

import java.util.Date;

import com.shinemo.client.common.BaseDO;

import lombok.Getter;
import lombok.Setter;

/**
 * @author htdong
 * @date 2020年6月2日 下午5:23:41
 */
@Setter
@Getter
public class UserConfigDO extends BaseDO {

    private static final long serialVersionUID = -3151035287940764635L;

    private Long id;
    private Date gmtCreate;
    private Date gmtModified;
    private String userId;
    private String gridBiz;
}