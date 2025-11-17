package cn.hutool.core.codec;

import java.util.Arrays;
import org.apache.tomcat.util.bcel.Const;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/codec/Base32Codec.class */
public class Base32Codec implements Encoder<byte[], String>, Decoder<CharSequence, byte[]> {
    public static Base32Codec INSTANCE = new Base32Codec();

    @Override // cn.hutool.core.codec.Encoder
    public String encode(byte[] data) {
        return encode(data, false);
    }

    public String encode(byte[] data, boolean useHex) {
        Base32Encoder encoder = useHex ? Base32Encoder.HEX_ENCODER : Base32Encoder.ENCODER;
        return encoder.encode(data);
    }

    @Override // cn.hutool.core.codec.Decoder
    public byte[] decode(CharSequence encoded) {
        return decode(encoded, false);
    }

    public byte[] decode(CharSequence encoded, boolean useHex) {
        Base32Decoder decoder = useHex ? Base32Decoder.HEX_DECODER : Base32Decoder.DECODER;
        return decoder.decode(encoded);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/codec/Base32Codec$Base32Encoder.class */
    public static class Base32Encoder implements Encoder<byte[], String> {
        private final char[] alphabet;
        private final Character pad;
        private static final Character DEFAULT_PAD = '=';
        private static final int[] BASE32_FILL = {-1, 4, 1, 6, 3};
        private static final String DEFAULT_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        public static final Base32Encoder ENCODER = new Base32Encoder(DEFAULT_ALPHABET, DEFAULT_PAD);
        private static final String HEX_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUV";
        public static final Base32Encoder HEX_ENCODER = new Base32Encoder(HEX_ALPHABET, DEFAULT_PAD);

        public Base32Encoder(String alphabet, Character pad) {
            this.alphabet = alphabet.toCharArray();
            this.pad = pad;
        }

        @Override // cn.hutool.core.codec.Encoder
        public String encode(byte[] bArr) {
            int i;
            int i2;
            int i3 = 0;
            int i4 = 0;
            int length = (bArr.length * 8) / 5;
            if (length != 0) {
                length = length + 1 + BASE32_FILL[(bArr.length * 8) % 5];
            }
            StringBuilder sb = new StringBuilder(length);
            while (i3 < bArr.length) {
                int i5 = bArr[i3] >= 0 ? bArr[i3] : (bArr[i3] == true ? 1 : 0) + 256;
                if (i4 > 3) {
                    if (i3 + 1 < bArr.length) {
                        i2 = bArr[i3 + 1] >= 0 ? bArr[i3 + 1] : (bArr[i3 + 1] == true ? 1 : 0) + 256;
                    } else {
                        i2 = 0;
                    }
                    int i6 = i5 & (Const.MAX_ARRAY_DIMENSIONS >> i4);
                    i4 = (i4 + 5) % 8;
                    i = (i6 << i4) | (i2 >> (8 - i4));
                    i3++;
                } else {
                    i = (i5 >> (8 - (i4 + 5))) & 31;
                    i4 = (i4 + 5) % 8;
                    if (i4 == 0) {
                        i3++;
                    }
                }
                sb.append(this.alphabet[i]);
            }
            if (null != this.pad) {
                while (sb.length() < length) {
                    sb.append(this.pad.charValue());
                }
            }
            return sb.toString();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/codec/Base32Codec$Base32Decoder.class */
    public static class Base32Decoder implements Decoder<CharSequence, byte[]> {
        private static final char BASE_CHAR = '0';
        public static final Base32Decoder DECODER = new Base32Decoder("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567");
        public static final Base32Decoder HEX_DECODER = new Base32Decoder("0123456789ABCDEFGHIJKLMNOPQRSTUV");
        private final byte[] lookupTable = new byte[128];

        public Base32Decoder(String alphabet) {
            Arrays.fill(this.lookupTable, (byte) -1);
            int length = alphabet.length();
            for (int i = 0; i < length; i++) {
                char c = alphabet.charAt(i);
                this.lookupTable[c - '0'] = (byte) i;
                if (c >= 'A' && c <= 'Z') {
                    this.lookupTable[Character.toLowerCase(c) - '0'] = (byte) i;
                }
            }
        }

        @Override // cn.hutool.core.codec.Decoder
        public byte[] decode(CharSequence encoded) {
            byte b;
            String base32 = encoded.toString();
            int len = base32.endsWith("=") ? (base32.indexOf("=") * 5) / 8 : (base32.length() * 5) / 8;
            byte[] bytes = new byte[len];
            int index = 0;
            int offset = 0;
            for (int i = 0; i < base32.length(); i++) {
                int lookup = base32.charAt(i) - '0';
                if (lookup >= 0 && lookup < this.lookupTable.length && (b = this.lookupTable[lookup]) >= 0) {
                    if (index <= 3) {
                        index = (index + 5) % 8;
                        if (index == 0) {
                            int i2 = offset;
                            bytes[i2] = (byte) (bytes[i2] | b);
                            offset++;
                            if (offset >= bytes.length) {
                                break;
                            }
                        } else {
                            int i3 = offset;
                            bytes[i3] = (byte) (bytes[i3] | (b << (8 - index)));
                        }
                    } else {
                        index = (index + 5) % 8;
                        int i4 = offset;
                        bytes[i4] = (byte) (bytes[i4] | (b >>> index));
                        offset++;
                        if (offset >= bytes.length) {
                            break;
                        }
                        bytes[offset] = (byte) (bytes[offset] | (b << (8 - index)));
                    }
                }
            }
            return bytes;
        }
    }
}
