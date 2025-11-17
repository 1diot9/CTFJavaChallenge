package org.springframework.boot.autoconfigure.service.connection;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/service/connection/ConnectionDetailsNotFoundException.class */
public class ConnectionDetailsNotFoundException extends RuntimeException {
    /* JADX INFO: Access modifiers changed from: package-private */
    public <S> ConnectionDetailsNotFoundException(S source) {
        this("No ConnectionDetails found for source '%s'".formatted(source));
    }

    public ConnectionDetailsNotFoundException(String message) {
        super(message);
    }

    public ConnectionDetailsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
