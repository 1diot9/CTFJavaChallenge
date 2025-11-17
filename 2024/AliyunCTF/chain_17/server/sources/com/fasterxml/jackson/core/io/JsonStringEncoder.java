package com.fasterxml.jackson.core.io;

import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.TextBuffer;
import java.io.IOException;
import java.util.Arrays;
import org.springframework.asm.Opcodes;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/JsonStringEncoder.class */
public final class JsonStringEncoder {
    private static final int SURR1_FIRST = 55296;
    private static final int SURR1_LAST = 56319;
    private static final int SURR2_FIRST = 56320;
    private static final int SURR2_LAST = 57343;
    static final int MIN_CHAR_BUFFER_SIZE = 16;
    static final int MAX_CHAR_BUFFER_SIZE = 32000;
    static final int MIN_BYTE_BUFFER_SIZE = 24;
    static final int MAX_BYTE_BUFFER_SIZE = 32000;
    private static final char[] HC = CharTypes.copyHexChars(true);
    private static final byte[] HB = CharTypes.copyHexBytes(true);
    private static final JsonStringEncoder instance = new JsonStringEncoder();

    public static JsonStringEncoder getInstance() {
        return instance;
    }

    public char[] quoteAsString(String input) {
        int _appendNamed;
        int inputLen = input.length();
        char[] outputBuffer = new char[_initialCharBufSize(inputLen)];
        int[] escCodes = CharTypes.get7BitOutputEscapes();
        int escCodeCount = escCodes.length;
        int inPtr = 0;
        TextBuffer textBuffer = null;
        int outPtr = 0;
        char[] qbuf = null;
        loop0: while (inPtr < inputLen) {
            do {
                char c = input.charAt(inPtr);
                if (c >= escCodeCount || escCodes[c] == 0) {
                    if (outPtr >= outputBuffer.length) {
                        if (textBuffer == null) {
                            textBuffer = TextBuffer.fromInitial(outputBuffer);
                        }
                        try {
                            outputBuffer = textBuffer.finishCurrentSegment();
                            outPtr = 0;
                        } catch (IOException e) {
                            throw new IllegalStateException(e);
                        }
                    }
                    int i = outPtr;
                    outPtr++;
                    outputBuffer[i] = c;
                    inPtr++;
                } else {
                    if (qbuf == null) {
                        qbuf = _qbuf();
                    }
                    int i2 = inPtr;
                    inPtr++;
                    char d = input.charAt(i2);
                    int escCode = escCodes[d];
                    if (escCode < 0) {
                        _appendNamed = _appendNumeric(d, qbuf);
                    } else {
                        _appendNamed = _appendNamed(escCode, qbuf);
                    }
                    int length = _appendNamed;
                    if (outPtr + length > outputBuffer.length) {
                        int first = outputBuffer.length - outPtr;
                        if (first > 0) {
                            System.arraycopy(qbuf, 0, outputBuffer, outPtr, first);
                        }
                        if (textBuffer == null) {
                            textBuffer = TextBuffer.fromInitial(outputBuffer);
                        }
                        try {
                            outputBuffer = textBuffer.finishCurrentSegment();
                            int second = length - first;
                            System.arraycopy(qbuf, first, outputBuffer, 0, second);
                            outPtr = second;
                        } catch (IOException e2) {
                            throw new IllegalStateException(e2);
                        }
                    } else {
                        System.arraycopy(qbuf, 0, outputBuffer, outPtr, length);
                        outPtr += length;
                    }
                }
            } while (inPtr < inputLen);
        }
        if (textBuffer == null) {
            return Arrays.copyOfRange(outputBuffer, 0, outPtr);
        }
        textBuffer.setCurrentLength(outPtr);
        try {
            return textBuffer.contentsAsArray();
        } catch (IOException e3) {
            throw new IllegalStateException(e3);
        }
    }

    public char[] quoteAsString(CharSequence input) {
        int _appendNamed;
        if (input instanceof String) {
            return quoteAsString((String) input);
        }
        TextBuffer textBuffer = null;
        int inputLen = input.length();
        char[] outputBuffer = new char[_initialCharBufSize(inputLen)];
        int[] escCodes = CharTypes.get7BitOutputEscapes();
        int escCodeCount = escCodes.length;
        int inPtr = 0;
        int outPtr = 0;
        char[] qbuf = null;
        loop0: while (inPtr < inputLen) {
            do {
                char c = input.charAt(inPtr);
                if (c >= escCodeCount || escCodes[c] == 0) {
                    if (outPtr >= outputBuffer.length) {
                        if (textBuffer == null) {
                            textBuffer = TextBuffer.fromInitial(outputBuffer);
                        }
                        try {
                            outputBuffer = textBuffer.finishCurrentSegment();
                            outPtr = 0;
                        } catch (IOException e) {
                            throw new IllegalStateException(e);
                        }
                    }
                    int i = outPtr;
                    outPtr++;
                    outputBuffer[i] = c;
                    inPtr++;
                } else {
                    if (qbuf == null) {
                        qbuf = _qbuf();
                    }
                    int i2 = inPtr;
                    inPtr++;
                    char d = input.charAt(i2);
                    int escCode = escCodes[d];
                    if (escCode < 0) {
                        _appendNamed = _appendNumeric(d, qbuf);
                    } else {
                        _appendNamed = _appendNamed(escCode, qbuf);
                    }
                    int length = _appendNamed;
                    if (outPtr + length > outputBuffer.length) {
                        int first = outputBuffer.length - outPtr;
                        if (first > 0) {
                            System.arraycopy(qbuf, 0, outputBuffer, outPtr, first);
                        }
                        if (textBuffer == null) {
                            textBuffer = TextBuffer.fromInitial(outputBuffer);
                        }
                        try {
                            outputBuffer = textBuffer.finishCurrentSegment();
                            int second = length - first;
                            System.arraycopy(qbuf, first, outputBuffer, 0, second);
                            outPtr = second;
                        } catch (IOException e2) {
                            throw new IllegalStateException(e2);
                        }
                    } else {
                        System.arraycopy(qbuf, 0, outputBuffer, outPtr, length);
                        outPtr += length;
                    }
                }
            } while (inPtr < inputLen);
        }
        if (textBuffer == null) {
            return Arrays.copyOfRange(outputBuffer, 0, outPtr);
        }
        textBuffer.setCurrentLength(outPtr);
        try {
            return textBuffer.contentsAsArray();
        } catch (IOException e3) {
            throw new IllegalStateException(e3);
        }
    }

    public void quoteAsString(CharSequence input, StringBuilder output) {
        int _appendNamed;
        int[] escCodes = CharTypes.get7BitOutputEscapes();
        int escCodeCount = escCodes.length;
        int inPtr = 0;
        int inputLen = input.length();
        char[] qbuf = null;
        while (inPtr < inputLen) {
            do {
                char c = input.charAt(inPtr);
                if (c >= escCodeCount || escCodes[c] == 0) {
                    output.append(c);
                    inPtr++;
                } else {
                    if (qbuf == null) {
                        qbuf = _qbuf();
                    }
                    int i = inPtr;
                    inPtr++;
                    char d = input.charAt(i);
                    int escCode = escCodes[d];
                    if (escCode < 0) {
                        _appendNamed = _appendNumeric(d, qbuf);
                    } else {
                        _appendNamed = _appendNamed(escCode, qbuf);
                    }
                    int length = _appendNamed;
                    output.append(qbuf, 0, length);
                }
            } while (inPtr < inputLen);
            return;
        }
    }

    public byte[] quoteAsUTF8(String text) {
        int outputPtr;
        int ch2;
        int inputPtr = 0;
        int inputEnd = text.length();
        int outputPtr2 = 0;
        byte[] outputBuffer = new byte[_initialByteBufSize(inputEnd)];
        ByteArrayBuilder bb = null;
        loop0: while (inputPtr < inputEnd) {
            int[] escCodes = CharTypes.get7BitOutputEscapes();
            do {
                int ch3 = text.charAt(inputPtr);
                if (ch3 <= 127 && escCodes[ch3] == 0) {
                    if (outputPtr2 >= outputBuffer.length) {
                        if (bb == null) {
                            bb = ByteArrayBuilder.fromInitial(outputBuffer, outputPtr2);
                        }
                        outputBuffer = bb.finishCurrentSegment();
                        outputPtr2 = 0;
                    }
                    int i = outputPtr2;
                    outputPtr2++;
                    outputBuffer[i] = (byte) ch3;
                    inputPtr++;
                } else {
                    if (bb == null) {
                        bb = ByteArrayBuilder.fromInitial(outputBuffer, outputPtr2);
                    }
                    if (outputPtr2 >= outputBuffer.length) {
                        outputBuffer = bb.finishCurrentSegment();
                        outputPtr2 = 0;
                    }
                    int i2 = inputPtr;
                    inputPtr++;
                    int ch4 = text.charAt(i2);
                    if (ch4 <= 127) {
                        int escape = escCodes[ch4];
                        outputPtr2 = _appendByte(ch4, escape, bb, outputPtr2);
                        outputBuffer = bb.getCurrentSegment();
                    } else {
                        if (ch4 <= 2047) {
                            int i3 = outputPtr2;
                            outputPtr = outputPtr2 + 1;
                            outputBuffer[i3] = (byte) (192 | (ch4 >> 6));
                            ch2 = 128 | (ch4 & 63);
                        } else if (ch4 < 55296 || ch4 > 57343) {
                            int i4 = outputPtr2;
                            int outputPtr3 = outputPtr2 + 1;
                            outputBuffer[i4] = (byte) (224 | (ch4 >> 12));
                            if (outputPtr3 >= outputBuffer.length) {
                                outputBuffer = bb.finishCurrentSegment();
                                outputPtr3 = 0;
                            }
                            int i5 = outputPtr3;
                            outputPtr = outputPtr3 + 1;
                            outputBuffer[i5] = (byte) (128 | ((ch4 >> 6) & 63));
                            ch2 = 128 | (ch4 & 63);
                        } else {
                            if (ch4 > 56319) {
                                _illegal(ch4);
                            }
                            if (inputPtr >= inputEnd) {
                                _illegal(ch4);
                            }
                            inputPtr++;
                            int ch5 = _convert(ch4, text.charAt(inputPtr));
                            if (ch5 > 1114111) {
                                _illegal(ch5);
                            }
                            int i6 = outputPtr2;
                            int outputPtr4 = outputPtr2 + 1;
                            outputBuffer[i6] = (byte) (240 | (ch5 >> 18));
                            if (outputPtr4 >= outputBuffer.length) {
                                outputBuffer = bb.finishCurrentSegment();
                                outputPtr4 = 0;
                            }
                            int i7 = outputPtr4;
                            int outputPtr5 = outputPtr4 + 1;
                            outputBuffer[i7] = (byte) (128 | ((ch5 >> 12) & 63));
                            if (outputPtr5 >= outputBuffer.length) {
                                outputBuffer = bb.finishCurrentSegment();
                                outputPtr5 = 0;
                            }
                            int i8 = outputPtr5;
                            outputPtr = outputPtr5 + 1;
                            outputBuffer[i8] = (byte) (128 | ((ch5 >> 6) & 63));
                            ch2 = 128 | (ch5 & 63);
                        }
                        if (outputPtr >= outputBuffer.length) {
                            outputBuffer = bb.finishCurrentSegment();
                            outputPtr = 0;
                        }
                        int i9 = outputPtr;
                        outputPtr2 = outputPtr + 1;
                        outputBuffer[i9] = (byte) ch2;
                    }
                }
            } while (inputPtr < inputEnd);
        }
        if (bb == null) {
            return Arrays.copyOfRange(outputBuffer, 0, outputPtr2);
        }
        return bb.completeAndCoalesce(outputPtr2);
    }

    /* JADX WARN: Code restructure failed: missing block: B:26:0x0076, code lost:            if (r13 != null) goto L21;     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0079, code lost:            r13 = com.fasterxml.jackson.core.util.ByteArrayBuilder.fromInitial(r11, r10);     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0086, code lost:            if (r10 < r12) goto L24;     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0089, code lost:            r11 = r13.finishCurrentSegment();        r12 = r11.length;        r10 = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x009d, code lost:            if (r14 >= 2048) goto L27;     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00a0, code lost:            r1 = r10;        r10 = r10 + 1;        r11[r1] = (byte) (192 | (r14 >> 6));     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x01a0, code lost:            if (r10 < r12) goto L64;     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x01a3, code lost:            r11 = r13.finishCurrentSegment();        r12 = r11.length;        r10 = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x01b2, code lost:            r1 = r10;        r10 = r10 + 1;        r11[r1] = (byte) (128 | (r14 & 63));     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x00b9, code lost:            if (r14 < 55296) goto L31;     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x00c0, code lost:            if (r14 <= 57343) goto L35;     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x0107, code lost:            if (r14 <= 56319) goto L38;     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x010a, code lost:            _illegal(r14);     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x0111, code lost:            if (r8 < r0) goto L41;     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x0114, code lost:            _illegal(r14);     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x0119, code lost:            r2 = r8;        r8 = r8 + 1;        r14 = _convert(r14, r7.charAt(r2));     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x012c, code lost:            if (r14 <= 1114111) goto L44;     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x012f, code lost:            _illegal(r14);     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x0134, code lost:            r1 = r10;        r10 = r10 + 1;        r11[r1] = (byte) (240 | (r14 >> 18));     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x014a, code lost:            if (r10 < r12) goto L47;     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x014d, code lost:            r11 = r13.finishCurrentSegment();        r12 = r11.length;        r10 = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x015c, code lost:            r1 = r10;        r10 = r10 + 1;        r11[r1] = (byte) (128 | ((r14 >> 12) & 63));     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x0175, code lost:            if (r10 < r12) goto L50;     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x0178, code lost:            r11 = r13.finishCurrentSegment();        r12 = r11.length;        r10 = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x0187, code lost:            r1 = r10;        r10 = r10 + 1;        r11[r1] = (byte) (128 | ((r14 >> 6) & 63));     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x00c3, code lost:            r1 = r10;        r10 = r10 + 1;        r11[r1] = (byte) (224 | (r14 >> 12));     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x00d9, code lost:            if (r10 < r12) goto L34;     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x00dc, code lost:            r11 = r13.finishCurrentSegment();        r12 = r11.length;        r10 = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x00eb, code lost:            r1 = r10;        r10 = r10 + 1;        r11[r1] = (byte) (128 | ((r14 >> 6) & 63));     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public byte[] encodeAsUTF8(java.lang.String r7) {
        /*
            Method dump skipped, instructions count: 477
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.io.JsonStringEncoder.encodeAsUTF8(java.lang.String):byte[]");
    }

    /* JADX WARN: Code restructure failed: missing block: B:26:0x007c, code lost:            if (r13 != null) goto L21;     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x007f, code lost:            r13 = com.fasterxml.jackson.core.util.ByteArrayBuilder.fromInitial(r11, r10);     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x008c, code lost:            if (r10 < r12) goto L24;     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x008f, code lost:            r11 = r13.finishCurrentSegment();        r12 = r11.length;        r10 = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x00a3, code lost:            if (r14 >= 2048) goto L27;     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00a6, code lost:            r1 = r10;        r10 = r10 + 1;        r11[r1] = (byte) (192 | (r14 >> 6));     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x01a8, code lost:            if (r10 < r12) goto L64;     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x01ab, code lost:            r11 = r13.finishCurrentSegment();        r12 = r11.length;        r10 = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x01ba, code lost:            r1 = r10;        r10 = r10 + 1;        r11[r1] = (byte) (128 | (r14 & 63));     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x00bf, code lost:            if (r14 < 55296) goto L31;     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x00c6, code lost:            if (r14 <= 57343) goto L35;     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x010d, code lost:            if (r14 <= 56319) goto L38;     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x0110, code lost:            _illegal(r14);     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x0117, code lost:            if (r8 < r0) goto L41;     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x011a, code lost:            _illegal(r14);     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x011f, code lost:            r2 = r8;        r8 = r8 + 1;        r14 = _convert(r14, r7.charAt(r2));     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x0134, code lost:            if (r14 <= 1114111) goto L44;     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x0137, code lost:            _illegal(r14);     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x013c, code lost:            r1 = r10;        r10 = r10 + 1;        r11[r1] = (byte) (240 | (r14 >> 18));     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x0152, code lost:            if (r10 < r12) goto L47;     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x0155, code lost:            r11 = r13.finishCurrentSegment();        r12 = r11.length;        r10 = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x0164, code lost:            r1 = r10;        r10 = r10 + 1;        r11[r1] = (byte) (128 | ((r14 >> 12) & 63));     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x017d, code lost:            if (r10 < r12) goto L50;     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x0180, code lost:            r11 = r13.finishCurrentSegment();        r12 = r11.length;        r10 = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x018f, code lost:            r1 = r10;        r10 = r10 + 1;        r11[r1] = (byte) (128 | ((r14 >> 6) & 63));     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x00c9, code lost:            r1 = r10;        r10 = r10 + 1;        r11[r1] = (byte) (224 | (r14 >> 12));     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x00df, code lost:            if (r10 < r12) goto L34;     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x00e2, code lost:            r11 = r13.finishCurrentSegment();        r12 = r11.length;        r10 = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x00f1, code lost:            r1 = r10;        r10 = r10 + 1;        r11[r1] = (byte) (128 | ((r14 >> 6) & 63));     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public byte[] encodeAsUTF8(java.lang.CharSequence r7) {
        /*
            Method dump skipped, instructions count: 485
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.io.JsonStringEncoder.encodeAsUTF8(java.lang.CharSequence):byte[]");
    }

    private char[] _qbuf() {
        char[] qbuf = {'\\', 0, '0', '0'};
        return qbuf;
    }

    private int _appendNumeric(int value, char[] qbuf) {
        qbuf[1] = 'u';
        qbuf[4] = HC[value >> 4];
        qbuf[5] = HC[value & 15];
        return 6;
    }

    private int _appendNamed(int esc, char[] qbuf) {
        qbuf[1] = (char) esc;
        return 2;
    }

    private int _appendByte(int ch2, int esc, ByteArrayBuilder bb, int ptr) {
        bb.setCurrentSegmentLength(ptr);
        bb.append(92);
        if (esc < 0) {
            bb.append(Opcodes.LNEG);
            if (ch2 > 255) {
                int hi = ch2 >> 8;
                bb.append(HB[hi >> 4]);
                bb.append(HB[hi & 15]);
                ch2 &= 255;
            } else {
                bb.append(48);
                bb.append(48);
            }
            bb.append(HB[ch2 >> 4]);
            bb.append(HB[ch2 & 15]);
        } else {
            bb.append((byte) esc);
        }
        return bb.getCurrentSegmentLength();
    }

    private static int _convert(int p1, int p2) {
        if (p2 < 56320 || p2 > 57343) {
            throw new IllegalArgumentException("Broken surrogate pair: first char 0x" + Integer.toHexString(p1) + ", second 0x" + Integer.toHexString(p2) + "; illegal combination");
        }
        return 65536 + ((p1 - 55296) << 10) + (p2 - 56320);
    }

    private static void _illegal(int c) {
        throw new IllegalArgumentException(UTF8Writer.illegalSurrogateDesc(c));
    }

    static int _initialCharBufSize(int strLen) {
        int estimated = Math.max(16, strLen + Math.min(6 + (strLen >> 3), 1000));
        return Math.min(estimated, 32000);
    }

    static int _initialByteBufSize(int strLen) {
        int doubled = Math.max(24, strLen + 6 + (strLen >> 1));
        return Math.min(doubled, 32000);
    }
}
