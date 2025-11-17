package org.springframework.boot.rsocket.server;

import java.net.InetAddress;
import org.springframework.boot.rsocket.server.RSocketServer;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.SslStoreProvider;
import org.springframework.util.unit.DataSize;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/rsocket/server/ConfigurableRSocketServerFactory.class */
public interface ConfigurableRSocketServerFactory {
    void setPort(int port);

    void setFragmentSize(DataSize fragmentSize);

    void setAddress(InetAddress address);

    void setTransport(RSocketServer.Transport transport);

    void setSsl(Ssl ssl);

    @Deprecated(since = "3.1.0", forRemoval = true)
    void setSslStoreProvider(SslStoreProvider sslStoreProvider);

    void setSslBundles(SslBundles sslBundles);
}
