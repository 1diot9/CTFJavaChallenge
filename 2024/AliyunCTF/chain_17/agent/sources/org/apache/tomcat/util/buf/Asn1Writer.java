package org.apache.tomcat.util.buf;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/Asn1Writer.class */
public class Asn1Writer {
    public static byte[] writeSequence(byte[]... components) {
        int len = 0;
        for (byte[] bArr : components) {
            len += bArr.length;
        }
        byte[] combined = new byte[len];
        int pos = 0;
        for (byte[] component : components) {
            System.arraycopy(component, 0, combined, pos, component.length);
            pos += component.length;
        }
        return writeTag((byte) 48, combined);
    }

    public static byte[] writeInteger(int value) {
        int valueSize = 1;
        while ((value >> (valueSize * 8)) > 0) {
            valueSize++;
        }
        byte[] valueBytes = new byte[valueSize];
        int i = 0;
        while (valueSize > 0) {
            valueBytes[i] = (byte) (value >> (8 * (valueSize - 1)));
            value >>= 8;
            valueSize--;
            i++;
        }
        return writeTag((byte) 2, valueBytes);
    }

    public static byte[] writeOctetString(byte[] data) {
        return writeTag((byte) 4, data);
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x005b, code lost:            java.lang.System.arraycopy(r7, 0, r0, 1 + r9, r7.length);     */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x0069, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0035, code lost:            r0[1] = (byte) (127 + r9);        r11 = r9;     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x0042, code lost:            if (r8 <= 0) goto L17;     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0045, code lost:            r0[r11] = (byte) (r8 & org.apache.tomcat.util.bcel.Const.MAX_ARRAY_DIMENSIONS);        r8 = r8 >> 8;        r11 = r11 - 1;     */
    /* JADX WARN: Code restructure failed: missing block: B:2:0x0008, code lost:            if (r8 > 127) goto L4;     */
    /* JADX WARN: Code restructure failed: missing block: B:3:0x000b, code lost:            r9 = r9 + 1;     */
    /* JADX WARN: Code restructure failed: missing block: B:4:0x0014, code lost:            if ((r8 >> (r9 * 8)) > 0) goto L16;     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x0017, code lost:            r0 = new byte[(1 + r9) + r8];        r0[0] = r6;     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0029, code lost:            if (r8 >= 128) goto L9;     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x002c, code lost:            r0[1] = (byte) r8;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static byte[] writeTag(byte r6, byte[] r7) {
        /*
            r0 = r7
            int r0 = r0.length
            r8 = r0
            r0 = 1
            r9 = r0
            r0 = r8
            r1 = 127(0x7f, float:1.78E-43)
            if (r0 <= r1) goto L17
        Lb:
            int r9 = r9 + 1
            r0 = r8
            r1 = r9
            r2 = 8
            int r1 = r1 * r2
            int r0 = r0 >> r1
            if (r0 > 0) goto Lb
        L17:
            r0 = 1
            r1 = r9
            int r0 = r0 + r1
            r1 = r8
            int r0 = r0 + r1
            byte[] r0 = new byte[r0]
            r10 = r0
            r0 = r10
            r1 = 0
            r2 = r6
            r0[r1] = r2
            r0 = r8
            r1 = 128(0x80, float:1.8E-43)
            if (r0 >= r1) goto L35
            r0 = r10
            r1 = 1
            r2 = r8
            byte r2 = (byte) r2
            r0[r1] = r2
            goto L5b
        L35:
            r0 = r10
            r1 = 1
            r2 = 127(0x7f, float:1.78E-43)
            r3 = r9
            int r2 = r2 + r3
            byte r2 = (byte) r2
            r0[r1] = r2
            r0 = r9
            r11 = r0
        L41:
            r0 = r8
            if (r0 <= 0) goto L5b
            r0 = r10
            r1 = r11
            r2 = r8
            r3 = 255(0xff, float:3.57E-43)
            r2 = r2 & r3
            byte r2 = (byte) r2
            r0[r1] = r2
            r0 = r8
            r1 = 8
            int r0 = r0 >> r1
            r8 = r0
            int r11 = r11 + (-1)
            goto L41
        L5b:
            r0 = r7
            r1 = 0
            r2 = r10
            r3 = 1
            r4 = r9
            int r3 = r3 + r4
            r4 = r7
            int r4 = r4.length
            java.lang.System.arraycopy(r0, r1, r2, r3, r4)
            r0 = r10
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.buf.Asn1Writer.writeTag(byte, byte[]):byte[]");
    }
}
