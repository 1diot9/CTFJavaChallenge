package org.springframework.boot.loader.net.protocol.jar;

import java.io.IOException;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;

/* loaded from: server.jar:org/springframework/boot/loader/net/protocol/jar/UrlJarManifest.class */
class UrlJarManifest {
    private static final Object NONE = new Object();
    private final ManifestSupplier supplier;
    private volatile Object supplied;

    /* JADX INFO: Access modifiers changed from: package-private */
    @FunctionalInterface
    /* loaded from: server.jar:org/springframework/boot/loader/net/protocol/jar/UrlJarManifest$ManifestSupplier.class */
    public interface ManifestSupplier {
        Manifest getManifest() throws IOException;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public UrlJarManifest(ManifestSupplier supplier) {
        this.supplier = supplier;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Manifest get() throws IOException {
        Manifest manifest = supply();
        if (manifest == null) {
            return null;
        }
        Manifest copy = new Manifest();
        copy.getMainAttributes().putAll((Map) manifest.getMainAttributes().clone());
        manifest.getEntries().forEach((key, value) -> {
            copy.getEntries().put(key, cloneAttributes(value));
        });
        return copy;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Attributes getEntryAttributes(JarEntry entry) throws IOException {
        Manifest manifest = supply();
        if (manifest == null) {
            return null;
        }
        Attributes attributes = manifest.getEntries().get(entry.getName());
        return cloneAttributes(attributes);
    }

    private Attributes cloneAttributes(Attributes attributes) {
        if (attributes != null) {
            return (Attributes) attributes.clone();
        }
        return null;
    }

    private Manifest supply() throws IOException {
        Object supplied = this.supplied;
        if (supplied == null) {
            supplied = this.supplier.getManifest();
            this.supplied = supplied != null ? supplied : NONE;
        }
        if (supplied != NONE) {
            return (Manifest) supplied;
        }
        return null;
    }
}
