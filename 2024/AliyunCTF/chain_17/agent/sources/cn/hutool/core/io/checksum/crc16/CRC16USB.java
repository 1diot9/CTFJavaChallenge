package cn.hutool.core.io.checksum.crc16;

import cn.hutool.core.net.NetUtil;
import org.apache.tomcat.util.bcel.Const;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/checksum/crc16/CRC16USB.class */
public class CRC16USB extends CRC16Checksum {
    private static final long serialVersionUID = 1;
    private static final int WC_POLY = 40961;

    @Override // cn.hutool.core.io.checksum.crc16.CRC16Checksum, java.util.zip.Checksum
    public void reset() {
        this.wCRCin = NetUtil.PORT_RANGE_MAX;
    }

    @Override // cn.hutool.core.io.checksum.crc16.CRC16Checksum, java.util.zip.Checksum
    public void update(byte[] b, int off, int len) {
        super.update(b, off, len);
        this.wCRCin ^= NetUtil.PORT_RANGE_MAX;
    }

    @Override // java.util.zip.Checksum
    public void update(int b) {
        this.wCRCin ^= b & Const.MAX_ARRAY_DIMENSIONS;
        for (int j = 0; j < 8; j++) {
            if ((this.wCRCin & 1) != 0) {
                this.wCRCin >>= 1;
                this.wCRCin ^= 40961;
            } else {
                this.wCRCin >>= 1;
            }
        }
    }
}
