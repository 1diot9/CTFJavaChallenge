package org.springframework.boot.autoconfigure.rsocket;

import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.ServerTransport;
import io.rsocket.transport.netty.server.WebsocketRouteTransport;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.boot.web.embedded.netty.NettyRouteProvider;
import reactor.netty.http.server.HttpServerRoutes;
import reactor.netty.http.server.WebsocketServerSpec;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/rsocket/RSocketWebSocketNettyRouteProvider.class */
class RSocketWebSocketNettyRouteProvider implements NettyRouteProvider {
    private final String mappingPath;
    private final SocketAcceptor socketAcceptor;
    private final List<RSocketServerCustomizer> customizers;
    private final Consumer<WebsocketServerSpec.Builder> serverSpecCustomizer;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RSocketWebSocketNettyRouteProvider(String mappingPath, SocketAcceptor socketAcceptor, Consumer<WebsocketServerSpec.Builder> serverSpecCustomizer, Stream<RSocketServerCustomizer> customizers) {
        this.mappingPath = mappingPath;
        this.socketAcceptor = socketAcceptor;
        this.serverSpecCustomizer = serverSpecCustomizer;
        this.customizers = customizers.toList();
    }

    @Override // java.util.function.Function
    public HttpServerRoutes apply(HttpServerRoutes httpServerRoutes) {
        RSocketServer server = RSocketServer.create(this.socketAcceptor);
        this.customizers.forEach(customizer -> {
            customizer.customize(server);
        });
        ServerTransport.ConnectionAcceptor connectionAcceptor = server.asConnectionAcceptor();
        return httpServerRoutes.ws(this.mappingPath, WebsocketRouteTransport.newHandler(connectionAcceptor), createWebsocketServerSpec());
    }

    private WebsocketServerSpec createWebsocketServerSpec() {
        WebsocketServerSpec.Builder builder = WebsocketServerSpec.builder();
        this.serverSpecCustomizer.accept(builder);
        return builder.build();
    }
}
