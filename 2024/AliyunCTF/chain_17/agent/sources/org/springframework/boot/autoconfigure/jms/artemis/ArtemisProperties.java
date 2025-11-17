package org.springframework.boot.autoconfigure.jms.artemis;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.boot.autoconfigure.jms.JmsPoolConnectionFactoryProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "spring.artemis")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/artemis/ArtemisProperties.class */
public class ArtemisProperties {
    private ArtemisMode mode;
    private String brokerUrl;
    private String user;
    private String password;
    private final Embedded embedded = new Embedded();

    @NestedConfigurationProperty
    private final JmsPoolConnectionFactoryProperties pool = new JmsPoolConnectionFactoryProperties();

    public ArtemisMode getMode() {
        return this.mode;
    }

    public void setMode(ArtemisMode mode) {
        this.mode = mode;
    }

    public String getBrokerUrl() {
        return this.brokerUrl;
    }

    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Embedded getEmbedded() {
        return this.embedded;
    }

    public JmsPoolConnectionFactoryProperties getPool() {
        return this.pool;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/artemis/ArtemisProperties$Embedded.class */
    public static class Embedded {
        private static final AtomicInteger serverIdCounter = new AtomicInteger();
        private boolean persistent;
        private String dataDirectory;
        private int serverId = serverIdCounter.getAndIncrement();
        private boolean enabled = true;
        private String[] queues = new String[0];
        private String[] topics = new String[0];
        private String clusterPassword = UUID.randomUUID().toString();
        private boolean defaultClusterPassword = true;

        public int getServerId() {
            return this.serverId;
        }

        public void setServerId(int serverId) {
            this.serverId = serverId;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isPersistent() {
            return this.persistent;
        }

        public void setPersistent(boolean persistent) {
            this.persistent = persistent;
        }

        public String getDataDirectory() {
            return this.dataDirectory;
        }

        public void setDataDirectory(String dataDirectory) {
            this.dataDirectory = dataDirectory;
        }

        public String[] getQueues() {
            return this.queues;
        }

        public void setQueues(String[] queues) {
            this.queues = queues;
        }

        public String[] getTopics() {
            return this.topics;
        }

        public void setTopics(String[] topics) {
            this.topics = topics;
        }

        public String getClusterPassword() {
            return this.clusterPassword;
        }

        public void setClusterPassword(String clusterPassword) {
            this.clusterPassword = clusterPassword;
            this.defaultClusterPassword = false;
        }

        public boolean isDefaultClusterPassword() {
            return this.defaultClusterPassword;
        }

        public Map<String, Object> generateTransportParameters() {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("serverId", Integer.valueOf(getServerId()));
            return parameters;
        }
    }
}
