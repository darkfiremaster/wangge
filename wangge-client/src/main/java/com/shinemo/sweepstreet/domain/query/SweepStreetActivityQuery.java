package com.shinemo.sweepstreet.domain.query;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.shinemo.client.common.QueryBase;

/**
 * 查询类
 * @ClassName: TSweepStreetActivityQuery
 * @author Zeng Peng
 * @Date 2020-08-13 15:35:20
 */
@Getter
@Setter
public class SweepStreetActivityQuery extends QueryBase {

    private Long id;
    /**
     * 父扫街活动id
     */
    private Long parentId;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 修改时间
     */
    private Date gmtModified;
    /**
     * 扫街活动标题
     */
    private String title;
    /**
     * 创建人id
     */
    private Long creatorId;
    /**
     * 创建人名称
     */
    private String creatorName;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 名称
     */
    private String name;
    /**
     * 计划开始时间
     */
    private Date planStartTime;
    /**
     * 计划结束时间
     */
    private Date planEndTime;
    /**
     * 实际开始时间
     */
    private Date realStartTime;
    /**
     * 实际结束时间
     */
    private Date realEndTime;
    /**
     * 坐标
     */
    private String location;
    /**
     * 状态 0-待开始 1-已开始 2-已开始 3-已结束 4-超时自动结束 5-异常结束
     */
    private Integer status;
    /**
     * 网格id
     */
    private String gridId;

    private Date endFilterStartTime;
    private Date endFilterEndTime;
    private List<Integer> statusList;
}
