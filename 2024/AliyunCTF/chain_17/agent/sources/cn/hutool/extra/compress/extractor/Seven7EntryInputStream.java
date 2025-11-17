package cn.hutool.extra.compress.extractor;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/compress/extractor/Seven7EntryInputStream.class */
public class Seven7EntryInputStream extends InputStream {
    private final SevenZFile sevenZFile;
    private final long size;
    private long readSize = 0;

    public Seven7EntryInputStream(SevenZFile sevenZFile, SevenZArchiveEntry entry) {
        this.sevenZFile = sevenZFile;
        this.size = entry.getSize();
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        try {
            return Math.toIntExact(this.size);
        } catch (ArithmeticException e) {
            throw new IOException("Entry size is too large!(max than Integer.MAX)", e);
        }
    }

    public long getReadSize() {
        return this.readSize;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        this.readSize++;
        return this.sevenZFile.read();
    }
}
