package com.shinemo.wangge.core.service.operate.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.operate.domain.LoginInfoResultDO;
import com.shinemo.operate.query.LoginInfoResultQuery;
import com.shinemo.stallup.domain.utils.SubTableUtils;
import com.shinemo.wangge.core.service.operate.LoginStatisticsService;
import com.shinemo.wangge.dal.mapper.LoginInfoResultMapper;
import com.shinemo.wangge.dal.slave.mapper.SlaveLoginInfoResultMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
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
    private LoginInfoResultMapper loginInfoResultMapper;

    @Resource
    private SlaveLoginInfoResultMapper slaveLoginInfoResultMapper;

    @Override
    public ApiResult<List<LoginInfoResultDO>> saveYesterdayLoginInfoResult() {
        LoginInfoResultQuery loginInfoResultQuery = new LoginInfoResultQuery();
        DateTime yesterday = DateUtil.yesterday();
        loginInfoResultQuery.setStartTime(DateUtil.beginOfDay(yesterday));
        loginInfoResultQuery.setEndTime(DateUtil.endOfDay(yesterday));
        loginInfoResultQuery.setTableIndex(SubTableUtils.getTableIndexByOnlyMonth(LocalDate.now().minusDays(1)));
        List<LoginInfoResultDO> loginInfoResultDOList = slaveLoginInfoResultMapper.getLoginInfoResultDOList(loginInfoResultQuery);

        //获取前天登录信息结果集
        Map<String, LoginInfoResultDO> beforeYesterdayLoginInfoResultMap = getBeforeYesterdayLoginInfoResultMap();

        for (LoginInfoResultDO loginInfoResultDO : loginInfoResultDOList) {
            //统计的日期是昨天
            loginInfoResultDO.setStatisticsTime(LocalDateTime.now().minusDays(1));
            //获取前天登录结果
            LoginInfoResultDO beforeYesterdayLoginInfoResult = beforeYesterdayLoginInfoResultMap.get(loginInfoResultDO.getCityName() + loginInfoResultDO.getCountyName());
            if (dataIsValid(beforeYesterdayLoginInfoResult)) {
                Integer oldLoginTotalCount = beforeYesterdayLoginInfoResult.getLoginTotalCount();
                Integer oldLoginPersonTotalCount = beforeYesterdayLoginInfoResult.getLoginPersonTotalCount();
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

    private boolean dataIsValid(LoginInfoResultDO yesterdayLoginInfoResult) {
        return yesterdayLoginInfoResult != null && !Objects.equals(0, yesterdayLoginInfoResult.getLoginTotalCount()) && !Objects.equals(0, yesterdayLoginInfoResult.getLoginPersonTotalCount());
    }

    private Map<String, LoginInfoResultDO> getBeforeYesterdayLoginInfoResultMap() {
        List<LoginInfoResultDO> beforeYesterdayLoginInfoResultDOList = getBeforeYesterdayLoginInfoResultList();

        return beforeYesterdayLoginInfoResultDOList.stream()
                .collect(Collectors.toMap(v -> v.getCityName() + v.getCountyName(), a -> a, (k1, k2) -> k2));
    }

    private List<LoginInfoResultDO> getBeforeYesterdayLoginInfoResultList() {
        List<LoginInfoResultDO> loginInfoResultDOS = slaveLoginInfoResultMapper.getBeforeYesterdayLoginInfoResultList();
        return loginInfoResultDOS;
    }



}


