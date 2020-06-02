package com.shinemo.sweepfloor.domain.query;


import lombok.Data;

@Data
public class UpdateSweepFloorStatusQuery {
    private Long id;
    /**
     */
    private Integer status;
}
