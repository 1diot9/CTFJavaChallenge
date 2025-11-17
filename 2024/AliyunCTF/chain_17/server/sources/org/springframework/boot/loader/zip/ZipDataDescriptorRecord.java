package org.springframework.boot.loader.zip;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.springframework.boot.loader.log.DebugLogger;

/* loaded from: server.jar:org/springframework/boot/loader/zip/ZipDataDescriptorRecord.class */
final class ZipDataDescriptorRecord extends Record {
    private final boolean includeSignature;
    private final int crc32;
    private final int compressedSize;
    private final int uncompressedSize;
    private static final DebugLogger debug = DebugLogger.get(ZipDataDescriptorRecord.class);
    private static final int SIGNATURE = 134695760;
    private static final int DATA_SIZE = 12;
    private static final int SIGNATURE_SIZE = 4;

    ZipDataDescriptorRecord(boolean includeSignature, int crc32, int compressedSize, int uncompressedSize) {
        this.includeSignature = includeSignature;
        this.crc32 = crc32;
        this.compressedSize = compressedSize;
        this.uncompressedSize = uncompressedSize;
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, ZipDataDescriptorRecord.class), ZipDataDescriptorRecord.class, "includeSignature;crc32;compressedSize;uncompressedSize", "FIELD:Lorg/springframework/boot/loader/zip/ZipDataDescriptorRecord;->includeSignature:Z", "FIELD:Lorg/springframework/boot/loader/zip/ZipDataDescriptorRecord;->crc32:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipDataDescriptorRecord;->compressedSize:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipDataDescriptorRecord;->uncompressedSize:I").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, ZipDataDescriptorRecord.class), ZipDataDescriptorRecord.class, "includeSignature;crc32;compressedSize;uncompressedSize", "FIELD:Lorg/springframework/boot/loader/zip/ZipDataDescriptorRecord;->includeSignature:Z", "FIELD:Lorg/springframework/boot/loader/zip/ZipDataDescriptorRecord;->crc32:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipDataDescriptorRecord;->compressedSize:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipDataDescriptorRecord;->uncompressedSize:I").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, ZipDataDescriptorRecord.class, Object.class), ZipDataDescriptorRecord.class, "includeSignature;crc32;compressedSize;uncompressedSize", "FIELD:Lorg/springframework/boot/loader/zip/ZipDataDescriptorRecord;->includeSignature:Z", "FIELD:Lorg/springframework/boot/loader/zip/ZipDataDescriptorRecord;->crc32:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipDataDescriptorRecord;->compressedSize:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipDataDescriptorRecord;->uncompressedSize:I").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public boolean includeSignature() {
        return this.includeSignature;
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

    /* JADX INFO: Access modifiers changed from: package-private */
    public long size() {
        return !includeSignature() ? 12L : 16L;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] asByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate((int) size());
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        if (this.includeSignature) {
            buffer.putInt(SIGNATURE);
        }
        buffer.putInt(this.crc32);
        buffer.putInt(this.compressedSize);
        buffer.putInt(this.uncompressedSize);
        return buffer.array();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ZipDataDescriptorRecord load(DataBlock dataBlock, long pos) throws IOException {
        debug.log("Loading ZipDataDescriptorRecord from position %s", Long.valueOf(pos));
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(4);
        dataBlock.readFully(buffer, pos);
        buffer.rewind();
        int signatureOrCrc = buffer.getInt();
        boolean hasSignature = signatureOrCrc == SIGNATURE;
        buffer.rewind();
        buffer.limit(!hasSignature ? 8 : 12);
        dataBlock.readFully(buffer, pos + 4);
        buffer.rewind();
        return new ZipDataDescriptorRecord(hasSignature, !hasSignature ? signatureOrCrc : buffer.getInt(), buffer.getInt(), buffer.getInt());
    }

    static boolean isPresentBasedOnFlag(ZipLocalFileHeaderRecord localRecord) {
        return isPresentBasedOnFlag(localRecord.generalPurposeBitFlag());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isPresentBasedOnFlag(ZipCentralDirectoryFileHeaderRecord centralRecord) {
        return isPresentBasedOnFlag(centralRecord.generalPurposeBitFlag());
    }

    static boolean isPresentBasedOnFlag(int generalPurposeBitFlag) {
        return (generalPurposeBitFlag & 8) != 0;
    }
}
