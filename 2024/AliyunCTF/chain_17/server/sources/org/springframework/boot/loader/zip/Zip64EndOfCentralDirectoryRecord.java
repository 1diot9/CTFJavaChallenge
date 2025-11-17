package org.springframework.boot.loader.zip;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.springframework.boot.loader.log.DebugLogger;

/* loaded from: server.jar:org/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord.class */
final class Zip64EndOfCentralDirectoryRecord extends Record {
    private final long size;
    private final long sizeOfZip64EndOfCentralDirectoryRecord;
    private final short versionMadeBy;
    private final short versionNeededToExtract;
    private final int numberOfThisDisk;
    private final int diskWhereCentralDirectoryStarts;
    private final long numberOfCentralDirectoryEntriesOnThisDisk;
    private final long totalNumberOfCentralDirectoryEntries;
    private final long sizeOfCentralDirectory;
    private final long offsetToStartOfCentralDirectory;
    private static final DebugLogger debug = DebugLogger.get(Zip64EndOfCentralDirectoryRecord.class);
    private static final int SIGNATURE = 101075792;
    private static final int MINIMUM_SIZE = 56;

    Zip64EndOfCentralDirectoryRecord(long size, long sizeOfZip64EndOfCentralDirectoryRecord, short versionMadeBy, short versionNeededToExtract, int numberOfThisDisk, int diskWhereCentralDirectoryStarts, long numberOfCentralDirectoryEntriesOnThisDisk, long totalNumberOfCentralDirectoryEntries, long sizeOfCentralDirectory, long offsetToStartOfCentralDirectory) {
        this.size = size;
        this.sizeOfZip64EndOfCentralDirectoryRecord = sizeOfZip64EndOfCentralDirectoryRecord;
        this.versionMadeBy = versionMadeBy;
        this.versionNeededToExtract = versionNeededToExtract;
        this.numberOfThisDisk = numberOfThisDisk;
        this.diskWhereCentralDirectoryStarts = diskWhereCentralDirectoryStarts;
        this.numberOfCentralDirectoryEntriesOnThisDisk = numberOfCentralDirectoryEntriesOnThisDisk;
        this.totalNumberOfCentralDirectoryEntries = totalNumberOfCentralDirectoryEntries;
        this.sizeOfCentralDirectory = sizeOfCentralDirectory;
        this.offsetToStartOfCentralDirectory = offsetToStartOfCentralDirectory;
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Zip64EndOfCentralDirectoryRecord.class), Zip64EndOfCentralDirectoryRecord.class, "size;sizeOfZip64EndOfCentralDirectoryRecord;versionMadeBy;versionNeededToExtract;numberOfThisDisk;diskWhereCentralDirectoryStarts;numberOfCentralDirectoryEntriesOnThisDisk;totalNumberOfCentralDirectoryEntries;sizeOfCentralDirectory;offsetToStartOfCentralDirectory", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->size:J", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->sizeOfZip64EndOfCentralDirectoryRecord:J", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->versionMadeBy:S", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->versionNeededToExtract:S", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->numberOfThisDisk:I", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->diskWhereCentralDirectoryStarts:I", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->numberOfCentralDirectoryEntriesOnThisDisk:J", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->totalNumberOfCentralDirectoryEntries:J", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->sizeOfCentralDirectory:J", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->offsetToStartOfCentralDirectory:J").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Zip64EndOfCentralDirectoryRecord.class), Zip64EndOfCentralDirectoryRecord.class, "size;sizeOfZip64EndOfCentralDirectoryRecord;versionMadeBy;versionNeededToExtract;numberOfThisDisk;diskWhereCentralDirectoryStarts;numberOfCentralDirectoryEntriesOnThisDisk;totalNumberOfCentralDirectoryEntries;sizeOfCentralDirectory;offsetToStartOfCentralDirectory", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->size:J", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->sizeOfZip64EndOfCentralDirectoryRecord:J", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->versionMadeBy:S", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->versionNeededToExtract:S", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->numberOfThisDisk:I", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->diskWhereCentralDirectoryStarts:I", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->numberOfCentralDirectoryEntriesOnThisDisk:J", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->totalNumberOfCentralDirectoryEntries:J", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->sizeOfCentralDirectory:J", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->offsetToStartOfCentralDirectory:J").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Zip64EndOfCentralDirectoryRecord.class, Object.class), Zip64EndOfCentralDirectoryRecord.class, "size;sizeOfZip64EndOfCentralDirectoryRecord;versionMadeBy;versionNeededToExtract;numberOfThisDisk;diskWhereCentralDirectoryStarts;numberOfCentralDirectoryEntriesOnThisDisk;totalNumberOfCentralDirectoryEntries;sizeOfCentralDirectory;offsetToStartOfCentralDirectory", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->size:J", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->sizeOfZip64EndOfCentralDirectoryRecord:J", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->versionMadeBy:S", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->versionNeededToExtract:S", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->numberOfThisDisk:I", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->diskWhereCentralDirectoryStarts:I", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->numberOfCentralDirectoryEntriesOnThisDisk:J", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->totalNumberOfCentralDirectoryEntries:J", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->sizeOfCentralDirectory:J", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryRecord;->offsetToStartOfCentralDirectory:J").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public long size() {
        return this.size;
    }

    public long sizeOfZip64EndOfCentralDirectoryRecord() {
        return this.sizeOfZip64EndOfCentralDirectoryRecord;
    }

    public short versionMadeBy() {
        return this.versionMadeBy;
    }

    public short versionNeededToExtract() {
        return this.versionNeededToExtract;
    }

    public int numberOfThisDisk() {
        return this.numberOfThisDisk;
    }

    public int diskWhereCentralDirectoryStarts() {
        return this.diskWhereCentralDirectoryStarts;
    }

    public long numberOfCentralDirectoryEntriesOnThisDisk() {
        return this.numberOfCentralDirectoryEntriesOnThisDisk;
    }

    public long totalNumberOfCentralDirectoryEntries() {
        return this.totalNumberOfCentralDirectoryEntries;
    }

    public long sizeOfCentralDirectory() {
        return this.sizeOfCentralDirectory;
    }

    public long offsetToStartOfCentralDirectory() {
        return this.offsetToStartOfCentralDirectory;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Zip64EndOfCentralDirectoryRecord load(DataBlock dataBlock, Zip64EndOfCentralDirectoryLocator locator) throws IOException {
        if (locator == null) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.allocate(56);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        long size = locator.pos() - locator.offsetToZip64EndOfCentralDirectoryRecord();
        long pos = locator.pos() - size;
        debug.log("Loading Zip64EndOfCentralDirectoryRecord from position %s size %s", Long.valueOf(pos), Long.valueOf(size));
        dataBlock.readFully(buffer, pos);
        buffer.rewind();
        int signature = buffer.getInt();
        if (signature != SIGNATURE) {
            debug.log("Found incorrect Zip64EndOfCentralDirectoryRecord signature %s at position %s", Integer.valueOf(signature), Long.valueOf(pos));
            throw new IOException("Zip64 'End Of Central Directory Record' not found at position " + pos + ". Zip file is corrupt or includes prefixed bytes which are not supported with Zip64 files");
        }
        return new Zip64EndOfCentralDirectoryRecord(size, buffer.getLong(), buffer.getShort(), buffer.getShort(), buffer.getInt(), buffer.getInt(), buffer.getLong(), buffer.getLong(), buffer.getLong(), buffer.getLong());
    }
}
