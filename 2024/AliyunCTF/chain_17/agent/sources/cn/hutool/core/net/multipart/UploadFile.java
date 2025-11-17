package cn.hutool.core.net.multipart;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/net/multipart/UploadFile.class */
public class UploadFile {
    private static final String TMP_FILE_PREFIX = "hutool-";
    private static final String TMP_FILE_SUFFIX = ".upload.tmp";
    private final UploadFileHeader header;
    private final UploadSetting setting;
    private long size = -1;
    private byte[] data;
    private File tempFile;

    public UploadFile(UploadFileHeader header, UploadSetting setting) {
        this.header = header;
        this.setting = setting;
    }

    public void delete() {
        if (this.tempFile != null) {
            this.tempFile.delete();
        }
        if (this.data != null) {
            this.data = null;
        }
    }

    public File write(String destPath) throws IOException {
        if (this.data != null || this.tempFile != null) {
            return write(FileUtil.file(destPath));
        }
        return null;
    }

    public File write(File destination) throws IOException {
        assertValid();
        if (destination.isDirectory()) {
            destination = new File(destination, this.header.getFileName());
        }
        if (this.data != null) {
            FileUtil.writeBytes(this.data, destination);
            this.data = null;
        } else {
            if (null == this.tempFile) {
                throw new NullPointerException("Temp file is null !");
            }
            if (false == this.tempFile.exists()) {
                throw new NoSuchFileException("Temp file: [" + this.tempFile.getAbsolutePath() + "] not exist!");
            }
            FileUtil.move(this.tempFile, destination, true);
        }
        return destination;
    }

    public byte[] getFileContent() throws IOException {
        assertValid();
        if (this.data != null) {
            return this.data;
        }
        if (this.tempFile != null) {
            return FileUtil.readBytes(this.tempFile);
        }
        return null;
    }

    public InputStream getFileInputStream() throws IOException {
        assertValid();
        if (this.data != null) {
            return IoUtil.toBuffered(IoUtil.toStream(this.data));
        }
        if (this.tempFile != null) {
            return IoUtil.toBuffered(IoUtil.toStream(this.tempFile));
        }
        return null;
    }

    public UploadFileHeader getHeader() {
        return this.header;
    }

    public String getFileName() {
        if (this.header == null) {
            return null;
        }
        return this.header.getFileName();
    }

    public long size() {
        return this.size;
    }

    public boolean isUploaded() {
        return this.size > 0;
    }

    public boolean isInMemory() {
        return this.data != null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean processStream(MultipartRequestInputStream input) throws IOException {
        if (!isAllowedExtension()) {
            this.size = input.skipToBoundary();
            return false;
        }
        this.size = 0L;
        int memoryThreshold = this.setting.memoryThreshold;
        if (memoryThreshold > 0) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(memoryThreshold);
            long written = input.copy(baos, memoryThreshold);
            this.data = baos.toByteArray();
            if (written <= memoryThreshold) {
                this.size = this.data.length;
                return true;
            }
        }
        this.tempFile = FileUtil.createTempFile(TMP_FILE_PREFIX, TMP_FILE_SUFFIX, FileUtil.touch(this.setting.tmpUploadPath), false);
        BufferedOutputStream out = FileUtil.getOutputStream(this.tempFile);
        if (this.data != null) {
            this.size = this.data.length;
            out.write(this.data);
            this.data = null;
        }
        long maxFileSize = this.setting.maxFileSize;
        try {
            if (maxFileSize == -1) {
                this.size += input.copy(out);
                IoUtil.close((Closeable) out);
                return true;
            }
            this.size += input.copy(out, (maxFileSize - this.size) + 1);
            if (this.size <= maxFileSize) {
                IoUtil.close((Closeable) out);
                return true;
            }
            this.tempFile.delete();
            this.tempFile = null;
            input.skipToBoundary();
            IoUtil.close((Closeable) out);
            return false;
        } catch (Throwable th) {
            IoUtil.close((Closeable) out);
            throw th;
        }
    }

    private boolean isAllowedExtension() {
        String[] exts = this.setting.fileExts;
        boolean isAllow = this.setting.isAllowFileExts;
        if (exts == null || exts.length == 0) {
            return isAllow;
        }
        String fileNameExt = FileUtil.extName(getFileName());
        for (String fileExtension : this.setting.fileExts) {
            if (fileNameExt.equalsIgnoreCase(fileExtension)) {
                return isAllow;
            }
        }
        return !isAllow;
    }

    private void assertValid() throws IOException {
        if (false == isUploaded()) {
            throw new IOException(StrUtil.format("File [{}] upload fail", getFileName()));
        }
    }
}
