package cn.hutool.core.io;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/CharsetDetector.class */
public class CharsetDetector {
    private static final Charset[] DEFAULT_CHARSETS;

    static {
        String[] names = {CharsetUtil.UTF_8, CharsetUtil.GBK, "GB2312", "GB18030", "UTF-16BE", "UTF-16LE", "UTF-16", "BIG5", "UNICODE", "US-ASCII"};
        DEFAULT_CHARSETS = (Charset[]) Convert.convert(Charset[].class, (Object) names);
    }

    public static Charset detect(File file, Charset... charsets) {
        return detect(FileUtil.getInputStream(file), charsets);
    }

    public static Charset detect(InputStream in, Charset... charsets) {
        return detect(32768, in, charsets);
    }

    public static Charset detect(int bufferSize, InputStream in, Charset... charsets) {
        if (ArrayUtil.isEmpty((Object[]) charsets)) {
            charsets = DEFAULT_CHARSETS;
        }
        byte[] buffer = new byte[bufferSize];
        while (in.read(buffer) > -1) {
            try {
                try {
                    for (Charset charset : charsets) {
                        CharsetDecoder decoder = charset.newDecoder();
                        if (identify(buffer, decoder)) {
                            return charset;
                        }
                    }
                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
            } finally {
                IoUtil.close((Closeable) in);
            }
        }
        IoUtil.close((Closeable) in);
        return null;
    }

    private static boolean identify(byte[] bytes, CharsetDecoder decoder) {
        try {
            decoder.decode(ByteBuffer.wrap(bytes));
            return true;
        } catch (CharacterCodingException e) {
            return false;
        }
    }
}
