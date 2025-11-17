package org.h2.store;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import org.h2.util.Bits;
import org.h2.util.MathUtils;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/Data.class */
public class Data {
    private byte[] data;
    private int pos;

    private Data(byte[] bArr) {
        this.data = bArr;
    }

    public void writeInt(int i) {
        Bits.writeInt(this.data, this.pos, i);
        this.pos += 4;
    }

    public int readInt() {
        int readInt = Bits.readInt(this.data, this.pos);
        this.pos += 4;
        return readInt;
    }

    private void writeStringWithoutLength(char[] cArr, int i) {
        int i2 = this.pos;
        byte[] bArr = this.data;
        for (int i3 = 0; i3 < i; i3++) {
            char c = cArr[i3];
            if (c < 128) {
                int i4 = i2;
                i2++;
                bArr[i4] = (byte) c;
            } else if (c >= 2048) {
                int i5 = i2;
                int i6 = i2 + 1;
                bArr[i5] = (byte) (224 | (c >> '\f'));
                int i7 = i6 + 1;
                bArr[i6] = (byte) ((c >> 6) & 63);
                i2 = i7 + 1;
                bArr[i7] = (byte) (c & '?');
            } else {
                int i8 = i2;
                int i9 = i2 + 1;
                bArr[i8] = (byte) (192 | (c >> 6));
                i2 = i9 + 1;
                bArr[i9] = (byte) (c & '?');
            }
        }
        this.pos = i2;
    }

    public static Data create(int i) {
        return new Data(new byte[i]);
    }

    public int length() {
        return this.pos;
    }

    public byte[] getBytes() {
        return this.data;
    }

    public void reset() {
        this.pos = 0;
    }

    public void write(byte[] bArr, int i, int i2) {
        System.arraycopy(bArr, i, this.data, this.pos, i2);
        this.pos += i2;
    }

    public void read(byte[] bArr, int i, int i2) {
        System.arraycopy(this.data, this.pos, bArr, i, i2);
        this.pos += i2;
    }

    public void setPos(int i) {
        this.pos = i;
    }

    public byte readByte() {
        byte[] bArr = this.data;
        int i = this.pos;
        this.pos = i + 1;
        return bArr[i];
    }

    public void checkCapacity(int i) {
        if (this.pos + i >= this.data.length) {
            expand(i);
        }
    }

    private void expand(int i) {
        this.data = Utils.copyBytes(this.data, (this.data.length + i) * 2);
    }

    public void fillAligned() {
        int roundUpInt = MathUtils.roundUpInt(this.pos + 2, 16);
        this.pos = roundUpInt;
        if (this.data.length < roundUpInt) {
            checkCapacity(roundUpInt - this.data.length);
        }
    }

    public static void copyString(Reader reader, OutputStream outputStream) throws IOException {
        char[] cArr = new char[4096];
        Data data = new Data(new byte[12288]);
        while (true) {
            int read = reader.read(cArr);
            if (read >= 0) {
                data.writeStringWithoutLength(cArr, read);
                outputStream.write(data.data, 0, data.pos);
                data.reset();
            } else {
                return;
            }
        }
    }
}
