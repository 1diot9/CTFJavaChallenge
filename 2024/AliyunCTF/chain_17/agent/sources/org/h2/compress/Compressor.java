package org.h2.compress;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/compress/Compressor.class */
public interface Compressor {
    public static final int NO = 0;
    public static final int LZF = 1;
    public static final int DEFLATE = 2;

    int getAlgorithm();

    int compress(byte[] bArr, int i, int i2, byte[] bArr2, int i3);

    void expand(byte[] bArr, int i, int i2, byte[] bArr2, int i3, int i4);

    void setOptions(String str);
}
