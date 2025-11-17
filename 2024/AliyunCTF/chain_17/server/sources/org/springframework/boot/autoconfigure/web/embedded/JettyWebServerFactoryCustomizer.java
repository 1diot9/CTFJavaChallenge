package org.springframework.boot.autoconfigure.web.embedded;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.server.AbstractConnector;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.RequestLogWriter;
import org.eclipse.jetty.server.Server;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/embedded/JettyWebServerFactoryCustomizer.class */
public class JettyWebServerFactoryCustomizer implements WebServerFactoryCustomizer<ConfigurableJettyWebServerFactory>, Ordered {
    static final int ORDER = 0;
    private final Environment environment;
    private final ServerProperties serverProperties;

    public JettyWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties) {
        this.environment = environment;
        this.serverProperties = serverProperties;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(ConfigurableJettyWebServerFactory factory) {
        ServerProperties.Jetty properties = this.serverProperties.getJetty();
        factory.setUseForwardHeaders(getOrDeduceUseForwardHeaders());
        ServerProperties.Jetty.Threads threadProperties = properties.getThreads();
        factory.setThreadPool(JettyThreadPool.create(properties.getThreads()));
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(properties);
        PropertyMapper.Source from = map.from(properties::getMaxConnections);
        Objects.requireNonNull(factory);
        from.to((v1) -> {
            r1.setMaxConnections(v1);
        });
        Objects.requireNonNull(threadProperties);
        PropertyMapper.Source from2 = map.from(threadProperties::getAcceptors);
        Objects.requireNonNull(factory);
        from2.to((v1) -> {
            r1.setAcceptors(v1);
        });
        Objects.requireNonNull(threadProperties);
        PropertyMapper.Source from3 = map.from(threadProperties::getSelectors);
        Objects.requireNonNull(factory);
        from3.to((v1) -> {
            r1.setSelectors(v1);
        });
        ServerProperties serverProperties = this.serverProperties;
        Objects.requireNonNull(serverProperties);
        map.from(serverProperties::getMaxHttpRequestHeaderSize).asInt((v0) -> {
            return v0.toBytes();
        }).when(this::isPositive).to(maxHttpRequestHeaderSize -> {
            factory.addServerCustomizers(new MaxHttpRequestHeaderSizeCustomizer(maxHttpRequestHeaderSize.intValue()));
        });
        Objects.requireNonNull(properties);
        map.from(properties::getMaxHttpResponseHeaderSize).asInt((v0) -> {
            return v0.toBytes();
        }).when(this::isPositive).to(maxHttpResponseHeaderSize -> {
            factory.addServerCustomizers(new MaxHttpResponseHeaderSizeCustomizer(maxHttpResponseHeaderSize.intValue()));
        });
        Objects.requireNonNull(properties);
        map.from(properties::getMaxHttpFormPostSize).asInt((v0) -> {
            return v0.toBytes();
        }).when(this::isPositive).to(maxHttpFormPostSize -> {
            customizeMaxHttpFormPostSize(factory, maxHttpFormPostSize.intValue());
        });
        Objects.requireNonNull(properties);
        map.from(properties::getConnectionIdleTimeout).to(idleTimeout -> {
            customizeIdleTimeout(factory, idleTimeout);
        });
        Objects.requireNonNull(properties);
        map.from(properties::getAccesslog).when((v0) -> {
            return v0.isEnabled();
        }).to(accesslog -> {
            customizeAccessLog(factory, accesslog);
        });
    }

    private boolean isPositive(Integer value) {
        return value.intValue() > 0;
    }

    private boolean getOrDeduceUseForwardHeaders() {
        if (this.serverProperties.getForwardHeadersStrategy() == null) {
            CloudPlatform platform = CloudPlatform.getActive(this.environment);
            return platform != null && platform.isUsingForwardHeaders();
        }
        return this.serverProperties.getForwardHeadersStrategy().equals(ServerProperties.ForwardHeadersStrategy.NATIVE);
    }

    private void customizeIdleTimeout(ConfigurableJettyWebServerFactory factory, Duration connectionTimeout) {
        factory.addServerCustomizers(server -> {
            for (AbstractConnector abstractConnector : server.getConnectors()) {
                if (abstractConnector instanceof AbstractConnector) {
                    AbstractConnector abstractConnector2 = abstractConnector;
                    abstractConnector2.setIdleTimeout(connectionTimeout.toMillis());
                }
            }
        });
    }

    private void customizeMaxHttpFormPostSize(ConfigurableJettyWebServerFactory factory, final int maxHttpFormPostSize) {
        factory.addServerCustomizers(new JettyServerCustomizer() { // from class: org.springframework.boot.autoconfigure.web.embedded.JettyWebServerFactoryCustomizer.1
            @Override // org.springframework.boot.web.embedded.jetty.JettyServerCustomizer
            public void customize(Server server) {
                setHandlerMaxHttpFormPostSize(server.getHandlers());
            }

            private void setHandlerMaxHttpFormPostSize(List<Handler> handlers) {
                for (Handler handler : handlers) {
                    setHandlerMaxHttpFormPostSize(handler);
                }
            }

            private void setHandlerMaxHttpFormPostSize(Handler handler) {
                if (handler instanceof ServletContextHandler) {
                    ServletContextHandler contextHandler = (ServletContextHandler) handler;
                    contextHandler.setMaxFormContentSize(maxHttpFormPostSize);
                } else if (handler instanceof Handler.Wrapper) {
                    Handler.Wrapper wrapper = (Handler.Wrapper) handler;
                    setHandlerMaxHttpFormPostSize(wrapper.getHandler());
                } else if (handler instanceof Handler.Collection) {
                    Handler.Collection collection = (Handler.Collection) handler;
                    setHandlerMaxHttpFormPostSize(collection.getHandlers());
                }
            }
        });
    }

    private void customizeAccessLog(ConfigurableJettyWebServerFactory factory, ServerProperties.Jetty.Accesslog properties) {
        factory.addServerCustomizers(server -> {
            RequestLogWriter logWriter = new RequestLogWriter();
            String format = getLogFormat(properties);
            CustomRequestLog log = new CustomRequestLog(logWriter, format);
            if (!CollectionUtils.isEmpty(properties.getIgnorePaths())) {
                log.setIgnorePaths((String[]) properties.getIgnorePaths().toArray(new String[0]));
            }
            if (properties.getFilename() != null) {
                logWriter.setFilename(properties.getFilename());
            }
            if (properties.getFileDateFormat() != null) {
                logWriter.setFilenameDateFormat(properties.getFileDateFormat());
            }
            logWriter.setRetainDays(properties.getRetentionPeriod());
            logWriter.setAppend(properties.isAppend());
            server.setRequestLog(log);
        });
    }

    private String getLogFormat(ServerProperties.Jetty.Accesslog properties) {
        if (properties.getCustomFormat() != null) {
            return properties.getCustomFormat();
        }
        if (ServerProperties.Jetty.Accesslog.FORMAT.EXTENDED_NCSA.equals(properties.getFormat())) {
            return "%{client}a - %u %t \"%r\" %s %O \"%{Referer}i\" \"%{User-Agent}i\"";
        }
        return "%{client}a - %u %t \"%r\" %s %O";
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/embedded/JettyWebServerFactoryCustomizer$MaxHttpRequestHeaderSizeCustomizer.class */
    public static class MaxHttpRequestHeaderSizeCustomizer implements JettyServerCustomizer {
        private final int maxRequestHeaderSize;

        MaxHttpRequestHeaderSizeCustomizer(int maxRequestHeaderSize) {
            this.maxRequestHeaderSize = maxRequestHeaderSize;
        }

        @Override // org.springframework.boot.web.embedded.jetty.JettyServerCustomizer
        public void customize(Server server) {
            Arrays.stream(server.getConnectors()).forEach(this::customize);
        }

        private void customize(Connector connector) {
            connector.getConnectionFactories().forEach(this::customize);
        }

        private void customize(ConnectionFactory factory) {
            if (factory instanceof HttpConfiguration.ConnectionFactory) {
                ((HttpConfiguration.ConnectionFactory) factory).getHttpConfiguration().setRequestHeaderSize(this.maxRequestHeaderSize);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/embedded/JettyWebServerFactoryCustomizer$MaxHttpResponseHeaderSizeCustomizer.class */
    public static class MaxHttpResponseHeaderSizeCustomizer implements JettyServerCustomizer {
        private final int maxResponseHeaderSize;

        MaxHttpResponseHeaderSizeCustomizer(int maxResponseHeaderSize) {
            this.maxResponseHeaderSize = maxResponseHeaderSize;
        }

        @Override // org.springframework.boot.web.embedded.jetty.JettyServerCustomizer
        public void customize(Server server) {
            Arrays.stream(server.getConnectors()).forEach(this::customize);
        }

        private void customize(Connector connector) {
            connector.getConnectionFactories().forEach(this::customize);
        }

        private void customize(ConnectionFactory factory) {
            if (factory instanceof HttpConfiguration.ConnectionFactory) {
                HttpConfiguration.ConnectionFactory httpConnectionFactory = (HttpConfiguration.ConnectionFactory) factory;
                httpConnectionFactory.getHttpConfiguration().setResponseHeaderSize(this.maxResponseHeaderSize);
            }
        }
    }
}
