package io.r2dbc.spi;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/ConstantPool.class */
abstract class ConstantPool<T> {
    private final ConcurrentMap<String, T> constants = new ConcurrentHashMap();

    abstract T createConstant(String str, boolean z);

    public String toString() {
        return "ConstantPool{constants=" + this.constants + '}';
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final T valueOf(String name, boolean sensitive) {
        Assert.requireNonNull(name, "name must not be null");
        Assert.requireNonEmpty(name, "name must not be empty");
        return this.constants.computeIfAbsent(name, n -> {
            return createConstant(n, sensitive);
        });
    }
}
