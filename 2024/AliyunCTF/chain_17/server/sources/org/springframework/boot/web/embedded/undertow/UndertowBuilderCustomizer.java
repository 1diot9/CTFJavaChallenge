package org.springframework.boot.web.embedded.undertow;

import io.undertow.Undertow;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/undertow/UndertowBuilderCustomizer.class */
public interface UndertowBuilderCustomizer {
    void customize(Undertow.Builder builder);
}
