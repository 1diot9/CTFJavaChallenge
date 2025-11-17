package io.r2dbc.spi;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Readable.class */
public interface Readable {
    @Nullable
    <T> T get(int i, Class<T> cls);

    @Nullable
    <T> T get(String str, Class<T> cls);

    @Nullable
    default Object get(int index) {
        return get(index, Object.class);
    }

    @Nullable
    default Object get(String name) {
        return get(name, Object.class);
    }
}
