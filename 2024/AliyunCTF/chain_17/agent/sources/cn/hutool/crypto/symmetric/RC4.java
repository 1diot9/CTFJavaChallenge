package cn.hutool.crypto.symmetric;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.CryptoException;
import cn.hutool.crypto.SecureUtil;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.tomcat.util.bcel.Const;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/symmetric/RC4.class */
public class RC4 implements Serializable {
    private static final long serialVersionUID = 1;
    private static final int SBOX_LENGTH = 256;
    private static final int KEY_MIN_LENGTH = 5;
    private int[] sbox;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public RC4(String key) throws CryptoException {
        setKey(key);
    }

    public byte[] encrypt(String message, Charset charset) throws CryptoException {
        return crypt(StrUtil.bytes(message, charset));
    }

    public byte[] encrypt(String message) throws CryptoException {
        return encrypt(message, CharsetUtil.CHARSET_UTF_8);
    }

    public String encryptHex(byte[] data) {
        return HexUtil.encodeHexStr(crypt(data));
    }

    public String encryptBase64(byte[] data) {
        return Base64.encode(crypt(data));
    }

    public String encryptHex(String data, Charset charset) {
        return HexUtil.encodeHexStr(encrypt(data, charset));
    }

    public String encryptHex(String data) {
        return HexUtil.encodeHexStr(encrypt(data));
    }

    public String encryptBase64(String data, Charset charset) {
        return Base64.encode(encrypt(data, charset));
    }

    public String encryptBase64(String data) {
        return Base64.encode(encrypt(data));
    }

    public String decrypt(byte[] message, Charset charset) throws CryptoException {
        return StrUtil.str(crypt(message), charset);
    }

    public String decrypt(byte[] message) throws CryptoException {
        return decrypt(message, CharsetUtil.CHARSET_UTF_8);
    }

    public String decrypt(String message) {
        return decrypt(SecureUtil.decode(message));
    }

    public String decrypt(String message, Charset charset) {
        return StrUtil.str(decrypt(message), charset);
    }

    public byte[] crypt(byte[] msg) {
        ReentrantReadWriteLock.ReadLock readLock = this.lock.readLock();
        readLock.lock();
        try {
            int[] sbox = (int[]) this.sbox.clone();
            byte[] code = new byte[msg.length];
            int i = 0;
            int j = 0;
            for (int n = 0; n < msg.length; n++) {
                i = (i + 1) % 256;
                j = (j + sbox[i]) % 256;
                swap(i, j, sbox);
                int rand = sbox[(sbox[i] + sbox[j]) % 256];
                code[n] = (byte) (rand ^ msg[n]);
            }
            return code;
        } finally {
            readLock.unlock();
        }
    }

    public void setKey(String key) throws CryptoException {
        int length = key.length();
        if (length < 5 || length >= 256) {
            throw new CryptoException("Key length has to be between {} and {}", 5, Integer.valueOf(Const.MAX_ARRAY_DIMENSIONS));
        }
        ReentrantReadWriteLock.WriteLock writeLock = this.lock.writeLock();
        writeLock.lock();
        try {
            this.sbox = initSBox(StrUtil.utf8Bytes(key));
            writeLock.unlock();
        } catch (Throwable th) {
            writeLock.unlock();
            throw th;
        }
    }

    private int[] initSBox(byte[] key) {
        int[] sbox = new int[256];
        int j = 0;
        for (int i = 0; i < 256; i++) {
            sbox[i] = i;
        }
        for (int i2 = 0; i2 < 256; i2++) {
            j = (((j + sbox[i2]) + key[i2 % key.length]) & Const.MAX_ARRAY_DIMENSIONS) % 256;
            swap(i2, j, sbox);
        }
        return sbox;
    }

    private void swap(int i, int j, int[] sbox) {
        int temp = sbox[i];
        sbox[i] = sbox[j];
        sbox[j] = temp;
    }
}
