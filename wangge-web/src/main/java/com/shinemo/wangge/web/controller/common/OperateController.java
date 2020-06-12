package com.shinemo.wangge.web.controller.common;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.operate.vo.UserOperateLogVO;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.wangge.core.service.operate.OperateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

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

    @Resource
    private ThreadPoolTaskExecutor asyncServiceExecutor;

    @PostMapping("/addUserOperateLog")
    public ApiResult addUserOperateLog(@RequestBody UserOperateLogVO userOperateLogVO) {
        log.info("[addUserOperateLog] 新增操作日志");
        userOperateLogVO.setMobile(SmartGridContext.getMobile());
        userOperateLogVO.setUid(SmartGridContext.getUid());
        userOperateLogVO.setUserName(SmartGridContext.getUserName());
        asyncServiceExecutor.submit(() -> operateService.addUserOperateLog(userOperateLogVO));
        return ApiResult.of(0);
    }

}
