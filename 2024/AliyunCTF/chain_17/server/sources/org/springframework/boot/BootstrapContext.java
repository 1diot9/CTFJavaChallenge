package org.springframework.boot;

import java.util.function.Supplier;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/BootstrapContext.class */
public interface BootstrapContext {
    <T> T get(Class<T> type) throws IllegalStateException;

    <T> T getOrElse(Class<T> type, T other);

    <T> T getOrElseSupply(Class<T> type, Supplier<T> other);

    <T, X extends Throwable> T getOrElseThrow(Class<T> type, Supplier<? extends X> exceptionSupplier) throws Throwable;

    <T> boolean isRegistered(Class<T> type);
}
