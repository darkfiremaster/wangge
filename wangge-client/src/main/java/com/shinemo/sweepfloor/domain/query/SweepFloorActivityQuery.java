package com.shinemo.sweepfloor.domain.query;

import com.shinemo.common.tools.query.Query;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SweepFloorActivityQuery extends Query {
    /**
     * 状态
     */
    private Integer status;

    List<Integer> statusList;

    private Long id;

    private List<Long> ids;

    /** 创建人 */
    private String creator;

    private String mobile;

    private Date startTime;
    private Date endTime;
    private String gridId;
    /** 是否根据创建时间过滤 */
    private Date createTime;
}
