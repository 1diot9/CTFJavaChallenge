package cn.hutool.core.util;

import cn.hutool.core.collection.EnumerationIter;
import cn.hutool.core.compress.Deflate;
import cn.hutool.core.compress.Gzip;
import cn.hutool.core.compress.ZipCopyVisitor;
import cn.hutool.core.compress.ZipReader;
import cn.hutool.core.compress.ZipWriter;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.LimitedInputStream;
import cn.hutool.core.io.file.FileSystemUtil;
import cn.hutool.core.io.file.PathUtil;
import cn.hutool.core.io.resource.Resource;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/util/ZipUtil.class */
public class ZipUtil {
    private static final int DEFAULT_BYTE_ARRAY_LENGTH = 32;
    private static final Charset DEFAULT_CHARSET = CharsetUtil.defaultCharset();

    public static ZipFile toZipFile(File file, Charset charset) {
        try {
            return new ZipFile(file, (Charset) ObjectUtil.defaultIfNull(charset, CharsetUtil.CHARSET_UTF_8));
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static InputStream getStream(ZipFile zipFile, ZipEntry zipEntry) {
        try {
            return new LimitedInputStream(zipFile.getInputStream(zipEntry), zipEntry.getSize());
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static ZipOutputStream getZipOutputStream(OutputStream out, Charset charset) {
        if (out instanceof ZipOutputStream) {
            return (ZipOutputStream) out;
        }
        return new ZipOutputStream(out, charset);
    }

    /* JADX WARN: Failed to apply debug info
    java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r10v2 ??
    java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r11v0 ??
    java.lang.NullPointerException
     */
    /* JADX WARN: Multi-variable type inference failed. Error: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.RegisterArg.getSVar()" because the return value of "jadx.core.dex.nodes.InsnNode.getResult()" is null
    	at jadx.core.dex.visitors.typeinference.AbstractTypeConstraint.collectRelatedVars(AbstractTypeConstraint.java:31)
    	at jadx.core.dex.visitors.typeinference.AbstractTypeConstraint.<init>(AbstractTypeConstraint.java:19)
    	at jadx.core.dex.visitors.typeinference.TypeSearch$1.<init>(TypeSearch.java:376)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.makeMoveConstraint(TypeSearch.java:376)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.makeConstraint(TypeSearch.java:361)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.collectConstraints(TypeSearch.java:341)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:60)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.runMultiVariableSearch(FixTypesVisitor.java:116)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:91)
     */
    /* JADX WARN: Not initialized variable reg: 10, insn: 0x007c: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r10 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY] A[D('zipFileSystem' java.nio.file.FileSystem)]) A[TRY_LEAVE], block:B:28:0x007c */
    /* JADX WARN: Not initialized variable reg: 11, insn: 0x0080: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r11 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:30:0x0080 */
    /* JADX WARN: Type inference failed for: r10v2, names: [zipFileSystem], types: [java.nio.file.FileSystem] */
    /* JADX WARN: Type inference failed for: r11v0, types: [java.lang.Throwable] */
    public static void append(Path zipPath, Path appendFilePath, CopyOption... options) throws IORuntimeException {
        ?? r10;
        ?? r11;
        try {
            try {
                FileSystem createZip = FileSystemUtil.createZip(zipPath.toString());
                Throwable th = null;
                if (Files.isDirectory(appendFilePath, new LinkOption[0])) {
                    Path parent = appendFilePath.getParent();
                    if (null == parent) {
                        parent = appendFilePath;
                    }
                    Files.walkFileTree(appendFilePath, new ZipCopyVisitor(parent, createZip, options));
                } else {
                    Files.copy(appendFilePath, createZip.getPath(PathUtil.getName(appendFilePath), new String[0]), options);
                }
                if (createZip != null) {
                    if (0 != 0) {
                        try {
                            createZip.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        createZip.close();
                    }
                }
            } catch (FileAlreadyExistsException e) {
            } catch (IOException e2) {
                throw new IORuntimeException(e2);
            }
        } catch (Throwable th3) {
            if (r10 != 0) {
                if (r11 != 0) {
                    try {
                        r10.close();
                    } catch (Throwable th4) {
                        r11.addSuppressed(th4);
                    }
                } else {
                    r10.close();
                }
            }
            throw th3;
        }
    }

    public static File zip(String srcPath) throws UtilException {
        return zip(srcPath, DEFAULT_CHARSET);
    }

    public static File zip(String srcPath, Charset charset) throws UtilException {
        return zip(FileUtil.file(srcPath), charset);
    }

    public static File zip(File srcFile) throws UtilException {
        return zip(srcFile, DEFAULT_CHARSET);
    }

    public static File zip(File srcFile, Charset charset) throws UtilException {
        File zipFile = FileUtil.file(srcFile.getParentFile(), FileUtil.mainName(srcFile) + ".zip");
        zip(zipFile, charset, false, srcFile);
        return zipFile;
    }

    public static File zip(String srcPath, String zipPath) throws UtilException {
        return zip(srcPath, zipPath, false);
    }

    public static File zip(String srcPath, String zipPath, boolean withSrcDir) throws UtilException {
        return zip(srcPath, zipPath, DEFAULT_CHARSET, withSrcDir);
    }

    public static File zip(String srcPath, String zipPath, Charset charset, boolean withSrcDir) throws UtilException {
        File srcFile = FileUtil.file(srcPath);
        File zipFile = FileUtil.file(zipPath);
        zip(zipFile, charset, withSrcDir, srcFile);
        return zipFile;
    }

    public static File zip(File zipFile, boolean withSrcDir, File... srcFiles) throws UtilException {
        return zip(zipFile, DEFAULT_CHARSET, withSrcDir, srcFiles);
    }

    public static File zip(File zipFile, Charset charset, boolean withSrcDir, File... srcFiles) throws UtilException {
        return zip(zipFile, charset, withSrcDir, (FileFilter) null, srcFiles);
    }

    public static File zip(File zipFile, Charset charset, boolean withSrcDir, FileFilter filter, File... srcFiles) throws IORuntimeException {
        validateFiles(zipFile, srcFiles);
        ZipWriter.of(zipFile, charset).add(withSrcDir, filter, srcFiles).close();
        return zipFile;
    }

    public static void zip(OutputStream out, Charset charset, boolean withSrcDir, FileFilter filter, File... srcFiles) throws IORuntimeException {
        ZipWriter.of(out, charset).add(withSrcDir, filter, srcFiles).close();
    }

    @Deprecated
    public static void zip(ZipOutputStream zipOutputStream, boolean withSrcDir, FileFilter filter, File... srcFiles) throws IORuntimeException {
        ZipWriter zipWriter = new ZipWriter(zipOutputStream);
        Throwable th = null;
        try {
            try {
                zipWriter.add(withSrcDir, filter, srcFiles);
                if (zipWriter != null) {
                    if (0 != 0) {
                        try {
                            zipWriter.close();
                            return;
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                            return;
                        }
                    }
                    zipWriter.close();
                }
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (zipWriter != null) {
                if (th != null) {
                    try {
                        zipWriter.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    zipWriter.close();
                }
            }
            throw th4;
        }
    }

    public static File zip(File zipFile, String path, String data) throws UtilException {
        return zip(zipFile, path, data, DEFAULT_CHARSET);
    }

    public static File zip(File zipFile, String path, String data, Charset charset) throws UtilException {
        return zip(zipFile, path, IoUtil.toStream(data, charset), charset);
    }

    public static File zip(File zipFile, String path, InputStream in) throws UtilException {
        return zip(zipFile, path, in, DEFAULT_CHARSET);
    }

    public static File zip(File zipFile, String path, InputStream in, Charset charset) throws UtilException {
        return zip(zipFile, new String[]{path}, new InputStream[]{in}, charset);
    }

    public static File zip(File zipFile, String[] paths, InputStream[] ins) throws UtilException {
        return zip(zipFile, paths, ins, DEFAULT_CHARSET);
    }

    public static File zip(File zipFile, String[] paths, InputStream[] ins, Charset charset) throws UtilException {
        ZipWriter zipWriter = ZipWriter.of(zipFile, charset);
        Throwable th = null;
        try {
            zipWriter.add(paths, ins);
            if (zipWriter != null) {
                if (0 != 0) {
                    try {
                        zipWriter.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                } else {
                    zipWriter.close();
                }
            }
            return zipFile;
        } catch (Throwable th3) {
            if (zipWriter != null) {
                if (0 != 0) {
                    try {
                        zipWriter.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    zipWriter.close();
                }
            }
            throw th3;
        }
    }

    public static void zip(OutputStream out, String[] paths, InputStream[] ins) {
        zip(getZipOutputStream(out, DEFAULT_CHARSET), paths, ins);
    }

    public static void zip(ZipOutputStream zipOutputStream, String[] paths, InputStream[] ins) throws IORuntimeException {
        ZipWriter zipWriter = new ZipWriter(zipOutputStream);
        Throwable th = null;
        try {
            try {
                zipWriter.add(paths, ins);
                if (zipWriter != null) {
                    if (0 != 0) {
                        try {
                            zipWriter.close();
                            return;
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                            return;
                        }
                    }
                    zipWriter.close();
                }
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (zipWriter != null) {
                if (th != null) {
                    try {
                        zipWriter.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    zipWriter.close();
                }
            }
            throw th4;
        }
    }

    public static File zip(File zipFile, Charset charset, Resource... resources) throws UtilException {
        ZipWriter.of(zipFile, charset).add(resources).close();
        return zipFile;
    }

    public static File unzip(String zipFilePath) throws UtilException {
        return unzip(zipFilePath, DEFAULT_CHARSET);
    }

    public static File unzip(String zipFilePath, Charset charset) throws UtilException {
        return unzip(FileUtil.file(zipFilePath), charset);
    }

    public static File unzip(File zipFile) throws UtilException {
        return unzip(zipFile, DEFAULT_CHARSET);
    }

    public static File unzip(File zipFile, Charset charset) throws UtilException {
        File destDir = FileUtil.file(zipFile.getParentFile(), FileUtil.mainName(zipFile));
        return unzip(zipFile, destDir, charset);
    }

    public static File unzip(String zipFilePath, String outFileDir) throws UtilException {
        return unzip(zipFilePath, outFileDir, DEFAULT_CHARSET);
    }

    public static File unzip(String zipFilePath, String outFileDir, Charset charset) throws UtilException {
        return unzip(FileUtil.file(zipFilePath), FileUtil.mkdir(outFileDir), charset);
    }

    public static File unzip(File zipFile, File outFile) throws UtilException {
        return unzip(zipFile, outFile, DEFAULT_CHARSET);
    }

    public static File unzip(File zipFile, File outFile, Charset charset) {
        return unzip(toZipFile(zipFile, charset), outFile);
    }

    public static File unzip(ZipFile zipFile, File outFile) throws IORuntimeException {
        return unzip(zipFile, outFile, -1L);
    }

    public static File unzip(ZipFile zipFile, File outFile, long limit) throws IORuntimeException {
        if (outFile.exists() && outFile.isFile()) {
            throw new IllegalArgumentException(StrUtil.format("Target path [{}] exist!", outFile.getAbsolutePath()));
        }
        if (limit > 0) {
            Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
            long zipFileSize = 0;
            while (zipEntries.hasMoreElements()) {
                ZipEntry zipEntry = zipEntries.nextElement();
                zipFileSize += zipEntry.getSize();
                if (zipFileSize > limit) {
                    throw new IllegalArgumentException("The file size exceeds the limit");
                }
            }
        }
        ZipReader reader = new ZipReader(zipFile);
        Throwable th = null;
        try {
            try {
                reader.readTo(outFile);
                if (reader != null) {
                    if (0 != 0) {
                        try {
                            reader.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        reader.close();
                    }
                }
                return outFile;
            } finally {
            }
        } catch (Throwable th3) {
            if (reader != null) {
                if (th != null) {
                    try {
                        reader.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    reader.close();
                }
            }
            throw th3;
        }
    }

    public static InputStream get(File zipFile, Charset charset, String path) {
        return get(toZipFile(zipFile, charset), path);
    }

    public static InputStream get(ZipFile zipFile, String path) {
        ZipEntry entry = zipFile.getEntry(path);
        if (null != entry) {
            return getStream(zipFile, entry);
        }
        return null;
    }

    public static void read(ZipFile zipFile, Consumer<ZipEntry> consumer) {
        ZipReader reader = new ZipReader(zipFile);
        Throwable th = null;
        try {
            try {
                reader.read(consumer);
                if (reader != null) {
                    if (0 != 0) {
                        try {
                            reader.close();
                            return;
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                            return;
                        }
                    }
                    reader.close();
                }
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (reader != null) {
                if (th != null) {
                    try {
                        reader.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    reader.close();
                }
            }
            throw th4;
        }
    }

    public static File unzip(InputStream in, File outFile, Charset charset) throws UtilException {
        if (null == charset) {
            charset = DEFAULT_CHARSET;
        }
        return unzip(new ZipInputStream(in, charset), outFile);
    }

    public static File unzip(ZipInputStream zipStream, File outFile) throws UtilException {
        ZipReader reader = new ZipReader(zipStream);
        Throwable th = null;
        try {
            try {
                reader.readTo(outFile);
                if (reader != null) {
                    if (0 != 0) {
                        try {
                            reader.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        reader.close();
                    }
                }
                return outFile;
            } finally {
            }
        } catch (Throwable th3) {
            if (reader != null) {
                if (th != null) {
                    try {
                        reader.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    reader.close();
                }
            }
            throw th3;
        }
    }

    public static void read(ZipInputStream zipStream, Consumer<ZipEntry> consumer) {
        ZipReader reader = new ZipReader(zipStream);
        Throwable th = null;
        try {
            try {
                reader.read(consumer);
                if (reader != null) {
                    if (0 != 0) {
                        try {
                            reader.close();
                            return;
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                            return;
                        }
                    }
                    reader.close();
                }
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (reader != null) {
                if (th != null) {
                    try {
                        reader.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    reader.close();
                }
            }
            throw th4;
        }
    }

    public static byte[] unzipFileBytes(String zipFilePath, String name) {
        return unzipFileBytes(zipFilePath, DEFAULT_CHARSET, name);
    }

    public static byte[] unzipFileBytes(String zipFilePath, Charset charset, String name) {
        return unzipFileBytes(FileUtil.file(zipFilePath), charset, name);
    }

    public static byte[] unzipFileBytes(File zipFile, String name) {
        return unzipFileBytes(zipFile, DEFAULT_CHARSET, name);
    }

    public static byte[] unzipFileBytes(File zipFile, Charset charset, String name) {
        ZipReader reader = ZipReader.of(zipFile, charset);
        Throwable th = null;
        try {
            try {
                byte[] readBytes = IoUtil.readBytes(reader.get(name));
                if (reader != null) {
                    if (0 != 0) {
                        try {
                            reader.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        reader.close();
                    }
                }
                return readBytes;
            } finally {
            }
        } catch (Throwable th3) {
            if (reader != null) {
                if (th != null) {
                    try {
                        reader.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    reader.close();
                }
            }
            throw th3;
        }
    }

    public static byte[] gzip(String content, String charset) throws UtilException {
        return gzip(StrUtil.bytes(content, charset));
    }

    public static byte[] gzip(byte[] buf) throws UtilException {
        return gzip(new ByteArrayInputStream(buf), buf.length);
    }

    public static byte[] gzip(File file) throws UtilException {
        BufferedInputStream in = null;
        try {
            in = FileUtil.getInputStream(file);
            byte[] gzip = gzip(in, (int) file.length());
            IoUtil.close((Closeable) in);
            return gzip;
        } catch (Throwable th) {
            IoUtil.close((Closeable) in);
            throw th;
        }
    }

    public static byte[] gzip(InputStream in) throws UtilException {
        return gzip(in, 32);
    }

    public static byte[] gzip(InputStream in, int length) throws UtilException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
        Gzip.of(in, bos).gzip().close();
        return bos.toByteArray();
    }

    public static String unGzip(byte[] buf, String charset) throws UtilException {
        return StrUtil.str(unGzip(buf), charset);
    }

    public static byte[] unGzip(byte[] buf) throws UtilException {
        return unGzip(new ByteArrayInputStream(buf), buf.length);
    }

    public static byte[] unGzip(InputStream in) throws UtilException {
        return unGzip(in, 32);
    }

    public static byte[] unGzip(InputStream in, int length) throws UtilException {
        FastByteArrayOutputStream bos = new FastByteArrayOutputStream(length);
        Gzip.of(in, bos).unGzip().close();
        return bos.toByteArray();
    }

    public static byte[] zlib(String content, String charset, int level) {
        return zlib(StrUtil.bytes(content, charset), level);
    }

    public static byte[] zlib(File file, int level) {
        BufferedInputStream in = null;
        try {
            in = FileUtil.getInputStream(file);
            byte[] zlib = zlib(in, level, (int) file.length());
            IoUtil.close((Closeable) in);
            return zlib;
        } catch (Throwable th) {
            IoUtil.close((Closeable) in);
            throw th;
        }
    }

    public static byte[] zlib(byte[] buf, int level) {
        return zlib(new ByteArrayInputStream(buf), level, buf.length);
    }

    public static byte[] zlib(InputStream in, int level) {
        return zlib(in, level, 32);
    }

    public static byte[] zlib(InputStream in, int level, int length) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(length);
        Deflate.of(in, out, false).deflater(level);
        return out.toByteArray();
    }

    public static String unZlib(byte[] buf, String charset) {
        return StrUtil.str(unZlib(buf), charset);
    }

    public static byte[] unZlib(byte[] buf) {
        return unZlib(new ByteArrayInputStream(buf), buf.length);
    }

    public static byte[] unZlib(InputStream in) {
        return unZlib(in, 32);
    }

    public static byte[] unZlib(InputStream in, int length) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(length);
        Deflate.of(in, out, false).inflater();
        return out.toByteArray();
    }

    public static List<String> listFileNames(ZipFile zipFile, String dir) {
        if (StrUtil.isNotBlank(dir)) {
            dir = StrUtil.addSuffixIfNot(dir, "/");
        }
        List<String> fileNames = new ArrayList<>();
        Iterator<E> it = new EnumerationIter(zipFile.entries()).iterator();
        while (it.hasNext()) {
            ZipEntry entry = (ZipEntry) it.next();
            String name = entry.getName();
            if (StrUtil.isEmpty(dir) || name.startsWith(dir)) {
                String nameSuffix = StrUtil.removePrefix(name, dir);
                if (StrUtil.isNotEmpty(nameSuffix) && false == StrUtil.contains((CharSequence) nameSuffix, '/')) {
                    fileNames.add(nameSuffix);
                }
            }
        }
        return fileNames;
    }

    private static void validateFiles(File zipFile, File... srcFiles) throws UtilException {
        File parentFile;
        if (zipFile.isDirectory()) {
            throw new UtilException("Zip file [{}] must not be a directory !", zipFile.getAbsoluteFile());
        }
        for (File srcFile : srcFiles) {
            if (null != srcFile) {
                if (false == srcFile.exists()) {
                    throw new UtilException(StrUtil.format("File [{}] not exist!", srcFile.getAbsolutePath()));
                }
                try {
                    parentFile = zipFile.getCanonicalFile().getParentFile();
                } catch (IOException e) {
                    parentFile = zipFile.getParentFile();
                }
                if (srcFile.isDirectory() && FileUtil.isSub(srcFile, parentFile)) {
                    throw new UtilException("Zip file path [{}] must not be the child directory of [{}] !", zipFile.getPath(), srcFile.getPath());
                }
            }
        }
    }
}
