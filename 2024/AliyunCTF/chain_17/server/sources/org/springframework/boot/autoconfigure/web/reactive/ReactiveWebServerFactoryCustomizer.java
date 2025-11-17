package org.springframework.boot.autoconfigure.web.reactive;

import java.util.Objects;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveWebServerFactoryCustomizer.class */
public class ReactiveWebServerFactoryCustomizer implements WebServerFactoryCustomizer<ConfigurableReactiveWebServerFactory>, Ordered {
    private final ServerProperties serverProperties;
    private final SslBundles sslBundles;

    public ReactiveWebServerFactoryCustomizer(ServerProperties serverProperties) {
        this(serverProperties, null);
    }

    public ReactiveWebServerFactoryCustomizer(ServerProperties serverProperties, SslBundles sslBundles) {
        this.serverProperties = serverProperties;
        this.sslBundles = sslBundles;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(ConfigurableReactiveWebServerFactory factory) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        ServerProperties serverProperties = this.serverProperties;
        Objects.requireNonNull(serverProperties);
        PropertyMapper.Source from = map.from(serverProperties::getPort);
        Objects.requireNonNull(factory);
        from.to((v1) -> {
            r1.setPort(v1);
        });
        ServerProperties serverProperties2 = this.serverProperties;
        Objects.requireNonNull(serverProperties2);
        PropertyMapper.Source from2 = map.from(serverProperties2::getAddress);
        Objects.requireNonNull(factory);
        from2.to(factory::setAddress);
        ServerProperties serverProperties3 = this.serverProperties;
        Objects.requireNonNull(serverProperties3);
        PropertyMapper.Source from3 = map.from(serverProperties3::getSsl);
        Objects.requireNonNull(factory);
        from3.to(factory::setSsl);
        ServerProperties serverProperties4 = this.serverProperties;
        Objects.requireNonNull(serverProperties4);
        PropertyMapper.Source from4 = map.from(serverProperties4::getCompression);
        Objects.requireNonNull(factory);
        from4.to(factory::setCompression);
        ServerProperties serverProperties5 = this.serverProperties;
        Objects.requireNonNull(serverProperties5);
        PropertyMapper.Source from5 = map.from(serverProperties5::getHttp2);
        Objects.requireNonNull(factory);
        from5.to(factory::setHttp2);
        PropertyMapper.Source from6 = map.from((PropertyMapper) this.serverProperties.getShutdown());
        Objects.requireNonNull(factory);
        from6.to(factory::setShutdown);
        PropertyMapper.Source from7 = map.from(() -> {
            return this.sslBundles;
        });
        Objects.requireNonNull(factory);
        from7.to(factory::setSslBundles);
    }
}
