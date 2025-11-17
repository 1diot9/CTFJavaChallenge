package io.r2dbc.spi;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/ConnectionFactoryProvider.class */
public interface ConnectionFactoryProvider {
    ConnectionFactory create(ConnectionFactoryOptions connectionFactoryOptions);

    boolean supports(ConnectionFactoryOptions connectionFactoryOptions);

    String getDriver();
}
