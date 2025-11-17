package org.springframework.util.function;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/function/SingletonSupplier.class */
public class SingletonSupplier<T> implements Supplier<T> {

    @Nullable
    private final Supplier<? extends T> instanceSupplier;

    @Nullable
    private final Supplier<? extends T> defaultSupplier;

    @Nullable
    private volatile T singletonInstance;
    private final Lock writeLock;

    public SingletonSupplier(@Nullable T instance, Supplier<? extends T> defaultSupplier) {
        this.writeLock = new ReentrantLock();
        this.instanceSupplier = null;
        this.defaultSupplier = defaultSupplier;
        this.singletonInstance = instance;
    }

    public SingletonSupplier(@Nullable Supplier<? extends T> instanceSupplier, Supplier<? extends T> defaultSupplier) {
        this.writeLock = new ReentrantLock();
        this.instanceSupplier = instanceSupplier;
        this.defaultSupplier = defaultSupplier;
    }

    private SingletonSupplier(Supplier<? extends T> supplier) {
        this.writeLock = new ReentrantLock();
        this.instanceSupplier = supplier;
        this.defaultSupplier = null;
    }

    private SingletonSupplier(T singletonInstance) {
        this.writeLock = new ReentrantLock();
        this.instanceSupplier = null;
        this.defaultSupplier = null;
        this.singletonInstance = singletonInstance;
    }

    @Override // java.util.function.Supplier
    @Nullable
    public T get() {
        T instance = this.singletonInstance;
        if (instance == null) {
            this.writeLock.lock();
            try {
                instance = this.singletonInstance;
                if (instance == null) {
                    if (this.instanceSupplier != null) {
                        instance = this.instanceSupplier.get();
                    }
                    if (instance == null && this.defaultSupplier != null) {
                        instance = this.defaultSupplier.get();
                    }
                    this.singletonInstance = instance;
                }
            } finally {
                this.writeLock.unlock();
            }
        }
        return instance;
    }

    public T obtain() {
        T instance = get();
        Assert.state(instance != null, "No instance from Supplier");
        return instance;
    }

    public static <T> SingletonSupplier<T> of(T instance) {
        return new SingletonSupplier<>(instance);
    }

    @Nullable
    public static <T> SingletonSupplier<T> ofNullable(@Nullable T instance) {
        if (instance != null) {
            return new SingletonSupplier<>(instance);
        }
        return null;
    }

    public static <T> SingletonSupplier<T> of(Supplier<T> supplier) {
        return new SingletonSupplier<>((Supplier) supplier);
    }

    @Nullable
    public static <T> SingletonSupplier<T> ofNullable(@Nullable Supplier<T> supplier) {
        if (supplier != null) {
            return new SingletonSupplier<>((Supplier) supplier);
        }
        return null;
    }
}
