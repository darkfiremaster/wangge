package com.shinemo.wangge.core.service.redirect;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.ShowTabVO;

/**
 * @Author shangkaihui
 * @Date 2020/7/15 16:43
 * @Desc
 */
public interface RedirectService {

    ApiResult<String> getRedirectUrl(Integer type);

    ApiResult<ShowTabVO> showTab();
}
