package org.h2.store.fs.encrypt;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.h2.mvstore.DataUtils;
import org.h2.security.AES;
import org.h2.security.SHA256;
import org.h2.store.fs.FileBaseDefault;
import org.h2.util.MathUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/encrypt/FileEncrypt.class */
public class FileEncrypt extends FileBaseDefault {
    public static final int BLOCK_SIZE = 4096;
    static final int BLOCK_SIZE_MASK = 4095;
    static final int HEADER_LENGTH = 4096;
    private static final byte[] HEADER;
    private static final int SALT_POS;
    private static final int SALT_LENGTH = 8;
    private static final int HASH_ITERATIONS = 10;
    private final FileChannel base;
    private volatile long size;
    private final String name;
    private volatile XTS xts;
    private byte[] encryptionKey;
    private FileEncrypt source;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !FileEncrypt.class.desiredAssertionStatus();
        HEADER = "H2encrypt\n".getBytes(StandardCharsets.ISO_8859_1);
        SALT_POS = HEADER.length;
    }

    public FileEncrypt(String str, byte[] bArr, FileChannel fileChannel) {
        this.name = str;
        this.base = fileChannel;
        this.encryptionKey = bArr;
    }

    public FileEncrypt(String str, FileEncrypt fileEncrypt, FileChannel fileChannel) {
        this.name = str;
        this.base = fileChannel;
        this.source = fileEncrypt;
        try {
            fileEncrypt.init();
        } catch (IOException e) {
            throw DataUtils.newMVStoreException(3, "Can not open {0} using encryption of {1}", str, fileEncrypt.name);
        }
    }

    private XTS init() throws IOException {
        XTS xts = this.xts;
        if (xts == null) {
            xts = createXTS();
        }
        return xts;
    }

    private synchronized XTS createXTS() throws IOException {
        XTS xts;
        byte[] secureRandomBytes;
        XTS xts2 = this.xts;
        if (xts2 != null) {
            return xts2;
        }
        if (!$assertionsDisabled && this.size != 0) {
            throw new AssertionError();
        }
        long size = this.base.size() - 4096;
        boolean z = size >= 0;
        if (this.encryptionKey != null) {
            if (z) {
                secureRandomBytes = new byte[8];
                readFully(this.base, SALT_POS, ByteBuffer.wrap(secureRandomBytes));
            } else {
                byte[] copyOf = Arrays.copyOf(HEADER, 4096);
                secureRandomBytes = MathUtils.secureRandomBytes(8);
                System.arraycopy(secureRandomBytes, 0, copyOf, SALT_POS, secureRandomBytes.length);
                writeFully(this.base, 0L, ByteBuffer.wrap(copyOf));
            }
            AES aes = new AES();
            aes.setKey(SHA256.getPBKDF2(this.encryptionKey, secureRandomBytes, 10, 16));
            this.encryptionKey = null;
            xts = new XTS(aes);
        } else {
            if (!z) {
                ByteBuffer allocateDirect = ByteBuffer.allocateDirect(4096);
                readFully(this.source.base, 0L, allocateDirect);
                allocateDirect.flip();
                writeFully(this.base, 0L, allocateDirect);
            }
            xts = this.source.xts;
            this.source = null;
        }
        if (z) {
            if ((size & 4095) != 0) {
                size -= 4096;
            }
            this.size = size;
        }
        XTS xts3 = xts;
        this.xts = xts3;
        return xts3;
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.spi.AbstractInterruptibleChannel
    protected void implCloseChannel() throws IOException {
        this.base.close();
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public int read(ByteBuffer byteBuffer, long j) throws IOException {
        int remaining = byteBuffer.remaining();
        if (remaining == 0) {
            return 0;
        }
        XTS init = init();
        int min = (int) Math.min(remaining, this.size - j);
        if (j >= this.size) {
            return -1;
        }
        if (j < 0) {
            throw new IllegalArgumentException("pos: " + j);
        }
        if ((j & 4095) != 0 || (min & 4095) != 0) {
            long j2 = (j / 4096) * 4096;
            int i = (int) (j - j2);
            int i2 = ((((min + i) + 4096) - 1) / 4096) * 4096;
            ByteBuffer allocate = ByteBuffer.allocate(i2);
            readInternal(allocate, j2, i2, init);
            allocate.flip().limit(i + min).position(i);
            byteBuffer.put(allocate);
            return min;
        }
        readInternal(byteBuffer, j, min, init);
        return min;
    }

    private void readInternal(ByteBuffer byteBuffer, long j, int i, XTS xts) throws IOException {
        int position = byteBuffer.position();
        readFully(this.base, j + 4096, byteBuffer);
        long j2 = j / 4096;
        while (i > 0) {
            long j3 = j2;
            j2 = j3 + 1;
            xts.decrypt(j3, 4096, byteBuffer.array(), byteBuffer.arrayOffset() + position);
            position += 4096;
            i -= 4096;
        }
    }

    private static void readFully(FileChannel fileChannel, long j, ByteBuffer byteBuffer) throws IOException {
        do {
            int read = fileChannel.read(byteBuffer, j);
            if (read < 0) {
                throw new EOFException();
            }
            j += read;
        } while (byteBuffer.remaining() > 0);
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public int write(ByteBuffer byteBuffer, long j) throws IOException {
        XTS init = init();
        int remaining = byteBuffer.remaining();
        if ((j & 4095) != 0 || (remaining & 4095) != 0) {
            long j2 = (j / 4096) * 4096;
            int i = (int) (j - j2);
            int i2 = ((((remaining + i) + 4096) - 1) / 4096) * 4096;
            ByteBuffer allocate = ByteBuffer.allocate(i2);
            int min = Math.min(i2, (((int) (((this.size - j2) + 4096) - 1)) / 4096) * 4096);
            if (min > 0) {
                readInternal(allocate, j2, min, init);
                allocate.rewind();
            }
            allocate.limit(i + remaining).position(i);
            allocate.put(byteBuffer).limit(i2).rewind();
            writeInternal(allocate, j2, i2, init);
            this.size = Math.max(this.size, j + remaining);
            int i3 = (int) (this.size & 4095);
            if (i3 > 0) {
                writeFully(this.base, j2 + 4096 + i2, ByteBuffer.allocate(i3));
            }
            return remaining;
        }
        writeInternal(byteBuffer, j, remaining, init);
        this.size = Math.max(this.size, j + remaining);
        return remaining;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void writeInternal(ByteBuffer byteBuffer, long j, int i, XTS xts) throws IOException {
        ByteBuffer put = ByteBuffer.allocate(i).put(byteBuffer);
        put.flip();
        long j2 = j / 4096;
        int i2 = 0;
        for (int i3 = i; i3 > 0; i3 -= 4096) {
            j2++;
            xts.encrypt(xts, 4096, put.array(), put.arrayOffset() + i2);
            i2 += 4096;
        }
        writeFully(this.base, j + 4096, put);
    }

    private static void writeFully(FileChannel fileChannel, long j, ByteBuffer byteBuffer) throws IOException {
        do {
            j += fileChannel.write(byteBuffer, j);
        } while (byteBuffer.remaining() > 0);
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
    public long size() throws IOException {
        init();
        return this.size;
    }

    @Override // org.h2.store.fs.FileBaseDefault
    protected void implTruncate(long j) throws IOException {
        init();
        if (j > this.size) {
            return;
        }
        if (j < 0) {
            throw new IllegalArgumentException("newSize: " + j);
        }
        if (((int) (j & 4095)) > 0) {
            this.base.truncate(j + 4096 + 4096);
        } else {
            this.base.truncate(j + 4096);
        }
        this.size = j;
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public void force(boolean z) throws IOException {
        this.base.force(z);
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public FileLock tryLock(long j, long j2, boolean z) throws IOException {
        return this.base.tryLock(j, j2, z);
    }

    public String toString() {
        return this.name;
    }
}
