package com.shinemo.todo.query;

import com.shinemo.smartgrid.domain.QueryBase;
import lombok.Data;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 14:17
 * @Desc
 */
@Data
public class TodoTypeQuery extends QueryBase {
    private Long id;

    private Integer status;
}
