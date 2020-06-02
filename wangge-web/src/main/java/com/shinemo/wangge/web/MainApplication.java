package com.shinemo.wangge.web;

import com.shinemo.wangge.web.config.AuthCheckerAutoConfiguration;
import com.shinemo.wangge.web.config.SmCommonProperties;
import com.shinemo.wangge.web.intercepter.CustMgrInterceptor;
import com.shinemo.wangge.web.intercepter.IntranetInterceptor;
import com.shinemo.wangge.web.intercepter.SmartGridInterceptor;
import com.shinemo.wangge.web.intercepter.TokenAuthChecker;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author htdong
 * @date 2020年4月15日 上午11:30:57
 */
@ImportResource({
        "classpath:wangge-web.xml"
})
@SpringBootApplication(
        scanBasePackages = {
                "com.shinemo.wangge",
                "com.shinemo.springboot"
        }
)
@EnableNacosConfig(globalProperties = @NacosProperties(enableRemoteSyncConfig = "true", username = "${nacos.username}", password = "${nacos.password}"))
@NacosPropertySource(dataId = "wangge", groupId = "${nacos.group.id}")
// @NacosPropertySource(dataId = "wangge", groupId = "dynamic", autoRefreshed =
// true, first = true)
@MapperScan(basePackages = {
        "com.shinemo.wangge.mapper"
})
@EnableScheduling
public class MainApplication implements WebMvcConfigurer {

    @Resource
    private CustMgrInterceptor custMgrInterceptor;

    @Resource
    private IntranetInterceptor intranetInterceptor;

    @Resource
    private TokenAuthChecker tokenAuthChecker;

    @Resource
    private AuthCheckerAutoConfiguration authCheckerAutoConfiguration;

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        SmCommonProperties properties = authCheckerAutoConfiguration.getProperties();
        registry.addInterceptor(tokenAuthChecker)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        properties.getErrorPath(),
                        properties.getCheckStatusPath(),
                        "/backdoor/**"
                )
                .excludePathPatterns(properties.getAuth().getExcludeUrlPatterns())
                .order(0);
        registry.addInterceptor(custMgrInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/backdoor/**", "/error")
                .order(Ordered.LOWEST_PRECEDENCE);
        registry.addInterceptor(intranetInterceptor)
                .addPathPatterns("/backdoor/**")
                .order(Ordered.HIGHEST_PRECEDENCE);
        registry.addInterceptor(new SmartGridInterceptor())
                .addPathPatterns("/stallUp/**")
                .addPathPatterns("/smartGrid/**")
                .addPathPatterns("/sweepFloor/**")
                .addPathPatterns("/thirdapi/**")
                .excludePathPatterns("/backdoor/**", "/error")
                .order(Ordered.LOWEST_PRECEDENCE);
    }
}