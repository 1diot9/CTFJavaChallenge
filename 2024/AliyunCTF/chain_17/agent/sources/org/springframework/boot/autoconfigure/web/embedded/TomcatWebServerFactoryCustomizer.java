package org.springframework.boot.autoconfigure.web.embedded;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.function.ObjIntConsumer;
import java.util.stream.Collectors;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.valves.AccessLogValve;
import org.apache.catalina.valves.ErrorReportValve;
import org.apache.catalina.valves.RemoteIpValve;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.coyote.http2.Http2Protocol;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/embedded/TomcatWebServerFactoryCustomizer.class */
public class TomcatWebServerFactoryCustomizer implements WebServerFactoryCustomizer<ConfigurableTomcatWebServerFactory>, Ordered {
    static final int ORDER = 0;
    private final Environment environment;
    private final ServerProperties serverProperties;

    public TomcatWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties) {
        this.environment = environment;
        this.serverProperties = serverProperties;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(ConfigurableTomcatWebServerFactory factory) {
        ServerProperties.Tomcat properties = this.serverProperties.getTomcat();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(properties);
        PropertyMapper.Source from = map.from(properties::getBasedir);
        Objects.requireNonNull(factory);
        from.to(factory::setBaseDirectory);
        Objects.requireNonNull(properties);
        PropertyMapper.Source as = map.from(properties::getBackgroundProcessorDelay).as((v0) -> {
            return v0.getSeconds();
        }).as((v0) -> {
            return v0.intValue();
        });
        Objects.requireNonNull(factory);
        as.to((v1) -> {
            r1.setBackgroundProcessorDelay(v1);
        });
        customizeRemoteIpValve(factory);
        ServerProperties.Tomcat.Threads threadProperties = properties.getThreads();
        Objects.requireNonNull(threadProperties);
        map.from(threadProperties::getMax).when((v1) -> {
            return isPositive(v1);
        }).to(maxThreads -> {
            customizeMaxThreads(factory, threadProperties.getMax());
        });
        Objects.requireNonNull(threadProperties);
        map.from(threadProperties::getMinSpare).when((v1) -> {
            return isPositive(v1);
        }).to(minSpareThreads -> {
            customizeMinThreads(factory, minSpareThreads.intValue());
        });
        map.from((PropertyMapper) this.serverProperties.getMaxHttpRequestHeaderSize()).asInt((v0) -> {
            return v0.toBytes();
        }).when((v1) -> {
            return isPositive(v1);
        }).to(maxHttpRequestHeaderSize -> {
            customizeMaxHttpRequestHeaderSize(factory, maxHttpRequestHeaderSize.intValue());
        });
        Objects.requireNonNull(properties);
        map.from(properties::getMaxHttpResponseHeaderSize).asInt((v0) -> {
            return v0.toBytes();
        }).when((v1) -> {
            return isPositive(v1);
        }).to(maxHttpResponseHeaderSize -> {
            customizeMaxHttpResponseHeaderSize(factory, maxHttpResponseHeaderSize.intValue());
        });
        Objects.requireNonNull(properties);
        map.from(properties::getMaxSwallowSize).asInt((v0) -> {
            return v0.toBytes();
        }).to(maxSwallowSize -> {
            customizeMaxSwallowSize(factory, maxSwallowSize.intValue());
        });
        Objects.requireNonNull(properties);
        map.from(properties::getMaxHttpFormPostSize).asInt((v0) -> {
            return v0.toBytes();
        }).when(maxHttpFormPostSize -> {
            return maxHttpFormPostSize.intValue() != 0;
        }).to(maxHttpFormPostSize2 -> {
            customizeMaxHttpFormPostSize(factory, maxHttpFormPostSize2.intValue());
        });
        Objects.requireNonNull(properties);
        map.from(properties::getAccesslog).when((v0) -> {
            return v0.isEnabled();
        }).to(enabled -> {
            customizeAccessLog(factory);
        });
        Objects.requireNonNull(properties);
        PropertyMapper.Source from2 = map.from(properties::getUriEncoding);
        Objects.requireNonNull(factory);
        from2.to(factory::setUriEncoding);
        Objects.requireNonNull(properties);
        map.from(properties::getConnectionTimeout).to(connectionTimeout -> {
            customizeConnectionTimeout(factory, connectionTimeout);
        });
        Objects.requireNonNull(properties);
        map.from(properties::getMaxConnections).when((v1) -> {
            return isPositive(v1);
        }).to(maxConnections -> {
            customizeMaxConnections(factory, maxConnections.intValue());
        });
        Objects.requireNonNull(properties);
        map.from(properties::getAcceptCount).when((v1) -> {
            return isPositive(v1);
        }).to(acceptCount -> {
            customizeAcceptCount(factory, acceptCount.intValue());
        });
        Objects.requireNonNull(properties);
        map.from(properties::getProcessorCache).to(processorCache -> {
            customizeProcessorCache(factory, processorCache.intValue());
        });
        Objects.requireNonNull(properties);
        map.from(properties::getKeepAliveTimeout).to(keepAliveTimeout -> {
            customizeKeepAliveTimeout(factory, keepAliveTimeout);
        });
        Objects.requireNonNull(properties);
        map.from(properties::getMaxKeepAliveRequests).to(maxKeepAliveRequests -> {
            customizeMaxKeepAliveRequests(factory, maxKeepAliveRequests.intValue());
        });
        Objects.requireNonNull(properties);
        map.from(properties::getRelaxedPathChars).as(this::joinCharacters).whenHasText().to(relaxedChars -> {
            customizeRelaxedPathChars(factory, relaxedChars);
        });
        Objects.requireNonNull(properties);
        map.from(properties::getRelaxedQueryChars).as(this::joinCharacters).whenHasText().to(relaxedChars2 -> {
            customizeRelaxedQueryChars(factory, relaxedChars2);
        });
        Objects.requireNonNull(properties);
        map.from(properties::isRejectIllegalHeader).to(rejectIllegalHeader -> {
            customizeRejectIllegalHeader(factory, rejectIllegalHeader.booleanValue());
        });
        customizeStaticResources(factory);
        customizeErrorReportValve(this.serverProperties.getError(), factory);
    }

    private boolean isPositive(int value) {
        return value > 0;
    }

    private void customizeAcceptCount(ConfigurableTomcatWebServerFactory factory, int acceptCount) {
        customizeHandler(factory, acceptCount, AbstractProtocol.class, (v0, v1) -> {
            v0.setAcceptCount(v1);
        });
    }

    private void customizeProcessorCache(ConfigurableTomcatWebServerFactory factory, int processorCache) {
        customizeHandler(factory, processorCache, AbstractProtocol.class, (v0, v1) -> {
            v0.setProcessorCache(v1);
        });
    }

    private void customizeKeepAliveTimeout(ConfigurableTomcatWebServerFactory factory, Duration keepAliveTimeout) {
        factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            for (UpgradeProtocol upgradeProtocol : handler.findUpgradeProtocols()) {
                if (upgradeProtocol instanceof Http2Protocol) {
                    Http2Protocol protocol = (Http2Protocol) upgradeProtocol;
                    protocol.setKeepAliveTimeout(keepAliveTimeout.toMillis());
                }
            }
            if (handler instanceof AbstractProtocol) {
                AbstractProtocol<?> protocol2 = (AbstractProtocol) handler;
                protocol2.setKeepAliveTimeout((int) keepAliveTimeout.toMillis());
            }
        });
    }

    private void customizeMaxKeepAliveRequests(ConfigurableTomcatWebServerFactory factory, int maxKeepAliveRequests) {
        customizeHandler(factory, maxKeepAliveRequests, AbstractHttp11Protocol.class, (v0, v1) -> {
            v0.setMaxKeepAliveRequests(v1);
        });
    }

    private void customizeMaxConnections(ConfigurableTomcatWebServerFactory factory, int maxConnections) {
        customizeHandler(factory, maxConnections, AbstractProtocol.class, (v0, v1) -> {
            v0.setMaxConnections(v1);
        });
    }

    private void customizeConnectionTimeout(ConfigurableTomcatWebServerFactory factory, Duration connectionTimeout) {
        customizeHandler(factory, (int) connectionTimeout.toMillis(), AbstractProtocol.class, (v0, v1) -> {
            v0.setConnectionTimeout(v1);
        });
    }

    private void customizeRelaxedPathChars(ConfigurableTomcatWebServerFactory factory, String relaxedChars) {
        factory.addConnectorCustomizers(connector -> {
            connector.setProperty("relaxedPathChars", relaxedChars);
        });
    }

    private void customizeRelaxedQueryChars(ConfigurableTomcatWebServerFactory factory, String relaxedChars) {
        factory.addConnectorCustomizers(connector -> {
            connector.setProperty("relaxedQueryChars", relaxedChars);
        });
    }

    private void customizeRejectIllegalHeader(ConfigurableTomcatWebServerFactory factory, boolean rejectIllegalHeader) {
        factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            if (handler instanceof AbstractHttp11Protocol) {
                AbstractHttp11Protocol<?> protocol = (AbstractHttp11Protocol) handler;
                protocol.setRejectIllegalHeader(rejectIllegalHeader);
            }
        });
    }

    private String joinCharacters(List<Character> content) {
        return (String) content.stream().map((v0) -> {
            return String.valueOf(v0);
        }).collect(Collectors.joining());
    }

    private void customizeRemoteIpValve(ConfigurableTomcatWebServerFactory factory) {
        ServerProperties.Tomcat.Remoteip remoteIpProperties = this.serverProperties.getTomcat().getRemoteip();
        String protocolHeader = remoteIpProperties.getProtocolHeader();
        String remoteIpHeader = remoteIpProperties.getRemoteIpHeader();
        if (StringUtils.hasText(protocolHeader) || StringUtils.hasText(remoteIpHeader) || getOrDeduceUseForwardHeaders()) {
            RemoteIpValve valve = new RemoteIpValve();
            valve.setProtocolHeader(StringUtils.hasLength(protocolHeader) ? protocolHeader : "X-Forwarded-Proto");
            if (StringUtils.hasLength(remoteIpHeader)) {
                valve.setRemoteIpHeader(remoteIpHeader);
            }
            valve.setTrustedProxies(remoteIpProperties.getTrustedProxies());
            valve.setInternalProxies(remoteIpProperties.getInternalProxies());
            try {
                valve.setHostHeader(remoteIpProperties.getHostHeader());
            } catch (NoSuchMethodError e) {
            }
            valve.setPortHeader(remoteIpProperties.getPortHeader());
            valve.setProtocolHeaderHttpsValue(remoteIpProperties.getProtocolHeaderHttpsValue());
            factory.addEngineValves(valve);
        }
    }

    private boolean getOrDeduceUseForwardHeaders() {
        if (this.serverProperties.getForwardHeadersStrategy() != null) {
            return this.serverProperties.getForwardHeadersStrategy() == ServerProperties.ForwardHeadersStrategy.NATIVE;
        }
        CloudPlatform platform = CloudPlatform.getActive(this.environment);
        return platform != null && platform.isUsingForwardHeaders();
    }

    private void customizeMaxThreads(ConfigurableTomcatWebServerFactory factory, int maxThreads) {
        customizeHandler(factory, maxThreads, AbstractProtocol.class, (v0, v1) -> {
            v0.setMaxThreads(v1);
        });
    }

    private void customizeMinThreads(ConfigurableTomcatWebServerFactory factory, int minSpareThreads) {
        customizeHandler(factory, minSpareThreads, AbstractProtocol.class, (v0, v1) -> {
            v0.setMinSpareThreads(v1);
        });
    }

    private void customizeMaxHttpRequestHeaderSize(ConfigurableTomcatWebServerFactory factory, int maxHttpRequestHeaderSize) {
        customizeHandler(factory, maxHttpRequestHeaderSize, AbstractHttp11Protocol.class, (v0, v1) -> {
            v0.setMaxHttpRequestHeaderSize(v1);
        });
    }

    private void customizeMaxHttpResponseHeaderSize(ConfigurableTomcatWebServerFactory factory, int maxHttpResponseHeaderSize) {
        customizeHandler(factory, maxHttpResponseHeaderSize, AbstractHttp11Protocol.class, (v0, v1) -> {
            v0.setMaxHttpResponseHeaderSize(v1);
        });
    }

    private void customizeMaxSwallowSize(ConfigurableTomcatWebServerFactory factory, int maxSwallowSize) {
        customizeHandler(factory, maxSwallowSize, AbstractHttp11Protocol.class, (v0, v1) -> {
            v0.setMaxSwallowSize(v1);
        });
    }

    private <T extends ProtocolHandler> void customizeHandler(ConfigurableTomcatWebServerFactory factory, int value, Class<T> type, ObjIntConsumer<T> consumer) {
        factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            if (type.isAssignableFrom(handler.getClass())) {
                consumer.accept((ProtocolHandler) type.cast(handler), value);
            }
        });
    }

    private void customizeMaxHttpFormPostSize(ConfigurableTomcatWebServerFactory factory, int maxHttpFormPostSize) {
        factory.addConnectorCustomizers(connector -> {
            connector.setMaxPostSize(maxHttpFormPostSize);
        });
    }

    private void customizeAccessLog(ConfigurableTomcatWebServerFactory factory) {
        ServerProperties.Tomcat tomcatProperties = this.serverProperties.getTomcat();
        AccessLogValve valve = new AccessLogValve();
        PropertyMapper map = PropertyMapper.get();
        ServerProperties.Tomcat.Accesslog accessLogConfig = tomcatProperties.getAccesslog();
        PropertyMapper.Source from = map.from((PropertyMapper) accessLogConfig.getConditionIf());
        Objects.requireNonNull(valve);
        from.to(valve::setConditionIf);
        PropertyMapper.Source from2 = map.from((PropertyMapper) accessLogConfig.getConditionUnless());
        Objects.requireNonNull(valve);
        from2.to(valve::setConditionUnless);
        PropertyMapper.Source from3 = map.from((PropertyMapper) accessLogConfig.getPattern());
        Objects.requireNonNull(valve);
        from3.to(valve::setPattern);
        PropertyMapper.Source from4 = map.from((PropertyMapper) accessLogConfig.getDirectory());
        Objects.requireNonNull(valve);
        from4.to(valve::setDirectory);
        PropertyMapper.Source from5 = map.from((PropertyMapper) accessLogConfig.getPrefix());
        Objects.requireNonNull(valve);
        from5.to(valve::setPrefix);
        PropertyMapper.Source from6 = map.from((PropertyMapper) accessLogConfig.getSuffix());
        Objects.requireNonNull(valve);
        from6.to(valve::setSuffix);
        PropertyMapper.Source whenHasText = map.from((PropertyMapper) accessLogConfig.getEncoding()).whenHasText();
        Objects.requireNonNull(valve);
        whenHasText.to(valve::setEncoding);
        PropertyMapper.Source whenHasText2 = map.from((PropertyMapper) accessLogConfig.getLocale()).whenHasText();
        Objects.requireNonNull(valve);
        whenHasText2.to(valve::setLocale);
        PropertyMapper.Source from7 = map.from((PropertyMapper) Boolean.valueOf(accessLogConfig.isCheckExists()));
        Objects.requireNonNull(valve);
        from7.to((v1) -> {
            r1.setCheckExists(v1);
        });
        PropertyMapper.Source from8 = map.from((PropertyMapper) Boolean.valueOf(accessLogConfig.isRotate()));
        Objects.requireNonNull(valve);
        from8.to((v1) -> {
            r1.setRotatable(v1);
        });
        PropertyMapper.Source from9 = map.from((PropertyMapper) Boolean.valueOf(accessLogConfig.isRenameOnRotate()));
        Objects.requireNonNull(valve);
        from9.to((v1) -> {
            r1.setRenameOnRotate(v1);
        });
        PropertyMapper.Source from10 = map.from((PropertyMapper) Integer.valueOf(accessLogConfig.getMaxDays()));
        Objects.requireNonNull(valve);
        from10.to((v1) -> {
            r1.setMaxDays(v1);
        });
        PropertyMapper.Source from11 = map.from((PropertyMapper) accessLogConfig.getFileDateFormat());
        Objects.requireNonNull(valve);
        from11.to(valve::setFileDateFormat);
        PropertyMapper.Source from12 = map.from((PropertyMapper) Boolean.valueOf(accessLogConfig.isIpv6Canonical()));
        Objects.requireNonNull(valve);
        from12.to((v1) -> {
            r1.setIpv6Canonical(v1);
        });
        PropertyMapper.Source from13 = map.from((PropertyMapper) Boolean.valueOf(accessLogConfig.isRequestAttributesEnabled()));
        Objects.requireNonNull(valve);
        from13.to((v1) -> {
            r1.setRequestAttributesEnabled(v1);
        });
        PropertyMapper.Source from14 = map.from((PropertyMapper) Boolean.valueOf(accessLogConfig.isBuffered()));
        Objects.requireNonNull(valve);
        from14.to((v1) -> {
            r1.setBuffered(v1);
        });
        factory.addEngineValves(valve);
    }

    private void customizeStaticResources(ConfigurableTomcatWebServerFactory factory) {
        ServerProperties.Tomcat.Resource resource = this.serverProperties.getTomcat().getResource();
        factory.addContextCustomizers(context -> {
            context.addLifecycleListener(event -> {
                if (event.getType().equals(Lifecycle.CONFIGURE_START_EVENT)) {
                    context.getResources().setCachingAllowed(resource.isAllowCaching());
                    if (resource.getCacheTtl() != null) {
                        long ttl = resource.getCacheTtl().toMillis();
                        context.getResources().setCacheTtl(ttl);
                    }
                }
            });
        });
    }

    private void customizeErrorReportValve(ErrorProperties error, ConfigurableTomcatWebServerFactory factory) {
        if (error.getIncludeStacktrace() == ErrorProperties.IncludeAttribute.NEVER) {
            factory.addContextCustomizers(context -> {
                ErrorReportValve valve = new ErrorReportValve();
                valve.setShowServerInfo(false);
                valve.setShowReport(false);
                context.getParent().getPipeline().addValve(valve);
            });
        }
    }
}
