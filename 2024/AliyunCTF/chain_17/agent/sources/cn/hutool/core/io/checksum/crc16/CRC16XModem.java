package cn.hutool.core.io.checksum.crc16;

import cn.hutool.core.net.NetUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/checksum/crc16/CRC16XModem.class */
public class CRC16XModem extends CRC16Checksum {
    private static final long serialVersionUID = 1;
    private static final int WC_POLY = 4129;

    @Override // cn.hutool.core.io.checksum.crc16.CRC16Checksum, java.util.zip.Checksum
    public void update(byte[] b, int off, int len) {
        super.update(b, off, len);
        this.wCRCin &= NetUtil.PORT_RANGE_MAX;
    }

    @Override // java.util.zip.Checksum
    public void update(int b) {
        for (int i = 0; i < 8; i++) {
            boolean bit = ((b >> (7 - i)) & 1) == 1;
            boolean c15 = ((this.wCRCin >> 15) & 1) == 1;
            this.wCRCin <<= 1;
            if (c15 ^ bit) {
                this.wCRCin ^= WC_POLY;
            }
        }
    }
}
