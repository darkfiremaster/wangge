package com.shinemo.wangge.web;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.shinemo.wangge.web.config.AuthCheckerAutoConfiguration;
import com.shinemo.wangge.web.config.SmCommonProperties;
import com.shinemo.wangge.web.intercepter.IntranetInterceptor;
import com.shinemo.wangge.web.intercepter.SmartGridInterceptor;
import com.shinemo.wangge.web.intercepter.TokenAuthChecker;
import com.shinemo.wangge.web.intercepter.WanggeIdCheckerInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.Ordered;
import org.springframework.retry.annotation.EnableRetry;
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
        }
)
@EnableNacosConfig(globalProperties = @NacosProperties(enableRemoteSyncConfig = "true", username = "${nacos.username}", password = "${nacos.password}"))
@NacosPropertySource(dataId = "wangge", groupId = "${nacos.group.id}")
@NacosPropertySource(dataId = "wangge", groupId = "dynamic", autoRefreshed = true, first = true)
@EnableScheduling
@EnableRetry
public class MainApplication implements WebMvcConfigurer {


    @Resource
    private IntranetInterceptor intranetInterceptor;

    @Resource
    private TokenAuthChecker tokenAuthChecker;

    @Resource
    private AuthCheckerAutoConfiguration authCheckerAutoConfiguration;
    @Resource
    private WanggeIdCheckerInterceptor wanggeIdCheckerInterceptor;

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Bean
    public SmartGridInterceptor getSmartGridInterceptor(){
        return new SmartGridInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        SmCommonProperties properties = authCheckerAutoConfiguration.getProperties();
        registry.addInterceptor(tokenAuthChecker)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        properties.getErrorPath(),
                        properties.getCheckStatusPath(),
                        "/backdoor/**")
                .excludePathPatterns(properties.getAuth().getExcludeUrlPatterns())
                .order(0);
        registry.addInterceptor(intranetInterceptor)
                .addPathPatterns("/backdoor/**")
                .order(Ordered.HIGHEST_PRECEDENCE);
        registry.addInterceptor(wanggeIdCheckerInterceptor)
                .addPathPatterns("/**")
                .order(Ordered.LOWEST_PRECEDENCE);
        registry.addInterceptor(getSmartGridInterceptor())
                .addPathPatterns("/stallUp/**")
                .addPathPatterns("/smartGrid/**")
                .addPathPatterns("/sweepFloor/**")
                .addPathPatterns("/thirdapi/**")
                .addPathPatterns("/sweepvillage/**")
                .addPathPatterns("/operate/**")
                .excludePathPatterns("/backdoor/**", "/error")
                .order(1);
    }
}