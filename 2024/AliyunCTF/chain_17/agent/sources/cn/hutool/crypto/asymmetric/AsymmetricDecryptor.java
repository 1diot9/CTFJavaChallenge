package cn.hutool.crypto.asymmetric;

import cn.hutool.core.codec.BCD;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import java.io.InputStream;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/asymmetric/AsymmetricDecryptor.class */
public interface AsymmetricDecryptor {
    byte[] decrypt(byte[] bArr, KeyType keyType);

    default byte[] decrypt(InputStream data, KeyType keyType) throws IORuntimeException {
        return decrypt(IoUtil.readBytes(data), keyType);
    }

    default byte[] decrypt(String data, KeyType keyType) {
        return decrypt(SecureUtil.decode(data), keyType);
    }

    default String decryptStr(String data, KeyType keyType, Charset charset) {
        return StrUtil.str(decrypt(data, keyType), charset);
    }

    default String decryptStr(String data, KeyType keyType) {
        return decryptStr(data, keyType, CharsetUtil.CHARSET_UTF_8);
    }

    default byte[] decryptFromBcd(String data, KeyType keyType) {
        return decryptFromBcd(data, keyType, CharsetUtil.CHARSET_UTF_8);
    }

    @Deprecated
    default byte[] decryptFromBcd(String data, KeyType keyType, Charset charset) {
        Assert.notNull(data, "Bcd string must be not null!", new Object[0]);
        byte[] dataBytes = BCD.ascToBcd(StrUtil.bytes(data, charset));
        return decrypt(dataBytes, keyType);
    }

    @Deprecated
    default String decryptStrFromBcd(String data, KeyType keyType, Charset charset) {
        return StrUtil.str(decryptFromBcd(data, keyType, charset), charset);
    }

    @Deprecated
    default String decryptStrFromBcd(String data, KeyType keyType) {
        return decryptStrFromBcd(data, keyType, CharsetUtil.CHARSET_UTF_8);
    }
}
