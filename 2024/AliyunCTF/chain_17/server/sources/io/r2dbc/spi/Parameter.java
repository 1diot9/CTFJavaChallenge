package io.r2dbc.spi;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Parameter.class */
public interface Parameter {

    /* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Parameter$In.class */
    public interface In {
    }

    /* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Parameter$Out.class */
    public interface Out {
    }

    Type getType();

    @Nullable
    Object getValue();
}
