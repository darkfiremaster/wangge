package com.shinemo.stallup.domain.request;

import lombok.Data;

/**
 * 短信预热反向鉴权入参
 */
@Data
public class SmsHotReverseAuthRequest {

    private String authKey;

    private String mobile;

    private String sourceKey;
}
