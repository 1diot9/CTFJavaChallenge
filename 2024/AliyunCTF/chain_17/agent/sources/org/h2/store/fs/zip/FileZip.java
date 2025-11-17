package org.h2.store.fs.zip;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.NonWritableChannelException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.h2.store.fs.FakeFileChannel;
import org.h2.store.fs.FileBase;
import org.h2.util.IOUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/zip/FileZip.class */
class FileZip extends FileBase {
    private static final byte[] SKIP_BUFFER = new byte[1024];
    private final ZipFile file;
    private final ZipEntry entry;
    private long pos;
    private InputStream in;
    private long inPos;
    private final long length;
    private boolean skipUsingRead;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileZip(ZipFile zipFile, ZipEntry zipEntry) {
        this.file = zipFile;
        this.entry = zipEntry;
        this.length = zipEntry.getSize();
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
    public long position() {
        return this.pos;
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
    public long size() {
        return this.length;
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel, java.nio.channels.ReadableByteChannel
    public int read(ByteBuffer byteBuffer) throws IOException {
        seek();
        int read = this.in.read(byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining());
        if (read > 0) {
            byteBuffer.position(byteBuffer.position() + read);
            this.pos += read;
            this.inPos += read;
        }
        return read;
    }

    private void seek() throws IOException {
        if (this.inPos > this.pos) {
            if (this.in != null) {
                this.in.close();
            }
            this.in = null;
        }
        if (this.in == null) {
            this.in = this.file.getInputStream(this.entry);
            this.inPos = 0L;
        }
        if (this.inPos < this.pos) {
            long j = this.pos - this.inPos;
            if (!this.skipUsingRead) {
                try {
                    IOUtils.skipFully(this.in, j);
                } catch (NullPointerException e) {
                    this.skipUsingRead = true;
                }
            }
            if (this.skipUsingRead) {
                while (j > 0) {
                    j -= this.in.read(SKIP_BUFFER, 0, (int) Math.min(SKIP_BUFFER.length, j));
                }
            }
            this.inPos = this.pos;
        }
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
    public FileChannel position(long j) {
        this.pos = j;
        return this;
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
    public FileChannel truncate(long j) throws IOException {
        throw new IOException("File is read-only");
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public void force(boolean z) throws IOException {
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel, java.nio.channels.WritableByteChannel
    public int write(ByteBuffer byteBuffer) throws IOException {
        throw new NonWritableChannelException();
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public synchronized FileLock tryLock(long j, long j2, boolean z) throws IOException {
        if (z) {
            return new FileLock(FakeFileChannel.INSTANCE, j, j2, z) { // from class: org.h2.store.fs.zip.FileZip.1
                @Override // java.nio.channels.FileLock
                public boolean isValid() {
                    return true;
                }

                @Override // java.nio.channels.FileLock
                public void release() throws IOException {
                }
            };
        }
        return null;
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.spi.AbstractInterruptibleChannel
    protected void implCloseChannel() throws IOException {
        if (this.in != null) {
            this.in.close();
            this.in = null;
        }
        this.file.close();
    }
}
