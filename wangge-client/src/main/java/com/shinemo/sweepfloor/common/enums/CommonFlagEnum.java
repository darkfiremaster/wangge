package com.shinemo.sweepfloor.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
  通用标识：0、1
 */
@Getter
@AllArgsConstructor
public enum CommonFlagEnum {
    ZERO_FLAG(0, "zero_flag"),
    ONE_FLAG(1, "one_flag"),
    ;
    private final int id;
    private final String desc;
}
