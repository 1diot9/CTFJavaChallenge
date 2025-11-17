package cn.hutool.core.codec;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.StrUtil;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/codec/Base16Codec.class */
public class Base16Codec implements Encoder<byte[], char[]>, Decoder<CharSequence, byte[]> {
    public static final Base16Codec CODEC_LOWER = new Base16Codec(true);
    public static final Base16Codec CODEC_UPPER = new Base16Codec(false);
    private final char[] alphabets;

    public Base16Codec(boolean lowerCase) {
        this.alphabets = (lowerCase ? "0123456789abcdef" : "0123456789ABCDEF").toCharArray();
    }

    @Override // cn.hutool.core.codec.Encoder
    public char[] encode(byte[] data) {
        int len = data.length;
        char[] out = new char[len << 1];
        int j = 0;
        for (int i = 0; i < len; i++) {
            int i2 = j;
            int j2 = j + 1;
            out[i2] = this.alphabets[(240 & data[i]) >>> 4];
            j = j2 + 1;
            out[j2] = this.alphabets[15 & data[i]];
        }
        return out;
    }

    @Override // cn.hutool.core.codec.Decoder
    public byte[] decode(CharSequence encoded) {
        if (StrUtil.isEmpty(encoded)) {
            return null;
        }
        CharSequence encoded2 = StrUtil.cleanBlank(encoded);
        int len = encoded2.length();
        if ((len & 1) != 0) {
            encoded2 = CustomBooleanEditor.VALUE_0 + ((Object) encoded2);
            len = encoded2.length();
        }
        byte[] out = new byte[len >> 1];
        int i = 0;
        int j = 0;
        while (j < len) {
            int f = toDigit(encoded2.charAt(j), j) << 4;
            int j2 = j + 1;
            int f2 = f | toDigit(encoded2.charAt(j2), j2);
            j = j2 + 1;
            out[i] = (byte) (f2 & Const.MAX_ARRAY_DIMENSIONS);
            i++;
        }
        return out;
    }

    public String toUnicodeHex(char ch2) {
        return "\\u" + this.alphabets[(ch2 >> '\f') & 15] + this.alphabets[(ch2 >> '\b') & 15] + this.alphabets[(ch2 >> 4) & 15] + this.alphabets[ch2 & 15];
    }

    public void appendHex(StringBuilder builder, byte b) {
        int high = (b & 240) >>> 4;
        int low = b & 15;
        builder.append(this.alphabets[high]);
        builder.append(this.alphabets[low]);
    }

    private static int toDigit(char ch2, int index) {
        int digit = Character.digit(ch2, 16);
        if (digit < 0) {
            throw new UtilException("Illegal hexadecimal character {} at index {}", Character.valueOf(ch2), Integer.valueOf(index));
        }
        return digit;
    }
}
