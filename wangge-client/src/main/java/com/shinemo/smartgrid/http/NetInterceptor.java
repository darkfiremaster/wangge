package com.shinemo.smartgrid.http;

import okhttp3.*;

import java.io.IOException;

public class NetInterceptor implements Interceptor {


    public NetInterceptor() {
        super();
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //移除头部参数
        request = request.newBuilder()
                .removeHeader("User-Agent")
                .removeHeader("Accept-Encoding")
                .build();
        Response response = chain.proceed(request);
        if (response.body() != null && response.body().contentType() != null) {
            MediaType mediaType = response.body().contentType();
            String content = response.body().string();
            ResponseBody responseBody = ResponseBody.create(mediaType, content);
            return response.newBuilder().body(responseBody).build();
        } else {
            return response;
        }
    }

}