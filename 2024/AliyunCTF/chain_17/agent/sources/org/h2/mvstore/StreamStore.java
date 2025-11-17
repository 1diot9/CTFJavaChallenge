package org.h2.mvstore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntConsumer;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/StreamStore.class */
public final class StreamStore {
    private final Map<Long, byte[]> map;
    private final int minBlockSize;
    private final int maxBlockSize;
    private final AtomicLong nextKey;
    private final AtomicReference<byte[]> nextBuffer;
    private final IntConsumer onStoreCallback;

    public StreamStore(Map<Long, byte[]> map) {
        this(map, 256, 262144, null);
    }

    public StreamStore(Map<Long, byte[]> map, int i, int i2) {
        this(map, i, i2, null);
    }

    public StreamStore(Map<Long, byte[]> map, IntConsumer intConsumer) {
        this(map, 256, 262144, intConsumer);
    }

    public StreamStore(Map<Long, byte[]> map, int i, int i2, IntConsumer intConsumer) {
        this.nextKey = new AtomicLong();
        this.nextBuffer = new AtomicReference<>();
        this.map = map;
        this.minBlockSize = i;
        this.maxBlockSize = i2;
        this.onStoreCallback = intConsumer;
    }

    public Map<Long, byte[]> getMap() {
        return this.map;
    }

    public void setNextKey(long j) {
        this.nextKey.set(j);
    }

    public long getNextKey() {
        return this.nextKey.get();
    }

    public int getMinBlockSize() {
        return this.minBlockSize;
    }

    public long getMaxBlockSize() {
        return this.maxBlockSize;
    }

    public byte[] put(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i = 0;
        while (!put(byteArrayOutputStream, inputStream, i)) {
            try {
                if (byteArrayOutputStream.size() > this.maxBlockSize / 2) {
                    byteArrayOutputStream = putIndirectId(byteArrayOutputStream);
                    i++;
                }
            } catch (IOException e) {
                remove(byteArrayOutputStream.toByteArray());
                throw e;
            }
        }
        if (byteArrayOutputStream.size() > this.minBlockSize * 2) {
            byteArrayOutputStream = putIndirectId(byteArrayOutputStream);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private boolean put(ByteArrayOutputStream byteArrayOutputStream, InputStream inputStream, int i) throws IOException {
        boolean put;
        if (i > 0) {
            ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
            do {
                put = put(byteArrayOutputStream2, inputStream, i - 1);
                if (byteArrayOutputStream2.size() > this.maxBlockSize / 2) {
                    putIndirectId(byteArrayOutputStream2).writeTo(byteArrayOutputStream);
                    return put;
                }
            } while (!put);
            byteArrayOutputStream2.writeTo(byteArrayOutputStream);
            return true;
        }
        byte[] andSet = this.nextBuffer.getAndSet(null);
        if (andSet == null) {
            andSet = new byte[this.maxBlockSize];
        }
        byte[] read = read(inputStream, andSet);
        if (read != andSet) {
            this.nextBuffer.set(andSet);
        }
        int length = read.length;
        if (length == 0) {
            return true;
        }
        boolean z = length < this.maxBlockSize;
        if (length < this.minBlockSize) {
            byteArrayOutputStream.write(0);
            DataUtils.writeVarInt(byteArrayOutputStream, length);
            byteArrayOutputStream.write(read);
        } else {
            byteArrayOutputStream.write(1);
            DataUtils.writeVarInt(byteArrayOutputStream, length);
            DataUtils.writeVarLong(byteArrayOutputStream, writeBlock(read));
        }
        return z;
    }

    private static byte[] read(InputStream inputStream, byte[] bArr) throws IOException {
        int i = 0;
        int length = bArr.length;
        while (length > 0) {
            try {
                int read = inputStream.read(bArr, i, length);
                if (read < 0) {
                    return Arrays.copyOf(bArr, i);
                }
                i += read;
                length -= read;
            } catch (RuntimeException e) {
                throw new IOException(e);
            }
        }
        return bArr;
    }

    private ByteArrayOutputStream putIndirectId(ByteArrayOutputStream byteArrayOutputStream) throws IOException {
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
        byteArrayOutputStream2.write(2);
        DataUtils.writeVarLong(byteArrayOutputStream2, length(byteArray));
        DataUtils.writeVarLong(byteArrayOutputStream2, writeBlock(byteArray));
        return byteArrayOutputStream2;
    }

    private long writeBlock(byte[] bArr) {
        long andIncrementNextKey = getAndIncrementNextKey();
        this.map.put(Long.valueOf(andIncrementNextKey), bArr);
        if (this.onStoreCallback != null) {
            this.onStoreCallback.accept(bArr.length);
        }
        return andIncrementNextKey;
    }

    private long getAndIncrementNextKey() {
        long j;
        long andIncrement = this.nextKey.getAndIncrement();
        if (!this.map.containsKey(Long.valueOf(andIncrement))) {
            return andIncrement;
        }
        synchronized (this) {
            long j2 = andIncrement;
            long j3 = Long.MAX_VALUE;
            while (j2 < j3) {
                long j4 = (j2 + j3) >>> 1;
                if (this.map.containsKey(Long.valueOf(j4))) {
                    j2 = j4 + 1;
                } else {
                    j3 = j4;
                }
            }
            j = j2;
            this.nextKey.set(j + 1);
        }
        return j;
    }

    public long getMaxBlockKey(byte[] bArr) {
        long j = -1;
        ByteBuffer wrap = ByteBuffer.wrap(bArr);
        while (wrap.hasRemaining()) {
            switch (wrap.get()) {
                case 0:
                    wrap.position(wrap.position() + DataUtils.readVarInt(wrap));
                    break;
                case 1:
                    DataUtils.readVarInt(wrap);
                    j = Math.max(j, DataUtils.readVarLong(wrap));
                    break;
                case 2:
                    DataUtils.readVarLong(wrap);
                    long readVarLong = DataUtils.readVarLong(wrap);
                    j = readVarLong;
                    long maxBlockKey = getMaxBlockKey(this.map.get(Long.valueOf(readVarLong)));
                    if (maxBlockKey < 0) {
                        break;
                    } else {
                        j = Math.max(j, maxBlockKey);
                        break;
                    }
                default:
                    throw DataUtils.newIllegalArgumentException("Unsupported id {0}", Arrays.toString(bArr));
            }
        }
        return j;
    }

    public void remove(byte[] bArr) {
        ByteBuffer wrap = ByteBuffer.wrap(bArr);
        while (wrap.hasRemaining()) {
            switch (wrap.get()) {
                case 0:
                    wrap.position(wrap.position() + DataUtils.readVarInt(wrap));
                    break;
                case 1:
                    DataUtils.readVarInt(wrap);
                    this.map.remove(Long.valueOf(DataUtils.readVarLong(wrap)));
                    break;
                case 2:
                    DataUtils.readVarLong(wrap);
                    long readVarLong = DataUtils.readVarLong(wrap);
                    remove(this.map.get(Long.valueOf(readVarLong)));
                    this.map.remove(Long.valueOf(readVarLong));
                    break;
                default:
                    throw DataUtils.newIllegalArgumentException("Unsupported id {0}", Arrays.toString(bArr));
            }
        }
    }

    public static String toString(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        ByteBuffer wrap = ByteBuffer.wrap(bArr);
        long j = 0;
        while (wrap.hasRemaining()) {
            switch (wrap.get()) {
                case 0:
                    int readVarInt = DataUtils.readVarInt(wrap);
                    wrap.position(wrap.position() + readVarInt);
                    sb.append("data len=").append(readVarInt);
                    j += readVarInt;
                    break;
                case 1:
                    int readVarInt2 = DataUtils.readVarInt(wrap);
                    j += readVarInt2;
                    sb.append("block ").append(DataUtils.readVarLong(wrap)).append(" len=").append(readVarInt2);
                    break;
                case 2:
                    int readVarInt3 = DataUtils.readVarInt(wrap);
                    j += DataUtils.readVarLong(wrap);
                    sb.append("indirect block ").append(DataUtils.readVarLong(wrap)).append(" len=").append(readVarInt3);
                    break;
                default:
                    sb.append("error");
                    break;
            }
            sb.append(", ");
        }
        sb.append("length=").append(j);
        return sb.toString();
    }

    public long length(byte[] bArr) {
        ByteBuffer wrap = ByteBuffer.wrap(bArr);
        long j = 0;
        while (wrap.hasRemaining()) {
            switch (wrap.get()) {
                case 0:
                    int readVarInt = DataUtils.readVarInt(wrap);
                    wrap.position(wrap.position() + readVarInt);
                    j += readVarInt;
                    break;
                case 1:
                    j += DataUtils.readVarInt(wrap);
                    DataUtils.readVarLong(wrap);
                    break;
                case 2:
                    j += DataUtils.readVarLong(wrap);
                    DataUtils.readVarLong(wrap);
                    break;
                default:
                    throw DataUtils.newIllegalArgumentException("Unsupported id {0}", Arrays.toString(bArr));
            }
        }
        return j;
    }

    public boolean isInPlace(byte[] bArr) {
        ByteBuffer wrap = ByteBuffer.wrap(bArr);
        while (wrap.hasRemaining()) {
            if (wrap.get() != 0) {
                return false;
            }
            wrap.position(wrap.position() + DataUtils.readVarInt(wrap));
        }
        return true;
    }

    public InputStream get(byte[] bArr) {
        return new Stream(this, bArr);
    }

    byte[] getBlock(long j) {
        byte[] bArr = this.map.get(Long.valueOf(j));
        if (bArr == null) {
            throw DataUtils.newMVStoreException(50, "Block {0} not found", Long.valueOf(j));
        }
        return bArr;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/StreamStore$Stream.class */
    static class Stream extends InputStream {
        private final StreamStore store;
        private byte[] oneByteBuffer;
        private ByteBuffer idBuffer;
        private ByteArrayInputStream buffer;
        private long skip;
        private final long length;
        private long pos;

        Stream(StreamStore streamStore, byte[] bArr) {
            this.store = streamStore;
            this.length = streamStore.length(bArr);
            this.idBuffer = ByteBuffer.wrap(bArr);
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            byte[] bArr = this.oneByteBuffer;
            if (bArr == null) {
                byte[] bArr2 = new byte[1];
                this.oneByteBuffer = bArr2;
                bArr = bArr2;
            }
            if (read(bArr, 0, 1) == -1) {
                return -1;
            }
            return bArr[0] & 255;
        }

        @Override // java.io.InputStream
        public long skip(long j) {
            long min = Math.min(this.length - this.pos, j);
            if (min == 0) {
                return 0L;
            }
            if (this.buffer != null) {
                long skip = this.buffer.skip(min);
                if (skip > 0) {
                    min = skip;
                } else {
                    this.buffer = null;
                    this.skip += min;
                }
            } else {
                this.skip += min;
            }
            this.pos += min;
            return min;
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            this.buffer = null;
            this.idBuffer.position(this.idBuffer.limit());
            this.pos = this.length;
        }

        @Override // java.io.InputStream
        public int read(byte[] bArr, int i, int i2) throws IOException {
            if (i2 <= 0) {
                return 0;
            }
            while (true) {
                if (this.buffer == null) {
                    try {
                        this.buffer = nextBuffer();
                        if (this.buffer == null) {
                            return -1;
                        }
                    } catch (MVStoreException e) {
                        throw new IOException(DataUtils.formatMessage(50, "Block not found in id {0}", Arrays.toString(this.idBuffer.array())), e);
                    }
                }
                int read = this.buffer.read(bArr, i, i2);
                if (read > 0) {
                    this.pos += read;
                    return read;
                }
                this.buffer = null;
            }
        }

        private ByteArrayInputStream nextBuffer() {
            while (this.idBuffer.hasRemaining()) {
                switch (this.idBuffer.get()) {
                    case 0:
                        int readVarInt = DataUtils.readVarInt(this.idBuffer);
                        if (this.skip >= readVarInt) {
                            this.skip -= readVarInt;
                            this.idBuffer.position(this.idBuffer.position() + readVarInt);
                            break;
                        } else {
                            int position = (int) (this.idBuffer.position() + this.skip);
                            int i = (int) (readVarInt - this.skip);
                            this.idBuffer.position(position + i);
                            return new ByteArrayInputStream(this.idBuffer.array(), position, i);
                        }
                    case 1:
                        int readVarInt2 = DataUtils.readVarInt(this.idBuffer);
                        long readVarLong = DataUtils.readVarLong(this.idBuffer);
                        if (this.skip >= readVarInt2) {
                            this.skip -= readVarInt2;
                            break;
                        } else {
                            byte[] block = this.store.getBlock(readVarLong);
                            int i2 = (int) this.skip;
                            this.skip = 0L;
                            return new ByteArrayInputStream(block, i2, block.length - i2);
                        }
                    case 2:
                        long readVarLong2 = DataUtils.readVarLong(this.idBuffer);
                        long readVarLong3 = DataUtils.readVarLong(this.idBuffer);
                        if (this.skip >= readVarLong2) {
                            this.skip -= readVarLong2;
                            break;
                        } else {
                            byte[] block2 = this.store.getBlock(readVarLong3);
                            ByteBuffer allocate = ByteBuffer.allocate((block2.length + this.idBuffer.limit()) - this.idBuffer.position());
                            allocate.put(block2);
                            allocate.put(this.idBuffer);
                            allocate.flip();
                            this.idBuffer = allocate;
                            return nextBuffer();
                        }
                    default:
                        throw DataUtils.newIllegalArgumentException("Unsupported id {0}", Arrays.toString(this.idBuffer.array()));
                }
            }
            return null;
        }
    }
}
