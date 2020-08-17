package com.shinemo.wangge.test.web;

import com.shinemo.wangge.core.push.PushService;
import com.shinemo.wangge.core.push.domain.PushMsgExtra;
import com.shinemo.wangge.web.MainApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Author shangkaihui
 * @Date 2020/8/17 14:37
 * @Desc
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainApplication.class)
public class PushTest {

    @Resource
    private PushService pushService;

    @Test
    public void pushTest() {
        PushMsgExtra pushMsgExtra = PushMsgExtra.builder()
                .icon("http://statics.jituancaiyun.com/cdn/images/message-icon/ap87178073.png")
                .action("https://developer.e.uban360.com/intelligent-grids/index.html")
                //.orgId(83817L)
                .content("测试摆摊")
                .title("测试title")
                .messageTitle("测试messageTitle")
                .build();
        pushService.push(pushMsgExtra, 53, 69553056L);
    }
}
