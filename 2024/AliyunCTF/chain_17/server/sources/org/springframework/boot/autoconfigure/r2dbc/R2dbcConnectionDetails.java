package org.springframework.boot.autoconfigure.r2dbc;

import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/r2dbc/R2dbcConnectionDetails.class */
public interface R2dbcConnectionDetails extends ConnectionDetails {
    ConnectionFactoryOptions getConnectionFactoryOptions();
}
