package org.springframework.util;

import ch.qos.logback.core.FileAppender;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/StreamUtils.class */
public abstract class StreamUtils {
    public static final int BUFFER_SIZE = 8192;
    private static final byte[] EMPTY_CONTENT = new byte[0];

    public static byte[] copyToByteArray(@Nullable InputStream in) throws IOException {
        if (in == null) {
            return EMPTY_CONTENT;
        }
        return in.readAllBytes();
    }

    public static String copyToString(@Nullable InputStream in, Charset charset) throws IOException {
        if (in == null) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(in, charset);
        char[] buffer = new char[8192];
        while (true) {
            int charsRead = reader.read(buffer);
            if (charsRead != -1) {
                out.append(buffer, 0, charsRead);
            } else {
                return out.toString();
            }
        }
    }

    public static String copyToString(ByteArrayOutputStream baos, Charset charset) {
        Assert.notNull(baos, "No ByteArrayOutputStream specified");
        Assert.notNull(charset, "No Charset specified");
        return baos.toString(charset);
    }

    public static void copy(byte[] in, OutputStream out) throws IOException {
        Assert.notNull(in, "No input byte array specified");
        Assert.notNull(out, "No OutputStream specified");
        out.write(in);
        out.flush();
    }

    public static void copy(String in, Charset charset, OutputStream out) throws IOException {
        Assert.notNull(in, "No input String specified");
        Assert.notNull(charset, "No Charset specified");
        Assert.notNull(out, "No OutputStream specified");
        out.write(in.getBytes(charset));
        out.flush();
    }

    public static int copy(InputStream in, OutputStream out) throws IOException {
        Assert.notNull(in, "No InputStream specified");
        Assert.notNull(out, "No OutputStream specified");
        int count = (int) in.transferTo(out);
        out.flush();
        return count;
    }

    public static long copyRange(InputStream in, OutputStream out, long start, long end) throws IOException {
        int bytesRead;
        Assert.notNull(in, "No InputStream specified");
        Assert.notNull(out, "No OutputStream specified");
        long skipped = in.skip(start);
        if (skipped < start) {
            IOException iOException = new IOException("Skipped only " + skipped + " bytes out of " + iOException + " required");
            throw iOException;
        }
        long bytesToCopy = (end - start) + 1;
        byte[] buffer = new byte[(int) Math.min(FileAppender.DEFAULT_BUFFER_SIZE, bytesToCopy)];
        while (bytesToCopy > 0 && (bytesRead = in.read(buffer)) != -1) {
            if (bytesRead <= bytesToCopy) {
                out.write(buffer, 0, bytesRead);
                bytesToCopy -= bytesRead;
            } else {
                out.write(buffer, 0, (int) bytesToCopy);
                bytesToCopy = 0;
            }
        }
        return ((end - start) + 1) - bytesToCopy;
    }

    public static int drain(InputStream in) throws IOException {
        Assert.notNull(in, "No InputStream specified");
        return (int) in.transferTo(OutputStream.nullOutputStream());
    }

    @Deprecated(since = "6.0")
    public static InputStream emptyInput() {
        return InputStream.nullInputStream();
    }

    public static InputStream nonClosing(InputStream in) {
        Assert.notNull(in, "No InputStream specified");
        return new NonClosingInputStream(in);
    }

    public static OutputStream nonClosing(OutputStream out) {
        Assert.notNull(out, "No OutputStream specified");
        return new NonClosingOutputStream(out);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/StreamUtils$NonClosingInputStream.class */
    private static class NonClosingInputStream extends FilterInputStream {
        public NonClosingInputStream(InputStream in) {
            super(in);
        }

        @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/StreamUtils$NonClosingOutputStream.class */
    private static class NonClosingOutputStream extends FilterOutputStream {
        public NonClosingOutputStream(OutputStream out) {
            super(out);
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream
        public void write(byte[] b, int off, int let) throws IOException {
            this.out.write(b, off, let);
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
        }
    }
}
