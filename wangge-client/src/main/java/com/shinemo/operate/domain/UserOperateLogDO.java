package com.shinemo.operate.domain;

import com.shinemo.stallup.domain.utils.SubTableUtils;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

/**
 * @Author shangkaihui
 * @Date 2020/6/10 10:09
 * @Desc
 */
@Data
public class UserOperateLogDO {

    private Long id;
    private Date gmtCreate;
    private Date gmtModified;
    private String tableIndex = SubTableUtils.getTableIndexByOnlyMonth(LocalDate.now());

    private String mobile;

    private String uid;

    private String username;

    private Date operateTime;

    private Integer type;
}


