package cn.hutool.crypto.symmetric;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.KeyUtil;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import org.apache.catalina.realm.SecretKeyCredentialHandler;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/symmetric/PBKDF2.class */
public class PBKDF2 {
    private String algorithm;
    private int keyLength;
    private int iterationCount;

    public PBKDF2() {
        this.algorithm = SecretKeyCredentialHandler.DEFAULT_ALGORITHM;
        this.keyLength = 512;
        this.iterationCount = 1000;
    }

    public PBKDF2(String algorithm, int keyLength, int iterationCount) {
        this.algorithm = SecretKeyCredentialHandler.DEFAULT_ALGORITHM;
        this.keyLength = 512;
        this.iterationCount = 1000;
        this.algorithm = algorithm;
        this.keyLength = keyLength;
        this.iterationCount = iterationCount;
    }

    public byte[] encrypt(char[] password, byte[] salt) {
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password, salt, this.iterationCount, this.keyLength);
        SecretKey secretKey = KeyUtil.generateKey(this.algorithm, pbeKeySpec);
        return secretKey.getEncoded();
    }

    public String encryptHex(char[] password, byte[] salt) {
        return HexUtil.encodeHexStr(encrypt(password, salt));
    }
}
