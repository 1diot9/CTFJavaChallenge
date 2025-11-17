package cn.hutool.core.text;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/text/ASCIIStrCache.class */
public class ASCIIStrCache {
    private static final int ASCII_LENGTH = 128;
    private static final String[] CACHE = new String[128];

    static {
        char c = 0;
        while (true) {
            char c2 = c;
            if (c2 < 128) {
                CACHE[c2] = String.valueOf(c2);
                c = (char) (c2 + 1);
            } else {
                return;
            }
        }
    }

    public static String toString(char c) {
        return c < 128 ? CACHE[c] : String.valueOf(c);
    }
}
