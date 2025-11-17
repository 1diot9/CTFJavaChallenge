package org.springframework.boot.autoconfigure.r2dbc;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/r2dbc/MissingR2dbcPoolDependencyException.class */
class MissingR2dbcPoolDependencyException extends RuntimeException {
    /* JADX INFO: Access modifiers changed from: package-private */
    public MissingR2dbcPoolDependencyException() {
        super("R2DBC connection pooling has been configured but the io.r2dbc.pool.ConnectionPool class is not present.");
    }
}
