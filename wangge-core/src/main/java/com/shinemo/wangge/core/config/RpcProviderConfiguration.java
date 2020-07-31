package com.shinemo.wangge.core.config;

import com.shinemo.jce.spring.AaceProviderBean;
import com.shinemo.schedule.facade.WanggeScheduleFacadeService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * 类说明: provider
 *
 * @author zengpeng
 */
@Configuration
public class RpcProviderConfiguration {

    @Bean(initMethod="init")
    @DependsOn("wanggeScheduleFacadeService")
    public AaceProviderBean providerWanggeScheduleFacadeService(@Qualifier("wanggeScheduleFacadeService") WanggeScheduleFacadeService wanggeScheduleFacadeService) {
        AaceProviderBean aaceProviderBean = new AaceProviderBean();
        aaceProviderBean.setInterfaceName(WanggeScheduleFacadeService.class.getName());
        aaceProviderBean.setTarget(wanggeScheduleFacadeService);
        return aaceProviderBean;
    }
}
