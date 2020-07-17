package com.shinemo.wangge.core.service.common.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailUtil;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.excel.LoginInfoExcelDTO;
import com.shinemo.excel.LoginResultExcelDTO;
import com.shinemo.wangge.core.service.common.ExcelService;
import com.shinemo.wangge.dal.slave.mapper.SlaveLoginInfoResultMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/7/17 13:44
 * @Desc
 */
@Service
@Slf4j
public class ExcelServiceImpl implements ExcelService {

    /**
     * 收件人地址
     */
    @NacosValue(value = "${recipientAddress}", autoRefreshed = true)
    private String recipientAddress = "shangkh@shinemo.com";

    /**
     * 抄送人地址
     */
    @NacosValue(value = "${copyAddress}", autoRefreshed = true)
    private String copyAddress = "shangkh@shinemo.com";

    private String filePath = "/data/logs/excel/";

    private String fileSuffix = ".xlsx";

    @Resource
    private SlaveLoginInfoResultMapper slaveLoginInfoResultMapper;

    @Override
    public void sendLoginInfoMail(String queryDate) {

        List<LoginInfoExcelDTO> loginInfoExcelDTOList = getLoginInfoExcelDTOList(queryDate);
        File loginInfoFile = getLoginInfoFile(queryDate, loginInfoExcelDTOList);

        List<LoginResultExcelDTO> loginResultExcelDTOList = getLoginResultExcelDTOList(queryDate);
        File loginResultFile = getLoginResultFile(queryDate, loginResultExcelDTOList);

        String subject = queryDate + "智慧网格小屏登录情况统计";
        String content = "附件为" + queryDate + "智慧网格小屏登录情况统计,请查收.";
        List<String> recipientAddressList = StrUtil.splitTrim(recipientAddress, ',');
        List<String> copyAddressList = StrUtil.splitTrim(copyAddress, ',');
        MailUtil.send(recipientAddressList, copyAddressList, null, subject, content, false, loginInfoFile, loginResultFile);

        log.info("[sendLoginInfoMail] 发送邮件excel成功");

        FileUtil.del(loginInfoFile);
        FileUtil.del(loginResultFile);

        log.info("[sendLoginInfoMail] 删除文件成功");

    }

    private File getLoginInfoFile(String queryDate, List<LoginInfoExcelDTO> loginInfoExcelDTOList) {
        String fileName = filePath + queryDate + "登录信息统计" + fileSuffix;
        File file = FileUtil.file(fileName);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            log.error("[getLoginInfoFile] 文件不存在,fileName:{}", fileName);
            throw new ApiException("文件不存在", e);
        }
        ExcelWriter writer = EasyExcelFactory.getWriter(fileOutputStream);
        Sheet sheet = new Sheet(1, 0, LoginInfoExcelDTO.class);
        sheet.setSheetName(queryDate + "登录信息统计");
        writer.write(loginInfoExcelDTOList, sheet);
        writer.finish();
        return file;
    }

    private File getLoginResultFile(String queryDate, List<LoginResultExcelDTO> loginResultExcelDTOList) {
        String fileName = filePath + queryDate + "登录结果统计" + fileSuffix;
        File file = FileUtil.file(fileName);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            log.error("[getLoginResultFile] 文件不存在,fileName:{}", fileName);
            throw new ApiException("文件不存在", e);
        }
        ExcelWriter writer = EasyExcelFactory.getWriter(fileOutputStream);
        Sheet sheet = new Sheet(1, 0, LoginResultExcelDTO.class);
        sheet.setSheetName(queryDate + "登录结果统计");
        writer.write(loginResultExcelDTOList, sheet);
        writer.finish();
        return file;
    }

    public List<LoginInfoExcelDTO> getLoginInfoExcelDTOList(String queryDate) {
        String date = queryDate;
        Assert.notBlank(date, "日期不能为空,格式为yyyy-MM-dd");
        String[] split = date.split("-");
        String tableIndex = split[1];//该表按月分表所以需要先获取月份
        List<LoginInfoExcelDTO> loginInfoExcelDTOList = slaveLoginInfoResultMapper.getLoginInfoExcelDTOList(date, tableIndex);
        log.info("[getLoginInfoExcelDTOList] 结果集数量:{}", loginInfoExcelDTOList.size());
        return loginInfoExcelDTOList;
    }

    public List<LoginResultExcelDTO> getLoginResultExcelDTOList(String queryDate) {
        String date = queryDate;
        Assert.notBlank(date, "日期不能为空,格式为yyyy-MM-dd");
        List<LoginResultExcelDTO> loginResultExcelDTOList = slaveLoginInfoResultMapper.getLoginResultExcelDTOList(date);
        log.info("[getLoginResultExcelDTOList] 结果集数量:{}", loginResultExcelDTOList.size());
        return loginResultExcelDTOList;
    }
}
