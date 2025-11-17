package org.springframework.boot.autoconfigure.jms;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

@ConfigurationProperties(prefix = "spring.jms")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/JmsProperties.class */
public class JmsProperties {
    private String jndiName;
    private boolean pubSubDomain = false;
    private final Cache cache = new Cache();
    private final Listener listener = new Listener();
    private final Template template = new Template();

    public boolean isPubSubDomain() {
        return this.pubSubDomain;
    }

    public void setPubSubDomain(boolean pubSubDomain) {
        this.pubSubDomain = pubSubDomain;
    }

    public String getJndiName() {
        return this.jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public Cache getCache() {
        return this.cache;
    }

    public Listener getListener() {
        return this.listener;
    }

    public Template getTemplate() {
        return this.template;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/JmsProperties$Cache.class */
    public static class Cache {
        private boolean enabled = true;
        private boolean consumers = false;
        private boolean producers = true;
        private int sessionCacheSize = 1;

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isConsumers() {
            return this.consumers;
        }

        public void setConsumers(boolean consumers) {
            this.consumers = consumers;
        }

        public boolean isProducers() {
            return this.producers;
        }

        public void setProducers(boolean producers) {
            this.producers = producers;
        }

        public int getSessionCacheSize() {
            return this.sessionCacheSize;
        }

        public void setSessionCacheSize(int sessionCacheSize) {
            this.sessionCacheSize = sessionCacheSize;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/JmsProperties$Listener.class */
    public static class Listener {
        private Integer minConcurrency;
        private Integer maxConcurrency;
        private boolean autoStartup = true;
        private Duration receiveTimeout = Duration.ofSeconds(1);
        private final Session session = new Session();

        public boolean isAutoStartup() {
            return this.autoStartup;
        }

        public void setAutoStartup(boolean autoStartup) {
            this.autoStartup = autoStartup;
        }

        @DeprecatedConfigurationProperty(replacement = "spring.jms.listener.session.acknowledge-mode", since = "3.2.0")
        @Deprecated(since = "3.2.0", forRemoval = true)
        public AcknowledgeMode getAcknowledgeMode() {
            return this.session.getAcknowledgeMode();
        }

        @Deprecated(since = "3.2.0", forRemoval = true)
        public void setAcknowledgeMode(AcknowledgeMode acknowledgeMode) {
            this.session.setAcknowledgeMode(acknowledgeMode);
        }

        @DeprecatedConfigurationProperty(replacement = "spring.jms.listener.min-concurrency", since = "3.2.0")
        @Deprecated(since = "3.2.0", forRemoval = true)
        public Integer getConcurrency() {
            return this.minConcurrency;
        }

        @Deprecated(since = "3.2.0", forRemoval = true)
        public void setConcurrency(Integer concurrency) {
            this.minConcurrency = concurrency;
        }

        public Integer getMinConcurrency() {
            return this.minConcurrency;
        }

        public void setMinConcurrency(Integer minConcurrency) {
            this.minConcurrency = minConcurrency;
        }

        public Integer getMaxConcurrency() {
            return this.maxConcurrency;
        }

        public void setMaxConcurrency(Integer maxConcurrency) {
            this.maxConcurrency = maxConcurrency;
        }

        public String formatConcurrency() {
            if (this.minConcurrency != null) {
                return this.minConcurrency + "-" + (this.maxConcurrency != null ? this.maxConcurrency : this.minConcurrency);
            }
            if (this.maxConcurrency != null) {
                return "1-" + this.maxConcurrency;
            }
            return null;
        }

        public Duration getReceiveTimeout() {
            return this.receiveTimeout;
        }

        public void setReceiveTimeout(Duration receiveTimeout) {
            this.receiveTimeout = receiveTimeout;
        }

        public Session getSession() {
            return this.session;
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/JmsProperties$Listener$Session.class */
        public static class Session {
            private AcknowledgeMode acknowledgeMode = AcknowledgeMode.AUTO;
            private Boolean transacted;

            public AcknowledgeMode getAcknowledgeMode() {
                return this.acknowledgeMode;
            }

            public void setAcknowledgeMode(AcknowledgeMode acknowledgeMode) {
                this.acknowledgeMode = acknowledgeMode;
            }

            public Boolean getTransacted() {
                return this.transacted;
            }

            public void setTransacted(Boolean transacted) {
                this.transacted = transacted;
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/JmsProperties$Template.class */
    public static class Template {
        private String defaultDestination;
        private Duration deliveryDelay;
        private DeliveryMode deliveryMode;
        private Integer priority;
        private Duration timeToLive;
        private Boolean qosEnabled;
        private Duration receiveTimeout;
        private final Session session = new Session();

        public String getDefaultDestination() {
            return this.defaultDestination;
        }

        public void setDefaultDestination(String defaultDestination) {
            this.defaultDestination = defaultDestination;
        }

        public Duration getDeliveryDelay() {
            return this.deliveryDelay;
        }

        public void setDeliveryDelay(Duration deliveryDelay) {
            this.deliveryDelay = deliveryDelay;
        }

        public DeliveryMode getDeliveryMode() {
            return this.deliveryMode;
        }

        public void setDeliveryMode(DeliveryMode deliveryMode) {
            this.deliveryMode = deliveryMode;
        }

        public Integer getPriority() {
            return this.priority;
        }

        public void setPriority(Integer priority) {
            this.priority = priority;
        }

        public Duration getTimeToLive() {
            return this.timeToLive;
        }

        public void setTimeToLive(Duration timeToLive) {
            this.timeToLive = timeToLive;
        }

        public boolean determineQosEnabled() {
            if (this.qosEnabled != null) {
                return this.qosEnabled.booleanValue();
            }
            return (getDeliveryMode() == null && getPriority() == null && getTimeToLive() == null) ? false : true;
        }

        public Boolean getQosEnabled() {
            return this.qosEnabled;
        }

        public void setQosEnabled(Boolean qosEnabled) {
            this.qosEnabled = qosEnabled;
        }

        public Duration getReceiveTimeout() {
            return this.receiveTimeout;
        }

        public void setReceiveTimeout(Duration receiveTimeout) {
            this.receiveTimeout = receiveTimeout;
        }

        public Session getSession() {
            return this.session;
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/JmsProperties$Template$Session.class */
        public static class Session {
            private AcknowledgeMode acknowledgeMode = AcknowledgeMode.AUTO;
            private boolean transacted = false;

            public AcknowledgeMode getAcknowledgeMode() {
                return this.acknowledgeMode;
            }

            public void setAcknowledgeMode(AcknowledgeMode acknowledgeMode) {
                this.acknowledgeMode = acknowledgeMode;
            }

            public boolean isTransacted() {
                return this.transacted;
            }

            public void setTransacted(boolean transacted) {
                this.transacted = transacted;
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/JmsProperties$DeliveryMode.class */
    public enum DeliveryMode {
        NON_PERSISTENT(1),
        PERSISTENT(2);

        private final int value;

        DeliveryMode(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }
}
