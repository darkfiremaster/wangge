package com.shinemo.wangge.core.event;

import com.shinemo.todo.domain.TodoDO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @Author shangkaihui
 * @Date 2020/8/25 10:58
 * @Desc
 */
@Getter
public class DaoSanJiaoTodoCreateEvent extends ApplicationEvent {


    private TodoDO todoDO;

    public DaoSanJiaoTodoCreateEvent(Object source, TodoDO todoDO) {
        super(source);
        this.todoDO = todoDO;
    }

    @Override
    public String toString() {
        return "DaoSanJiaoTodoCreateEvent{" +
                "todoDO=" + todoDO +
                ", source=" + source +
                '}';
    }
}
