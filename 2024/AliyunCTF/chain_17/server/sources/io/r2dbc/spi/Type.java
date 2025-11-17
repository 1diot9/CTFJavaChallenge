package io.r2dbc.spi;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Type.class */
public interface Type {

    /* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Type$InferredType.class */
    public interface InferredType extends Type {
    }

    Class<?> getJavaType();

    String getName();
}
