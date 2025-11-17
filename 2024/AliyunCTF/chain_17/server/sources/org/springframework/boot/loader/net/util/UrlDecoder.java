package org.springframework.boot.loader.net.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

/* loaded from: server.jar:org/springframework/boot/loader/net/util/UrlDecoder.class */
public final class UrlDecoder {
    private UrlDecoder() {
    }

    public static String decode(String string) {
        int length = string.length();
        if (length == 0 || string.indexOf(37) < 0) {
            return string;
        }
        StringBuilder result = new StringBuilder(length);
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);
        CharBuffer charBuffer = CharBuffer.allocate(length);
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
        int index = 0;
        while (index < length) {
            char ch2 = string.charAt(index);
            if (ch2 != '%') {
                result.append(ch2);
                if (index + 1 >= length) {
                    return result.toString();
                }
                index++;
            } else {
                index = fillByteBuffer(byteBuffer, string, index, length);
                decodeToCharBuffer(byteBuffer, charBuffer, decoder);
                result.append((CharSequence) charBuffer.flip());
            }
        }
        return result.toString();
    }

    private static int fillByteBuffer(ByteBuffer byteBuffer, String string, int index, int length) {
        byteBuffer.clear();
        do {
            byteBuffer.put(unescape(string, index));
            index += 3;
            if (index >= length) {
                break;
            }
        } while (string.charAt(index) == '%');
        byteBuffer.flip();
        return index;
    }

    private static byte unescape(String string, int index) {
        try {
            return (byte) Integer.parseInt(string, index + 1, index + 3, 16);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        }
    }

    private static void decodeToCharBuffer(ByteBuffer byteBuffer, CharBuffer charBuffer, CharsetDecoder decoder) {
        decoder.reset();
        charBuffer.clear();
        assertNoError(decoder.decode(byteBuffer, charBuffer, true));
        assertNoError(decoder.flush(charBuffer));
    }

    private static void assertNoError(CoderResult result) {
        if (result.isError()) {
            throw new IllegalArgumentException("Error decoding percent encoded characters");
        }
    }
}
