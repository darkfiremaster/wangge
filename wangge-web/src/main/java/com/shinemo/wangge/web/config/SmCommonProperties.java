package com.shinemo.wangge.web.config;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "sm")
public final class SmCommonProperties {
    @Value("${sm.env:${sm.conf.ENV:online}}")
    private static String env;

    @Value("${sm.aace-center-url:aace://aace.shinemo.net:16999/center}")
    private String aaceCenterUrl;

    @Value("${server.error.path:${error.path:/error}}")
    private String errorPath;

    private String checkStatusPath = "/checkstatus";

    private Power power = new Power();
    private Auth auth = new Auth();
    private AccessLog accessLog = new AccessLog();
    private Cgw cgw = new Cgw();


    @Getter
    @Setter
    public static class Auth {
        private List<String> urlPatterns = Lists.newArrayList("/**");
        private List<String> excludeUrlPatterns = Collections.emptyList();
    }

    @Getter
    @Setter
    public static class Power {
        private String redisKey = "portal";
        private String urlPatterns = "/*";
        private String excludeUrlPatterns;
    }

    @Getter
    @Setter
    public static class AccessLog {
        private int maxCacheSize = 2048;
        private String urlPatterns = "/*";
        private String excludeUrlPatterns;
    }

    @Getter
    @Setter
    public static class Cgw {
        private String urlPatterns = "/*";
        private String excludeUrlPatterns;
    }

    public boolean isDaily() {
        return "daily".equals(env);
    }

    public boolean isPre() {
        return "pre".equals(env);
    }

    public boolean isOnline() {
        return "online".equals(env);
    }
}
