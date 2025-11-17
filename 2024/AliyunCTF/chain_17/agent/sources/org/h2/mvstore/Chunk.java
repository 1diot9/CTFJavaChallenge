package org.h2.mvstore;

import cn.hutool.core.text.CharSequenceUtil;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Map;
import org.h2.mvstore.Chunk;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/Chunk.class */
public abstract class Chunk<C extends Chunk<C>> {
    public static final int MAX_ID = 67108863;
    static final int MAX_HEADER_LENGTH = 1024;
    static final int FOOTER_LENGTH = 128;
    private static final String ATTR_CHUNK = "chunk";
    private static final String ATTR_BLOCK = "block";
    private static final String ATTR_LEN = "len";
    private static final String ATTR_MAP = "map";
    private static final String ATTR_MAX = "max";
    private static final String ATTR_NEXT = "next";
    private static final String ATTR_PAGES = "pages";
    private static final String ATTR_ROOT = "root";
    private static final String ATTR_TIME = "time";
    private static final String ATTR_VERSION = "version";
    private static final String ATTR_LIVE_MAX = "liveMax";
    private static final String ATTR_LIVE_PAGES = "livePages";
    private static final String ATTR_UNUSED = "unused";
    private static final String ATTR_UNUSED_AT_VERSION = "unusedAtVersion";
    private static final String ATTR_PIN_COUNT = "pinCount";
    private static final String ATTR_TOC = "toc";
    private static final String ATTR_OCCUPANCY = "occupancy";
    private static final String ATTR_FLETCHER = "fletcher";
    public final int id;
    public volatile long block;
    public int len;
    int pageCount;
    int pageCountLive;
    int tocPos;
    BitSet occupancy;
    public long maxLen;
    public long maxLenLive;
    int collectPriority;
    long layoutRootPos;
    public long version;
    public long time;
    public long unused;
    long unusedAtVersion;
    public int mapId;
    public long next;
    private int pinCount;
    public volatile ByteBuffer buffer;
    static final /* synthetic */ boolean $assertionsDisabled;

    protected abstract ByteBuffer readFully(FileStore<C> fileStore, long j, int i);

    static {
        $assertionsDisabled = !Chunk.class.desiredAssertionStatus();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Chunk(String str) {
        this(DataUtils.parseMap(str), true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Chunk(Map<String, String> map, boolean z) {
        this(DataUtils.readHexInt(map, ATTR_CHUNK, 0));
        this.block = DataUtils.readHexLong(map, ATTR_BLOCK, 0L);
        this.len = DataUtils.readHexInt(map, ATTR_LEN, 0);
        this.version = DataUtils.readHexLong(map, ATTR_VERSION, this.id);
        if (z) {
            this.pageCount = DataUtils.readHexInt(map, ATTR_PAGES, 0);
            this.pageCountLive = DataUtils.readHexInt(map, ATTR_LIVE_PAGES, this.pageCount);
            this.mapId = DataUtils.readHexInt(map, "map", 0);
            this.maxLen = DataUtils.readHexLong(map, ATTR_MAX, 0L);
            this.maxLenLive = DataUtils.readHexLong(map, ATTR_LIVE_MAX, this.maxLen);
            this.layoutRootPos = DataUtils.readHexLong(map, ATTR_ROOT, 0L);
            this.time = DataUtils.readHexLong(map, ATTR_TIME, 0L);
            this.unused = DataUtils.readHexLong(map, ATTR_UNUSED, 0L);
            this.unusedAtVersion = DataUtils.readHexLong(map, ATTR_UNUSED_AT_VERSION, 0L);
            this.next = DataUtils.readHexLong(map, ATTR_NEXT, 0L);
            this.pinCount = DataUtils.readHexInt(map, ATTR_PIN_COUNT, 0);
            this.tocPos = DataUtils.readHexInt(map, ATTR_TOC, 0);
            byte[] parseHexBytes = DataUtils.parseHexBytes(map, ATTR_OCCUPANCY);
            if (parseHexBytes == null) {
                this.occupancy = new BitSet();
                if (!$assertionsDisabled && this.pageCountLive != this.pageCount) {
                    throw new AssertionError();
                }
                return;
            }
            this.occupancy = BitSet.valueOf(parseHexBytes);
            if (this.pageCount - this.pageCountLive != this.occupancy.cardinality()) {
                throw DataUtils.newMVStoreException(6, "Inconsistent occupancy info {0} - {1} != {2} {3}", Integer.valueOf(this.pageCount), Integer.valueOf(this.pageCountLive), Integer.valueOf(this.occupancy.cardinality()), this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Chunk(int i) {
        this.id = i;
        if (i <= 0) {
            throw DataUtils.newMVStoreException(6, "Invalid chunk id {0}", Integer.valueOf(i));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String readChunkHeader(ByteBuffer byteBuffer) {
        int position = byteBuffer.position();
        byte[] bArr = new byte[Math.min(byteBuffer.remaining(), 1024)];
        byteBuffer.get(bArr);
        for (int i = 0; i < bArr.length; i++) {
            if (bArr[i] == 10) {
                byteBuffer.position(position + i + 1);
                return new String(bArr, 0, i, StandardCharsets.ISO_8859_1).trim();
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int estimateHeaderSize() {
        int length = getHeaderBytes().length;
        if ($assertionsDisabled || (57 <= length && length <= 94)) {
            return length + 104 + 1;
        }
        throw new AssertionError(length + CharSequenceUtil.SPACE + getHeader());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeChunkHeader(WriteBuffer writeBuffer, int i) {
        int position = (writeBuffer.position() + i) - 1;
        writeBuffer.put(getHeaderBytes());
        while (writeBuffer.position() < position) {
            writeBuffer.put((byte) 32);
        }
        if (i != 0 && writeBuffer.position() > position) {
            throw DataUtils.newMVStoreException(3, "Chunk metadata too long {0} {1} {2}", Integer.valueOf(position), Integer.valueOf(writeBuffer.position()), getHeader());
        }
        writeBuffer.put((byte) 10);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getMetaKey(int i) {
        return DataUtils.META_CHUNK + Integer.toHexString(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getFillRate() {
        if (!$assertionsDisabled && this.maxLenLive > this.maxLen) {
            throw new AssertionError(this.maxLenLive + " > " + this.maxLen);
        }
        if (this.maxLenLive <= 0) {
            return 0;
        }
        if (this.maxLenLive == this.maxLen) {
            return 100;
        }
        return 1 + ((int) ((98 * this.maxLenLive) / this.maxLen));
    }

    public int hashCode() {
        return this.id;
    }

    public boolean equals(Object obj) {
        return (obj instanceof Chunk) && ((Chunk) obj).id == this.id;
    }

    public final String asString() {
        StringBuilder sb = new StringBuilder(240);
        dump(sb);
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dump(StringBuilder sb) {
        DataUtils.appendMap(sb, ATTR_CHUNK, this.id);
        DataUtils.appendMap(sb, ATTR_BLOCK, this.block);
        DataUtils.appendMap(sb, ATTR_LEN, this.len);
        DataUtils.appendMap(sb, ATTR_PAGES, this.pageCount);
        if (this.pageCount != this.pageCountLive) {
            DataUtils.appendMap(sb, ATTR_LIVE_PAGES, this.pageCountLive);
        }
        DataUtils.appendMap(sb, ATTR_MAX, this.maxLen);
        if (this.maxLen != this.maxLenLive) {
            DataUtils.appendMap(sb, ATTR_LIVE_MAX, this.maxLenLive);
        }
        DataUtils.appendMap(sb, "map", this.mapId);
        if (this.next != 0) {
            DataUtils.appendMap(sb, ATTR_NEXT, this.next);
        }
        DataUtils.appendMap(sb, ATTR_ROOT, this.layoutRootPos);
        DataUtils.appendMap(sb, ATTR_TIME, this.time);
        if (this.unused != 0) {
            DataUtils.appendMap(sb, ATTR_UNUSED, this.unused);
        }
        if (this.unusedAtVersion != 0) {
            DataUtils.appendMap(sb, ATTR_UNUSED_AT_VERSION, this.unusedAtVersion);
        }
        DataUtils.appendMap(sb, ATTR_VERSION, this.version);
        if (this.pinCount > 0) {
            DataUtils.appendMap(sb, ATTR_PIN_COUNT, this.pinCount);
        }
        if (this.tocPos > 0) {
            DataUtils.appendMap(sb, ATTR_TOC, this.tocPos);
        }
        if (this.occupancy != null && !this.occupancy.isEmpty()) {
            DataUtils.appendMap(sb, ATTR_OCCUPANCY, StringUtils.convertBytesToHex(this.occupancy.toByteArray()));
        }
    }

    public String getHeader() {
        return new String(getHeaderBytes(), StandardCharsets.ISO_8859_1);
    }

    private byte[] getHeaderBytes() {
        StringBuilder sb = new StringBuilder(240);
        DataUtils.appendMap(sb, ATTR_CHUNK, this.id);
        DataUtils.appendMap(sb, ATTR_LEN, this.len);
        DataUtils.appendMap(sb, ATTR_PAGES, this.pageCount);
        if (this.pinCount > 0) {
            DataUtils.appendMap(sb, ATTR_PIN_COUNT, this.pinCount);
        }
        DataUtils.appendMap(sb, ATTR_MAX, this.maxLen);
        DataUtils.appendMap(sb, "map", this.mapId);
        DataUtils.appendMap(sb, ATTR_ROOT, this.layoutRootPos);
        DataUtils.appendMap(sb, ATTR_TIME, this.time);
        DataUtils.appendMap(sb, ATTR_VERSION, this.version);
        if (this.next != 0) {
            DataUtils.appendMap(sb, ATTR_NEXT, this.next);
        }
        if (this.tocPos > 0) {
            DataUtils.appendMap(sb, ATTR_TOC, this.tocPos);
        }
        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] getFooterBytes() {
        StringBuilder sb = new StringBuilder(128);
        DataUtils.appendMap(sb, ATTR_CHUNK, this.id);
        DataUtils.appendMap(sb, ATTR_LEN, this.len);
        DataUtils.appendMap(sb, ATTR_VERSION, this.version);
        byte[] bytes = sb.toString().getBytes(StandardCharsets.ISO_8859_1);
        DataUtils.appendMap(sb, ATTR_FLETCHER, DataUtils.getFletcher32(bytes, 0, bytes.length));
        while (sb.length() < 127) {
            sb.append(' ');
        }
        sb.append('\n');
        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAllocated() {
        return this.block != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isSaved() {
        return isAllocated() && this.buffer == null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isLive() {
        return this.pageCountLive > 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isRewritable() {
        return isSaved() && isLive() && this.pageCountLive < this.pageCount && isEvacuatable();
    }

    private boolean isEvacuatable() {
        return this.pinCount == 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ByteBuffer readBufferForPage(FileStore<C> fileStore, int i, long j) {
        long j2;
        long j3;
        ByteBuffer slice;
        if (!$assertionsDisabled && !isSaved()) {
            throw new AssertionError(this);
        }
        while (true) {
            long j4 = this.block;
            try {
                long j5 = j4 * 4096;
                j2 = j5 + (this.len * 4096);
                j3 = j5 + i;
            } catch (MVStoreException e) {
                if (j4 == this.block) {
                    throw e;
                }
            }
            if (j3 < 0) {
                throw DataUtils.newMVStoreException(6, "Negative position {0}; p={1}, c={2}", Long.valueOf(j3), Long.valueOf(j), toString());
            }
            int pageMaxLength = DataUtils.getPageMaxLength(j);
            if (pageMaxLength == 2097152) {
                pageMaxLength = readFully(fileStore, j3, 128).getInt() + 4;
            }
            int min = (int) Math.min(j2 - j3, pageMaxLength);
            if (min < 0) {
                throw DataUtils.newMVStoreException(6, "Illegal page length {0} reading at {1}; max pos {2} ", Integer.valueOf(min), Long.valueOf(j3), Long.valueOf(j2));
            }
            ByteBuffer byteBuffer = this.buffer;
            if (byteBuffer == null) {
                slice = readFully(fileStore, j3, min);
            } else {
                ByteBuffer duplicate = byteBuffer.duplicate();
                duplicate.position(i);
                slice = duplicate.slice();
                slice.limit(min);
            }
            if (j4 == this.block) {
                return slice;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long[] readToC(FileStore<C> fileStore) {
        ByteBuffer slice;
        if (!$assertionsDisabled && this.buffer == null && !isAllocated()) {
            throw new AssertionError(this);
        }
        if (!$assertionsDisabled && this.tocPos <= 0) {
            throw new AssertionError();
        }
        long[] jArr = new long[this.pageCount];
        while (true) {
            long j = this.block;
            try {
                ByteBuffer byteBuffer = this.buffer;
                if (byteBuffer == null) {
                    slice = readFully(fileStore, (j * 4096) + this.tocPos, this.pageCount * 8);
                } else {
                    ByteBuffer duplicate = byteBuffer.duplicate();
                    duplicate.position(this.tocPos);
                    slice = duplicate.slice();
                }
                slice.asLongBuffer().get(jArr);
            } catch (MVStoreException e) {
                if (j == this.block) {
                    throw e;
                }
            }
            if (j == this.block) {
                return jArr;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void accountForWrittenPage(int i, boolean z) {
        this.maxLen += i;
        this.pageCount++;
        this.maxLenLive += i;
        this.pageCountLive++;
        if (z) {
            this.pinCount++;
        }
        if (!$assertionsDisabled && this.pageCount - this.pageCountLive != this.occupancy.cardinality()) {
            throw new AssertionError(this.pageCount + " - " + this.pageCountLive + " <> " + this.occupancy.cardinality() + " : " + this.occupancy);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean accountForRemovedPage(int i, int i2, boolean z, long j, long j2) {
        if (!$assertionsDisabled && this.buffer == null && !isAllocated()) {
            throw new AssertionError(this);
        }
        if (this.tocPos > 0) {
            if (!$assertionsDisabled && (i < 0 || i >= this.pageCount)) {
                throw new AssertionError(i + " // " + this.pageCount);
            }
            if (!$assertionsDisabled && this.occupancy.get(i)) {
                throw new AssertionError(i + CharSequenceUtil.SPACE + this + CharSequenceUtil.SPACE + this.occupancy);
            }
            if (!$assertionsDisabled && this.pageCount - this.pageCountLive != this.occupancy.cardinality()) {
                throw new AssertionError(this.pageCount + " - " + this.pageCountLive + " <> " + this.occupancy.cardinality() + " : " + this.occupancy);
            }
            this.occupancy.set(i);
        }
        this.maxLenLive -= i2;
        this.pageCountLive--;
        if (z) {
            this.pinCount--;
        }
        if (this.unusedAtVersion < j2) {
            this.unusedAtVersion = j2;
        }
        if (!$assertionsDisabled && this.pinCount < 0) {
            throw new AssertionError(this);
        }
        if (!$assertionsDisabled && this.pageCountLive < 0) {
            throw new AssertionError(this);
        }
        if (!$assertionsDisabled && this.pinCount > this.pageCountLive) {
            throw new AssertionError(this);
        }
        if (!$assertionsDisabled && this.maxLenLive < 0) {
            throw new AssertionError(this);
        }
        if (!$assertionsDisabled) {
            if ((this.pageCountLive == 0) != (this.maxLenLive == 0)) {
                throw new AssertionError(this);
            }
        }
        if (!isLive()) {
            this.unused = j;
            return true;
        }
        return false;
    }

    public String toString() {
        return asString() + (this.buffer == null ? "" : ", buf");
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/Chunk$PositionComparator.class */
    public static final class PositionComparator<C extends Chunk<C>> implements Comparator<C> {
        public static final Comparator<? extends Chunk<?>> INSTANCE = new PositionComparator();

        public static <C extends Chunk<C>> Comparator<C> instance() {
            return (Comparator<C>) INSTANCE;
        }

        private PositionComparator() {
        }

        @Override // java.util.Comparator
        public int compare(C c, C c2) {
            return Long.compare(c.block, c2.block);
        }
    }
}
