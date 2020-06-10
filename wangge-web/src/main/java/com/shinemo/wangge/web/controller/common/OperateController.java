package com.shinemo.wangge.web.controller.common;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.operate.vo.UserOperateLogVO;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.wangge.core.service.operate.OperateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author shangkaihui
 * @Date 2020/6/9 10:16
 * @Desc
 */
@RestController
@Slf4j
@RequestMapping("/operate")
public class OperateController {

    @Resource
    private OperateService operateService;

    @PostMapping("/addUserOperateLog")
    public ApiResult addUserOperateLog(@RequestBody UserOperateLogVO userOperateLogVO) {
        userOperateLogVO.setMobile(SmartGridContext.getMobile());
        userOperateLogVO.setUid(SmartGridContext.getUid());
        userOperateLogVO.setUserName(SmartGridContext.getUserName());
        return operateService.addUserOperateLog(userOperateLogVO);
    }


}
