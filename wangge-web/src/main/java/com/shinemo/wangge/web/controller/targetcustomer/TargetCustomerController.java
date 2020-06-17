package com.shinemo.wangge.web.controller.targetcustomer;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.targetcustomer.domain.request.TargetCustomerRequest;
import com.shinemo.targetcustomer.domain.response.TargetCustomerResponse;
import com.shinemo.wangge.core.service.targetcustomer.TargetIndexMobileService;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

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
    public ApiResult<TargetCustomerResponse> getByMobile(){
        String mobile = SmartGridContext.getMobile();
        return targetIndexMobileService.findByMobile(mobile);
    }


}
