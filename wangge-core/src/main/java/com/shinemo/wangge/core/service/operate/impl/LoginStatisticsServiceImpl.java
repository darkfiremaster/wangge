package com.shinemo.wangge.core.service.operate.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.operate.domain.LoginInfoResultDO;
import com.shinemo.operate.excel.LoginInfoExcelDTO;
import com.shinemo.operate.query.LoginInfoResultQuery;
import com.shinemo.wangge.core.service.operate.LoginStatisticsService;
import com.shinemo.wangge.dal.mapper.LoginInfoResultMapper;
import com.shinemo.wangge.dal.mapper.UserOperateLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author shangkaihui
 * @Date 2020/6/10 11:00
 * @Desc
 */
@Service
@Slf4j
public class LoginStatisticsServiceImpl implements LoginStatisticsService {

    @Resource
    private UserOperateLogMapper userOperateLogMapper;

    @Resource
    private LoginInfoResultMapper loginInfoResultMapper;

    @Override
    @Transactional
    public ApiResult<List<LoginInfoResultDO>> saveTodayLoginInfoResult() {
        LoginInfoResultQuery loginInfoResultQuery = new LoginInfoResultQuery();
        DateTime yesterday = DateUtil.yesterday();
        loginInfoResultQuery.setStartTime(DateUtil.beginOfDay(yesterday));
        loginInfoResultQuery.setEndTime(DateUtil.endOfDay(yesterday));
        List<LoginInfoResultDO> loginInfoResultDOList = loginInfoResultMapper.getLoginInfoResultDOList(loginInfoResultQuery);

        Map<String, LoginInfoResultDO> yesterdayLoginInfoResultMap = getYesterdayLoginInfoResultMap();

        for (LoginInfoResultDO loginInfoResultDO : loginInfoResultDOList) {
            //统计的日期是昨天
            loginInfoResultDO.setStatisticsTime(LocalDateTime.now().minusDays(1));
            //获取前天登录结果
            LoginInfoResultDO yesterdayLoginInfoResult = yesterdayLoginInfoResultMap.get(loginInfoResultDO.getCityCode() + loginInfoResultDO.getCountyCode());
            if (yesterdayLoginInfoResult != null && !Objects.equals(0, yesterdayLoginInfoResult.getLoginTotalCount()) && Objects.equals(0, yesterdayLoginInfoResult.getLoginPersonTotalCount())) {
                Integer oldLoginTotalCount = yesterdayLoginInfoResult.getLoginTotalCount();
                Integer oldLoginPersonTotalCount = yesterdayLoginInfoResult.getLoginPersonTotalCount();
                //计算日环比: (今日-昨日)/昨日
                BigDecimal todayLoginCountDayPercent = NumberUtil.div(String.valueOf(loginInfoResultDO.getLoginTotalCount() - oldLoginTotalCount), String.valueOf(oldLoginTotalCount), 2);
                loginInfoResultDO.setLoginCountDayPercent(todayLoginCountDayPercent.doubleValue());
                BigDecimal todayLoginPersonDayPercent = NumberUtil.div(String.valueOf(loginInfoResultDO.getLoginPersonTotalCount() - oldLoginPersonTotalCount), String.valueOf(oldLoginPersonTotalCount), 2);
                loginInfoResultDO.setLoginPersonDayPercent(todayLoginPersonDayPercent.doubleValue());
            }
        }

        for (int i = 0; i < loginInfoResultDOList.size(); i += 50) {
            loginInfoResultMapper.insertBatch(loginInfoResultDOList.subList(i, Math.min(i + 50, loginInfoResultDOList.size())));
        }

        return ApiResult.of(0, loginInfoResultDOList);
    }

    private Map<String, LoginInfoResultDO> getYesterdayLoginInfoResultMap() {
        List<LoginInfoResultDO> yesterdayLoginInfoResultDOList = getYesterdayLoginInfoResultList();

        return yesterdayLoginInfoResultDOList.stream()
                .collect(Collectors.toMap(v -> v.getCityCode() + v.getCityCode(), a -> a, (k1, k2) -> k1));
    }

    private List<LoginInfoResultDO> getYesterdayLoginInfoResultList() {
        List<LoginInfoResultDO> loginInfoResultDOS = loginInfoResultMapper.getYesterdayLoginInfoResultList();
        return loginInfoResultDOS;
    }


    @Override
    public ApiResult<List<LoginInfoExcelDTO>> getLoginInfoExcelDTOList() {
        List<LoginInfoExcelDTO> loginInfoExcelDTOList = userOperateLogMapper.getLoginInfoExcelDTOList();
        return ApiResult.of(0, loginInfoExcelDTOList);
    }
}


