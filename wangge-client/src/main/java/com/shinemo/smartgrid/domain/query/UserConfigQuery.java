package com.shinemo.smartgrid.domain.query;

import com.shinemo.client.common.QueryBase;

import lombok.Getter;
import lombok.Setter;

/**
 * @author htdong
 * @date 2020年6月2日 下午5:23:52
 */
@Setter
@Getter
public class UserConfigQuery extends QueryBase {

    private static final long serialVersionUID = -978340157367648446L;

    private Long id;
    private String userId;
}