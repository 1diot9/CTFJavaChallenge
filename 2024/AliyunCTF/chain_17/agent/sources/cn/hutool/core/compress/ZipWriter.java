package cn.hutool.core.compress;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/compress/ZipWriter.class */
public class ZipWriter implements Closeable {
    private final ZipOutputStream out;

    public static ZipWriter of(File zipFile, Charset charset) {
        return new ZipWriter(zipFile, charset);
    }

    public static ZipWriter of(OutputStream out, Charset charset) {
        return new ZipWriter(out, charset);
    }

    public ZipWriter(File zipFile, Charset charset) {
        this.out = getZipOutputStream(zipFile, charset);
    }

    public ZipWriter(OutputStream out, Charset charset) {
        this.out = ZipUtil.getZipOutputStream(out, charset);
    }

    public ZipWriter(ZipOutputStream out) {
        this.out = out;
    }

    public ZipWriter setLevel(int level) {
        this.out.setLevel(level);
        return this;
    }

    public ZipWriter setComment(String comment) {
        this.out.setComment(comment);
        return this;
    }

    public ZipOutputStream getOut() {
        return this.out;
    }

    public ZipWriter add(boolean withSrcDir, FileFilter filter, File... files) throws IORuntimeException {
        for (File file : files) {
            try {
                String srcRootDir = file.getCanonicalPath();
                if (false == file.isDirectory() || withSrcDir) {
                    srcRootDir = file.getCanonicalFile().getParentFile().getCanonicalPath();
                }
                _add(file, srcRootDir, filter);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
        return this;
    }

    public ZipWriter add(Resource... resources) throws IORuntimeException {
        for (Resource resource : resources) {
            if (null != resource) {
                add(resource.getName(), resource.getStream());
            }
        }
        return this;
    }

    public ZipWriter add(String path, InputStream in) throws IORuntimeException {
        String path2 = StrUtil.nullToEmpty(path);
        if (null == in) {
            path2 = StrUtil.addSuffixIfNot(path2, "/");
            if (StrUtil.isBlank(path2)) {
                return this;
            }
        }
        return putEntry(path2, in);
    }

    public ZipWriter add(String[] paths, InputStream[] ins) throws IORuntimeException {
        if (ArrayUtil.isEmpty((Object[]) paths) || ArrayUtil.isEmpty((Object[]) ins)) {
            throw new IllegalArgumentException("Paths or ins is empty !");
        }
        if (paths.length != ins.length) {
            throw new IllegalArgumentException("Paths length is not equals to ins length !");
        }
        for (int i = 0; i < paths.length; i++) {
            add(paths[i], ins[i]);
        }
        return this;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IORuntimeException {
        try {
            try {
                this.out.finish();
                IoUtil.close((Closeable) this.out);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } catch (Throwable th) {
            IoUtil.close((Closeable) this.out);
            throw th;
        }
    }

    private static ZipOutputStream getZipOutputStream(File zipFile, Charset charset) {
        return ZipUtil.getZipOutputStream(FileUtil.getOutputStream(zipFile), charset);
    }

    private ZipWriter _add(File file, String srcRootDir, FileFilter filter) throws IORuntimeException {
        if (null == file || (null != filter && false == filter.accept(file))) {
            return this;
        }
        String subPath = FileUtil.subPath(srcRootDir, file);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (ArrayUtil.isEmpty((Object[]) files)) {
                add(subPath, (InputStream) null);
            } else {
                for (File childFile : files) {
                    _add(childFile, srcRootDir, filter);
                }
            }
        } else {
            putEntry(subPath, FileUtil.getInputStream(file));
        }
        return this;
    }

    private ZipWriter putEntry(String path, InputStream in) throws IORuntimeException {
        try {
            try {
                this.out.putNextEntry(new ZipEntry(path));
                if (null != in) {
                    IoUtil.copy(in, this.out);
                }
                this.out.closeEntry();
                IoUtil.close((Closeable) in);
                IoUtil.flush(this.out);
                return this;
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } catch (Throwable th) {
            IoUtil.close((Closeable) in);
            throw th;
        }
    }
}
