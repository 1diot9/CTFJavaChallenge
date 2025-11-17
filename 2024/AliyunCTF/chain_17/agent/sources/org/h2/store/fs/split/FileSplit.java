package org.h2.store.fs.split;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import org.h2.message.DbException;
import org.h2.mvstore.DataUtils;
import org.h2.store.fs.FileBaseDefault;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/split/FileSplit.class */
class FileSplit extends FileBaseDefault {
    private final FilePathSplit filePath;
    private final String mode;
    private final long maxLength;
    private FileChannel[] list;
    private volatile long length;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileSplit(FilePathSplit filePathSplit, String str, FileChannel[] fileChannelArr, long j, long j2) {
        this.filePath = filePathSplit;
        this.mode = str;
        this.list = fileChannelArr;
        this.length = j;
        this.maxLength = j2;
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.spi.AbstractInterruptibleChannel
    public synchronized void implCloseChannel() throws IOException {
        for (FileChannel fileChannel : this.list) {
            fileChannel.close();
        }
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
    public long size() {
        return this.length;
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public synchronized int read(ByteBuffer byteBuffer, long j) throws IOException {
        int remaining = byteBuffer.remaining();
        if (remaining == 0) {
            return 0;
        }
        int min = (int) Math.min(remaining, this.length - j);
        if (min <= 0) {
            return -1;
        }
        long j2 = j % this.maxLength;
        return getFileChannel(j).read(byteBuffer, j2);
    }

    private FileChannel getFileChannel(long j) throws IOException {
        int i = (int) (j / this.maxLength);
        while (i >= this.list.length) {
            int length = this.list.length;
            FileChannel[] fileChannelArr = new FileChannel[length + 1];
            System.arraycopy(this.list, 0, fileChannelArr, 0, length);
            fileChannelArr[length] = this.filePath.getBase(length).open(this.mode);
            this.list = fileChannelArr;
        }
        return this.list[i];
    }

    @Override // org.h2.store.fs.FileBaseDefault
    protected void implTruncate(long j) throws IOException {
        if (j >= this.length) {
            return;
        }
        int i = 1 + ((int) (j / this.maxLength));
        if (i < this.list.length) {
            FileChannel[] fileChannelArr = new FileChannel[i];
            for (int length = this.list.length - 1; length >= i; length--) {
                this.list[length].truncate(0L);
                this.list[length].close();
                try {
                    this.filePath.getBase(length).delete();
                } catch (DbException e) {
                    throw DataUtils.convertToIOException(e);
                }
            }
            System.arraycopy(this.list, 0, fileChannelArr, 0, fileChannelArr.length);
            this.list = fileChannelArr;
        }
        this.list[this.list.length - 1].truncate(j - (this.maxLength * (i - 1)));
        this.length = j;
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public synchronized void force(boolean z) throws IOException {
        for (FileChannel fileChannel : this.list) {
            fileChannel.force(z);
        }
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public synchronized int write(ByteBuffer byteBuffer, long j) throws IOException {
        int write;
        if (j >= this.length && j > this.maxLength) {
            long j2 = this.length - (this.length % this.maxLength);
            long j3 = this.maxLength;
            while (true) {
                long j4 = j2 + j3;
                if (j4 >= j) {
                    break;
                }
                if (j4 > this.length) {
                    position(j4 - 1);
                    write(ByteBuffer.wrap(new byte[1]));
                }
                j = j;
                j2 = j4;
                j3 = this.maxLength;
            }
        }
        long j5 = j % this.maxLength;
        int remaining = byteBuffer.remaining();
        FileChannel fileChannel = getFileChannel(j);
        int min = (int) Math.min(remaining, this.maxLength - j5);
        if (min == remaining) {
            write = fileChannel.write(byteBuffer, j5);
        } else {
            int limit = byteBuffer.limit();
            byteBuffer.limit(byteBuffer.position() + min);
            write = fileChannel.write(byteBuffer, j5);
            byteBuffer.limit(limit);
        }
        this.length = Math.max(this.length, j + write);
        return write;
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public synchronized FileLock tryLock(long j, long j2, boolean z) throws IOException {
        return this.list[0].tryLock(j, j2, z);
    }

    public String toString() {
        return this.filePath.toString();
    }
}
