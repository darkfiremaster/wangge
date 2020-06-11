package com.shinemo.gridinfo.domain.query;

import com.shinemo.client.common.QueryBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * 类说明: 网格信息查询条件
 *
 * @author 曾鹏
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SmartGridInfoQuery extends QueryBase {
    private Long id;

    /**
     * 网格编码
     */
    private String gridId;


}
