package org.springframework.boot.loader.jar;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.Runtime;
import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;
import java.nio.file.attribute.FileTime;
import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import org.jooq.types.UInteger;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.boot.loader.log.DebugLogger;
import org.springframework.boot.loader.ref.Cleaner;
import org.springframework.boot.loader.zip.CloseableDataBlock;
import org.springframework.boot.loader.zip.ZipContent;

/* loaded from: server.jar:org/springframework/boot/loader/jar/NestedJarFile.class */
public class NestedJarFile extends JarFile {
    private static final int DECIMAL = 10;
    private static final String META_INF = "META-INF/";
    static final String META_INF_VERSIONS = "META-INF/versions/";
    static final int BASE_VERSION = baseVersion().feature();
    private static final DebugLogger debug = DebugLogger.get(NestedJarFile.class);
    private final Cleaner cleaner;
    private final NestedJarFileResources resources;
    private final Cleaner.Cleanable cleanup;
    private final String name;
    private final int version;
    private volatile NestedJarEntry lastEntry;
    private volatile boolean closed;
    private volatile ManifestInfo manifestInfo;
    private volatile MetaInfVersionsInfo metaInfVersionsInfo;

    NestedJarFile(File file) throws IOException {
        this(file, null, null, false, org.springframework.boot.loader.ref.Cleaner.instance);
    }

    public NestedJarFile(File file, String nestedEntryName) throws IOException {
        this(file, nestedEntryName, null, true, org.springframework.boot.loader.ref.Cleaner.instance);
    }

    public NestedJarFile(File file, String nestedEntryName, Runtime.Version version) throws IOException {
        this(file, nestedEntryName, version, true, org.springframework.boot.loader.ref.Cleaner.instance);
    }

    NestedJarFile(File file, String nestedEntryName, Runtime.Version version, boolean onlyNestedJars, org.springframework.boot.loader.ref.Cleaner cleaner) throws IOException {
        super(file);
        if (onlyNestedJars && (nestedEntryName == null || nestedEntryName.isEmpty())) {
            throw new IllegalArgumentException("nestedEntryName must not be empty");
        }
        debug.log("Created nested jar file (%s, %s, %s)", file, nestedEntryName, version);
        this.cleaner = cleaner;
        this.resources = new NestedJarFileResources(file, nestedEntryName);
        this.cleanup = cleaner.register(this, this.resources);
        this.name = file.getPath() + (nestedEntryName != null ? "!/" + nestedEntryName : "");
        this.version = version != null ? version.feature() : baseVersion().feature();
    }

    public InputStream getRawZipDataInputStream() throws IOException {
        RawZipDataInputStream inputStream = new RawZipDataInputStream(this.resources.zipContent().openRawZipData().asInputStream());
        this.resources.addInputStream(inputStream);
        return inputStream;
    }

    @Override // java.util.jar.JarFile
    public Manifest getManifest() throws IOException {
        try {
            return ((ManifestInfo) this.resources.zipContentForManifest().getInfo(ManifestInfo.class, this::getManifestInfo)).getManifest();
        } catch (UncheckedIOException ex) {
            throw ex.getCause();
        }
    }

    @Override // java.util.jar.JarFile, java.util.zip.ZipFile
    public Enumeration<JarEntry> entries() {
        JarEntriesEnumeration jarEntriesEnumeration;
        synchronized (this) {
            ensureOpen();
            jarEntriesEnumeration = new JarEntriesEnumeration(this.resources.zipContent());
        }
        return jarEntriesEnumeration;
    }

    @Override // java.util.jar.JarFile, java.util.zip.ZipFile
    public Stream<JarEntry> stream() {
        Stream map;
        synchronized (this) {
            ensureOpen();
            map = streamContentEntries().map(x$0 -> {
                return new NestedJarEntry(this, x$0);
            });
        }
        return map;
    }

    public Stream<JarEntry> versionedStream() {
        Stream<JarEntry> filter;
        synchronized (this) {
            ensureOpen();
            filter = streamContentEntries().map(this::getBaseName).filter((v0) -> {
                return Objects.nonNull(v0);
            }).distinct().map(this::getJarEntry).filter((v0) -> {
                return Objects.nonNull(v0);
            });
        }
        return filter;
    }

    private Stream<ZipContent.Entry> streamContentEntries() {
        ZipContentEntriesSpliterator spliterator = new ZipContentEntriesSpliterator(this.resources.zipContent());
        return StreamSupport.stream(spliterator, false);
    }

    private String getBaseName(ZipContent.Entry contentEntry) {
        String name = contentEntry.getName();
        if (!name.startsWith(META_INF_VERSIONS)) {
            return name;
        }
        int versionNumberStartIndex = META_INF_VERSIONS.length();
        int versionNumberEndIndex = versionNumberStartIndex != -1 ? name.indexOf(47, versionNumberStartIndex) : -1;
        if (versionNumberEndIndex == -1 || versionNumberEndIndex == name.length() - 1) {
            return null;
        }
        try {
            int versionNumber = Integer.parseInt(name, versionNumberStartIndex, versionNumberEndIndex, 10);
            if (versionNumber > this.version) {
                return null;
            }
            return name.substring(versionNumberEndIndex + 1);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override // java.util.jar.JarFile
    public JarEntry getJarEntry(String name) {
        return getNestedJarEntry(name);
    }

    @Override // java.util.jar.JarFile, java.util.zip.ZipFile
    public JarEntry getEntry(String name) {
        return getNestedJarEntry(name);
    }

    public boolean hasEntry(String name) {
        boolean hasEntry;
        NestedJarEntry lastEntry = this.lastEntry;
        if (lastEntry != null && name.equals(lastEntry.getName())) {
            return true;
        }
        ZipContent.Entry entry = getVersionedContentEntry(name);
        if (entry != null) {
            return true;
        }
        synchronized (this) {
            ensureOpen();
            hasEntry = this.resources.zipContent().hasEntry(null, name);
        }
        return hasEntry;
    }

    private NestedJarEntry getNestedJarEntry(String name) {
        Objects.requireNonNull(name, "name");
        NestedJarEntry lastEntry = this.lastEntry;
        if (lastEntry != null && name.equals(lastEntry.getName())) {
            return lastEntry;
        }
        ZipContent.Entry entry = getVersionedContentEntry(name);
        ZipContent.Entry entry2 = entry != null ? entry : getContentEntry(null, name);
        if (entry2 == null) {
            return null;
        }
        NestedJarEntry nestedJarEntry = new NestedJarEntry(entry2, name);
        this.lastEntry = nestedJarEntry;
        return nestedJarEntry;
    }

    private ZipContent.Entry getVersionedContentEntry(String name) {
        ZipContent.Entry entry;
        if (BASE_VERSION >= this.version || name.startsWith(META_INF) || !getManifestInfo().isMultiRelease()) {
            return null;
        }
        MetaInfVersionsInfo metaInfVersionsInfo = getMetaInfVersionsInfo();
        int[] versions = metaInfVersionsInfo.versions();
        String[] directories = metaInfVersionsInfo.directories();
        for (int i = versions.length - 1; i >= 0; i--) {
            if (versions[i] <= this.version && (entry = getContentEntry(directories[i], name)) != null) {
                return entry;
            }
        }
        return null;
    }

    private ZipContent.Entry getContentEntry(String namePrefix, String name) {
        ZipContent.Entry entry;
        synchronized (this) {
            ensureOpen();
            entry = this.resources.zipContent().getEntry(namePrefix, name);
        }
        return entry;
    }

    private ManifestInfo getManifestInfo() {
        ManifestInfo manifestInfo;
        ManifestInfo manifestInfo2 = this.manifestInfo;
        if (manifestInfo2 != null) {
            return manifestInfo2;
        }
        synchronized (this) {
            ensureOpen();
            manifestInfo = (ManifestInfo) this.resources.zipContent().getInfo(ManifestInfo.class, this::getManifestInfo);
        }
        this.manifestInfo = manifestInfo;
        return manifestInfo;
    }

    private ManifestInfo getManifestInfo(ZipContent zipContent) {
        ZipContent.Entry contentEntry = zipContent.getEntry("META-INF/MANIFEST.MF");
        if (contentEntry == null) {
            return ManifestInfo.NONE;
        }
        try {
            InputStream inputStream = getInputStream(contentEntry);
            try {
                Manifest manifest = new Manifest(inputStream);
                ManifestInfo manifestInfo = new ManifestInfo(manifest);
                if (inputStream != null) {
                    inputStream.close();
                }
                return manifestInfo;
            } finally {
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private MetaInfVersionsInfo getMetaInfVersionsInfo() {
        MetaInfVersionsInfo metaInfVersionsInfo;
        MetaInfVersionsInfo metaInfVersionsInfo2 = this.metaInfVersionsInfo;
        if (metaInfVersionsInfo2 != null) {
            return metaInfVersionsInfo2;
        }
        synchronized (this) {
            ensureOpen();
            metaInfVersionsInfo = (MetaInfVersionsInfo) this.resources.zipContent().getInfo(MetaInfVersionsInfo.class, MetaInfVersionsInfo::get);
        }
        this.metaInfVersionsInfo = metaInfVersionsInfo;
        return metaInfVersionsInfo;
    }

    @Override // java.util.jar.JarFile, java.util.zip.ZipFile
    public InputStream getInputStream(ZipEntry entry) throws IOException {
        Objects.requireNonNull(entry, BeanDefinitionParserDelegate.ENTRY_ELEMENT);
        if (entry instanceof NestedJarEntry) {
            NestedJarEntry nestedJarEntry = (NestedJarEntry) entry;
            if (nestedJarEntry.isOwnedBy(this)) {
                return getInputStream(nestedJarEntry.contentEntry());
            }
        }
        return getInputStream(getNestedJarEntry(entry.getName()).contentEntry());
    }

    private InputStream getInputStream(ZipContent.Entry contentEntry) throws IOException {
        InputStream inputStream;
        int compression = contentEntry.getCompressionMethod();
        if (compression != 0 && compression != 8) {
            throw new ZipException("invalid compression method");
        }
        synchronized (this) {
            ensureOpen();
            InputStream inputStream2 = new JarEntryInputStream(contentEntry);
            if (compression == 8) {
                try {
                    inputStream2 = new JarEntryInflaterInputStream(this, (JarEntryInputStream) inputStream2, this.resources);
                } catch (RuntimeException ex) {
                    inputStream2.close();
                    throw ex;
                }
            }
            this.resources.addInputStream(inputStream2);
            inputStream = inputStream2;
        }
        return inputStream;
    }

    @Override // java.util.zip.ZipFile
    public String getComment() {
        String comment;
        synchronized (this) {
            ensureOpen();
            comment = this.resources.zipContent().getComment();
        }
        return comment;
    }

    @Override // java.util.zip.ZipFile
    public int size() {
        int size;
        synchronized (this) {
            ensureOpen();
            size = this.resources.zipContent().size();
        }
        return size;
    }

    @Override // java.util.zip.ZipFile, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        super.close();
        if (this.closed) {
            return;
        }
        this.closed = true;
        synchronized (this) {
            try {
                this.cleanup.clean();
            } catch (UncheckedIOException ex) {
                throw ex.getCause();
            }
        }
    }

    @Override // java.util.zip.ZipFile
    public String getName() {
        return this.name;
    }

    private void ensureOpen() {
        if (this.closed) {
            throw new IllegalStateException("Zip file closed");
        }
        if (this.resources.zipContent() == null) {
            throw new IllegalStateException("The object is not initialized.");
        }
    }

    public void clearCache() {
        synchronized (this) {
            this.lastEntry = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:org/springframework/boot/loader/jar/NestedJarFile$NestedJarEntry.class */
    public class NestedJarEntry extends JarEntry {
        private static final IllegalStateException CANNOT_BE_MODIFIED_EXCEPTION = new IllegalStateException("Neste jar entries cannot be modified");
        private final ZipContent.Entry contentEntry;
        private final String name;
        private volatile boolean populated;

        NestedJarEntry(final NestedJarFile this$0, ZipContent.Entry contentEntry) {
            this(contentEntry, contentEntry.getName());
        }

        NestedJarEntry(ZipContent.Entry contentEntry, String name) {
            super(contentEntry.getName());
            this.contentEntry = contentEntry;
            this.name = name;
        }

        @Override // java.util.zip.ZipEntry
        public long getTime() {
            populate();
            return super.getTime();
        }

        public LocalDateTime getTimeLocal() {
            populate();
            return super.getTimeLocal();
        }

        @Override // java.util.zip.ZipEntry
        public void setTime(long time) {
            throw CANNOT_BE_MODIFIED_EXCEPTION;
        }

        public void setTimeLocal(LocalDateTime time) {
            throw CANNOT_BE_MODIFIED_EXCEPTION;
        }

        @Override // java.util.zip.ZipEntry
        public FileTime getLastModifiedTime() {
            populate();
            return super.getLastModifiedTime();
        }

        @Override // java.util.zip.ZipEntry
        public ZipEntry setLastModifiedTime(FileTime time) {
            throw CANNOT_BE_MODIFIED_EXCEPTION;
        }

        @Override // java.util.zip.ZipEntry
        public FileTime getLastAccessTime() {
            populate();
            return super.getLastAccessTime();
        }

        @Override // java.util.zip.ZipEntry
        public ZipEntry setLastAccessTime(FileTime time) {
            throw CANNOT_BE_MODIFIED_EXCEPTION;
        }

        @Override // java.util.zip.ZipEntry
        public FileTime getCreationTime() {
            populate();
            return super.getCreationTime();
        }

        @Override // java.util.zip.ZipEntry
        public ZipEntry setCreationTime(FileTime time) {
            throw CANNOT_BE_MODIFIED_EXCEPTION;
        }

        @Override // java.util.zip.ZipEntry
        public long getSize() {
            return this.contentEntry.getUncompressedSize() & UInteger.MAX_VALUE;
        }

        @Override // java.util.zip.ZipEntry
        public void setSize(long size) {
            throw CANNOT_BE_MODIFIED_EXCEPTION;
        }

        @Override // java.util.zip.ZipEntry
        public long getCompressedSize() {
            populate();
            return super.getCompressedSize();
        }

        @Override // java.util.zip.ZipEntry
        public void setCompressedSize(long csize) {
            throw CANNOT_BE_MODIFIED_EXCEPTION;
        }

        @Override // java.util.zip.ZipEntry
        public long getCrc() {
            populate();
            return super.getCrc();
        }

        @Override // java.util.zip.ZipEntry
        public void setCrc(long crc) {
            throw CANNOT_BE_MODIFIED_EXCEPTION;
        }

        @Override // java.util.zip.ZipEntry
        public int getMethod() {
            populate();
            return super.getMethod();
        }

        @Override // java.util.zip.ZipEntry
        public void setMethod(int method) {
            throw CANNOT_BE_MODIFIED_EXCEPTION;
        }

        @Override // java.util.zip.ZipEntry
        public byte[] getExtra() {
            populate();
            return super.getExtra();
        }

        @Override // java.util.zip.ZipEntry
        public void setExtra(byte[] extra) {
            throw CANNOT_BE_MODIFIED_EXCEPTION;
        }

        @Override // java.util.zip.ZipEntry
        public String getComment() {
            populate();
            return super.getComment();
        }

        @Override // java.util.zip.ZipEntry
        public void setComment(String comment) {
            throw CANNOT_BE_MODIFIED_EXCEPTION;
        }

        boolean isOwnedBy(NestedJarFile nestedJarFile) {
            return NestedJarFile.this == nestedJarFile;
        }

        @Override // java.util.jar.JarEntry
        public String getRealName() {
            return super.getName();
        }

        @Override // java.util.zip.ZipEntry
        public String getName() {
            return this.name;
        }

        @Override // java.util.jar.JarEntry
        public Attributes getAttributes() throws IOException {
            Manifest manifest = NestedJarFile.this.getManifest();
            if (manifest != null) {
                return manifest.getAttributes(getName());
            }
            return null;
        }

        @Override // java.util.jar.JarEntry
        public Certificate[] getCertificates() {
            return getSecurityInfo().getCertificates(contentEntry());
        }

        @Override // java.util.jar.JarEntry
        public CodeSigner[] getCodeSigners() {
            return getSecurityInfo().getCodeSigners(contentEntry());
        }

        private SecurityInfo getSecurityInfo() {
            return (SecurityInfo) NestedJarFile.this.resources.zipContent().getInfo(SecurityInfo.class, SecurityInfo::get);
        }

        ZipContent.Entry contentEntry() {
            return this.contentEntry;
        }

        private void populate() {
            boolean populated = this.populated;
            if (!populated) {
                ZipEntry entry = this.contentEntry.as((Function<String, ZipEntry>) ZipEntry::new);
                super.setMethod(entry.getMethod());
                super.setTime(entry.getTime());
                super.setCrc(entry.getCrc());
                super.setCompressedSize(entry.getCompressedSize());
                super.setSize(entry.getSize());
                super.setExtra(entry.getExtra());
                super.setComment(entry.getComment());
                this.populated = true;
            }
        }
    }

    /* loaded from: server.jar:org/springframework/boot/loader/jar/NestedJarFile$JarEntriesEnumeration.class */
    private class JarEntriesEnumeration implements Enumeration<JarEntry> {
        private final ZipContent zipContent;
        private int cursor;

        JarEntriesEnumeration(ZipContent zipContent) {
            this.zipContent = zipContent;
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return this.cursor < this.zipContent.size();
        }

        @Override // java.util.Enumeration
        /* renamed from: nextElement, reason: merged with bridge method [inline-methods] */
        public JarEntry nextElement2() {
            NestedJarEntry nestedJarEntry;
            if (!hasMoreElements()) {
                throw new NoSuchElementException();
            }
            synchronized (NestedJarFile.this) {
                NestedJarFile.this.ensureOpen();
                NestedJarFile nestedJarFile = NestedJarFile.this;
                ZipContent zipContent = this.zipContent;
                int i = this.cursor;
                this.cursor = i + 1;
                nestedJarEntry = new NestedJarEntry(nestedJarFile, zipContent.getEntry(i));
            }
            return nestedJarEntry;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:org/springframework/boot/loader/jar/NestedJarFile$ZipContentEntriesSpliterator.class */
    public class ZipContentEntriesSpliterator extends Spliterators.AbstractSpliterator<ZipContent.Entry> {
        private static final int ADDITIONAL_CHARACTERISTICS = 1297;
        private final ZipContent zipContent;
        private int cursor;

        ZipContentEntriesSpliterator(ZipContent zipContent) {
            super(zipContent.size(), ADDITIONAL_CHARACTERISTICS);
            this.zipContent = zipContent;
        }

        @Override // java.util.Spliterator
        public boolean tryAdvance(Consumer<? super ZipContent.Entry> action) {
            if (this.cursor < this.zipContent.size()) {
                synchronized (NestedJarFile.this) {
                    NestedJarFile.this.ensureOpen();
                    ZipContent zipContent = this.zipContent;
                    int i = this.cursor;
                    this.cursor = i + 1;
                    action.accept(zipContent.getEntry(i));
                }
                return true;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:org/springframework/boot/loader/jar/NestedJarFile$JarEntryInputStream.class */
    public class JarEntryInputStream extends InputStream {
        private final int uncompressedSize;
        private final CloseableDataBlock content;
        private long pos;
        private long remaining;
        private volatile boolean closed;

        JarEntryInputStream(ZipContent.Entry entry) throws IOException {
            this.uncompressedSize = entry.getUncompressedSize();
            this.content = entry.openContent();
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            byte[] b = new byte[1];
            if (read(b, 0, 1) == 1) {
                return b[0] & 255;
            }
            return -1;
        }

        @Override // java.io.InputStream
        public int read(byte[] b, int off, int len) throws IOException {
            int count;
            synchronized (NestedJarFile.this) {
                ensureOpen();
                ByteBuffer dst = ByteBuffer.wrap(b, off, len);
                count = this.content.read(dst, this.pos);
                if (count > 0) {
                    this.pos += count;
                    this.remaining -= count;
                }
            }
            if (this.remaining == 0) {
                close();
            }
            return count;
        }

        @Override // java.io.InputStream
        public long skip(long n) throws IOException {
            long result;
            synchronized (NestedJarFile.this) {
                result = n > 0 ? maxForwardSkip(n) : maxBackwardSkip(n);
                this.pos += result;
                this.remaining -= result;
            }
            if (this.remaining == 0) {
                close();
            }
            return result;
        }

        private long maxForwardSkip(long n) {
            boolean willCauseOverflow = this.pos + n < 0;
            return (willCauseOverflow || n > this.remaining) ? this.remaining : n;
        }

        private long maxBackwardSkip(long n) {
            return Math.max(-this.pos, n);
        }

        @Override // java.io.InputStream
        public int available() {
            if (this.remaining < 2147483647L) {
                return (int) this.remaining;
            }
            return Integer.MAX_VALUE;
        }

        private void ensureOpen() throws ZipException {
            if (NestedJarFile.this.closed || this.closed) {
                throw new ZipException("ZipFile closed");
            }
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            if (this.closed) {
                return;
            }
            this.closed = true;
            this.content.close();
            NestedJarFile.this.resources.removeInputStream(this);
        }

        int getUncompressedSize() {
            return this.uncompressedSize;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:org/springframework/boot/loader/jar/NestedJarFile$JarEntryInflaterInputStream.class */
    public class JarEntryInflaterInputStream extends ZipInflaterInputStream {
        private final Cleaner.Cleanable cleanup;
        private volatile boolean closed;

        JarEntryInflaterInputStream(final NestedJarFile this$0, JarEntryInputStream inputStream, NestedJarFileResources resources) {
            this(inputStream, resources, resources.getOrCreateInflater());
        }

        private JarEntryInflaterInputStream(JarEntryInputStream inputStream, NestedJarFileResources resources, Inflater inflater) {
            super(inputStream, inflater, inputStream.getUncompressedSize());
            this.cleanup = NestedJarFile.this.cleaner.register(this, resources.createInflatorCleanupAction(inflater));
        }

        @Override // java.util.zip.InflaterInputStream, java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            if (this.closed) {
                return;
            }
            this.closed = true;
            super.close();
            NestedJarFile.this.resources.removeInputStream(this);
            this.cleanup.clean();
        }
    }

    /* loaded from: server.jar:org/springframework/boot/loader/jar/NestedJarFile$RawZipDataInputStream.class */
    private class RawZipDataInputStream extends FilterInputStream {
        private volatile boolean closed;

        RawZipDataInputStream(InputStream in) {
            super(in);
        }

        @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            if (this.closed) {
                return;
            }
            this.closed = true;
            super.close();
            NestedJarFile.this.resources.removeInputStream(this);
        }
    }
}
