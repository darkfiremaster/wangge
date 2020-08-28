package com.shinemo.wangge.core.event;

import com.shinemo.stallup.domain.model.StallUpActivity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @Author shangkaihui
 * @Date 2020/8/25 10:58
 * @Desc
 */
@Getter
public class StallUpCreateEvent extends ApplicationEvent {


    private StallUpActivity stallUpActivity;

    public StallUpCreateEvent(Object source, StallUpActivity stallUpActivity) {
        super(source);
        this.stallUpActivity = stallUpActivity;
    }

    @Override
    public String toString() {
        return "StallUpCreateEvent{" +
                "stallUpActivity=" + stallUpActivity +
                ", source=" + source +
                '}';
    }
}
