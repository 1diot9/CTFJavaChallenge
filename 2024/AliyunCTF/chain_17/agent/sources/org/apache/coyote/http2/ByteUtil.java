package org.apache.coyote.http2;

import java.nio.ByteBuffer;
import org.apache.tomcat.util.bcel.Const;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/ByteUtil.class */
class ByteUtil {
    private ByteUtil() {
    }

    static boolean isBit7Set(byte input) {
        return (input & 128) != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int get31Bits(byte[] input, int firstByte) {
        return ((input[firstByte] & Byte.MAX_VALUE) << 24) + ((input[firstByte + 1] & 255) << 16) + ((input[firstByte + 2] & 255) << 8) + (input[firstByte + 3] & 255);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int get31Bits(ByteBuffer input, int firstByte) {
        return ((input.get(firstByte) & Byte.MAX_VALUE) << 24) + ((input.get(firstByte + 1) & 255) << 16) + ((input.get(firstByte + 2) & 255) << 8) + (input.get(firstByte + 3) & 255);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void set31Bits(byte[] output, int firstByte, int value) {
        output[firstByte] = (byte) ((value & 2130706432) >> 24);
        output[firstByte + 1] = (byte) ((value & 16711680) >> 16);
        output[firstByte + 2] = (byte) ((value & 65280) >> 8);
        output[firstByte + 3] = (byte) (value & Const.MAX_ARRAY_DIMENSIONS);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getOneByte(byte[] input, int pos) {
        return input[pos] & 255;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getOneByte(ByteBuffer input, int pos) {
        return input.get(pos) & 255;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getTwoBytes(byte[] input, int firstByte) {
        return ((input[firstByte] & 255) << 8) + (input[firstByte + 1] & 255);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getThreeBytes(byte[] input, int firstByte) {
        return ((input[firstByte] & 255) << 16) + ((input[firstByte + 1] & 255) << 8) + (input[firstByte + 2] & 255);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getThreeBytes(ByteBuffer input, int firstByte) {
        return ((input.get(firstByte) & 255) << 16) + ((input.get(firstByte + 1) & 255) << 8) + (input.get(firstByte + 2) & 255);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setTwoBytes(byte[] output, int firstByte, int value) {
        output[firstByte] = (byte) ((value & 65280) >> 8);
        output[firstByte + 1] = (byte) (value & Const.MAX_ARRAY_DIMENSIONS);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setThreeBytes(byte[] output, int firstByte, int value) {
        output[firstByte] = (byte) ((value & 16711680) >> 16);
        output[firstByte + 1] = (byte) ((value & 65280) >> 8);
        output[firstByte + 2] = (byte) (value & Const.MAX_ARRAY_DIMENSIONS);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long getFourBytes(byte[] input, int firstByte) {
        return ((input[firstByte] & 255) << 24) + ((input[firstByte + 1] & 255) << 16) + ((input[firstByte + 2] & 255) << 8) + (input[firstByte + 3] & 255);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setFourBytes(byte[] output, int firstByte, long value) {
        output[firstByte] = (byte) ((value & (-16777216)) >> 24);
        output[firstByte + 1] = (byte) ((value & 16711680) >> 16);
        output[firstByte + 2] = (byte) ((value & 65280) >> 8);
        output[firstByte + 3] = (byte) (value & 255);
    }
}
