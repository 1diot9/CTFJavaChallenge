package org.springframework.beans.factory.support;

import java.lang.reflect.Method;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.function.ThrowingBiFunction;
import org.springframework.util.function.ThrowingSupplier;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/InstanceSupplier.class */
public interface InstanceSupplier<T> extends ThrowingSupplier<T> {
    T get(RegisteredBean registeredBean) throws Exception;

    @Override // org.springframework.util.function.ThrowingSupplier
    default T getWithException() {
        throw new IllegalStateException("No RegisteredBean parameter provided");
    }

    @Nullable
    default Method getFactoryMethod() {
        return null;
    }

    default <V> InstanceSupplier<V> andThen(final ThrowingBiFunction<RegisteredBean, ? super T, ? extends V> after) {
        Assert.notNull(after, "'after' function must not be null");
        return new InstanceSupplier<V>() { // from class: org.springframework.beans.factory.support.InstanceSupplier.1
            @Override // org.springframework.beans.factory.support.InstanceSupplier
            public V get(RegisteredBean registeredBean) throws Exception {
                return (V) after.applyWithException(registeredBean, InstanceSupplier.this.get(registeredBean));
            }

            @Override // org.springframework.beans.factory.support.InstanceSupplier
            public Method getFactoryMethod() {
                return InstanceSupplier.this.getFactoryMethod();
            }
        };
    }

    static <T> InstanceSupplier<T> using(ThrowingSupplier<T> supplier) {
        Assert.notNull(supplier, "Supplier must not be null");
        if (supplier instanceof InstanceSupplier) {
            InstanceSupplier<T> instanceSupplier = (InstanceSupplier) supplier;
            return instanceSupplier;
        }
        return registeredBean -> {
            return supplier.getWithException();
        };
    }

    static <T> InstanceSupplier<T> using(@Nullable final Method factoryMethod, final ThrowingSupplier<T> supplier) {
        Assert.notNull(supplier, "Supplier must not be null");
        if (supplier instanceof InstanceSupplier) {
            InstanceSupplier<T> instanceSupplier = (InstanceSupplier) supplier;
            if (instanceSupplier.getFactoryMethod() == factoryMethod) {
                return instanceSupplier;
            }
        }
        return new InstanceSupplier<T>() { // from class: org.springframework.beans.factory.support.InstanceSupplier.2
            @Override // org.springframework.beans.factory.support.InstanceSupplier
            public T get(RegisteredBean registeredBean) throws Exception {
                return (T) ThrowingSupplier.this.getWithException();
            }

            @Override // org.springframework.beans.factory.support.InstanceSupplier
            public Method getFactoryMethod() {
                return factoryMethod;
            }
        };
    }

    static <T> InstanceSupplier<T> of(InstanceSupplier<T> instanceSupplier) {
        Assert.notNull(instanceSupplier, "InstanceSupplier must not be null");
        return instanceSupplier;
    }
}
