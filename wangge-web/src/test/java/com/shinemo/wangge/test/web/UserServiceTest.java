package com.shinemo.wangge.test.web;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;
import com.shinemo.wangge.core.service.user.UserService;
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
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUserOperateLog() {
        List<GridUserRoleDetail> mockUserRoleDetail = userService.getMockUserRoleDetail();

        ApiResult<Void> result = userService.updateUserGridRoleRelation(mockUserRoleDetail, "13607713224");
        System.out.println("result = " + result);
    }



}
