package org.springframework.boot.autoconfigure.security.oauth2.resource.reactive;

import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/reactive/JwkSetUriReactiveJwtDecoderBuilderCustomizer.class */
public interface JwkSetUriReactiveJwtDecoderBuilderCustomizer {
    void customize(NimbusReactiveJwtDecoder.JwkSetUriReactiveJwtDecoderBuilder builder);
}
