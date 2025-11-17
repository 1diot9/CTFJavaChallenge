package org.apache.tomcat.util.http.fileupload.util.mime;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/util/mime/RFC2231Utility.class */
public final class RFC2231Utility {
    private static final byte MASK = Byte.MAX_VALUE;
    private static final int MASK_128 = 128;
    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
    private static final byte[] HEX_DECODE = new byte[128];

    static {
        for (int i = 0; i < HEX_DIGITS.length; i++) {
            HEX_DECODE[HEX_DIGITS[i]] = (byte) i;
            HEX_DECODE[Character.toLowerCase(HEX_DIGITS[i])] = (byte) i;
        }
    }

    private RFC2231Utility() {
    }

    public static boolean hasEncodedValue(String paramName) {
        return paramName != null && paramName.lastIndexOf(42) == paramName.length() - 1;
    }

    public static String stripDelimiter(String paramName) {
        if (hasEncodedValue(paramName)) {
            StringBuilder paramBuilder = new StringBuilder(paramName);
            paramBuilder.deleteCharAt(paramName.lastIndexOf(42));
            return paramBuilder.toString();
        }
        return paramName;
    }

    public static String decodeText(String encodedText) throws UnsupportedEncodingException {
        int langDelimitStart = encodedText.indexOf(39);
        if (langDelimitStart == -1) {
            return encodedText;
        }
        String mimeCharset = encodedText.substring(0, langDelimitStart);
        int langDelimitEnd = encodedText.indexOf(39, langDelimitStart + 1);
        if (langDelimitEnd == -1) {
            return encodedText;
        }
        byte[] bytes = fromHex(encodedText.substring(langDelimitEnd + 1));
        return new String(bytes, getJavaCharset(mimeCharset));
    }

    private static byte[] fromHex(String text) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(text.length());
        int i = 0;
        while (i < text.length()) {
            int i2 = i;
            i++;
            char c = text.charAt(i2);
            if (c == '%') {
                if (i > text.length() - 2) {
                    break;
                }
                int i3 = i + 1;
                byte b1 = HEX_DECODE[text.charAt(i) & 127];
                i = i3 + 1;
                byte b2 = HEX_DECODE[text.charAt(i3) & 127];
                out.write((b1 << 4) | b2);
            } else {
                out.write((byte) c);
            }
        }
        return out.toByteArray();
    }

    private static String getJavaCharset(String mimeCharset) {
        return mimeCharset;
    }
}
