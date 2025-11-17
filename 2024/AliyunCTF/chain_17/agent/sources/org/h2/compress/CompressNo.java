package org.h2.compress;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/compress/CompressNo.class */
public class CompressNo implements Compressor {
    @Override // org.h2.compress.Compressor
    public int getAlgorithm() {
        return 0;
    }

    @Override // org.h2.compress.Compressor
    public void setOptions(String str) {
    }

    @Override // org.h2.compress.Compressor
    public int compress(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        System.arraycopy(bArr, i, bArr2, i3, i2);
        return i3 + i2;
    }

    @Override // org.h2.compress.Compressor
    public void expand(byte[] bArr, int i, int i2, byte[] bArr2, int i3, int i4) {
        System.arraycopy(bArr, i, bArr2, i3, i4);
    }
}
