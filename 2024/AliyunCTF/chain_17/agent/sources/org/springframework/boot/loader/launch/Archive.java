package org.springframework.boot.loader.launch;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.Manifest;

/* loaded from: agent.jar:org/springframework/boot/loader/launch/Archive.class */
public interface Archive extends AutoCloseable {
    public static final Predicate<Entry> ALL_ENTRIES = entry -> {
        return true;
    };

    /* loaded from: agent.jar:org/springframework/boot/loader/launch/Archive$Entry.class */
    public interface Entry {
        String name();

        boolean isDirectory();
    }

    Manifest getManifest() throws IOException;

    Set<URL> getClassPathUrls(Predicate<Entry> includeFilter, Predicate<Entry> directorySearchFilter) throws IOException;

    default Set<URL> getClassPathUrls(Predicate<Entry> includeFilter) throws IOException {
        return getClassPathUrls(includeFilter, ALL_ENTRIES);
    }

    default boolean isExploded() {
        return getRootDirectory() != null;
    }

    default File getRootDirectory() {
        return null;
    }

    @Override // java.lang.AutoCloseable
    default void close() throws Exception {
    }

    static Archive create(Class<?> target) throws Exception {
        return create(target.getProtectionDomain());
    }

    static Archive create(ProtectionDomain protectionDomain) throws Exception {
        CodeSource codeSource = protectionDomain.getCodeSource();
        URI location = codeSource != null ? codeSource.getLocation().toURI() : null;
        String path = location != null ? location.getSchemeSpecificPart() : null;
        if (path == null) {
            throw new IllegalStateException("Unable to determine code source archive");
        }
        return create(new File(path));
    }

    static Archive create(File target) throws Exception {
        if (target.exists()) {
            return target.isDirectory() ? new ExplodedArchive(target) : new JarFileArchive(target);
        }
        throw new IllegalStateException("Unable to determine code source archive from " + target);
    }
}
