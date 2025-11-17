package org.springframework.boot.autoconfigure.r2dbc;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/r2dbc/MultipleConnectionPoolConfigurationsException.class */
class MultipleConnectionPoolConfigurationsException extends RuntimeException {
    /* JADX INFO: Access modifiers changed from: package-private */
    public MultipleConnectionPoolConfigurationsException() {
        super("R2DBC connection pooling configuration should be provided by either the spring.r2dbc.pool.* properties or the spring.r2dbc.url property but both have been used.");
    }
}
