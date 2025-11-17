package org.springframework.boot.web.server;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/server/GracefulShutdownResult.class */
public enum GracefulShutdownResult {
    REQUESTS_ACTIVE,
    IDLE,
    IMMEDIATE
}
