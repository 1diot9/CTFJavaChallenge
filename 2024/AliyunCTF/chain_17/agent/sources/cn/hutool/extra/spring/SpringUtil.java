package cn.hutool.extra.spring;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ArrayUtil;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

@Component
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/spring/SpringUtil.class */
public class SpringUtil implements BeanFactoryPostProcessor, ApplicationContextAware {
    private static ConfigurableListableBeanFactory beanFactory;
    private static ApplicationContext applicationContext;

    @Override // org.springframework.beans.factory.config.BeanFactoryPostProcessor
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory2) throws BeansException {
        beanFactory = beanFactory2;
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext2) {
        applicationContext = applicationContext2;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static ListableBeanFactory getBeanFactory() {
        ListableBeanFactory factory = null == beanFactory ? applicationContext : beanFactory;
        if (null == factory) {
            throw new UtilException("No ConfigurableListableBeanFactory or ApplicationContext injected, maybe not in the Spring environment?");
        }
        return factory;
    }

    public static ConfigurableListableBeanFactory getConfigurableBeanFactory() throws UtilException {
        ConfigurableListableBeanFactory factory;
        if (null != beanFactory) {
            factory = beanFactory;
        } else if (applicationContext instanceof ConfigurableApplicationContext) {
            factory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
        } else {
            throw new UtilException("No ConfigurableListableBeanFactory from context!");
        }
        return factory;
    }

    public static <T> T getBean(String str) {
        return (T) getBeanFactory().getBean(str);
    }

    public static <T> T getBean(Class<T> cls) {
        return (T) getBeanFactory().getBean(cls);
    }

    public static <T> T getBean(String str, Class<T> cls) {
        return (T) getBeanFactory().getBean(str, cls);
    }

    public static <T> T getBean(TypeReference<T> typeReference) {
        ParameterizedType parameterizedType = (ParameterizedType) typeReference.getType();
        Class cls = (Class) parameterizedType.getRawType();
        return (T) getBean(getBeanFactory().getBeanNamesForType(ResolvableType.forClassWithGenerics((Class<?>) cls, (Class<?>[]) Arrays.stream(parameterizedType.getActualTypeArguments()).map(type -> {
            return (Class) type;
        }).toArray(x$0 -> {
            return new Class[x$0];
        })))[0], cls);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        return getBeanFactory().getBeansOfType(type);
    }

    public static String[] getBeanNamesForType(Class<?> type) {
        return getBeanFactory().getBeanNamesForType(type);
    }

    public static String getProperty(String key) {
        if (null == applicationContext) {
            return null;
        }
        return applicationContext.getEnvironment().getProperty(key);
    }

    public static String getApplicationName() {
        return getProperty("spring.application.name");
    }

    public static String[] getActiveProfiles() {
        if (null == applicationContext) {
            return null;
        }
        return applicationContext.getEnvironment().getActiveProfiles();
    }

    public static String getActiveProfile() {
        String[] activeProfiles = getActiveProfiles();
        if (ArrayUtil.isNotEmpty((Object[]) activeProfiles)) {
            return activeProfiles[0];
        }
        return null;
    }

    public static <T> void registerBean(String beanName, T bean) {
        ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
        factory.autowireBean(bean);
        factory.registerSingleton(beanName, bean);
    }

    public static void unregisterBean(String beanName) {
        SingletonBeanRegistry configurableBeanFactory = getConfigurableBeanFactory();
        if (configurableBeanFactory instanceof DefaultSingletonBeanRegistry) {
            DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) configurableBeanFactory;
            registry.destroySingleton(beanName);
            return;
        }
        throw new UtilException("Can not unregister bean, the factory is not a DefaultSingletonBeanRegistry!");
    }

    public static void publishEvent(ApplicationEvent event) {
        if (null != applicationContext) {
            applicationContext.publishEvent(event);
        }
    }

    public static void publishEvent(Object event) {
        if (null != applicationContext) {
            applicationContext.publishEvent(event);
        }
    }
}
