package org.springframework.context;

import java.io.Closeable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/ConfigurableApplicationContext.class */
public interface ConfigurableApplicationContext extends ApplicationContext, Lifecycle, Closeable {
    public static final String CONFIG_LOCATION_DELIMITERS = ",; \t\n";
    public static final String CONVERSION_SERVICE_BEAN_NAME = "conversionService";
    public static final String LOAD_TIME_WEAVER_BEAN_NAME = "loadTimeWeaver";
    public static final String ENVIRONMENT_BEAN_NAME = "environment";
    public static final String SYSTEM_PROPERTIES_BEAN_NAME = "systemProperties";
    public static final String SYSTEM_ENVIRONMENT_BEAN_NAME = "systemEnvironment";
    public static final String APPLICATION_STARTUP_BEAN_NAME = "applicationStartup";
    public static final String SHUTDOWN_HOOK_THREAD_NAME = "SpringContextShutdownHook";

    void setId(String id);

    void setParent(@Nullable ApplicationContext parent);

    void setEnvironment(ConfigurableEnvironment environment);

    @Override // org.springframework.core.env.EnvironmentCapable
    ConfigurableEnvironment getEnvironment();

    void setApplicationStartup(ApplicationStartup applicationStartup);

    ApplicationStartup getApplicationStartup();

    void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);

    void addApplicationListener(ApplicationListener<?> listener);

    void removeApplicationListener(ApplicationListener<?> listener);

    void setClassLoader(ClassLoader classLoader);

    void addProtocolResolver(ProtocolResolver resolver);

    void refresh() throws BeansException, IllegalStateException;

    void registerShutdownHook();

    @Override // java.io.Closeable, java.lang.AutoCloseable
    void close();

    boolean isActive();

    ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;
}
