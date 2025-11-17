package cn.hutool.core.io.checksum.crc16;

import cn.hutool.core.net.NetUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/checksum/crc16/CRC16Ansi.class */
public class CRC16Ansi extends CRC16Checksum {
    private static final long serialVersionUID = 1;
    private static final int WC_POLY = 40961;

    @Override // cn.hutool.core.io.checksum.crc16.CRC16Checksum, java.util.zip.Checksum
    public void reset() {
        this.wCRCin = NetUtil.PORT_RANGE_MAX;
    }

    @Override // java.util.zip.Checksum
    public void update(int b) {
        int hi = this.wCRCin >> 8;
        this.wCRCin = hi ^ b;
        for (int i = 0; i < 8; i++) {
            int flag = this.wCRCin & 1;
            this.wCRCin >>= 1;
            if (flag == 1) {
                this.wCRCin ^= 40961;
            }
        }
    }
}
