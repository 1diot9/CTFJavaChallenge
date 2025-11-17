package org.springframework.boot.loader.launch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.Manifest;
import org.springframework.boot.loader.launch.Archive;

/* loaded from: agent.jar:org/springframework/boot/loader/launch/ExplodedArchive.class */
class ExplodedArchive implements Archive {
    private static final Object NO_MANIFEST = new Object();
    private static final Set<String> SKIPPED_NAMES = Set.of(".", "..");
    private static final Comparator<File> entryComparator = Comparator.comparing((v0) -> {
        return v0.getAbsolutePath();
    });
    private final File rootDirectory;
    private final String rootUriPath;
    private volatile Object manifest;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ExplodedArchive(File rootDirectory) {
        if (!rootDirectory.exists() || !rootDirectory.isDirectory()) {
            throw new IllegalArgumentException("Invalid source directory " + rootDirectory);
        }
        this.rootDirectory = rootDirectory;
        this.rootUriPath = this.rootDirectory.toURI().getPath();
    }

    @Override // org.springframework.boot.loader.launch.Archive
    public Manifest getManifest() throws IOException {
        Object manifest = this.manifest;
        if (manifest == null) {
            manifest = loadManifest();
            this.manifest = manifest;
        }
        if (manifest != NO_MANIFEST) {
            return (Manifest) manifest;
        }
        return null;
    }

    private Object loadManifest() throws IOException {
        File file = new File(this.rootDirectory, "META-INF/MANIFEST.MF");
        if (!file.exists()) {
            return NO_MANIFEST;
        }
        FileInputStream inputStream = new FileInputStream(file);
        try {
            Manifest manifest = new Manifest(inputStream);
            inputStream.close();
            return manifest;
        } catch (Throwable th) {
            try {
                inputStream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // org.springframework.boot.loader.launch.Archive
    public Set<URL> getClassPathUrls(Predicate<Archive.Entry> includeFilter, Predicate<Archive.Entry> directorySearchFilter) throws IOException {
        Set<URL> urls = new LinkedHashSet<>();
        LinkedList<File> files = new LinkedList<>(listFiles(this.rootDirectory));
        while (!files.isEmpty()) {
            File file = files.poll();
            if (!SKIPPED_NAMES.contains(file.getName())) {
                String entryName = file.toURI().getPath().substring(this.rootUriPath.length());
                Archive.Entry entry = new FileArchiveEntry(entryName, file);
                if (entry.isDirectory() && directorySearchFilter.test(entry)) {
                    files.addAll(0, listFiles(file));
                }
                if (includeFilter.test(entry)) {
                    urls.add(file.toURI().toURL());
                }
            }
        }
        return urls;
    }

    private List<File> listFiles(File file) {
        File[] files = file.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        Arrays.sort(files, entryComparator);
        return Arrays.asList(files);
    }

    @Override // org.springframework.boot.loader.launch.Archive
    public File getRootDirectory() {
        return this.rootDirectory;
    }

    public String toString() {
        return this.rootDirectory.toString();
    }

    /* loaded from: agent.jar:org/springframework/boot/loader/launch/ExplodedArchive$FileArchiveEntry.class */
    private static final class FileArchiveEntry extends Record implements Archive.Entry {
        private final String name;
        private final File file;

        private FileArchiveEntry(String name, File file) {
            this.name = name;
            this.file = file;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, FileArchiveEntry.class), FileArchiveEntry.class, "name;file", "FIELD:Lorg/springframework/boot/loader/launch/ExplodedArchive$FileArchiveEntry;->name:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/loader/launch/ExplodedArchive$FileArchiveEntry;->file:Ljava/io/File;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, FileArchiveEntry.class), FileArchiveEntry.class, "name;file", "FIELD:Lorg/springframework/boot/loader/launch/ExplodedArchive$FileArchiveEntry;->name:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/loader/launch/ExplodedArchive$FileArchiveEntry;->file:Ljava/io/File;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, FileArchiveEntry.class, Object.class), FileArchiveEntry.class, "name;file", "FIELD:Lorg/springframework/boot/loader/launch/ExplodedArchive$FileArchiveEntry;->name:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/loader/launch/ExplodedArchive$FileArchiveEntry;->file:Ljava/io/File;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        @Override // org.springframework.boot.loader.launch.Archive.Entry
        public String name() {
            return this.name;
        }

        public File file() {
            return this.file;
        }

        @Override // org.springframework.boot.loader.launch.Archive.Entry
        public boolean isDirectory() {
            return this.file.isDirectory();
        }
    }
}
