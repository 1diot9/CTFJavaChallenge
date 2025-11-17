package org.h2.store;

import java.io.IOException;
import java.io.InputStream;
import org.h2.message.DbException;
import org.h2.tools.CompressTool;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/FileStoreInputStream.class */
public class FileStoreInputStream extends InputStream {
    private FileStore store;
    private final Data page;
    private int remainingInBuffer;
    private final CompressTool compress;
    private boolean endOfFile;
    private final boolean alwaysClose;

    public FileStoreInputStream(FileStore fileStore, boolean z, boolean z2) {
        this.store = fileStore;
        this.alwaysClose = z2;
        if (z) {
            this.compress = CompressTool.getInstance();
        } else {
            this.compress = null;
        }
        this.page = Data.create(16);
        try {
            if (fileStore.length() <= 48) {
                close();
            } else {
                fillBuffer();
            }
        } catch (IOException e) {
            throw DbException.convertIOException(e, fileStore.name);
        }
    }

    @Override // java.io.InputStream
    public int available() {
        if (this.remainingInBuffer <= 0) {
            return 0;
        }
        return this.remainingInBuffer;
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr) throws IOException {
        return read(bArr, 0, bArr.length);
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        int readBlock;
        if (i2 == 0) {
            return 0;
        }
        int i3 = 0;
        while (i2 > 0 && (readBlock = readBlock(bArr, i, i2)) >= 0) {
            i3 += readBlock;
            i += readBlock;
            i2 -= readBlock;
        }
        if (i3 == 0) {
            return -1;
        }
        return i3;
    }

    private int readBlock(byte[] bArr, int i, int i2) throws IOException {
        fillBuffer();
        if (this.endOfFile) {
            return -1;
        }
        int min = Math.min(this.remainingInBuffer, i2);
        this.page.read(bArr, i, min);
        this.remainingInBuffer -= min;
        return min;
    }

    private void fillBuffer() throws IOException {
        if (this.remainingInBuffer > 0 || this.endOfFile) {
            return;
        }
        this.page.reset();
        this.store.openFile();
        if (this.store.length() == this.store.getFilePointer()) {
            close();
            return;
        }
        this.store.readFully(this.page.getBytes(), 0, 16);
        this.page.reset();
        this.remainingInBuffer = this.page.readInt();
        if (this.remainingInBuffer < 0) {
            close();
            return;
        }
        this.page.checkCapacity(this.remainingInBuffer);
        if (this.compress != null) {
            this.page.checkCapacity(4);
            this.page.readInt();
        }
        this.page.setPos(this.page.length() + this.remainingInBuffer);
        this.page.fillAligned();
        int length = this.page.length() - 16;
        this.page.reset();
        this.page.readInt();
        this.store.readFully(this.page.getBytes(), 16, length);
        this.page.reset();
        this.page.readInt();
        if (this.compress != null) {
            int readInt = this.page.readInt();
            byte[] newBytes = Utils.newBytes(this.remainingInBuffer);
            this.page.read(newBytes, 0, this.remainingInBuffer);
            this.page.reset();
            this.page.checkCapacity(readInt);
            CompressTool.expand(newBytes, this.page.getBytes(), 0);
            this.remainingInBuffer = readInt;
        }
        if (this.alwaysClose) {
            this.store.closeFile();
        }
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        if (this.store != null) {
            try {
                this.store.close();
                this.endOfFile = true;
            } finally {
                this.store = null;
            }
        }
    }

    protected void finalize() {
        close();
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        fillBuffer();
        if (this.endOfFile) {
            return -1;
        }
        int readByte = this.page.readByte() & 255;
        this.remainingInBuffer--;
        return readByte;
    }
}
