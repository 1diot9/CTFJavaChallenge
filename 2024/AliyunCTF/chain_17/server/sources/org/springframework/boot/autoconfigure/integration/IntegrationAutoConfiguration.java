package org.springframework.boot.autoconfigure.integration;

import io.rsocket.RSocket;
import io.rsocket.transport.netty.server.TcpServerTransport;
import java.time.Duration;
import java.util.Objects;
import javax.management.MBeanServer;
import javax.sql.DataSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxProperties;
import org.springframework.boot.autoconfigure.rsocket.RSocketMessagingAutoConfiguration;
import org.springframework.boot.autoconfigure.sql.init.OnDatabaseInitializationCondition;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.context.properties.source.MutuallyExclusiveConfigurationPropertiesException;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.boot.task.ThreadPoolTaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.config.EnableIntegrationManagement;
import org.springframework.integration.config.IntegrationComponentScanRegistrar;
import org.springframework.integration.config.IntegrationManagementConfigurer;
import org.springframework.integration.jdbc.store.JdbcMessageStore;
import org.springframework.integration.jmx.config.EnableIntegrationMBeanExport;
import org.springframework.integration.monitor.IntegrationMBeanExporter;
import org.springframework.integration.rsocket.ClientRSocketConnector;
import org.springframework.integration.rsocket.IntegrationRSocketEndpoint;
import org.springframework.integration.rsocket.ServerRSocketConnector;
import org.springframework.integration.rsocket.ServerRSocketMessageHandler;
import org.springframework.integration.rsocket.outbound.RSocketOutboundGateway;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.util.StringUtils;

@EnableConfigurationProperties({IntegrationProperties.class, JmxProperties.class})
@AutoConfiguration(after = {DataSourceAutoConfiguration.class, JmxAutoConfiguration.class, TaskSchedulingAutoConfiguration.class})
@ConditionalOnClass({EnableIntegration.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration.class */
public class IntegrationAutoConfiguration {
    @ConditionalOnMissingBean(name = {"integrationGlobalProperties"})
    @Bean(name = {"integrationGlobalProperties"})
    public static org.springframework.integration.context.IntegrationProperties integrationGlobalProperties(IntegrationProperties properties) {
        org.springframework.integration.context.IntegrationProperties integrationProperties = new org.springframework.integration.context.IntegrationProperties();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        PropertyMapper.Source from = map.from((PropertyMapper) Boolean.valueOf(properties.getChannel().isAutoCreate()));
        Objects.requireNonNull(integrationProperties);
        from.to((v1) -> {
            r1.setChannelsAutoCreate(v1);
        });
        PropertyMapper.Source from2 = map.from((PropertyMapper) Integer.valueOf(properties.getChannel().getMaxUnicastSubscribers()));
        Objects.requireNonNull(integrationProperties);
        from2.to((v1) -> {
            r1.setChannelsMaxUnicastSubscribers(v1);
        });
        PropertyMapper.Source from3 = map.from((PropertyMapper) Integer.valueOf(properties.getChannel().getMaxBroadcastSubscribers()));
        Objects.requireNonNull(integrationProperties);
        from3.to((v1) -> {
            r1.setChannelsMaxBroadcastSubscribers(v1);
        });
        PropertyMapper.Source from4 = map.from((PropertyMapper) Boolean.valueOf(properties.getError().isRequireSubscribers()));
        Objects.requireNonNull(integrationProperties);
        from4.to((v1) -> {
            r1.setErrorChannelRequireSubscribers(v1);
        });
        PropertyMapper.Source from5 = map.from((PropertyMapper) Boolean.valueOf(properties.getError().isIgnoreFailures()));
        Objects.requireNonNull(integrationProperties);
        from5.to((v1) -> {
            r1.setErrorChannelIgnoreFailures(v1);
        });
        PropertyMapper.Source from6 = map.from((PropertyMapper) Boolean.valueOf(properties.getEndpoint().isThrowExceptionOnLateReply()));
        Objects.requireNonNull(integrationProperties);
        from6.to((v1) -> {
            r1.setMessagingTemplateThrowExceptionOnLateReply(v1);
        });
        PropertyMapper.Source as = map.from((PropertyMapper) properties.getEndpoint().getReadOnlyHeaders()).as((v0) -> {
            return StringUtils.toStringArray(v0);
        });
        Objects.requireNonNull(integrationProperties);
        as.to(integrationProperties::setReadOnlyHeaders);
        PropertyMapper.Source as2 = map.from((PropertyMapper) properties.getEndpoint().getNoAutoStartup()).as((v0) -> {
            return StringUtils.toStringArray(v0);
        });
        Objects.requireNonNull(integrationProperties);
        as2.to(integrationProperties::setNoAutoStartupEndpoints);
        return integrationProperties;
    }

    @Configuration(proxyBeanMethods = false)
    @EnableIntegration
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$IntegrationConfiguration.class */
    protected static class IntegrationConfiguration {
        protected IntegrationConfiguration() {
        }

        @ConditionalOnMissingBean(name = {"org.springframework.integration.context.defaultPollerMetadata"})
        @Bean({"org.springframework.integration.context.defaultPollerMetadata"})
        public PollerMetadata defaultPollerMetadata(IntegrationProperties integrationProperties) {
            IntegrationProperties.Poller poller = integrationProperties.getPoller();
            MutuallyExclusiveConfigurationPropertiesException.throwIfMultipleNonNullValuesIn(entries -> {
                entries.put("spring.integration.poller.cron", StringUtils.hasText(poller.getCron()) ? poller.getCron() : null);
                entries.put("spring.integration.poller.fixed-delay", poller.getFixedDelay());
                entries.put("spring.integration.poller.fixed-rate", poller.getFixedRate());
            });
            PollerMetadata pollerMetadata = new PollerMetadata();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            Objects.requireNonNull(poller);
            PropertyMapper.Source from = map.from(poller::getMaxMessagesPerPoll);
            Objects.requireNonNull(pollerMetadata);
            from.to((v1) -> {
                r1.setMaxMessagesPerPoll(v1);
            });
            Objects.requireNonNull(poller);
            PropertyMapper.Source as = map.from(poller::getReceiveTimeout).as((v0) -> {
                return v0.toMillis();
            });
            Objects.requireNonNull(pollerMetadata);
            as.to((v1) -> {
                r1.setReceiveTimeout(v1);
            });
            PropertyMapper.Source as2 = map.from((PropertyMapper) poller).as(this::asTrigger);
            Objects.requireNonNull(pollerMetadata);
            as2.to(pollerMetadata::setTrigger);
            return pollerMetadata;
        }

        private Trigger asTrigger(IntegrationProperties.Poller poller) {
            if (StringUtils.hasText(poller.getCron())) {
                return new CronTrigger(poller.getCron());
            }
            if (poller.getFixedDelay() != null) {
                return createPeriodicTrigger(poller.getFixedDelay(), poller.getInitialDelay(), false);
            }
            if (poller.getFixedRate() != null) {
                return createPeriodicTrigger(poller.getFixedRate(), poller.getInitialDelay(), true);
            }
            return null;
        }

        private Trigger createPeriodicTrigger(Duration period, Duration initialDelay, boolean fixedRate) {
            PeriodicTrigger trigger = new PeriodicTrigger(period);
            if (initialDelay != null) {
                trigger.setInitialDelay(initialDelay);
            }
            trigger.setFixedRate(fixedRate);
            return trigger;
        }
    }

    @ConditionalOnMissingBean(name = {"taskScheduler"})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnBean({TaskSchedulerBuilder.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$IntegrationTaskSchedulerConfiguration.class */
    protected static class IntegrationTaskSchedulerConfiguration {
        protected IntegrationTaskSchedulerConfiguration() {
        }

        @Bean(name = {"taskScheduler"})
        public ThreadPoolTaskScheduler taskScheduler(TaskSchedulerBuilder taskSchedulerBuilder, ObjectProvider<ThreadPoolTaskSchedulerBuilder> threadPoolTaskSchedulerBuilderProvider) {
            ThreadPoolTaskSchedulerBuilder threadPoolTaskSchedulerBuilder = threadPoolTaskSchedulerBuilderProvider.getIfUnique();
            if (threadPoolTaskSchedulerBuilder != null) {
                return threadPoolTaskSchedulerBuilder.build();
            }
            return taskSchedulerBuilder.build();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({EnableIntegrationMBeanExport.class})
    @ConditionalOnMissingBean(value = {IntegrationMBeanExporter.class}, search = SearchStrategy.CURRENT)
    @ConditionalOnBean({MBeanServer.class})
    @ConditionalOnProperty(prefix = "spring.jmx", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$IntegrationJmxConfiguration.class */
    protected static class IntegrationJmxConfiguration {
        protected IntegrationJmxConfiguration() {
        }

        @Bean
        public IntegrationMBeanExporter integrationMbeanExporter(BeanFactory beanFactory, JmxProperties properties) {
            IntegrationMBeanExporter exporter = new IntegrationMBeanExporter();
            String defaultDomain = properties.getDefaultDomain();
            if (StringUtils.hasLength(defaultDomain)) {
                exporter.setDefaultDomain(defaultDomain);
            }
            exporter.setServer((MBeanServer) beanFactory.getBean(properties.getServer(), MBeanServer.class));
            return exporter;
        }
    }

    @ConditionalOnMissingBean(value = {IntegrationManagementConfigurer.class}, name = {"integrationManagementConfigurer"}, search = SearchStrategy.CURRENT)
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({EnableIntegrationManagement.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$IntegrationManagementConfiguration.class */
    protected static class IntegrationManagementConfiguration {
        protected IntegrationManagementConfiguration() {
        }

        @EnableIntegrationManagement(defaultLoggingEnabled = "${spring.integration.management.default-logging-enabled:true}", observationPatterns = {"${spring.integration.management.observation-patterns:}"})
        @Configuration(proxyBeanMethods = false)
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$IntegrationManagementConfiguration$EnableIntegrationManagementConfiguration.class */
        protected static class EnableIntegrationManagementConfiguration {
            protected EnableIntegrationManagementConfiguration() {
            }
        }
    }

    @ConditionalOnMissingBean({IntegrationComponentScanRegistrar.class})
    @Configuration(proxyBeanMethods = false)
    @Import({IntegrationAutoConfigurationScanRegistrar.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$IntegrationComponentScanConfiguration.class */
    protected static class IntegrationComponentScanConfiguration {
        protected IntegrationComponentScanConfiguration() {
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({JdbcMessageStore.class})
    @ConditionalOnSingleCandidate(DataSource.class)
    @Conditional({OnIntegrationDatasourceInitializationCondition.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$IntegrationJdbcConfiguration.class */
    protected static class IntegrationJdbcConfiguration {
        protected IntegrationJdbcConfiguration() {
        }

        @ConditionalOnMissingBean({IntegrationDataSourceScriptDatabaseInitializer.class})
        @Bean
        public IntegrationDataSourceScriptDatabaseInitializer integrationDataSourceInitializer(DataSource dataSource, IntegrationProperties properties) {
            return new IntegrationDataSourceScriptDatabaseInitializer(dataSource, properties.getJdbc());
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({IntegrationRSocketEndpoint.class, RSocketRequester.class, RSocket.class})
    @Conditional({AnyRSocketChannelAdapterAvailable.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$IntegrationRSocketConfiguration.class */
    protected static class IntegrationRSocketConfiguration {
        protected IntegrationRSocketConfiguration() {
        }

        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$IntegrationRSocketConfiguration$AnyRSocketChannelAdapterAvailable.class */
        static class AnyRSocketChannelAdapterAvailable extends AnyNestedCondition {
            AnyRSocketChannelAdapterAvailable() {
                super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
            }

            @ConditionalOnBean({IntegrationRSocketEndpoint.class})
            /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$IntegrationRSocketConfiguration$AnyRSocketChannelAdapterAvailable$IntegrationRSocketEndpointAvailable.class */
            static class IntegrationRSocketEndpointAvailable {
                IntegrationRSocketEndpointAvailable() {
                }
            }

            @ConditionalOnBean({RSocketOutboundGateway.class})
            /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$IntegrationRSocketConfiguration$AnyRSocketChannelAdapterAvailable$RSocketOutboundGatewayAvailable.class */
            static class RSocketOutboundGatewayAvailable {
                RSocketOutboundGatewayAvailable() {
                }
            }
        }

        @AutoConfigureBefore({RSocketMessagingAutoConfiguration.class})
        @Configuration(proxyBeanMethods = false)
        @ConditionalOnClass({TcpServerTransport.class})
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$IntegrationRSocketConfiguration$IntegrationRSocketServerConfiguration.class */
        protected static class IntegrationRSocketServerConfiguration {
            protected IntegrationRSocketServerConfiguration() {
            }

            @ConditionalOnMissingBean({ServerRSocketMessageHandler.class})
            @Bean
            public RSocketMessageHandler serverRSocketMessageHandler(RSocketStrategies rSocketStrategies, IntegrationProperties integrationProperties) {
                ServerRSocketMessageHandler serverRSocketMessageHandler = new ServerRSocketMessageHandler(integrationProperties.getRsocket().getServer().isMessageMappingEnabled());
                serverRSocketMessageHandler.setRSocketStrategies(rSocketStrategies);
                return serverRSocketMessageHandler;
            }

            @ConditionalOnMissingBean
            @Bean
            public ServerRSocketConnector serverRSocketConnector(ServerRSocketMessageHandler messageHandler) {
                return new ServerRSocketConnector(messageHandler);
            }
        }

        @Configuration(proxyBeanMethods = false)
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$IntegrationRSocketConfiguration$IntegrationRSocketClientConfiguration.class */
        protected static class IntegrationRSocketClientConfiguration {
            protected IntegrationRSocketClientConfiguration() {
            }

            @ConditionalOnMissingBean
            @Conditional({RemoteRSocketServerAddressConfigured.class})
            @Bean
            public ClientRSocketConnector clientRSocketConnector(IntegrationProperties integrationProperties, RSocketStrategies rSocketStrategies) {
                ClientRSocketConnector clientRSocketConnector;
                IntegrationProperties.RSocket.Client client = integrationProperties.getRsocket().getClient();
                if (client.getUri() != null) {
                    clientRSocketConnector = new ClientRSocketConnector(client.getUri());
                } else {
                    clientRSocketConnector = new ClientRSocketConnector(client.getHost(), client.getPort().intValue());
                }
                ClientRSocketConnector clientRSocketConnector2 = clientRSocketConnector;
                clientRSocketConnector2.setRSocketStrategies(rSocketStrategies);
                return clientRSocketConnector2;
            }

            /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$IntegrationRSocketConfiguration$IntegrationRSocketClientConfiguration$RemoteRSocketServerAddressConfigured.class */
            static class RemoteRSocketServerAddressConfigured extends AnyNestedCondition {
                RemoteRSocketServerAddressConfigured() {
                    super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
                }

                @ConditionalOnProperty(prefix = "spring.integration.rsocket.client", name = {"uri"})
                /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$IntegrationRSocketConfiguration$IntegrationRSocketClientConfiguration$RemoteRSocketServerAddressConfigured$WebSocketAddressConfigured.class */
                static class WebSocketAddressConfigured {
                    WebSocketAddressConfigured() {
                    }
                }

                @ConditionalOnProperty(prefix = "spring.integration.rsocket.client", name = {"host", "port"})
                /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$IntegrationRSocketConfiguration$IntegrationRSocketClientConfiguration$RemoteRSocketServerAddressConfigured$TcpAddressConfigured.class */
                static class TcpAddressConfigured {
                    TcpAddressConfigured() {
                    }
                }
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/integration/IntegrationAutoConfiguration$OnIntegrationDatasourceInitializationCondition.class */
    static class OnIntegrationDatasourceInitializationCondition extends OnDatabaseInitializationCondition {
        OnIntegrationDatasourceInitializationCondition() {
            super("Integration", "spring.integration.jdbc.initialize-schema");
        }
    }
}
