package org.springframework.boot.loader.zip;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.springframework.boot.loader.log.DebugLogger;

/* loaded from: agent.jar:org/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord.class */
final class ZipEndOfCentralDirectoryRecord extends Record {
    private final short numberOfThisDisk;
    private final short diskWhereCentralDirectoryStarts;
    private final short numberOfCentralDirectoryEntriesOnThisDisk;
    private final short totalNumberOfCentralDirectoryEntries;
    private final int sizeOfCentralDirectory;
    private final int offsetToStartOfCentralDirectory;
    private final short commentLength;
    private static final DebugLogger debug = DebugLogger.get(ZipEndOfCentralDirectoryRecord.class);
    private static final int SIGNATURE = 101010256;
    private static final int MAXIMUM_COMMENT_LENGTH = 65535;
    private static final int MINIMUM_SIZE = 22;
    private static final int MAXIMUM_SIZE = 65557;
    static final int BUFFER_SIZE = 256;
    static final int COMMENT_OFFSET = 22;

    ZipEndOfCentralDirectoryRecord(short numberOfThisDisk, short diskWhereCentralDirectoryStarts, short numberOfCentralDirectoryEntriesOnThisDisk, short totalNumberOfCentralDirectoryEntries, int sizeOfCentralDirectory, int offsetToStartOfCentralDirectory, short commentLength) {
        this.numberOfThisDisk = numberOfThisDisk;
        this.diskWhereCentralDirectoryStarts = diskWhereCentralDirectoryStarts;
        this.numberOfCentralDirectoryEntriesOnThisDisk = numberOfCentralDirectoryEntriesOnThisDisk;
        this.totalNumberOfCentralDirectoryEntries = totalNumberOfCentralDirectoryEntries;
        this.sizeOfCentralDirectory = sizeOfCentralDirectory;
        this.offsetToStartOfCentralDirectory = offsetToStartOfCentralDirectory;
        this.commentLength = commentLength;
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, ZipEndOfCentralDirectoryRecord.class), ZipEndOfCentralDirectoryRecord.class, "numberOfThisDisk;diskWhereCentralDirectoryStarts;numberOfCentralDirectoryEntriesOnThisDisk;totalNumberOfCentralDirectoryEntries;sizeOfCentralDirectory;offsetToStartOfCentralDirectory;commentLength", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;->numberOfThisDisk:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;->diskWhereCentralDirectoryStarts:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;->numberOfCentralDirectoryEntriesOnThisDisk:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;->totalNumberOfCentralDirectoryEntries:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;->sizeOfCentralDirectory:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;->offsetToStartOfCentralDirectory:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;->commentLength:S").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, ZipEndOfCentralDirectoryRecord.class), ZipEndOfCentralDirectoryRecord.class, "numberOfThisDisk;diskWhereCentralDirectoryStarts;numberOfCentralDirectoryEntriesOnThisDisk;totalNumberOfCentralDirectoryEntries;sizeOfCentralDirectory;offsetToStartOfCentralDirectory;commentLength", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;->numberOfThisDisk:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;->diskWhereCentralDirectoryStarts:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;->numberOfCentralDirectoryEntriesOnThisDisk:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;->totalNumberOfCentralDirectoryEntries:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;->sizeOfCentralDirectory:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;->offsetToStartOfCentralDirectory:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;->commentLength:S").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, ZipEndOfCentralDirectoryRecord.class, Object.class), ZipEndOfCentralDirectoryRecord.class, "numberOfThisDisk;diskWhereCentralDirectoryStarts;numberOfCentralDirectoryEntriesOnThisDisk;totalNumberOfCentralDirectoryEntries;sizeOfCentralDirectory;offsetToStartOfCentralDirectory;commentLength", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;->numberOfThisDisk:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;->diskWhereCentralDirectoryStarts:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;->numberOfCentralDirectoryEntriesOnThisDisk:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;->totalNumberOfCentralDirectoryEntries:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;->sizeOfCentralDirectory:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;->offsetToStartOfCentralDirectory:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;->commentLength:S").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public short numberOfThisDisk() {
        return this.numberOfThisDisk;
    }

    public short diskWhereCentralDirectoryStarts() {
        return this.diskWhereCentralDirectoryStarts;
    }

    public short numberOfCentralDirectoryEntriesOnThisDisk() {
        return this.numberOfCentralDirectoryEntriesOnThisDisk;
    }

    public short totalNumberOfCentralDirectoryEntries() {
        return this.totalNumberOfCentralDirectoryEntries;
    }

    public int sizeOfCentralDirectory() {
        return this.sizeOfCentralDirectory;
    }

    public int offsetToStartOfCentralDirectory() {
        return this.offsetToStartOfCentralDirectory;
    }

    public short commentLength() {
        return this.commentLength;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ZipEndOfCentralDirectoryRecord(short totalNumberOfCentralDirectoryEntries, int sizeOfCentralDirectory, int offsetToStartOfCentralDirectory) {
        this((short) 0, (short) 0, totalNumberOfCentralDirectoryEntries, totalNumberOfCentralDirectoryEntries, sizeOfCentralDirectory, offsetToStartOfCentralDirectory, (short) 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long size() {
        return 22 + this.commentLength;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] asByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(22);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(SIGNATURE);
        buffer.putShort(this.numberOfThisDisk);
        buffer.putShort(this.diskWhereCentralDirectoryStarts);
        buffer.putShort(this.numberOfCentralDirectoryEntriesOnThisDisk);
        buffer.putShort(this.totalNumberOfCentralDirectoryEntries);
        buffer.putInt(this.sizeOfCentralDirectory);
        buffer.putInt(this.offsetToStartOfCentralDirectory);
        buffer.putShort(this.commentLength);
        return buffer.array();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Located load(DataBlock dataBlock) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        long pos = locate(dataBlock, buffer);
        return new Located(pos, new ZipEndOfCentralDirectoryRecord(buffer.getShort(), buffer.getShort(), buffer.getShort(), buffer.getShort(), buffer.getInt(), buffer.getInt(), buffer.getShort()));
    }

    private static long locate(DataBlock dataBlock, ByteBuffer buffer) throws IOException {
        long endPos = dataBlock.size();
        debug.log("Finding EndOfCentralDirectoryRecord starting at end position %s", Long.valueOf(endPos));
        while (endPos > 0) {
            buffer.clear();
            long totalRead = dataBlock.size() - endPos;
            if (totalRead > 65557) {
                throw new IOException("Zip 'End Of Central Directory Record' not found after reading " + totalRead + " bytes");
            }
            long startPos = endPos - buffer.limit();
            if (startPos < 0) {
                buffer.limit(((int) startPos) + buffer.limit());
                startPos = 0;
            }
            debug.log("Finding EndOfCentralDirectoryRecord from %s with limit %s", Long.valueOf(startPos), Integer.valueOf(buffer.limit()));
            dataBlock.readFully(buffer, startPos);
            int offset = findInBuffer(buffer);
            if (offset >= 0) {
                debug.log("Found EndOfCentralDirectoryRecord at %s + %s", Long.valueOf(startPos), Integer.valueOf(offset));
                return startPos + offset;
            }
            endPos = (endPos - 256) + 22;
        }
        throw new IOException("Zip 'End Of Central Directory Record' not found after reading entire data block");
    }

    private static int findInBuffer(ByteBuffer buffer) {
        for (int pos = buffer.limit() - 4; pos >= 0; pos--) {
            buffer.position(pos);
            if (buffer.getInt() == SIGNATURE) {
                return pos;
            }
        }
        return -1;
    }

    /* loaded from: agent.jar:org/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord$Located.class */
    static final class Located extends Record {
        private final long pos;
        private final ZipEndOfCentralDirectoryRecord endOfCentralDirectoryRecord;

        Located(long pos, ZipEndOfCentralDirectoryRecord endOfCentralDirectoryRecord) {
            this.pos = pos;
            this.endOfCentralDirectoryRecord = endOfCentralDirectoryRecord;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Located.class), Located.class, "pos;endOfCentralDirectoryRecord", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord$Located;->pos:J", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord$Located;->endOfCentralDirectoryRecord:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Located.class), Located.class, "pos;endOfCentralDirectoryRecord", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord$Located;->pos:J", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord$Located;->endOfCentralDirectoryRecord:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Located.class, Object.class), Located.class, "pos;endOfCentralDirectoryRecord", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord$Located;->pos:J", "FIELD:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord$Located;->endOfCentralDirectoryRecord:Lorg/springframework/boot/loader/zip/ZipEndOfCentralDirectoryRecord;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public long pos() {
            return this.pos;
        }

        public ZipEndOfCentralDirectoryRecord endOfCentralDirectoryRecord() {
            return this.endOfCentralDirectoryRecord;
        }
    }
}
