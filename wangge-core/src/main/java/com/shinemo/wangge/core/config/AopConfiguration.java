package com.shinemo.wangge.core.config;

import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;

import com.shinemo.client.aop.log.FacadeExceptionAop;
import com.shinemo.client.aop.log.PrintParamResult;
import com.shinemo.client.aop.performance.PerformanceInner;
import com.shinemo.client.aop.performance.PerformanceOuter;
import com.shinemo.client.aop.util.SpringAopUtils;

/**
 * @author htdong
 * @date 2020年5月27日 上午11:33:34
 */
@Configuration
public class AopConfiguration implements BeanDefinitionRegistryPostProcessor {

    @Bean
    public PerformanceOuter performanceOuter() {
        return new PerformanceOuter();
    }

    @Bean
    public PerformanceInner performanceInner() {
        return new PerformanceInner();
    }

    @Bean
    public PrintParamResult printParamResult() {
        return new PrintParamResult();
    }

    @Bean
    public FacadeExceptionAop facadeExceptionAop() {
        return new FacadeExceptionAop();
    }

    @Bean
    public Pointcut applicationFacadePointcut() {
        AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut();
        aspectJExpressionPointcut
                .setExpression("execution(* (com.shinemo.wangge.client..facade.*FacadeService+).*(..))");
        return aspectJExpressionPointcut;
    }

    @Bean
    @DependsOn({ "performanceOuter", "applicationFacadePointcut" })
    public Advisor performanceOuterAdvisor(@Qualifier("performanceOuter") PerformanceOuter performanceOuter,
            @Qualifier("applicationFacadePointcut") Pointcut pointcut) {
        DefaultBeanFactoryPointcutAdvisor advisor = new DefaultBeanFactoryPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(performanceOuter);
        return advisor;
    }

    @Bean
    @DependsOn({ "performanceInner" })
    public Advisor performanceInnerAdvisor(@Qualifier("performanceInner") PerformanceInner performanceInner) {
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        advisor.setExpression("execution(* com.shinemo.wangge.core..*.*(..))");
        advisor.setAdvice(performanceInner);
        return advisor;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        SpringAopUtils.registerAround("around", "printParamResult", Ordered.HIGHEST_PRECEDENCE,
                "applicationFacadePointcut", registry);
        SpringAopUtils.registerAround("around", "facadeExceptionAop", Ordered.HIGHEST_PRECEDENCE,
                "applicationFacadePointcut", registry);
    }
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }
}