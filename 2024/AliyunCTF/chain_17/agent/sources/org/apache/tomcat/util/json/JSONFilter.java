package org.apache.tomcat.util.json;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/json/JSONFilter.class */
public class JSONFilter {
    public static char[] escape(char c) {
        if (c < ' ' || c == '\"' || c == '\\' || Character.isHighSurrogate(c) || Character.isLowSurrogate(c)) {
            char popular = getPopularChar(c);
            if (popular > 0) {
                return new char[]{'\\', popular};
            }
            StringBuilder escaped = new StringBuilder(6);
            escaped.append("\\u");
            escaped.append(String.format("%04X", Integer.valueOf(c)));
            return escaped.toString().toCharArray();
        }
        char[] result = {c};
        return result;
    }

    public static String escape(String input) {
        return escape(input, 0, input.length()).toString();
    }

    public static CharSequence escape(CharSequence input) {
        return escape(input, 0, input.length());
    }

    public static CharSequence escape(CharSequence input, int off, int length) {
        StringBuilder escaped = null;
        int lastUnescapedStart = off;
        for (int i = off; i < length; i++) {
            char c = input.charAt(i);
            if (c < ' ' || c == '\"' || c == '\\' || Character.isHighSurrogate(c) || Character.isLowSurrogate(c)) {
                if (escaped == null) {
                    escaped = new StringBuilder(length + 20);
                }
                if (lastUnescapedStart < i) {
                    escaped.append(input.subSequence(lastUnescapedStart, i));
                }
                lastUnescapedStart = i + 1;
                char popular = getPopularChar(c);
                if (popular > 0) {
                    escaped.append('\\').append(popular);
                } else {
                    escaped.append("\\u");
                    escaped.append(String.format("%04X", Integer.valueOf(c)));
                }
            }
        }
        if (escaped == null) {
            if (off == 0 && length == input.length()) {
                return input;
            }
            return input.subSequence(off, length - off);
        }
        if (lastUnescapedStart < length) {
            escaped.append(input.subSequence(lastUnescapedStart, length));
        }
        return escaped.toString();
    }

    private JSONFilter() {
    }

    private static char getPopularChar(char c) {
        switch (c) {
            case '\b':
                return 'b';
            case '\t':
                return 't';
            case '\n':
                return 'n';
            case '\f':
                return 'f';
            case '\r':
                return 'r';
            case '\"':
            case '/':
            case '\\':
                return c;
            default:
                return (char) 0;
        }
    }
}
