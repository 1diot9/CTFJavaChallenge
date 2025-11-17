package org.springframework.boot.web.embedded.netty;

import java.util.function.Function;
import reactor.netty.http.server.HttpServer;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/netty/NettyServerCustomizer.class */
public interface NettyServerCustomizer extends Function<HttpServer, HttpServer> {
}
