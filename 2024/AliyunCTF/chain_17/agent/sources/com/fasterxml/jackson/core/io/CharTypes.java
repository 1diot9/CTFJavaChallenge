package com.fasterxml.jackson.core.io;

import com.alibaba.com.caucho.hessian.io.Hessian2Constants;
import java.util.Arrays;
import org.apache.tomcat.util.bcel.Const;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/CharTypes.class */
public final class CharTypes {
    protected static final char[] HC = "0123456789ABCDEF".toCharArray();
    protected static final char[] HClower = "0123456789abcdef".toCharArray();
    protected static final byte[] HB;
    protected static final byte[] HBlower;
    protected static final int[] sInputCodes;
    protected static final int[] sInputCodesUTF8;
    protected static final int[] sInputCodesJsNames;
    protected static final int[] sInputCodesUtf8JsNames;
    protected static final int[] sInputCodesComment;
    protected static final int[] sInputCodesWS;
    protected static final int[] sOutputEscapes128;
    protected static final int[] sHexValues;

    static {
        int i;
        int len = HC.length;
        HB = new byte[len];
        HBlower = new byte[len];
        for (int i2 = 0; i2 < len; i2++) {
            HB[i2] = (byte) HC[i2];
            HBlower[i2] = (byte) HClower[i2];
        }
        int[] table = new int[256];
        for (int i3 = 0; i3 < 32; i3++) {
            table[i3] = -1;
        }
        table[34] = 1;
        table[92] = 1;
        sInputCodes = table;
        int[] table2 = new int[sInputCodes.length];
        System.arraycopy(sInputCodes, 0, table2, 0, table2.length);
        for (int c = 128; c < 256; c++) {
            if ((c & 224) == 192) {
                i = 2;
            } else if ((c & 240) == 224) {
                i = 3;
            } else if ((c & Hessian2Constants.BC_LONG_BYTE_ZERO) == 240) {
                i = 4;
            } else {
                i = -1;
            }
            int code = i;
            table2[c] = code;
        }
        sInputCodesUTF8 = table2;
        int[] table3 = new int[256];
        Arrays.fill(table3, -1);
        for (int i4 = 33; i4 < 256; i4++) {
            if (Character.isJavaIdentifierPart((char) i4)) {
                table3[i4] = 0;
            }
        }
        table3[64] = 0;
        table3[35] = 0;
        table3[42] = 0;
        table3[45] = 0;
        table3[43] = 0;
        sInputCodesJsNames = table3;
        int[] table4 = new int[256];
        System.arraycopy(sInputCodesJsNames, 0, table4, 0, table4.length);
        Arrays.fill(table4, 128, 128, 0);
        sInputCodesUtf8JsNames = table4;
        int[] buf = new int[256];
        System.arraycopy(sInputCodesUTF8, 128, buf, 128, 128);
        Arrays.fill(buf, 0, 32, -1);
        buf[9] = 0;
        buf[10] = 10;
        buf[13] = 13;
        buf[42] = 42;
        sInputCodesComment = buf;
        int[] buf2 = new int[256];
        System.arraycopy(sInputCodesUTF8, 128, buf2, 128, 128);
        Arrays.fill(buf2, 0, 32, -1);
        buf2[32] = 1;
        buf2[9] = 1;
        buf2[10] = 10;
        buf2[13] = 13;
        buf2[47] = 47;
        buf2[35] = 35;
        sInputCodesWS = buf2;
        int[] table5 = new int[128];
        for (int i5 = 0; i5 < 32; i5++) {
            table5[i5] = -1;
        }
        table5[34] = 34;
        table5[92] = 92;
        table5[8] = 98;
        table5[9] = 116;
        table5[12] = 102;
        table5[10] = 110;
        table5[13] = 114;
        sOutputEscapes128 = table5;
        sHexValues = new int[256];
        Arrays.fill(sHexValues, -1);
        for (int i6 = 0; i6 < 10; i6++) {
            sHexValues[48 + i6] = i6;
        }
        for (int i7 = 0; i7 < 6; i7++) {
            sHexValues[97 + i7] = 10 + i7;
            sHexValues[65 + i7] = 10 + i7;
        }
    }

    public static int[] getInputCodeLatin1() {
        return sInputCodes;
    }

    public static int[] getInputCodeUtf8() {
        return sInputCodesUTF8;
    }

    public static int[] getInputCodeLatin1JsNames() {
        return sInputCodesJsNames;
    }

    public static int[] getInputCodeUtf8JsNames() {
        return sInputCodesUtf8JsNames;
    }

    public static int[] getInputCodeComment() {
        return sInputCodesComment;
    }

    public static int[] getInputCodeWS() {
        return sInputCodesWS;
    }

    public static int[] get7BitOutputEscapes() {
        return sOutputEscapes128;
    }

    public static int[] get7BitOutputEscapes(int quoteChar) {
        if (quoteChar == 34) {
            return sOutputEscapes128;
        }
        return AltEscapes.instance.escapesFor(quoteChar);
    }

    public static int charToHex(int ch2) {
        return sHexValues[ch2 & Const.MAX_ARRAY_DIMENSIONS];
    }

    public static char hexToChar(int ch2) {
        return HC[ch2];
    }

    public static void appendQuoted(StringBuilder sb, String content) {
        int[] escCodes = sOutputEscapes128;
        int escLen = escCodes.length;
        int len = content.length();
        for (int i = 0; i < len; i++) {
            char c = content.charAt(i);
            if (c >= escLen || escCodes[c] == 0) {
                sb.append(c);
            } else {
                sb.append('\\');
                int escCode = escCodes[c];
                if (escCode < 0) {
                    sb.append('u');
                    sb.append('0');
                    sb.append('0');
                    sb.append(HC[c >> 4]);
                    sb.append(HC[c & 15]);
                } else {
                    sb.append((char) escCode);
                }
            }
        }
    }

    @Deprecated
    public static char[] copyHexChars() {
        return copyHexChars(true);
    }

    public static char[] copyHexChars(boolean uppercase) {
        return uppercase ? (char[]) HC.clone() : (char[]) HClower.clone();
    }

    @Deprecated
    public static byte[] copyHexBytes() {
        return copyHexBytes(true);
    }

    public static byte[] copyHexBytes(boolean uppercase) {
        return uppercase ? (byte[]) HB.clone() : (byte[]) HBlower.clone();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/CharTypes$AltEscapes.class */
    private static class AltEscapes {
        public static final AltEscapes instance = new AltEscapes();
        private int[][] _altEscapes = new int[128];

        /* JADX WARN: Type inference failed for: r1v1, types: [int[], int[][]] */
        private AltEscapes() {
        }

        public int[] escapesFor(int quoteChar) {
            int[] esc = this._altEscapes[quoteChar];
            if (esc == null) {
                esc = Arrays.copyOf(CharTypes.sOutputEscapes128, 128);
                if (esc[quoteChar] == 0) {
                    esc[quoteChar] = -1;
                }
                this._altEscapes[quoteChar] = esc;
            }
            return esc;
        }
    }
}
