package org.springframework.boot.autoconfigure.amqp;

import ch.qos.logback.core.net.ssl.SSL;
import ch.qos.logback.core.spi.AbstractComponentTracker;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.AbstractConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties(prefix = "spring.rabbitmq")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties.class */
public class RabbitProperties {
    private static final int DEFAULT_PORT = 5672;
    private static final int DEFAULT_PORT_SECURE = 5671;
    private static final int DEFAULT_STREAM_PORT = 5552;
    private Integer port;
    private String virtualHost;
    private String addresses;

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration requestedHeartbeat;
    private boolean publisherReturns;
    private CachingConnectionFactory.ConfirmType publisherConfirmType;
    private Duration connectionTimeout;
    private List<Address> parsedAddresses;
    private String host = "localhost";
    private String username = "guest";
    private String password = "guest";
    private final Ssl ssl = new Ssl();
    private AbstractConnectionFactory.AddressShuffleMode addressShuffleMode = AbstractConnectionFactory.AddressShuffleMode.NONE;
    private int requestedChannelMax = 2047;
    private Duration channelRpcTimeout = Duration.ofMinutes(10);
    private DataSize maxInboundMessageBodySize = DataSize.ofMegabytes(64);
    private final Cache cache = new Cache();
    private final Listener listener = new Listener();
    private final Template template = new Template();
    private final Stream stream = new Stream();

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$BaseContainer.class */
    public static abstract class BaseContainer {
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$ContainerType.class */
    public enum ContainerType {
        SIMPLE,
        DIRECT,
        STREAM
    }

    public String getHost() {
        return this.host;
    }

    public String determineHost() {
        if (CollectionUtils.isEmpty(this.parsedAddresses)) {
            return getHost();
        }
        return this.parsedAddresses.get(0).host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return this.port;
    }

    public int determinePort() {
        if (CollectionUtils.isEmpty(this.parsedAddresses)) {
            Integer port = getPort();
            if (port != null) {
                return port.intValue();
            }
            return ((Boolean) Optional.ofNullable(getSsl().getEnabled()).orElse(false)).booleanValue() ? DEFAULT_PORT_SECURE : DEFAULT_PORT;
        }
        return this.parsedAddresses.get(0).port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getAddresses() {
        return this.addresses;
    }

    public String determineAddresses() {
        if (CollectionUtils.isEmpty(this.parsedAddresses)) {
            if (this.host.contains(",")) {
                throw new InvalidConfigurationPropertyValueException("spring.rabbitmq.host", this.host, "Invalid character ','. Value must be a single host. For multiple hosts, use property 'spring.rabbitmq.addresses' instead.");
            }
            return this.host + ":" + determinePort();
        }
        List<String> addressStrings = new ArrayList<>();
        for (Address parsedAddress : this.parsedAddresses) {
            addressStrings.add(parsedAddress.host + ":" + parsedAddress.port);
        }
        return StringUtils.collectionToCommaDelimitedString(addressStrings);
    }

    public void setAddresses(String addresses) {
        this.addresses = addresses;
        this.parsedAddresses = parseAddresses(addresses);
    }

    private List<Address> parseAddresses(String addresses) {
        List<Address> parsedAddresses = new ArrayList<>();
        for (String address : StringUtils.commaDelimitedListToStringArray(addresses)) {
            parsedAddresses.add(new Address(address, ((Boolean) Optional.ofNullable(getSsl().getEnabled()).orElse(false)).booleanValue()));
        }
        return parsedAddresses;
    }

    public String getUsername() {
        return this.username;
    }

    public String determineUsername() {
        if (CollectionUtils.isEmpty(this.parsedAddresses)) {
            return this.username;
        }
        Address address = this.parsedAddresses.get(0);
        return address.username != null ? address.username : this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public String determinePassword() {
        if (CollectionUtils.isEmpty(this.parsedAddresses)) {
            return getPassword();
        }
        Address address = this.parsedAddresses.get(0);
        return address.password != null ? address.password : getPassword();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Ssl getSsl() {
        return this.ssl;
    }

    public String getVirtualHost() {
        return this.virtualHost;
    }

    public String determineVirtualHost() {
        if (CollectionUtils.isEmpty(this.parsedAddresses)) {
            return getVirtualHost();
        }
        Address address = this.parsedAddresses.get(0);
        return address.virtualHost != null ? address.virtualHost : getVirtualHost();
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = StringUtils.hasText(virtualHost) ? virtualHost : "/";
    }

    public AbstractConnectionFactory.AddressShuffleMode getAddressShuffleMode() {
        return this.addressShuffleMode;
    }

    public void setAddressShuffleMode(AbstractConnectionFactory.AddressShuffleMode addressShuffleMode) {
        this.addressShuffleMode = addressShuffleMode;
    }

    public Duration getRequestedHeartbeat() {
        return this.requestedHeartbeat;
    }

    public void setRequestedHeartbeat(Duration requestedHeartbeat) {
        this.requestedHeartbeat = requestedHeartbeat;
    }

    public int getRequestedChannelMax() {
        return this.requestedChannelMax;
    }

    public void setRequestedChannelMax(int requestedChannelMax) {
        this.requestedChannelMax = requestedChannelMax;
    }

    public boolean isPublisherReturns() {
        return this.publisherReturns;
    }

    public void setPublisherReturns(boolean publisherReturns) {
        this.publisherReturns = publisherReturns;
    }

    public Duration getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setPublisherConfirmType(CachingConnectionFactory.ConfirmType publisherConfirmType) {
        this.publisherConfirmType = publisherConfirmType;
    }

    public CachingConnectionFactory.ConfirmType getPublisherConfirmType() {
        return this.publisherConfirmType;
    }

    public void setConnectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Duration getChannelRpcTimeout() {
        return this.channelRpcTimeout;
    }

    public void setChannelRpcTimeout(Duration channelRpcTimeout) {
        this.channelRpcTimeout = channelRpcTimeout;
    }

    public DataSize getMaxInboundMessageBodySize() {
        return this.maxInboundMessageBodySize;
    }

    public void setMaxInboundMessageBodySize(DataSize maxInboundMessageBodySize) {
        this.maxInboundMessageBodySize = maxInboundMessageBodySize;
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

    public Stream getStream() {
        return this.stream;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$Ssl.class */
    public class Ssl {
        private static final String SUN_X509 = "SunX509";
        private Boolean enabled;
        private String bundle;
        private String keyStore;
        private String keyStorePassword;
        private String trustStore;
        private String trustStorePassword;
        private String algorithm;
        private String keyStoreType = "PKCS12";
        private String keyStoreAlgorithm = SUN_X509;
        private String trustStoreType = SSL.DEFAULT_KEYSTORE_TYPE;
        private String trustStoreAlgorithm = SUN_X509;
        private boolean validateServerCertificate = true;
        private boolean verifyHostname = true;

        public Ssl() {
        }

        public Boolean getEnabled() {
            return this.enabled;
        }

        public boolean determineEnabled() {
            boolean defaultEnabled = ((Boolean) Optional.ofNullable(getEnabled()).orElse(false)).booleanValue() || this.bundle != null;
            if (CollectionUtils.isEmpty(RabbitProperties.this.parsedAddresses)) {
                return defaultEnabled;
            }
            Address address = RabbitProperties.this.parsedAddresses.get(0);
            return address.determineSslEnabled(defaultEnabled);
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public String getBundle() {
            return this.bundle;
        }

        public void setBundle(String bundle) {
            this.bundle = bundle;
        }

        public String getKeyStore() {
            return this.keyStore;
        }

        public void setKeyStore(String keyStore) {
            this.keyStore = keyStore;
        }

        public String getKeyStoreType() {
            return this.keyStoreType;
        }

        public void setKeyStoreType(String keyStoreType) {
            this.keyStoreType = keyStoreType;
        }

        public String getKeyStorePassword() {
            return this.keyStorePassword;
        }

        public void setKeyStorePassword(String keyStorePassword) {
            this.keyStorePassword = keyStorePassword;
        }

        public String getKeyStoreAlgorithm() {
            return this.keyStoreAlgorithm;
        }

        public void setKeyStoreAlgorithm(String keyStoreAlgorithm) {
            this.keyStoreAlgorithm = keyStoreAlgorithm;
        }

        public String getTrustStore() {
            return this.trustStore;
        }

        public void setTrustStore(String trustStore) {
            this.trustStore = trustStore;
        }

        public String getTrustStoreType() {
            return this.trustStoreType;
        }

        public void setTrustStoreType(String trustStoreType) {
            this.trustStoreType = trustStoreType;
        }

        public String getTrustStorePassword() {
            return this.trustStorePassword;
        }

        public void setTrustStorePassword(String trustStorePassword) {
            this.trustStorePassword = trustStorePassword;
        }

        public String getTrustStoreAlgorithm() {
            return this.trustStoreAlgorithm;
        }

        public void setTrustStoreAlgorithm(String trustStoreAlgorithm) {
            this.trustStoreAlgorithm = trustStoreAlgorithm;
        }

        public String getAlgorithm() {
            return this.algorithm;
        }

        public void setAlgorithm(String sslAlgorithm) {
            this.algorithm = sslAlgorithm;
        }

        public boolean isValidateServerCertificate() {
            return this.validateServerCertificate;
        }

        public void setValidateServerCertificate(boolean validateServerCertificate) {
            this.validateServerCertificate = validateServerCertificate;
        }

        public boolean getVerifyHostname() {
            return this.verifyHostname;
        }

        public void setVerifyHostname(boolean verifyHostname) {
            this.verifyHostname = verifyHostname;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$Cache.class */
    public static class Cache {
        private final Channel channel = new Channel();
        private final Connection connection = new Connection();

        public Channel getChannel() {
            return this.channel;
        }

        public Connection getConnection() {
            return this.connection;
        }

        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$Cache$Channel.class */
        public static class Channel {
            private Integer size;
            private Duration checkoutTimeout;

            public Integer getSize() {
                return this.size;
            }

            public void setSize(Integer size) {
                this.size = size;
            }

            public Duration getCheckoutTimeout() {
                return this.checkoutTimeout;
            }

            public void setCheckoutTimeout(Duration checkoutTimeout) {
                this.checkoutTimeout = checkoutTimeout;
            }
        }

        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$Cache$Connection.class */
        public static class Connection {
            private CachingConnectionFactory.CacheMode mode = CachingConnectionFactory.CacheMode.CHANNEL;
            private Integer size;

            public CachingConnectionFactory.CacheMode getMode() {
                return this.mode;
            }

            public void setMode(CachingConnectionFactory.CacheMode mode) {
                this.mode = mode;
            }

            public Integer getSize() {
                return this.size;
            }

            public void setSize(Integer size) {
                this.size = size;
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$Listener.class */
    public static class Listener {
        private ContainerType type = ContainerType.SIMPLE;
        private final SimpleContainer simple = new SimpleContainer();
        private final DirectContainer direct = new DirectContainer();
        private final StreamContainer stream = new StreamContainer();

        public ContainerType getType() {
            return this.type;
        }

        public void setType(ContainerType containerType) {
            this.type = containerType;
        }

        public SimpleContainer getSimple() {
            return this.simple;
        }

        public DirectContainer getDirect() {
            return this.direct;
        }

        public StreamContainer getStream() {
            return this.stream;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$AmqpContainer.class */
    public static abstract class AmqpContainer extends BaseContainer {
        private AcknowledgeMode acknowledgeMode;
        private Integer prefetch;
        private Boolean defaultRequeueRejected;
        private Duration idleEventInterval;
        private boolean forceStop;
        private boolean autoStartup = true;
        private boolean deBatchingEnabled = true;
        private final ListenerRetry retry = new ListenerRetry();

        public abstract boolean isMissingQueuesFatal();

        public boolean isAutoStartup() {
            return this.autoStartup;
        }

        public void setAutoStartup(boolean autoStartup) {
            this.autoStartup = autoStartup;
        }

        public AcknowledgeMode getAcknowledgeMode() {
            return this.acknowledgeMode;
        }

        public void setAcknowledgeMode(AcknowledgeMode acknowledgeMode) {
            this.acknowledgeMode = acknowledgeMode;
        }

        public Integer getPrefetch() {
            return this.prefetch;
        }

        public void setPrefetch(Integer prefetch) {
            this.prefetch = prefetch;
        }

        public Boolean getDefaultRequeueRejected() {
            return this.defaultRequeueRejected;
        }

        public void setDefaultRequeueRejected(Boolean defaultRequeueRejected) {
            this.defaultRequeueRejected = defaultRequeueRejected;
        }

        public Duration getIdleEventInterval() {
            return this.idleEventInterval;
        }

        public void setIdleEventInterval(Duration idleEventInterval) {
            this.idleEventInterval = idleEventInterval;
        }

        public boolean isDeBatchingEnabled() {
            return this.deBatchingEnabled;
        }

        public void setDeBatchingEnabled(boolean deBatchingEnabled) {
            this.deBatchingEnabled = deBatchingEnabled;
        }

        public boolean isForceStop() {
            return this.forceStop;
        }

        public void setForceStop(boolean forceStop) {
            this.forceStop = forceStop;
        }

        public ListenerRetry getRetry() {
            return this.retry;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$SimpleContainer.class */
    public static class SimpleContainer extends AmqpContainer {
        private Integer concurrency;
        private Integer maxConcurrency;
        private Integer batchSize;
        private boolean missingQueuesFatal = true;
        private boolean consumerBatchEnabled;

        public Integer getConcurrency() {
            return this.concurrency;
        }

        public void setConcurrency(Integer concurrency) {
            this.concurrency = concurrency;
        }

        public Integer getMaxConcurrency() {
            return this.maxConcurrency;
        }

        public void setMaxConcurrency(Integer maxConcurrency) {
            this.maxConcurrency = maxConcurrency;
        }

        public Integer getBatchSize() {
            return this.batchSize;
        }

        public void setBatchSize(Integer batchSize) {
            this.batchSize = batchSize;
        }

        @Override // org.springframework.boot.autoconfigure.amqp.RabbitProperties.AmqpContainer
        public boolean isMissingQueuesFatal() {
            return this.missingQueuesFatal;
        }

        public void setMissingQueuesFatal(boolean missingQueuesFatal) {
            this.missingQueuesFatal = missingQueuesFatal;
        }

        public boolean isConsumerBatchEnabled() {
            return this.consumerBatchEnabled;
        }

        public void setConsumerBatchEnabled(boolean consumerBatchEnabled) {
            this.consumerBatchEnabled = consumerBatchEnabled;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$DirectContainer.class */
    public static class DirectContainer extends AmqpContainer {
        private Integer consumersPerQueue;
        private boolean missingQueuesFatal = false;

        public Integer getConsumersPerQueue() {
            return this.consumersPerQueue;
        }

        public void setConsumersPerQueue(Integer consumersPerQueue) {
            this.consumersPerQueue = consumersPerQueue;
        }

        @Override // org.springframework.boot.autoconfigure.amqp.RabbitProperties.AmqpContainer
        public boolean isMissingQueuesFatal() {
            return this.missingQueuesFatal;
        }

        public void setMissingQueuesFatal(boolean missingQueuesFatal) {
            this.missingQueuesFatal = missingQueuesFatal;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$StreamContainer.class */
    public static class StreamContainer extends BaseContainer {
        private boolean nativeListener;

        public boolean isNativeListener() {
            return this.nativeListener;
        }

        public void setNativeListener(boolean nativeListener) {
            this.nativeListener = nativeListener;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$Template.class */
    public static class Template {
        private Boolean mandatory;
        private Duration receiveTimeout;
        private Duration replyTimeout;
        private String defaultReceiveQueue;
        private final Retry retry = new Retry();
        private String exchange = "";
        private String routingKey = "";

        public Retry getRetry() {
            return this.retry;
        }

        public Boolean getMandatory() {
            return this.mandatory;
        }

        public void setMandatory(Boolean mandatory) {
            this.mandatory = mandatory;
        }

        public Duration getReceiveTimeout() {
            return this.receiveTimeout;
        }

        public void setReceiveTimeout(Duration receiveTimeout) {
            this.receiveTimeout = receiveTimeout;
        }

        public Duration getReplyTimeout() {
            return this.replyTimeout;
        }

        public void setReplyTimeout(Duration replyTimeout) {
            this.replyTimeout = replyTimeout;
        }

        public String getExchange() {
            return this.exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getRoutingKey() {
            return this.routingKey;
        }

        public void setRoutingKey(String routingKey) {
            this.routingKey = routingKey;
        }

        public String getDefaultReceiveQueue() {
            return this.defaultReceiveQueue;
        }

        public void setDefaultReceiveQueue(String defaultReceiveQueue) {
            this.defaultReceiveQueue = defaultReceiveQueue;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$Retry.class */
    public static class Retry {
        private boolean enabled;
        private int maxAttempts = 3;
        private Duration initialInterval = Duration.ofMillis(1000);
        private double multiplier = 1.0d;
        private Duration maxInterval = Duration.ofMillis(AbstractComponentTracker.LINGERING_TIMEOUT);

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getMaxAttempts() {
            return this.maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public Duration getInitialInterval() {
            return this.initialInterval;
        }

        public void setInitialInterval(Duration initialInterval) {
            this.initialInterval = initialInterval;
        }

        public double getMultiplier() {
            return this.multiplier;
        }

        public void setMultiplier(double multiplier) {
            this.multiplier = multiplier;
        }

        public Duration getMaxInterval() {
            return this.maxInterval;
        }

        public void setMaxInterval(Duration maxInterval) {
            this.maxInterval = maxInterval;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$ListenerRetry.class */
    public static class ListenerRetry extends Retry {
        private boolean stateless = true;

        public boolean isStateless() {
            return this.stateless;
        }

        public void setStateless(boolean stateless) {
            this.stateless = stateless;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$Address.class */
    public static final class Address {
        private static final String PREFIX_AMQP = "amqp://";
        private static final String PREFIX_AMQP_SECURE = "amqps://";
        private String host;
        private int port;
        private String username;
        private String password;
        private String virtualHost;
        private Boolean secureConnection;

        private Address(String input, boolean sslEnabled) {
            parseHostAndPort(parseVirtualHost(parseUsernameAndPassword(trimPrefix(input.trim()))), sslEnabled);
        }

        private String trimPrefix(String input) {
            if (input.startsWith(PREFIX_AMQP_SECURE)) {
                this.secureConnection = true;
                return input.substring(PREFIX_AMQP_SECURE.length());
            }
            if (input.startsWith(PREFIX_AMQP)) {
                this.secureConnection = false;
                return input.substring(PREFIX_AMQP.length());
            }
            return input;
        }

        private String parseUsernameAndPassword(String input) {
            String[] splitInput = StringUtils.split(input, "@");
            if (splitInput == null) {
                return input;
            }
            String credentials = splitInput[0];
            String[] splitCredentials = StringUtils.split(credentials, ":");
            if (splitCredentials == null) {
                this.username = credentials;
            } else {
                this.username = splitCredentials[0];
                this.password = splitCredentials[1];
            }
            return splitInput[1];
        }

        private String parseVirtualHost(String input) {
            int hostIndex = input.indexOf(47);
            if (hostIndex >= 0) {
                this.virtualHost = input.substring(hostIndex + 1);
                if (this.virtualHost.isEmpty()) {
                    this.virtualHost = "/";
                }
                input = input.substring(0, hostIndex);
            }
            return input;
        }

        private void parseHostAndPort(String input, boolean sslEnabled) {
            int bracketIndex = input.lastIndexOf(93);
            int colonIndex = input.lastIndexOf(58);
            if (colonIndex == -1 || colonIndex < bracketIndex) {
                this.host = input;
                this.port = determineSslEnabled(sslEnabled) ? RabbitProperties.DEFAULT_PORT_SECURE : RabbitProperties.DEFAULT_PORT;
            } else {
                this.host = input.substring(0, colonIndex);
                this.port = Integer.parseInt(input.substring(colonIndex + 1));
            }
        }

        private boolean determineSslEnabled(boolean sslEnabled) {
            return this.secureConnection != null ? this.secureConnection.booleanValue() : sslEnabled;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$Stream.class */
    public static final class Stream {
        private String host = "localhost";
        private int port = RabbitProperties.DEFAULT_STREAM_PORT;
        private String virtualHost;
        private String username;
        private String password;
        private String name;

        public String getHost() {
            return this.host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return this.port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getVirtualHost() {
            return this.virtualHost;
        }

        public void setVirtualHost(String virtualHost) {
            this.virtualHost = virtualHost;
        }

        public String getUsername() {
            return this.username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return this.password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
