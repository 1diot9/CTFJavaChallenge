package cn.hutool.core.io.copy;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.lang.Assert;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/copy/ChannelCopier.class */
public class ChannelCopier extends IoCopier<ReadableByteChannel, WritableByteChannel> {
    public ChannelCopier() {
        this(8192);
    }

    public ChannelCopier(int bufferSize) {
        this(bufferSize, -1L);
    }

    public ChannelCopier(int bufferSize, long count) {
        this(bufferSize, count, null);
    }

    public ChannelCopier(int bufferSize, long count, StreamProgress progress) {
        super(bufferSize, count, progress);
    }

    @Override // cn.hutool.core.io.copy.IoCopier
    public long copy(ReadableByteChannel source, WritableByteChannel target) {
        Assert.notNull(source, "InputStream is null !", new Object[0]);
        Assert.notNull(target, "OutputStream is null !", new Object[0]);
        StreamProgress progress = this.progress;
        if (null != progress) {
            progress.start();
        }
        try {
            long size = doCopy(source, target, ByteBuffer.allocate(bufferSize(this.count)), progress);
            if (null != progress) {
                progress.finish();
            }
            return size;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private long doCopy(ReadableByteChannel source, WritableByteChannel target, ByteBuffer buffer, StreamProgress progress) throws IOException {
        int read;
        long numToRead = this.count > 0 ? this.count : Long.MAX_VALUE;
        long total = 0;
        while (numToRead > 0 && (read = source.read(buffer)) >= 0) {
            buffer.flip();
            target.write(buffer);
            buffer.clear();
            numToRead -= read;
            total += read;
            if (null != progress) {
                progress.progress(this.count, total);
            }
        }
        return total;
    }
}
