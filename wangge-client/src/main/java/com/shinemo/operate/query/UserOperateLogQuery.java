package com.shinemo.operate.query;

import com.shinemo.smartgrid.domain.QueryBase;
import lombok.Data;

import java.util.Date;

/**
 * @Author shangkaihui
 * @Date 2020/6/10 10:09
 * @Desc
 */
@Data
public class UserOperateLogQuery extends QueryBase {

    private Long id;

    private String tableIndex;

    private Date startTime;

    private Date endTime;

    private String mobile;

}
