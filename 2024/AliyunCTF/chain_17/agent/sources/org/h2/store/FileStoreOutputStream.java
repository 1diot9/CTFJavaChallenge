package org.h2.store;

import java.io.OutputStream;
import java.util.Arrays;
import org.h2.tools.CompressTool;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/FileStoreOutputStream.class */
public class FileStoreOutputStream extends OutputStream {
    private FileStore store;
    private final Data page;
    private final String compressionAlgorithm;
    private final CompressTool compress;
    private final byte[] buffer = {0};

    public FileStoreOutputStream(FileStore fileStore, String str) {
        this.store = fileStore;
        if (str != null) {
            this.compress = CompressTool.getInstance();
            this.compressionAlgorithm = str;
        } else {
            this.compress = null;
            this.compressionAlgorithm = null;
        }
        this.page = Data.create(16);
    }

    @Override // java.io.OutputStream
    public void write(int i) {
        this.buffer[0] = (byte) i;
        write(this.buffer);
    }

    @Override // java.io.OutputStream
    public void write(byte[] bArr) {
        write(bArr, 0, bArr.length);
    }

    @Override // java.io.OutputStream
    public void write(byte[] bArr, int i, int i2) {
        if (i2 > 0) {
            this.page.reset();
            if (this.compress != null) {
                if (i != 0 || i2 != bArr.length) {
                    bArr = Arrays.copyOfRange(bArr, i, i + i2);
                    i = 0;
                }
                byte[] compress = this.compress.compress(bArr, this.compressionAlgorithm);
                int length = compress.length;
                this.page.checkCapacity(8 + length);
                this.page.writeInt(length);
                this.page.writeInt(i2);
                this.page.write(compress, i, length);
            } else {
                this.page.checkCapacity(4 + i2);
                this.page.writeInt(i2);
                this.page.write(bArr, i, i2);
            }
            this.page.fillAligned();
            this.store.write(this.page.getBytes(), 0, this.page.length());
        }
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        if (this.store != null) {
            try {
                this.store.close();
            } finally {
                this.store = null;
            }
        }
    }
}
