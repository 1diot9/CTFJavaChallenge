package org.h2.store.fs.async;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.h2.store.fs.FileBaseDefault;
import org.h2.store.fs.FileUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/async/FileAsync.class */
class FileAsync extends FileBaseDefault {
    private final String name;
    private final AsynchronousFileChannel channel;

    private static <T> T complete(Future<T> future) throws IOException {
        T t;
        boolean z = false;
        while (true) {
            try {
                boolean z2 = z;
                t = future.get();
                if (!z2) {
                    break;
                }
                Thread.currentThread().interrupt();
                break;
            } catch (InterruptedException e) {
                z = true;
            } catch (ExecutionException e2) {
                throw new IOException(e2.getCause());
            }
        }
        return t;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileAsync(String str, String str2) throws IOException {
        this.name = str;
        this.channel = AsynchronousFileChannel.open(Paths.get(str, new String[0]), FileUtils.modeToOptions(str2), null, FileUtils.NO_ATTRIBUTES);
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.spi.AbstractInterruptibleChannel
    public void implCloseChannel() throws IOException {
        this.channel.close();
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
    public long size() throws IOException {
        return this.channel.size();
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public int read(ByteBuffer byteBuffer, long j) throws IOException {
        return ((Integer) complete(this.channel.read(byteBuffer, j))).intValue();
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public int write(ByteBuffer byteBuffer, long j) throws IOException {
        return ((Integer) complete(this.channel.write(byteBuffer, j))).intValue();
    }

    @Override // org.h2.store.fs.FileBaseDefault
    protected void implTruncate(long j) throws IOException {
        this.channel.truncate(j);
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public void force(boolean z) throws IOException {
        this.channel.force(z);
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public FileLock tryLock(long j, long j2, boolean z) throws IOException {
        return this.channel.tryLock(j, j2, z);
    }

    public String toString() {
        return "async:" + this.name;
    }
}
