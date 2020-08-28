package com.shinemo.wangge.core.schedule;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.shinemo.todo.dto.TodoCountDTO;
import com.shinemo.wangge.dal.mapper.ThirdTodoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author shangkaihui
 * @Date 2020/8/28 11:18
 * @Desc 预警工单超时提醒
 */
@Component
@Slf4j
public class YuJingWarnSchedule {

    @Resource
    private ThirdTodoMapper thirdTodoMapper;

    /**
     * 每天早上9点触发预警工单超时提醒
     */
    @Scheduled(cron = "0 0 9 * * ? ")
    public void yujingTodoTimeoutWarn() {

        long start = System.currentTimeMillis();
        //1. 查询今日超时工单
        DateTime startTime = DateUtil.beginOfDay(new Date());
        DateTime endTime = DateUtil.beginOfDay(DateUtil.tomorrow());
        List<TodoCountDTO> todoCountDTOList = thirdTodoMapper.getYuJingTimeoutCount(startTime.toString(), endTime.toString());

        //模拟数据
        for (int i = 1; i <= 15; i++) {
            TodoCountDTO todoCountDTO = new TodoCountDTO();
            todoCountDTO.setMobile("13588039023");
            todoCountDTO.setTodoCount(i);
            todoCountDTOList.add(todoCountDTO);
        }

        if (CollUtil.isEmpty(todoCountDTOList)) {
            log.info("[yujingTodoTimeoutWarn] data is null");
            return;
        }


        //2.使用多线程拆解任务,发短信
        List<List<TodoCountDTO>> taskList = CollUtil.split(todoCountDTOList, 10);
        ExecutorService executorService = Executors.newFixedThreadPool(taskList.size());
        log.info("[yujingTodoTimeoutWarn] 需要发送的短信数量:{}, 需要创建线程数:{}", todoCountDTOList.size(), taskList.size());

        List<CompletableFuture> futureList = new ArrayList<>();
        for (List<TodoCountDTO> todoCountDTOS : taskList) {
            CompletableFuture<Void> cf = CompletableFuture.runAsync(new YuJingTimeoutWarnTask(todoCountDTOS), executorService);
            futureList.add(cf);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        executorService.shutdown();
        log.info("[yujingTodoTimeoutWarn] 发送短信完成,耗时:{}毫秒", System.currentTimeMillis() - start);

    }


}
