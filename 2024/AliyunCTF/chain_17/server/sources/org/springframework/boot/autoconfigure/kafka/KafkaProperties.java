package org.springframework.boot.autoconfigure.kafka;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.context.properties.source.MutuallyExclusiveConfigurationPropertiesException;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.core.io.Resource;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.security.jaas.KafkaJaasLoginModuleInitializer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties(prefix = "spring.kafka")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties.class */
public class KafkaProperties {
    private String clientId;
    private List<String> bootstrapServers = new ArrayList(Collections.singletonList("localhost:9092"));
    private final Map<String, String> properties = new HashMap();
    private final Consumer consumer = new Consumer();
    private final Producer producer = new Producer();
    private final Admin admin = new Admin();
    private final Streams streams = new Streams();
    private final Listener listener = new Listener();
    private final Ssl ssl = new Ssl();
    private final Jaas jaas = new Jaas();
    private final Template template = new Template();
    private final Security security = new Security();
    private final Retry retry = new Retry();

    public List<String> getBootstrapServers() {
        return this.bootstrapServers;
    }

    public void setBootstrapServers(List<String> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public Consumer getConsumer() {
        return this.consumer;
    }

    public Producer getProducer() {
        return this.producer;
    }

    public Listener getListener() {
        return this.listener;
    }

    public Admin getAdmin() {
        return this.admin;
    }

    public Streams getStreams() {
        return this.streams;
    }

    public Ssl getSsl() {
        return this.ssl;
    }

    public Jaas getJaas() {
        return this.jaas;
    }

    public Template getTemplate() {
        return this.template;
    }

    public Security getSecurity() {
        return this.security;
    }

    public Retry getRetry() {
        return this.retry;
    }

    private Map<String, Object> buildCommonProperties(SslBundles sslBundles) {
        Map<String, Object> properties = new HashMap<>();
        if (this.bootstrapServers != null) {
            properties.put("bootstrap.servers", this.bootstrapServers);
        }
        if (this.clientId != null) {
            properties.put("client.id", this.clientId);
        }
        properties.putAll(this.ssl.buildProperties(sslBundles));
        properties.putAll(this.security.buildProperties());
        if (!CollectionUtils.isEmpty(this.properties)) {
            properties.putAll(this.properties);
        }
        return properties;
    }

    @Deprecated(since = "3.2.0", forRemoval = true)
    public Map<String, Object> buildConsumerProperties() {
        return buildConsumerProperties(null);
    }

    public Map<String, Object> buildConsumerProperties(SslBundles sslBundles) {
        Map<String, Object> properties = buildCommonProperties(sslBundles);
        properties.putAll(this.consumer.buildProperties(sslBundles));
        return properties;
    }

    @Deprecated(since = "3.2.0", forRemoval = true)
    public Map<String, Object> buildProducerProperties() {
        return buildProducerProperties(null);
    }

    public Map<String, Object> buildProducerProperties(SslBundles sslBundles) {
        Map<String, Object> properties = buildCommonProperties(sslBundles);
        properties.putAll(this.producer.buildProperties(sslBundles));
        return properties;
    }

    @Deprecated(since = "3.2.0", forRemoval = true)
    public Map<String, Object> buildAdminProperties() {
        return buildAdminProperties(null);
    }

    public Map<String, Object> buildAdminProperties(SslBundles sslBundles) {
        Map<String, Object> properties = buildCommonProperties(sslBundles);
        properties.putAll(this.admin.buildProperties(sslBundles));
        return properties;
    }

    @Deprecated(since = "3.2.0", forRemoval = true)
    public Map<String, Object> buildStreamsProperties() {
        return buildStreamsProperties(null);
    }

    public Map<String, Object> buildStreamsProperties(SslBundles sslBundles) {
        Map<String, Object> properties = buildCommonProperties(sslBundles);
        properties.putAll(this.streams.buildProperties(sslBundles));
        return properties;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Consumer.class */
    public static class Consumer {
        private Duration autoCommitInterval;
        private String autoOffsetReset;
        private List<String> bootstrapServers;
        private String clientId;
        private Boolean enableAutoCommit;
        private Duration fetchMaxWait;
        private DataSize fetchMinSize;
        private String groupId;
        private Duration heartbeatInterval;
        private Integer maxPollRecords;
        private final Ssl ssl = new Ssl();
        private final Security security = new Security();
        private IsolationLevel isolationLevel = IsolationLevel.READ_UNCOMMITTED;
        private Class<?> keyDeserializer = StringDeserializer.class;
        private Class<?> valueDeserializer = StringDeserializer.class;
        private final Map<String, String> properties = new HashMap();

        public Ssl getSsl() {
            return this.ssl;
        }

        public Security getSecurity() {
            return this.security;
        }

        public Duration getAutoCommitInterval() {
            return this.autoCommitInterval;
        }

        public void setAutoCommitInterval(Duration autoCommitInterval) {
            this.autoCommitInterval = autoCommitInterval;
        }

        public String getAutoOffsetReset() {
            return this.autoOffsetReset;
        }

        public void setAutoOffsetReset(String autoOffsetReset) {
            this.autoOffsetReset = autoOffsetReset;
        }

        public List<String> getBootstrapServers() {
            return this.bootstrapServers;
        }

        public void setBootstrapServers(List<String> bootstrapServers) {
            this.bootstrapServers = bootstrapServers;
        }

        public String getClientId() {
            return this.clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public Boolean getEnableAutoCommit() {
            return this.enableAutoCommit;
        }

        public void setEnableAutoCommit(Boolean enableAutoCommit) {
            this.enableAutoCommit = enableAutoCommit;
        }

        public Duration getFetchMaxWait() {
            return this.fetchMaxWait;
        }

        public void setFetchMaxWait(Duration fetchMaxWait) {
            this.fetchMaxWait = fetchMaxWait;
        }

        public DataSize getFetchMinSize() {
            return this.fetchMinSize;
        }

        public void setFetchMinSize(DataSize fetchMinSize) {
            this.fetchMinSize = fetchMinSize;
        }

        public String getGroupId() {
            return this.groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public Duration getHeartbeatInterval() {
            return this.heartbeatInterval;
        }

        public void setHeartbeatInterval(Duration heartbeatInterval) {
            this.heartbeatInterval = heartbeatInterval;
        }

        public IsolationLevel getIsolationLevel() {
            return this.isolationLevel;
        }

        public void setIsolationLevel(IsolationLevel isolationLevel) {
            this.isolationLevel = isolationLevel;
        }

        public Class<?> getKeyDeserializer() {
            return this.keyDeserializer;
        }

        public void setKeyDeserializer(Class<?> keyDeserializer) {
            this.keyDeserializer = keyDeserializer;
        }

        public Class<?> getValueDeserializer() {
            return this.valueDeserializer;
        }

        public void setValueDeserializer(Class<?> valueDeserializer) {
            this.valueDeserializer = valueDeserializer;
        }

        public Integer getMaxPollRecords() {
            return this.maxPollRecords;
        }

        public void setMaxPollRecords(Integer maxPollRecords) {
            this.maxPollRecords = maxPollRecords;
        }

        public Map<String, String> getProperties() {
            return this.properties;
        }

        public Map<String, Object> buildProperties(SslBundles sslBundles) {
            Properties properties = new Properties();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            map.from(this::getAutoCommitInterval).asInt((v0) -> {
                return v0.toMillis();
            }).to(properties.in("auto.commit.interval.ms"));
            map.from(this::getAutoOffsetReset).to(properties.in("auto.offset.reset"));
            map.from(this::getBootstrapServers).to(properties.in("bootstrap.servers"));
            map.from(this::getClientId).to(properties.in("client.id"));
            map.from(this::getEnableAutoCommit).to(properties.in("enable.auto.commit"));
            map.from(this::getFetchMaxWait).asInt((v0) -> {
                return v0.toMillis();
            }).to(properties.in("fetch.max.wait.ms"));
            map.from(this::getFetchMinSize).asInt((v0) -> {
                return v0.toBytes();
            }).to(properties.in("fetch.min.bytes"));
            map.from(this::getGroupId).to(properties.in("group.id"));
            map.from(this::getHeartbeatInterval).asInt((v0) -> {
                return v0.toMillis();
            }).to(properties.in("heartbeat.interval.ms"));
            map.from(() -> {
                return getIsolationLevel().name().toLowerCase(Locale.ROOT);
            }).to(properties.in("isolation.level"));
            map.from(this::getKeyDeserializer).to(properties.in("key.deserializer"));
            map.from(this::getValueDeserializer).to(properties.in("value.deserializer"));
            map.from(this::getMaxPollRecords).to(properties.in("max.poll.records"));
            return properties.with(this.ssl, this.security, this.properties, sslBundles);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Producer.class */
    public static class Producer {
        private String acks;
        private DataSize batchSize;
        private List<String> bootstrapServers;
        private DataSize bufferMemory;
        private String clientId;
        private String compressionType;
        private Integer retries;
        private String transactionIdPrefix;
        private final Ssl ssl = new Ssl();
        private final Security security = new Security();
        private Class<?> keySerializer = StringSerializer.class;
        private Class<?> valueSerializer = StringSerializer.class;
        private final Map<String, String> properties = new HashMap();

        public Ssl getSsl() {
            return this.ssl;
        }

        public Security getSecurity() {
            return this.security;
        }

        public String getAcks() {
            return this.acks;
        }

        public void setAcks(String acks) {
            this.acks = acks;
        }

        public DataSize getBatchSize() {
            return this.batchSize;
        }

        public void setBatchSize(DataSize batchSize) {
            this.batchSize = batchSize;
        }

        public List<String> getBootstrapServers() {
            return this.bootstrapServers;
        }

        public void setBootstrapServers(List<String> bootstrapServers) {
            this.bootstrapServers = bootstrapServers;
        }

        public DataSize getBufferMemory() {
            return this.bufferMemory;
        }

        public void setBufferMemory(DataSize bufferMemory) {
            this.bufferMemory = bufferMemory;
        }

        public String getClientId() {
            return this.clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getCompressionType() {
            return this.compressionType;
        }

        public void setCompressionType(String compressionType) {
            this.compressionType = compressionType;
        }

        public Class<?> getKeySerializer() {
            return this.keySerializer;
        }

        public void setKeySerializer(Class<?> keySerializer) {
            this.keySerializer = keySerializer;
        }

        public Class<?> getValueSerializer() {
            return this.valueSerializer;
        }

        public void setValueSerializer(Class<?> valueSerializer) {
            this.valueSerializer = valueSerializer;
        }

        public Integer getRetries() {
            return this.retries;
        }

        public void setRetries(Integer retries) {
            this.retries = retries;
        }

        public String getTransactionIdPrefix() {
            return this.transactionIdPrefix;
        }

        public void setTransactionIdPrefix(String transactionIdPrefix) {
            this.transactionIdPrefix = transactionIdPrefix;
        }

        public Map<String, String> getProperties() {
            return this.properties;
        }

        public Map<String, Object> buildProperties(SslBundles sslBundles) {
            Properties properties = new Properties();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            map.from(this::getAcks).to(properties.in("acks"));
            map.from(this::getBatchSize).asInt((v0) -> {
                return v0.toBytes();
            }).to(properties.in("batch.size"));
            map.from(this::getBootstrapServers).to(properties.in("bootstrap.servers"));
            map.from(this::getBufferMemory).as((v0) -> {
                return v0.toBytes();
            }).to(properties.in("buffer.memory"));
            map.from(this::getClientId).to(properties.in("client.id"));
            map.from(this::getCompressionType).to(properties.in("compression.type"));
            map.from(this::getKeySerializer).to(properties.in("key.serializer"));
            map.from(this::getRetries).to(properties.in("retries"));
            map.from(this::getValueSerializer).to(properties.in("value.serializer"));
            return properties.with(this.ssl, this.security, this.properties, sslBundles);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Admin.class */
    public static class Admin {
        private String clientId;
        private Duration closeTimeout;
        private Duration operationTimeout;
        private boolean failFast;
        private boolean modifyTopicConfigs;
        private final Ssl ssl = new Ssl();
        private final Security security = new Security();
        private final Map<String, String> properties = new HashMap();
        private boolean autoCreate = true;

        public Ssl getSsl() {
            return this.ssl;
        }

        public Security getSecurity() {
            return this.security;
        }

        public String getClientId() {
            return this.clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public Duration getCloseTimeout() {
            return this.closeTimeout;
        }

        public void setCloseTimeout(Duration closeTimeout) {
            this.closeTimeout = closeTimeout;
        }

        public Duration getOperationTimeout() {
            return this.operationTimeout;
        }

        public void setOperationTimeout(Duration operationTimeout) {
            this.operationTimeout = operationTimeout;
        }

        public boolean isFailFast() {
            return this.failFast;
        }

        public void setFailFast(boolean failFast) {
            this.failFast = failFast;
        }

        public boolean isModifyTopicConfigs() {
            return this.modifyTopicConfigs;
        }

        public void setModifyTopicConfigs(boolean modifyTopicConfigs) {
            this.modifyTopicConfigs = modifyTopicConfigs;
        }

        public boolean isAutoCreate() {
            return this.autoCreate;
        }

        public void setAutoCreate(boolean autoCreate) {
            this.autoCreate = autoCreate;
        }

        public Map<String, String> getProperties() {
            return this.properties;
        }

        public Map<String, Object> buildProperties(SslBundles sslBundles) {
            Properties properties = new Properties();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            map.from(this::getClientId).to(properties.in("client.id"));
            return properties.with(this.ssl, this.security, this.properties, sslBundles);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Streams.class */
    public static class Streams {
        private String applicationId;
        private List<String> bootstrapServers;
        private DataSize cacheMaxSizeBuffering;
        private DataSize stateStoreCacheMaxSize;
        private String clientId;
        private Integer replicationFactor;
        private String stateDir;
        private final Ssl ssl = new Ssl();
        private final Security security = new Security();
        private final Cleanup cleanup = new Cleanup();
        private boolean autoStartup = true;
        private final Map<String, String> properties = new HashMap();

        public Ssl getSsl() {
            return this.ssl;
        }

        public Security getSecurity() {
            return this.security;
        }

        public Cleanup getCleanup() {
            return this.cleanup;
        }

        public String getApplicationId() {
            return this.applicationId;
        }

        public void setApplicationId(String applicationId) {
            this.applicationId = applicationId;
        }

        public boolean isAutoStartup() {
            return this.autoStartup;
        }

        public void setAutoStartup(boolean autoStartup) {
            this.autoStartup = autoStartup;
        }

        public List<String> getBootstrapServers() {
            return this.bootstrapServers;
        }

        public void setBootstrapServers(List<String> bootstrapServers) {
            this.bootstrapServers = bootstrapServers;
        }

        @DeprecatedConfigurationProperty(replacement = "spring.kafka.streams.state-store-cache-max-size", since = "3.1.0")
        @Deprecated(since = "3.1.0", forRemoval = true)
        public DataSize getCacheMaxSizeBuffering() {
            return this.cacheMaxSizeBuffering;
        }

        @Deprecated(since = "3.1.0", forRemoval = true)
        public void setCacheMaxSizeBuffering(DataSize cacheMaxSizeBuffering) {
            this.cacheMaxSizeBuffering = cacheMaxSizeBuffering;
        }

        public DataSize getStateStoreCacheMaxSize() {
            return this.stateStoreCacheMaxSize;
        }

        public void setStateStoreCacheMaxSize(DataSize stateStoreCacheMaxSize) {
            this.stateStoreCacheMaxSize = stateStoreCacheMaxSize;
        }

        public String getClientId() {
            return this.clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public Integer getReplicationFactor() {
            return this.replicationFactor;
        }

        public void setReplicationFactor(Integer replicationFactor) {
            this.replicationFactor = replicationFactor;
        }

        public String getStateDir() {
            return this.stateDir;
        }

        public void setStateDir(String stateDir) {
            this.stateDir = stateDir;
        }

        public Map<String, String> getProperties() {
            return this.properties;
        }

        public Map<String, Object> buildProperties(SslBundles sslBundles) {
            Properties properties = new Properties();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            map.from(this::getApplicationId).to(properties.in("application.id"));
            map.from(this::getBootstrapServers).to(properties.in("bootstrap.servers"));
            map.from(this::getCacheMaxSizeBuffering).asInt((v0) -> {
                return v0.toBytes();
            }).to(properties.in("cache.max.bytes.buffering"));
            map.from(this::getStateStoreCacheMaxSize).asInt((v0) -> {
                return v0.toBytes();
            }).to(properties.in("statestore.cache.max.bytes"));
            map.from(this::getClientId).to(properties.in("client.id"));
            map.from(this::getReplicationFactor).to(properties.in("replication.factor"));
            map.from(this::getStateDir).to(properties.in("state.dir"));
            return properties.with(this.ssl, this.security, this.properties, sslBundles);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Template.class */
    public static class Template {
        private String defaultTopic;
        private String transactionIdPrefix;
        private boolean observationEnabled;

        public String getDefaultTopic() {
            return this.defaultTopic;
        }

        public void setDefaultTopic(String defaultTopic) {
            this.defaultTopic = defaultTopic;
        }

        public String getTransactionIdPrefix() {
            return this.transactionIdPrefix;
        }

        public void setTransactionIdPrefix(String transactionIdPrefix) {
            this.transactionIdPrefix = transactionIdPrefix;
        }

        public boolean isObservationEnabled() {
            return this.observationEnabled;
        }

        public void setObservationEnabled(boolean observationEnabled) {
            this.observationEnabled = observationEnabled;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Listener.class */
    public static class Listener {
        private ContainerProperties.AckMode ackMode;
        private Boolean asyncAcks;
        private String clientId;
        private Integer concurrency;
        private Duration pollTimeout;
        private Float noPollThreshold;
        private Integer ackCount;
        private Duration ackTime;
        private Duration idleEventInterval;
        private Duration idlePartitionEventInterval;

        @DurationUnit(ChronoUnit.SECONDS)
        private Duration monitorInterval;
        private Boolean logContainerConfig;
        private Boolean changeConsumerThreadName;
        private boolean observationEnabled;
        private Type type = Type.SINGLE;
        private Duration idleBetweenPolls = Duration.ZERO;
        private boolean missingTopicsFatal = false;
        private boolean immediateStop = false;
        private boolean autoStartup = true;

        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Listener$Type.class */
        public enum Type {
            SINGLE,
            BATCH
        }

        public Type getType() {
            return this.type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public ContainerProperties.AckMode getAckMode() {
            return this.ackMode;
        }

        public void setAckMode(ContainerProperties.AckMode ackMode) {
            this.ackMode = ackMode;
        }

        public Boolean getAsyncAcks() {
            return this.asyncAcks;
        }

        public void setAsyncAcks(Boolean asyncAcks) {
            this.asyncAcks = asyncAcks;
        }

        public String getClientId() {
            return this.clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public Integer getConcurrency() {
            return this.concurrency;
        }

        public void setConcurrency(Integer concurrency) {
            this.concurrency = concurrency;
        }

        public Duration getPollTimeout() {
            return this.pollTimeout;
        }

        public void setPollTimeout(Duration pollTimeout) {
            this.pollTimeout = pollTimeout;
        }

        public Float getNoPollThreshold() {
            return this.noPollThreshold;
        }

        public void setNoPollThreshold(Float noPollThreshold) {
            this.noPollThreshold = noPollThreshold;
        }

        public Integer getAckCount() {
            return this.ackCount;
        }

        public void setAckCount(Integer ackCount) {
            this.ackCount = ackCount;
        }

        public Duration getAckTime() {
            return this.ackTime;
        }

        public void setAckTime(Duration ackTime) {
            this.ackTime = ackTime;
        }

        public Duration getIdleBetweenPolls() {
            return this.idleBetweenPolls;
        }

        public void setIdleBetweenPolls(Duration idleBetweenPolls) {
            this.idleBetweenPolls = idleBetweenPolls;
        }

        public Duration getIdleEventInterval() {
            return this.idleEventInterval;
        }

        public void setIdleEventInterval(Duration idleEventInterval) {
            this.idleEventInterval = idleEventInterval;
        }

        public Duration getIdlePartitionEventInterval() {
            return this.idlePartitionEventInterval;
        }

        public void setIdlePartitionEventInterval(Duration idlePartitionEventInterval) {
            this.idlePartitionEventInterval = idlePartitionEventInterval;
        }

        public Duration getMonitorInterval() {
            return this.monitorInterval;
        }

        public void setMonitorInterval(Duration monitorInterval) {
            this.monitorInterval = monitorInterval;
        }

        public Boolean getLogContainerConfig() {
            return this.logContainerConfig;
        }

        public void setLogContainerConfig(Boolean logContainerConfig) {
            this.logContainerConfig = logContainerConfig;
        }

        public boolean isMissingTopicsFatal() {
            return this.missingTopicsFatal;
        }

        public void setMissingTopicsFatal(boolean missingTopicsFatal) {
            this.missingTopicsFatal = missingTopicsFatal;
        }

        public boolean isImmediateStop() {
            return this.immediateStop;
        }

        public void setImmediateStop(boolean immediateStop) {
            this.immediateStop = immediateStop;
        }

        public boolean isAutoStartup() {
            return this.autoStartup;
        }

        public void setAutoStartup(boolean autoStartup) {
            this.autoStartup = autoStartup;
        }

        public Boolean getChangeConsumerThreadName() {
            return this.changeConsumerThreadName;
        }

        public void setChangeConsumerThreadName(Boolean changeConsumerThreadName) {
            this.changeConsumerThreadName = changeConsumerThreadName;
        }

        public boolean isObservationEnabled() {
            return this.observationEnabled;
        }

        public void setObservationEnabled(boolean observationEnabled) {
            this.observationEnabled = observationEnabled;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Ssl.class */
    public static class Ssl {
        private String bundle;
        private String keyPassword;
        private String keyStoreCertificateChain;
        private String keyStoreKey;
        private Resource keyStoreLocation;
        private String keyStorePassword;
        private String keyStoreType;
        private String trustStoreCertificates;
        private Resource trustStoreLocation;
        private String trustStorePassword;
        private String trustStoreType;
        private String protocol;

        public String getBundle() {
            return this.bundle;
        }

        public void setBundle(String bundle) {
            this.bundle = bundle;
        }

        public String getKeyPassword() {
            return this.keyPassword;
        }

        public void setKeyPassword(String keyPassword) {
            this.keyPassword = keyPassword;
        }

        public String getKeyStoreCertificateChain() {
            return this.keyStoreCertificateChain;
        }

        public void setKeyStoreCertificateChain(String keyStoreCertificateChain) {
            this.keyStoreCertificateChain = keyStoreCertificateChain;
        }

        public String getKeyStoreKey() {
            return this.keyStoreKey;
        }

        public void setKeyStoreKey(String keyStoreKey) {
            this.keyStoreKey = keyStoreKey;
        }

        public Resource getKeyStoreLocation() {
            return this.keyStoreLocation;
        }

        public void setKeyStoreLocation(Resource keyStoreLocation) {
            this.keyStoreLocation = keyStoreLocation;
        }

        public String getKeyStorePassword() {
            return this.keyStorePassword;
        }

        public void setKeyStorePassword(String keyStorePassword) {
            this.keyStorePassword = keyStorePassword;
        }

        public String getKeyStoreType() {
            return this.keyStoreType;
        }

        public void setKeyStoreType(String keyStoreType) {
            this.keyStoreType = keyStoreType;
        }

        public String getTrustStoreCertificates() {
            return this.trustStoreCertificates;
        }

        public void setTrustStoreCertificates(String trustStoreCertificates) {
            this.trustStoreCertificates = trustStoreCertificates;
        }

        public Resource getTrustStoreLocation() {
            return this.trustStoreLocation;
        }

        public void setTrustStoreLocation(Resource trustStoreLocation) {
            this.trustStoreLocation = trustStoreLocation;
        }

        public String getTrustStorePassword() {
            return this.trustStorePassword;
        }

        public void setTrustStorePassword(String trustStorePassword) {
            this.trustStorePassword = trustStorePassword;
        }

        public String getTrustStoreType() {
            return this.trustStoreType;
        }

        public void setTrustStoreType(String trustStoreType) {
            this.trustStoreType = trustStoreType;
        }

        public String getProtocol() {
            return this.protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        @Deprecated(since = "3.2.0", forRemoval = true)
        public Map<String, Object> buildProperties() {
            return buildProperties(null);
        }

        public Map<String, Object> buildProperties(SslBundles sslBundles) {
            validate();
            Properties properties = new Properties();
            if (getBundle() != null) {
                properties.in("ssl.engine.factory.class").accept(SslBundleSslEngineFactory.class.getName());
                properties.in(SslBundle.class.getName()).accept(sslBundles.getBundle(getBundle()));
            } else {
                PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
                map.from(this::getKeyPassword).to(properties.in("ssl.key.password"));
                map.from(this::getKeyStoreCertificateChain).to(properties.in("ssl.keystore.certificate.chain"));
                map.from(this::getKeyStoreKey).to(properties.in("ssl.keystore.key"));
                map.from(this::getKeyStoreLocation).as(this::resourceToPath).to(properties.in("ssl.keystore.location"));
                map.from(this::getKeyStorePassword).to(properties.in("ssl.keystore.password"));
                map.from(this::getKeyStoreType).to(properties.in("ssl.keystore.type"));
                map.from(this::getTrustStoreCertificates).to(properties.in("ssl.truststore.certificates"));
                map.from(this::getTrustStoreLocation).as(this::resourceToPath).to(properties.in("ssl.truststore.location"));
                map.from(this::getTrustStorePassword).to(properties.in("ssl.truststore.password"));
                map.from(this::getTrustStoreType).to(properties.in("ssl.truststore.type"));
                map.from(this::getProtocol).to(properties.in("ssl.protocol"));
            }
            return properties;
        }

        private void validate() {
            MutuallyExclusiveConfigurationPropertiesException.throwIfMultipleNonNullValuesIn(entries -> {
                entries.put("spring.kafka.ssl.key-store-key", getKeyStoreKey());
                entries.put("spring.kafka.ssl.key-store-location", getKeyStoreLocation());
            });
            MutuallyExclusiveConfigurationPropertiesException.throwIfMultipleNonNullValuesIn(entries2 -> {
                entries2.put("spring.kafka.ssl.trust-store-certificates", getTrustStoreCertificates());
                entries2.put("spring.kafka.ssl.trust-store-location", getTrustStoreLocation());
            });
            MutuallyExclusiveConfigurationPropertiesException.throwIfMultipleNonNullValuesIn(entries3 -> {
                entries3.put("spring.kafka.ssl.bundle", getBundle());
                entries3.put("spring.kafka.ssl.key-store-key", getKeyStoreKey());
            });
            MutuallyExclusiveConfigurationPropertiesException.throwIfMultipleNonNullValuesIn(entries4 -> {
                entries4.put("spring.kafka.ssl.bundle", getBundle());
                entries4.put("spring.kafka.ssl.key-store-location", getKeyStoreLocation());
            });
            MutuallyExclusiveConfigurationPropertiesException.throwIfMultipleNonNullValuesIn(entries5 -> {
                entries5.put("spring.kafka.ssl.bundle", getBundle());
                entries5.put("spring.kafka.ssl.trust-store-certificates", getTrustStoreCertificates());
            });
            MutuallyExclusiveConfigurationPropertiesException.throwIfMultipleNonNullValuesIn(entries6 -> {
                entries6.put("spring.kafka.ssl.bundle", getBundle());
                entries6.put("spring.kafka.ssl.trust-store-location", getTrustStoreLocation());
            });
        }

        private String resourceToPath(Resource resource) {
            try {
                return resource.getFile().getAbsolutePath();
            } catch (IOException ex) {
                throw new IllegalStateException("Resource '" + resource + "' must be on a file system", ex);
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Jaas.class */
    public static class Jaas {
        private boolean enabled;
        private String loginModule = "com.sun.security.auth.module.Krb5LoginModule";
        private KafkaJaasLoginModuleInitializer.ControlFlag controlFlag = KafkaJaasLoginModuleInitializer.ControlFlag.REQUIRED;
        private final Map<String, String> options = new HashMap();

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getLoginModule() {
            return this.loginModule;
        }

        public void setLoginModule(String loginModule) {
            this.loginModule = loginModule;
        }

        public KafkaJaasLoginModuleInitializer.ControlFlag getControlFlag() {
            return this.controlFlag;
        }

        public void setControlFlag(KafkaJaasLoginModuleInitializer.ControlFlag controlFlag) {
            this.controlFlag = controlFlag;
        }

        public Map<String, String> getOptions() {
            return this.options;
        }

        public void setOptions(Map<String, String> options) {
            if (options != null) {
                this.options.putAll(options);
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Security.class */
    public static class Security {
        private String protocol;

        public String getProtocol() {
            return this.protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public Map<String, Object> buildProperties() {
            Properties properties = new Properties();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            map.from(this::getProtocol).to(properties.in("security.protocol"));
            return properties;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Retry.class */
    public static class Retry {
        private final Topic topic = new Topic();

        public Topic getTopic() {
            return this.topic;
        }

        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Retry$Topic.class */
        public static class Topic {
            private boolean enabled;
            private int attempts = 3;
            private Duration delay = Duration.ofSeconds(1);
            private double multiplier = 0.0d;
            private Duration maxDelay = Duration.ZERO;
            private boolean randomBackOff = false;

            public boolean isEnabled() {
                return this.enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public int getAttempts() {
                return this.attempts;
            }

            public void setAttempts(int attempts) {
                this.attempts = attempts;
            }

            public Duration getDelay() {
                return this.delay;
            }

            public void setDelay(Duration delay) {
                this.delay = delay;
            }

            public double getMultiplier() {
                return this.multiplier;
            }

            public void setMultiplier(double multiplier) {
                this.multiplier = multiplier;
            }

            public Duration getMaxDelay() {
                return this.maxDelay;
            }

            public void setMaxDelay(Duration maxDelay) {
                this.maxDelay = maxDelay;
            }

            public boolean isRandomBackOff() {
                return this.randomBackOff;
            }

            public void setRandomBackOff(boolean randomBackOff) {
                this.randomBackOff = randomBackOff;
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Cleanup.class */
    public static class Cleanup {
        private boolean onStartup = false;
        private boolean onShutdown = false;

        public boolean isOnStartup() {
            return this.onStartup;
        }

        public void setOnStartup(boolean onStartup) {
            this.onStartup = onStartup;
        }

        public boolean isOnShutdown() {
            return this.onShutdown;
        }

        public void setOnShutdown(boolean onShutdown) {
            this.onShutdown = onShutdown;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$IsolationLevel.class */
    public enum IsolationLevel {
        READ_UNCOMMITTED((byte) 0),
        READ_COMMITTED((byte) 1);

        private final byte id;

        IsolationLevel(byte id) {
            this.id = id;
        }

        public byte id() {
            return this.id;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Properties.class */
    public static final class Properties extends HashMap<String, Object> {
        private Properties() {
        }

        <V> java.util.function.Consumer<V> in(String key) {
            return value -> {
                put(key, value);
            };
        }

        Properties with(Ssl ssl, Security security, Map<String, String> properties, SslBundles sslBundles) {
            putAll(ssl.buildProperties(sslBundles));
            putAll(security.buildProperties());
            putAll(properties);
            return this;
        }
    }
}
