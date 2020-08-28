package com.shinemo.wangge.core.schedule;

import com.shinemo.todo.dto.TodoCountDTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/8/28 14:12
 * @Desc
 */
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class YuJingTimeoutWarnTask implements Runnable {

    private List<TodoCountDTO> todoCountDTO;

    @SneakyThrows
    @Override
    public void run() {
        log.info("线程:{}开始任务", Thread.currentThread().getName());
        for (TodoCountDTO countDTO : todoCountDTO) {
            try {
                Thread.sleep(1000);
                log.info("线程:{}发送短信成功,mobile:{},count:{}", Thread.currentThread().getName(), countDTO.getMobile(), countDTO.getTodoCount());
            } catch (Exception e) {
                log.error("发送短信失败,失败原因:{},手机号:{}", e.getMessage(), countDTO.getMobile());
            }
        }
        log.info("线程:{}完成任务", Thread.currentThread().getName());
    }
}
