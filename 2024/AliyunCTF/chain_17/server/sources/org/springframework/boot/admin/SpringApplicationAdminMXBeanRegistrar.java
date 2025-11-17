package org.springframework.boot.admin;

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/admin/SpringApplicationAdminMXBeanRegistrar.class */
public class SpringApplicationAdminMXBeanRegistrar implements ApplicationContextAware, GenericApplicationListener, EnvironmentAware, InitializingBean, DisposableBean {
    private static final Log logger = LogFactory.getLog((Class<?>) SpringApplicationAdmin.class);
    private ConfigurableApplicationContext applicationContext;
    private final ObjectName objectName;
    private Environment environment = new StandardEnvironment();
    private boolean ready = false;
    private boolean embeddedWebApplication = false;

    public SpringApplicationAdminMXBeanRegistrar(String name) throws MalformedObjectNameException {
        this.objectName = new ObjectName(name);
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Assert.state(applicationContext instanceof ConfigurableApplicationContext, "ApplicationContext does not implement ConfigurableApplicationContext");
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override // org.springframework.context.EnvironmentAware
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override // org.springframework.context.event.GenericApplicationListener
    public boolean supportsEventType(ResolvableType eventType) {
        Class<?> type = eventType.getRawClass();
        if (type == null) {
            return false;
        }
        return ApplicationReadyEvent.class.isAssignableFrom(type) || WebServerInitializedEvent.class.isAssignableFrom(type);
    }

    @Override // org.springframework.context.event.SmartApplicationListener
    public boolean supportsSourceType(Class<?> sourceType) {
        return true;
    }

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            ApplicationReadyEvent readyEvent = (ApplicationReadyEvent) event;
            onApplicationReadyEvent(readyEvent);
        }
        if (event instanceof WebServerInitializedEvent) {
            WebServerInitializedEvent initializedEvent = (WebServerInitializedEvent) event;
            onWebServerInitializedEvent(initializedEvent);
        }
    }

    @Override // org.springframework.context.event.SmartApplicationListener, org.springframework.core.Ordered
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    void onApplicationReadyEvent(ApplicationReadyEvent event) {
        if (this.applicationContext.equals(event.getApplicationContext())) {
            this.ready = true;
        }
    }

    void onWebServerInitializedEvent(WebServerInitializedEvent event) {
        if (this.applicationContext.equals(event.getApplicationContext())) {
            this.embeddedWebApplication = true;
        }
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        server.registerMBean(new SpringApplicationAdmin(), this.objectName);
        if (logger.isDebugEnabled()) {
            logger.debug("Application Admin MBean registered with name '" + this.objectName + "'");
        }
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() throws Exception {
        ManagementFactory.getPlatformMBeanServer().unregisterMBean(this.objectName);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/admin/SpringApplicationAdminMXBeanRegistrar$SpringApplicationAdmin.class */
    private final class SpringApplicationAdmin implements SpringApplicationAdminMXBean {
        private SpringApplicationAdmin() {
        }

        @Override // org.springframework.boot.admin.SpringApplicationAdminMXBean
        public boolean isReady() {
            return SpringApplicationAdminMXBeanRegistrar.this.ready;
        }

        @Override // org.springframework.boot.admin.SpringApplicationAdminMXBean
        public boolean isEmbeddedWebApplication() {
            return SpringApplicationAdminMXBeanRegistrar.this.embeddedWebApplication;
        }

        @Override // org.springframework.boot.admin.SpringApplicationAdminMXBean
        public String getProperty(String key) {
            return SpringApplicationAdminMXBeanRegistrar.this.environment.getProperty(key);
        }

        @Override // org.springframework.boot.admin.SpringApplicationAdminMXBean
        public void shutdown() {
            SpringApplicationAdminMXBeanRegistrar.logger.info("Application shutdown requested.");
            SpringApplicationAdminMXBeanRegistrar.this.applicationContext.close();
        }
    }
}
