package org.jooq.impl;

import java.io.Serializable;
import java.util.function.Supplier;

@org.jooq.Internal
@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/LazySupplier.class */
public interface LazySupplier<T> extends Serializable, Supplier<T> {
}
