package cn.hutool.crypto.asymmetric;

import cn.hutool.core.codec.BCD;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import java.io.InputStream;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/asymmetric/AsymmetricEncryptor.class */
public interface AsymmetricEncryptor {
    byte[] encrypt(byte[] bArr, KeyType keyType);

    default String encryptHex(byte[] data, KeyType keyType) {
        return HexUtil.encodeHexStr(encrypt(data, keyType));
    }

    default String encryptBase64(byte[] data, KeyType keyType) {
        return Base64.encode(encrypt(data, keyType));
    }

    default byte[] encrypt(String data, String charset, KeyType keyType) {
        return encrypt(StrUtil.bytes(data, charset), keyType);
    }

    default byte[] encrypt(String data, Charset charset, KeyType keyType) {
        return encrypt(StrUtil.bytes(data, charset), keyType);
    }

    default byte[] encrypt(String data, KeyType keyType) {
        return encrypt(StrUtil.utf8Bytes(data), keyType);
    }

    default String encryptHex(String data, KeyType keyType) {
        return HexUtil.encodeHexStr(encrypt(data, keyType));
    }

    default String encryptHex(String data, Charset charset, KeyType keyType) {
        return HexUtil.encodeHexStr(encrypt(data, charset, keyType));
    }

    default String encryptBase64(String data, KeyType keyType) {
        return Base64.encode(encrypt(data, keyType));
    }

    default String encryptBase64(String data, Charset charset, KeyType keyType) {
        return Base64.encode(encrypt(data, charset, keyType));
    }

    default byte[] encrypt(InputStream data, KeyType keyType) throws IORuntimeException {
        return encrypt(IoUtil.readBytes(data), keyType);
    }

    default String encryptHex(InputStream data, KeyType keyType) {
        return HexUtil.encodeHexStr(encrypt(data, keyType));
    }

    default String encryptBase64(InputStream data, KeyType keyType) {
        return Base64.encode(encrypt(data, keyType));
    }

    @Deprecated
    default String encryptBcd(String data, KeyType keyType) {
        return encryptBcd(data, keyType, CharsetUtil.CHARSET_UTF_8);
    }

    @Deprecated
    default String encryptBcd(String data, KeyType keyType, Charset charset) {
        return BCD.bcdToStr(encrypt(data, charset, keyType));
    }
}
