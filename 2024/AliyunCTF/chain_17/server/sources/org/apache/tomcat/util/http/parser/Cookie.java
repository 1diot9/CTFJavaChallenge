package org.apache.tomcat.util.http.parser;

import java.nio.charset.StandardCharsets;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.http.ServerCookie;
import org.apache.tomcat.util.http.ServerCookies;
import org.apache.tomcat.util.log.UserDataHelper;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/Cookie.class */
public class Cookie {
    private static final Log log = LogFactory.getLog((Class<?>) Cookie.class);
    private static final UserDataHelper invalidCookieLog = new UserDataHelper(log);
    private static final StringManager sm = StringManager.getManager("org.apache.tomcat.util.http.parser");
    private static final boolean[] isCookieOctet = new boolean[256];
    private static final boolean[] isText = new boolean[256];
    private static final byte[] EMPTY_BYTES = new byte[0];
    private static final byte TAB_BYTE = 9;
    private static final byte SPACE_BYTE = 32;
    private static final byte QUOTE_BYTE = 34;
    private static final byte COMMA_BYTE = 44;
    private static final byte SEMICOLON_BYTE = 59;
    private static final byte EQUALS_BYTE = 61;
    private static final byte SLASH_BYTE = 92;
    private static final byte DEL_BYTE = Byte.MAX_VALUE;

    static {
        for (int i = 0; i < 256; i++) {
            if (i < 33 || i == 34 || i == 44 || i == 59 || i == 92 || i == 127) {
                isCookieOctet[i] = false;
            } else {
                isCookieOctet[i] = true;
            }
        }
        for (int i2 = 0; i2 < 256; i2++) {
            if (i2 < 9 || ((i2 > 9 && i2 < 32) || i2 == 127)) {
                isText[i2] = false;
            } else {
                isText[i2] = true;
            }
        }
    }

    private Cookie() {
    }

    public static void parseCookie(byte[] bytes, int offset, int len, ServerCookies serverCookies) {
        ByteBuffer bb = new ByteBuffer(bytes, offset, len);
        boolean moreToProcess = true;
        while (moreToProcess) {
            skipLWS(bb);
            int start = bb.position();
            ByteBuffer name = readToken(bb);
            ByteBuffer value = null;
            skipLWS(bb);
            if (skipByte(bb, (byte) 61) == SkipResult.FOUND) {
                skipLWS(bb);
                value = readCookieValueRfc6265(bb);
                if (value == null) {
                    skipUntilSemiColon(bb);
                    logInvalidHeader(start, bb);
                } else {
                    skipLWS(bb);
                }
            }
            SkipResult skipResult = skipByte(bb, (byte) 59);
            if (skipResult != SkipResult.FOUND) {
                if (skipResult == SkipResult.NOT_FOUND) {
                    skipUntilSemiColon(bb);
                    logInvalidHeader(start, bb);
                } else {
                    moreToProcess = false;
                }
            }
            if (name.hasRemaining()) {
                ServerCookie sc = serverCookies.addCookie();
                sc.getName().setBytes(name.array(), name.position(), name.remaining());
                if (value == null) {
                    sc.getValue().setBytes(EMPTY_BYTES, 0, EMPTY_BYTES.length);
                } else {
                    sc.getValue().setBytes(value.array(), value.position(), value.remaining());
                }
            }
        }
    }

    private static void skipLWS(ByteBuffer bb) {
        while (bb.hasRemaining()) {
            byte b = bb.get();
            if (b != 9 && b != 32) {
                bb.rewind();
                return;
            }
        }
    }

    private static void skipUntilSemiColon(ByteBuffer bb) {
        while (bb.hasRemaining() && bb.get() != 59) {
        }
    }

    private static SkipResult skipByte(ByteBuffer bb, byte target) {
        if (!bb.hasRemaining()) {
            return SkipResult.EOF;
        }
        if (bb.get() == target) {
            return SkipResult.FOUND;
        }
        bb.rewind();
        return SkipResult.NOT_FOUND;
    }

    /* JADX WARN: Code restructure failed: missing block: B:25:0x008f, code lost:            return new org.apache.tomcat.util.http.parser.Cookie.ByteBuffer(r7.bytes, r0, r10 - r0);     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static org.apache.tomcat.util.http.parser.Cookie.ByteBuffer readCookieValueRfc6265(org.apache.tomcat.util.http.parser.Cookie.ByteBuffer r7) {
        /*
            r0 = 0
            r8 = r0
            r0 = r7
            boolean r0 = r0.hasRemaining()
            if (r0 == 0) goto L1b
            r0 = r7
            byte r0 = r0.get()
            r1 = 34
            if (r0 != r1) goto L17
            r0 = 1
            r8 = r0
            goto L1b
        L17:
            r0 = r7
            r0.rewind()
        L1b:
            r0 = r7
            int r0 = r0.position()
            r9 = r0
            r0 = r7
            int r0 = r0.limit()
            r10 = r0
        L25:
            r0 = r7
            boolean r0 = r0.hasRemaining()
            if (r0 == 0) goto L80
            r0 = r7
            byte r0 = r0.get()
            r11 = r0
            boolean[] r0 = org.apache.tomcat.util.http.parser.Cookie.isCookieOctet
            r1 = r11
            r2 = 255(0xff, float:3.57E-43)
            r1 = r1 & r2
            r0 = r0[r1]
            if (r0 == 0) goto L42
            goto L7d
        L42:
            r0 = r11
            r1 = 59
            if (r0 == r1) goto L57
            r0 = r11
            r1 = 32
            if (r0 == r1) goto L57
            r0 = r11
            r1 = 9
            if (r0 != r1) goto L66
        L57:
            r0 = r7
            int r0 = r0.position()
            r1 = 1
            int r0 = r0 - r1
            r10 = r0
            r0 = r7
            r1 = r10
            r0.position(r1)
            goto L80
        L66:
            r0 = r8
            if (r0 == 0) goto L7b
            r0 = r11
            r1 = 34
            if (r0 != r1) goto L7b
            r0 = r7
            int r0 = r0.position()
            r1 = 1
            int r0 = r0 - r1
            r10 = r0
            goto L80
        L7b:
            r0 = 0
            return r0
        L7d:
            goto L25
        L80:
            org.apache.tomcat.util.http.parser.Cookie$ByteBuffer r0 = new org.apache.tomcat.util.http.parser.Cookie$ByteBuffer
            r1 = r0
            r2 = r7
            byte[] r2 = r2.bytes
            r3 = r9
            r4 = r10
            r5 = r9
            int r4 = r4 - r5
            r1.<init>(r2, r3, r4)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.http.parser.Cookie.readCookieValueRfc6265(org.apache.tomcat.util.http.parser.Cookie$ByteBuffer):org.apache.tomcat.util.http.parser.Cookie$ByteBuffer");
    }

    private static ByteBuffer readToken(ByteBuffer bb) {
        int start = bb.position();
        int end = bb.limit();
        while (true) {
            if (!bb.hasRemaining()) {
                break;
            }
            if (!HttpParser.isToken(bb.get())) {
                end = bb.position() - 1;
                bb.position(end);
                break;
            }
        }
        return new ByteBuffer(bb.bytes, start, end - start);
    }

    private static void logInvalidHeader(int start, ByteBuffer bb) {
        UserDataHelper.Mode logMode = invalidCookieLog.getNextMode();
        if (logMode != null) {
            String headerValue = new String(bb.array(), start, bb.position() - start, StandardCharsets.UTF_8);
            String message = sm.getString("cookie.invalidCookieValue", headerValue);
            switch (logMode) {
                case INFO_THEN_DEBUG:
                    message = message + sm.getString("cookie.fallToDebug");
                    break;
                case INFO:
                    break;
                case DEBUG:
                    log.debug(message);
                    return;
                default:
                    return;
            }
            log.info(message);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/Cookie$ByteBuffer.class */
    public static class ByteBuffer {
        private final byte[] bytes;
        private int limit;
        private int position;

        ByteBuffer(byte[] bytes, int offset, int len) {
            this.position = 0;
            this.bytes = bytes;
            this.position = offset;
            this.limit = offset + len;
        }

        public int position() {
            return this.position;
        }

        public void position(int position) {
            this.position = position;
        }

        public int limit() {
            return this.limit;
        }

        public int remaining() {
            return this.limit - this.position;
        }

        public boolean hasRemaining() {
            return this.position < this.limit;
        }

        public byte get() {
            byte[] bArr = this.bytes;
            int i = this.position;
            this.position = i + 1;
            return bArr[i];
        }

        public void rewind() {
            this.position--;
        }

        public byte[] array() {
            return this.bytes;
        }

        public String toString() {
            return "position [" + this.position + "], limit [" + this.limit + "]";
        }
    }
}
