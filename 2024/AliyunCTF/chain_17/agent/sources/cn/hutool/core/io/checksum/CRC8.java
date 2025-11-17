package cn.hutool.core.io.checksum;

import java.io.Serializable;
import java.util.zip.Checksum;
import org.apache.tomcat.util.bcel.Const;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/checksum/CRC8.class */
public class CRC8 implements Checksum, Serializable {
    private static final long serialVersionUID = 1;
    private final short init;
    private final short[] crcTable = new short[256];
    private short value;

    public CRC8(int polynomial, short init) {
        int i;
        this.init = init;
        this.value = init;
        for (int dividend = 0; dividend < 256; dividend++) {
            int remainder = dividend;
            for (int bit = 0; bit < 8; bit++) {
                if ((remainder & 1) != 0) {
                    i = (remainder >>> 1) ^ polynomial;
                } else {
                    i = remainder >>> 1;
                }
                remainder = i;
            }
            this.crcTable[dividend] = (short) remainder;
        }
    }

    @Override // java.util.zip.Checksum
    public void update(byte[] buffer, int offset, int len) {
        for (int i = 0; i < len; i++) {
            int data = buffer[offset + i] ^ this.value;
            this.value = (short) (this.crcTable[data & Const.MAX_ARRAY_DIMENSIONS] ^ (this.value << 8));
        }
    }

    @Override // java.util.zip.Checksum
    public void update(byte[] buffer) {
        update(buffer, 0, buffer.length);
    }

    @Override // java.util.zip.Checksum
    public void update(int b) {
        update(new byte[]{(byte) b}, 0, 1);
    }

    @Override // java.util.zip.Checksum
    public long getValue() {
        return this.value & 255;
    }

    @Override // java.util.zip.Checksum
    public void reset() {
        this.value = this.init;
    }
}
