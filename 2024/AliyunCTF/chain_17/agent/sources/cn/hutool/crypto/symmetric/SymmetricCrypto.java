package cn.hutool.crypto.symmetric;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.CipherMode;
import cn.hutool.crypto.CipherWrapper;
import cn.hutool.crypto.CryptoException;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.Padding;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/symmetric/SymmetricCrypto.class */
public class SymmetricCrypto implements SymmetricEncryptor, SymmetricDecryptor, Serializable {
    private static final long serialVersionUID = 1;
    private CipherWrapper cipherWrapper;
    private SecretKey secretKey;
    private boolean isZeroPadding;
    private final Lock lock;

    public SymmetricCrypto(SymmetricAlgorithm algorithm) {
        this(algorithm, (byte[]) null);
    }

    public SymmetricCrypto(String algorithm) {
        this(algorithm, (byte[]) null);
    }

    public SymmetricCrypto(SymmetricAlgorithm algorithm, byte[] key) {
        this(algorithm.getValue(), key);
    }

    public SymmetricCrypto(SymmetricAlgorithm algorithm, SecretKey key) {
        this(algorithm.getValue(), key);
    }

    public SymmetricCrypto(String algorithm, byte[] key) {
        this(algorithm, KeyUtil.generateKey(algorithm, key));
    }

    public SymmetricCrypto(String algorithm, SecretKey key) {
        this(algorithm, key, null);
    }

    public SymmetricCrypto(String algorithm, SecretKey key, AlgorithmParameterSpec paramsSpec) {
        this.lock = new ReentrantLock();
        init(algorithm, key);
        initParams(algorithm, paramsSpec);
    }

    public SymmetricCrypto init(String algorithm, SecretKey key) {
        Assert.notBlank(algorithm, "'algorithm' must be not blank !", new Object[0]);
        this.secretKey = key;
        if (algorithm.contains(Padding.ZeroPadding.name())) {
            algorithm = StrUtil.replace(algorithm, Padding.ZeroPadding.name(), Padding.NoPadding.name());
            this.isZeroPadding = true;
        }
        this.cipherWrapper = new CipherWrapper(algorithm);
        return this;
    }

    public SecretKey getSecretKey() {
        return this.secretKey;
    }

    public Cipher getCipher() {
        return this.cipherWrapper.getCipher();
    }

    public SymmetricCrypto setParams(AlgorithmParameterSpec params) {
        this.cipherWrapper.setParams(params);
        return this;
    }

    public SymmetricCrypto setIv(IvParameterSpec iv) {
        return setParams(iv);
    }

    public SymmetricCrypto setIv(byte[] iv) {
        return setIv(new IvParameterSpec(iv));
    }

    public SymmetricCrypto setRandom(SecureRandom random) {
        this.cipherWrapper.setRandom(random);
        return this;
    }

    public SymmetricCrypto setMode(CipherMode mode) {
        this.lock.lock();
        try {
            try {
                initMode(mode.getValue());
                this.lock.unlock();
                return this;
            } catch (Exception e) {
                throw new CryptoException(e);
            }
        } catch (Throwable th) {
            this.lock.unlock();
            throw th;
        }
    }

    public byte[] update(byte[] data) {
        Cipher cipher = this.cipherWrapper.getCipher();
        this.lock.lock();
        try {
            try {
                byte[] update = cipher.update(paddingDataWithZero(data, cipher.getBlockSize()));
                this.lock.unlock();
                return update;
            } catch (Exception e) {
                throw new CryptoException(e);
            }
        } catch (Throwable th) {
            this.lock.unlock();
            throw th;
        }
    }

    public String updateHex(byte[] data) {
        return HexUtil.encodeHexStr(update(data));
    }

    @Override // cn.hutool.crypto.symmetric.SymmetricEncryptor
    public byte[] encrypt(byte[] data) {
        this.lock.lock();
        try {
            try {
                Cipher cipher = initMode(1);
                byte[] doFinal = cipher.doFinal(paddingDataWithZero(data, cipher.getBlockSize()));
                this.lock.unlock();
                return doFinal;
            } catch (Exception e) {
                throw new CryptoException(e);
            }
        } catch (Throwable th) {
            this.lock.unlock();
            throw th;
        }
    }

    @Override // cn.hutool.crypto.symmetric.SymmetricEncryptor
    public void encrypt(InputStream data, OutputStream out, boolean isClose) throws IORuntimeException {
        int blockSize;
        int remainLength;
        this.lock.lock();
        CipherOutputStream cipherOutputStream = null;
        try {
            try {
                try {
                    Cipher cipher = initMode(1);
                    cipherOutputStream = new CipherOutputStream(out, cipher);
                    long length = IoUtil.copy(data, cipherOutputStream);
                    if (this.isZeroPadding && (blockSize = cipher.getBlockSize()) > 0 && (remainLength = (int) (length % blockSize)) > 0) {
                        cipherOutputStream.write(new byte[blockSize - remainLength]);
                        cipherOutputStream.flush();
                    }
                    this.lock.unlock();
                    IoUtil.close((Closeable) cipherOutputStream);
                    if (isClose) {
                        IoUtil.close((Closeable) data);
                    }
                } catch (Exception e) {
                    throw new CryptoException(e);
                }
            } catch (IORuntimeException e2) {
                throw e2;
            }
        } catch (Throwable th) {
            this.lock.unlock();
            IoUtil.close((Closeable) cipherOutputStream);
            if (isClose) {
                IoUtil.close((Closeable) data);
            }
            throw th;
        }
    }

    @Override // cn.hutool.crypto.symmetric.SymmetricDecryptor
    public byte[] decrypt(byte[] bytes) {
        this.lock.lock();
        try {
            try {
                Cipher cipher = initMode(2);
                int blockSize = cipher.getBlockSize();
                byte[] decryptData = cipher.doFinal(bytes);
                this.lock.unlock();
                return removePadding(decryptData, blockSize);
            } catch (Exception e) {
                throw new CryptoException(e);
            }
        } catch (Throwable th) {
            this.lock.unlock();
            throw th;
        }
    }

    @Override // cn.hutool.crypto.symmetric.SymmetricDecryptor
    public void decrypt(InputStream data, OutputStream out, boolean isClose) throws IORuntimeException {
        int blockSize;
        this.lock.lock();
        try {
            try {
                try {
                    Cipher cipher = initMode(2);
                    CipherInputStream cipherInputStream = new CipherInputStream(data, cipher);
                    if (this.isZeroPadding && (blockSize = cipher.getBlockSize()) > 0) {
                        copyForZeroPadding(cipherInputStream, out, blockSize);
                        this.lock.unlock();
                        IoUtil.close((Closeable) cipherInputStream);
                        if (!isClose) {
                            return;
                        }
                        IoUtil.close((Closeable) data);
                        return;
                    }
                    IoUtil.copy(cipherInputStream, out);
                    this.lock.unlock();
                    IoUtil.close((Closeable) cipherInputStream);
                    if (isClose) {
                        IoUtil.close((Closeable) data);
                    }
                } catch (IOException e) {
                    throw new IORuntimeException(e);
                } catch (Exception e2) {
                    throw new CryptoException(e2);
                }
            } catch (IORuntimeException e3) {
                throw e3;
            }
        } catch (Throwable th) {
            this.lock.unlock();
            IoUtil.close((Closeable) null);
            if (isClose) {
                IoUtil.close((Closeable) data);
            }
            throw th;
        }
    }

    private SymmetricCrypto initParams(String algorithm, AlgorithmParameterSpec paramsSpec) {
        if (null == paramsSpec) {
            byte[] iv = (byte[]) Opt.ofNullable(this.cipherWrapper).map((v0) -> {
                return v0.getCipher();
            }).map((v0) -> {
                return v0.getIV();
            }).get();
            if (StrUtil.startWithIgnoreCase(algorithm, "PBE")) {
                if (null == iv) {
                    iv = RandomUtil.randomBytes(8);
                }
                paramsSpec = new PBEParameterSpec(iv, 100);
            } else if (StrUtil.startWithIgnoreCase(algorithm, "AES") && null != iv) {
                paramsSpec = new IvParameterSpec(iv);
            }
        }
        return setParams(paramsSpec);
    }

    private Cipher initMode(int mode) throws InvalidKeyException, InvalidAlgorithmParameterException {
        return this.cipherWrapper.initMode(mode, this.secretKey).getCipher();
    }

    private byte[] paddingDataWithZero(byte[] data, int blockSize) {
        int length;
        int remainLength;
        if (this.isZeroPadding && (remainLength = (length = data.length) % blockSize) > 0) {
            return ArrayUtil.resize(data, (length + blockSize) - remainLength);
        }
        return data;
    }

    private byte[] removePadding(byte[] data, int blockSize) {
        if (this.isZeroPadding && blockSize > 0) {
            int length = data.length;
            int remainLength = length % blockSize;
            if (remainLength == 0) {
                int i = length - 1;
                while (i >= 0 && 0 == data[i]) {
                    i--;
                }
                return ArrayUtil.resize(data, i + 1);
            }
        }
        return data;
    }

    private static void copyForZeroPadding(CipherInputStream in, OutputStream out, int blockSize) throws IOException {
        int preReadSize;
        int n = 1;
        if (8192 > blockSize) {
            n = Math.max(1, 8192 / blockSize);
        }
        int bufSize = blockSize * n;
        byte[] preBuffer = new byte[bufSize];
        byte[] buffer = new byte[bufSize];
        boolean isFirst = true;
        int i = 0;
        while (true) {
            preReadSize = i;
            int readSize = in.read(buffer);
            if (readSize == -1) {
                break;
            }
            if (isFirst) {
                isFirst = false;
            } else {
                out.write(preBuffer, 0, preReadSize);
            }
            ArrayUtil.copy(buffer, preBuffer, readSize);
            i = readSize;
        }
        int i2 = preReadSize - 1;
        while (i2 >= 0 && 0 == preBuffer[i2]) {
            i2--;
        }
        out.write(preBuffer, 0, i2 + 1);
        out.flush();
    }
}
