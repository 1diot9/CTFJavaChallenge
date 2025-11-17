package org.apache.tomcat.util.buf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/UEncoder.class */
public final class UEncoder {
    private BitSet safeChars;
    private C2BConverter c2b = null;
    private ByteChunk bb = null;
    private CharChunk cb = null;
    private CharChunk output = null;

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/UEncoder$SafeCharsSet.class */
    public enum SafeCharsSet {
        WITH_SLASH("/"),
        DEFAULT("");

        private final BitSet safeChars = UEncoder.initialSafeChars();

        private BitSet getSafeChars() {
            return this.safeChars;
        }

        SafeCharsSet(String additionalSafeChars) {
            for (char c : additionalSafeChars.toCharArray()) {
                this.safeChars.set(c);
            }
        }
    }

    public UEncoder(SafeCharsSet safeCharsSet) {
        this.safeChars = null;
        this.safeChars = safeCharsSet.getSafeChars();
    }

    public CharChunk encodeURL(String s, int start, int end) throws IOException {
        char d;
        if (this.c2b == null) {
            this.bb = new ByteChunk(8);
            this.cb = new CharChunk(2);
            this.output = new CharChunk(64);
            this.c2b = new C2BConverter(StandardCharsets.UTF_8);
        } else {
            this.bb.recycle();
            this.cb.recycle();
            this.output.recycle();
        }
        int i = start;
        while (i < end) {
            char c = s.charAt(i);
            if (this.safeChars.get(c)) {
                this.output.append(c);
            } else {
                this.cb.append(c);
                this.c2b.convert(this.cb, this.bb);
                if (c >= 55296 && c <= 56319 && i + 1 < end && (d = s.charAt(i + 1)) >= 56320 && d <= 57343) {
                    this.cb.append(d);
                    this.c2b.convert(this.cb, this.bb);
                    i++;
                }
                urlEncode(this.output, this.bb);
                this.cb.recycle();
                this.bb.recycle();
            }
            i++;
        }
        return this.output;
    }

    protected void urlEncode(CharChunk out, ByteChunk bb) throws IOException {
        byte[] bytes = bb.getBuffer();
        for (int j = bb.getStart(); j < bb.getEnd(); j++) {
            out.append('%');
            char ch2 = Character.forDigit((bytes[j] >> 4) & 15, 16);
            out.append(ch2);
            char ch3 = Character.forDigit(bytes[j] & 15, 16);
            out.append(ch3);
        }
    }

    private static BitSet initialSafeChars() {
        BitSet initialSafeChars = new BitSet(128);
        for (int i = 97; i <= 122; i++) {
            initialSafeChars.set(i);
        }
        for (int i2 = 65; i2 <= 90; i2++) {
            initialSafeChars.set(i2);
        }
        for (int i3 = 48; i3 <= 57; i3++) {
            initialSafeChars.set(i3);
        }
        initialSafeChars.set(36);
        initialSafeChars.set(45);
        initialSafeChars.set(95);
        initialSafeChars.set(46);
        initialSafeChars.set(33);
        initialSafeChars.set(42);
        initialSafeChars.set(39);
        initialSafeChars.set(40);
        initialSafeChars.set(41);
        initialSafeChars.set(44);
        return initialSafeChars;
    }
}
