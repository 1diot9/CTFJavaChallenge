package org.springframework.boot.ssl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ssl/DefaultSslBundleRegistry.class */
public class DefaultSslBundleRegistry implements SslBundleRegistry, SslBundles {
    private static final Log logger = LogFactory.getLog((Class<?>) DefaultSslBundleRegistry.class);
    private final Map<String, RegisteredSslBundle> registeredBundles = new ConcurrentHashMap();

    public DefaultSslBundleRegistry() {
    }

    public DefaultSslBundleRegistry(String name, SslBundle bundle) {
        registerBundle(name, bundle);
    }

    @Override // org.springframework.boot.ssl.SslBundleRegistry
    public void registerBundle(String name, SslBundle bundle) {
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(bundle, "Bundle must not be null");
        RegisteredSslBundle previous = this.registeredBundles.putIfAbsent(name, new RegisteredSslBundle(name, bundle));
        Assert.state(previous == null, (Supplier<String>) () -> {
            return "Cannot replace existing SSL bundle '%s'".formatted(name);
        });
    }

    @Override // org.springframework.boot.ssl.SslBundleRegistry
    public void updateBundle(String name, SslBundle updatedBundle) {
        getRegistered(name).update(updatedBundle);
    }

    @Override // org.springframework.boot.ssl.SslBundles
    public SslBundle getBundle(String name) {
        return getRegistered(name).getBundle();
    }

    @Override // org.springframework.boot.ssl.SslBundles
    public void addBundleUpdateHandler(String name, Consumer<SslBundle> updateHandler) throws NoSuchSslBundleException {
        getRegistered(name).addUpdateHandler(updateHandler);
    }

    private RegisteredSslBundle getRegistered(String name) throws NoSuchSslBundleException {
        Assert.notNull(name, "Name must not be null");
        RegisteredSslBundle registered = this.registeredBundles.get(name);
        if (registered == null) {
            throw new NoSuchSslBundleException(name, "SSL bundle name '%s' cannot be found".formatted(name));
        }
        return registered;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ssl/DefaultSslBundleRegistry$RegisteredSslBundle.class */
    public static class RegisteredSslBundle {
        private final String name;
        private final List<Consumer<SslBundle>> updateHandlers = new CopyOnWriteArrayList();
        private volatile SslBundle bundle;

        RegisteredSslBundle(String name, SslBundle bundle) {
            this.name = name;
            this.bundle = bundle;
        }

        void update(SslBundle updatedBundle) {
            Assert.notNull(updatedBundle, "UpdatedBundle must not be null");
            this.bundle = updatedBundle;
            if (this.updateHandlers.isEmpty()) {
                DefaultSslBundleRegistry.logger.warn(LogMessage.format("SSL bundle '%s' has been updated but may be in use by a technology that doesn't support SSL reloading", this.name));
            }
            this.updateHandlers.forEach(handler -> {
                handler.accept(updatedBundle);
            });
        }

        SslBundle getBundle() {
            return this.bundle;
        }

        void addUpdateHandler(Consumer<SslBundle> updateHandler) {
            Assert.notNull(updateHandler, "UpdateHandler must not be null");
            this.updateHandlers.add(updateHandler);
        }
    }
}
