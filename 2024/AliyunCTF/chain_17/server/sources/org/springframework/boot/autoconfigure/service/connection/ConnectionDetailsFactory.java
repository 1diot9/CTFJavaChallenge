package org.springframework.boot.autoconfigure.service.connection;

import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/service/connection/ConnectionDetailsFactory.class */
public interface ConnectionDetailsFactory<S, D extends ConnectionDetails> {
    D getConnectionDetails(S source);
}
