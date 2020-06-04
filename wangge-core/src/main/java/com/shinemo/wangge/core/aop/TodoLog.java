package com.shinemo.wangge.core.aop;

import java.lang.annotation.*;

/**
 * @Author shangkaihui
 * @Date 2020/6/4 17:01
 * @Desc
 */
@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TodoLog {
}
