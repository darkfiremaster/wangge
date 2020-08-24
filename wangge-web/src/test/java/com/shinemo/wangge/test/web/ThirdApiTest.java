package com.shinemo.wangge.test.web;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.wangge.core.service.sweepstreet.SweepStreetService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingV2Service;
import com.shinemo.wangge.web.MainApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author shangkaihui
 * @Date 2020/8/17 16:55
 * @Desc
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainApplication.class)
public class ThirdApiTest {

    @Resource
    private ThirdApiMappingV2Service thirdApiMappingV2Service;

    @Resource
    private SweepStreetService sweepStreetService;

    @Test
    public void test1() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("mobile", "13588039023");
        ApiResult<Map<String, Object>> result = thirdApiMappingV2Service.dispatch(map, "getGroupList");
        System.out.println("result = " + result);

    }

    @Test
    public void testGetBusinessDetailUrl() {
        ApiResult<String> result = sweepStreetService.getBusinessDetailUrl("G7712423433", "108.3805869280,22.76371734557");
        System.out.println("result = " + result);

    }
}
