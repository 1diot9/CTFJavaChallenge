package cn.hutool.crypto.digest.mac;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.CryptoException;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.security.MessageDigest;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/digest/mac/Mac.class */
public class Mac implements Serializable {
    private static final long serialVersionUID = 1;
    private final MacEngine engine;

    public Mac(MacEngine engine) {
        this.engine = engine;
    }

    public MacEngine getEngine() {
        return this.engine;
    }

    public byte[] digest(String data, Charset charset) {
        return digest(StrUtil.bytes(data, charset));
    }

    public byte[] digest(String data) {
        return digest(data, CharsetUtil.CHARSET_UTF_8);
    }

    public String digestBase64(String data, boolean isUrlSafe) {
        return digestBase64(data, CharsetUtil.CHARSET_UTF_8, isUrlSafe);
    }

    public String digestBase64(String data, Charset charset, boolean isUrlSafe) {
        byte[] digest = digest(data, charset);
        return isUrlSafe ? Base64.encodeUrlSafe(digest) : Base64.encode(digest);
    }

    public String digestHex(String data, Charset charset) {
        return HexUtil.encodeHexStr(digest(data, charset));
    }

    public String digestHex(String data) {
        return digestHex(data, CharsetUtil.CHARSET_UTF_8);
    }

    public byte[] digest(File file) throws CryptoException {
        InputStream in = null;
        try {
            in = FileUtil.getInputStream(file);
            byte[] digest = digest(in);
            IoUtil.close((Closeable) in);
            return digest;
        } catch (Throwable th) {
            IoUtil.close((Closeable) in);
            throw th;
        }
    }

    public String digestHex(File file) {
        return HexUtil.encodeHexStr(digest(file));
    }

    public byte[] digest(byte[] data) {
        return digest(new ByteArrayInputStream(data), -1);
    }

    public String digestHex(byte[] data) {
        return HexUtil.encodeHexStr(digest(data));
    }

    public byte[] digest(InputStream data) {
        return digest(data, 8192);
    }

    public String digestHex(InputStream data) {
        return HexUtil.encodeHexStr(digest(data));
    }

    public byte[] digest(InputStream data, int bufferLength) {
        return this.engine.digest(data, bufferLength);
    }

    public String digestHex(InputStream data, int bufferLength) {
        return HexUtil.encodeHexStr(digest(data, bufferLength));
    }

    public boolean verify(byte[] digest, byte[] digestToCompare) {
        return MessageDigest.isEqual(digest, digestToCompare);
    }

    public int getMacLength() {
        return this.engine.getMacLength();
    }

    public String getAlgorithm() {
        return this.engine.getAlgorithm();
    }
}
