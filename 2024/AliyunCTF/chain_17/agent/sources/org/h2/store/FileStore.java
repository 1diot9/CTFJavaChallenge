package org.h2.store;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import java.io.IOException;
import java.lang.ref.Reference;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.coyote.http11.Constants;
import org.h2.api.ErrorCode;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.security.SecureFileStore;
import org.h2.store.fs.FileUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/FileStore.class */
public class FileStore {
    public static final int HEADER_LENGTH = 48;
    private static final String HEADER;
    private static final boolean ASSERT;
    protected String name;
    private final DataHandler handler;
    private FileChannel file;
    private long filePos;
    private long fileLength;
    private Reference<?> autoDeleteReference;
    private boolean checkedWriting = true;
    private final String mode;
    private java.nio.channels.FileLock lock;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !FileStore.class.desiredAssertionStatus();
        HEADER = "-- H2 0.5/B --      ".substring(0, 15) + StrPool.LF;
        boolean z = false;
        if (!$assertionsDisabled) {
            z = true;
            if (1 == 0) {
                throw new AssertionError();
            }
        }
        ASSERT = z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public FileStore(DataHandler dataHandler, String str, String str2) {
        this.handler = dataHandler;
        this.name = str;
        try {
            boolean exists = FileUtils.exists(str);
            if (exists && !FileUtils.canWrite(str)) {
                str2 = "r";
            } else {
                FileUtils.createDirectories(FileUtils.getParent(str));
            }
            this.file = FileUtils.open(str, str2);
            if (exists) {
                this.fileLength = this.file.size();
            }
            this.mode = str2;
        } catch (IOException e) {
            throw DbException.convertIOException(e, "name: " + str + " mode: " + str2);
        }
    }

    public static FileStore open(DataHandler dataHandler, String str, String str2) {
        return open(dataHandler, str, str2, null, null, 0);
    }

    public static FileStore open(DataHandler dataHandler, String str, String str2, String str3, byte[] bArr) {
        return open(dataHandler, str, str2, str3, bArr, 1024);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.h2.store.FileStore] */
    public static FileStore open(DataHandler dataHandler, String str, String str2, String str3, byte[] bArr, int i) {
        SecureFileStore secureFileStore;
        if (str3 == null) {
            secureFileStore = new FileStore(dataHandler, str, str2);
        } else {
            secureFileStore = new SecureFileStore(dataHandler, str, str2, str3, bArr, i);
        }
        return secureFileStore;
    }

    protected byte[] generateSalt() {
        return HEADER.getBytes(StandardCharsets.UTF_8);
    }

    protected void initKey(byte[] bArr) {
    }

    public void setCheckedWriting(boolean z) {
        this.checkedWriting = z;
    }

    private void checkWritingAllowed() {
        if (this.handler != null && this.checkedWriting) {
            this.handler.checkWritingAllowed();
        }
    }

    private void checkPowerOff() {
        if (this.handler != null) {
            this.handler.checkPowerOff();
        }
    }

    public void init() {
        byte[] bytes = HEADER.getBytes(StandardCharsets.UTF_8);
        if (length() < 48) {
            this.checkedWriting = false;
            writeDirect(bytes, 0, 16);
            byte[] generateSalt = generateSalt();
            writeDirect(generateSalt, 0, 16);
            initKey(generateSalt);
            write(bytes, 0, 16);
            this.checkedWriting = true;
            return;
        }
        seek(0L);
        byte[] bArr = new byte[16];
        readFullyDirect(bArr, 0, 16);
        if (!Arrays.equals(bArr, bytes)) {
            throw DbException.get(ErrorCode.FILE_VERSION_ERROR_1, this.name);
        }
        byte[] bArr2 = new byte[16];
        readFullyDirect(bArr2, 0, 16);
        initKey(bArr2);
        readFully(bArr, 0, 16);
        if (!Arrays.equals(bArr, bytes)) {
            throw DbException.get(ErrorCode.FILE_ENCRYPTION_ERROR_1, this.name);
        }
    }

    public void close() {
        if (this.file != null) {
            try {
                try {
                    trace(Constants.CLOSE, this.name, this.file);
                    this.file.close();
                    this.file = null;
                } catch (IOException e) {
                    throw DbException.convertIOException(e, this.name);
                }
            } catch (Throwable th) {
                this.file = null;
                throw th;
            }
        }
    }

    public void closeSilently() {
        try {
            close();
        } catch (Exception e) {
        }
    }

    public void closeAndDeleteSilently() {
        if (this.file != null) {
            closeSilently();
            this.handler.getTempFileDeleter().deleteFile(this.autoDeleteReference, this.name);
            this.name = null;
        }
    }

    public void readFullyDirect(byte[] bArr, int i, int i2) {
        readFully(bArr, i, i2);
    }

    public void readFully(byte[] bArr, int i, int i2) {
        if (i2 < 0 || i2 % 16 != 0) {
            throw DbException.getInternalError("unaligned read " + this.name + " len " + i2);
        }
        checkPowerOff();
        try {
            FileUtils.readFully(this.file, ByteBuffer.wrap(bArr, i, i2));
            this.filePos += i2;
        } catch (IOException e) {
            throw DbException.convertIOException(e, this.name);
        }
    }

    public void seek(long j) {
        if (j % 16 != 0) {
            throw DbException.getInternalError("unaligned seek " + this.name + " pos " + j);
        }
        try {
            if (j != this.filePos) {
                this.file.position(j);
                this.filePos = j;
            }
        } catch (IOException e) {
            throw DbException.convertIOException(e, this.name);
        }
    }

    protected void writeDirect(byte[] bArr, int i, int i2) {
        write(bArr, i, i2);
    }

    public void write(byte[] bArr, int i, int i2) {
        if (i2 < 0 || i2 % 16 != 0) {
            throw DbException.getInternalError("unaligned write " + this.name + " len " + i2);
        }
        checkWritingAllowed();
        checkPowerOff();
        try {
            FileUtils.writeFully(this.file, ByteBuffer.wrap(bArr, i, i2));
            this.filePos += i2;
            this.fileLength = Math.max(this.filePos, this.fileLength);
        } catch (IOException e) {
            closeFileSilently();
            throw DbException.convertIOException(e, this.name);
        }
    }

    public void setLength(long j) {
        if (j % 16 != 0) {
            throw DbException.getInternalError("unaligned setLength " + this.name + " pos " + j);
        }
        checkPowerOff();
        checkWritingAllowed();
        try {
            if (j > this.fileLength) {
                long j2 = this.filePos;
                this.file.position(j - 1);
                FileUtils.writeFully(this.file, ByteBuffer.wrap(new byte[1]));
                this.file.position(j2);
            } else {
                this.file.truncate(j);
            }
            this.fileLength = j;
        } catch (IOException e) {
            closeFileSilently();
            throw DbException.convertIOException(e, this.name);
        }
    }

    public long length() {
        long j = this.fileLength;
        if (ASSERT) {
            try {
                j = this.file.size();
                if (j != this.fileLength) {
                    throw DbException.getInternalError("file " + this.name + " length " + j + " expected " + this.fileLength);
                }
                if (j % 16 != 0) {
                    long j2 = (j + 16) - (j % 16);
                    this.file.truncate(j2);
                    this.fileLength = j2;
                    throw DbException.getInternalError("unaligned file length " + this.name + " len " + j);
                }
            } catch (IOException e) {
                throw DbException.convertIOException(e, this.name);
            }
        }
        return j;
    }

    public long getFilePointer() {
        if (ASSERT) {
            try {
                if (this.file.position() != this.filePos) {
                    throw DbException.getInternalError(this.file.position() + CharSequenceUtil.SPACE + this.filePos);
                }
            } catch (IOException e) {
                throw DbException.convertIOException(e, this.name);
            }
        }
        return this.filePos;
    }

    public void sync() {
        try {
            this.file.force(true);
        } catch (IOException e) {
            closeFileSilently();
            throw DbException.convertIOException(e, this.name);
        }
    }

    public void autoDelete() {
        if (this.autoDeleteReference == null) {
            this.autoDeleteReference = this.handler.getTempFileDeleter().addFile(this.name, this);
        }
    }

    public void stopAutoDelete() {
        this.handler.getTempFileDeleter().stopAutoDelete(this.autoDeleteReference, this.name);
        this.autoDeleteReference = null;
    }

    public void closeFile() throws IOException {
        this.file.close();
        this.file = null;
    }

    private void closeFileSilently() {
        try {
            this.file.close();
        } catch (IOException e) {
        }
    }

    public void openFile() throws IOException {
        if (this.file == null) {
            this.file = FileUtils.open(this.name, this.mode);
            this.file.position(this.filePos);
        }
    }

    private static void trace(String str, String str2, Object obj) {
        if (SysProperties.TRACE_IO) {
            System.out.println("FileStore." + str + CharSequenceUtil.SPACE + str2 + CharSequenceUtil.SPACE + obj);
        }
    }

    public synchronized boolean tryLock() {
        try {
            this.lock = this.file.tryLock();
            return this.lock != null;
        } catch (Exception e) {
            return false;
        }
    }

    public synchronized void releaseLock() {
        if (this.file != null && this.lock != null) {
            try {
                this.lock.release();
            } catch (Exception e) {
            }
            this.lock = null;
        }
    }
}
