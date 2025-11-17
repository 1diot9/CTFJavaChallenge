package cn.hutool.core.codec;

import cn.hutool.core.lang.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/codec/Caesar.class */
public class Caesar {
    public static final String TABLE = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";

    public static String encode(String message, int offset) {
        Assert.notNull(message, "message must be not null!", new Object[0]);
        int len = message.length();
        char[] plain = message.toCharArray();
        for (int i = 0; i < len; i++) {
            char c = message.charAt(i);
            if (false != Character.isLetter(c)) {
                plain[i] = encodeChar(c, offset);
            }
        }
        return new String(plain);
    }

    public static String decode(String cipherText, int offset) {
        Assert.notNull(cipherText, "cipherText must be not null!", new Object[0]);
        int len = cipherText.length();
        char[] plain = cipherText.toCharArray();
        for (int i = 0; i < len; i++) {
            char c = cipherText.charAt(i);
            if (false != Character.isLetter(c)) {
                plain[i] = decodeChar(c, offset);
            }
        }
        return new String(plain);
    }

    private static char encodeChar(char c, int offset) {
        int position = (TABLE.indexOf(c) + offset) % 52;
        return TABLE.charAt(position);
    }

    private static char decodeChar(char c, int offset) {
        int position = (TABLE.indexOf(c) - offset) % 52;
        if (position < 0) {
            position += 52;
        }
        return TABLE.charAt(position);
    }
}
