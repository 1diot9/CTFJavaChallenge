package org.springframework.jmx.access;

import java.io.IOException;
import java.util.Map;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jmx.MBeanServerNotFoundException;
import org.springframework.jmx.support.JmxUtils;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/access/ConnectorDelegate.class */
class ConnectorDelegate {
    private static final Log logger = LogFactory.getLog((Class<?>) ConnectorDelegate.class);

    @Nullable
    private JMXConnector connector;

    public MBeanServerConnection connect(@Nullable JMXServiceURL serviceUrl, @Nullable Map<String, ?> environment, @Nullable String agentId) throws MBeanServerNotFoundException {
        if (serviceUrl != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Connecting to remote MBeanServer at URL [" + serviceUrl + "]");
            }
            try {
                this.connector = JMXConnectorFactory.connect(serviceUrl, environment);
                return this.connector.getMBeanServerConnection();
            } catch (IOException ex) {
                throw new MBeanServerNotFoundException("Could not connect to remote MBeanServer [" + serviceUrl + "]", ex);
            }
        }
        logger.debug("Attempting to locate local MBeanServer");
        return JmxUtils.locateMBeanServer(agentId);
    }

    public void close() {
        if (this.connector != null) {
            try {
                this.connector.close();
            } catch (IOException ex) {
                logger.debug("Could not close JMX connector", ex);
            }
        }
    }
}
