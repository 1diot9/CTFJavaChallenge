package org.jooq.tools.jdbc;

import java.io.File;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import org.jooq.Source;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/jdbc/MockFileDatabaseConfiguration.class */
public final class MockFileDatabaseConfiguration {
    final LineNumberReader in;
    final boolean patterns;
    final String nullLiteral;

    public MockFileDatabaseConfiguration() {
        this(new LineNumberReader(new StringReader("")), false, null);
    }

    private MockFileDatabaseConfiguration(LineNumberReader in, boolean patterns, String nullLiteral) {
        this.in = in;
        this.patterns = patterns;
        this.nullLiteral = nullLiteral;
    }

    public final MockFileDatabaseConfiguration source(File file) {
        return source(Source.of(file, "UTF-8"));
    }

    public final MockFileDatabaseConfiguration source(File file, String encoding) {
        return source(Source.of(file, encoding));
    }

    public final MockFileDatabaseConfiguration source(InputStream stream) {
        return source(Source.of(stream, "UTF-8"));
    }

    public final MockFileDatabaseConfiguration source(InputStream stream, String encoding) {
        return source(Source.of(stream, encoding));
    }

    public final MockFileDatabaseConfiguration source(Source source) {
        return source(source.reader());
    }

    public final MockFileDatabaseConfiguration source(Reader reader) {
        return new MockFileDatabaseConfiguration(new LineNumberReader(reader), this.patterns, this.nullLiteral);
    }

    public final MockFileDatabaseConfiguration source(String string) {
        return source(Source.of(string));
    }

    public final MockFileDatabaseConfiguration patterns(boolean newPatterns) {
        return new MockFileDatabaseConfiguration(this.in, newPatterns, this.nullLiteral);
    }

    public final MockFileDatabaseConfiguration nullLiteral(String newNullLiteral) {
        return new MockFileDatabaseConfiguration(this.in, this.patterns, newNullLiteral);
    }
}
