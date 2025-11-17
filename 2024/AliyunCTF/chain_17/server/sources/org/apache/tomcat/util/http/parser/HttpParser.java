package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.Reader;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/HttpParser.class */
public class HttpParser {
    private static final int ARRAY_SIZE = 128;
    private static final HttpParser DEFAULT;
    private final boolean[] IS_NOT_REQUEST_TARGET = new boolean[128];
    private final boolean[] IS_ABSOLUTEPATH_RELAXED = new boolean[128];
    private final boolean[] IS_QUERY_RELAXED = new boolean[128];
    private static final StringManager sm = StringManager.getManager((Class<?>) HttpParser.class);
    private static final boolean[] IS_CONTROL = new boolean[128];
    private static final boolean[] IS_SEPARATOR = new boolean[128];
    private static final boolean[] IS_TOKEN = new boolean[128];
    private static final boolean[] IS_HEX = new boolean[128];
    private static final boolean[] IS_HTTP_PROTOCOL = new boolean[128];
    private static final boolean[] IS_ALPHA = new boolean[128];
    private static final boolean[] IS_NUMERIC = new boolean[128];
    private static final boolean[] IS_SCHEME = new boolean[128];
    private static final boolean[] IS_UNRESERVED = new boolean[128];
    private static final boolean[] IS_SUBDELIM = new boolean[128];
    private static final boolean[] IS_USERINFO = new boolean[128];
    private static final boolean[] IS_RELAXABLE = new boolean[128];

    static {
        for (int i = 0; i < 128; i++) {
            if (i < 32 || i == 127) {
                IS_CONTROL[i] = true;
            }
            if (i == 40 || i == 41 || i == 60 || i == 62 || i == 64 || i == 44 || i == 59 || i == 58 || i == 92 || i == 34 || i == 47 || i == 91 || i == 93 || i == 63 || i == 61 || i == 123 || i == 125 || i == 32 || i == 9) {
                IS_SEPARATOR[i] = true;
            }
            if (!IS_CONTROL[i] && !IS_SEPARATOR[i] && i < 128) {
                IS_TOKEN[i] = true;
            }
            if ((i >= 48 && i <= 57) || ((i >= 97 && i <= 102) || (i >= 65 && i <= 70))) {
                IS_HEX[i] = true;
            }
            if (i == 72 || i == 84 || i == 80 || i == 47 || i == 46 || (i >= 48 && i <= 57)) {
                IS_HTTP_PROTOCOL[i] = true;
            }
            if (i >= 48 && i <= 57) {
                IS_NUMERIC[i] = true;
            }
            if ((i >= 97 && i <= 122) || (i >= 65 && i <= 90)) {
                IS_ALPHA[i] = true;
            }
            if (IS_ALPHA[i] || IS_NUMERIC[i] || i == 43 || i == 45 || i == 46) {
                IS_SCHEME[i] = true;
            }
            if (IS_ALPHA[i] || IS_NUMERIC[i] || i == 45 || i == 46 || i == 95 || i == 126) {
                IS_UNRESERVED[i] = true;
            }
            if (i == 33 || i == 36 || i == 38 || i == 39 || i == 40 || i == 41 || i == 42 || i == 43 || i == 44 || i == 59 || i == 61) {
                IS_SUBDELIM[i] = true;
            }
            if (IS_UNRESERVED[i] || i == 37 || IS_SUBDELIM[i] || i == 58) {
                IS_USERINFO[i] = true;
            }
            if (i == 34 || i == 60 || i == 62 || i == 91 || i == 92 || i == 93 || i == 94 || i == 96 || i == 123 || i == 124 || i == 125) {
                IS_RELAXABLE[i] = true;
            }
        }
        DEFAULT = new HttpParser(null, null);
    }

    public HttpParser(String relaxedPathChars, String relaxedQueryChars) {
        for (int i = 0; i < 128; i++) {
            if (IS_CONTROL[i] || i == 32 || i == 34 || i == 35 || i == 60 || i == 62 || i == 92 || i == 94 || i == 96 || i == 123 || i == 124 || i == 125) {
                this.IS_NOT_REQUEST_TARGET[i] = true;
            }
            if (IS_USERINFO[i] || i == 64 || i == 47) {
                this.IS_ABSOLUTEPATH_RELAXED[i] = true;
            }
            if (this.IS_ABSOLUTEPATH_RELAXED[i] || i == 63) {
                this.IS_QUERY_RELAXED[i] = true;
            }
        }
        relax(this.IS_ABSOLUTEPATH_RELAXED, relaxedPathChars);
        relax(this.IS_QUERY_RELAXED, relaxedQueryChars);
    }

    public boolean isNotRequestTargetRelaxed(int c) {
        try {
            return this.IS_NOT_REQUEST_TARGET[c];
        } catch (ArrayIndexOutOfBoundsException e) {
            return true;
        }
    }

    public boolean isAbsolutePathRelaxed(int c) {
        try {
            return this.IS_ABSOLUTEPATH_RELAXED[c];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    public boolean isQueryRelaxed(int c) {
        try {
            return this.IS_QUERY_RELAXED[c];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    public static String unquote(String input) {
        int start;
        int end;
        if (input == null || input.length() < 2) {
            return input;
        }
        if (input.charAt(0) == '\"') {
            start = 1;
            end = input.length() - 1;
        } else {
            start = 0;
            end = input.length();
        }
        StringBuilder result = new StringBuilder();
        int i = start;
        while (i < end) {
            char c = input.charAt(i);
            if (input.charAt(i) == '\\') {
                i++;
                if (i == end) {
                    return null;
                }
                result.append(input.charAt(i));
            } else {
                result.append(c);
            }
            i++;
        }
        return result.toString();
    }

    public static boolean isToken(int c) {
        try {
            return IS_TOKEN[c];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean isToken(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        for (char c : s.toCharArray()) {
            if (!isToken(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isHex(int c) {
        try {
            return IS_HEX[c];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean isNotRequestTarget(int c) {
        return DEFAULT.isNotRequestTargetRelaxed(c);
    }

    public static boolean isHttpProtocol(int c) {
        try {
            return IS_HTTP_PROTOCOL[c];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean isAlpha(int c) {
        try {
            return IS_ALPHA[c];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean isNumeric(int c) {
        try {
            return IS_NUMERIC[c];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean isScheme(int c) {
        try {
            return IS_SCHEME[c];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean isScheme(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        char[] chars = s.toCharArray();
        if (!isAlpha(chars[0])) {
            return false;
        }
        if (chars.length > 1) {
            for (int i = 1; i < chars.length; i++) {
                if (!isScheme(chars[i])) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    public static boolean isUserInfo(int c) {
        try {
            return IS_USERINFO[c];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    private static boolean isRelaxable(int c) {
        try {
            return IS_RELAXABLE[c];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean isAbsolutePath(int c) {
        return DEFAULT.isAbsolutePathRelaxed(c);
    }

    public static boolean isQuery(int c) {
        return DEFAULT.isQueryRelaxed(c);
    }

    public static boolean isControl(int c) {
        try {
            return IS_CONTROL[c];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int skipLws(Reader input) throws IOException {
        input.mark(1);
        int read = input.read();
        while (true) {
            int c = read;
            if (c == 32 || c == 9 || c == 10 || c == 13) {
                input.mark(1);
                read = input.read();
            } else {
                input.reset();
                return c;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SkipResult skipConstant(Reader input, String constant) throws IOException {
        int len = constant.length();
        skipLws(input);
        input.mark(len);
        int c = input.read();
        for (int i = 0; i < len; i++) {
            if (i == 0 && c == -1) {
                return SkipResult.EOF;
            }
            if (c != constant.charAt(i)) {
                input.reset();
                return SkipResult.NOT_FOUND;
            }
            if (i != len - 1) {
                c = input.read();
            }
        }
        return SkipResult.FOUND;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String readToken(Reader input) throws IOException {
        int c;
        StringBuilder result = new StringBuilder();
        skipLws(input);
        input.mark(1);
        int read = input.read();
        while (true) {
            c = read;
            if (c == -1 || !isToken(c)) {
                break;
            }
            result.append((char) c);
            input.mark(1);
            read = input.read();
        }
        input.reset();
        if (c != -1 && result.length() == 0) {
            return null;
        }
        return result.toString();
    }

    static String readDigits(Reader input) throws IOException {
        StringBuilder result = new StringBuilder();
        skipLws(input);
        input.mark(1);
        int read = input.read();
        while (true) {
            int c = read;
            if (c == -1 || !isNumeric(c)) {
                break;
            }
            result.append((char) c);
            input.mark(1);
            read = input.read();
        }
        input.reset();
        return result.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long readLong(Reader input) throws IOException {
        String digits = readDigits(input);
        if (digits.length() == 0) {
            return -1L;
        }
        return Long.parseLong(digits);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String readQuotedString(Reader input, boolean returnQuoted) throws IOException {
        skipLws(input);
        if (input.read() != 34) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        if (returnQuoted) {
            result.append('\"');
        }
        int read = input.read();
        while (true) {
            int c = read;
            if (c != 34) {
                if (c == -1) {
                    return null;
                }
                if (c == 92) {
                    int c2 = input.read();
                    if (returnQuoted) {
                        result.append('\\');
                    }
                    result.append((char) c2);
                } else {
                    result.append((char) c);
                }
                read = input.read();
            } else {
                if (returnQuoted) {
                    result.append('\"');
                }
                return result.toString();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String readTokenOrQuotedString(Reader input, boolean returnQuoted) throws IOException {
        int c = skipLws(input);
        if (c == 34) {
            return readQuotedString(input, returnQuoted);
        }
        return readToken(input);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String readQuotedToken(Reader input) throws IOException {
        int c;
        StringBuilder result = new StringBuilder();
        boolean quoted = false;
        skipLws(input);
        input.mark(1);
        int c2 = input.read();
        if (c2 == 34) {
            quoted = true;
        } else {
            if (c2 == -1 || !isToken(c2)) {
                return null;
            }
            result.append((char) c2);
        }
        input.mark(1);
        int read = input.read();
        while (true) {
            c = read;
            if (c == -1 || !isToken(c)) {
                break;
            }
            result.append((char) c);
            input.mark(1);
            read = input.read();
        }
        if (quoted) {
            if (c != 34) {
                return null;
            }
        } else {
            input.reset();
        }
        if (c != -1 && result.length() == 0) {
            return null;
        }
        return result.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String readLhex(Reader input) throws IOException {
        int c;
        StringBuilder result = new StringBuilder();
        boolean quoted = false;
        skipLws(input);
        input.mark(1);
        int c2 = input.read();
        if (c2 == 34) {
            quoted = true;
        } else {
            if (c2 == -1 || !isHex(c2)) {
                return null;
            }
            if (65 <= c2 && c2 <= 70) {
                c2 += 32;
            }
            result.append((char) c2);
        }
        input.mark(1);
        int read = input.read();
        while (true) {
            c = read;
            if (c == -1 || !isHex(c)) {
                break;
            }
            if (65 <= c && c <= 70) {
                c += 32;
            }
            result.append((char) c);
            input.mark(1);
            read = input.read();
        }
        if (quoted) {
            if (c != 34) {
                return null;
            }
        } else {
            input.reset();
        }
        if (c != -1 && result.length() == 0) {
            return null;
        }
        return result.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static double readWeight(Reader input, char delimiter) throws IOException {
        int c;
        skipLws(input);
        int c2 = input.read();
        if (c2 == -1 || c2 == delimiter) {
            return 1.0d;
        }
        if (c2 != 113) {
            skipUntil(input, c2, delimiter);
            return 0.0d;
        }
        skipLws(input);
        int c3 = input.read();
        if (c3 != 61) {
            skipUntil(input, c3, delimiter);
            return 0.0d;
        }
        skipLws(input);
        int c4 = input.read();
        StringBuilder value = new StringBuilder(5);
        int decimalPlacesRead = -1;
        if (c4 == 48 || c4 == 49) {
            value.append((char) c4);
            int read = input.read();
            while (true) {
                c = read;
                if (decimalPlacesRead == -1 && c == 46) {
                    value.append('.');
                    decimalPlacesRead = 0;
                } else {
                    if (decimalPlacesRead <= -1 || c < 48 || c > 57) {
                        break;
                    }
                    if (decimalPlacesRead < 3) {
                        value.append((char) c);
                        decimalPlacesRead++;
                    }
                }
                read = input.read();
            }
            if (c == 9 || c == 32) {
                skipLws(input);
                c = input.read();
            }
            if (c != delimiter && c != -1) {
                skipUntil(input, c, delimiter);
                return 0.0d;
            }
            double result = Double.parseDouble(value.toString());
            if (result > 1.0d) {
                return 0.0d;
            }
            return result;
        }
        skipUntil(input, c4, delimiter);
        return 0.0d;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x002e, code lost:            if (r10 != false) goto L14;     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x0033, code lost:            if (r11 != (-1)) goto L16;     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0051, code lost:            r9.reset();     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0059, code lost:            return readHostDomainName(r9);     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0050, code lost:            throw new java.lang.IllegalArgumentException(org.apache.tomcat.util.http.parser.HttpParser.sm.getString("http.invalidOctet", java.lang.Integer.toString(r11)));     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static int readHostIPv4(java.io.Reader r9, boolean r10) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 343
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.http.parser.HttpParser.readHostIPv4(java.io.Reader, boolean):int");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int readHostIPv6(Reader reader) throws IOException {
        int pos;
        if (reader.read() != 91) {
            throw new IllegalArgumentException(sm.getString("http.noOpeningBracket"));
        }
        int h16Count = 0;
        int h16Size = 0;
        int pos2 = 1;
        boolean parsedDoubleColon = false;
        int precedingColonsCount = 0;
        while (true) {
            int c = reader.read();
            if (h16Count == 0 && precedingColonsCount == 1 && c != 58) {
                throw new IllegalArgumentException(sm.getString("http.singleColonStart"));
            }
            if (isHex(c)) {
                if (h16Size == 0) {
                    precedingColonsCount = 0;
                    h16Count++;
                }
                h16Size++;
                if (h16Size > 4) {
                    throw new IllegalArgumentException(sm.getString("http.invalidHextet"));
                }
            } else if (c == 58) {
                if (precedingColonsCount >= 2) {
                    throw new IllegalArgumentException(sm.getString("http.tooManyColons"));
                }
                if (precedingColonsCount == 1) {
                    if (parsedDoubleColon) {
                        throw new IllegalArgumentException(sm.getString("http.tooManyDoubleColons"));
                    }
                    parsedDoubleColon = true;
                    h16Count++;
                }
                precedingColonsCount++;
                reader.mark(4);
                h16Size = 0;
            } else {
                if (c == 93) {
                    if (precedingColonsCount == 1) {
                        throw new IllegalArgumentException(sm.getString("http.singleColonEnd"));
                    }
                    pos = pos2 + 1;
                } else if (c == 46) {
                    if (h16Count == 7 || (h16Count < 7 && parsedDoubleColon)) {
                        reader.reset();
                        pos = (pos2 - h16Size) + readHostIPv4(reader, true);
                        h16Count++;
                    } else {
                        throw new IllegalArgumentException(sm.getString("http.invalidIpv4Location"));
                    }
                } else {
                    throw new IllegalArgumentException(sm.getString("http.illegalCharacterIpv6", Character.toString((char) c)));
                }
                if (h16Count > 8) {
                    throw new IllegalArgumentException(sm.getString("http.tooManyHextets", Integer.toString(h16Count)));
                }
                if (h16Count != 8 && !parsedDoubleColon) {
                    throw new IllegalArgumentException(sm.getString("http.tooFewHextets", Integer.toString(h16Count)));
                }
                int c2 = reader.read();
                if (c2 == 58) {
                    return validatePort(reader, pos);
                }
                if (c2 == -1) {
                    return -1;
                }
                throw new IllegalArgumentException(sm.getString("http.illegalAfterIpv6", Character.toString((char) c2)));
            }
            pos2++;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int readHostDomainName(Reader reader) throws IOException {
        DomainParseState state = DomainParseState.NEW;
        int pos = 0;
        while (state.mayContinue()) {
            state = state.next(reader.read());
            pos++;
        }
        if (DomainParseState.COLON == state) {
            return validatePort(reader, pos - 1);
        }
        return -1;
    }

    static int validatePort(Reader reader, int colonPosition) throws IOException {
        readLong(reader);
        if (reader.read() == -1) {
            return colonPosition;
        }
        throw new IllegalArgumentException();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SkipResult skipUntil(Reader input, int c, char target) throws IOException {
        while (c != -1 && c != target) {
            c = input.read();
        }
        if (c == -1) {
            return SkipResult.EOF;
        }
        return SkipResult.FOUND;
    }

    private void relax(boolean[] flags, String relaxedChars) {
        if (relaxedChars != null && relaxedChars.length() > 0) {
            char[] chars = relaxedChars.toCharArray();
            for (char c : chars) {
                if (isRelaxable(c)) {
                    flags[c] = true;
                    this.IS_NOT_REQUEST_TARGET[c] = false;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/HttpParser$DomainParseState.class */
    public enum DomainParseState {
        NEW(true, false, false, false, "http.invalidCharacterDomain.atStart"),
        ALPHA(true, true, true, true, "http.invalidCharacterDomain.afterLetter"),
        NUMERIC(true, true, true, true, "http.invalidCharacterDomain.afterNumber"),
        PERIOD(true, false, false, true, "http.invalidCharacterDomain.afterPeriod"),
        HYPHEN(true, true, false, false, "http.invalidCharacterDomain.afterHyphen"),
        COLON(false, false, false, false, "http.invalidCharacterDomain.afterColon"),
        END(false, false, false, false, "http.invalidCharacterDomain.atEnd");

        private final boolean mayContinue;
        private final boolean allowsHyphen;
        private final boolean allowsPeriod;
        private final boolean allowsEnd;
        private final String errorMsg;

        DomainParseState(boolean mayContinue, boolean allowsHyphen, boolean allowsPeriod, boolean allowsEnd, String errorMsg) {
            this.mayContinue = mayContinue;
            this.allowsHyphen = allowsHyphen;
            this.allowsPeriod = allowsPeriod;
            this.allowsEnd = allowsEnd;
            this.errorMsg = errorMsg;
        }

        public boolean mayContinue() {
            return this.mayContinue;
        }

        public DomainParseState next(int c) {
            if (c == -1) {
                if (this.allowsEnd) {
                    return END;
                }
                throw new IllegalArgumentException(HttpParser.sm.getString("http.invalidSegmentEndState", name()));
            }
            if (HttpParser.isAlpha(c)) {
                return ALPHA;
            }
            if (HttpParser.isNumeric(c)) {
                return NUMERIC;
            }
            if (c == 46) {
                if (this.allowsPeriod) {
                    return PERIOD;
                }
                throw new IllegalArgumentException(HttpParser.sm.getString(this.errorMsg, Character.toString((char) c)));
            }
            if (c == 58) {
                if (this.allowsEnd) {
                    return COLON;
                }
                throw new IllegalArgumentException(HttpParser.sm.getString(this.errorMsg, Character.toString((char) c)));
            }
            if (c == 45) {
                if (this.allowsHyphen) {
                    return HYPHEN;
                }
                throw new IllegalArgumentException(HttpParser.sm.getString(this.errorMsg, Character.toString((char) c)));
            }
            throw new IllegalArgumentException(HttpParser.sm.getString("http.illegalCharacterDomain", Character.toString((char) c)));
        }
    }
}
