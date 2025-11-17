package cn.hutool.core.io.file;

import cn.hutool.core.io.FileUtil;
import java.io.File;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/file/FileWrapper.class */
public class FileWrapper implements Serializable {
    private static final long serialVersionUID = 1;
    protected File file;
    protected Charset charset;
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public FileWrapper(File file, Charset charset) {
        this.file = file;
        this.charset = charset;
    }

    public File getFile() {
        return this.file;
    }

    public FileWrapper setFile(File file) {
        this.file = file;
        return this;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public FileWrapper setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public String readableFileSize() {
        return FileUtil.readableFileSize(this.file.length());
    }
}
