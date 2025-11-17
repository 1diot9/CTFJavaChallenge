package org.springframework.boot.loader.net.protocol.jar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Runtime;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.function.Consumer;
import java.util.jar.JarFile;
import org.springframework.boot.loader.net.protocol.nested.NestedLocation;
import org.springframework.boot.loader.net.util.UrlDecoder;

/* loaded from: server.jar:org/springframework/boot/loader/net/protocol/jar/UrlJarFileFactory.class */
class UrlJarFileFactory {
    /* JADX INFO: Access modifiers changed from: package-private */
    public JarFile createJarFile(URL jarFileUrl, Consumer<JarFile> closeAction) throws IOException {
        Runtime.Version version = getVersion(jarFileUrl);
        if (isLocalFileUrl(jarFileUrl)) {
            return createJarFileForLocalFile(jarFileUrl, version, closeAction);
        }
        if (isNestedUrl(jarFileUrl)) {
            return createJarFileForNested(jarFileUrl, version, closeAction);
        }
        return createJarFileForStream(jarFileUrl, version, closeAction);
    }

    private Runtime.Version getVersion(URL url) {
        return "base".equals(url.getRef()) ? JarFile.baseVersion() : JarFile.runtimeVersion();
    }

    private boolean isLocalFileUrl(URL url) {
        return url.getProtocol().equalsIgnoreCase("file") && isLocal(url.getHost());
    }

    private boolean isLocal(String host) {
        return host == null || host.isEmpty() || host.equals("~") || host.equalsIgnoreCase("localhost");
    }

    private JarFile createJarFileForLocalFile(URL url, Runtime.Version version, Consumer<JarFile> closeAction) throws IOException {
        String path = UrlDecoder.decode(url.getPath());
        return new UrlJarFile(new File(path), version, closeAction);
    }

    private JarFile createJarFileForNested(URL url, Runtime.Version version, Consumer<JarFile> closeAction) throws IOException {
        NestedLocation location = NestedLocation.fromUrl(url);
        return new UrlNestedJarFile(location.path().toFile(), location.nestedEntryName(), version, closeAction);
    }

    private JarFile createJarFileForStream(URL url, Runtime.Version version, Consumer<JarFile> closeAction) throws IOException {
        InputStream in = url.openStream();
        try {
            JarFile createJarFileForStream = createJarFileForStream(in, version, closeAction);
            if (in != null) {
                in.close();
            }
            return createJarFileForStream;
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    private JarFile createJarFileForStream(InputStream in, Runtime.Version version, Consumer<JarFile> closeAction) throws IOException {
        Path local = Files.createTempFile("jar_cache", null, new FileAttribute[0]);
        try {
            Files.copy(in, local, StandardCopyOption.REPLACE_EXISTING);
            JarFile jarFile = new UrlJarFile(local.toFile(), version, closeAction);
            local.toFile().deleteOnExit();
            return jarFile;
        } catch (Throwable ex) {
            deleteIfPossible(local, ex);
            throw ex;
        }
    }

    private void deleteIfPossible(Path local, Throwable cause) {
        try {
            Files.delete(local);
        } catch (IOException ex) {
            cause.addSuppressed(ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isNestedUrl(URL url) {
        return url.getProtocol().equalsIgnoreCase("nested");
    }
}
