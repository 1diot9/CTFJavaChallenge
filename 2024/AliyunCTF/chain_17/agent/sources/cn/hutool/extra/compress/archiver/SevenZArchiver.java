package cn.hutool.extra.compress.archiver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.SeekableByteChannel;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/compress/archiver/SevenZArchiver.class */
public class SevenZArchiver implements Archiver {
    private final SevenZOutputFile sevenZOutputFile;
    private SeekableByteChannel channel;
    private OutputStream out;

    @Override // cn.hutool.extra.compress.archiver.Archiver
    public /* bridge */ /* synthetic */ Archiver add(File file, String str, Filter filter) {
        return add(file, str, (Filter<File>) filter);
    }

    public SevenZArchiver(File file) {
        try {
            this.sevenZOutputFile = new SevenZOutputFile(file);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public SevenZArchiver(OutputStream out) {
        this.out = out;
        this.channel = new SeekableInMemoryByteChannel();
        try {
            this.sevenZOutputFile = new SevenZOutputFile(this.channel);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public SevenZArchiver(SeekableByteChannel channel) {
        try {
            this.sevenZOutputFile = new SevenZOutputFile(channel);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public SevenZOutputFile getSevenZOutputFile() {
        return this.sevenZOutputFile;
    }

    @Override // cn.hutool.extra.compress.archiver.Archiver
    public SevenZArchiver add(File file, String path, Filter<File> filter) {
        try {
            addInternal(file, path, filter);
            return this;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override // cn.hutool.extra.compress.archiver.Archiver
    public SevenZArchiver finish() {
        try {
            this.sevenZOutputFile.finish();
            return this;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override // cn.hutool.extra.compress.archiver.Archiver, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        try {
            finish();
        } catch (Exception e) {
        }
        if (null != this.out && (this.channel instanceof SeekableInMemoryByteChannel)) {
            try {
                this.out.write(this.channel.array());
            } catch (IOException e2) {
                throw new IORuntimeException(e2);
            }
        }
        IoUtil.close((Closeable) this.sevenZOutputFile);
    }

    private void addInternal(File file, String path, Filter<File> filter) throws IOException {
        String entryName;
        if (null != filter && false == filter.accept(file)) {
            return;
        }
        SevenZOutputFile out = this.sevenZOutputFile;
        if (StrUtil.isNotEmpty(path)) {
            entryName = StrUtil.addSuffixIfNot(path, "/") + file.getName();
        } else {
            entryName = file.getName();
        }
        out.putArchiveEntry(out.createArchiveEntry(file, entryName));
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (ArrayUtil.isNotEmpty((Object[]) files)) {
                for (File childFile : files) {
                    addInternal(childFile, entryName, filter);
                }
                return;
            }
            return;
        }
        if (file.isFile()) {
            out.write(FileUtil.readBytes(file));
        }
        out.closeArchiveEntry();
    }
}
