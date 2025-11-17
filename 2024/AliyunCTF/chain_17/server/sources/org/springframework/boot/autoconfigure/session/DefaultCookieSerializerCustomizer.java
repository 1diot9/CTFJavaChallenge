package org.springframework.boot.autoconfigure.session;

import org.springframework.session.web.http.DefaultCookieSerializer;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/DefaultCookieSerializerCustomizer.class */
public interface DefaultCookieSerializerCustomizer {
    void customize(DefaultCookieSerializer cookieSerializer);
}
