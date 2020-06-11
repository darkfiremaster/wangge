package com.shinemo.wangge.test.web;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.operate.vo.UserOperateLogVO;
import com.shinemo.wangge.core.service.operate.OperateService;
import com.shinemo.wangge.web.MainApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Author shangkaihui
 * @Date 2020/6/11 10:03
 * @Desc
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainApplication.class)
public class OperateServiceTest {

    @Resource
    private OperateService operateService;

    @Test
    public void testAddUserOperateLog() {
        UserOperateLogVO userOperateLogVO = new UserOperateLogVO();
        userOperateLogVO.setType(1);
        userOperateLogVO.setMobile("13588039023");
        userOperateLogVO.setUserName("skh");
        userOperateLogVO.setUid("1");

        ApiResult<Void> result = operateService.addUserOperateLog(userOperateLogVO);
        System.out.println("result = " + result);
    }


}
