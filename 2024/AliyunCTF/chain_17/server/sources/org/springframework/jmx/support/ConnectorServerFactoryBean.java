package org.springframework.jmx.support;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.management.JMException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.MBeanServerForwarder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.JmxException;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/support/ConnectorServerFactoryBean.class */
public class ConnectorServerFactoryBean extends MBeanRegistrationSupport implements FactoryBean<JMXConnectorServer>, InitializingBean, DisposableBean {
    public static final String DEFAULT_SERVICE_URL = "service:jmx:jmxmp://localhost:9875";

    @Nullable
    private MBeanServerForwarder forwarder;

    @Nullable
    private ObjectName objectName;

    @Nullable
    private JMXConnectorServer connectorServer;
    private String serviceUrl = DEFAULT_SERVICE_URL;
    private final Map<String, Object> environment = new HashMap();
    private boolean threaded = false;
    private boolean daemon = false;

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public void setEnvironment(@Nullable Properties environment) {
        CollectionUtils.mergePropertiesIntoMap(environment, this.environment);
    }

    public void setEnvironmentMap(@Nullable Map<String, ?> environment) {
        if (environment != null) {
            this.environment.putAll(environment);
        }
    }

    public void setForwarder(MBeanServerForwarder forwarder) {
        this.forwarder = forwarder;
    }

    public void setObjectName(Object objectName) throws MalformedObjectNameException {
        this.objectName = ObjectNameManager.getInstance(objectName);
    }

    public void setThreaded(boolean threaded) {
        this.threaded = threaded;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws JMException, IOException {
        if (this.server == null) {
            this.server = JmxUtils.locateMBeanServer();
        }
        JMXServiceURL url = new JMXServiceURL(this.serviceUrl);
        this.connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, this.environment, this.server);
        if (this.forwarder != null) {
            this.connectorServer.setMBeanServerForwarder(this.forwarder);
        }
        if (this.objectName != null) {
            doRegister(this.connectorServer, this.objectName);
        }
        try {
            if (this.threaded) {
                final JMXConnectorServer serverToStart = this.connectorServer;
                Thread connectorThread = new Thread() { // from class: org.springframework.jmx.support.ConnectorServerFactoryBean.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        try {
                            serverToStart.start();
                        } catch (IOException ex) {
                            throw new JmxException("Could not start JMX connector server after delay", ex);
                        }
                    }
                };
                connectorThread.setName("JMX Connector Thread [" + this.serviceUrl + "]");
                connectorThread.setDaemon(this.daemon);
                connectorThread.start();
            } else {
                this.connectorServer.start();
            }
            if (this.logger.isInfoEnabled()) {
                this.logger.info("JMX connector server started: " + this.connectorServer);
            }
        } catch (IOException ex) {
            unregisterBeans();
            throw ex;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public JMXConnectorServer getObject() {
        return this.connectorServer;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<? extends JMXConnectorServer> getObjectType() {
        return this.connectorServer != null ? this.connectorServer.getClass() : JMXConnectorServer.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() throws IOException {
        try {
            if (this.connectorServer != null) {
                if (this.logger.isInfoEnabled()) {
                    this.logger.info("Stopping JMX connector server: " + this.connectorServer);
                }
                this.connectorServer.stop();
            }
        } finally {
            unregisterBeans();
        }
    }
}
