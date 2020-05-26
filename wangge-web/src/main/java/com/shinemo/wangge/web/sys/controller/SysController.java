package com.shinemo.wangge.web.sys.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author htdong
 * @date 2020年5月26日 下午6:06:44
 */
@RestController
public class SysController {

    @GetMapping("checkstatus")
    public String checkstatus() {
        return "success";
    }
}