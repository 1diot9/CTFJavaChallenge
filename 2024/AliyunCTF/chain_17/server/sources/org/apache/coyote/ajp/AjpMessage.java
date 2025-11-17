package org.apache.coyote.ajp;

import java.nio.ByteBuffer;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/ajp/AjpMessage.class */
public class AjpMessage {
    private static final Log log = LogFactory.getLog((Class<?>) AjpMessage.class);
    protected static final StringManager sm = StringManager.getManager((Class<?>) AjpMessage.class);
    protected final byte[] buf;
    protected int pos;
    protected int len;

    public AjpMessage(int packetSize) {
        this.buf = new byte[packetSize];
    }

    public void reset() {
        this.len = 4;
        this.pos = 4;
    }

    public void end() {
        this.len = this.pos;
        int dLen = this.len - 4;
        this.buf[0] = 65;
        this.buf[1] = 66;
        this.buf[2] = (byte) ((dLen >>> 8) & 255);
        this.buf[3] = (byte) (dLen & 255);
    }

    public byte[] getBuffer() {
        return this.buf;
    }

    public int getLen() {
        return this.len;
    }

    public void appendInt(int val) {
        byte[] bArr = this.buf;
        int i = this.pos;
        this.pos = i + 1;
        bArr[i] = (byte) ((val >>> 8) & 255);
        byte[] bArr2 = this.buf;
        int i2 = this.pos;
        this.pos = i2 + 1;
        bArr2[i2] = (byte) (val & 255);
    }

    public void appendByte(int val) {
        byte[] bArr = this.buf;
        int i = this.pos;
        this.pos = i + 1;
        bArr[i] = (byte) val;
    }

    public void appendBytes(MessageBytes mb) {
        if (mb == null) {
            log.error(sm.getString("ajpmessage.null"), new NullPointerException());
            appendInt(0);
            appendByte(0);
            return;
        }
        if (mb.getType() != 2) {
            mb.toBytes();
            ByteChunk bc = mb.getByteChunk();
            byte[] buffer = bc.getBuffer();
            for (int i = bc.getOffset(); i < bc.getLength(); i++) {
                if ((buffer[i] > -1 && buffer[i] <= 31 && buffer[i] != 9) || buffer[i] == Byte.MAX_VALUE) {
                    buffer[i] = 32;
                }
            }
        }
        appendByteChunk(mb.getByteChunk());
    }

    public void appendByteChunk(ByteChunk bc) {
        if (bc == null) {
            log.error(sm.getString("ajpmessage.null"), new NullPointerException());
            appendInt(0);
            appendByte(0);
            return;
        }
        appendBytes(bc.getBytes(), bc.getStart(), bc.getLength());
    }

    public void appendBytes(byte[] b, int off, int numBytes) {
        if (checkOverflow(numBytes)) {
            return;
        }
        appendInt(numBytes);
        System.arraycopy(b, off, this.buf, this.pos, numBytes);
        this.pos += numBytes;
        appendByte(0);
    }

    public void appendBytes(ByteBuffer b) {
        int numBytes = b.remaining();
        if (checkOverflow(numBytes)) {
            return;
        }
        appendInt(numBytes);
        b.get(this.buf, this.pos, numBytes);
        this.pos += numBytes;
        appendByte(0);
    }

    private boolean checkOverflow(int numBytes) {
        if (this.pos + numBytes + 3 > this.buf.length) {
            log.error(sm.getString("ajpmessage.overflow", numBytes, this.pos), new ArrayIndexOutOfBoundsException());
            if (log.isDebugEnabled()) {
                dump("Overflow/coBytes");
                return true;
            }
            return true;
        }
        return false;
    }

    public int getInt() {
        byte[] bArr = this.buf;
        int i = this.pos;
        this.pos = i + 1;
        int b1 = bArr[i] & 255;
        byte[] bArr2 = this.buf;
        int i2 = this.pos;
        this.pos = i2 + 1;
        int b2 = bArr2[i2] & 255;
        validatePos(this.pos);
        return (b1 << 8) + b2;
    }

    public int peekInt() {
        validatePos(this.pos + 2);
        int b1 = this.buf[this.pos] & 255;
        int b2 = this.buf[this.pos + 1] & 255;
        return (b1 << 8) + b2;
    }

    public byte getByte() {
        byte[] bArr = this.buf;
        int i = this.pos;
        this.pos = i + 1;
        byte res = bArr[i];
        validatePos(this.pos);
        return res;
    }

    public void getBytes(MessageBytes mb) {
        doGetBytes(mb, true);
    }

    public void getBodyBytes(MessageBytes mb) {
        doGetBytes(mb, false);
    }

    private void doGetBytes(MessageBytes mb, boolean terminated) {
        int length = getInt();
        if (length == 65535 || length == -1) {
            mb.recycle();
            return;
        }
        if (terminated) {
            validatePos(this.pos + length + 1);
        } else {
            validatePos(this.pos + length);
        }
        mb.setBytes(this.buf, this.pos, length);
        mb.getCharChunk().recycle();
        this.pos += length;
        if (terminated) {
            this.pos++;
        }
    }

    public int getLongInt() {
        byte[] bArr = this.buf;
        int i = this.pos;
        this.pos = i + 1;
        int b1 = bArr[i] & 255;
        byte[] bArr2 = this.buf;
        int i2 = this.pos;
        this.pos = i2 + 1;
        int b12 = ((b1 << 8) | (bArr2[i2] & 255)) << 8;
        byte[] bArr3 = this.buf;
        int i3 = this.pos;
        this.pos = i3 + 1;
        int b13 = (b12 | (bArr3[i3] & 255)) << 8;
        byte[] bArr4 = this.buf;
        int i4 = this.pos;
        this.pos = i4 + 1;
        int b14 = b13 | (bArr4[i4] & 255);
        validatePos(this.pos);
        return b14;
    }

    public int processHeader(boolean toContainer) {
        this.pos = 0;
        int mark = getInt();
        this.len = getInt();
        if ((toContainer && mark != 4660) || (!toContainer && mark != 16706)) {
            log.error(sm.getString("ajpmessage.invalid", mark));
            if (log.isDebugEnabled()) {
                dump("In");
                return -1;
            }
            return -1;
        }
        if (log.isDebugEnabled()) {
            log.debug("Received " + this.len + " " + this.buf[0]);
        }
        return this.len;
    }

    private void dump(String prefix) {
        if (log.isDebugEnabled()) {
            log.debug(prefix + ": " + HexUtils.toHexString(this.buf) + " " + this.pos + "/" + (this.len + 4));
        }
        int max = this.pos;
        if (this.len + 4 > this.pos) {
            max = this.len + 4;
        }
        if (max > 1000) {
            max = 1000;
        }
        if (log.isDebugEnabled()) {
            for (int j = 0; j < max; j += 16) {
                log.debug(hexLine(this.buf, j, this.len));
            }
        }
    }

    private void validatePos(int posToTest) {
        if (posToTest > this.len + 4) {
            throw new ArrayIndexOutOfBoundsException(sm.getString("ajpMessage.invalidPos", Integer.valueOf(posToTest)));
        }
    }

    protected static String hexLine(byte[] buf, int start, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < start + 16; i++) {
            if (i < len + 4) {
                sb.append(hex(buf[i])).append(' ');
            } else {
                sb.append("   ");
            }
        }
        sb.append(" | ");
        for (int i2 = start; i2 < start + 16 && i2 < len + 4; i2++) {
            if (!Character.isISOControl((char) buf[i2])) {
                sb.append(Character.valueOf((char) buf[i2]));
            } else {
                sb.append('.');
            }
        }
        return sb.toString();
    }

    protected static String hex(int x) {
        String h = Integer.toHexString(x);
        if (h.length() == 1) {
            h = "0" + h;
        }
        return h.substring(h.length() - 2);
    }
}
