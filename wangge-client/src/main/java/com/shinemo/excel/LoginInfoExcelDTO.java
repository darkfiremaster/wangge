package com.shinemo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author shangkaihui
 * @Date 2020/7/9 19:36
 * @Desc
 */
@Data
public class LoginInfoExcelDTO extends BaseRowModel {

    @ExcelProperty("账号")
    private String mobile;
    @ExcelProperty("姓名")
    private String username;
    @ExcelProperty(value = "登录时间",format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operateTime;
    @ExcelProperty("归属地市")
    private String cityName;
    @ExcelProperty("归属区县")
    private String countyName;
    @ExcelProperty("所属网格")
    private String gridName;
    @ExcelProperty("所属角色")
    private String roleName;


}
