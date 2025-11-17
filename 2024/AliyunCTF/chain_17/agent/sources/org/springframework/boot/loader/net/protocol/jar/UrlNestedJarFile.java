package org.springframework.boot.loader.net.protocol.jar;

import java.io.File;
import java.io.IOException;
import java.lang.Runtime;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.springframework.boot.loader.jar.NestedJarFile;

/* loaded from: agent.jar:org/springframework/boot/loader/net/protocol/jar/UrlNestedJarFile.class */
class UrlNestedJarFile extends NestedJarFile {
    private final UrlJarManifest manifest;
    private final Consumer<JarFile> closeAction;

    /* JADX INFO: Access modifiers changed from: package-private */
    public UrlNestedJarFile(File file, String nestedEntryName, Runtime.Version version, Consumer<JarFile> closeAction) throws IOException {
        super(file, nestedEntryName, version);
        this.manifest = new UrlJarManifest(() -> {
            return super.getManifest();
        });
        this.closeAction = closeAction;
    }

    @Override // org.springframework.boot.loader.jar.NestedJarFile, java.util.jar.JarFile
    public Manifest getManifest() throws IOException {
        return this.manifest.get();
    }

    @Override // org.springframework.boot.loader.jar.NestedJarFile, java.util.jar.JarFile, java.util.zip.ZipFile
    public JarEntry getEntry(String name) {
        return UrlJarEntry.of(super.getEntry(name), this.manifest);
    }

    @Override // org.springframework.boot.loader.jar.NestedJarFile, java.util.zip.ZipFile, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.closeAction != null) {
            this.closeAction.accept(this);
        }
        super.close();
    }
}
