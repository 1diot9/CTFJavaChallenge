package org.springframework.boot.web.embedded.tomcat;

import org.apache.coyote.ProtocolHandler;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/tomcat/TomcatProtocolHandlerCustomizer.class */
public interface TomcatProtocolHandlerCustomizer<T extends ProtocolHandler> {
    void customize(T protocolHandler);
}
