package com.shinemo.smartgrid.domain.query;

import com.shinemo.client.common.QueryBase;

import lombok.Getter;
import lombok.Setter;

/**
 * @author htdong
 * @date 2020年6月15日 上午11:48:57
 */
@Setter
@Getter
public class BackdoorLoginQuery extends QueryBase {

    private static final long serialVersionUID = 2326705592840859887L;

    private Long id;
    private String mobile;
}
