package com.shinemo.wangge.core.push.impl;

import com.shinemo.Aace.context.AaceContext;
import com.shinemo.client.ace.msg.domain.ImMessage;
import com.shinemo.client.ace.msg.domain.MsgStructEnum;
import com.shinemo.client.ace.msg.service.PushMsgService;
import com.shinemo.client.util.GsonUtil;
import com.shinemo.jce.common.serialize.Codecs;
import com.shinemo.wangge.core.push.PushService;
import com.shinemo.wangge.core.push.domain.PushMsgExtra;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @Date: Created by Zeng Pingping on 12 / 12 / 2018
 * @Author: zengpp@shinemo.com
 * @Description:
 */
@Slf4j
@Service
class PushServiceImpl implements PushService {

    @Resource
    private PushMsgService pushMsgService;

    // 消息标题
    private final static String MESSAGE_NAME = "测试消息标题";

    @Override
    public void push(PushMsgExtra extra, int appType, long receiver) {
        //check(extra);
        int msgStruct = StringUtils.isNotBlank(extra.getImage()) ? MsgStructEnum.MST_ASSISTANT : MsgStructEnum.MST_APP;
        push(appType, receiver, msgStruct, extra);
    }

    //private void check(PushMsgExtra extra) {
    //    IS_TRUE(StringUtils.isNotBlank(extra.getTitle()), BAD_PARAMETER_ERROR, "push 标题不能为空");
    //    IS_TRUE(StringUtils.isNotBlank(extra.getContent()), BAD_PARAMETER_ERROR, "push 内容不能为空");
    //    NOT_NULL(extra.getOrgId(), BAD_PARAMETER_ERROR, "push 企业不能为空");
    //}

    private void push(int appType, long receiver, int msgStruct, PushMsgExtra extra) {
        try {
            String data = URLEncoder.encode("{\"url\":\"" + extra.getAction() + "\"}", StandardCharsets.UTF_8.name());
            extra.setAction("native://openurl?data=" + data);
        } catch (UnsupportedEncodingException e) {
            log.error("push encode error ", e);
            return;
        }

        // 是否可以分享 1 不可分享
        extra.setIsShare(1);
        // 组装消息
        ImMessage imMessage = new ImMessage(0, msgStruct,
                extra.getMessageTitle().getBytes(StandardCharsets.UTF_8),
                GsonUtil.toJson(extra).getBytes(),
                MESSAGE_NAME);
        boolean result = pushMsgService.sendNormalMsg2One(MsgStructEnum.CARE, String.valueOf(receiver),
                MsgStructEnum.MT_NORMAL, Codecs.getAaceEncoder().aaceEncode(imMessage, ImMessage.class),
                true,
                new AaceContext(String.valueOf(appType)));
        log.info("消息发送结果:{}", result);
        //IS_TRUE(result, CareErrors.SEND_PUSH_ERROR);
    }

}
