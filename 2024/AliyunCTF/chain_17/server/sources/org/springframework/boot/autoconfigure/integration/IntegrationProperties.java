package org.springframework.boot.autoconfigure.integration;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.sql.init.DatabaseInitializationMode;

@ConfigurationProperties(prefix = "spring.integration")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationProperties.class */
public class IntegrationProperties {
    private final Channel channel = new Channel();
    private final Endpoint endpoint = new Endpoint();
    private final Error error = new Error();
    private final Jdbc jdbc = new Jdbc();
    private final RSocket rsocket = new RSocket();
    private final Poller poller = new Poller();
    private final Management management = new Management();

    public Channel getChannel() {
        return this.channel;
    }

    public Endpoint getEndpoint() {
        return this.endpoint;
    }

    public Error getError() {
        return this.error;
    }

    public Jdbc getJdbc() {
        return this.jdbc;
    }

    public RSocket getRsocket() {
        return this.rsocket;
    }

    public Poller getPoller() {
        return this.poller;
    }

    public Management getManagement() {
        return this.management;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationProperties$Channel.class */
    public static class Channel {
        private boolean autoCreate = true;
        private int maxUnicastSubscribers = Integer.MAX_VALUE;
        private int maxBroadcastSubscribers = Integer.MAX_VALUE;

        public void setAutoCreate(boolean autoCreate) {
            this.autoCreate = autoCreate;
        }

        public boolean isAutoCreate() {
            return this.autoCreate;
        }

        public void setMaxUnicastSubscribers(int maxUnicastSubscribers) {
            this.maxUnicastSubscribers = maxUnicastSubscribers;
        }

        public int getMaxUnicastSubscribers() {
            return this.maxUnicastSubscribers;
        }

        public void setMaxBroadcastSubscribers(int maxBroadcastSubscribers) {
            this.maxBroadcastSubscribers = maxBroadcastSubscribers;
        }

        public int getMaxBroadcastSubscribers() {
            return this.maxBroadcastSubscribers;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationProperties$Endpoint.class */
    public static class Endpoint {
        private boolean throwExceptionOnLateReply = false;
        private List<String> readOnlyHeaders = new ArrayList();
        private List<String> noAutoStartup = new ArrayList();

        public void setThrowExceptionOnLateReply(boolean throwExceptionOnLateReply) {
            this.throwExceptionOnLateReply = throwExceptionOnLateReply;
        }

        public boolean isThrowExceptionOnLateReply() {
            return this.throwExceptionOnLateReply;
        }

        public List<String> getReadOnlyHeaders() {
            return this.readOnlyHeaders;
        }

        public void setReadOnlyHeaders(List<String> readOnlyHeaders) {
            this.readOnlyHeaders = readOnlyHeaders;
        }

        public List<String> getNoAutoStartup() {
            return this.noAutoStartup;
        }

        public void setNoAutoStartup(List<String> noAutoStartup) {
            this.noAutoStartup = noAutoStartup;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationProperties$Error.class */
    public static class Error {
        private boolean requireSubscribers = true;
        private boolean ignoreFailures = true;

        public boolean isRequireSubscribers() {
            return this.requireSubscribers;
        }

        public void setRequireSubscribers(boolean requireSubscribers) {
            this.requireSubscribers = requireSubscribers;
        }

        public boolean isIgnoreFailures() {
            return this.ignoreFailures;
        }

        public void setIgnoreFailures(boolean ignoreFailures) {
            this.ignoreFailures = ignoreFailures;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationProperties$Jdbc.class */
    public static class Jdbc {
        private static final String DEFAULT_SCHEMA_LOCATION = "classpath:org/springframework/integration/jdbc/schema-@@platform@@.sql";
        private String platform;
        private String schema = DEFAULT_SCHEMA_LOCATION;
        private DatabaseInitializationMode initializeSchema = DatabaseInitializationMode.EMBEDDED;

        public String getSchema() {
            return this.schema;
        }

        public void setSchema(String schema) {
            this.schema = schema;
        }

        public String getPlatform() {
            return this.platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public DatabaseInitializationMode getInitializeSchema() {
            return this.initializeSchema;
        }

        public void setInitializeSchema(DatabaseInitializationMode initializeSchema) {
            this.initializeSchema = initializeSchema;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationProperties$RSocket.class */
    public static class RSocket {
        private final Client client = new Client();
        private final Server server = new Server();

        public Client getClient() {
            return this.client;
        }

        public Server getServer() {
            return this.server;
        }

        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationProperties$RSocket$Client.class */
        public static class Client {
            private String host;
            private Integer port;
            private URI uri;

            public void setHost(String host) {
                this.host = host;
            }

            public String getHost() {
                return this.host;
            }

            public void setPort(Integer port) {
                this.port = port;
            }

            public Integer getPort() {
                return this.port;
            }

            public void setUri(URI uri) {
                this.uri = uri;
            }

            public URI getUri() {
                return this.uri;
            }
        }

        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationProperties$RSocket$Server.class */
        public static class Server {
            private boolean messageMappingEnabled;

            public boolean isMessageMappingEnabled() {
                return this.messageMappingEnabled;
            }

            public void setMessageMappingEnabled(boolean messageMappingEnabled) {
                this.messageMappingEnabled = messageMappingEnabled;
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationProperties$Poller.class */
    public static class Poller {
        private int maxMessagesPerPoll = Integer.MIN_VALUE;
        private Duration receiveTimeout = Duration.ofSeconds(1);
        private Duration fixedDelay;
        private Duration fixedRate;
        private Duration initialDelay;
        private String cron;

        public int getMaxMessagesPerPoll() {
            return this.maxMessagesPerPoll;
        }

        public void setMaxMessagesPerPoll(int maxMessagesPerPoll) {
            this.maxMessagesPerPoll = maxMessagesPerPoll;
        }

        public Duration getReceiveTimeout() {
            return this.receiveTimeout;
        }

        public void setReceiveTimeout(Duration receiveTimeout) {
            this.receiveTimeout = receiveTimeout;
        }

        public Duration getFixedDelay() {
            return this.fixedDelay;
        }

        public void setFixedDelay(Duration fixedDelay) {
            this.fixedDelay = fixedDelay;
        }

        public Duration getFixedRate() {
            return this.fixedRate;
        }

        public void setFixedRate(Duration fixedRate) {
            this.fixedRate = fixedRate;
        }

        public Duration getInitialDelay() {
            return this.initialDelay;
        }

        public void setInitialDelay(Duration initialDelay) {
            this.initialDelay = initialDelay;
        }

        public String getCron() {
            return this.cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationProperties$Management.class */
    public static class Management {
        private boolean defaultLoggingEnabled = true;
        private List<String> observationPatterns = new ArrayList();

        public boolean isDefaultLoggingEnabled() {
            return this.defaultLoggingEnabled;
        }

        public void setDefaultLoggingEnabled(boolean defaultLoggingEnabled) {
            this.defaultLoggingEnabled = defaultLoggingEnabled;
        }

        public List<String> getObservationPatterns() {
            return this.observationPatterns;
        }

        public void setObservationPatterns(List<String> observationPatterns) {
            this.observationPatterns = observationPatterns;
        }
    }
}
