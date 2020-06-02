package com.shinemo.wangge.core.schedule;


import com.shinemo.stallup.domain.utils.SubTableUtils;
import com.shinemo.wangge.dal.mapper.TableOperatorMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * 定时任务 创建按月分表
 *
 * @author Chenzhe Mao
 * @date 2020-03-16
 */
@Component
@Slf4j
public class HuaWeiApiCreateTableTask {
	private static final String CREATE_SQL_FORMAT = "CREATE TABLE IF NOT EXISTS `t_huawei_api_log%s` (\n" +
		"  `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
		"  `gmt_create` datetime NOT NULL,\n" +
		"  `gmt_modified` datetime DEFAULT NULL,\n" +
		"  `mobile` varchar(255) NOT NULL COMMENT '手机号',\n" +
		"  `status` int(11) DEFAULT NULL COMMENT '请求返回状态码',\n" +
		"  `cost_time` bigint(20) DEFAULT NULL COMMENT '耗时',\n" +
		"  `request` text NOT NULL COMMENT '请求参数',\n" +
		"  `response` text COMMENT '返回参数',\n" +
		"  `url` varchar(255) NOT NULL COMMENT '访问路径',\n" +
		"  PRIMARY KEY (`id`)\n" +
		") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;";
	@Resource
	private TableOperatorMapper tableOperatorMapper;

	@PostConstruct
	public void init() {
		//项目重启，执行当月和下一月
		log.info("[HuaWeiApiCreateTableTask] init");
		String currentMonthIndex = SubTableUtils.getTableIndexByMonth();
		createTable(currentMonthIndex);
		execute();
	}

	@Scheduled(cron = "0 0 1 * * ? ")
	public void execute() {
		//定时任务每天执行下一月
		log.info("[HuaWeiApiCreateTableTask] execute");
		String nextMonthIndex = SubTableUtils.getTableIndexByMonth(LocalDate.now().plusMonths(1));
		createTable(nextMonthIndex);
	}

	private void createTable(String index) {
		String sql = String.format(CREATE_SQL_FORMAT, index);
		log.info("[HuaWeiApiCreateTableTask] createTable, sql:{}", sql);
		tableOperatorMapper.createTable(sql);
	}
}
