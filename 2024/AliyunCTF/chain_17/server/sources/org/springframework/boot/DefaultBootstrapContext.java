package org.springframework.boot;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.boot.BootstrapRegistry;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/DefaultBootstrapContext.class */
public class DefaultBootstrapContext implements ConfigurableBootstrapContext {
    private final Map<Class<?>, BootstrapRegistry.InstanceSupplier<?>> instanceSuppliers = new HashMap();
    private final Map<Class<?>, Object> instances = new HashMap();
    private final ApplicationEventMulticaster events = new SimpleApplicationEventMulticaster();

    @Override // org.springframework.boot.BootstrapRegistry
    public <T> void register(Class<T> type, BootstrapRegistry.InstanceSupplier<T> instanceSupplier) {
        register(type, instanceSupplier, true);
    }

    @Override // org.springframework.boot.BootstrapRegistry
    public <T> void registerIfAbsent(Class<T> type, BootstrapRegistry.InstanceSupplier<T> instanceSupplier) {
        register(type, instanceSupplier, false);
    }

    private <T> void register(Class<T> type, BootstrapRegistry.InstanceSupplier<T> instanceSupplier, boolean replaceExisting) {
        Assert.notNull(type, "Type must not be null");
        Assert.notNull(instanceSupplier, "InstanceSupplier must not be null");
        synchronized (this.instanceSuppliers) {
            boolean alreadyRegistered = this.instanceSuppliers.containsKey(type);
            if (replaceExisting || !alreadyRegistered) {
                Assert.state(!this.instances.containsKey(type), (Supplier<String>) () -> {
                    return type.getName() + " has already been created";
                });
                this.instanceSuppliers.put(type, instanceSupplier);
            }
        }
    }

    @Override // org.springframework.boot.BootstrapRegistry, org.springframework.boot.BootstrapContext
    public <T> boolean isRegistered(Class<T> type) {
        boolean containsKey;
        synchronized (this.instanceSuppliers) {
            containsKey = this.instanceSuppliers.containsKey(type);
        }
        return containsKey;
    }

    @Override // org.springframework.boot.BootstrapRegistry
    public <T> BootstrapRegistry.InstanceSupplier<T> getRegisteredInstanceSupplier(Class<T> type) {
        BootstrapRegistry.InstanceSupplier<T> instanceSupplier;
        synchronized (this.instanceSuppliers) {
            instanceSupplier = (BootstrapRegistry.InstanceSupplier) this.instanceSuppliers.get(type);
        }
        return instanceSupplier;
    }

    @Override // org.springframework.boot.BootstrapRegistry
    public void addCloseListener(ApplicationListener<BootstrapContextClosedEvent> listener) {
        this.events.addApplicationListener(listener);
    }

    @Override // org.springframework.boot.BootstrapContext
    public <T> T get(Class<T> cls) throws IllegalStateException {
        return (T) getOrElseThrow(cls, () -> {
            return new IllegalStateException(cls.getName() + " has not been registered");
        });
    }

    @Override // org.springframework.boot.BootstrapContext
    public <T> T getOrElse(Class<T> cls, T t) {
        return (T) getOrElseSupply(cls, () -> {
            return t;
        });
    }

    @Override // org.springframework.boot.BootstrapContext
    public <T> T getOrElseSupply(Class<T> cls, Supplier<T> supplier) {
        T t;
        synchronized (this.instanceSuppliers) {
            BootstrapRegistry.InstanceSupplier<?> instanceSupplier = this.instanceSuppliers.get(cls);
            t = instanceSupplier != null ? (T) getInstance(cls, instanceSupplier) : supplier.get();
        }
        return t;
    }

    @Override // org.springframework.boot.BootstrapContext
    public <T, X extends Throwable> T getOrElseThrow(Class<T> cls, Supplier<? extends X> supplier) throws Throwable {
        T t;
        synchronized (this.instanceSuppliers) {
            BootstrapRegistry.InstanceSupplier<?> instanceSupplier = this.instanceSuppliers.get(cls);
            if (instanceSupplier == null) {
                throw supplier.get();
            }
            t = (T) getInstance(cls, instanceSupplier);
        }
        return t;
    }

    private <T> T getInstance(Class<T> cls, BootstrapRegistry.InstanceSupplier<?> instanceSupplier) {
        Object obj = this.instances.get(cls);
        if (obj == null) {
            obj = instanceSupplier.get(this);
            if (instanceSupplier.getScope() == BootstrapRegistry.Scope.SINGLETON) {
                this.instances.put(cls, obj);
            }
        }
        return (T) obj;
    }

    public void close(ConfigurableApplicationContext applicationContext) {
        this.events.multicastEvent(new BootstrapContextClosedEvent(this, applicationContext));
    }
}
