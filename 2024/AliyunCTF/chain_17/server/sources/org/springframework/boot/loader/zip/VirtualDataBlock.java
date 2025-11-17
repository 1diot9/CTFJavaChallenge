package org.springframework.boot.loader.zip;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

/* loaded from: server.jar:org/springframework/boot/loader/zip/VirtualDataBlock.class */
class VirtualDataBlock implements DataBlock {
    private List<DataBlock> parts;
    private long size;

    /* JADX INFO: Access modifiers changed from: protected */
    public VirtualDataBlock() {
    }

    VirtualDataBlock(Collection<? extends DataBlock> parts) throws IOException {
        setParts(parts);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setParts(Collection<? extends DataBlock> parts) throws IOException {
        this.parts = List.copyOf(parts);
        long size = 0;
        for (DataBlock part : parts) {
            size += part.size();
        }
        this.size = size;
    }

    @Override // org.springframework.boot.loader.zip.DataBlock
    public long size() throws IOException {
        return this.size;
    }

    @Override // org.springframework.boot.loader.zip.DataBlock
    public int read(ByteBuffer dst, long pos) throws IOException {
        if (pos < 0 || pos >= this.size) {
            return -1;
        }
        long offset = 0;
        int result = 0;
        for (DataBlock part : this.parts) {
            while (pos >= offset && pos < offset + part.size()) {
                int count = part.read(dst, pos - offset);
                result += Math.max(count, 0);
                if (count <= 0 || !dst.hasRemaining()) {
                    return result;
                }
                pos += count;
            }
            offset += part.size();
        }
        return result;
    }
}
