package cn.hutool.crypto.symmetric;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/symmetric/SymmetricDecryptor.class */
public interface SymmetricDecryptor {
    byte[] decrypt(byte[] bArr);

    void decrypt(InputStream inputStream, OutputStream outputStream, boolean z);

    default String decryptStr(byte[] bytes, Charset charset) {
        return StrUtil.str(decrypt(bytes), charset);
    }

    default String decryptStr(byte[] bytes) {
        return decryptStr(bytes, CharsetUtil.CHARSET_UTF_8);
    }

    default byte[] decrypt(String data) {
        return decrypt(SecureUtil.decode(data));
    }

    default String decryptStr(String data, Charset charset) {
        return StrUtil.str(decrypt(data), charset);
    }

    default String decryptStr(String data) {
        return decryptStr(data, CharsetUtil.CHARSET_UTF_8);
    }

    default byte[] decrypt(InputStream data) throws IORuntimeException {
        return decrypt(IoUtil.readBytes(data));
    }

    default String decryptStr(InputStream data, Charset charset) {
        return StrUtil.str(decrypt(data), charset);
    }

    default String decryptStr(InputStream data) {
        return decryptStr(data, CharsetUtil.CHARSET_UTF_8);
    }
}
