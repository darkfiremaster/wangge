package com.shinemo.smartgrid.domain;

import com.shinemo.stallup.domain.model.GridUserRoleDetail;
import lombok.Data;

import java.util.List;

/**
 * 网格信息token
 */
@Data
public class GridInfoToken {
    /** 所有网格信息集合 */
    private List<GridUserRoleDetail> gridList;

    private GridUserRoleDetail gridDetail;
}
