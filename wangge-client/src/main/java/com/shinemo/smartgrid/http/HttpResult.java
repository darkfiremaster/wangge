package com.shinemo.smartgrid.http;

import com.shinemo.util.GsonUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpResult {
    private int code;
    private String content;
    private long costTime;
    private String errorMsg;

    public boolean success() {
        return getCode() == 200;
    }

    /**
     * 获取结果
     *
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T getResult(Class<T> clazz) {
        return GsonUtil.fromJson(getContent(), clazz);
    }


    public boolean timeOut() {
        return costTime > HttpConstants.READ_TIME_OUT_SECONDS * 1000;
    }

}
