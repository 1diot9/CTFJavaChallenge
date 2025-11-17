package org.springframework.boot.loader.zip;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.springframework.boot.loader.log.DebugLogger;

/* loaded from: server.jar:org/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryLocator.class */
final class Zip64EndOfCentralDirectoryLocator extends Record {
    private final long pos;
    private final int numberOfThisDisk;
    private final long offsetToZip64EndOfCentralDirectoryRecord;
    private final int totalNumberOfDisks;
    private static final DebugLogger debug = DebugLogger.get(Zip64EndOfCentralDirectoryLocator.class);
    private static final int SIGNATURE = 117853008;
    static final int SIZE = 20;

    Zip64EndOfCentralDirectoryLocator(long pos, int numberOfThisDisk, long offsetToZip64EndOfCentralDirectoryRecord, int totalNumberOfDisks) {
        this.pos = pos;
        this.numberOfThisDisk = numberOfThisDisk;
        this.offsetToZip64EndOfCentralDirectoryRecord = offsetToZip64EndOfCentralDirectoryRecord;
        this.totalNumberOfDisks = totalNumberOfDisks;
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Zip64EndOfCentralDirectoryLocator.class), Zip64EndOfCentralDirectoryLocator.class, "pos;numberOfThisDisk;offsetToZip64EndOfCentralDirectoryRecord;totalNumberOfDisks", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryLocator;->pos:J", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryLocator;->numberOfThisDisk:I", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryLocator;->offsetToZip64EndOfCentralDirectoryRecord:J", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryLocator;->totalNumberOfDisks:I").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Zip64EndOfCentralDirectoryLocator.class), Zip64EndOfCentralDirectoryLocator.class, "pos;numberOfThisDisk;offsetToZip64EndOfCentralDirectoryRecord;totalNumberOfDisks", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryLocator;->pos:J", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryLocator;->numberOfThisDisk:I", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryLocator;->offsetToZip64EndOfCentralDirectoryRecord:J", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryLocator;->totalNumberOfDisks:I").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Zip64EndOfCentralDirectoryLocator.class, Object.class), Zip64EndOfCentralDirectoryLocator.class, "pos;numberOfThisDisk;offsetToZip64EndOfCentralDirectoryRecord;totalNumberOfDisks", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryLocator;->pos:J", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryLocator;->numberOfThisDisk:I", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryLocator;->offsetToZip64EndOfCentralDirectoryRecord:J", "FIELD:Lorg/springframework/boot/loader/zip/Zip64EndOfCentralDirectoryLocator;->totalNumberOfDisks:I").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public long pos() {
        return this.pos;
    }

    public int numberOfThisDisk() {
        return this.numberOfThisDisk;
    }

    public long offsetToZip64EndOfCentralDirectoryRecord() {
        return this.offsetToZip64EndOfCentralDirectoryRecord;
    }

    public int totalNumberOfDisks() {
        return this.totalNumberOfDisks;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Zip64EndOfCentralDirectoryLocator find(DataBlock dataBlock, long endOfCentralDirectoryPos) throws IOException {
        debug.log("Finding Zip64EndOfCentralDirectoryLocator from EOCD at %s", Long.valueOf(endOfCentralDirectoryPos));
        long pos = endOfCentralDirectoryPos - 20;
        if (pos < 0) {
            debug.log("No Zip64EndOfCentralDirectoryLocator due to negative position %s", Long.valueOf(pos));
            return null;
        }
        ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        dataBlock.read(buffer, pos);
        buffer.rewind();
        int signature = buffer.getInt();
        if (signature != SIGNATURE) {
            debug.log("Found incorrect Zip64EndOfCentralDirectoryLocator signature %s at position %s", Integer.valueOf(signature), Long.valueOf(pos));
            return null;
        }
        debug.log("Found Zip64EndOfCentralDirectoryLocator at position %s", Long.valueOf(pos));
        return new Zip64EndOfCentralDirectoryLocator(pos, buffer.getInt(), buffer.getLong(), buffer.getInt());
    }
}
