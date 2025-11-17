package org.springframework.boot.web.embedded.netty;

import java.util.function.Function;
import reactor.netty.http.server.HttpServerRoutes;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/netty/NettyRouteProvider.class */
public interface NettyRouteProvider extends Function<HttpServerRoutes, HttpServerRoutes> {
}
