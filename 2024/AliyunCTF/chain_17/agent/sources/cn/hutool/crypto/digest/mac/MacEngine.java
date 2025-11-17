package cn.hutool.crypto.digest.mac;

import cn.hutool.crypto.CryptoException;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/digest/mac/MacEngine.class */
public interface MacEngine {
    void update(byte[] bArr, int i, int i2);

    byte[] doFinal();

    void reset();

    int getMacLength();

    String getAlgorithm();

    default void update(byte[] in) {
        update(in, 0, in.length);
    }

    default byte[] digest(InputStream data, int bufferLength) {
        if (bufferLength < 1) {
            bufferLength = 8192;
        }
        byte[] buffer = new byte[bufferLength];
        try {
            try {
                int read = data.read(buffer, 0, bufferLength);
                while (read > -1) {
                    update(buffer, 0, read);
                    read = data.read(buffer, 0, bufferLength);
                }
                byte[] result = doFinal();
                reset();
                return result;
            } catch (IOException e) {
                throw new CryptoException(e);
            }
        } catch (Throwable th) {
            reset();
            throw th;
        }
    }
}
