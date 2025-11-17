package com.fasterxml.jackson.core.io.doubleparser;

import java.util.Arrays;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/doubleparser/AbstractNumberParser.class */
abstract class AbstractNumberParser {
    public static final String ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH = "offset < 0 or length > str.length";
    public static final String SYNTAX_ERROR = "illegal syntax";
    public static final String VALUE_EXCEEDS_LIMITS = "value exceeds limits";
    static final byte DECIMAL_POINT_CLASS = -4;
    static final byte OTHER_CLASS = -1;
    static final byte[] CHAR_TO_HEX_MAP = new byte[256];

    static {
        Arrays.fill(CHAR_TO_HEX_MAP, (byte) -1);
        char c = '0';
        while (true) {
            char ch2 = c;
            if (ch2 > '9') {
                break;
            }
            CHAR_TO_HEX_MAP[ch2] = (byte) (ch2 - '0');
            c = (char) (ch2 + 1);
        }
        char c2 = 'A';
        while (true) {
            char ch3 = c2;
            if (ch3 > 'F') {
                break;
            }
            CHAR_TO_HEX_MAP[ch3] = (byte) ((ch3 - 'A') + 10);
            c2 = (char) (ch3 + 1);
        }
        char c3 = 'a';
        while (true) {
            char ch4 = c3;
            if (ch4 > 'f') {
                break;
            }
            CHAR_TO_HEX_MAP[ch4] = (byte) ((ch4 - 'a') + 10);
            c3 = (char) (ch4 + 1);
        }
        char c4 = '.';
        while (true) {
            char ch5 = c4;
            if (ch5 <= '.') {
                CHAR_TO_HEX_MAP[ch5] = DECIMAL_POINT_CLASS;
                c4 = (char) (ch5 + 1);
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static byte charAt(byte[] str, int i, int endIndex) {
        if (i < endIndex) {
            return str[i];
        }
        return (byte) 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static char charAt(char[] str, int i, int endIndex) {
        if (i < endIndex) {
            return str[i];
        }
        return (char) 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static char charAt(CharSequence str, int i, int endIndex) {
        if (i < endIndex) {
            return str.charAt(i);
        }
        return (char) 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int lookupHex(byte ch2) {
        return CHAR_TO_HEX_MAP[ch2 & 255];
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int lookupHex(char ch2) {
        if (ch2 < 128) {
            return CHAR_TO_HEX_MAP[ch2];
        }
        return -1;
    }
}
