package com.shinemo.sweepfloor.domain.vo;

import com.shinemo.stallup.domain.model.StallUpBizType;
import lombok.Data;

import java.util.List;

@Data
public class SweepFloorBizListVO {
    private List<StallUpBizType> sweepFloorBizList;

    private List<StallUpBizType> sweepFloorList;

}
