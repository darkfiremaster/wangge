package com.shinemo.wangge.web.controller.common;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.operate.vo.UserOperateLogVO;
import com.shinemo.wangge.core.service.operate.OperateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author shangkaihui
 * @Date 2020/6/9 10:16
 * @Desc
 */
@RestController
@Slf4j
@RequestMapping("/operate")
public class OperateController {

    @Autowired
    private OperateService operateService;

    @PostMapping("/addUserOperateLog")
    public ApiResult addUserOperateLog(@RequestBody UserOperateLogVO userOperateLogVO) {
        return operateService.addUserOperateLog(userOperateLogVO);
    }


}
