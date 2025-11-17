package org.springframework.boot.loader.nio.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import org.springframework.boot.loader.zip.CloseableDataBlock;
import org.springframework.boot.loader.zip.DataBlock;
import org.springframework.boot.loader.zip.ZipContent;

/* loaded from: server.jar:org/springframework/boot/loader/nio/file/NestedByteChannel.class */
class NestedByteChannel implements SeekableByteChannel {
    private long position;
    private final Resources resources;
    private final Cleaner.Cleanable cleanup;
    private final long size;
    private volatile boolean closed;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NestedByteChannel(Path path, String nestedEntryName) throws IOException {
        this(path, nestedEntryName, org.springframework.boot.loader.ref.Cleaner.instance);
    }

    NestedByteChannel(Path path, String nestedEntryName, org.springframework.boot.loader.ref.Cleaner cleaner) throws IOException {
        this.resources = new Resources(path, nestedEntryName);
        this.cleanup = cleaner.register(this, this.resources);
        this.size = this.resources.getData().size();
    }

    @Override // java.nio.channels.Channel
    public boolean isOpen() {
        return !this.closed;
    }

    @Override // java.nio.channels.Channel, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        try {
            this.cleanup.clean();
        } catch (UncheckedIOException ex) {
            throw ex.getCause();
        }
    }

    @Override // java.nio.channels.SeekableByteChannel, java.nio.channels.ReadableByteChannel
    public int read(ByteBuffer dst) throws IOException {
        assertNotClosed();
        int total = 0;
        while (dst.remaining() > 0) {
            int count = this.resources.getData().read(dst, this.position);
            if (count <= 0) {
                if (total != 0) {
                    return 0;
                }
                return count;
            }
            total += count;
            this.position += count;
        }
        return total;
    }

    @Override // java.nio.channels.SeekableByteChannel, java.nio.channels.WritableByteChannel
    public int write(ByteBuffer src) throws IOException {
        throw new NonWritableChannelException();
    }

    @Override // java.nio.channels.SeekableByteChannel
    public long position() throws IOException {
        assertNotClosed();
        return this.position;
    }

    @Override // java.nio.channels.SeekableByteChannel
    public SeekableByteChannel position(long position) throws IOException {
        assertNotClosed();
        if (position < 0 || position >= this.size) {
            throw new IllegalArgumentException("Position must be in bounds");
        }
        this.position = position;
        return this;
    }

    @Override // java.nio.channels.SeekableByteChannel
    public long size() throws IOException {
        assertNotClosed();
        return this.size;
    }

    @Override // java.nio.channels.SeekableByteChannel
    public SeekableByteChannel truncate(long size) throws IOException {
        throw new NonWritableChannelException();
    }

    private void assertNotClosed() throws ClosedChannelException {
        if (this.closed) {
            throw new ClosedChannelException();
        }
    }

    /* loaded from: server.jar:org/springframework/boot/loader/nio/file/NestedByteChannel$Resources.class */
    static class Resources implements Runnable {
        private final ZipContent zipContent;
        private final CloseableDataBlock data;

        Resources(Path path, String nestedEntryName) throws IOException {
            this.zipContent = ZipContent.open(path, nestedEntryName);
            this.data = this.zipContent.openRawZipData();
        }

        DataBlock getData() {
            return this.data;
        }

        @Override // java.lang.Runnable
        public void run() {
            releaseAll();
        }

        private void releaseAll() {
            IOException exception = null;
            try {
                this.data.close();
            } catch (IOException ex) {
                exception = ex;
            }
            try {
                this.zipContent.close();
            } catch (IOException ex2) {
                if (exception != null) {
                    ex2.addSuppressed(exception);
                }
                exception = ex2;
            }
            if (exception != null) {
                throw new UncheckedIOException(exception);
            }
        }
    }
}
