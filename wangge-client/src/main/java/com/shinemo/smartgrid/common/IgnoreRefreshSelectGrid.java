package com.shinemo.smartgrid.common;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreRefreshSelectGrid {
}