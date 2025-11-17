package org.springframework.boot.autoconfigure.web.servlet;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.WebListenerRegistrar;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryCustomizer.class */
public class ServletWebServerFactoryCustomizer implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>, Ordered {
    private final ServerProperties serverProperties;
    private final List<WebListenerRegistrar> webListenerRegistrars;
    private final List<CookieSameSiteSupplier> cookieSameSiteSuppliers;
    private final SslBundles sslBundles;

    public ServletWebServerFactoryCustomizer(ServerProperties serverProperties) {
        this(serverProperties, Collections.emptyList());
    }

    public ServletWebServerFactoryCustomizer(ServerProperties serverProperties, List<WebListenerRegistrar> webListenerRegistrars) {
        this(serverProperties, webListenerRegistrars, null, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ServletWebServerFactoryCustomizer(ServerProperties serverProperties, List<WebListenerRegistrar> webListenerRegistrars, List<CookieSameSiteSupplier> cookieSameSiteSuppliers, SslBundles sslBundles) {
        this.serverProperties = serverProperties;
        this.webListenerRegistrars = webListenerRegistrars;
        this.cookieSameSiteSuppliers = cookieSameSiteSuppliers;
        this.sslBundles = sslBundles;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(ConfigurableServletWebServerFactory factory) {
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
        ServerProperties.Servlet servlet = this.serverProperties.getServlet();
        Objects.requireNonNull(servlet);
        PropertyMapper.Source from3 = map.from(servlet::getContextPath);
        Objects.requireNonNull(factory);
        from3.to(factory::setContextPath);
        ServerProperties.Servlet servlet2 = this.serverProperties.getServlet();
        Objects.requireNonNull(servlet2);
        PropertyMapper.Source from4 = map.from(servlet2::getApplicationDisplayName);
        Objects.requireNonNull(factory);
        from4.to(factory::setDisplayName);
        ServerProperties.Servlet servlet3 = this.serverProperties.getServlet();
        Objects.requireNonNull(servlet3);
        PropertyMapper.Source from5 = map.from(servlet3::isRegisterDefaultServlet);
        Objects.requireNonNull(factory);
        from5.to((v1) -> {
            r1.setRegisterDefaultServlet(v1);
        });
        ServerProperties.Servlet servlet4 = this.serverProperties.getServlet();
        Objects.requireNonNull(servlet4);
        PropertyMapper.Source from6 = map.from(servlet4::getSession);
        Objects.requireNonNull(factory);
        from6.to(factory::setSession);
        ServerProperties serverProperties3 = this.serverProperties;
        Objects.requireNonNull(serverProperties3);
        PropertyMapper.Source from7 = map.from(serverProperties3::getSsl);
        Objects.requireNonNull(factory);
        from7.to(factory::setSsl);
        ServerProperties.Servlet servlet5 = this.serverProperties.getServlet();
        Objects.requireNonNull(servlet5);
        PropertyMapper.Source from8 = map.from(servlet5::getJsp);
        Objects.requireNonNull(factory);
        from8.to(factory::setJsp);
        ServerProperties serverProperties4 = this.serverProperties;
        Objects.requireNonNull(serverProperties4);
        PropertyMapper.Source from9 = map.from(serverProperties4::getCompression);
        Objects.requireNonNull(factory);
        from9.to(factory::setCompression);
        ServerProperties serverProperties5 = this.serverProperties;
        Objects.requireNonNull(serverProperties5);
        PropertyMapper.Source from10 = map.from(serverProperties5::getHttp2);
        Objects.requireNonNull(factory);
        from10.to(factory::setHttp2);
        ServerProperties serverProperties6 = this.serverProperties;
        Objects.requireNonNull(serverProperties6);
        PropertyMapper.Source from11 = map.from(serverProperties6::getServerHeader);
        Objects.requireNonNull(factory);
        from11.to(factory::setServerHeader);
        ServerProperties.Servlet servlet6 = this.serverProperties.getServlet();
        Objects.requireNonNull(servlet6);
        PropertyMapper.Source from12 = map.from(servlet6::getContextParameters);
        Objects.requireNonNull(factory);
        from12.to(factory::setInitParameters);
        PropertyMapper.Source from13 = map.from((PropertyMapper) this.serverProperties.getShutdown());
        Objects.requireNonNull(factory);
        from13.to(factory::setShutdown);
        PropertyMapper.Source from14 = map.from(() -> {
            return this.sslBundles;
        });
        Objects.requireNonNull(factory);
        from14.to(factory::setSslBundles);
        PropertyMapper.Source whenNot = map.from(() -> {
            return this.cookieSameSiteSuppliers;
        }).whenNot((v0) -> {
            return CollectionUtils.isEmpty(v0);
        });
        Objects.requireNonNull(factory);
        whenNot.to(factory::setCookieSameSiteSuppliers);
        this.webListenerRegistrars.forEach(registrar -> {
            registrar.register(factory);
        });
    }
}
