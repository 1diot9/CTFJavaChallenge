package org.springframework.jmx.access;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Map;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXServiceURL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.JmxException;
import org.springframework.jmx.MBeanServerNotFoundException;
import org.springframework.jmx.support.NotificationListenerHolder;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/access/NotificationListenerRegistrar.class */
public class NotificationListenerRegistrar extends NotificationListenerHolder implements InitializingBean, DisposableBean {
    protected final Log logger = LogFactory.getLog(getClass());
    private final ConnectorDelegate connector = new ConnectorDelegate();

    @Nullable
    private MBeanServerConnection server;

    @Nullable
    private JMXServiceURL serviceUrl;

    @Nullable
    private Map<String, ?> environment;

    @Nullable
    private String agentId;

    @Nullable
    private ObjectName[] actualObjectNames;

    public void setServer(MBeanServerConnection server) {
        this.server = server;
    }

    public void setEnvironment(@Nullable Map<String, ?> environment) {
        this.environment = environment;
    }

    @Nullable
    public Map<String, ?> getEnvironment() {
        return this.environment;
    }

    public void setServiceUrl(String url) throws MalformedURLException {
        this.serviceUrl = new JMXServiceURL(url);
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (getNotificationListener() == null) {
            throw new IllegalArgumentException("Property 'notificationListener' is required");
        }
        if (CollectionUtils.isEmpty(this.mappedObjectNames)) {
            throw new IllegalArgumentException("Property 'mappedObjectName' is required");
        }
        prepare();
    }

    public void prepare() {
        if (this.server == null) {
            this.server = this.connector.connect(this.serviceUrl, this.environment, this.agentId);
        }
        try {
            this.actualObjectNames = getResolvedObjectNames();
            if (this.actualObjectNames != null) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Registering NotificationListener for MBeans " + Arrays.toString(this.actualObjectNames));
                }
                for (ObjectName actualObjectName : this.actualObjectNames) {
                    this.server.addNotificationListener(actualObjectName, getNotificationListener(), getNotificationFilter(), getHandback());
                }
            }
        } catch (IOException ex) {
            throw new MBeanServerNotFoundException("Could not connect to remote MBeanServer at URL [" + this.serviceUrl + "]", ex);
        } catch (Exception ex2) {
            throw new JmxException("Unable to register NotificationListener", ex2);
        }
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        try {
            if (this.server != null && this.actualObjectNames != null) {
                for (ObjectName actualObjectName : this.actualObjectNames) {
                    try {
                        this.server.removeNotificationListener(actualObjectName, getNotificationListener(), getNotificationFilter(), getHandback());
                    } catch (Exception ex) {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Unable to unregister NotificationListener", ex);
                        }
                    }
                }
            }
        } finally {
            this.connector.close();
        }
    }
}
