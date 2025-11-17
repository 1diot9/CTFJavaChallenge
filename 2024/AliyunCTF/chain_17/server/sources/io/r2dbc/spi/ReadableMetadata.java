package io.r2dbc.spi;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/ReadableMetadata.class */
public interface ReadableMetadata {
    Type getType();

    String getName();

    @Nullable
    default Class<?> getJavaType() {
        return null;
    }

    @Nullable
    default Object getNativeTypeMetadata() {
        return null;
    }

    default Nullability getNullability() {
        return Nullability.UNKNOWN;
    }

    @Nullable
    default Integer getPrecision() {
        return null;
    }

    @Nullable
    default Integer getScale() {
        return null;
    }
}
