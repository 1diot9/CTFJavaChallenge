package org.jooq.tools;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/Longs.class */
public final class Longs {
    private Longs() {
    }

    public static Long tryParse(String string) {
        if (string.isEmpty()) {
            return null;
        }
        char firstChar = string.charAt(0);
        boolean negative = firstChar == '-';
        int index = (negative || firstChar == '+') ? 1 : 0;
        int length = string.length();
        if (index == length) {
            return null;
        }
        int index2 = index + 1;
        int digit = Character.digit(string.charAt(index), 10);
        if (digit < 0 || digit >= 10) {
            return null;
        }
        long accum = -digit;
        long cap = Long.MIN_VALUE / 10;
        while (index2 < length) {
            int i = index2;
            index2++;
            int digit2 = Character.digit(string.charAt(i), 10);
            if (digit2 >= 0 && digit2 < 10 && accum >= cap) {
                long accum2 = accum * 10;
                if (accum2 < Long.MIN_VALUE + digit2) {
                    return null;
                }
                accum = accum2 - digit2;
            } else {
                return null;
            }
        }
        if (negative) {
            return Long.valueOf(accum);
        }
        if (accum == Long.MIN_VALUE) {
            return null;
        }
        return Long.valueOf(-accum);
    }
}
