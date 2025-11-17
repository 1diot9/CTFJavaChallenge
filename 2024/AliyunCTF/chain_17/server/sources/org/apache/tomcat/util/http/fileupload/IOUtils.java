package org.apache.tomcat.util.http.fileupload;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/IOUtils.class */
public class IOUtils {
    public static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 4096;

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > 2147483647L) {
            return -1;
        }
        return (int) count;
    }

    public static long copyLarge(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0;
        while (true) {
            int n = input.read(buffer);
            if (-1 != n) {
                output.write(buffer, 0, n);
                count += n;
            } else {
                return count;
            }
        }
    }

    public static int read(InputStream input, byte[] buffer, int offset, int length) throws IOException {
        int remaining;
        if (length < 0) {
            throw new IllegalArgumentException("Length must not be negative: " + length);
        }
        int i = length;
        while (true) {
            remaining = i;
            if (remaining <= 0) {
                break;
            }
            int location = length - remaining;
            int count = input.read(buffer, offset + location, remaining);
            if (-1 == count) {
                break;
            }
            i = remaining - count;
        }
        return length - remaining;
    }

    public static void readFully(InputStream input, byte[] buffer, int offset, int length) throws IOException {
        int actual = read(input, buffer, offset, length);
        if (actual != length) {
            throw new EOFException("Length to read: " + length + " actual: " + actual);
        }
    }

    public static void readFully(InputStream input, byte[] buffer) throws IOException {
        readFully(input, buffer, 0, buffer.length);
    }
}
