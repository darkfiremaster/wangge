package com.shinemo.wangge.web.controller.common;

import cn.hutool.core.lang.Assert;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.excel.LoginInfoExcelDTO;
import com.shinemo.excel.LoginResultExcelDTO;
import com.shinemo.wangge.dal.slave.mapper.SlaveLoginInfoResultMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/7/9 19:28
 * @Desc
 */
@RequestMapping("/excel")
@RestController
@Slf4j
public class ExcelController {

    @Resource
    private SlaveLoginInfoResultMapper slaveLoginInfoResultMapper;

    /**
     * 获取登录结果列表
     *
     * @param date
     * @return
     */
    @GetMapping("/getLoginResultDTOList")
    public ApiResult<List<LoginResultExcelDTO>> getLoginResultDTOList(String date) {
        Assert.notBlank(date, "日期不能为空,格式为yyyy-MM-dd");
        List<LoginResultExcelDTO> loginResultExcelDTOList = slaveLoginInfoResultMapper.getLoginResultExcelDTOList(date);
        log.info("[getLoginResultDTOList] 获取登录结果列表,date:{},结果集数量:{}", date, loginResultExcelDTOList.size());
        return ApiResult.of(0, loginResultExcelDTOList);
    }

    /**
     * 获取登录信息列表
     */
    @GetMapping("/getLoginInfoDTOList")
    public ApiResult<List<LoginInfoExcelDTO>> getLoginInfoDTOList(String date) {
        Assert.notBlank(date, "日期不能为空,格式为yyyy-MM-dd");
        String[] split = date.split("-");
        String tableIndex = split[1];//该表按月分表所以需要先获取月份
        List<LoginInfoExcelDTO> loginInfoExcelDTOList = slaveLoginInfoResultMapper.getLoginInfoExcelDTOList(date, tableIndex);
        log.info("[getLoginResultDTOList] 获取登录信息列表,date:{},结果集数量:{}", date, loginInfoExcelDTOList.size());
        return ApiResult.of(0, loginInfoExcelDTOList);
    }

    ///**
    // * 获取正式环境登录结果统计excel
    // */
    //@GetMapping("/exportLoginResultExcel")
    //public String exportLoginResultExcel(String date, HttpServletResponse response) {
    //    Assert.notBlank(date, "日期不能为空,格式为yyyy-MM-dd");
    //    //List<LoginResultExcelDTO> loginResultExcelDTOList = slaveLoginInfoResultMapper.getLoginResultExcelDTOList(date);
    //    HashMap<String, Object> paramMap = new HashMap<>();
    //    paramMap.put("date", date);
    //    String url = testDomin + "/cmgr-gx-smartgrid/excel/getLoginResultDTOList";
    //    String res = HttpUtil.get(url, paramMap);
    //    JSONObject jsonObject = JSONUtil.parseObj(res);
    //    JSONArray data = JSONUtil.parseArray(JSONUtil.toJsonStr(jsonObject.get("data")));
    //    List<LoginResultExcelDTO> loginResultExcelDTOList = data.toList(LoginResultExcelDTO.class);
    //    log.info("[exportLoginResultExcel] 请求地址:{},结果集数量:{}", url, loginResultExcelDTOList.size());
    //    try {
    //        ExcelUtil.writeExcel(response, loginResultExcelDTOList, "登录结果统计", "登录结果统计",
    //                ExcelTypeEnum.XLSX, LoginResultExcelDTO.class);
    //    } catch (ExcelException e) {
    //        throw new ApiException("导出excel异常", e);
    //    }
    //    log.info("[exportLoginInfoExcel] 导出登录结果excel成功");
    //    return "success";
    //}



    ///**
    // * 获取正式环境登录统计信息excel
    // */
    //@GetMapping("/exportLoginInfoExcel")
    //public String exportLoginInfoExcel(String date, HttpServletResponse response) throws FileNotFoundException {
    //    Assert.notBlank(date, "日期不能为空,格式为yyyy-MM-dd");
    //    HashMap<String, Object> paramMap = new HashMap<>();
    //    paramMap.put("date", date);
    //    String url = devDomin + "/cmgr-gx-smartgrid/excel/getLoginInfoDTOList";
    //    String res = HttpUtil.get(url, paramMap);
    //    JSONObject jsonObject = JSONUtil.parseObj(res);
    //    JSONArray data = JSONUtil.parseArray(JSONUtil.toJsonStr(jsonObject.get("data")));
    //    List<LoginInfoExcelDTO> loginInfoExcelDTOS = data.toList(LoginInfoExcelDTO.class);
    //
    //    log.info("[exportLoginInfoExcel] 请求地址:{},结果集数量:{}", url, loginInfoExcelDTOS.size());
    //
    //    File file = FileUtil.file("/Users/cindy/Desktop/" + "writeSimple" + System.currentTimeMillis()+".xlsx");
    //    FileOutputStream fileOutputStream = new FileOutputStream(file);
    //    ExcelWriter writer = EasyExcelFactory.getWriter(fileOutputStream);
    //    Sheet sheet = new Sheet(1, 0, LoginInfoExcelDTO.class);
    //    writer.write(loginInfoExcelDTOS,sheet);
    //    writer.finish();
    //    //try {
    //    //    ExcelUtil.writeExcel(response, loginInfoExcelDTOS, "登录信息统计", "登录信息统计",
    //    //            ExcelTypeEnum.XLSX, LoginInfoExcelDTO.class);
    //    //} catch (ExcelException e) {
    //    //    throw new ApiException("导出excel异常", e);
    //    //}
    //    log.info("[exportLoginInfoExcel] 导出登录信息excel成功");
    //    return "success";
    //}
}
