package org.springframework.boot.autoconfigure.service.connection;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/service/connection/ConnectionDetailsFactoryNotFoundException.class */
public class ConnectionDetailsFactoryNotFoundException extends RuntimeException {
    /* JADX INFO: Access modifiers changed from: package-private */
    public <S> ConnectionDetailsFactoryNotFoundException(S source) {
        this("No ConnectionDetailsFactory found for source '%s'".formatted(source));
    }

    public ConnectionDetailsFactoryNotFoundException(String message) {
        super(message);
    }

    public ConnectionDetailsFactoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
