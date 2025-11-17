package org.jooq.tools;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/Ints.class */
public final class Ints {
    private Ints() {
    }

    public static Integer tryParse(String string) {
        return tryParse(string, 0, string.length());
    }

    public static Integer tryParse(String string, int begin, int end) {
        int accum;
        if (begin < 0 || end > string.length() || end - begin < 1) {
            return null;
        }
        char firstChar = string.charAt(begin);
        boolean negative = firstChar == '-';
        int index = (negative || firstChar == '+') ? begin + 1 : begin;
        if (index == end) {
            return null;
        }
        int index2 = index + 1;
        int digit = Character.digit(string.charAt(index), 10);
        if (digit < 0 || digit >= 10) {
            return null;
        }
        int accum2 = -digit;
        int cap = Integer.MIN_VALUE / 10;
        while (index2 < end) {
            int i = index2;
            index2++;
            int digit2 = Character.digit(string.charAt(i), 10);
            if (digit2 < 0 || digit2 >= 10 || accum2 < cap || (accum = accum2 * 10) < Integer.MIN_VALUE + digit2) {
                return null;
            }
            accum2 = accum - digit2;
        }
        if (negative) {
            return Integer.valueOf(accum2);
        }
        if (accum2 == Integer.MIN_VALUE) {
            return null;
        }
        return Integer.valueOf(-accum2);
    }
}
