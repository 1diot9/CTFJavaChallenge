package org.h2.store.fs.retry;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import org.h2.store.fs.FileBase;
import org.h2.store.fs.FileUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/retry/FileRetryOnInterrupt.class */
class FileRetryOnInterrupt extends FileBase {
    private final String fileName;
    private final String mode;
    private FileChannel channel;
    private FileLockRetry lock;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileRetryOnInterrupt(String str, String str2) throws IOException {
        this.fileName = str;
        this.mode = str2;
        open();
    }

    private void open() throws IOException {
        this.channel = FileUtils.open(this.fileName, this.mode);
    }

    private void reopen(int i, IOException iOException) throws IOException {
        if (i > 20) {
            throw iOException;
        }
        if (!(iOException instanceof ClosedByInterruptException) && !(iOException instanceof ClosedChannelException)) {
            throw iOException;
        }
        Thread.interrupted();
        FileChannel fileChannel = this.channel;
        synchronized (this) {
            if (fileChannel == this.channel) {
                open();
                reLock();
            }
        }
    }

    private void reLock() throws IOException {
        if (this.lock == null) {
            return;
        }
        try {
            this.lock.base.release();
        } catch (IOException e) {
        }
        FileLock tryLock = this.channel.tryLock(this.lock.position(), this.lock.size(), this.lock.isShared());
        if (tryLock == null) {
            throw new IOException("Re-locking failed");
        }
        this.lock.base = tryLock;
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.spi.AbstractInterruptibleChannel
    public void implCloseChannel() throws IOException {
        try {
            this.channel.close();
        } catch (IOException e) {
        }
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
    public long position() throws IOException {
        int i = 0;
        while (true) {
            try {
                return this.channel.position();
            } catch (IOException e) {
                reopen(i, e);
                i++;
            }
        }
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
    public long size() throws IOException {
        int i = 0;
        while (true) {
            try {
                return this.channel.size();
            } catch (IOException e) {
                reopen(i, e);
                i++;
            }
        }
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel, java.nio.channels.ReadableByteChannel
    public int read(ByteBuffer byteBuffer) throws IOException {
        long position = position();
        int i = 0;
        while (true) {
            try {
                return this.channel.read(byteBuffer);
            } catch (IOException e) {
                reopen(i, e);
                position(position);
                i++;
            }
        }
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public int read(ByteBuffer byteBuffer, long j) throws IOException {
        int i = 0;
        while (true) {
            try {
                return this.channel.read(byteBuffer, j);
            } catch (IOException e) {
                reopen(i, e);
                i++;
            }
        }
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
    public FileChannel position(long j) throws IOException {
        int i = 0;
        while (true) {
            try {
                this.channel.position(j);
                return this;
            } catch (IOException e) {
                reopen(i, e);
                i++;
            }
        }
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
    public FileChannel truncate(long j) throws IOException {
        int i = 0;
        while (true) {
            try {
                this.channel.truncate(j);
                return this;
            } catch (IOException e) {
                reopen(i, e);
                i++;
            }
        }
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public void force(boolean z) throws IOException {
        int i = 0;
        while (true) {
            try {
                this.channel.force(z);
                return;
            } catch (IOException e) {
                reopen(i, e);
                i++;
            }
        }
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel, java.nio.channels.WritableByteChannel
    public int write(ByteBuffer byteBuffer) throws IOException {
        long position = position();
        int i = 0;
        while (true) {
            try {
                return this.channel.write(byteBuffer);
            } catch (IOException e) {
                reopen(i, e);
                position(position);
                i++;
            }
        }
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public int write(ByteBuffer byteBuffer, long j) throws IOException {
        int i = 0;
        while (true) {
            try {
                return this.channel.write(byteBuffer, j);
            } catch (IOException e) {
                reopen(i, e);
                i++;
            }
        }
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public synchronized FileLock tryLock(long j, long j2, boolean z) throws IOException {
        FileLock tryLock = this.channel.tryLock(j, j2, z);
        if (tryLock == null) {
            return null;
        }
        this.lock = new FileLockRetry(tryLock, this);
        return this.lock;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/retry/FileRetryOnInterrupt$FileLockRetry.class */
    public static class FileLockRetry extends FileLock {
        FileLock base;

        protected FileLockRetry(FileLock fileLock, FileChannel fileChannel) {
            super(fileChannel, fileLock.position(), fileLock.size(), fileLock.isShared());
            this.base = fileLock;
        }

        @Override // java.nio.channels.FileLock
        public boolean isValid() {
            return this.base.isValid();
        }

        @Override // java.nio.channels.FileLock
        public void release() throws IOException {
            this.base.release();
        }
    }

    public String toString() {
        return "retry:" + this.fileName;
    }
}
