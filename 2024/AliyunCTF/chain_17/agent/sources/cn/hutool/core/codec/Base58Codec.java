package cn.hutool.core.codec;

import cn.hutool.core.util.StrUtil;
import java.util.Arrays;
import org.apache.tomcat.util.bcel.Const;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/codec/Base58Codec.class */
public class Base58Codec implements Encoder<byte[], String>, Decoder<CharSequence, byte[]> {
    public static Base58Codec INSTANCE = new Base58Codec();

    @Override // cn.hutool.core.codec.Encoder
    public String encode(byte[] data) {
        return Base58Encoder.ENCODER.encode(data);
    }

    @Override // cn.hutool.core.codec.Decoder
    public byte[] decode(CharSequence encoded) throws IllegalArgumentException {
        return Base58Decoder.DECODER.decode(encoded);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/codec/Base58Codec$Base58Encoder.class */
    public static class Base58Encoder implements Encoder<byte[], String> {
        private static final String DEFAULT_ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        public static final Base58Encoder ENCODER = new Base58Encoder(DEFAULT_ALPHABET.toCharArray());
        private final char[] alphabet;
        private final char alphabetZero;

        public Base58Encoder(char[] alphabet) {
            this.alphabet = alphabet;
            this.alphabetZero = alphabet[0];
        }

        @Override // cn.hutool.core.codec.Encoder
        public String encode(byte[] data) {
            if (null == data) {
                return null;
            }
            if (data.length == 0) {
                return "";
            }
            int zeroCount = 0;
            while (zeroCount < data.length && data[zeroCount] == 0) {
                zeroCount++;
            }
            byte[] data2 = Arrays.copyOf(data, data.length);
            char[] encoded = new char[data2.length * 2];
            int outputStart = encoded.length;
            int inputStart = zeroCount;
            while (inputStart < data2.length) {
                outputStart--;
                encoded[outputStart] = this.alphabet[Base58Codec.divmod(data2, inputStart, 256, 58)];
                if (data2[inputStart] == 0) {
                    inputStart++;
                }
            }
            while (outputStart < encoded.length && encoded[outputStart] == this.alphabetZero) {
                outputStart++;
            }
            while (true) {
                zeroCount--;
                if (zeroCount >= 0) {
                    outputStart--;
                    encoded[outputStart] = this.alphabetZero;
                } else {
                    return new String(encoded, outputStart, encoded.length - outputStart);
                }
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/codec/Base58Codec$Base58Decoder.class */
    public static class Base58Decoder implements Decoder<CharSequence, byte[]> {
        public static Base58Decoder DECODER = new Base58Decoder("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz");
        private final byte[] lookupTable;

        public Base58Decoder(String alphabet) {
            byte[] lookupTable = new byte[123];
            Arrays.fill(lookupTable, (byte) -1);
            int length = alphabet.length();
            for (int i = 0; i < length; i++) {
                lookupTable[alphabet.charAt(i)] = (byte) i;
            }
            this.lookupTable = lookupTable;
        }

        @Override // cn.hutool.core.codec.Decoder
        public byte[] decode(CharSequence encoded) {
            if (encoded.length() == 0) {
                return new byte[0];
            }
            byte[] input58 = new byte[encoded.length()];
            for (int i = 0; i < encoded.length(); i++) {
                char c = encoded.charAt(i);
                int digit = c < 128 ? this.lookupTable[c] : (byte) -1;
                if (digit < 0) {
                    throw new IllegalArgumentException(StrUtil.format("Invalid char '{}' at [{}]", Character.valueOf(c), Integer.valueOf(i)));
                }
                input58[i] = (byte) digit;
            }
            int zeros = 0;
            while (zeros < input58.length && input58[zeros] == 0) {
                zeros++;
            }
            byte[] decoded = new byte[encoded.length()];
            int outputStart = decoded.length;
            int inputStart = zeros;
            while (inputStart < input58.length) {
                outputStart--;
                decoded[outputStart] = Base58Codec.divmod(input58, inputStart, 58, 256);
                if (input58[inputStart] == 0) {
                    inputStart++;
                }
            }
            while (outputStart < decoded.length && decoded[outputStart] == 0) {
                outputStart++;
            }
            return Arrays.copyOfRange(decoded, outputStart - zeros, decoded.length);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte divmod(byte[] number, int firstDigit, int base, int divisor) {
        int remainder = 0;
        for (int i = firstDigit; i < number.length; i++) {
            int digit = number[i] & Const.MAX_ARRAY_DIMENSIONS;
            int temp = (remainder * base) + digit;
            number[i] = (byte) (temp / divisor);
            remainder = temp % divisor;
        }
        return (byte) remainder;
    }
}
