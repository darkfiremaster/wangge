package com.shinemo.wangge.core.config;

import com.shinemo.client.ace.Imlogin.IMLoginService;
import com.shinemo.client.ace.tinyfs.TinyfsService;
import com.shinemo.jce.spring.AaceConsumerBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class RpcConsumerConfiguration {


    @Bean(initMethod="init")
    public AaceConsumerBean tinyfsService() {
        AaceConsumerBean aaceConsumerBean = new AaceConsumerBean();
        aaceConsumerBean.setProxy("Tinyfs");
        aaceConsumerBean.setInterfaceName(TinyfsService.class.getName());
        aaceConsumerBean.setRegisterInterfaceName("Tinyfs");
        aaceConsumerBean.setRpcType("aace");
        return aaceConsumerBean;
    }

    @Bean(initMethod = "init" ,name = "aaceIMLoginService")
    public AaceConsumerBean iMLoginService(@Value("${imlogin.aace.proxy.name}") String proxy) {
        AaceConsumerBean aaceConsumerBean = new AaceConsumerBean();
        aaceConsumerBean.setInterfaceName(IMLoginService.class.getName());
        aaceConsumerBean.setProxy(proxy);
        aaceConsumerBean.setRegisterInterfaceName("IMLogin");
        aaceConsumerBean.setRpcType("aace");
        return aaceConsumerBean;
    }

}