package cn.hutool.crypto.digest;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/digest/MD5.class */
public class MD5 extends Digester {
    private static final long serialVersionUID = 1;

    public static MD5 create() {
        return new MD5();
    }

    public MD5() {
        super(DigestAlgorithm.MD5);
    }

    public MD5(byte[] salt) {
        this(salt, 0, 1);
    }

    public MD5(byte[] salt, int digestCount) {
        this(salt, 0, digestCount);
    }

    public MD5(byte[] salt, int saltPosition, int digestCount) {
        this();
        this.salt = salt;
        this.saltPosition = saltPosition;
        this.digestCount = digestCount;
    }

    public String digestHex16(String data, Charset charset) {
        return DigestUtil.md5HexTo16(digestHex(data, charset));
    }

    public String digestHex16(String data) {
        return DigestUtil.md5HexTo16(digestHex(data));
    }

    public String digestHex16(InputStream data) {
        return DigestUtil.md5HexTo16(digestHex(data));
    }

    public String digestHex16(File data) {
        return DigestUtil.md5HexTo16(digestHex(data));
    }

    public String digestHex16(byte[] data) {
        return DigestUtil.md5HexTo16(digestHex(data));
    }
}
