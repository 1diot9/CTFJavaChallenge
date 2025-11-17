package org.springframework.boot.web.codec;

import org.springframework.http.codec.CodecConfigurer;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/codec/CodecCustomizer.class */
public interface CodecCustomizer {
    void customize(CodecConfigurer configurer);
}
