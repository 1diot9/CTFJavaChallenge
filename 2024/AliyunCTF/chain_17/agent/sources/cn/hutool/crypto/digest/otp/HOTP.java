package cn.hutool.crypto.digest.otp;

import cn.hutool.core.codec.Base32;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/digest/otp/HOTP.class */
public class HOTP {
    public static final int DEFAULT_PASSWORD_LENGTH = 6;
    private final HMac mac;
    private final int passwordLength;
    private final int modDivisor;
    private final byte[] buffer;
    private static final int[] MOD_DIVISORS = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};
    public static final HmacAlgorithm HOTP_HMAC_ALGORITHM = HmacAlgorithm.HmacSHA1;

    public HOTP(byte[] key) {
        this(6, key);
    }

    public HOTP(int passwordLength, byte[] key) {
        this(passwordLength, HOTP_HMAC_ALGORITHM, key);
    }

    public HOTP(int passwordLength, HmacAlgorithm algorithm, byte[] key) {
        if (passwordLength >= MOD_DIVISORS.length) {
            throw new IllegalArgumentException("Password length must be < " + MOD_DIVISORS.length);
        }
        this.mac = new HMac(algorithm, key);
        this.modDivisor = MOD_DIVISORS[passwordLength];
        this.passwordLength = passwordLength;
        this.buffer = new byte[8];
    }

    public synchronized int generate(long counter) {
        this.buffer[0] = (byte) ((counter & (-72057594037927936L)) >>> 56);
        this.buffer[1] = (byte) ((counter & 71776119061217280L) >>> 48);
        this.buffer[2] = (byte) ((counter & 280375465082880L) >>> 40);
        this.buffer[3] = (byte) ((counter & 1095216660480L) >>> 32);
        this.buffer[4] = (byte) ((counter & 4278190080L) >>> 24);
        this.buffer[5] = (byte) ((counter & 16711680) >>> 16);
        this.buffer[6] = (byte) ((counter & 65280) >>> 8);
        this.buffer[7] = (byte) (counter & 255);
        byte[] digest = this.mac.digest(this.buffer);
        return truncate(digest);
    }

    public static String generateSecretKey(int numBytes) {
        return Base32.encode(RandomUtil.getSHA1PRNGRandom(RandomUtil.randomBytes(256)).generateSeed(numBytes));
    }

    public int getPasswordLength() {
        return this.passwordLength;
    }

    public String getAlgorithm() {
        return this.mac.getAlgorithm();
    }

    private int truncate(byte[] digest) {
        int offset = digest[digest.length - 1] & 15;
        return (((((digest[offset] & Byte.MAX_VALUE) << 24) | ((digest[offset + 1] & 255) << 16)) | ((digest[offset + 2] & 255) << 8)) | (digest[offset + 3] & 255)) % this.modDivisor;
    }
}
