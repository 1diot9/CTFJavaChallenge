package org.springframework.boot.loader.net.protocol.jar;

import java.io.File;
import java.io.IOException;
import java.lang.Runtime;
import java.util.function.Consumer;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import org.springframework.boot.loader.ref.Cleaner;

/* loaded from: agent.jar:org/springframework/boot/loader/net/protocol/jar/UrlJarFile.class */
class UrlJarFile extends JarFile {
    private final UrlJarManifest manifest;
    private final Consumer<JarFile> closeAction;

    /* JADX INFO: Access modifiers changed from: package-private */
    public UrlJarFile(File file, Runtime.Version version, Consumer<JarFile> closeAction) throws IOException {
        super(file, true, 1, version);
        Cleaner.instance.register(this, null);
        this.manifest = new UrlJarManifest(() -> {
            return super.getManifest();
        });
        this.closeAction = closeAction;
    }

    @Override // java.util.jar.JarFile, java.util.zip.ZipFile
    public ZipEntry getEntry(String name) {
        return UrlJarEntry.of(super.getEntry(name), this.manifest);
    }

    @Override // java.util.jar.JarFile
    public Manifest getManifest() throws IOException {
        return this.manifest.get();
    }

    @Override // java.util.zip.ZipFile, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.closeAction != null) {
            this.closeAction.accept(this);
        }
        super.close();
    }
}
