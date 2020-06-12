package com.shinemo.wangge.test.web;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.stallup.domain.huawei.GetGridUserInfoResult;
import com.shinemo.stallup.domain.request.HuaWeiRequest;
import com.shinemo.wangge.core.service.stallup.HuaWeiService;
import com.shinemo.wangge.core.service.user.UserService;
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
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Resource
    private HuaWeiService huaWeiService;

    //@Test
    //public void testAddUserOperateLog() {
    //
    //    List<GridUserRoleDetail> mockUserRoleDetail = userService.getMockUserRoleDetail();
    //    ApiResult<Void> result = userService.updateUserGridRoleRelation(mockUserRoleDetail, "13607713224");
    //    System.out.println("result = " + result);
    //}


    @Test
    public void testUserInfo() {
        ApiResult<GetGridUserInfoResult.DataBean> result = huaWeiService.getGridUserInfoDetail(HuaWeiRequest.builder().mobile("13607713224").build());
        System.out.println("result = " + result);
    }
}
