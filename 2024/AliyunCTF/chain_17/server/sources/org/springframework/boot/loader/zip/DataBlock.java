package org.springframework.boot.loader.zip;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/* loaded from: server.jar:org/springframework/boot/loader/zip/DataBlock.class */
public interface DataBlock {
    long size() throws IOException;

    int read(ByteBuffer dst, long pos) throws IOException;

    default void readFully(ByteBuffer dst, long pos) throws IOException {
        do {
            int count = read(dst, pos);
            if (count <= 0) {
                throw new EOFException();
            }
            pos += count;
        } while (dst.hasRemaining());
    }

    default InputStream asInputStream() throws IOException {
        return new DataBlockInputStream(this);
    }
}
