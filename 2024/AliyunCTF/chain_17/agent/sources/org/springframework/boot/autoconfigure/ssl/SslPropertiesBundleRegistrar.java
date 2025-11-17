package org.springframework.boot.autoconfigure.ssl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.ssl.SslProperties;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundleRegistry;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ssl/SslPropertiesBundleRegistrar.class */
class SslPropertiesBundleRegistrar implements SslBundleRegistrar {
    private final SslProperties.Bundles properties;
    private final FileWatcher fileWatcher;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SslPropertiesBundleRegistrar(SslProperties properties, FileWatcher fileWatcher) {
        this.properties = properties.getBundle();
        this.fileWatcher = fileWatcher;
    }

    @Override // org.springframework.boot.autoconfigure.ssl.SslBundleRegistrar
    public void registerBundles(SslBundleRegistry registry) {
        registerBundles(registry, this.properties.getPem(), PropertiesSslBundle::get, this::watchedPemPaths);
        registerBundles(registry, this.properties.getJks(), PropertiesSslBundle::get, this::watchedJksPaths);
    }

    private <P extends SslBundleProperties> void registerBundles(SslBundleRegistry registry, Map<String, P> properties, Function<P, SslBundle> bundleFactory, Function<P, Set<Path>> watchedPaths) {
        properties.forEach((bundleName, bundleProperties) -> {
            Supplier<SslBundle> bundleSupplier = () -> {
                return (SslBundle) bundleFactory.apply(bundleProperties);
            };
            try {
                registry.registerBundle(bundleName, bundleSupplier.get());
                if (bundleProperties.isReloadOnUpdate()) {
                    Supplier<Set<Path>> pathsSupplier = () -> {
                        return (Set) watchedPaths.apply(bundleProperties);
                    };
                    watchForUpdates(registry, bundleName, pathsSupplier, bundleSupplier);
                }
            } catch (IllegalStateException ex) {
                throw new IllegalStateException("Unable to register SSL bundle '%s'".formatted(bundleName), ex);
            }
        });
    }

    private void watchForUpdates(SslBundleRegistry registry, String bundleName, Supplier<Set<Path>> pathsSupplier, Supplier<SslBundle> bundleSupplier) {
        try {
            this.fileWatcher.watch(pathsSupplier.get(), () -> {
                registry.updateBundle(bundleName, (SslBundle) bundleSupplier.get());
            });
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Unable to watch for reload on update", ex);
        }
    }

    private Set<Path> watchedJksPaths(JksSslBundleProperties properties) {
        List<BundleContentProperty> watched = new ArrayList<>();
        watched.add(new BundleContentProperty("keystore.location", properties.getKeystore().getLocation()));
        watched.add(new BundleContentProperty("truststore.location", properties.getTruststore().getLocation()));
        return watchedPaths(watched);
    }

    private Set<Path> watchedPemPaths(PemSslBundleProperties properties) {
        List<BundleContentProperty> watched = new ArrayList<>();
        watched.add(new BundleContentProperty("keystore.private-key", properties.getKeystore().getPrivateKey()));
        watched.add(new BundleContentProperty("keystore.certificate", properties.getKeystore().getCertificate()));
        watched.add(new BundleContentProperty("truststore.private-key", properties.getTruststore().getPrivateKey()));
        watched.add(new BundleContentProperty("truststore.certificate", properties.getTruststore().getCertificate()));
        return watchedPaths(watched);
    }

    private Set<Path> watchedPaths(List<BundleContentProperty> properties) {
        return (Set) properties.stream().filter((v0) -> {
            return v0.hasValue();
        }).map((v0) -> {
            return v0.toWatchPath();
        }).collect(Collectors.toSet());
    }
}
