package org.springframework.boot.autoconfigure.jms.activemq;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.autoconfigure.jms.JmsPoolConnectionFactoryProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "spring.activemq")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/activemq/ActiveMQProperties.class */
public class ActiveMQProperties {
    private static final String DEFAULT_NETWORK_BROKER_URL = "tcp://localhost:61616";
    private String brokerUrl;
    private String user;
    private String password;
    private Duration closeTimeout = Duration.ofSeconds(15);
    private boolean nonBlockingRedelivery = false;
    private Duration sendTimeout = Duration.ofMillis(0);

    @NestedConfigurationProperty
    private final JmsPoolConnectionFactoryProperties pool = new JmsPoolConnectionFactoryProperties();
    private final Packages packages = new Packages();

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

    public Duration getCloseTimeout() {
        return this.closeTimeout;
    }

    public void setCloseTimeout(Duration closeTimeout) {
        this.closeTimeout = closeTimeout;
    }

    public boolean isNonBlockingRedelivery() {
        return this.nonBlockingRedelivery;
    }

    public void setNonBlockingRedelivery(boolean nonBlockingRedelivery) {
        this.nonBlockingRedelivery = nonBlockingRedelivery;
    }

    public Duration getSendTimeout() {
        return this.sendTimeout;
    }

    public void setSendTimeout(Duration sendTimeout) {
        this.sendTimeout = sendTimeout;
    }

    public JmsPoolConnectionFactoryProperties getPool() {
        return this.pool;
    }

    public Packages getPackages() {
        return this.packages;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String determineBrokerUrl() {
        if (this.brokerUrl != null) {
            return this.brokerUrl;
        }
        return DEFAULT_NETWORK_BROKER_URL;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/activemq/ActiveMQProperties$Packages.class */
    public static class Packages {
        private Boolean trustAll;
        private List<String> trusted = new ArrayList();

        public Boolean getTrustAll() {
            return this.trustAll;
        }

        public void setTrustAll(Boolean trustAll) {
            this.trustAll = trustAll;
        }

        public List<String> getTrusted() {
            return this.trusted;
        }

        public void setTrusted(List<String> trusted) {
            this.trusted = trusted;
        }
    }
}
