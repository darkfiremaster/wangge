package com.shinemo.stallup.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsHotReverseAuthResponse {
    private Integer code;
    private String msg;
    private String mobile;
}
