package com.shinemo.wangge.web.controller.targetcustomer;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.targetcustomer.domain.request.TargetCustomerRequest;
import com.shinemo.targetcustomer.domain.response.TargetCustomerResponse;
import com.shinemo.wangge.core.service.targetcustomer.TargetIndexMobileService;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 类说明:
 *
 * @author 曾鹏
 */
@RestController
@RequestMapping("/targetcustomer")
public class TargetCustomerController {

    @Resource
    private TargetIndexMobileService targetIndexMobileService;

    @GetMapping("/getByMobile")
    public ApiResult<TargetCustomerResponse> getByMobile(@RequestBody TargetCustomerRequest request){
        Assert.notNull(request,"request is null");
        Assert.hasText(request.getMobile(),"mobile is null");

        return targetIndexMobileService.findByMobile(request.getMobile());
    }


}
