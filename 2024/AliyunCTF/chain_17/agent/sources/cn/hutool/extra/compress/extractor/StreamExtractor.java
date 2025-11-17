package cn.hutool.extra.compress.extractor;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.compress.CompressException;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/compress/extractor/StreamExtractor.class */
public class StreamExtractor implements Extractor {
    private final ArchiveInputStream in;

    public StreamExtractor(Charset charset, File file) {
        this(charset, (String) null, file);
    }

    public StreamExtractor(Charset charset, String archiverName, File file) {
        this(charset, archiverName, FileUtil.getInputStream(file));
    }

    public StreamExtractor(Charset charset, InputStream in) {
        this(charset, (String) null, in);
    }

    public StreamExtractor(Charset charset, String archiverName, InputStream in) {
        if (in instanceof ArchiveInputStream) {
            this.in = (ArchiveInputStream) in;
            return;
        }
        ArchiveStreamFactory factory = new ArchiveStreamFactory(charset.name());
        try {
            in = IoUtil.toBuffered(in);
            if (StrUtil.isBlank(archiverName)) {
                this.in = factory.createArchiveInputStream(in);
            } else if ("tgz".equalsIgnoreCase(archiverName) || "tar.gz".equalsIgnoreCase(archiverName)) {
                try {
                    this.in = new TarArchiveInputStream(new GzipCompressorInputStream(in));
                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
            } else {
                this.in = factory.createArchiveInputStream(archiverName, in);
            }
        } catch (ArchiveException e2) {
            IoUtil.close((Closeable) in);
            throw new CompressException((Throwable) e2);
        }
    }

    @Override // cn.hutool.extra.compress.extractor.Extractor
    public void extract(File targetDir, int stripComponents, Filter<ArchiveEntry> filter) {
        try {
            try {
                extractInternal(targetDir, stripComponents, filter);
                close();
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } catch (Throwable th) {
            close();
            throw th;
        }
    }

    private void extractInternal(File targetDir, int stripComponents, Filter<ArchiveEntry> filter) throws IOException {
        String entryName;
        Assert.isTrue(null != targetDir && (false == targetDir.exists() || targetDir.isDirectory()), "target must be dir.", new Object[0]);
        ArchiveInputStream in = this.in;
        while (true) {
            ArchiveEntry entry = in.getNextEntry();
            if (null != entry) {
                if (null == filter || false != filter.accept(entry)) {
                    if (false != in.canReadEntryData(entry) && (entryName = stripName(entry.getName(), stripComponents)) != null) {
                        File outItemFile = FileUtil.file(targetDir, entryName);
                        if (entry.isDirectory()) {
                            outItemFile.mkdirs();
                        } else {
                            FileUtil.writeFromStream(in, outItemFile, false);
                        }
                    }
                }
            } else {
                return;
            }
        }
    }

    @Override // cn.hutool.extra.compress.extractor.Extractor, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        IoUtil.close((Closeable) this.in);
    }
}
