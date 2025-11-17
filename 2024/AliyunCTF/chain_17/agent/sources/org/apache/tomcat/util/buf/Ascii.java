package org.apache.tomcat.util.buf;

import org.apache.tomcat.util.bcel.Const;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/Ascii.class */
public final class Ascii {
    private static final byte[] toLower = new byte[256];
    private static final boolean[] isDigit = new boolean[256];
    private static final long OVERFLOW_LIMIT = 922337203685477580L;

    static {
        for (int i = 0; i < 256; i++) {
            toLower[i] = (byte) i;
        }
        for (int lc = 97; lc <= 122; lc++) {
            int uc = (lc + 65) - 97;
            toLower[uc] = (byte) lc;
        }
        for (int d = 48; d <= 57; d++) {
            isDigit[d] = true;
        }
    }

    public static int toLower(int c) {
        return toLower[c & Const.MAX_ARRAY_DIMENSIONS] & 255;
    }

    private static boolean isDigit(int c) {
        return isDigit[c & Const.MAX_ARRAY_DIMENSIONS];
    }

    public static long parseLong(byte[] b, int off, int len) throws NumberFormatException {
        if (b != null && len > 0) {
            int off2 = off + 1;
            byte b2 = b[off];
            if (isDigit(b2)) {
                long j = b2 - 48;
                while (true) {
                    long n = j;
                    len--;
                    if (len > 0) {
                        int i = off2;
                        off2++;
                        byte b3 = b[i];
                        if (!isDigit(b3) || (n >= OVERFLOW_LIMIT && (n != OVERFLOW_LIMIT || b3 - 48 >= 8))) {
                            break;
                        }
                        j = ((n * 10) + b3) - 48;
                    } else {
                        return n;
                    }
                }
                throw new NumberFormatException();
            }
        }
        throw new NumberFormatException();
    }
}
