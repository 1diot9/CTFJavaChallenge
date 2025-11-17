package ch.qos.logback.core.encoder;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/encoder/JsonEscapeUtil.class */
public class JsonEscapeUtil {
    static final int ESCAPE_CODES_COUNT = 32;
    protected static final char[] HEXADECIMALS_TABLE = "0123456789ABCDEF".toCharArray();
    static final String[] ESCAPE_CODES = new String[32];

    static {
        char c = 0;
        while (true) {
            char c2 = c;
            if (c2 < ' ') {
                switch (c2) {
                    case '\b':
                        ESCAPE_CODES[c2] = "\\b";
                        break;
                    case '\t':
                        ESCAPE_CODES[c2] = "\\t";
                        break;
                    case '\n':
                        ESCAPE_CODES[c2] = "\\n";
                        break;
                    case 11:
                    default:
                        ESCAPE_CODES[c2] = _computeEscapeCodeBelowASCII32(c2);
                        break;
                    case '\f':
                        ESCAPE_CODES[c2] = "\\f";
                        break;
                    case '\r':
                        ESCAPE_CODES[c2] = "\\r";
                        break;
                }
                c = (char) (c2 + 1);
            } else {
                return;
            }
        }
    }

    private static String _computeEscapeCodeBelowASCII32(char c) {
        if (c > ' ') {
            throw new IllegalArgumentException("input must be less than 32");
        }
        StringBuilder sb = new StringBuilder(6);
        sb.append("\\u00");
        int highPart = c >> 4;
        sb.append(HEXADECIMALS_TABLE[highPart]);
        int lowPart = c & 15;
        sb.append(HEXADECIMALS_TABLE[lowPart]);
        return sb.toString();
    }

    static String getObligatoryEscapeCode(char c) {
        if (c < ' ') {
            return ESCAPE_CODES[c];
        }
        if (c == '\"') {
            return "\\\"";
        }
        if (c == '\\') {
            return "\\/";
        }
        return null;
    }

    public static String jsonEscapeString(String input) {
        int length = input.length();
        int lenthWithLeeway = (int) (length * 1.1d);
        StringBuilder sb = new StringBuilder(lenthWithLeeway);
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            String escaped = getObligatoryEscapeCode(c);
            if (escaped == null) {
                sb.append(c);
            } else {
                sb.append(escaped);
            }
        }
        return sb.toString();
    }
}
