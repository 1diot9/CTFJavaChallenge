package org.springframework.boot.loader.launch;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.springframework.boot.loader.launch.Archive;

/* loaded from: server.jar:org/springframework/boot/loader/launch/ExecutableArchiveLauncher.class */
public abstract class ExecutableArchiveLauncher extends Launcher {
    private static final String START_CLASS_ATTRIBUTE = "Start-Class";
    protected static final String BOOT_CLASSPATH_INDEX_ATTRIBUTE = "Spring-Boot-Classpath-Index";
    protected static final String DEFAULT_CLASSPATH_INDEX_FILE_NAME = "classpath.idx";
    private final Archive archive;
    private final ClassPathIndexFile classPathIndex;

    protected abstract boolean isIncludedOnClassPath(Archive.Entry entry);

    protected abstract String getEntryPathPrefix();

    public ExecutableArchiveLauncher() throws Exception {
        this(Archive.create((Class<?>) Launcher.class));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ExecutableArchiveLauncher(Archive archive) throws Exception {
        this.archive = archive;
        this.classPathIndex = getClassPathIndex(this.archive);
    }

    ClassPathIndexFile getClassPathIndex(Archive archive) throws IOException {
        if (!archive.isExploded()) {
            return null;
        }
        String location = getClassPathIndexFileLocation(archive);
        return ClassPathIndexFile.loadIfPossible(archive.getRootDirectory(), location);
    }

    private String getClassPathIndexFileLocation(Archive archive) throws IOException {
        Manifest manifest = archive.getManifest();
        Attributes attributes = manifest != null ? manifest.getMainAttributes() : null;
        String location = attributes != null ? attributes.getValue(BOOT_CLASSPATH_INDEX_ATTRIBUTE) : null;
        return location != null ? location : getEntryPathPrefix() + "classpath.idx";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.loader.launch.Launcher
    public ClassLoader createClassLoader(Collection<URL> urls) throws Exception {
        if (this.classPathIndex != null) {
            urls = new ArrayList(urls);
            urls.addAll(this.classPathIndex.getUrls());
        }
        return super.createClassLoader(urls);
    }

    @Override // org.springframework.boot.loader.launch.Launcher
    protected final Archive getArchive() {
        return this.archive;
    }

    @Override // org.springframework.boot.loader.launch.Launcher
    protected String getMainClass() throws Exception {
        Manifest manifest = this.archive.getManifest();
        String mainClass = manifest != null ? manifest.getMainAttributes().getValue(START_CLASS_ATTRIBUTE) : null;
        if (mainClass == null) {
            throw new IllegalStateException("No 'Start-Class' manifest entry specified in " + this);
        }
        return mainClass;
    }

    @Override // org.springframework.boot.loader.launch.Launcher
    protected Set<URL> getClassPathUrls() throws Exception {
        return this.archive.getClassPathUrls(this::isIncludedOnClassPathAndNotIndexed, this::isSearchedDirectory);
    }

    private boolean isIncludedOnClassPathAndNotIndexed(Archive.Entry entry) {
        if (isIncludedOnClassPath(entry)) {
            return this.classPathIndex == null || !this.classPathIndex.containsEntry(entry.name());
        }
        return false;
    }

    protected boolean isSearchedDirectory(Archive.Entry entry) {
        return (getEntryPathPrefix() == null || entry.name().startsWith(getEntryPathPrefix())) && !isIncludedOnClassPath(entry);
    }
}
