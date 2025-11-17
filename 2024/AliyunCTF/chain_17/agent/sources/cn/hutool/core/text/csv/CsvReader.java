package cn.hutool.core.text.csv;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/text/csv/CsvReader.class */
public class CsvReader extends CsvBaseReader implements Iterable<CsvRow>, Closeable {
    private static final long serialVersionUID = 1;
    private final Reader reader;

    public CsvReader() {
        this(null);
    }

    public CsvReader(CsvReadConfig config) {
        this((Reader) null, config);
    }

    public CsvReader(File file, CsvReadConfig config) {
        this(file, DEFAULT_CHARSET, config);
    }

    public CsvReader(Path path, CsvReadConfig config) {
        this(path, DEFAULT_CHARSET, config);
    }

    public CsvReader(File file, Charset charset, CsvReadConfig config) {
        this(FileUtil.getReader(file, charset), config);
    }

    public CsvReader(Path path, Charset charset, CsvReadConfig config) {
        this(FileUtil.getReader(path, charset), config);
    }

    public CsvReader(Reader reader, CsvReadConfig config) {
        super(config);
        this.reader = reader;
    }

    public CsvData read() throws IORuntimeException {
        return read(this.reader);
    }

    public void read(CsvRowHandler rowHandler) throws IORuntimeException {
        read(this.reader, rowHandler);
    }

    public Stream<CsvRow> stream() {
        return (Stream) StreamSupport.stream(spliterator(), false).onClose(() -> {
            try {
                close();
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        });
    }

    @Override // java.lang.Iterable
    public Iterator<CsvRow> iterator() {
        return parse(this.reader);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        IoUtil.close((Closeable) this.reader);
    }
}
