package ch.qos.logback.core.util;

import java.math.BigDecimal;
import java.nio.ByteBuffer;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/util/DirectJson.class */
public final class DirectJson {
    private static final int INITIAL_BUFFER_SIZE = 1024;
    private static final byte QUOTE = 34;
    private static final byte ENTRY_SEP = 58;
    private static final byte KV_SEP = 44;
    private static final byte DOT = 46;
    private static final byte OPEN_OBJ = 123;
    private static final byte CLOSE_OBJ = 125;
    private static final byte OPEN_ARR = 91;
    private static final byte CLOSE_ARR = 93;
    private static final byte[] NEWLINE = {92, 110};
    private static final byte[] ESCAPE = {92, 92};
    private static final byte[] LINEBREAK = {92, 114};
    private static final byte[] TAB = {92, 116};
    private static final byte[] TRUE = {116, 114, 117, 101};
    private static final byte[] FALSE = {102, 97, 108, 115, 101};
    private static final byte[] NULL = {110, 117, 108, 108};
    private ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

    public void openObject() {
        this.buffer.put((byte) 123);
    }

    public void openArray() {
        this.buffer.put((byte) 91);
    }

    public void openObject(String str) {
        writeString(str);
        writeEntrySep();
        this.buffer.put((byte) 123);
    }

    public void openArray(String str) {
        writeString(str);
        writeEntrySep();
        this.buffer.put((byte) 91);
    }

    public void closeObject() {
        int target = this.buffer.position() - 1;
        if (44 == this.buffer.get(target)) {
            this.buffer.put(target, (byte) 125);
        } else {
            this.buffer.put((byte) 125);
        }
    }

    public void closeArray() {
        int target = this.buffer.position() - 1;
        if (44 == this.buffer.get(target)) {
            this.buffer.put(target, (byte) 93);
        } else {
            this.buffer.put((byte) 93);
        }
    }

    public void writeRaw(String str) {
        for (int i = 0; i < str.length(); i++) {
            int chr = str.codePointAt(i);
            switch (chr) {
                case 9:
                    this.buffer.put(TAB);
                    break;
                case 10:
                    this.buffer.put(NEWLINE);
                    break;
                case 13:
                    this.buffer.put(LINEBREAK);
                    break;
                case 92:
                    this.buffer.put(ESCAPE);
                    break;
                default:
                    if (chr >= 128 && chr <= 1114111) {
                        this.buffer.put(String.valueOf(str.charAt(i)).getBytes());
                        break;
                    } else if (chr > 31) {
                        this.buffer.put((byte) chr);
                        break;
                    } else {
                        break;
                    }
            }
        }
    }

    public void writeRaw(char chr) {
        this.buffer.put((byte) chr);
    }

    public void writeRaw(byte[] chr) {
        this.buffer.put(chr);
    }

    public void writeQuote() {
        this.buffer.put((byte) 34);
    }

    public void writeString(String str) {
        checkSpace(str.length() + 3);
        this.buffer.put((byte) 34);
        writeRaw(str);
        this.buffer.put((byte) 34);
        this.buffer.put((byte) 44);
    }

    public void writeSep() {
        this.buffer.put((byte) 44);
    }

    public void writeNumberRaw(long data) {
        int pos = this.buffer.position();
        int sz = ((int) Math.log10(data)) + 1;
        long dataPointer = data;
        for (int i = sz - 1; i >= 0; i--) {
            byte chr = (byte) (dataPointer % 10);
            dataPointer /= 10;
            this.buffer.put(pos + i, (byte) (chr + 48));
        }
        this.buffer.position(pos + sz);
    }

    public void writeNumber(long data) {
        int pos = this.buffer.position();
        int sz = data == 0 ? 1 : ((int) Math.log10(data)) + 1;
        long dataPointer = data;
        for (int i = sz - 1; i >= 0; i--) {
            byte chr = (byte) (dataPointer % 10);
            dataPointer /= 10;
            this.buffer.put(pos + i, (byte) (chr + 48));
        }
        this.buffer.position(pos + sz);
        this.buffer.put((byte) 44);
    }

    public void writeNumber(double data) {
        int pos = this.buffer.position();
        long whole = (long) data;
        int sz = ((int) Math.log10(whole)) + 1;
        for (int i = sz - 1; i >= 0; i--) {
            byte chr = (byte) (whole % 10);
            whole /= 10;
            this.buffer.put(pos + i, (byte) (chr + 48));
        }
        this.buffer.position(pos + sz);
        this.buffer.put((byte) 46);
        int pos2 = this.buffer.position();
        BigDecimal fractional = BigDecimal.valueOf(data).remainder(BigDecimal.ONE);
        int decs = 0;
        while (!fractional.equals(BigDecimal.ZERO)) {
            BigDecimal fractional2 = fractional.movePointRight(1);
            byte chr2 = (byte) (fractional2.intValue() + 48);
            fractional = fractional2.remainder(BigDecimal.ONE);
            decs++;
            this.buffer.put(chr2);
        }
        this.buffer.position(pos2 + decs);
        this.buffer.put((byte) 44);
    }

    public void writeEntrySep() {
        this.buffer.put(this.buffer.position() - 1, (byte) 58);
    }

    public void writeStringValue(String key, String value) {
        writeString(key);
        writeEntrySep();
        writeString(value);
    }

    public void writeNumberValue(String key, long value) {
        writeString(key);
        writeEntrySep();
        writeNumber(value);
    }

    public void writeNumberValue(String key, double value) {
        writeString(key);
        writeEntrySep();
        writeNumber(value);
    }

    public void writeBoolean(boolean value) {
        this.buffer.put(value ? TRUE : FALSE);
        this.buffer.put((byte) 44);
    }

    public void writeNull() {
        this.buffer.put(NULL);
        this.buffer.put((byte) 44);
    }

    public void checkSpace(int size) {
        if (this.buffer.position() + size >= this.buffer.capacity()) {
            int newSize = (this.buffer.capacity() + size) * 2;
            ByteBuffer newBuffer = ByteBuffer.allocateDirect(newSize);
            this.buffer.flip();
            newBuffer.put(this.buffer);
            this.buffer = newBuffer;
        }
    }

    public byte[] flush() {
        byte[] result = new byte[this.buffer.position()];
        this.buffer.flip();
        this.buffer.get(result);
        this.buffer.clear();
        return result;
    }
}
