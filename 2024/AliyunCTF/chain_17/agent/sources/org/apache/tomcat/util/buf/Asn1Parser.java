package org.apache.tomcat.util.buf;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/Asn1Parser.class */
public class Asn1Parser {
    private static final StringManager sm = StringManager.getManager((Class<?>) Asn1Parser.class);
    public static final int TAG_INTEGER = 2;
    public static final int TAG_OCTET_STRING = 4;
    public static final int TAG_NULL = 5;
    public static final int TAG_OID = 6;
    public static final int TAG_SEQUENCE = 48;
    public static final int TAG_ATTRIBUTE_BASE = 160;
    private final byte[] source;
    private int pos = 0;
    private Deque<Integer> nestedSequenceEndPositions = new ArrayDeque();

    public Asn1Parser(byte[] source) {
        this.source = source;
    }

    public boolean eof() {
        return this.pos == this.source.length;
    }

    public int peekTag() {
        return this.source[this.pos] & 255;
    }

    public void parseTagSequence() {
        while (this.nestedSequenceEndPositions.size() > 0 && this.nestedSequenceEndPositions.peekLast().intValue() <= this.pos) {
            this.nestedSequenceEndPositions.pollLast();
        }
        parseTag(48);
        this.nestedSequenceEndPositions.addLast(-1);
    }

    public void parseTag(int tag) {
        int value = next();
        if (value != tag) {
            throw new IllegalArgumentException(sm.getString("asn1Parser.tagMismatch", Integer.valueOf(tag), Integer.valueOf(value)));
        }
    }

    public void parseFullLength() {
        int len = parseLength();
        if (len + this.pos != this.source.length) {
            throw new IllegalArgumentException(sm.getString("asn1Parser.lengthInvalid", Integer.valueOf(len), Integer.valueOf(this.source.length - this.pos)));
        }
    }

    public int parseLength() {
        int len = next();
        if (len > 127) {
            int bytes = len - 128;
            len = 0;
            for (int i = 0; i < bytes; i++) {
                len = (len << 8) + next();
            }
        }
        if (this.nestedSequenceEndPositions.peekLast() != null && this.nestedSequenceEndPositions.peekLast().intValue() == -1) {
            this.nestedSequenceEndPositions.pollLast();
            this.nestedSequenceEndPositions.addLast(Integer.valueOf(this.pos + len));
        }
        return len;
    }

    public BigInteger parseInt() {
        byte[] val = parseBytes(2);
        return new BigInteger(val);
    }

    public byte[] parseOctetString() {
        return parseBytes(4);
    }

    public void parseNull() {
        parseBytes(5);
    }

    public byte[] parseOIDAsBytes() {
        return parseBytes(6);
    }

    public byte[] parseAttributeAsBytes(int index) {
        return parseBytes(160 + index);
    }

    private byte[] parseBytes(int tag) {
        parseTag(tag);
        int len = parseLength();
        byte[] result = new byte[len];
        System.arraycopy(this.source, this.pos, result, 0, result.length);
        this.pos += result.length;
        return result;
    }

    public void parseBytes(byte[] dest) {
        System.arraycopy(this.source, this.pos, dest, 0, dest.length);
        this.pos += dest.length;
    }

    private int next() {
        byte[] bArr = this.source;
        int i = this.pos;
        this.pos = i + 1;
        return bArr[i] & 255;
    }

    public int getNestedSequenceLevel() {
        return this.nestedSequenceEndPositions.size();
    }
}
