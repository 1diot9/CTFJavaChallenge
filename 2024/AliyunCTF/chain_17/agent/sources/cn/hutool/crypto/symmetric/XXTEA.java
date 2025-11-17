package cn.hutool.crypto.symmetric;

import cn.hutool.core.io.IoUtil;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/symmetric/XXTEA.class */
public class XXTEA implements SymmetricEncryptor, SymmetricDecryptor, Serializable {
    private static final long serialVersionUID = 1;
    private static final int DELTA = -1640531527;
    private final byte[] key;

    public XXTEA(byte[] key) {
        this.key = key;
    }

    @Override // cn.hutool.crypto.symmetric.SymmetricEncryptor
    public byte[] encrypt(byte[] data) {
        if (data.length == 0) {
            return data;
        }
        return toByteArray(encrypt(toIntArray(data, true), toIntArray(fixKey(this.key), false)), false);
    }

    @Override // cn.hutool.crypto.symmetric.SymmetricEncryptor
    public void encrypt(InputStream data, OutputStream out, boolean isClose) {
        IoUtil.write(out, isClose, encrypt(IoUtil.readBytes(data)));
    }

    @Override // cn.hutool.crypto.symmetric.SymmetricDecryptor
    public byte[] decrypt(byte[] data) {
        if (data.length == 0) {
            return data;
        }
        return toByteArray(decrypt(toIntArray(data, false), toIntArray(fixKey(this.key), false)), true);
    }

    @Override // cn.hutool.crypto.symmetric.SymmetricDecryptor
    public void decrypt(InputStream data, OutputStream out, boolean isClose) {
        IoUtil.write(out, isClose, decrypt(IoUtil.readBytes(data)));
    }

    private static int[] encrypt(int[] v, int[] k) {
        int n = v.length - 1;
        if (n < 1) {
            return v;
        }
        int q = 6 + (52 / (n + 1));
        int z = v[n];
        int sum = 0;
        while (true) {
            int i = q;
            q--;
            if (i > 0) {
                sum += DELTA;
                int e = (sum >>> 2) & 3;
                int p = 0;
                while (p < n) {
                    int y = v[p + 1];
                    int i2 = p;
                    int mx = v[i2] + mx(sum, y, z, p, e, k);
                    v[i2] = mx;
                    z = mx;
                    p++;
                }
                int y2 = v[0];
                int mx2 = v[n] + mx(sum, y2, z, p, e, k);
                v[n] = mx2;
                z = mx2;
            } else {
                return v;
            }
        }
    }

    private static int[] decrypt(int[] v, int[] k) {
        int n = v.length - 1;
        if (n < 1) {
            return v;
        }
        int q = 6 + (52 / (n + 1));
        int y = v[0];
        int i = q * DELTA;
        while (true) {
            int sum = i;
            if (sum != 0) {
                int e = (sum >>> 2) & 3;
                int p = n;
                while (p > 0) {
                    int z = v[p - 1];
                    int i2 = p;
                    int mx = v[i2] - mx(sum, y, z, p, e, k);
                    v[i2] = mx;
                    y = mx;
                    p--;
                }
                int z2 = v[n];
                int mx2 = v[0] - mx(sum, y, z2, p, e, k);
                v[0] = mx2;
                y = mx2;
                i = sum - DELTA;
            } else {
                return v;
            }
        }
    }

    private static int mx(int sum, int y, int z, int p, int e, int[] k) {
        return (((z >>> 5) ^ (y << 2)) + ((y >>> 3) ^ (z << 4))) ^ ((sum ^ y) + (k[(p & 3) ^ e] ^ z));
    }

    private static byte[] fixKey(byte[] key) {
        if (key.length == 16) {
            return key;
        }
        byte[] fixedkey = new byte[16];
        System.arraycopy(key, 0, fixedkey, 0, Math.min(key.length, 16));
        return fixedkey;
    }

    private static int[] toIntArray(byte[] data, boolean includeLength) {
        int[] result;
        int n = (data.length & 3) == 0 ? data.length >>> 2 : (data.length >>> 2) + 1;
        if (includeLength) {
            result = new int[n + 1];
            result[n] = data.length;
        } else {
            result = new int[n];
        }
        int n2 = data.length;
        for (int i = 0; i < n2; i++) {
            int[] iArr = result;
            int i2 = i >>> 2;
            iArr[i2] = iArr[i2] | ((255 & data[i]) << ((i & 3) << 3));
        }
        return result;
    }

    private static byte[] toByteArray(int[] data, boolean includeLength) {
        int n = data.length << 2;
        if (includeLength) {
            int m = data[data.length - 1];
            int n2 = n - 4;
            if (m < n2 - 3 || m > n2) {
                return null;
            }
            n = m;
        }
        byte[] result = new byte[n];
        for (int i = 0; i < n; i++) {
            result[i] = (byte) (data[i >>> 2] >>> ((i & 3) << 3));
        }
        return result;
    }
}
