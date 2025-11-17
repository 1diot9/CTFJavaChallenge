package cn.hutool.extra.compress;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.compress.archiver.Archiver;
import cn.hutool.extra.compress.archiver.SevenZArchiver;
import cn.hutool.extra.compress.archiver.StreamArchiver;
import cn.hutool.extra.compress.extractor.Extractor;
import cn.hutool.extra.compress.extractor.SevenZExtractor;
import cn.hutool.extra.compress.extractor.StreamExtractor;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.apache.commons.compress.archivers.StreamingNotSupportedException;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/compress/CompressUtil.class */
public class CompressUtil {
    public static CompressorOutputStream getOut(String compressorName, OutputStream out) {
        try {
            return new CompressorStreamFactory().createCompressorOutputStream(compressorName, out);
        } catch (CompressorException e) {
            throw new CompressException((Throwable) e);
        }
    }

    public static CompressorInputStream getIn(String compressorName, InputStream in) {
        InputStream in2 = IoUtil.toMarkSupportStream(in);
        try {
            if (StrUtil.isBlank(compressorName)) {
                compressorName = CompressorStreamFactory.detect(in2);
            }
            return new CompressorStreamFactory().createCompressorInputStream(compressorName, in2);
        } catch (CompressorException e) {
            throw new CompressException((Throwable) e);
        }
    }

    public static Archiver createArchiver(Charset charset, String archiverName, File file) {
        if ("7z".equalsIgnoreCase(archiverName)) {
            return new SevenZArchiver(file);
        }
        return StreamArchiver.create(charset, archiverName, file);
    }

    public static Archiver createArchiver(Charset charset, String archiverName, OutputStream out) {
        if ("7z".equalsIgnoreCase(archiverName)) {
            return new SevenZArchiver(out);
        }
        return StreamArchiver.create(charset, archiverName, out);
    }

    public static Extractor createExtractor(Charset charset, File file) {
        return createExtractor(charset, (String) null, file);
    }

    public static Extractor createExtractor(Charset charset, String archiverName, File file) {
        if ("7z".equalsIgnoreCase(archiverName)) {
            return new SevenZExtractor(file);
        }
        try {
            return new StreamExtractor(charset, archiverName, file);
        } catch (CompressException e) {
            Throwable cause = e.getCause();
            if ((cause instanceof StreamingNotSupportedException) && cause.getMessage().contains("7z")) {
                return new SevenZExtractor(file);
            }
            throw e;
        }
    }

    public static Extractor createExtractor(Charset charset, InputStream in) {
        return createExtractor(charset, (String) null, in);
    }

    public static Extractor createExtractor(Charset charset, String archiverName, InputStream in) {
        if ("7z".equalsIgnoreCase(archiverName)) {
            return new SevenZExtractor(in);
        }
        try {
            return new StreamExtractor(charset, archiverName, in);
        } catch (CompressException e) {
            Throwable cause = e.getCause();
            if ((cause instanceof StreamingNotSupportedException) && cause.getMessage().contains("7z")) {
                return new SevenZExtractor(in);
            }
            throw e;
        }
    }
}
