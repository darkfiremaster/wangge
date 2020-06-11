package com.shinemo.wangge.web.controller.gridinfo;

import com.alibaba.excel.util.StringUtils;
import com.shinemo.common.annotation.SmIgnore;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.gridinfo.domain.model.SmartGridInfoDO;
import com.shinemo.stallup.domain.response.SearchResponse;
import com.shinemo.wangge.core.service.gridinfo.SmartGridInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 类说明: 网格信息
 *
 * @author 曾鹏
 */
@RestController
@RequestMapping("gridInfo")
@Slf4j
public class SmartGridInfoController {

    @Resource
    private SmartGridInfoService smartGridInfoService;

    @GetMapping("search")
    @SmIgnore
    public ApiResult<SmartGridInfoDO> search(@RequestParam String gridId) {
        if (!StringUtils.hasText(gridId)) {
            return ApiResult.of(0);
        }
        return smartGridInfoService.getByGridId(gridId);
    }
}
