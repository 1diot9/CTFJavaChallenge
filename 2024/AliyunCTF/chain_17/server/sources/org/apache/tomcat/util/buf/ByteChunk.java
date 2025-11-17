package org.apache.tomcat.util.buf;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/ByteChunk.class */
public final class ByteChunk extends AbstractChunk {
    private static final long serialVersionUID = 1;
    public static final Charset DEFAULT_CHARSET = StandardCharsets.ISO_8859_1;
    private transient Charset charset;
    private byte[] buff;
    private transient ByteInputChannel in = null;
    private transient ByteOutputChannel out = null;

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/ByteChunk$ByteInputChannel.class */
    public interface ByteInputChannel {
        int realReadBytes() throws IOException;
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/ByteChunk$ByteOutputChannel.class */
    public interface ByteOutputChannel {
        void realWriteBytes(byte[] bArr, int i, int i2) throws IOException;

        void realWriteBytes(ByteBuffer byteBuffer) throws IOException;
    }

    public ByteChunk() {
    }

    public ByteChunk(int initial) {
        allocate(initial, -1);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeUTF(getCharset().name());
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.charset = Charset.forName(ois.readUTF());
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override // org.apache.tomcat.util.buf.AbstractChunk
    public void recycle() {
        super.recycle();
        this.charset = null;
    }

    public void allocate(int initial, int limit) {
        if (this.buff == null || this.buff.length < initial) {
            this.buff = new byte[initial];
        }
        setLimit(limit);
        this.start = 0;
        this.end = 0;
        this.isSet = true;
        this.hasHashCode = false;
    }

    public void setBytes(byte[] b, int off, int len) {
        this.buff = b;
        this.start = off;
        this.end = this.start + len;
        this.isSet = true;
        this.hasHashCode = false;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public Charset getCharset() {
        if (this.charset == null) {
            this.charset = DEFAULT_CHARSET;
        }
        return this.charset;
    }

    public byte[] getBytes() {
        return getBuffer();
    }

    public byte[] getBuffer() {
        return this.buff;
    }

    public void setByteInputChannel(ByteInputChannel in) {
        this.in = in;
    }

    public void setByteOutputChannel(ByteOutputChannel out) {
        this.out = out;
    }

    public void append(byte b) throws IOException {
        makeSpace(1);
        int limit = getLimitInternal();
        if (this.end >= limit) {
            flushBuffer();
        }
        byte[] bArr = this.buff;
        int i = this.end;
        this.end = i + 1;
        bArr[i] = b;
    }

    public void append(ByteChunk src) throws IOException {
        append(src.getBytes(), src.getStart(), src.getLength());
    }

    public void append(byte[] src, int off, int len) throws IOException {
        makeSpace(len);
        int limit = getLimitInternal();
        if (len == limit && this.end == this.start && this.out != null) {
            this.out.realWriteBytes(src, off, len);
            return;
        }
        if (len <= limit - this.end) {
            System.arraycopy(src, off, this.buff, this.end, len);
            this.end += len;
            return;
        }
        int avail = limit - this.end;
        System.arraycopy(src, off, this.buff, this.end, avail);
        this.end += avail;
        flushBuffer();
        int i = len;
        int i2 = avail;
        while (true) {
            int remain = i - i2;
            if (remain > limit - this.end) {
                this.out.realWriteBytes(src, (off + len) - remain, limit - this.end);
                i = remain;
                i2 = limit - this.end;
            } else {
                System.arraycopy(src, (off + len) - remain, this.buff, this.end, remain);
                this.end += remain;
                return;
            }
        }
    }

    public void append(ByteBuffer from) throws IOException {
        int len = from.remaining();
        makeSpace(len);
        int limit = getLimitInternal();
        if (len == limit && this.end == this.start && this.out != null) {
            this.out.realWriteBytes(from);
            from.position(from.limit());
            return;
        }
        if (len <= limit - this.end) {
            from.get(this.buff, this.end, len);
            this.end += len;
            return;
        }
        int avail = limit - this.end;
        from.get(this.buff, this.end, avail);
        this.end += avail;
        flushBuffer();
        int fromLimit = from.limit();
        int remain = len - avail;
        int avail2 = limit - this.end;
        while (remain >= avail2) {
            from.limit(from.position() + avail2);
            this.out.realWriteBytes(from);
            from.position(from.limit());
            remain -= avail2;
        }
        from.limit(fromLimit);
        from.get(this.buff, this.end, remain);
        this.end += remain;
    }

    public int subtract() throws IOException {
        if (checkEof()) {
            return -1;
        }
        byte[] bArr = this.buff;
        int i = this.start;
        this.start = i + 1;
        return bArr[i] & 255;
    }

    public byte subtractB() throws IOException {
        if (checkEof()) {
            return (byte) -1;
        }
        byte[] bArr = this.buff;
        int i = this.start;
        this.start = i + 1;
        return bArr[i];
    }

    public int subtract(byte[] dest, int off, int len) throws IOException {
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

    public int subtract(ByteBuffer to) throws IOException {
        if (checkEof()) {
            return -1;
        }
        int n = Math.min(to.remaining(), getLength());
        to.put(this.buff, this.start, n);
        to.limit(to.position());
        to.position(to.position() - n);
        this.start += n;
        return n;
    }

    private boolean checkEof() throws IOException {
        if (this.end - this.start == 0) {
            if (this.in == null) {
                return true;
            }
            int n = this.in.realReadBytes();
            if (n < 0) {
                return true;
            }
            return false;
        }
        return false;
    }

    public void flushBuffer() throws IOException {
        if (this.out == null) {
            throw new BufferOverflowException(sm.getString("chunk.overflow", Integer.valueOf(getLimit()), Integer.valueOf(this.buff.length)));
        }
        this.out.realWriteBytes(this.buff, this.start, this.end - this.start);
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
            this.buff = new byte[(int) desiredSize];
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
        byte[] tmp = new byte[(int) newSize];
        System.arraycopy(this.buff, this.start, tmp, 0, this.end - this.start);
        this.buff = tmp;
        this.end -= this.start;
        this.start = 0;
    }

    public String toString() {
        try {
            return toString(CodingErrorAction.REPLACE, CodingErrorAction.REPLACE);
        } catch (CharacterCodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public String toString(CodingErrorAction malformedInputAction, CodingErrorAction unmappableCharacterAction) throws CharacterCodingException {
        if (isNull()) {
            return null;
        }
        if (this.end - this.start == 0) {
            return "";
        }
        return StringCache.toString(this, malformedInputAction, unmappableCharacterAction);
    }

    @Deprecated
    public String toStringInternal() {
        try {
            return toStringInternal(CodingErrorAction.REPLACE, CodingErrorAction.REPLACE);
        } catch (CharacterCodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public String toStringInternal(CodingErrorAction malformedInputAction, CodingErrorAction unmappableCharacterAction) throws CharacterCodingException {
        CharBuffer cb;
        if (this.charset == null) {
            this.charset = DEFAULT_CHARSET;
        }
        if (malformedInputAction == CodingErrorAction.REPLACE && unmappableCharacterAction == CodingErrorAction.REPLACE) {
            cb = this.charset.decode(ByteBuffer.wrap(this.buff, this.start, this.end - this.start));
        } else {
            cb = this.charset.newDecoder().onMalformedInput(malformedInputAction).onUnmappableCharacter(unmappableCharacterAction).decode(ByteBuffer.wrap(this.buff, this.start, this.end - this.start));
        }
        return new String(cb.array(), cb.arrayOffset(), cb.length());
    }

    public long getLong() {
        return Ascii.parseLong(this.buff, this.start, this.end - this.start);
    }

    public boolean equals(Object obj) {
        if (obj instanceof ByteChunk) {
            return equals((ByteChunk) obj);
        }
        return false;
    }

    public boolean equals(String s) {
        byte[] b = this.buff;
        int len = this.end - this.start;
        if (b == null || len != s.length()) {
            return false;
        }
        int off = this.start;
        for (int i = 0; i < len; i++) {
            int i2 = off;
            off++;
            if (b[i2] != s.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public boolean equalsIgnoreCase(String s) {
        byte[] b = this.buff;
        int len = this.end - this.start;
        if (b == null || len != s.length()) {
            return false;
        }
        int off = this.start;
        for (int i = 0; i < len; i++) {
            int i2 = off;
            off++;
            if (Ascii.toLower(b[i2]) != Ascii.toLower(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(ByteChunk bb) {
        return equals(bb.getBytes(), bb.getStart(), bb.getLength());
    }

    public boolean equals(byte[] b2, int off2, int len2) {
        int i;
        int i2;
        byte[] b1 = this.buff;
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

    public boolean equalsIgnoreCase(byte[] b2, int off2, int len2) {
        int i;
        int i2;
        byte[] b1 = this.buff;
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
        } while (Ascii.toLower(b1[i]) == Ascii.toLower(b2[i2]));
        return false;
    }

    public boolean equals(CharChunk cc) {
        return equals(cc.getChars(), cc.getStart(), cc.getLength());
    }

    public boolean equals(char[] c2, int off2, int len2) {
        int i;
        int i2;
        byte[] b1 = this.buff;
        if (c2 == null && b1 == null) {
            return true;
        }
        if (b1 == null || c2 == null || this.end - this.start != len2) {
            return false;
        }
        int off1 = this.start;
        int len = this.end - this.start;
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
        } while (((char) b1[i]) == c2[i2]);
        return false;
    }

    public boolean startsWith(String s, int pos) {
        byte[] b = this.buff;
        int len = s.length();
        if (b == null || len + pos > this.end - this.start) {
            return false;
        }
        int off = this.start + pos;
        for (int i = 0; i < len; i++) {
            int i2 = off;
            off++;
            if (b[i2] != s.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public boolean startsWithIgnoreCase(String s, int pos) {
        byte[] b = this.buff;
        int len = s.length();
        if (b == null || len + pos > this.end - this.start) {
            return false;
        }
        int off = this.start + pos;
        for (int i = 0; i < len; i++) {
            int i2 = off;
            off++;
            if (Ascii.toLower(b[i2]) != Ascii.toLower(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Override // org.apache.tomcat.util.buf.AbstractChunk
    protected int getBufferElement(int index) {
        return this.buff[index];
    }

    public int indexOf(char c, int starting) {
        int ret = indexOf(this.buff, this.start + starting, this.end, c);
        if (ret >= this.start) {
            return ret - this.start;
        }
        return -1;
    }

    public static int indexOf(byte[] bytes, int start, int end, char s) {
        for (int offset = start; offset < end; offset++) {
            byte b = bytes[offset];
            if (b == s) {
                return offset;
            }
        }
        return -1;
    }

    public static int findByte(byte[] bytes, int start, int end, byte b) {
        for (int offset = start; offset < end; offset++) {
            if (bytes[offset] == b) {
                return offset;
            }
        }
        return -1;
    }

    public static int findBytes(byte[] bytes, int start, int end, byte[] b) {
        for (int offset = start; offset < end; offset++) {
            for (byte value : b) {
                if (bytes[offset] == value) {
                    return offset;
                }
            }
        }
        return -1;
    }

    public static byte[] convertToBytes(String value) {
        byte[] result = new byte[value.length()];
        for (int i = 0; i < value.length(); i++) {
            result[i] = (byte) value.charAt(i);
        }
        return result;
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/ByteChunk$BufferOverflowException.class */
    public static class BufferOverflowException extends IOException {
        private static final long serialVersionUID = 1;

        public BufferOverflowException(String message) {
            super(message);
        }
    }
}
