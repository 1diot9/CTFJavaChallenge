package org.springframework.boot.loader.zip;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:org/springframework/boot/loader/zip/VirtualZipDataBlock.class */
class VirtualZipDataBlock extends VirtualDataBlock implements CloseableDataBlock {
    private final CloseableDataBlock data;

    /* JADX INFO: Access modifiers changed from: package-private */
    public VirtualZipDataBlock(CloseableDataBlock data, NameOffsetLookups nameOffsetLookups, ZipCentralDirectoryFileHeaderRecord[] centralRecords, long[] centralRecordPositions) throws IOException {
        this.data = data;
        List<DataBlock> parts = new ArrayList<>();
        List<DataBlock> centralParts = new ArrayList<>();
        long offset = 0;
        long sizeOfCentralDirectory = 0;
        for (int i = 0; i < centralRecords.length; i++) {
            ZipCentralDirectoryFileHeaderRecord centralRecord = centralRecords[i];
            int nameOffset = nameOffsetLookups.get(i);
            long centralRecordPos = centralRecordPositions[i];
            DataBlock name = new DataPart(centralRecordPos + 46 + nameOffset, Short.toUnsignedLong(centralRecord.fileNameLength()) - nameOffset);
            long localRecordPos = Integer.toUnsignedLong(centralRecord.offsetToLocalHeader());
            ZipLocalFileHeaderRecord localRecord = ZipLocalFileHeaderRecord.load(this.data, localRecordPos);
            DataBlock content = new DataPart(localRecordPos + localRecord.size(), centralRecord.compressedSize());
            boolean hasDescriptorRecord = ZipDataDescriptorRecord.isPresentBasedOnFlag(centralRecord);
            ZipDataDescriptorRecord dataDescriptorRecord = !hasDescriptorRecord ? null : ZipDataDescriptorRecord.load(data, localRecordPos + localRecord.size() + content.size());
            sizeOfCentralDirectory += addToCentral(centralParts, centralRecord, centralRecordPos, name, (int) offset);
            offset += addToLocal(parts, centralRecord, localRecord, dataDescriptorRecord, name, content);
        }
        parts.addAll(centralParts);
        ZipEndOfCentralDirectoryRecord eocd = new ZipEndOfCentralDirectoryRecord((short) centralRecords.length, (int) sizeOfCentralDirectory, (int) offset);
        parts.add(new ByteArrayDataBlock(eocd.asByteArray()));
        setParts(parts);
    }

    private long addToCentral(List<DataBlock> parts, ZipCentralDirectoryFileHeaderRecord originalRecord, long originalRecordPos, DataBlock name, int offsetToLocalHeader) throws IOException {
        ZipCentralDirectoryFileHeaderRecord record = originalRecord.withFileNameLength((short) (name.size() & 65535)).withOffsetToLocalHeader(offsetToLocalHeader);
        int originalExtraFieldLength = Short.toUnsignedInt(originalRecord.extraFieldLength());
        int originalFileCommentLength = Short.toUnsignedInt(originalRecord.fileCommentLength());
        DataBlock extraFieldAndComment = new DataPart(((originalRecordPos + originalRecord.size()) - originalExtraFieldLength) - originalFileCommentLength, originalExtraFieldLength + originalFileCommentLength);
        parts.add(new ByteArrayDataBlock(record.asByteArray()));
        parts.add(name);
        parts.add(extraFieldAndComment);
        return record.size();
    }

    private long addToLocal(List<DataBlock> parts, ZipCentralDirectoryFileHeaderRecord centralRecord, ZipLocalFileHeaderRecord originalRecord, ZipDataDescriptorRecord dataDescriptorRecord, DataBlock name, DataBlock content) throws IOException {
        ZipLocalFileHeaderRecord record = originalRecord.withFileNameLength((short) (name.size() & 65535));
        long originalRecordPos = Integer.toUnsignedLong(centralRecord.offsetToLocalHeader());
        int extraFieldLength = Short.toUnsignedInt(originalRecord.extraFieldLength());
        parts.add(new ByteArrayDataBlock(record.asByteArray()));
        parts.add(name);
        parts.add(new DataPart((originalRecordPos + originalRecord.size()) - extraFieldLength, extraFieldLength));
        parts.add(content);
        if (dataDescriptorRecord != null) {
            parts.add(new ByteArrayDataBlock(dataDescriptorRecord.asByteArray()));
        }
        return record.size() + content.size() + (dataDescriptorRecord != null ? dataDescriptorRecord.size() : 0L);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.data.close();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:org/springframework/boot/loader/zip/VirtualZipDataBlock$DataPart.class */
    public final class DataPart implements DataBlock {
        private final long offset;
        private final long size;

        DataPart(long offset, long size) {
            this.offset = offset;
            this.size = size;
        }

        @Override // org.springframework.boot.loader.zip.DataBlock
        public long size() throws IOException {
            return this.size;
        }

        @Override // org.springframework.boot.loader.zip.DataBlock
        public int read(ByteBuffer dst, long pos) throws IOException {
            int remaining = (int) (this.size - pos);
            if (remaining <= 0) {
                return -1;
            }
            int originalLimit = -1;
            if (dst.remaining() > remaining) {
                originalLimit = dst.limit();
                dst.limit(dst.position() + remaining);
            }
            int result = VirtualZipDataBlock.this.data.read(dst, this.offset + pos);
            if (originalLimit != -1) {
                dst.limit(originalLimit);
            }
            return result;
        }
    }
}
