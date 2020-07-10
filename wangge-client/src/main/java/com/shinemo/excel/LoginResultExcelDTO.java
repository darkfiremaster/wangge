package com.shinemo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

/**
 * @Author shangkaihui
 * @Date 2020/7/9 19:36
 * @Desc
 */
@Data
public class LoginResultExcelDTO extends BaseRowModel {

    @ExcelProperty("日期")
    private String statisticsTime;
    @ExcelProperty("地市")
    private String cityName;
    @ExcelProperty("区县")
    private String countyName;

    @ExcelProperty("网格长登录人数")
    private Integer gridCaptainLoginPersonCount;
    @ExcelProperty("网格长登录次数")
    private Integer gridCaptainLoginCount;

    @ExcelProperty("网格经理登录人数")
    private Integer gridManagerLoginPersonCount;
    @ExcelProperty("网格经理登录次数")
    private Integer gridManagerLoginCount;

    @ExcelProperty("直销员登录人数")
    private Integer directSellerLoginPersonCount;
    @ExcelProperty("直销员登录次数")
    private Integer directSellerLoginCount;

    @ExcelProperty("装维人员登录人数")
    private Integer decoratorLoginPersonCount;
    @ExcelProperty("装维人员登录次数")
    private Integer decoratorLoginCount;

    @ExcelProperty("营业厅登录人数")
    private Integer businessHallLoginPersonCount;
    @ExcelProperty("营业厅登录次数")
    private Integer businessHallLoginCount;

    @ExcelProperty("代理商人登录人数")
    private Integer agentBusinessLoginPersonCount;
    @ExcelProperty("代理商人登录次数")
    private Integer agentBusinessLoginCount;

    @ExcelProperty("运营人员登录人数")
    private Integer operatingPersonnelLoginPersonCount;
    @ExcelProperty("运营人员登录次数")
    private Integer operatingPersonnelLoginCount;

    @ExcelProperty("登录人数小计")
    private Integer loginPersonTotalCount;
    @ExcelProperty("登录次数小计")
    private Integer loginTotalCount;

    @ExcelProperty("登录人数日环比")
    private String loginPersonDayPercent;
    @ExcelProperty("登录次数日环比")
    private String loginCountDayPercent;
}
