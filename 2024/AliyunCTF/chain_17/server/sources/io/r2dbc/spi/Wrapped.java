package io.r2dbc.spi;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Wrapped.class */
public interface Wrapped<T> {
    T unwrap();

    /* JADX WARN: Multi-variable type inference failed */
    @Nullable
    default <E> E unwrap(Class<E> cls) {
        Assert.requireNonNull(cls, "targetClass must not be null");
        if (cls.isInstance(this)) {
            return this;
        }
        Object unwrap = unwrap();
        if (!(unwrap instanceof Wrapped)) {
            return null;
        }
        return (E) ((Wrapped) unwrap).unwrap(cls);
    }
}
