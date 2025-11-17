package org.apache.tomcat.util.codec.binary;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/codec/binary/StringUtils.class */
public class StringUtils {
    private static byte[] getBytes(String string, Charset charset) {
        if (string == null) {
            return null;
        }
        return string.getBytes(charset);
    }

    public static byte[] getBytesUtf8(String string) {
        return getBytes(string, StandardCharsets.UTF_8);
    }

    private static String newString(byte[] bytes, Charset charset) {
        if (bytes == null) {
            return null;
        }
        return new String(bytes, charset);
    }

    public static String newStringUsAscii(byte[] bytes) {
        return newString(bytes, StandardCharsets.US_ASCII);
    }

    public static String newStringUtf8(byte[] bytes) {
        return newString(bytes, StandardCharsets.UTF_8);
    }
}
