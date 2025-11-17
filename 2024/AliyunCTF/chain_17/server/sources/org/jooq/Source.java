package org.jooq;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import org.jooq.exception.IOException;
import org.jooq.tools.jdbc.JDBCUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Source.class */
public final class Source {
    private final String string;
    private final byte[] bytes;
    private final String charsetName;
    private final Charset charset;
    private final CharsetDecoder charsetDecoder;
    private final Reader reader;
    private final InputStream inputStream;
    private final java.io.File file;
    private final int length;

    private Source(String string, byte[] bytes, String charsetName, Charset charset, CharsetDecoder charsetDecoder, Reader reader, InputStream inputStream, java.io.File file, int length) {
        this.string = string;
        this.bytes = bytes;
        this.charsetName = charsetName;
        this.charset = charset;
        this.charsetDecoder = charsetDecoder;
        this.reader = reader;
        this.inputStream = inputStream;
        this.file = file;
        this.length = length;
    }

    public static final Source of(String string) {
        return new Source(string, null, null, null, null, null, null, null, -1);
    }

    public static final Source of(byte[] bytes) {
        return of(bytes, (Charset) null);
    }

    public static final Source of(byte[] bytes, String charsetName) {
        return new Source(null, bytes, charsetName, null, null, null, null, null, -1);
    }

    public static final Source of(byte[] bytes, Charset charset) {
        return new Source(null, bytes, null, charset, null, null, null, null, -1);
    }

    public static final Source of(byte[] bytes, CharsetDecoder charsetDecoder) {
        return new Source(null, bytes, null, null, charsetDecoder, null, null, null, -1);
    }

    public static final Source of(java.io.File file) {
        return new Source(null, null, null, null, null, null, null, file, -1);
    }

    public static final Source of(java.io.File file, String charsetName) {
        return new Source(null, null, charsetName, null, null, null, null, file, -1);
    }

    public static final Source of(java.io.File file, Charset charset) {
        return new Source(null, null, null, charset, null, null, null, file, -1);
    }

    public static final Source of(java.io.File file, CharsetDecoder charsetDecoder) {
        return new Source(null, null, null, null, charsetDecoder, null, null, file, -1);
    }

    public static final Source of(Reader reader) {
        return of(reader, -1);
    }

    public static final Source of(Reader reader, int length) {
        return new Source(null, null, null, null, null, reader, null, null, length);
    }

    public static final Source of(InputStream inputStream) {
        return of(inputStream, -1);
    }

    public static final Source of(InputStream inputStream, String charsetName) {
        return of(inputStream, -1, charsetName);
    }

    public static final Source of(InputStream inputStream, Charset charset) {
        return of(inputStream, -1, charset);
    }

    public static final Source of(InputStream inputStream, CharsetDecoder charsetDecoder) {
        return of(inputStream, -1, charsetDecoder);
    }

    public static final Source of(InputStream inputStream, int length) {
        return new Source(null, null, null, null, null, null, inputStream, null, length);
    }

    public static final Source of(InputStream inputStream, int length, String charsetName) {
        return new Source(null, null, charsetName, null, null, null, inputStream, null, length);
    }

    public static final Source of(InputStream inputStream, int length, Charset charset) {
        return new Source(null, null, null, charset, null, null, inputStream, null, length);
    }

    public static final Source of(InputStream inputStream, int length, CharsetDecoder charsetDecoder) {
        return new Source(null, null, null, null, charsetDecoder, null, inputStream, null, length);
    }

    public final Reader reader() throws IOException {
        try {
            if (this.string != null) {
                return new StringReader(this.string);
            }
            if (this.bytes != null) {
                if (this.length > -1) {
                    return inputStreamReader(new ByteArrayInputStream(this.bytes, 0, this.length));
                }
                return inputStreamReader(new ByteArrayInputStream(this.bytes));
            }
            if (this.reader != null) {
                if (this.length > -1) {
                    return new LengthLimitedReader(this.reader, this.length);
                }
                return this.reader;
            }
            if (this.inputStream != null) {
                if (this.length > -1) {
                    return inputStreamReader(new LengthLimitedInputStream(this.inputStream, this.length));
                }
                return inputStreamReader(this.inputStream);
            }
            if (this.file != null) {
                return new BufferedReader(inputStreamReader(new FileInputStream(this.file)));
            }
            throw new IllegalStateException("Could not produce a reader from this source");
        } catch (java.io.IOException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Source$LengthLimitedInputStream.class */
    public static class LengthLimitedInputStream extends InputStream {
        final InputStream is;
        int length;

        LengthLimitedInputStream(InputStream is, int length) {
            this.length = length;
            this.is = is;
        }

        @Override // java.io.InputStream
        public int read() throws java.io.IOException {
            if (this.length > 0) {
                this.length--;
                return this.is.read();
            }
            return -1;
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws java.io.IOException {
            this.is.close();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Source$LengthLimitedReader.class */
    public static class LengthLimitedReader extends Reader {
        final Reader reader;
        int length;

        LengthLimitedReader(Reader reader, int length) {
            this.length = length;
            this.reader = reader;
        }

        @Override // java.io.Reader
        public int read(char[] cbuf, int off, int len) throws java.io.IOException {
            if (this.length > 0) {
                int r = this.reader.read(cbuf, off, Math.min(this.length, len));
                this.length -= len;
                return r;
            }
            return -1;
        }

        @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws java.io.IOException {
            this.reader.close();
        }
    }

    public final String readString() throws IOException {
        StringWriter w = new StringWriter();
        Reader r = null;
        try {
            try {
                r = reader();
                char[] buffer = new char[8192];
                while (true) {
                    int read = r.read(buffer, 0, 8192);
                    if (read >= 0) {
                        w.write(buffer, 0, read);
                    } else {
                        JDBCUtils.safeClose((Closeable) r);
                        return w.toString();
                    }
                }
            } catch (java.io.IOException e) {
                throw new IOException("Could not read source", e);
            }
        } catch (Throwable th) {
            JDBCUtils.safeClose((Closeable) r);
            throw th;
        }
    }

    private final Reader inputStreamReader(InputStream is) throws UnsupportedEncodingException {
        if (this.charsetName != null) {
            return new InputStreamReader(is, this.charsetName);
        }
        if (this.charset != null) {
            return new InputStreamReader(is, this.charset);
        }
        if (this.charsetDecoder != null) {
            return new InputStreamReader(is, this.charsetDecoder);
        }
        return new InputStreamReader(is);
    }

    public String toString() {
        if (this.string != null) {
            return this.string;
        }
        if (this.bytes != null) {
            return readString();
        }
        if (this.reader != null) {
            return "Source (Reader)";
        }
        if (this.inputStream != null) {
            return "Source (InputStream)";
        }
        if (this.file != null) {
            return "Source (" + String.valueOf(this.file) + ")";
        }
        return "Source (other)";
    }
}
