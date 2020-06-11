package com.shinemo.operate.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

import java.util.Date;

/**
 * @Author shangkaihui
 * @Date 2020/6/9 19:13
 * @Desc
 */
@Data
public class LoginInfoExcelDTO extends BaseRowModel {

    @ExcelProperty(value = "账号")
    private String mobile;

    @ExcelProperty(value = "姓名")
    private String username;

    @ExcelProperty(value = "角色")
    private String roleName;

    @ExcelProperty(value = "归属地市")
    private String cityName;

    @ExcelProperty(value = "归属区县")
    private String countyName;

    @ExcelProperty(value = "归属网格")
    private String gridName;

    @ExcelProperty(value = "登录时间")
    private Date loginTime;
}
