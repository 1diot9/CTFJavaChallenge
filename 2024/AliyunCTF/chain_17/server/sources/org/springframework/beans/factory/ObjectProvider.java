package org.springframework.beans.factory;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/ObjectProvider.class */
public interface ObjectProvider<T> extends ObjectFactory<T>, Iterable<T> {
    T getObject(Object... args) throws BeansException;

    @Nullable
    T getIfAvailable() throws BeansException;

    @Nullable
    T getIfUnique() throws BeansException;

    default T getIfAvailable(Supplier<T> defaultSupplier) throws BeansException {
        T dependency = getIfAvailable();
        return dependency != null ? dependency : defaultSupplier.get();
    }

    default void ifAvailable(Consumer<T> dependencyConsumer) throws BeansException {
        T dependency = getIfAvailable();
        if (dependency != null) {
            dependencyConsumer.accept(dependency);
        }
    }

    default T getIfUnique(Supplier<T> defaultSupplier) throws BeansException {
        T dependency = getIfUnique();
        return dependency != null ? dependency : defaultSupplier.get();
    }

    default void ifUnique(Consumer<T> dependencyConsumer) throws BeansException {
        T dependency = getIfUnique();
        if (dependency != null) {
            dependencyConsumer.accept(dependency);
        }
    }

    @Override // java.lang.Iterable
    default Iterator<T> iterator() {
        return stream().iterator();
    }

    default Stream<T> stream() {
        throw new UnsupportedOperationException("Multi element access not supported");
    }

    default Stream<T> orderedStream() {
        throw new UnsupportedOperationException("Ordered element access not supported");
    }
}
