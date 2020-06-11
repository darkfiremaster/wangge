package com.shinemo.operate.query;

import com.shinemo.smartgrid.domain.QueryBase;
import lombok.Data;

import java.util.Date;

/**
 * @Author shangkaihui
 * @Date 2020/6/10 10:12
 * @Desc
 */
@Data
public class LoginInfoResultQuery extends QueryBase {
    private Long id;
    private Date startTime;
    private Date endTime;
    private String statisticsTime;
    private String tableIndex;
}
