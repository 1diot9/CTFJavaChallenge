package org.apache.tomcat.util.buf;

import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/CharChunk.class */
public final class CharChunk extends AbstractChunk implements CharSequence {
    private static final long serialVersionUID = 1;
    private char[] buff;
    private transient CharInputChannel in = null;
    private transient CharOutputChannel out = null;

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/CharChunk$CharInputChannel.class */
    public interface CharInputChannel {
        int realReadChars() throws IOException;
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/CharChunk$CharOutputChannel.class */
    public interface CharOutputChannel {
        void realWriteChars(char[] cArr, int i, int i2) throws IOException;
    }

    public CharChunk() {
    }

    public CharChunk(int initial) {
        allocate(initial, -1);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void allocate(int initial, int limit) {
        if (this.buff == null || this.buff.length < initial) {
            this.buff = new char[initial];
        }
        setLimit(limit);
        this.start = 0;
        this.end = 0;
        this.isSet = true;
        this.hasHashCode = false;
    }

    public void setChars(char[] c, int off, int len) {
        this.buff = c;
        this.start = off;
        this.end = this.start + len;
        this.isSet = true;
        this.hasHashCode = false;
    }

    public char[] getChars() {
        return getBuffer();
    }

    public char[] getBuffer() {
        return this.buff;
    }

    public void setCharInputChannel(CharInputChannel in) {
        this.in = in;
    }

    public void setCharOutputChannel(CharOutputChannel out) {
        this.out = out;
    }

    public void append(char c) throws IOException {
        makeSpace(1);
        int limit = getLimitInternal();
        if (this.end >= limit) {
            flushBuffer();
        }
        char[] cArr = this.buff;
        int i = this.end;
        this.end = i + 1;
        cArr[i] = c;
    }

    public void append(CharChunk src) throws IOException {
        append(src.getBuffer(), src.getOffset(), src.getLength());
    }

    public void append(char[] src, int off, int len) throws IOException {
        makeSpace(len);
        int limit = getLimitInternal();
        if (len == limit && this.end == this.start && this.out != null) {
            this.out.realWriteChars(src, off, len);
            return;
        }
        if (len <= limit - this.end) {
            System.arraycopy(src, off, this.buff, this.end, len);
            this.end += len;
        } else {
            if (len + this.end < 2 * limit) {
                int avail = limit - this.end;
                System.arraycopy(src, off, this.buff, this.end, avail);
                this.end += avail;
                flushBuffer();
                System.arraycopy(src, off + avail, this.buff, this.end, len - avail);
                this.end += len - avail;
                return;
            }
            flushBuffer();
            this.out.realWriteChars(src, off, len);
        }
    }

    public void append(String s) throws IOException {
        append(s, 0, s.length());
    }

    public void append(String s, int off, int len) throws IOException {
        if (s == null) {
            return;
        }
        makeSpace(len);
        int limit = getLimitInternal();
        int sOff = off;
        int sEnd = off + len;
        while (sOff < sEnd) {
            int d = min(limit - this.end, sEnd - sOff);
            s.getChars(sOff, sOff + d, this.buff, this.end);
            sOff += d;
            this.end += d;
            if (this.end >= limit) {
                flushBuffer();
            }
        }
    }

    public int subtract() throws IOException {
        if (checkEof()) {
            return -1;
        }
        char[] cArr = this.buff;
        int i = this.start;
        this.start = i + 1;
        return cArr[i];
    }

    public int subtract(char[] dest, int off, int len) throws IOException {
        if (checkEof()) {
            return -1;
        }
        int n = len;
        if (len > getLength()) {
            n = getLength();
        }
        System.arraycopy(this.buff, this.start, dest, off, n);
        this.start += n;
        return n;
    }

    private boolean checkEof() throws IOException {
        if (this.end - this.start == 0) {
            if (this.in == null) {
                return true;
            }
            int n = this.in.realReadChars();
            if (n < 0) {
                return true;
            }
            return false;
        }
        return false;
    }

    public void flushBuffer() throws IOException {
        if (this.out == null) {
            throw new IOException(sm.getString("chunk.overflow", Integer.valueOf(getLimit()), Integer.valueOf(this.buff.length)));
        }
        this.out.realWriteChars(this.buff, this.start, this.end - this.start);
        this.end = this.start;
    }

    public void makeSpace(int count) {
        long newSize;
        int limit = getLimitInternal();
        long desiredSize = this.end + count;
        if (desiredSize > limit) {
            desiredSize = limit;
        }
        if (this.buff == null) {
            if (desiredSize < 256) {
                desiredSize = 256;
            }
            this.buff = new char[(int) desiredSize];
        }
        if (desiredSize <= this.buff.length) {
            return;
        }
        if (desiredSize < 2 * this.buff.length) {
            newSize = this.buff.length * 2;
        } else {
            newSize = (this.buff.length * 2) + count;
        }
        if (newSize > limit) {
            newSize = limit;
        }
        char[] tmp = new char[(int) newSize];
        System.arraycopy(this.buff, 0, tmp, 0, this.end);
        this.buff = tmp;
    }

    @Override // java.lang.CharSequence
    public String toString() {
        if (isNull()) {
            return null;
        }
        if (this.end - this.start == 0) {
            return "";
        }
        return StringCache.toString(this);
    }

    public String toStringInternal() {
        return new String(this.buff, this.start, this.end - this.start);
    }

    public boolean equals(Object obj) {
        if (obj instanceof CharChunk) {
            return equals((CharChunk) obj);
        }
        return false;
    }

    public boolean equals(String s) {
        char[] c = this.buff;
        int len = this.end - this.start;
        if (c == null || len != s.length()) {
            return false;
        }
        int off = this.start;
        for (int i = 0; i < len; i++) {
            int i2 = off;
            off++;
            if (c[i2] != s.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public boolean equalsIgnoreCase(String s) {
        char[] c = this.buff;
        int len = this.end - this.start;
        if (c == null || len != s.length()) {
            return false;
        }
        int off = this.start;
        for (int i = 0; i < len; i++) {
            int i2 = off;
            off++;
            if (Ascii.toLower(c[i2]) != Ascii.toLower(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(CharChunk cc) {
        return equals(cc.getChars(), cc.getOffset(), cc.getLength());
    }

    public boolean equals(char[] b2, int off2, int len2) {
        int i;
        int i2;
        char[] b1 = this.buff;
        if (b1 == null && b2 == null) {
            return true;
        }
        int len = this.end - this.start;
        if (len != len2 || b1 == null || b2 == null) {
            return false;
        }
        int off1 = this.start;
        do {
            int i3 = len;
            len--;
            if (i3 <= 0) {
                return true;
            }
            i = off1;
            off1++;
            i2 = off2;
            off2++;
        } while (b1[i] == b2[i2]);
        return false;
    }

    public boolean startsWith(String s) {
        char[] c = this.buff;
        int len = s.length();
        if (c == null || len > this.end - this.start) {
            return false;
        }
        int off = this.start;
        for (int i = 0; i < len; i++) {
            int i2 = off;
            off++;
            if (c[i2] != s.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public boolean startsWithIgnoreCase(String s, int pos) {
        char[] c = this.buff;
        int len = s.length();
        if (c == null || len + pos > this.end - this.start) {
            return false;
        }
        int off = this.start + pos;
        for (int i = 0; i < len; i++) {
            int i2 = off;
            off++;
            if (Ascii.toLower(c[i2]) != Ascii.toLower(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean endsWith(String s) {
        char[] c = this.buff;
        int len = s.length();
        if (c == null || len > this.end - this.start) {
            return false;
        }
        int off = this.end - len;
        for (int i = 0; i < len; i++) {
            int i2 = off;
            off++;
            if (c[i2] != s.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    @Override // org.apache.tomcat.util.buf.AbstractChunk
    protected int getBufferElement(int index) {
        return this.buff[index];
    }

    public int indexOf(char c) {
        return indexOf(c, this.start);
    }

    public int indexOf(char c, int starting) {
        int ret = indexOf(this.buff, this.start + starting, this.end, c);
        if (ret >= this.start) {
            return ret - this.start;
        }
        return -1;
    }

    public static int indexOf(char[] chars, int start, int end, char s) {
        for (int offset = start; offset < end; offset++) {
            char c = chars[offset];
            if (c == s) {
                return offset;
            }
        }
        return -1;
    }

    private int min(int a, int b) {
        if (a < b) {
            return a;
        }
        return b;
    }

    @Override // java.lang.CharSequence
    public char charAt(int index) {
        return this.buff[index + this.start];
    }

    @Override // java.lang.CharSequence
    public CharSequence subSequence(int start, int end) {
        try {
            CharChunk result = (CharChunk) clone();
            result.setOffset(this.start + start);
            result.setEnd(this.start + end);
            return result;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override // java.lang.CharSequence
    public int length() {
        return this.end - this.start;
    }
}
