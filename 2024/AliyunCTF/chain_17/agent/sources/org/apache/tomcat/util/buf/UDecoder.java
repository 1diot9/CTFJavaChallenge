package org.apache.tomcat.util.buf;

import java.io.ByteArrayOutputStream;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/UDecoder.class */
public final class UDecoder {
    private static final StringManager sm = StringManager.getManager((Class<?>) UDecoder.class);
    private static final IOException EXCEPTION_EOF = new DecodeException(sm.getString("uDecoder.eof"));
    private static final IOException EXCEPTION_NOT_HEX_DIGIT = new DecodeException(sm.getString("uDecoder.isHexDigit"));
    private static final IOException EXCEPTION_SLASH = new DecodeException(sm.getString("uDecoder.noSlash"));

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/UDecoder$DecodeException.class */
    private static class DecodeException extends CharConversionException {
        private static final long serialVersionUID = 1;

        DecodeException(String s) {
            super(s);
        }

        @Override // java.lang.Throwable
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }

    public void convert(ByteChunk mb, boolean query) throws IOException {
        if (query) {
            convert(mb, true, EncodedSolidusHandling.DECODE);
        } else {
            convert(mb, false, EncodedSolidusHandling.REJECT);
        }
    }

    public void convert(ByteChunk mb, EncodedSolidusHandling encodedSolidusHandling) throws IOException {
        convert(mb, false, encodedSolidusHandling);
    }

    private void convert(ByteChunk mb, boolean query, EncodedSolidusHandling encodedSolidusHandling) throws IOException {
        int start = mb.getOffset();
        byte[] buff = mb.getBytes();
        int end = mb.getEnd();
        int idx = ByteChunk.findByte(buff, start, end, (byte) 37);
        int idx2 = -1;
        if (query) {
            idx2 = ByteChunk.findByte(buff, start, idx >= 0 ? idx : end, (byte) 43);
        }
        if (idx < 0 && idx2 < 0) {
            return;
        }
        if ((idx2 >= 0 && idx2 < idx) || idx < 0) {
            idx = idx2;
        }
        int j = idx;
        while (j < end) {
            if (buff[j] == 43 && query) {
                buff[idx] = 32;
            } else if (buff[j] != 37) {
                buff[idx] = buff[j];
            } else {
                if (j + 2 >= end) {
                    throw EXCEPTION_EOF;
                }
                byte b1 = buff[j + 1];
                byte b2 = buff[j + 2];
                if (!isHexDigit(b1) || !isHexDigit(b2)) {
                    throw EXCEPTION_NOT_HEX_DIGIT;
                }
                j += 2;
                int res = x2c(b1, b2);
                if (res == 47) {
                    switch (encodedSolidusHandling) {
                        case DECODE:
                            buff[idx] = (byte) res;
                            break;
                        case REJECT:
                            throw EXCEPTION_SLASH;
                        case PASS_THROUGH:
                            int i = idx;
                            int idx3 = idx + 1;
                            buff[i] = buff[j - 2];
                            idx = idx3 + 1;
                            buff[idx3] = buff[j - 1];
                            buff[idx] = buff[j];
                            break;
                    }
                } else {
                    buff[idx] = (byte) res;
                }
            }
            j++;
            idx++;
        }
        mb.setEnd(idx);
    }

    public static String URLDecode(String str, Charset charset) {
        if (str == null) {
            return null;
        }
        if (str.indexOf(37) == -1) {
            return str;
        }
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream(str.length() * 2);
        OutputStreamWriter osw = new OutputStreamWriter(baos, charset);
        char[] sourceChars = str.toCharArray();
        int len = sourceChars.length;
        int ix = 0;
        while (ix < len) {
            try {
                int i = ix;
                ix++;
                char c = sourceChars[i];
                if (c == '%') {
                    osw.flush();
                    if (ix + 2 > len) {
                        throw new IllegalArgumentException(sm.getString("uDecoder.urlDecode.missingDigit", str));
                    }
                    int ix2 = ix + 1;
                    char c1 = sourceChars[ix];
                    ix = ix2 + 1;
                    char c2 = sourceChars[ix2];
                    if (isHexDigit(c1) && isHexDigit(c2)) {
                        baos.write(x2c(c1, c2));
                    } else {
                        throw new IllegalArgumentException(sm.getString("uDecoder.urlDecode.missingDigit", str));
                    }
                } else {
                    osw.append(c);
                }
            } catch (IOException ioe) {
                throw new IllegalArgumentException(sm.getString("uDecoder.urlDecode.conversionError", str, charset.name()), ioe);
            }
        }
        osw.flush();
        return baos.toString(charset.name());
    }

    private static boolean isHexDigit(int c) {
        return (c >= 48 && c <= 57) || (c >= 97 && c <= 102) || (c >= 65 && c <= 70);
    }

    private static int x2c(byte b1, byte b2) {
        int digit = b1 >= 65 ? ((b1 & 223) - 65) + 10 : b1 - 48;
        return (digit * 16) + (b2 >= 65 ? ((b2 & 223) - 65) + 10 : b2 - 48);
    }

    private static int x2c(char b1, char b2) {
        int digit = b1 >= 'A' ? ((b1 & 223) - 65) + 10 : b1 - '0';
        return (digit * 16) + (b2 >= 'A' ? ((b2 & 223) - 65) + 10 : b2 - '0');
    }
}
