package org.springframework.boot;

import java.util.function.Supplier;
import org.springframework.context.ApplicationListener;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/BootstrapRegistry.class */
public interface BootstrapRegistry {

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/BootstrapRegistry$Scope.class */
    public enum Scope {
        SINGLETON,
        PROTOTYPE
    }

    <T> void register(Class<T> type, InstanceSupplier<T> instanceSupplier);

    <T> void registerIfAbsent(Class<T> type, InstanceSupplier<T> instanceSupplier);

    <T> boolean isRegistered(Class<T> type);

    <T> InstanceSupplier<T> getRegisteredInstanceSupplier(Class<T> type);

    void addCloseListener(ApplicationListener<BootstrapContextClosedEvent> listener);

    @FunctionalInterface
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/BootstrapRegistry$InstanceSupplier.class */
    public interface InstanceSupplier<T> {
        T get(BootstrapContext context);

        default Scope getScope() {
            return Scope.SINGLETON;
        }

        default InstanceSupplier<T> withScope(final Scope scope) {
            Assert.notNull(scope, "Scope must not be null");
            return new InstanceSupplier<T>() { // from class: org.springframework.boot.BootstrapRegistry.InstanceSupplier.1
                @Override // org.springframework.boot.BootstrapRegistry.InstanceSupplier
                public T get(BootstrapContext bootstrapContext) {
                    return (T) this.get(bootstrapContext);
                }

                @Override // org.springframework.boot.BootstrapRegistry.InstanceSupplier
                public Scope getScope() {
                    return scope;
                }
            };
        }

        static <T> InstanceSupplier<T> of(T instance) {
            return registry -> {
                return instance;
            };
        }

        static <T> InstanceSupplier<T> from(Supplier<T> supplier) {
            return registry -> {
                if (supplier != null) {
                    return supplier.get();
                }
                return null;
            };
        }
    }
}
