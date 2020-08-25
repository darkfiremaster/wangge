package com.shinemo.wangge.test.web;

import com.shinemo.Aace.context.AaceContext;
import com.shinemo.client.ace.Sms.SmsService;
import com.shinemo.wangge.core.push.PushService;
import com.shinemo.wangge.core.push.domain.PushMsgExtra;
import com.shinemo.wangge.web.MainApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;

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

    @Resource
    private SmsService smsService;


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

    @Test
    public void smsTest() {

        ArrayList<String> mobile = new ArrayList();
        ArrayList<String> content = new ArrayList();
        ArrayList<String> successMobile = new ArrayList();
        Integer templateId = 1;
        int result = smsService.sendSms(mobile, templateId, 53, content, successMobile, new AaceContext());
        System.out.println("result = " + result);
    }


}
