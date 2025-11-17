package org.springframework.jmx.support;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.target.AbstractLazyCreationTargetSource;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/support/MBeanServerConnectionFactoryBean.class */
public class MBeanServerConnectionFactoryBean implements FactoryBean<MBeanServerConnection>, BeanClassLoaderAware, InitializingBean, DisposableBean {

    @Nullable
    private JMXServiceURL serviceUrl;
    private final Map<String, Object> environment = new HashMap();
    private boolean connectOnStartup = true;

    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    @Nullable
    private JMXConnector connector;

    @Nullable
    private MBeanServerConnection connection;

    @Nullable
    private JMXConnectorLazyInitTargetSource connectorTargetSource;

    public void setServiceUrl(String url) throws MalformedURLException {
        this.serviceUrl = new JMXServiceURL(url);
    }

    public void setEnvironment(Properties environment) {
        CollectionUtils.mergePropertiesIntoMap(environment, this.environment);
    }

    public void setEnvironmentMap(@Nullable Map<String, ?> environment) {
        if (environment != null) {
            this.environment.putAll(environment);
        }
    }

    public void setConnectOnStartup(boolean connectOnStartup) {
        this.connectOnStartup = connectOnStartup;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws IOException {
        if (this.serviceUrl == null) {
            throw new IllegalArgumentException("Property 'serviceUrl' is required");
        }
        if (this.connectOnStartup) {
            connect();
        } else {
            createLazyConnection();
        }
    }

    private void connect() throws IOException {
        Assert.state(this.serviceUrl != null, "No JMXServiceURL set");
        this.connector = JMXConnectorFactory.connect(this.serviceUrl, this.environment);
        this.connection = this.connector.getMBeanServerConnection();
    }

    private void createLazyConnection() {
        this.connectorTargetSource = new JMXConnectorLazyInitTargetSource();
        TargetSource connectionTargetSource = new MBeanServerConnectionLazyInitTargetSource();
        this.connector = (JMXConnector) new ProxyFactory((Class<?>) JMXConnector.class, this.connectorTargetSource).getProxy(this.beanClassLoader);
        this.connection = (MBeanServerConnection) new ProxyFactory((Class<?>) MBeanServerConnection.class, connectionTargetSource).getProxy(this.beanClassLoader);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public MBeanServerConnection getObject() {
        return this.connection;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<? extends MBeanServerConnection> getObjectType() {
        return this.connection != null ? this.connection.getClass() : MBeanServerConnection.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() throws IOException {
        if (this.connector != null) {
            if (this.connectorTargetSource == null || this.connectorTargetSource.isInitialized()) {
                this.connector.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/support/MBeanServerConnectionFactoryBean$JMXConnectorLazyInitTargetSource.class */
    public class JMXConnectorLazyInitTargetSource extends AbstractLazyCreationTargetSource {
        private JMXConnectorLazyInitTargetSource() {
        }

        @Override // org.springframework.aop.target.AbstractLazyCreationTargetSource
        protected Object createObject() throws Exception {
            Assert.state(MBeanServerConnectionFactoryBean.this.serviceUrl != null, "No JMXServiceURL set");
            return JMXConnectorFactory.connect(MBeanServerConnectionFactoryBean.this.serviceUrl, MBeanServerConnectionFactoryBean.this.environment);
        }

        @Override // org.springframework.aop.target.AbstractLazyCreationTargetSource, org.springframework.aop.TargetSource, org.springframework.aop.TargetClassAware
        public Class<?> getTargetClass() {
            return JMXConnector.class;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/support/MBeanServerConnectionFactoryBean$MBeanServerConnectionLazyInitTargetSource.class */
    public class MBeanServerConnectionLazyInitTargetSource extends AbstractLazyCreationTargetSource {
        private MBeanServerConnectionLazyInitTargetSource() {
        }

        @Override // org.springframework.aop.target.AbstractLazyCreationTargetSource
        protected Object createObject() throws Exception {
            Assert.state(MBeanServerConnectionFactoryBean.this.connector != null, "JMXConnector not initialized");
            return MBeanServerConnectionFactoryBean.this.connector.getMBeanServerConnection();
        }

        @Override // org.springframework.aop.target.AbstractLazyCreationTargetSource, org.springframework.aop.TargetSource, org.springframework.aop.TargetClassAware
        public Class<?> getTargetClass() {
            return MBeanServerConnection.class;
        }
    }
}
