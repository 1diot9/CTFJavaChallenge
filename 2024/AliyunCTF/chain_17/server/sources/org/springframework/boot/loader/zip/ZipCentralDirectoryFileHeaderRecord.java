package org.springframework.boot.loader.zip;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.ValueRange;
import java.util.zip.ZipEntry;
import org.springframework.asm.Opcodes;
import org.springframework.boot.loader.log.DebugLogger;

/* loaded from: server.jar:org/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord.class */
final class ZipCentralDirectoryFileHeaderRecord extends Record {
    private final short versionMadeBy;
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
    private final short fileCommentLength;
    private final short diskNumberStart;
    private final short internalFileAttributes;
    private final int externalFileAttributes;
    private final int offsetToLocalHeader;
    private static final DebugLogger debug = DebugLogger.get(ZipCentralDirectoryFileHeaderRecord.class);
    private static final int SIGNATURE = 33639248;
    private static final int MINIMUM_SIZE = 46;
    static final int FILE_NAME_OFFSET = 46;

    ZipCentralDirectoryFileHeaderRecord(short versionMadeBy, short versionNeededToExtract, short generalPurposeBitFlag, short compressionMethod, short lastModFileTime, short lastModFileDate, int crc32, int compressedSize, int uncompressedSize, short fileNameLength, short extraFieldLength, short fileCommentLength, short diskNumberStart, short internalFileAttributes, int externalFileAttributes, int offsetToLocalHeader) {
        this.versionMadeBy = versionMadeBy;
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
        this.fileCommentLength = fileCommentLength;
        this.diskNumberStart = diskNumberStart;
        this.internalFileAttributes = internalFileAttributes;
        this.externalFileAttributes = externalFileAttributes;
        this.offsetToLocalHeader = offsetToLocalHeader;
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, ZipCentralDirectoryFileHeaderRecord.class), ZipCentralDirectoryFileHeaderRecord.class, "versionMadeBy;versionNeededToExtract;generalPurposeBitFlag;compressionMethod;lastModFileTime;lastModFileDate;crc32;compressedSize;uncompressedSize;fileNameLength;extraFieldLength;fileCommentLength;diskNumberStart;internalFileAttributes;externalFileAttributes;offsetToLocalHeader", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->versionMadeBy:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->versionNeededToExtract:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->generalPurposeBitFlag:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->compressionMethod:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->lastModFileTime:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->lastModFileDate:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->crc32:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->compressedSize:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->uncompressedSize:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->fileNameLength:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->extraFieldLength:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->fileCommentLength:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->diskNumberStart:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->internalFileAttributes:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->externalFileAttributes:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->offsetToLocalHeader:I").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, ZipCentralDirectoryFileHeaderRecord.class), ZipCentralDirectoryFileHeaderRecord.class, "versionMadeBy;versionNeededToExtract;generalPurposeBitFlag;compressionMethod;lastModFileTime;lastModFileDate;crc32;compressedSize;uncompressedSize;fileNameLength;extraFieldLength;fileCommentLength;diskNumberStart;internalFileAttributes;externalFileAttributes;offsetToLocalHeader", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->versionMadeBy:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->versionNeededToExtract:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->generalPurposeBitFlag:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->compressionMethod:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->lastModFileTime:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->lastModFileDate:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->crc32:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->compressedSize:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->uncompressedSize:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->fileNameLength:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->extraFieldLength:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->fileCommentLength:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->diskNumberStart:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->internalFileAttributes:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->externalFileAttributes:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->offsetToLocalHeader:I").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, ZipCentralDirectoryFileHeaderRecord.class, Object.class), ZipCentralDirectoryFileHeaderRecord.class, "versionMadeBy;versionNeededToExtract;generalPurposeBitFlag;compressionMethod;lastModFileTime;lastModFileDate;crc32;compressedSize;uncompressedSize;fileNameLength;extraFieldLength;fileCommentLength;diskNumberStart;internalFileAttributes;externalFileAttributes;offsetToLocalHeader", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->versionMadeBy:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->versionNeededToExtract:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->generalPurposeBitFlag:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->compressionMethod:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->lastModFileTime:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->lastModFileDate:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->crc32:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->compressedSize:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->uncompressedSize:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->fileNameLength:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->extraFieldLength:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->fileCommentLength:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->diskNumberStart:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->internalFileAttributes:S", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->externalFileAttributes:I", "FIELD:Lorg/springframework/boot/loader/zip/ZipCentralDirectoryFileHeaderRecord;->offsetToLocalHeader:I").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public short versionMadeBy() {
        return this.versionMadeBy;
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

    public short fileCommentLength() {
        return this.fileCommentLength;
    }

    public short diskNumberStart() {
        return this.diskNumberStart;
    }

    public short internalFileAttributes() {
        return this.internalFileAttributes;
    }

    public int externalFileAttributes() {
        return this.externalFileAttributes;
    }

    public int offsetToLocalHeader() {
        return this.offsetToLocalHeader;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long size() {
        return 46 + fileNameLength() + extraFieldLength() + fileCommentLength();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void copyTo(DataBlock dataBlock, long pos, ZipEntry zipEntry) throws IOException {
        int fileNameLength = Short.toUnsignedInt(fileNameLength());
        int extraLength = Short.toUnsignedInt(extraFieldLength());
        int commentLength = Short.toUnsignedInt(fileCommentLength());
        zipEntry.setMethod(Short.toUnsignedInt(compressionMethod()));
        zipEntry.setTime(decodeMsDosFormatDateTime(lastModFileDate(), lastModFileTime()));
        zipEntry.setCrc(Integer.toUnsignedLong(crc32()));
        zipEntry.setCompressedSize(Integer.toUnsignedLong(compressedSize()));
        zipEntry.setSize(Integer.toUnsignedLong(uncompressedSize()));
        if (extraLength > 0) {
            long extraPos = pos + 46 + fileNameLength;
            ByteBuffer buffer = ByteBuffer.allocate(extraLength);
            dataBlock.readFully(buffer, extraPos);
            zipEntry.setExtra(buffer.array());
        }
        if (commentLength > 0) {
            long commentPos = pos + 46 + fileNameLength + extraLength;
            zipEntry.setComment(ZipString.readString(dataBlock, commentPos, commentLength));
        }
    }

    private long decodeMsDosFormatDateTime(short date, short time) {
        int year = getChronoValue(((date >> 9) & Opcodes.LAND) + 1980, ChronoField.YEAR);
        int month = getChronoValue((date >> 5) & 15, ChronoField.MONTH_OF_YEAR);
        int day = getChronoValue(date & 31, ChronoField.DAY_OF_MONTH);
        int hour = getChronoValue((time >> 11) & 31, ChronoField.HOUR_OF_DAY);
        int minute = getChronoValue((time >> 5) & 63, ChronoField.MINUTE_OF_HOUR);
        int second = getChronoValue((time << 1) & 62, ChronoField.SECOND_OF_MINUTE);
        return ZonedDateTime.of(year, month, day, hour, minute, second, 0, ZoneId.systemDefault()).toInstant().truncatedTo(ChronoUnit.SECONDS).toEpochMilli();
    }

    private static int getChronoValue(long value, ChronoField field) {
        ValueRange range = field.range();
        return Math.toIntExact(Math.min(Math.max(value, range.getMinimum()), range.getMaximum()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ZipCentralDirectoryFileHeaderRecord withFileNameLength(short fileNameLength) {
        return this.fileNameLength != fileNameLength ? new ZipCentralDirectoryFileHeaderRecord(this.versionMadeBy, this.versionNeededToExtract, this.generalPurposeBitFlag, this.compressionMethod, this.lastModFileTime, this.lastModFileDate, this.crc32, this.compressedSize, this.uncompressedSize, fileNameLength, this.extraFieldLength, this.fileCommentLength, this.diskNumberStart, this.internalFileAttributes, this.externalFileAttributes, this.offsetToLocalHeader) : this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ZipCentralDirectoryFileHeaderRecord withOffsetToLocalHeader(int offsetToLocalHeader) {
        return this.offsetToLocalHeader != offsetToLocalHeader ? new ZipCentralDirectoryFileHeaderRecord(this.versionMadeBy, this.versionNeededToExtract, this.generalPurposeBitFlag, this.compressionMethod, this.lastModFileTime, this.lastModFileDate, this.crc32, this.compressedSize, this.uncompressedSize, this.fileNameLength, this.extraFieldLength, this.fileCommentLength, this.diskNumberStart, this.internalFileAttributes, this.externalFileAttributes, offsetToLocalHeader) : this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] asByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(46);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(SIGNATURE);
        buffer.putShort(this.versionMadeBy);
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
        buffer.putShort(this.fileCommentLength);
        buffer.putShort(this.diskNumberStart);
        buffer.putShort(this.internalFileAttributes);
        buffer.putInt(this.externalFileAttributes);
        buffer.putInt(this.offsetToLocalHeader);
        return buffer.array();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ZipCentralDirectoryFileHeaderRecord load(DataBlock dataBlock, long pos) throws IOException {
        debug.log("Loading CentralDirectoryFileHeaderRecord from position %s", Long.valueOf(pos));
        ByteBuffer buffer = ByteBuffer.allocate(46);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        dataBlock.readFully(buffer, pos);
        buffer.rewind();
        int signature = buffer.getInt();
        if (signature != SIGNATURE) {
            debug.log("Found incorrect CentralDirectoryFileHeaderRecord signature %s at position %s", Integer.valueOf(signature), Long.valueOf(pos));
            throw new IOException("Zip 'Central Directory File Header Record' not found at position " + pos);
        }
        return new ZipCentralDirectoryFileHeaderRecord(buffer.getShort(), buffer.getShort(), buffer.getShort(), buffer.getShort(), buffer.getShort(), buffer.getShort(), buffer.getInt(), buffer.getInt(), buffer.getInt(), buffer.getShort(), buffer.getShort(), buffer.getShort(), buffer.getShort(), buffer.getShort(), buffer.getInt(), buffer.getInt());
    }
}
