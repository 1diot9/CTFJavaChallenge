package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/StructuredField.class */
public class StructuredField {
    private static final int ARRAY_SIZE = 128;
    private static final StringManager sm = StringManager.getManager((Class<?>) StructuredField.class);
    private static final boolean[] IS_KEY_FIRST = new boolean[128];
    private static final boolean[] IS_KEY = new boolean[128];
    private static final boolean[] IS_OWS = new boolean[128];
    private static final boolean[] IS_BASE64 = new boolean[128];
    private static final boolean[] IS_TOKEN = new boolean[128];

    static {
        for (int i = 0; i < 128; i++) {
            if (i == 42 || (i >= 97 && i <= 122)) {
                IS_KEY_FIRST[i] = true;
                IS_KEY[i] = true;
            } else if ((i >= 48 && i <= 57) || i == 95 || i == 45 || i == 46) {
                IS_KEY[i] = true;
            }
        }
        for (int i2 = 0; i2 < 128; i2++) {
            if (i2 == 9 || i2 == 32) {
                IS_OWS[i2] = true;
            }
        }
        for (int i3 = 0; i3 < 128; i3++) {
            if (i3 == 43 || i3 == 47 || ((i3 >= 48 && i3 <= 57) || i3 == 61 || ((i3 >= 65 && i3 <= 90) || (i3 >= 97 && i3 <= 122)))) {
                IS_BASE64[i3] = true;
            }
        }
        for (int i4 = 0; i4 < 128; i4++) {
            if (HttpParser.isToken(i4) || i4 == 58 || i4 == 47) {
                IS_TOKEN[i4] = true;
            }
        }
    }

    static SfList parseSfList(Reader input) throws IOException {
        skipSP(input);
        SfList result = new SfList();
        if (peek(input) != -1) {
            while (true) {
                SfListMember listMember = parseSfListMember(input);
                result.addListMember(listMember);
                skipOWS(input);
                if (peek(input) == -1) {
                    break;
                }
                requireChar(input, 44);
                skipOWS(input);
                requireNotChar(input, -1);
            }
        }
        skipSP(input);
        requireChar(input, -1);
        return result;
    }

    static SfListMember parseSfListMember(Reader input) throws IOException {
        SfListMember listMember;
        if (peek(input) == 40) {
            listMember = parseSfInnerList(input);
        } else {
            listMember = parseSfBareItem(input);
        }
        parseSfParameters(input, listMember);
        return listMember;
    }

    static SfInnerList parseSfInnerList(Reader input) throws IOException {
        requireChar(input, 40);
        SfInnerList innerList = new SfInnerList();
        while (true) {
            skipSP(input);
            if (peek(input) != 41) {
                SfItem<?> item = parseSfBareItem(input);
                parseSfParameters(input, item);
                innerList.addListItem(item);
                input.mark(1);
                requireChar(input, 32, 41);
                input.reset();
            } else {
                requireChar(input, 41);
                return innerList;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SfDictionary parseSfDictionary(Reader input) throws IOException {
        SfListMember listMember;
        skipSP(input);
        SfDictionary result = new SfDictionary();
        if (peek(input) != -1) {
            while (true) {
                String key = parseSfKey(input);
                input.mark(1);
                int c = input.read();
                if (c == 61) {
                    listMember = parseSfListMember(input);
                } else {
                    listMember = new SfBoolean(true);
                    input.reset();
                }
                parseSfParameters(input, listMember);
                result.addDictionaryMember(key, listMember);
                skipOWS(input);
                if (peek(input) == -1) {
                    break;
                }
                requireChar(input, 44);
                skipOWS(input);
                requireNotChar(input, -1);
            }
        }
        skipSP(input);
        requireChar(input, -1);
        return result;
    }

    static SfItem<?> parseSfItem(Reader input) throws IOException {
        skipSP(input);
        SfItem<?> item = parseSfBareItem(input);
        parseSfParameters(input, item);
        skipSP(input);
        requireChar(input, -1);
        return item;
    }

    static SfItem<?> parseSfBareItem(Reader input) throws IOException {
        SfItem<?> item;
        int c = input.read();
        if (c == 45 || HttpParser.isNumeric(c)) {
            item = parseSfNumeric(input, c);
        } else if (c == 34) {
            item = parseSfString(input);
        } else if (c == 42 || HttpParser.isAlpha(c)) {
            item = parseSfToken(input, c);
        } else if (c == 58) {
            item = parseSfByteSequence(input);
        } else if (c == 63) {
            item = parseSfBoolean(input);
        } else {
            throw new IllegalArgumentException(sm.getString("sf.bareitem.invalidCharacter", String.format("\\u%40X", Integer.valueOf(c))));
        }
        return item;
    }

    static void parseSfParameters(Reader input, SfListMember listMember) throws IOException {
        SfItem<?> item;
        while (peek(input) == 59) {
            requireChar(input, 59);
            skipSP(input);
            String key = parseSfKey(input);
            input.mark(1);
            int c = input.read();
            if (c == 61) {
                item = parseSfBareItem(input);
            } else {
                item = new SfBoolean(true);
                input.reset();
            }
            listMember.addParameter(key, item);
        }
    }

    static String parseSfKey(Reader input) throws IOException {
        StringBuilder result = new StringBuilder();
        input.mark(1);
        int c = input.read();
        if (!isKeyFirst(c)) {
            throw new IllegalArgumentException(sm.getString("sf.key.invalidFirstCharacter", String.format("\\u%40X", Integer.valueOf(c))));
        }
        while (c != -1 && isKey(c)) {
            result.append((char) c);
            input.mark(1);
            c = input.read();
        }
        input.reset();
        return result.toString();
    }

    /* JADX WARN: Code restructure failed: missing block: B:43:0x00bc, code lost:            r13.reset();     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x010e, code lost:            if (r16 == false) goto L43;     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x0123, code lost:            return new org.apache.tomcat.util.http.parser.StructuredField.SfInteger(java.lang.Long.parseLong(r0.toString()) * r15);     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x0132, code lost:            if (r0.charAt(r0.length() - 1) != '.') goto L47;     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x0144, code lost:            throw new java.lang.IllegalArgumentException(org.apache.tomcat.util.http.parser.StructuredField.sm.getString("sf.numeric.decimalInvalidFinal"));     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x014e, code lost:            if ((r0.length() - r17) <= 3) goto L51;     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x0160, code lost:            throw new java.lang.IllegalArgumentException(org.apache.tomcat.util.http.parser.StructuredField.sm.getString("sf.numeric.decimalPartTooLong"));     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x0173, code lost:            return new org.apache.tomcat.util.http.parser.StructuredField.SfDecimal(java.lang.Double.parseDouble(r0.toString()) * r15);     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    static org.apache.tomcat.util.http.parser.StructuredField.SfItem<?> parseSfNumeric(java.io.Reader r13, int r14) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 372
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.http.parser.StructuredField.parseSfNumeric(java.io.Reader, int):org.apache.tomcat.util.http.parser.StructuredField$SfItem");
    }

    static SfString parseSfString(Reader input) throws IOException {
        int c;
        StringBuilder result = new StringBuilder();
        while (true) {
            c = input.read();
            if (c == 92) {
                requireNotChar(input, -1);
                c = input.read();
                if (c != 92 && c != 34) {
                    throw new IllegalArgumentException(sm.getString("sf.string.invalidEscape", String.format("\\u%40X", Integer.valueOf(c))));
                }
            } else if (c != 34) {
                if (c < 32 || c > 126) {
                    break;
                }
            } else {
                return new SfString(result.toString());
            }
            result.append((char) c);
        }
        throw new IllegalArgumentException(sm.getString("sf.string.invalidCharacter", String.format("\\u%40X", Integer.valueOf(c))));
    }

    static SfToken parseSfToken(Reader input, int first) throws IOException {
        StringBuilder result = new StringBuilder();
        result.append((char) first);
        while (true) {
            input.mark(1);
            int c = input.read();
            if (!isToken(c)) {
                input.reset();
                return new SfToken(result.toString());
            }
            result.append((char) c);
        }
    }

    static SfByteSequence parseSfByteSequence(Reader input) throws IOException {
        StringBuilder base64 = new StringBuilder();
        while (true) {
            int c = input.read();
            if (c != 58) {
                if (isBase64(c)) {
                    base64.append((char) c);
                } else {
                    throw new IllegalArgumentException(sm.getString("sf.base64.invalidCharacter", String.format("\\u%40X", Integer.valueOf(c))));
                }
            } else {
                return new SfByteSequence(Base64.decodeBase64(base64.toString()));
            }
        }
    }

    static SfBoolean parseSfBoolean(Reader input) throws IOException {
        int c = input.read();
        if (c == 49) {
            return new SfBoolean(true);
        }
        if (c == 48) {
            return new SfBoolean(false);
        }
        throw new IllegalArgumentException(sm.getString("sf.boolean.invalidCharacter", String.format("\\u%40X", Integer.valueOf(c))));
    }

    /* JADX WARN: Incorrect condition in loop: B:3:0x000d */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    static void skipSP(java.io.Reader r3) throws java.io.IOException {
        /*
            r0 = r3
            r1 = 1
            r0.mark(r1)
            r0 = r3
            int r0 = r0.read()
            r4 = r0
        La:
            r0 = r4
            r1 = 32
            if (r0 != r1) goto L1d
            r0 = r3
            r1 = 1
            r0.mark(r1)
            r0 = r3
            int r0 = r0.read()
            r4 = r0
            goto La
        L1d:
            r0 = r3
            r0.reset()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.http.parser.StructuredField.skipSP(java.io.Reader):void");
    }

    /* JADX WARN: Incorrect condition in loop: B:3:0x000e */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    static void skipOWS(java.io.Reader r3) throws java.io.IOException {
        /*
            r0 = r3
            r1 = 1
            r0.mark(r1)
            r0 = r3
            int r0 = r0.read()
            r4 = r0
        La:
            r0 = r4
            boolean r0 = isOws(r0)
            if (r0 == 0) goto L1e
            r0 = r3
            r1 = 1
            r0.mark(r1)
            r0 = r3
            int r0 = r0.read()
            r4 = r0
            goto La
        L1e:
            r0 = r3
            r0.reset()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.http.parser.StructuredField.skipOWS(java.io.Reader):void");
    }

    static void requireChar(Reader input, int... required) throws IOException {
        int c = input.read();
        for (int r : required) {
            if (c == r) {
                return;
            }
        }
        throw new IllegalArgumentException(sm.getString("sf.invalidCharacter", String.format("\\u%40X", Integer.valueOf(c))));
    }

    static void requireNotChar(Reader input, int required) throws IOException {
        input.mark(1);
        int c = input.read();
        if (c == required) {
            throw new IllegalArgumentException(sm.getString("sf.invalidCharacter", String.format("\\u%40X", Integer.valueOf(c))));
        }
        input.reset();
    }

    static int peek(Reader input) throws IOException {
        input.mark(1);
        int c = input.read();
        input.reset();
        return c;
    }

    static boolean isKeyFirst(int c) {
        try {
            return IS_KEY_FIRST[c];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    static boolean isKey(int c) {
        try {
            return IS_KEY[c];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    static boolean isOws(int c) {
        try {
            return IS_OWS[c];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    static boolean isBase64(int c) {
        try {
            return IS_BASE64[c];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    static boolean isToken(int c) {
        try {
            return IS_TOKEN[c];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    private StructuredField() {
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/StructuredField$SfDictionary.class */
    static class SfDictionary {
        private Map<String, SfListMember> dictionary = new LinkedHashMap();

        SfDictionary() {
        }

        void addDictionaryMember(String key, SfListMember value) {
            this.dictionary.put(key, value);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public SfListMember getDictionaryMember(String key) {
            return this.dictionary.get(key);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/StructuredField$SfList.class */
    static class SfList {
        private List<SfListMember> listMembers = new ArrayList();

        SfList() {
        }

        void addListMember(SfListMember listMember) {
            this.listMembers.add(listMember);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/StructuredField$SfListMember.class */
    public static class SfListMember {
        private Map<String, SfItem<?>> parameters = null;

        SfListMember() {
        }

        void addParameter(String key, SfItem<?> value) {
            if (this.parameters == null) {
                this.parameters = new LinkedHashMap();
            }
            this.parameters.put(key, value);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/StructuredField$SfInnerList.class */
    public static class SfInnerList extends SfListMember {
        List<SfItem<?>> listItems = new ArrayList();

        SfInnerList() {
        }

        void addListItem(SfItem<?> item) {
            this.listItems.add(item);
        }

        List<SfItem<?>> getListItem() {
            return this.listItems;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/StructuredField$SfItem.class */
    public static abstract class SfItem<T> extends SfListMember {
        private final T value;

        SfItem(T value) {
            this.value = value;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public T getVaue() {
            return this.value;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/StructuredField$SfInteger.class */
    public static class SfInteger extends SfItem<Long> {
        SfInteger(long value) {
            super(Long.valueOf(value));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/StructuredField$SfDecimal.class */
    public static class SfDecimal extends SfItem<Double> {
        SfDecimal(double value) {
            super(Double.valueOf(value));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/StructuredField$SfString.class */
    public static class SfString extends SfItem<String> {
        SfString(String value) {
            super(value);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/StructuredField$SfToken.class */
    public static class SfToken extends SfItem<String> {
        SfToken(String value) {
            super(value);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/StructuredField$SfByteSequence.class */
    public static class SfByteSequence extends SfItem<byte[]> {
        SfByteSequence(byte[] value) {
            super(value);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/StructuredField$SfBoolean.class */
    public static class SfBoolean extends SfItem<Boolean> {
        SfBoolean(boolean value) {
            super(Boolean.valueOf(value));
        }
    }
}
