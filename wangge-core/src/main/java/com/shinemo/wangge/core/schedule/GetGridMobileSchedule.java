package com.shinemo.wangge.core.schedule;

import com.shinemo.client.util.EnvUtil;
import com.shinemo.client.util.GsonUtil;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.my.redis.service.RedisService;
import com.shinemo.stallup.domain.request.HuaWeiRequest;
import com.shinemo.wangge.core.service.stallup.HuaWeiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * 拉取网格系统手机号任务
 *
 * @author Chenzhe Mao
 * @date 2020-04-27
 */
@Slf4j
@Component
public class GetGridMobileSchedule {

	@Resource
	private HuaWeiService huaWeiService;

	@Resource
	private RedisService redisService;
	private final String WANGGE_USER_MOBILE_LIST_KEY = "WANGGE_USER_MOBILE_LIST_KEY";
	private final Integer expire = 60*60*2;


	@Scheduled(cron = "0 0 2 * * ?")
	public void execute() {
		long begin = System.currentTimeMillis();
		log.info("[GetGridMobileSchedule] execute start");
		writeMobile();
		log.info("[GetGridMobileSchedule] execute end, costTime:{}ms", System.currentTimeMillis() - begin);
	}

	private void writeMobile() {
		ApiResult<List<String>> result = huaWeiService.getUserList(
			HuaWeiRequest.builder()
			.mobile("13396631940")
			.build());
		if (!result.isSuccess()) {
			System.out.println("error");
			return;
		}
		List<String> list = result.getData();
		if(EnvUtil.isDaily()){
			list.add("15958032925");
			list.add("15226536886");
		}

		redisService.set(WANGGE_USER_MOBILE_LIST_KEY, GsonUtil.toJson(list),expire);
		String separator = System.getProperty("line.separator");
		File file;
		OutputStreamWriter ops = null;
		BufferedWriter bw = null;
		try {
			String fileName = "mobile";
			file = new File("/data/logs/wangge/", fileName + ".txt");
			if(file.isFile() && file.exists()){
				file.delete();
			}
			file.createNewFile();
			ops = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			bw = new BufferedWriter(ops);
			for (String s : list) {
				bw.append(s).append(separator);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.flush();
					bw.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
