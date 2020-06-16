package com.shinemo.wangge.web.sys.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author htdong
 * @date 2020年5月26日 下午6:06:44
 */
@RestController
@Slf4j
public class SysController {

    @GetMapping("checkstatus")
    public String checkstatus() {
        log.info("测试环境项目启动成功");
        return "success";
    }

}