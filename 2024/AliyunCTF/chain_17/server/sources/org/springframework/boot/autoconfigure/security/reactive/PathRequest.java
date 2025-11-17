package org.springframework.boot.autoconfigure.security.reactive;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/reactive/PathRequest.class */
public final class PathRequest {
    private PathRequest() {
    }

    public static StaticResourceRequest toStaticResources() {
        return StaticResourceRequest.INSTANCE;
    }
}
