package org.springframework.boot.loader.net.protocol.jar;

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;

/* loaded from: server.jar:org/springframework/boot/loader/net/protocol/jar/UrlJarEntry.class */
final class UrlJarEntry extends JarEntry {
    private final UrlJarManifest manifest;

    private UrlJarEntry(JarEntry entry, UrlJarManifest manifest) {
        super(entry);
        this.manifest = manifest;
    }

    @Override // java.util.jar.JarEntry
    public Attributes getAttributes() throws IOException {
        return this.manifest.getEntryAttributes(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static UrlJarEntry of(ZipEntry entry, UrlJarManifest manifest) {
        if (entry != null) {
            return new UrlJarEntry((JarEntry) entry, manifest);
        }
        return null;
    }
}
