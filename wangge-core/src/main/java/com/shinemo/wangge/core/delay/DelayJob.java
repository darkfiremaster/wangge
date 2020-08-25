package com.shinemo.wangge.core.delay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DelayJob implements Serializable {

    private static final long serialVersionUID = -7558558883792921429L;

    private Map<String,Object> jobParams;//job执行参数
    private String executeTime;//执行时间
    private Class clazz;//具体执行实例实现
}