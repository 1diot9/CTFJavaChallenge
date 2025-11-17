package org.springframework.boot.autoconfigure.security.oauth2.resource.servlet;

import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/servlet/JwkSetUriJwtDecoderBuilderCustomizer.class */
public interface JwkSetUriJwtDecoderBuilderCustomizer {
    void customize(NimbusJwtDecoder.JwkSetUriJwtDecoderBuilder builder);
}
