package com.shinemo.gridinfo.domain.model;

import com.shinemo.client.common.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * 类说明: 网格信息实体类
 *
 * @author 曾鹏
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SmartGridInfoDO extends BaseDO {

    private Long id;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 网格名称
     */
    private String gridName;

    /**
     * 网格编码
     */
    private String gridId;

    /**
     * 市编码
     */
    private String cityCode;

    /**
     * 市名称
     */
    private String cityName;

    /**
     * 区编码
     */
    private String countyCode;

    /**
     * 区名称
     */
    private String countyName;
}
