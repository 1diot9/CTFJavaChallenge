package org.springframework.boot.loader.zip;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.springframework.boot.loader.log.DebugLogger;

/* loaded from: agent.jar:org/springframework/boot/loader/zip/ZipLocalFileHeaderRecord.class */
final class ZipLocalFileHeaderRecord extends Record {
    private final short versionNeededToExtract;
    private final short generalPurposeBitFlag;
    private final short compressionMethod;
    private final short lastModFileTime;
    private final short lastModFileDate;
    private final int crc32;
    private final int compressedSize;
    private final int uncompressedSize;
    private final short fileNameLength;
    private final short extraFieldLength;
    private static final DebugLogger debug = DebugLogger.get(ZipLocalFileHeaderRecord.class);
    private static final int SIGNATURE = 67324752;
    private static final int MINIMUM_SIZE = 30;

    ZipLocalFileHeaderRecord(short versionNeededToExtract, short generalPurposeBitFlag, short compressionMethod, short lastModFileTime, short lastModFileDate, int crc32, int compressedSize, int uncompressedSize, short fileNameLength, short extraFieldLength) {
        this.versionNeededToExtract = versionNeededToExtract;
        this.generalPurposeBitFlag = generalPurposeBitFlag;
        this.compressionMethod = compressionMethod;
        this.lastModFileTime = lastModFileTime;
        this.lastModFileDate = lastModFileDate;
        this.crc32 = crc32;
        this.compressedSize = compressedSize;
        this.uncompressedSize = uncompressedSize;
        this.fileNameLength = fileNameLength;
        this.extraFieldLength = extraFieldLength;
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, ZipLocalFileHeaderRecord.class), ZipLocalFileHeaderRecord.class, "versionNeededToExtract;generalPurposeBitFlag;compressionMethod;lastModFileTime;lastModFileDate;crc32;compressedSize;uncompressedSize;fileNameLength;extraFieldLength", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->versionNeededToExtract:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->generalPurposeBitFlag:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->compressionMethod:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->lastModFileTime:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->lastModFileDate:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->crc32:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->compressedSize:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->uncompressedSize:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->fileNameLength:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->extraFieldLength:S").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, ZipLocalFileHeaderRecord.class), ZipLocalFileHeaderRecord.class, "versionNeededToExtract;generalPurposeBitFlag;compressionMethod;lastModFileTime;lastModFileDate;crc32;compressedSize;uncompressedSize;fileNameLength;extraFieldLength", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->versionNeededToExtract:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->generalPurposeBitFlag:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->compressionMethod:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->lastModFileTime:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->lastModFileDate:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->crc32:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->compressedSize:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->uncompressedSize:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->fileNameLength:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->extraFieldLength:S").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, ZipLocalFileHeaderRecord.class, Object.class), ZipLocalFileHeaderRecord.class, "versionNeededToExtract;generalPurposeBitFlag;compressionMethod;lastModFileTime;lastModFileDate;crc32;compressedSize;uncompressedSize;fileNameLength;extraFieldLength", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->versionNeededToExtract:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->generalPurposeBitFlag:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->compressionMethod:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->lastModFileTime:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->lastModFileDate:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->crc32:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->compressedSize:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->uncompressedSize:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->fileNameLength:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipLocalFileHeaderRecord;->extraFieldLength:S").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public short versionNeededToExtract() {
        return this.versionNeededToExtract;
    }

    public short generalPurposeBitFlag() {
        return this.generalPurposeBitFlag;
    }

    public short compressionMethod() {
        return this.compressionMethod;
    }

    public short lastModFileTime() {
        return this.lastModFileTime;
    }

    public short lastModFileDate() {
        return this.lastModFileDate;
    }

    public int crc32() {
        return this.crc32;
    }

    public int compressedSize() {
        return this.compressedSize;
    }

    public int uncompressedSize() {
        return this.uncompressedSize;
    }

    public short fileNameLength() {
        return this.fileNameLength;
    }

    public short extraFieldLength() {
        return this.extraFieldLength;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long size() {
        return 30 + fileNameLength() + extraFieldLength();
    }

    ZipLocalFileHeaderRecord withExtraFieldLength(short extraFieldLength) {
        return new ZipLocalFileHeaderRecord(this.versionNeededToExtract, this.generalPurposeBitFlag, this.compressionMethod, this.lastModFileTime, this.lastModFileDate, this.crc32, this.compressedSize, this.uncompressedSize, this.fileNameLength, extraFieldLength);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ZipLocalFileHeaderRecord withFileNameLength(short fileNameLength) {
        return new ZipLocalFileHeaderRecord(this.versionNeededToExtract, this.generalPurposeBitFlag, this.compressionMethod, this.lastModFileTime, this.lastModFileDate, this.crc32, this.compressedSize, this.uncompressedSize, fileNameLength, this.extraFieldLength);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] asByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(30);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(SIGNATURE);
        buffer.putShort(this.versionNeededToExtract);
        buffer.putShort(this.generalPurposeBitFlag);
        buffer.putShort(this.compressionMethod);
        buffer.putShort(this.lastModFileTime);
        buffer.putShort(this.lastModFileDate);
        buffer.putInt(this.crc32);
        buffer.putInt(this.compressedSize);
        buffer.putInt(this.uncompressedSize);
        buffer.putShort(this.fileNameLength);
        buffer.putShort(this.extraFieldLength);
        return buffer.array();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ZipLocalFileHeaderRecord load(DataBlock dataBlock, long pos) throws IOException {
        debug.log("Loading LocalFileHeaderRecord from position %s", Long.valueOf(pos));
        ByteBuffer buffer = ByteBuffer.allocate(30);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        dataBlock.readFully(buffer, pos);
        buffer.rewind();
        if (buffer.getInt() != SIGNATURE) {
            throw new IOException("Zip 'Local File Header Record' not found at position " + pos);
        }
        return new ZipLocalFileHeaderRecord(buffer.getShort(), buffer.getShort(), buffer.getShort(), buffer.getShort(), buffer.getShort(), buffer.getInt(), buffer.getInt(), buffer.getInt(), buffer.getShort(), buffer.getShort());
    }
}
