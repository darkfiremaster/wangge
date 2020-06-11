package com.shinemo.wangge.test.web;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.operate.domain.LoginInfoResultDO;
import com.shinemo.operate.excel.LoginInfoExcelDTO;
import com.shinemo.wangge.core.service.operate.LoginStatisticsService;
import com.shinemo.wangge.web.MainApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/11 10:03
 * @Desc
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainApplication.class)
public class LoginStatisticsServiceTest {

    @Resource
    private LoginStatisticsService loginStatisticsService;

    @Test
    public void testGetLoginInfoExcelDTOList() {
        ApiResult<List<LoginInfoExcelDTO>> loginInfoExcelDTOList = loginStatisticsService.getLoginInfoExcelDTOList();
        System.out.println("loginInfoExcelDTOList = " + loginInfoExcelDTOList);
    }

    @Test
    public void testSaveYesterdayLoginInfoResult() {
        ApiResult<List<LoginInfoResultDO>> result = loginStatisticsService.saveYesterdayLoginInfoResult();
        System.out.println("result = " + result);
    }

}
