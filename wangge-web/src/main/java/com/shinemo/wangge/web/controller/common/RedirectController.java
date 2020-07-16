package com.shinemo.wangge.web.controller.common;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.ShowTabVO;
import com.shinemo.wangge.core.service.redirect.RedirectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author shangkaihui
 * @Date 2020/7/15 16:38
 * @Desc 跳转第三方
 */
@RestController
@RequestMapping("redirect")
@Slf4j
public class RedirectController {


    @Resource
    private RedirectService redirectService;

    @GetMapping("/getRedirectUrl")
    public ApiResult<String> getRedirectUrl(Integer type) {
        Assert.notNull(type, "type is null");
        return redirectService.getRedirectUrl(type);
    }

    @GetMapping("/showTab")
    public ApiResult<ShowTabVO> showTab() {
        return redirectService.showTab();
    }
}
