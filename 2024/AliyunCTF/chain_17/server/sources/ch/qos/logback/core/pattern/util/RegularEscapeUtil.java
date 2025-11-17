package ch.qos.logback.core.pattern.util;

import org.springframework.asm.Opcodes;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/pattern/util/RegularEscapeUtil.class */
public class RegularEscapeUtil implements IEscapeUtil {
    @Override // ch.qos.logback.core.pattern.util.IEscapeUtil
    public void escape(String escapeChars, StringBuffer buf, char next, int pointer) {
        if (escapeChars.indexOf(next) >= 0) {
            buf.append(next);
            return;
        }
        switch (next) {
            case '\\':
                buf.append(next);
                return;
            case Opcodes.SWAP /* 95 */:
                return;
            case Opcodes.FDIV /* 110 */:
                buf.append('\n');
                return;
            case Opcodes.FREM /* 114 */:
                buf.append('\r');
                return;
            case 't':
                buf.append('\t');
                return;
            default:
                String commaSeperatedEscapeChars = formatEscapeCharsForListing(escapeChars);
                throw new IllegalArgumentException("Illegal char '" + next + " at column " + pointer + ". Only \\\\, \\_" + commaSeperatedEscapeChars + ", \\t, \\n, \\r combinations are allowed as escape characters.");
        }
    }

    String formatEscapeCharsForListing(String escapeChars) {
        StringBuilder commaSeperatedEscapeChars = new StringBuilder();
        for (int i = 0; i < escapeChars.length(); i++) {
            commaSeperatedEscapeChars.append(", \\").append(escapeChars.charAt(i));
        }
        return commaSeperatedEscapeChars.toString();
    }

    public static String basicEscape(String s) {
        int len = s.length();
        StringBuilder sbuf = new StringBuilder(len);
        int i = 0;
        while (i < len) {
            int i2 = i;
            i++;
            char c = s.charAt(i2);
            if (c == '\\' && i < len) {
                i++;
                c = s.charAt(i);
                if (c == 'n') {
                    c = '\n';
                } else if (c == 'r') {
                    c = '\r';
                } else if (c == 't') {
                    c = '\t';
                } else if (c == 'f') {
                    c = '\f';
                } else if (c == '\b') {
                    c = '\b';
                } else if (c == '\"') {
                    c = '\"';
                } else if (c == '\'') {
                    c = '\'';
                } else if (c == '\\') {
                    c = '\\';
                }
            }
            sbuf.append(c);
        }
        return sbuf.toString();
    }
}
