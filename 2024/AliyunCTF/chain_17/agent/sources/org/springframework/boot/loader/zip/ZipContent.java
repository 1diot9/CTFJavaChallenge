package org.springframework.boot.loader.zip;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.ref.SoftReference;
import java.lang.runtime.ObjectMethods;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import org.springframework.boot.loader.log.DebugLogger;
import org.springframework.boot.loader.zip.ZipEndOfCentralDirectoryRecord;

/* loaded from: agent.jar:org/springframework/boot/loader/zip/ZipContent.class */
public final class ZipContent implements Closeable {
    private static final String META_INF = "META-INF/";
    private static final byte[] SIGNATURE_SUFFIX = ".DSA".getBytes(StandardCharsets.UTF_8);
    private static final DebugLogger debug = DebugLogger.get(ZipContent.class);
    private static final Map<Source, ZipContent> cache = new ConcurrentHashMap();
    private final Source source;
    private final Kind kind;
    private final FileChannelDataBlock data;
    private final long centralDirectoryPos;
    private final long commentPos;
    private final long commentLength;
    private final int[] lookupIndexes;
    private final int[] nameHashLookups;
    private final int[] relativeCentralDirectoryOffsetLookups;
    private final NameOffsetLookups nameOffsetLookups;
    private final boolean hasJarSignatureFile;
    private SoftReference<CloseableDataBlock> virtualData;
    private SoftReference<Map<Class<?>, Object>> info;

    /* loaded from: agent.jar:org/springframework/boot/loader/zip/ZipContent$Kind.class */
    public enum Kind {
        ZIP,
        NESTED_ZIP,
        NESTED_DIRECTORY
    }

    private ZipContent(Source source, Kind kind, FileChannelDataBlock data, long centralDirectoryPos, long commentPos, long commentLength, int[] lookupIndexes, int[] nameHashLookups, int[] relativeCentralDirectoryOffsetLookups, NameOffsetLookups nameOffsetLookups, boolean hasJarSignatureFile) {
        this.source = source;
        this.kind = kind;
        this.data = data;
        this.centralDirectoryPos = centralDirectoryPos;
        this.commentPos = commentPos;
        this.commentLength = commentLength;
        this.lookupIndexes = lookupIndexes;
        this.nameHashLookups = nameHashLookups;
        this.relativeCentralDirectoryOffsetLookups = relativeCentralDirectoryOffsetLookups;
        this.nameOffsetLookups = nameOffsetLookups;
        this.hasJarSignatureFile = hasJarSignatureFile;
    }

    public Kind getKind() {
        return this.kind;
    }

    public CloseableDataBlock openRawZipData() throws IOException {
        this.data.open();
        return !this.nameOffsetLookups.hasAnyEnabled() ? this.data : getVirtualData();
    }

    private CloseableDataBlock getVirtualData() throws IOException {
        CloseableDataBlock virtualData = this.virtualData != null ? this.virtualData.get() : null;
        if (virtualData != null) {
            return virtualData;
        }
        CloseableDataBlock virtualData2 = createVirtualData();
        this.virtualData = new SoftReference<>(virtualData2);
        return virtualData2;
    }

    private CloseableDataBlock createVirtualData() throws IOException {
        int size = size();
        NameOffsetLookups nameOffsetLookups = this.nameOffsetLookups.emptyCopy();
        ZipCentralDirectoryFileHeaderRecord[] centralRecords = new ZipCentralDirectoryFileHeaderRecord[size];
        long[] centralRecordPositions = new long[size];
        for (int i = 0; i < size; i++) {
            int lookupIndex = this.lookupIndexes[i];
            long pos = getCentralDirectoryFileHeaderRecordPos(lookupIndex);
            nameOffsetLookups.enable(i, this.nameOffsetLookups.isEnabled(lookupIndex));
            centralRecords[i] = ZipCentralDirectoryFileHeaderRecord.load(this.data, pos);
            centralRecordPositions[i] = pos;
        }
        return new VirtualZipDataBlock(this.data, nameOffsetLookups, centralRecords, centralRecordPositions);
    }

    public int size() {
        return this.lookupIndexes.length;
    }

    public String getComment() {
        try {
            return ZipString.readString(this.data, this.commentPos, this.commentLength);
        } catch (UncheckedIOException ex) {
            if (ex.getCause() instanceof ClosedChannelException) {
                throw new IllegalStateException("Zip content closed", ex);
            }
            throw ex;
        }
    }

    public Entry getEntry(CharSequence name) {
        return getEntry(null, name);
    }

    public Entry getEntry(CharSequence namePrefix, CharSequence name) {
        int nameHash = nameHash(namePrefix, name);
        int size = size();
        for (int lookupIndex = getFirstLookupIndex(nameHash); lookupIndex >= 0 && lookupIndex < size && this.nameHashLookups[lookupIndex] == nameHash; lookupIndex++) {
            long pos = getCentralDirectoryFileHeaderRecordPos(lookupIndex);
            ZipCentralDirectoryFileHeaderRecord centralRecord = loadZipCentralDirectoryFileHeaderRecord(pos);
            if (hasName(lookupIndex, centralRecord, pos, namePrefix, name)) {
                return new Entry(lookupIndex, centralRecord);
            }
        }
        return null;
    }

    public boolean hasEntry(CharSequence namePrefix, CharSequence name) {
        int nameHash = nameHash(namePrefix, name);
        int size = size();
        for (int lookupIndex = getFirstLookupIndex(nameHash); lookupIndex >= 0 && lookupIndex < size && this.nameHashLookups[lookupIndex] == nameHash; lookupIndex++) {
            long pos = getCentralDirectoryFileHeaderRecordPos(lookupIndex);
            ZipCentralDirectoryFileHeaderRecord centralRecord = loadZipCentralDirectoryFileHeaderRecord(pos);
            if (hasName(lookupIndex, centralRecord, pos, namePrefix, name)) {
                return true;
            }
        }
        return false;
    }

    public Entry getEntry(int index) {
        int lookupIndex = this.lookupIndexes[index];
        long pos = getCentralDirectoryFileHeaderRecordPos(lookupIndex);
        ZipCentralDirectoryFileHeaderRecord centralRecord = loadZipCentralDirectoryFileHeaderRecord(pos);
        return new Entry(lookupIndex, centralRecord);
    }

    private ZipCentralDirectoryFileHeaderRecord loadZipCentralDirectoryFileHeaderRecord(long pos) {
        try {
            return ZipCentralDirectoryFileHeaderRecord.load(this.data, pos);
        } catch (IOException ex) {
            if (ex instanceof ClosedChannelException) {
                throw new IllegalStateException("Zip content closed", ex);
            }
            throw new UncheckedIOException(ex);
        }
    }

    private int nameHash(CharSequence namePrefix, CharSequence name) {
        int nameHash = namePrefix != null ? ZipString.hash(0, namePrefix, false) : 0;
        return ZipString.hash(nameHash, name, true);
    }

    private int getFirstLookupIndex(int nameHash) {
        int lookupIndex = Arrays.binarySearch(this.nameHashLookups, 0, this.nameHashLookups.length, nameHash);
        if (lookupIndex < 0) {
            return -1;
        }
        while (lookupIndex > 0 && this.nameHashLookups[lookupIndex - 1] == nameHash) {
            lookupIndex--;
        }
        return lookupIndex;
    }

    private long getCentralDirectoryFileHeaderRecordPos(int lookupIndex) {
        return this.centralDirectoryPos + this.relativeCentralDirectoryOffsetLookups[lookupIndex];
    }

    private boolean hasName(int lookupIndex, ZipCentralDirectoryFileHeaderRecord centralRecord, long pos, CharSequence namePrefix, CharSequence name) {
        int offset = this.nameOffsetLookups.get(lookupIndex);
        long pos2 = pos + 46 + offset;
        int len = centralRecord.fileNameLength() - offset;
        ByteBuffer buffer = ByteBuffer.allocate(256);
        if (namePrefix != null) {
            int startsWithNamePrefix = ZipString.startsWith(buffer, this.data, pos2, len, namePrefix);
            if (startsWithNamePrefix == -1) {
                return false;
            }
            pos2 += startsWithNamePrefix;
            len -= startsWithNamePrefix;
        }
        return ZipString.matches(buffer, this.data, pos2, len, name, true);
    }

    public <I> I getInfo(Class<I> cls, Function<ZipContent, I> function) {
        Map<Class<?>, Object> map = this.info != null ? this.info.get() : null;
        if (map == null) {
            map = new ConcurrentHashMap();
            this.info = new SoftReference<>(map);
        }
        return (I) map.computeIfAbsent(cls, key -> {
            debug.log("Getting %s info from zip '%s'", cls.getName(), this);
            return function.apply(this);
        });
    }

    public boolean hasJarSignatureFile() {
        return this.hasJarSignatureFile;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.data.close();
    }

    public String toString() {
        return this.source.toString();
    }

    public static ZipContent open(Path path) throws IOException {
        return open(new Source(path.toAbsolutePath(), null));
    }

    public static ZipContent open(Path path, String nestedEntryName) throws IOException {
        return open(new Source(path.toAbsolutePath(), nestedEntryName));
    }

    private static ZipContent open(Source source) throws IOException {
        ZipContent zipContent = cache.get(source);
        if (zipContent != null) {
            debug.log("Opening existing cached zip content for %s", zipContent);
            zipContent.data.open();
            return zipContent;
        }
        debug.log("Loading zip content from %s", source);
        ZipContent zipContent2 = Loader.load(source);
        ZipContent previouslyCached = cache.putIfAbsent(source, zipContent2);
        if (previouslyCached != null) {
            debug.log("Closing zip content from %s since cache was populated from another thread", source);
            zipContent2.close();
            previouslyCached.data.open();
            return previouslyCached;
        }
        return zipContent2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:org/springframework/boot/loader/zip/ZipContent$Source.class */
    public static final class Source extends Record {
        private final Path path;
        private final String nestedEntryName;

        private Source(Path path, String nestedEntryName) {
            this.path = path;
            this.nestedEntryName = nestedEntryName;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Source.class), Source.class, "path;nestedEntryName", "FIELD:Lorg/springframework/boot/loader/zip/ZipContent$Source;->path:Ljava/nio/file/Path;", "FIELD:Lorg/springframework/boot/loader/zip/ZipContent$Source;->nestedEntryName:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Source.class, Object.class), Source.class, "path;nestedEntryName", "FIELD:Lorg/springframework/boot/loader/zip/ZipContent$Source;->path:Ljava/nio/file/Path;", "FIELD:Lorg/springframework/boot/loader/zip/ZipContent$Source;->nestedEntryName:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public Path path() {
            return this.path;
        }

        public String nestedEntryName() {
            return this.nestedEntryName;
        }

        boolean isNested() {
            return this.nestedEntryName != null;
        }

        @Override // java.lang.Record
        public String toString() {
            return !isNested() ? path().toString() : path() + "[" + nestedEntryName() + "]";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:org/springframework/boot/loader/zip/ZipContent$Loader.class */
    public static final class Loader {
        private final ByteBuffer buffer = ByteBuffer.allocate(256);
        private final Source source;
        private final FileChannelDataBlock data;
        private final long centralDirectoryPos;
        private final int[] index;
        private int[] nameHashLookups;
        private int[] relativeCentralDirectoryOffsetLookups;
        private final NameOffsetLookups nameOffsetLookups;
        private int cursor;

        private Loader(Source source, Entry directoryEntry, FileChannelDataBlock data, long centralDirectoryPos, int maxSize) {
            this.source = source;
            this.data = data;
            this.centralDirectoryPos = centralDirectoryPos;
            this.index = new int[maxSize];
            this.nameHashLookups = new int[maxSize];
            this.relativeCentralDirectoryOffsetLookups = new int[maxSize];
            this.nameOffsetLookups = directoryEntry != null ? new NameOffsetLookups(directoryEntry.getName().length(), maxSize) : NameOffsetLookups.NONE;
        }

        private void add(ZipCentralDirectoryFileHeaderRecord centralRecord, long pos, boolean enableNameOffset) throws IOException {
            int nameOffset = this.nameOffsetLookups.enable(this.cursor, enableNameOffset);
            int hash = ZipString.hash(this.buffer, this.data, pos + 46 + nameOffset, centralRecord.fileNameLength() - nameOffset, true);
            this.nameHashLookups[this.cursor] = hash;
            this.relativeCentralDirectoryOffsetLookups[this.cursor] = (int) (pos - this.centralDirectoryPos);
            this.index[this.cursor] = this.cursor;
            this.cursor++;
        }

        private ZipContent finish(Kind kind, long commentPos, long commentLength, boolean hasJarSignatureFile) {
            if (this.cursor != this.nameHashLookups.length) {
                this.nameHashLookups = Arrays.copyOf(this.nameHashLookups, this.cursor);
                this.relativeCentralDirectoryOffsetLookups = Arrays.copyOf(this.relativeCentralDirectoryOffsetLookups, this.cursor);
            }
            int size = this.nameHashLookups.length;
            sort(0, size - 1);
            int[] lookupIndexes = new int[size];
            for (int i = 0; i < size; i++) {
                lookupIndexes[this.index[i]] = i;
            }
            return new ZipContent(this.source, kind, this.data, this.centralDirectoryPos, commentPos, commentLength, lookupIndexes, this.nameHashLookups, this.relativeCentralDirectoryOffsetLookups, this.nameOffsetLookups, hasJarSignatureFile);
        }

        private void sort(int left, int right) {
            if (left < right) {
                int pivot = this.nameHashLookups[left + ((right - left) / 2)];
                int i = left;
                int j = right;
                while (i <= j) {
                    while (this.nameHashLookups[i] < pivot) {
                        i++;
                    }
                    while (this.nameHashLookups[j] > pivot) {
                        j--;
                    }
                    if (i <= j) {
                        swap(i, j);
                        i++;
                        j--;
                    }
                }
                if (left < j) {
                    sort(left, j);
                }
                if (right > i) {
                    sort(i, right);
                }
            }
        }

        private void swap(int i, int j) {
            swap(this.index, i, j);
            swap(this.nameHashLookups, i, j);
            swap(this.relativeCentralDirectoryOffsetLookups, i, j);
            this.nameOffsetLookups.swap(i, j);
        }

        private static void swap(int[] array, int i, int j) {
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }

        static ZipContent load(Source source) throws IOException {
            if (!source.isNested()) {
                return loadNonNested(source);
            }
            ZipContent zip = ZipContent.open(source.path());
            try {
                Entry entry = zip.getEntry(source.nestedEntryName());
                if (entry == null) {
                    throw new IOException("Nested entry '%s' not found in container zip '%s'".formatted(source.nestedEntryName(), source.path()));
                }
                ZipContent loadNestedZip = !entry.isDirectory() ? loadNestedZip(source, entry) : loadNestedDirectory(source, zip, entry);
                if (zip != null) {
                    zip.close();
                }
                return loadNestedZip;
            } catch (Throwable th) {
                if (zip != null) {
                    try {
                        zip.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        private static ZipContent loadNonNested(Source source) throws IOException {
            ZipContent.debug.log("Loading non-nested zip '%s'", source.path());
            return openAndLoad(source, Kind.ZIP, new FileChannelDataBlock(source.path()));
        }

        private static ZipContent loadNestedZip(Source source, Entry entry) throws IOException {
            if (entry.centralRecord.compressionMethod() != 0) {
                throw new IOException("Nested entry '%s' in container zip '%s' must not be compressed".formatted(source.nestedEntryName(), source.path()));
            }
            ZipContent.debug.log("Loading nested zip entry '%s' from '%s'", source.nestedEntryName(), source.path());
            return openAndLoad(source, Kind.NESTED_ZIP, entry.getContent());
        }

        private static ZipContent openAndLoad(Source source, Kind kind, FileChannelDataBlock data) throws IOException {
            try {
                data.open();
                return loadContent(source, kind, data);
            } catch (IOException | RuntimeException ex) {
                data.close();
                throw ex;
            }
        }

        private static ZipContent loadContent(Source source, Kind kind, FileChannelDataBlock data) throws IOException {
            ZipEndOfCentralDirectoryRecord.Located locatedEocd = ZipEndOfCentralDirectoryRecord.load(data);
            ZipEndOfCentralDirectoryRecord eocd = locatedEocd.endOfCentralDirectoryRecord();
            long eocdPos = locatedEocd.pos();
            Zip64EndOfCentralDirectoryLocator zip64Locator = Zip64EndOfCentralDirectoryLocator.find(data, eocdPos);
            Zip64EndOfCentralDirectoryRecord zip64Eocd = Zip64EndOfCentralDirectoryRecord.load(data, zip64Locator);
            FileChannelDataBlock data2 = data.slice(getStartOfZipContent(data, eocd, zip64Eocd));
            long centralDirectoryPos = zip64Eocd != null ? zip64Eocd.offsetToStartOfCentralDirectory() : Integer.toUnsignedLong(eocd.offsetToStartOfCentralDirectory());
            long numberOfEntries = zip64Eocd != null ? zip64Eocd.totalNumberOfCentralDirectoryEntries() : Short.toUnsignedInt(eocd.totalNumberOfCentralDirectoryEntries());
            if (numberOfEntries < 0) {
                throw new IllegalStateException("Invalid number of zip entries in " + source);
            }
            if (numberOfEntries > 2147483647L) {
                throw new IllegalStateException("Too many zip entries in " + source);
            }
            Loader loader = new Loader(source, null, data2, centralDirectoryPos, (int) numberOfEntries);
            ByteBuffer signatureNameSuffixBuffer = ByteBuffer.allocate(ZipContent.SIGNATURE_SUFFIX.length);
            boolean hasJarSignatureFile = false;
            long pos = centralDirectoryPos;
            for (int i = 0; i < numberOfEntries; i++) {
                ZipCentralDirectoryFileHeaderRecord centralRecord = ZipCentralDirectoryFileHeaderRecord.load(data2, pos);
                if (!hasJarSignatureFile) {
                    long filenamePos = pos + 46;
                    if (centralRecord.fileNameLength() > ZipContent.SIGNATURE_SUFFIX.length && ZipString.startsWith(loader.buffer, data2, filenamePos, centralRecord.fileNameLength(), ZipContent.META_INF) >= 0) {
                        signatureNameSuffixBuffer.clear();
                        data2.readFully(signatureNameSuffixBuffer, (filenamePos + centralRecord.fileNameLength()) - ZipContent.SIGNATURE_SUFFIX.length);
                        hasJarSignatureFile = Arrays.equals(ZipContent.SIGNATURE_SUFFIX, signatureNameSuffixBuffer.array());
                    }
                }
                loader.add(centralRecord, pos, false);
                pos += centralRecord.size();
            }
            long commentPos = locatedEocd.pos() + 22;
            return loader.finish(kind, commentPos, eocd.commentLength(), hasJarSignatureFile);
        }

        private static long getStartOfZipContent(FileChannelDataBlock data, ZipEndOfCentralDirectoryRecord eocd, Zip64EndOfCentralDirectoryRecord zip64Eocd) throws IOException {
            long specifiedOffsetToStartOfCentralDirectory = zip64Eocd != null ? zip64Eocd.offsetToStartOfCentralDirectory() : eocd.offsetToStartOfCentralDirectory();
            long sizeOfCentralDirectoryAndEndRecords = getSizeOfCentralDirectoryAndEndRecords(eocd, zip64Eocd);
            long actualOffsetToStartOfCentralDirectory = data.size() - sizeOfCentralDirectoryAndEndRecords;
            return actualOffsetToStartOfCentralDirectory - specifiedOffsetToStartOfCentralDirectory;
        }

        private static long getSizeOfCentralDirectoryAndEndRecords(ZipEndOfCentralDirectoryRecord eocd, Zip64EndOfCentralDirectoryRecord zip64Eocd) {
            long result = 0 + eocd.size();
            if (zip64Eocd != null) {
                result = result + 20 + zip64Eocd.size();
            }
            return result + (zip64Eocd != null ? zip64Eocd.sizeOfCentralDirectory() : eocd.sizeOfCentralDirectory());
        }

        private static ZipContent loadNestedDirectory(Source source, ZipContent zip, Entry directoryEntry) throws IOException {
            ZipContent.debug.log("Loading nested directry entry '%s' from '%s'", source.nestedEntryName(), source.path());
            if (!source.nestedEntryName().endsWith("/")) {
                throw new IllegalArgumentException("Nested entry name must end with '/'");
            }
            String directoryName = directoryEntry.getName();
            zip.data.open();
            try {
                Loader loader = new Loader(source, directoryEntry, zip.data, zip.centralDirectoryPos, zip.size());
                for (int cursor = 0; cursor < zip.size(); cursor++) {
                    int index = zip.lookupIndexes[cursor];
                    if (index != directoryEntry.getLookupIndex()) {
                        long pos = zip.getCentralDirectoryFileHeaderRecordPos(index);
                        ZipCentralDirectoryFileHeaderRecord centralRecord = ZipCentralDirectoryFileHeaderRecord.load(zip.data, pos);
                        long namePos = pos + 46;
                        short nameLen = centralRecord.fileNameLength();
                        if (ZipString.startsWith(loader.buffer, zip.data, namePos, nameLen, directoryName) != -1) {
                            loader.add(centralRecord, pos, true);
                        }
                    }
                }
                return loader.finish(Kind.NESTED_DIRECTORY, zip.commentPos, zip.commentLength, zip.hasJarSignatureFile);
            } catch (IOException | RuntimeException ex) {
                zip.data.close();
                throw ex;
            }
        }
    }

    /* loaded from: agent.jar:org/springframework/boot/loader/zip/ZipContent$Entry.class */
    public class Entry {
        private final int lookupIndex;
        private final ZipCentralDirectoryFileHeaderRecord centralRecord;
        private volatile String name;
        private volatile FileChannelDataBlock content;

        Entry(int lookupIndex, ZipCentralDirectoryFileHeaderRecord centralRecord) {
            this.lookupIndex = lookupIndex;
            this.centralRecord = centralRecord;
        }

        public int getLookupIndex() {
            return this.lookupIndex;
        }

        public boolean isDirectory() {
            return getName().endsWith("/");
        }

        public boolean hasNameStartingWith(CharSequence prefix) {
            String name = this.name;
            if (name != null) {
                return name.startsWith(prefix.toString());
            }
            long pos = ZipContent.this.getCentralDirectoryFileHeaderRecordPos(this.lookupIndex) + 46;
            return ZipString.startsWith(null, ZipContent.this.data, pos, this.centralRecord.fileNameLength(), prefix) != -1;
        }

        public String getName() {
            String name = this.name;
            if (name == null) {
                int offset = ZipContent.this.nameOffsetLookups.get(this.lookupIndex);
                long pos = ZipContent.this.getCentralDirectoryFileHeaderRecordPos(this.lookupIndex) + 46 + offset;
                name = ZipString.readString(ZipContent.this.data, pos, this.centralRecord.fileNameLength() - offset);
                this.name = name;
            }
            return name;
        }

        public int getCompressionMethod() {
            return this.centralRecord.compressionMethod();
        }

        public int getUncompressedSize() {
            return this.centralRecord.uncompressedSize();
        }

        public CloseableDataBlock openContent() throws IOException {
            FileChannelDataBlock content = getContent();
            content.open();
            return content;
        }

        private FileChannelDataBlock getContent() throws IOException {
            FileChannelDataBlock content = this.content;
            if (content == null) {
                int pos = this.centralRecord.offsetToLocalHeader();
                checkNotZip64Extended(pos);
                ZipLocalFileHeaderRecord localHeader = ZipLocalFileHeaderRecord.load(ZipContent.this.data, pos);
                int size = this.centralRecord.compressedSize();
                checkNotZip64Extended(size);
                content = ZipContent.this.data.slice(pos + localHeader.size(), size);
                this.content = content;
            }
            return content;
        }

        private void checkNotZip64Extended(int value) throws IOException {
            if (value == -1) {
                throw new IOException("Zip64 extended information extra fields are not supported");
            }
        }

        public <E extends ZipEntry> E as(Function<String, E> function) {
            return (E) as((entry, name) -> {
                return (ZipEntry) function.apply(name);
            });
        }

        public <E extends ZipEntry> E as(BiFunction<Entry, String, E> factory) {
            try {
                E result = factory.apply(this, getName());
                long pos = ZipContent.this.getCentralDirectoryFileHeaderRecordPos(this.lookupIndex);
                this.centralRecord.copyTo(ZipContent.this.data, pos, result);
                return result;
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
    }
}
