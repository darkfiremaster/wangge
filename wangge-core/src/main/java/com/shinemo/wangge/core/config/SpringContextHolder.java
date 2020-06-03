package com.shinemo.wangge.core.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Consumer;

import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        System.out.println("applicationContext正在初始化,application:"+context);
        SpringContextHolder.context = context;
    }

    public static Object getSpringBean(String beanName) {
        notEmpty(beanName, "bean name is required");
        return context == null ? null : context.getBean(beanName);
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        return context.getBean(beanName, clazz);
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static String[] getBeanDefinitionNames() {
        return context.getBeanDefinitionNames();
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return context.getBeansOfType(type);
    }

    /**
     * 根据 beanName 和 clazz 获取 bean，如果 bean 不存在，则根据 clazz 创建 bean，builder 用来构建 bean 的属性和依赖
     *
     * @param beanName
     * @param clazz
     * @param builder
     * @return T
     * @author harold
     * @date 2018-07-10
     **/
    public static <T> T getBeanAndGenerateIfNotExist(String beanName, Class<T> clazz,
                                                     Consumer<BeanDefinitionBuilder> builder) {
        return getBeanAndGenerateIfNotExist(beanName, null, clazz, builder);
    }

    /**
     * 根据 beanName 和 clazz 获取 bean，如果 bean 不存在，则根据 clazz 创建 bean
     *
     * @param beanName
     * @param clazz
     * @return T
     * @author harold
     * @date 2018-07-10
     **/
    public static <T> T getBeanAndGenerateIfNotExist(String beanName, Class<T> clazz) {
        return getBeanAndGenerateIfNotExist(beanName, null, clazz, null);
    }

    /**
     * 根据 beanName 和 vClass 获取 bean，如果 bean 不存在，则根据 clazz 创建 bean
     *
     * @param beanName
     * @param factoryClazz
     * @param clazz
     * @return V
     * @author harold
     * @date 2018-07-10
     **/
    public static <T extends FactoryBean<V>, V> V getBeanAndGenerateIfNotExist(String beanName, Class<T> factoryClazz, Class<V> clazz) {
        return getBeanAndGenerateIfNotExist(beanName, factoryClazz, clazz, null);
    }

    /**
     * 根据 beanName 和 vClass 获取 bean，如果 bean 不存在，则根据 clazz 创建 bean，builder 用来构建 bean 的属性和依赖
     *
     * @param beanName
     * @param factoryClazz
     * @param clazz
     * @param builder
     * @return V
     * @author harold
     * @date 2018-07-10
     **/
    public static <T extends FactoryBean<V>, V> V getBeanAndGenerateIfNotExist(String beanName, Class<T> factoryClazz, Class<V> clazz,
                                                                               Consumer<BeanDefinitionBuilder> builder) {
        notEmpty(beanName, "bean name is required");
        notNull(clazz, "clazz is required");
        if (context == null) {
            return null;
        }
        if (context.containsBean(beanName)) {
            return context.getBean(beanName, clazz);
        }
        synchronized (SpringContextHolder.class) {
            if (context.containsBean(beanName)) {
                return context.getBean(beanName, clazz);
            }
            Class<?> targetClazz = factoryClazz == null ? clazz : factoryClazz;
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(targetClazz);
            if (builder != null) {
                builder.accept(beanDefinitionBuilder);
            }
            ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) context;
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
            defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getRawBeanDefinition());
            return context.getBean(beanName, clazz);
        }
    }

    /**
     * 移除 bean
     *
     * @param beanName
     * @return void
     * @author Harold Luo
     * @date 2018-08-23
     **/
    public static void removeBean(String beanName) {
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) context;
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
        defaultListableBeanFactory.removeBeanDefinition(beanName);
    }
}

