package org.springframework.boot.r2dbc;

import io.r2dbc.spi.ConnectionFactory;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/r2dbc/ConnectionFactoryDecorator.class */
public interface ConnectionFactoryDecorator {
    ConnectionFactory decorate(ConnectionFactory delegate);
}
