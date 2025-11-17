package org.springframework.boot.autoconfigure.pulsar;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.pulsar.client.api.CompressionType;
import org.apache.pulsar.client.api.HashingScheme;
import org.apache.pulsar.client.api.MessageRoutingMode;
import org.apache.pulsar.client.api.ProducerAccessMode;
import org.apache.pulsar.client.api.RegexSubscriptionMode;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;
import org.apache.pulsar.client.api.SubscriptionMode;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.common.schema.SchemaType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.Assert;

@ConfigurationProperties("spring.pulsar")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarProperties.class */
public class PulsarProperties {
    private final Client client = new Client();
    private final Admin admin = new Admin();
    private final Defaults defaults = new Defaults();
    private final Function function = new Function();
    private final Producer producer = new Producer();
    private final Consumer consumer = new Consumer();
    private final Listener listener = new Listener();
    private final Reader reader = new Reader();
    private final Template template = new Template();

    public Client getClient() {
        return this.client;
    }

    public Admin getAdmin() {
        return this.admin;
    }

    public Defaults getDefaults() {
        return this.defaults;
    }

    public Producer getProducer() {
        return this.producer;
    }

    public Consumer getConsumer() {
        return this.consumer;
    }

    public Listener getListener() {
        return this.listener;
    }

    public Reader getReader() {
        return this.reader;
    }

    public Function getFunction() {
        return this.function;
    }

    public Template getTemplate() {
        return this.template;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarProperties$Client.class */
    public static class Client {
        private Duration lookupTimeout;
        private String serviceUrl = "pulsar://localhost:6650";
        private Duration operationTimeout = Duration.ofSeconds(30);
        private Duration connectionTimeout = Duration.ofSeconds(10);
        private final Authentication authentication = new Authentication();

        public String getServiceUrl() {
            return this.serviceUrl;
        }

        public void setServiceUrl(String serviceUrl) {
            this.serviceUrl = serviceUrl;
        }

        public Duration getOperationTimeout() {
            return this.operationTimeout;
        }

        public void setOperationTimeout(Duration operationTimeout) {
            this.operationTimeout = operationTimeout;
        }

        public Duration getLookupTimeout() {
            return this.lookupTimeout;
        }

        public void setLookupTimeout(Duration lookupTimeout) {
            this.lookupTimeout = lookupTimeout;
        }

        public Duration getConnectionTimeout() {
            return this.connectionTimeout;
        }

        public void setConnectionTimeout(Duration connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        public Authentication getAuthentication() {
            return this.authentication;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarProperties$Admin.class */
    public static class Admin {
        private String serviceUrl = "http://localhost:8080";
        private Duration connectionTimeout = Duration.ofMinutes(1);
        private Duration readTimeout = Duration.ofMinutes(1);
        private Duration requestTimeout = Duration.ofMinutes(5);
        private final Authentication authentication = new Authentication();

        public String getServiceUrl() {
            return this.serviceUrl;
        }

        public void setServiceUrl(String serviceUrl) {
            this.serviceUrl = serviceUrl;
        }

        public Duration getConnectionTimeout() {
            return this.connectionTimeout;
        }

        public void setConnectionTimeout(Duration connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        public Duration getReadTimeout() {
            return this.readTimeout;
        }

        public void setReadTimeout(Duration readTimeout) {
            this.readTimeout = readTimeout;
        }

        public Duration getRequestTimeout() {
            return this.requestTimeout;
        }

        public void setRequestTimeout(Duration requestTimeout) {
            this.requestTimeout = requestTimeout;
        }

        public Authentication getAuthentication() {
            return this.authentication;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarProperties$Defaults.class */
    public static class Defaults {
        private List<TypeMapping> typeMappings = new ArrayList();

        public List<TypeMapping> getTypeMappings() {
            return this.typeMappings;
        }

        public void setTypeMappings(List<TypeMapping> typeMappings) {
            this.typeMappings = typeMappings;
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarProperties$Defaults$TypeMapping.class */
        public static final class TypeMapping extends Record {
            private final Class<?> messageType;
            private final String topicName;
            private final SchemaInfo schemaInfo;

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, TypeMapping.class), TypeMapping.class, "messageType;topicName;schemaInfo", "FIELD:Lorg/springframework/boot/autoconfigure/pulsar/PulsarProperties$Defaults$TypeMapping;->messageType:Ljava/lang/Class;", "FIELD:Lorg/springframework/boot/autoconfigure/pulsar/PulsarProperties$Defaults$TypeMapping;->topicName:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/autoconfigure/pulsar/PulsarProperties$Defaults$TypeMapping;->schemaInfo:Lorg/springframework/boot/autoconfigure/pulsar/PulsarProperties$Defaults$SchemaInfo;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, TypeMapping.class), TypeMapping.class, "messageType;topicName;schemaInfo", "FIELD:Lorg/springframework/boot/autoconfigure/pulsar/PulsarProperties$Defaults$TypeMapping;->messageType:Ljava/lang/Class;", "FIELD:Lorg/springframework/boot/autoconfigure/pulsar/PulsarProperties$Defaults$TypeMapping;->topicName:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/autoconfigure/pulsar/PulsarProperties$Defaults$TypeMapping;->schemaInfo:Lorg/springframework/boot/autoconfigure/pulsar/PulsarProperties$Defaults$SchemaInfo;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, TypeMapping.class, Object.class), TypeMapping.class, "messageType;topicName;schemaInfo", "FIELD:Lorg/springframework/boot/autoconfigure/pulsar/PulsarProperties$Defaults$TypeMapping;->messageType:Ljava/lang/Class;", "FIELD:Lorg/springframework/boot/autoconfigure/pulsar/PulsarProperties$Defaults$TypeMapping;->topicName:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/autoconfigure/pulsar/PulsarProperties$Defaults$TypeMapping;->schemaInfo:Lorg/springframework/boot/autoconfigure/pulsar/PulsarProperties$Defaults$SchemaInfo;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Class<?> messageType() {
                return this.messageType;
            }

            public String topicName() {
                return this.topicName;
            }

            public SchemaInfo schemaInfo() {
                return this.schemaInfo;
            }

            public TypeMapping(Class<?> messageType, String topicName, SchemaInfo schemaInfo) {
                Assert.notNull(messageType, "messageType must not be null");
                Assert.isTrue((topicName == null && schemaInfo == null) ? false : true, "At least one of topicName or schemaInfo must not be null");
                this.messageType = messageType;
                this.topicName = topicName;
                this.schemaInfo = schemaInfo;
            }
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarProperties$Defaults$SchemaInfo.class */
        public static final class SchemaInfo extends Record {
            private final SchemaType schemaType;
            private final Class<?> messageKeyType;

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, SchemaInfo.class), SchemaInfo.class, "schemaType;messageKeyType", "FIELD:Lorg/springframework/boot/autoconfigure/pulsar/PulsarProperties$Defaults$SchemaInfo;->schemaType:Lorg/apache/pulsar/common/schema/SchemaType;", "FIELD:Lorg/springframework/boot/autoconfigure/pulsar/PulsarProperties$Defaults$SchemaInfo;->messageKeyType:Ljava/lang/Class;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, SchemaInfo.class), SchemaInfo.class, "schemaType;messageKeyType", "FIELD:Lorg/springframework/boot/autoconfigure/pulsar/PulsarProperties$Defaults$SchemaInfo;->schemaType:Lorg/apache/pulsar/common/schema/SchemaType;", "FIELD:Lorg/springframework/boot/autoconfigure/pulsar/PulsarProperties$Defaults$SchemaInfo;->messageKeyType:Ljava/lang/Class;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, SchemaInfo.class, Object.class), SchemaInfo.class, "schemaType;messageKeyType", "FIELD:Lorg/springframework/boot/autoconfigure/pulsar/PulsarProperties$Defaults$SchemaInfo;->schemaType:Lorg/apache/pulsar/common/schema/SchemaType;", "FIELD:Lorg/springframework/boot/autoconfigure/pulsar/PulsarProperties$Defaults$SchemaInfo;->messageKeyType:Ljava/lang/Class;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public SchemaType schemaType() {
                return this.schemaType;
            }

            public Class<?> messageKeyType() {
                return this.messageKeyType;
            }

            public SchemaInfo(SchemaType schemaType, Class<?> messageKeyType) {
                Assert.notNull(schemaType, "schemaType must not be null");
                Assert.isTrue(schemaType != SchemaType.NONE, "schemaType 'NONE' not supported");
                Assert.isTrue(messageKeyType == null || schemaType == SchemaType.KEY_VALUE, "messageKeyType can only be set when schemaType is KEY_VALUE");
                this.schemaType = schemaType;
                this.messageKeyType = messageKeyType;
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarProperties$Function.class */
    public static class Function {
        private boolean failFast = true;
        private boolean propagateFailures = true;
        private boolean propagateStopFailures = false;

        public boolean isFailFast() {
            return this.failFast;
        }

        public void setFailFast(boolean failFast) {
            this.failFast = failFast;
        }

        public boolean isPropagateFailures() {
            return this.propagateFailures;
        }

        public void setPropagateFailures(boolean propagateFailures) {
            this.propagateFailures = propagateFailures;
        }

        public boolean isPropagateStopFailures() {
            return this.propagateStopFailures;
        }

        public void setPropagateStopFailures(boolean propagateStopFailures) {
            this.propagateStopFailures = propagateStopFailures;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarProperties$Producer.class */
    public static class Producer {
        private String name;
        private String topicName;
        private boolean chunkingEnabled;
        private CompressionType compressionType;
        private Duration sendTimeout = Duration.ofSeconds(30);
        private MessageRoutingMode messageRoutingMode = MessageRoutingMode.RoundRobinPartition;
        private HashingScheme hashingScheme = HashingScheme.JavaStringHash;
        private boolean batchingEnabled = true;
        private ProducerAccessMode accessMode = ProducerAccessMode.Shared;
        private final Cache cache = new Cache();

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTopicName() {
            return this.topicName;
        }

        public void setTopicName(String topicName) {
            this.topicName = topicName;
        }

        public Duration getSendTimeout() {
            return this.sendTimeout;
        }

        public void setSendTimeout(Duration sendTimeout) {
            this.sendTimeout = sendTimeout;
        }

        public MessageRoutingMode getMessageRoutingMode() {
            return this.messageRoutingMode;
        }

        public void setMessageRoutingMode(MessageRoutingMode messageRoutingMode) {
            this.messageRoutingMode = messageRoutingMode;
        }

        public HashingScheme getHashingScheme() {
            return this.hashingScheme;
        }

        public void setHashingScheme(HashingScheme hashingScheme) {
            this.hashingScheme = hashingScheme;
        }

        public boolean isBatchingEnabled() {
            return this.batchingEnabled;
        }

        public void setBatchingEnabled(boolean batchingEnabled) {
            this.batchingEnabled = batchingEnabled;
        }

        public boolean isChunkingEnabled() {
            return this.chunkingEnabled;
        }

        public void setChunkingEnabled(boolean chunkingEnabled) {
            this.chunkingEnabled = chunkingEnabled;
        }

        public CompressionType getCompressionType() {
            return this.compressionType;
        }

        public void setCompressionType(CompressionType compressionType) {
            this.compressionType = compressionType;
        }

        public ProducerAccessMode getAccessMode() {
            return this.accessMode;
        }

        public void setAccessMode(ProducerAccessMode accessMode) {
            this.accessMode = accessMode;
        }

        public Cache getCache() {
            return this.cache;
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarProperties$Producer$Cache.class */
        public static class Cache {
            private Duration expireAfterAccess = Duration.ofMinutes(1);
            private long maximumSize = 1000;
            private int initialCapacity = 50;

            public Duration getExpireAfterAccess() {
                return this.expireAfterAccess;
            }

            public void setExpireAfterAccess(Duration expireAfterAccess) {
                this.expireAfterAccess = expireAfterAccess;
            }

            public long getMaximumSize() {
                return this.maximumSize;
            }

            public void setMaximumSize(long maximumSize) {
                this.maximumSize = maximumSize;
            }

            public int getInitialCapacity() {
                return this.initialCapacity;
            }

            public void setInitialCapacity(int initialCapacity) {
                this.initialCapacity = initialCapacity;
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarProperties$Consumer.class */
    public static class Consumer {
        private String name;
        private List<String> topics;
        private Pattern topicsPattern;

        @NestedConfigurationProperty
        private DeadLetterPolicy deadLetterPolicy;
        private int priorityLevel = 0;
        private boolean readCompacted = false;
        private final Subscription subscription = new Subscription();
        private boolean retryEnable = false;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Subscription getSubscription() {
            return this.subscription;
        }

        public List<String> getTopics() {
            return this.topics;
        }

        public void setTopics(List<String> topics) {
            this.topics = topics;
        }

        public Pattern getTopicsPattern() {
            return this.topicsPattern;
        }

        public void setTopicsPattern(Pattern topicsPattern) {
            this.topicsPattern = topicsPattern;
        }

        public int getPriorityLevel() {
            return this.priorityLevel;
        }

        public void setPriorityLevel(int priorityLevel) {
            this.priorityLevel = priorityLevel;
        }

        public boolean isReadCompacted() {
            return this.readCompacted;
        }

        public void setReadCompacted(boolean readCompacted) {
            this.readCompacted = readCompacted;
        }

        public DeadLetterPolicy getDeadLetterPolicy() {
            return this.deadLetterPolicy;
        }

        public void setDeadLetterPolicy(DeadLetterPolicy deadLetterPolicy) {
            this.deadLetterPolicy = deadLetterPolicy;
        }

        public boolean isRetryEnable() {
            return this.retryEnable;
        }

        public void setRetryEnable(boolean retryEnable) {
            this.retryEnable = retryEnable;
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarProperties$Consumer$Subscription.class */
        public static class Subscription {
            private String name;
            private SubscriptionInitialPosition initialPosition = SubscriptionInitialPosition.Latest;
            private SubscriptionMode mode = SubscriptionMode.Durable;
            private RegexSubscriptionMode topicsMode = RegexSubscriptionMode.PersistentOnly;
            private SubscriptionType type = SubscriptionType.Exclusive;

            public String getName() {
                return this.name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public SubscriptionInitialPosition getInitialPosition() {
                return this.initialPosition;
            }

            public void setInitialPosition(SubscriptionInitialPosition initialPosition) {
                this.initialPosition = initialPosition;
            }

            public SubscriptionMode getMode() {
                return this.mode;
            }

            public void setMode(SubscriptionMode mode) {
                this.mode = mode;
            }

            public RegexSubscriptionMode getTopicsMode() {
                return this.topicsMode;
            }

            public void setTopicsMode(RegexSubscriptionMode topicsMode) {
                this.topicsMode = topicsMode;
            }

            public SubscriptionType getType() {
                return this.type;
            }

            public void setType(SubscriptionType type) {
                this.type = type;
            }
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarProperties$Consumer$DeadLetterPolicy.class */
        public static class DeadLetterPolicy {
            private int maxRedeliverCount;
            private String retryLetterTopic;
            private String deadLetterTopic;
            private String initialSubscriptionName;

            public int getMaxRedeliverCount() {
                return this.maxRedeliverCount;
            }

            public void setMaxRedeliverCount(int maxRedeliverCount) {
                this.maxRedeliverCount = maxRedeliverCount;
            }

            public String getRetryLetterTopic() {
                return this.retryLetterTopic;
            }

            public void setRetryLetterTopic(String retryLetterTopic) {
                this.retryLetterTopic = retryLetterTopic;
            }

            public String getDeadLetterTopic() {
                return this.deadLetterTopic;
            }

            public void setDeadLetterTopic(String deadLetterTopic) {
                this.deadLetterTopic = deadLetterTopic;
            }

            public String getInitialSubscriptionName() {
                return this.initialSubscriptionName;
            }

            public void setInitialSubscriptionName(String initialSubscriptionName) {
                this.initialSubscriptionName = initialSubscriptionName;
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarProperties$Listener.class */
    public static class Listener {
        private SchemaType schemaType;
        private boolean observationEnabled = true;

        public SchemaType getSchemaType() {
            return this.schemaType;
        }

        public void setSchemaType(SchemaType schemaType) {
            this.schemaType = schemaType;
        }

        public boolean isObservationEnabled() {
            return this.observationEnabled;
        }

        public void setObservationEnabled(boolean observationEnabled) {
            this.observationEnabled = observationEnabled;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarProperties$Reader.class */
    public static class Reader {
        private String name;
        private List<String> topics;
        private String subscriptionName;
        private String subscriptionRolePrefix;
        private boolean readCompacted;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getTopics() {
            return this.topics;
        }

        public void setTopics(List<String> topics) {
            this.topics = topics;
        }

        public String getSubscriptionName() {
            return this.subscriptionName;
        }

        public void setSubscriptionName(String subscriptionName) {
            this.subscriptionName = subscriptionName;
        }

        public String getSubscriptionRolePrefix() {
            return this.subscriptionRolePrefix;
        }

        public void setSubscriptionRolePrefix(String subscriptionRolePrefix) {
            this.subscriptionRolePrefix = subscriptionRolePrefix;
        }

        public boolean isReadCompacted() {
            return this.readCompacted;
        }

        public void setReadCompacted(boolean readCompacted) {
            this.readCompacted = readCompacted;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarProperties$Template.class */
    public static class Template {
        private boolean observationsEnabled = true;

        public boolean isObservationsEnabled() {
            return this.observationsEnabled;
        }

        public void setObservationsEnabled(boolean observationsEnabled) {
            this.observationsEnabled = observationsEnabled;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarProperties$Authentication.class */
    public static class Authentication {
        private String pluginClassName;
        private Map<String, String> param = new LinkedHashMap();

        public String getPluginClassName() {
            return this.pluginClassName;
        }

        public void setPluginClassName(String pluginClassName) {
            this.pluginClassName = pluginClassName;
        }

        public Map<String, String> getParam() {
            return this.param;
        }

        public void setParam(Map<String, String> param) {
            this.param = param;
        }
    }
}
