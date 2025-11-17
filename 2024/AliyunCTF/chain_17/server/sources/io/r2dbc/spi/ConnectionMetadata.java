package io.r2dbc.spi;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/ConnectionMetadata.class */
public interface ConnectionMetadata {
    String getDatabaseProductName();

    String getDatabaseVersion();
}
