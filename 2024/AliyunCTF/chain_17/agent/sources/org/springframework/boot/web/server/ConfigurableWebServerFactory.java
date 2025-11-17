package org.springframework.boot.web.server;

import java.net.InetAddress;
import java.util.Set;
import org.springframework.boot.ssl.SslBundles;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/server/ConfigurableWebServerFactory.class */
public interface ConfigurableWebServerFactory extends WebServerFactory, ErrorPageRegistry {
    void setPort(int port);

    void setAddress(InetAddress address);

    void setErrorPages(Set<? extends ErrorPage> errorPages);

    void setSsl(Ssl ssl);

    @Deprecated(since = "3.1.0", forRemoval = true)
    void setSslStoreProvider(SslStoreProvider sslStoreProvider);

    void setSslBundles(SslBundles sslBundles);

    void setHttp2(Http2 http2);

    void setCompression(Compression compression);

    void setServerHeader(String serverHeader);

    default void setShutdown(Shutdown shutdown) {
    }
}
