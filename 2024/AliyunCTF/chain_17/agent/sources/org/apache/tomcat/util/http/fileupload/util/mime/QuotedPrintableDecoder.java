package org.apache.tomcat.util.http.fileupload.util.mime;

import java.io.IOException;
import java.io.OutputStream;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/util/mime/QuotedPrintableDecoder.class */
final class QuotedPrintableDecoder {
    private static final int UPPER_NIBBLE_SHIFT = 4;

    private QuotedPrintableDecoder() {
    }

    public static int decode(byte[] data, OutputStream out) throws IOException {
        int off = 0;
        int length = data.length;
        int endOffset = 0 + length;
        int bytesWritten = 0;
        while (off < endOffset) {
            int i = off;
            off++;
            byte ch2 = data[i];
            if (ch2 == 95) {
                out.write(32);
            } else if (ch2 == 61) {
                if (off + 1 >= endOffset) {
                    throw new IOException("Invalid quoted printable encoding; truncated escape sequence");
                }
                int off2 = off + 1;
                byte b1 = data[off];
                off = off2 + 1;
                byte b2 = data[off2];
                if (b1 == 13) {
                    if (b2 != 10) {
                        throw new IOException("Invalid quoted printable encoding; CR must be followed by LF");
                    }
                } else {
                    int c1 = hexToBinary(b1);
                    int c2 = hexToBinary(b2);
                    out.write((c1 << 4) | c2);
                    bytesWritten++;
                }
            } else {
                out.write(ch2);
                bytesWritten++;
            }
        }
        return bytesWritten;
    }

    private static int hexToBinary(byte b) throws IOException {
        int i = Character.digit((char) b, 16);
        if (i == -1) {
            throw new IOException("Invalid quoted printable encoding: not a valid hex digit: " + b);
        }
        return i;
    }
}
