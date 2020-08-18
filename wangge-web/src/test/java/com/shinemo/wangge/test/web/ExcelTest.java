package com.shinemo.wangge.test.web;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.shinemo.excel.LoginInfoExcelDTO;
import com.shinemo.excel.LoginResultExcelDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/7/10 11:52
 * @Desc
 */
@Slf4j
public class ExcelTest {

    private static final String onlineDomin = "https://api-gx.uban360.com";

    private String queryDate = "2020-07-16";


    @Test
    public void exportLoginInfoExcel() throws FileNotFoundException {
        String date = queryDate;
        Assert.notBlank(date, "日期不能为空,格式为yyyy-MM-dd");
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("date", date);
        String url = onlineDomin + "/cmgr-gx-smartgrid/excel/getLoginInfoDTOList";
        String res = HttpUtil.get(url, paramMap);
        JSONObject jsonObject = JSONUtil.parseObj(res);
        JSONArray data = JSONUtil.parseArray(JSONUtil.toJsonStr(jsonObject.get("data")));
        List<LoginInfoExcelDTO> loginInfoExcelDTOS = data.toList(LoginInfoExcelDTO.class);
        log.info("[exportLoginInfoExcel] 请求地址:{},结果集数量:{}", url, loginInfoExcelDTOS.size());
        String[] split = StrUtil.split(date, "-");
        date = split[1] + split[2];
        String fileName = "/Users/cindy/Desktop/" + date + "登录信息统计" + ".xlsx";
        File file = FileUtil.file(fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ExcelWriter writer = EasyExcelFactory.getWriter(fileOutputStream);
        Sheet sheet = new Sheet(1, 0, LoginInfoExcelDTO.class);
        sheet.setSheetName(date + "登录信息统计");
        writer.write(loginInfoExcelDTOS, sheet);
        writer.finish();
        log.info("[exportLoginInfoExcel] 导出登录信息excel成功");
    }

    /**
     * 获取正式环境登录结果统计excel
     */
    @Test
    public void exportLoginResultExcel() throws FileNotFoundException {
        String date = queryDate;
        Assert.notBlank(date, "日期不能为空,格式为yyyy-MM-dd");
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("date", date);
        String url = onlineDomin + "/cmgr-gx-smartgrid/excel/getLoginResultDTOList";
        String res = HttpUtil.get(url, paramMap);
        JSONObject jsonObject = JSONUtil.parseObj(res);
        JSONArray data = JSONUtil.parseArray(JSONUtil.toJsonStr(jsonObject.get("data")));
        List<LoginResultExcelDTO> loginResultExcelDTOList = data.toList(LoginResultExcelDTO.class);
        log.info("[exportLoginResultExcel] 请求地址:{},结果集数量:{}", url, loginResultExcelDTOList.size());
        String[] split = StrUtil.split(date, "-");
        date = split[1] + split[2];
        String fileName = "/Users/cindy/Desktop/" + date + "登录结果统计" + ".xlsx";
        File file = FileUtil.file(fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ExcelWriter writer = EasyExcelFactory.getWriter(fileOutputStream);
        Sheet sheet = new Sheet(1, 0, LoginResultExcelDTO.class);
        sheet.setSheetName(date + "登录结果统计");
        writer.write(loginResultExcelDTOList, sheet);
        writer.finish();

        log.info("[exportLoginInfoExcel] 导出登录结果excel成功");
    }


    @Test
    public void test(){
        System.out.println(System.currentTimeMillis());
    }

}
