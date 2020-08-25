package com.shinemo.wangge.core.delay;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.shinemo.todo.domain.TodoDO;
import com.shinemo.todo.query.TodoQuery;
import com.shinemo.wangge.dal.mapper.ThirdTodoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author shangkaihui
 * @Date 2020/8/24 15:38
 * @Desc
 */
@Component
@Slf4j
public class DaoSanJiaoWarnDelayJobExecutor implements DelayJobExecutor {

    @Resource
    private ThirdTodoMapper thirdTodoMapper;

    @Override
    public void execute(DelayJob job) {
        log.info("[execute] 开始执行任务:{}", job);

        Map<String, Object> jobParams = job.getJobParams();
        Long id = MapUtil.getLong(jobParams, "id");
        Assert.notNull(id, "id is null");

        TodoQuery todoQuery = new TodoQuery();
        todoQuery.setId(id);
        TodoDO todoDO = thirdTodoMapper.get(todoQuery);

        //判断工单的状态,如果是已执行,则不发送
        if (todoDO.getStatus().equals(0)) {
            //todo 发短信
        }


    }

}
