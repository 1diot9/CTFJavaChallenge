package org.h2.util;

import ch.qos.logback.core.joran.JoranConstants;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.CharsetUtil;
import com.alibaba.com.caucho.hessian.io.Hessian2Constants;
import java.io.ByteArrayOutputStream;
import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import org.h2.api.ErrorCode;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/StringUtils.class */
public class StringUtils {
    private static SoftReference<String[]> softCache;
    private static long softCacheCreatedNs;
    private static final int TO_UPPER_CACHE_LENGTH = 2048;
    private static final int TO_UPPER_CACHE_MAX_ENTRY_LENGTH = 64;
    private static final char[] HEX = "0123456789abcdef".toCharArray();
    private static final int[] HEX_DECODE = new int[103];
    private static final String[][] TO_UPPER_CACHE = new String[2048];

    /* JADX WARN: Type inference failed for: r0v5, types: [java.lang.String[], java.lang.String[][]] */
    static {
        for (int i = 0; i < HEX_DECODE.length; i++) {
            HEX_DECODE[i] = -1;
        }
        for (int i2 = 0; i2 <= 9; i2++) {
            HEX_DECODE[i2 + 48] = i2;
        }
        for (int i3 = 0; i3 <= 5; i3++) {
            int i4 = i3 + 10;
            HEX_DECODE[i3 + 65] = i4;
            HEX_DECODE[i3 + 97] = i4;
        }
    }

    private StringUtils() {
    }

    private static String[] getCache() {
        String[] strArr;
        if (softCache != null && (strArr = softCache.get()) != null) {
            return strArr;
        }
        long nanoTime = System.nanoTime();
        if (softCacheCreatedNs != 0 && nanoTime - softCacheCreatedNs < TimeUnit.SECONDS.toNanos(5L)) {
            return null;
        }
        try {
            String[] strArr2 = new String[SysProperties.OBJECT_CACHE_SIZE];
            softCache = new SoftReference<>(strArr2);
            softCacheCreatedNs = System.nanoTime();
            return strArr2;
        } catch (Throwable th) {
            softCacheCreatedNs = System.nanoTime();
            throw th;
        }
    }

    public static String toUpperEnglish(String str) {
        if (str.length() > 64) {
            return str.toUpperCase(Locale.ENGLISH);
        }
        int hashCode = str.hashCode() & Hessian2Constants.INT_BYTE_MAX;
        String[] strArr = TO_UPPER_CACHE[hashCode];
        if (strArr != null && strArr[0].equals(str)) {
            return strArr[1];
        }
        String upperCase = str.toUpperCase(Locale.ENGLISH);
        String[] strArr2 = new String[2];
        strArr2[0] = str;
        strArr2[1] = upperCase;
        TO_UPPER_CACHE[hashCode] = strArr2;
        return upperCase;
    }

    public static String toLowerEnglish(String str) {
        return str.toLowerCase(Locale.ENGLISH);
    }

    public static String quoteStringSQL(String str) {
        if (str == null) {
            return JoranConstants.NULL;
        }
        return quoteStringSQL(new StringBuilder(str.length() + 2), str).toString();
    }

    public static StringBuilder quoteStringSQL(StringBuilder sb, String str) {
        if (str == null) {
            return sb.append(JoranConstants.NULL);
        }
        return quoteIdentifierOrLiteral(sb, str, '\'');
    }

    public static String decodeUnicodeStringSQL(String str, int i) {
        int length = str.length();
        StringBuilder sb = new StringBuilder(length);
        int i2 = 0;
        while (i2 < length) {
            int codePointAt = str.codePointAt(i2);
            i2 += Character.charCount(codePointAt);
            if (codePointAt == i) {
                if (i2 >= length) {
                    throw getFormatException(str, i2);
                }
                codePointAt = str.codePointAt(i2);
                if (codePointAt == i) {
                    i2 += Character.charCount(codePointAt);
                } else {
                    if (i2 + 4 > length) {
                        throw getFormatException(str, i2);
                    }
                    if (str.charAt(i2) == '+') {
                        try {
                            if (i2 + 7 > length) {
                                throw getFormatException(str, i2);
                            }
                            int i3 = i2 + 1;
                            i2 += 7;
                            codePointAt = Integer.parseUnsignedInt(str.substring(i3, i2), 16);
                        } catch (NumberFormatException e) {
                            throw getFormatException(str, i2);
                        }
                    } else {
                        i2 += 4;
                        codePointAt = Integer.parseUnsignedInt(str.substring(i2, i2), 16);
                    }
                }
            }
            sb.appendCodePoint(codePointAt);
        }
        return sb.toString();
    }

    public static String javaEncode(String str) {
        StringBuilder sb = new StringBuilder(str.length());
        javaEncode(str, sb, false);
        return sb.toString();
    }

    public static void javaEncode(String str, StringBuilder sb, boolean z) {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            switch (charAt) {
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\'':
                    if (z) {
                        sb.append('\'');
                    }
                    sb.append('\'');
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                default:
                    if (charAt >= ' ' && charAt < 128) {
                        sb.append(charAt);
                        break;
                    } else {
                        sb.append("\\u").append(HEX[charAt >>> '\f']).append(HEX[(charAt >>> '\b') & 15]).append(HEX[(charAt >>> 4) & 15]).append(HEX[charAt & 15]);
                        break;
                    }
                    break;
            }
        }
    }

    public static String addAsterisk(String str, int i) {
        if (str != null) {
            int length = str.length();
            int min = Math.min(i, length);
            str = new StringBuilder(length + 3).append((CharSequence) str, 0, min).append("[*]").append((CharSequence) str, min, length).toString();
        }
        return str;
    }

    private static DbException getFormatException(String str, int i) {
        return DbException.get(ErrorCode.STRING_FORMAT_ERROR_1, addAsterisk(str, i));
    }

    public static String javaDecode(String str) {
        int length = str.length();
        StringBuilder sb = new StringBuilder(length);
        int i = 0;
        while (i < length) {
            char charAt = str.charAt(i);
            if (charAt == '\\') {
                if (i + 1 >= str.length()) {
                    throw getFormatException(str, i);
                }
                i++;
                char charAt2 = str.charAt(i);
                switch (charAt2) {
                    case '\"':
                        sb.append('\"');
                        break;
                    case '#':
                        sb.append('#');
                        break;
                    case ':':
                        sb.append(':');
                        break;
                    case '=':
                        sb.append('=');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case Opcodes.FDIV /* 110 */:
                        sb.append('\n');
                        break;
                    case Opcodes.FREM /* 114 */:
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case Opcodes.LNEG /* 117 */:
                        if (i + 4 >= length) {
                            throw getFormatException(str, i);
                        }
                        try {
                            char parseInt = (char) Integer.parseInt(str.substring(i + 1, i + 5), 16);
                            i += 4;
                            sb.append(parseInt);
                            break;
                        } catch (NumberFormatException e) {
                            throw getFormatException(str, i);
                        }
                    default:
                        if (charAt2 >= '0' && charAt2 <= '9' && i + 2 < length) {
                            try {
                                char parseInt2 = (char) Integer.parseInt(str.substring(i, i + 3), 8);
                                i += 2;
                                sb.append(parseInt2);
                                break;
                            } catch (NumberFormatException e2) {
                                throw getFormatException(str, i);
                            }
                        } else {
                            throw getFormatException(str, i);
                        }
                }
            } else {
                sb.append(charAt);
            }
            i++;
        }
        return sb.toString();
    }

    public static String quoteJavaString(String str) {
        if (str == null) {
            return "null";
        }
        StringBuilder append = new StringBuilder(str.length() + 2).append('\"');
        javaEncode(str, append, false);
        return append.append('\"').toString();
    }

    public static String quoteJavaStringArray(String[] strArr) {
        if (strArr == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder("new String[]{");
        for (int i = 0; i < strArr.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(quoteJavaString(strArr[i]));
        }
        return sb.append('}').toString();
    }

    public static String quoteJavaIntArray(int[] iArr) {
        if (iArr == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder("new int[]{");
        for (int i = 0; i < iArr.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(iArr[i]);
        }
        return sb.append('}').toString();
    }

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, CharsetUtil.UTF_8);
        } catch (Exception e) {
            throw DbException.convert(e);
        }
    }

    public static String urlDecode(String str) {
        int length = str.length();
        byte[] bArr = new byte[length];
        int i = 0;
        int i2 = 0;
        while (i2 < length) {
            char charAt = str.charAt(i2);
            if (charAt == '+') {
                int i3 = i;
                i++;
                bArr[i3] = 32;
            } else if (charAt == '%') {
                int i4 = i;
                i++;
                bArr[i4] = (byte) Integer.parseInt(str.substring(i2 + 1, i2 + 3), 16);
                i2 += 2;
            } else if (charAt <= 127 && charAt >= ' ') {
                int i5 = i;
                i++;
                bArr[i5] = (byte) charAt;
            } else {
                throw new IllegalArgumentException("Unexpected char " + ((int) charAt) + " decoding " + str);
            }
            i2++;
        }
        return new String(bArr, 0, i, StandardCharsets.UTF_8);
    }

    public static String[] arraySplit(String str, char c, boolean z) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        if (length == 0) {
            return new String[0];
        }
        ArrayList newSmallArrayList = Utils.newSmallArrayList();
        StringBuilder sb = new StringBuilder(length);
        int i = 0;
        while (i < length) {
            char charAt = str.charAt(i);
            if (charAt == c) {
                String sb2 = sb.toString();
                newSmallArrayList.add(z ? sb2.trim() : sb2);
                sb.setLength(0);
            } else if (charAt == '\\' && i < length - 1) {
                i++;
                sb.append(str.charAt(i));
            } else {
                sb.append(charAt);
            }
            i++;
        }
        String sb3 = sb.toString();
        newSmallArrayList.add(z ? sb3.trim() : sb3);
        return (String[]) newSmallArrayList.toArray(new String[0]);
    }

    public static String arrayCombine(String[] strArr, char c) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strArr.length; i++) {
            if (i > 0) {
                sb.append(c);
            }
            String str = strArr[i];
            if (str != null) {
                int length = str.length();
                for (int i2 = 0; i2 < length; i2++) {
                    char charAt = str.charAt(i2);
                    if (charAt == '\\' || charAt == c) {
                        sb.append('\\');
                    }
                    sb.append(charAt);
                }
            }
        }
        return sb.toString();
    }

    public static String xmlAttr(String str, String str2) {
        return CharSequenceUtil.SPACE + str + "=\"" + xmlText(str2) + "\"";
    }

    public static String xmlNode(String str, String str2, String str3) {
        return xmlNode(str, str2, str3, true);
    }

    public static String xmlNode(String str, String str2, String str3, boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append('<').append(str);
        if (str2 != null) {
            sb.append(str2);
        }
        if (str3 == null) {
            sb.append("/>\n");
            return sb.toString();
        }
        sb.append('>');
        if (z && str3.indexOf(10) >= 0) {
            sb.append('\n');
            indent(sb, str3, 4, true);
        } else {
            sb.append(str3);
        }
        sb.append("</").append(str).append(">\n");
        return sb.toString();
    }

    public static StringBuilder indent(StringBuilder sb, String str, int i, boolean z) {
        int i2 = 0;
        int length = str.length();
        while (i2 < length) {
            for (int i3 = 0; i3 < i; i3++) {
                sb.append(' ');
            }
            int indexOf = str.indexOf(10, i2);
            int i4 = indexOf < 0 ? length : indexOf + 1;
            sb.append((CharSequence) str, i2, i4);
            i2 = i4;
        }
        if (z && !str.endsWith(StrPool.LF)) {
            sb.append('\n');
        }
        return sb;
    }

    public static String xmlComment(String str) {
        int i = 0;
        while (true) {
            i = str.indexOf("--", i);
            if (i < 0) {
                break;
            }
            str = str.substring(0, i + 1) + CharSequenceUtil.SPACE + str.substring(i + 1);
        }
        if (str.indexOf(10) >= 0) {
            return indent(new StringBuilder(str.length() + 18).append("<!--\n"), str, 4, true).append("-->\n").toString();
        }
        return "<!-- " + str + " -->\n";
    }

    public static String xmlCData(String str) {
        if (str.contains("]]>")) {
            return xmlText(str);
        }
        String str2 = "<![CDATA[" + str + "]]>";
        return str.endsWith(StrPool.LF) ? str2 + StrPool.LF : str2;
    }

    public static String xmlStartDoc() {
        return "<?xml version=\"1.0\"?>\n";
    }

    public static String xmlText(String str) {
        return xmlText(str, false);
    }

    public static String xmlText(String str, boolean z) {
        int length = str.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            switch (charAt) {
                case '\t':
                    sb.append(charAt);
                    break;
                case '\n':
                case '\r':
                    if (z) {
                        sb.append("&#x").append(Integer.toHexString(charAt)).append(';');
                        break;
                    } else {
                        sb.append(charAt);
                        break;
                    }
                case '\"':
                    sb.append("&quot;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '\'':
                    sb.append("&#39;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                default:
                    if (charAt < ' ' || charAt > 127) {
                        sb.append("&#x").append(Integer.toHexString(charAt)).append(';');
                        break;
                    } else {
                        sb.append(charAt);
                        break;
                    }
                    break;
            }
        }
        return sb.toString();
    }

    public static String replaceAll(String str, String str2, String str3) {
        int indexOf = str.indexOf(str2);
        if (indexOf < 0 || str2.isEmpty()) {
            return str;
        }
        StringBuilder sb = new StringBuilder((str.length() - str2.length()) + str3.length());
        int i = 0;
        do {
            sb.append((CharSequence) str, i, indexOf).append(str3);
            i = indexOf + str2.length();
            indexOf = str.indexOf(str2, i);
        } while (indexOf >= 0);
        sb.append((CharSequence) str, i, str.length());
        return sb.toString();
    }

    public static String quoteIdentifier(String str) {
        return quoteIdentifierOrLiteral(new StringBuilder(str.length() + 2), str, '\"').toString();
    }

    public static StringBuilder quoteIdentifier(StringBuilder sb, String str) {
        return quoteIdentifierOrLiteral(sb, str, '\"');
    }

    private static StringBuilder quoteIdentifierOrLiteral(StringBuilder sb, String str, char c) {
        int length = sb.length();
        sb.append(c);
        int i = 0;
        int length2 = str.length();
        while (i < length2) {
            int codePointAt = str.codePointAt(i);
            i += Character.charCount(codePointAt);
            if (codePointAt < 32 || codePointAt > 127) {
                sb.setLength(length);
                sb.append("U&").append(c);
                int i2 = 0;
                while (i2 < length2) {
                    int codePointAt2 = str.codePointAt(i2);
                    i2 += Character.charCount(codePointAt2);
                    if (codePointAt2 >= 32 && codePointAt2 < 127) {
                        char c2 = (char) codePointAt2;
                        if (c2 == c || c2 == '\\') {
                            sb.append(c2);
                        }
                        sb.append(c2);
                    } else if (codePointAt2 <= 65535) {
                        appendHex(sb.append('\\'), codePointAt2, 2);
                    } else {
                        appendHex(sb.append("\\+"), codePointAt2, 3);
                    }
                }
                return sb.append(c);
            }
            if (codePointAt == c) {
                sb.append(c);
            }
            sb.append((char) codePointAt);
        }
        return sb.append(c);
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static String pad(String str, int i, String str2, boolean z) {
        int i2;
        if (i < 0) {
            i = 0;
        }
        if (i < str.length()) {
            return str.substring(0, i);
        }
        if (i == str.length()) {
            return str;
        }
        if (str2 == null || str2.isEmpty()) {
            i2 = 32;
        } else {
            i2 = str2.codePointAt(0);
        }
        StringBuilder sb = new StringBuilder(i);
        int length = i - str.length();
        if (Character.isSupplementaryCodePoint(i2)) {
            length >>= 1;
        }
        if (z) {
            sb.append(str);
        }
        for (int i3 = 0; i3 < length; i3++) {
            sb.appendCodePoint(i2);
        }
        if (!z) {
            sb.append(str);
        }
        return sb.toString();
    }

    public static char[] cloneCharArray(char[] cArr) {
        if (cArr == null) {
            return null;
        }
        int length = cArr.length;
        if (length == 0) {
            return cArr;
        }
        return Arrays.copyOf(cArr, length);
    }

    public static String trim(String str, boolean z, boolean z2, String str2) {
        IntPredicate intPredicate;
        int codePointAt;
        if (str2 == null || str2.isEmpty()) {
            return trim(str, z, z2, ' ');
        }
        int length = str2.length();
        if (length == 1) {
            return trim(str, z, z2, str2.charAt(0));
        }
        int codePointCount = str2.codePointCount(0, length);
        if (codePointCount <= 2) {
            int codePointAt2 = str2.codePointAt(0);
            if (codePointCount > 1 && codePointAt2 != (codePointAt = str2.codePointAt(Character.charCount(codePointAt2)))) {
                intPredicate = i -> {
                    return i == codePointAt2 || i == codePointAt;
                };
            } else {
                intPredicate = i2 -> {
                    return i2 == codePointAt2;
                };
            }
        } else {
            HashSet hashSet = new HashSet();
            IntStream codePoints = str2.codePoints();
            hashSet.getClass();
            codePoints.forEach((v1) -> {
                r1.add(v1);
            });
            hashSet.getClass();
            intPredicate = (v1) -> {
                return r0.contains(v1);
            };
        }
        return trim(str, z, z2, intPredicate);
    }

    private static String trim(String str, boolean z, boolean z2, IntPredicate intPredicate) {
        int i = 0;
        int length = str.length();
        if (z) {
            while (i < length) {
                int codePointAt = str.codePointAt(i);
                if (!intPredicate.test(codePointAt)) {
                    break;
                }
                i += Character.charCount(codePointAt);
            }
        }
        if (z2) {
            while (length > i) {
                int codePointBefore = str.codePointBefore(length);
                if (!intPredicate.test(codePointBefore)) {
                    break;
                }
                length -= Character.charCount(codePointBefore);
            }
        }
        return str.substring(i, length);
    }

    public static String trim(String str, boolean z, boolean z2, char c) {
        int i = 0;
        int length = str.length();
        if (z) {
            while (i < length && str.charAt(i) == c) {
                i++;
            }
        }
        if (z2) {
            while (length > i && str.charAt(length - 1) == c) {
                length--;
            }
        }
        return str.substring(i, length);
    }

    public static String trimSubstring(String str, int i) {
        return trimSubstring(str, i, str.length());
    }

    public static String trimSubstring(String str, int i, int i2) {
        while (i < i2 && str.charAt(i) <= ' ') {
            i++;
        }
        while (i < i2 && str.charAt(i2 - 1) <= ' ') {
            i2--;
        }
        return str.substring(i, i2);
    }

    public static StringBuilder trimSubstring(StringBuilder sb, String str, int i, int i2) {
        while (i < i2 && str.charAt(i) <= ' ') {
            i++;
        }
        while (i < i2 && str.charAt(i2 - 1) <= ' ') {
            i2--;
        }
        return sb.append((CharSequence) str, i, i2);
    }

    public static String truncateString(String str, int i) {
        String str2;
        if (str.length() > i) {
            if (i > 0) {
                str2 = str.substring(0, Character.isSurrogatePair(str.charAt(i - 1), str.charAt(i)) ? i - 1 : i);
            } else {
                str2 = "";
            }
            str = str2;
        }
        return str;
    }

    public static String cache(String str) {
        if (!SysProperties.OBJECT_CACHE) {
            return str;
        }
        if (str == null) {
            return str;
        }
        if (str.isEmpty()) {
            return "";
        }
        String[] cache = getCache();
        if (cache != null) {
            int hashCode = str.hashCode() & (SysProperties.OBJECT_CACHE_SIZE - 1);
            String str2 = cache[hashCode];
            if (str.equals(str2)) {
                return str2;
            }
            cache[hashCode] = str;
        }
        return str;
    }

    public static void clearCache() {
        softCache = null;
    }

    public static int parseUInt31(String str, int i, int i2) {
        if (i2 > str.length() || i < 0 || i > i2) {
            throw new IndexOutOfBoundsException();
        }
        if (i == i2) {
            throw new NumberFormatException("");
        }
        int i3 = 0;
        for (int i4 = i; i4 < i2; i4++) {
            char charAt = str.charAt(i4);
            if (charAt < '0' || charAt > '9' || i3 > 214748364) {
                throw new NumberFormatException(str.substring(i, i2));
            }
            i3 = ((i3 * 10) + charAt) - 48;
            if (i3 < 0) {
                throw new NumberFormatException(str.substring(i, i2));
            }
        }
        return i3;
    }

    public static byte[] convertHexToBytes(String str) {
        int length = str.length();
        if (length % 2 != 0) {
            throw DbException.get(ErrorCode.HEX_STRING_ODD_1, str);
        }
        int i = length / 2;
        byte[] bArr = new byte[i];
        int i2 = 0;
        int[] iArr = HEX_DECODE;
        for (int i3 = 0; i3 < i; i3++) {
            try {
                int i4 = (iArr[str.charAt(i3 + i3)] << 4) | iArr[str.charAt(i3 + i3 + 1)];
                i2 |= i4;
                bArr[i3] = (byte) i4;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw DbException.get(ErrorCode.HEX_STRING_WRONG_1, str);
            }
        }
        if ((i2 & (-256)) != 0) {
            throw DbException.get(ErrorCode.HEX_STRING_WRONG_1, str);
        }
        return bArr;
    }

    public static ByteArrayOutputStream convertHexWithSpacesToBytes(ByteArrayOutputStream byteArrayOutputStream, String str, int i, int i2) {
        if (byteArrayOutputStream == null) {
            byteArrayOutputStream = new ByteArrayOutputStream((i2 - i) >>> 1);
        }
        int i3 = 0;
        int[] iArr = HEX_DECODE;
        int i4 = i;
        while (i4 < i2) {
            try {
                int i5 = i4;
                i4++;
                char charAt = str.charAt(i5);
                if (charAt != ' ') {
                    while (i4 < i2) {
                        int i6 = i4;
                        i4++;
                        char charAt2 = str.charAt(i6);
                        if (charAt2 != ' ') {
                            int i7 = (iArr[charAt] << 4) | iArr[charAt2];
                            i3 |= i7;
                            byteArrayOutputStream.write(i7);
                        }
                    }
                    if (((i3 | iArr[charAt]) & (-256)) != 0) {
                        throw getHexStringException(ErrorCode.HEX_STRING_WRONG_1, str, i, i2);
                    }
                    throw getHexStringException(ErrorCode.HEX_STRING_ODD_1, str, i, i2);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                throw getHexStringException(ErrorCode.HEX_STRING_WRONG_1, str, i, i2);
            }
        }
        if ((i3 & (-256)) != 0) {
            throw getHexStringException(ErrorCode.HEX_STRING_WRONG_1, str, i, i2);
        }
        return byteArrayOutputStream;
    }

    private static DbException getHexStringException(int i, String str, int i2, int i3) {
        return DbException.get(i, str.substring(i2, i3));
    }

    public static String convertBytesToHex(byte[] bArr) {
        return convertBytesToHex(bArr, bArr.length);
    }

    public static String convertBytesToHex(byte[] bArr, int i) {
        byte[] bArr2 = new byte[i * 2];
        char[] cArr = HEX;
        int i2 = 0;
        for (int i3 = 0; i3 < i; i3++) {
            int i4 = bArr[i3] & 255;
            int i5 = i2;
            int i6 = i2 + 1;
            bArr2[i5] = (byte) cArr[i4 >> 4];
            i2 = i6 + 1;
            bArr2[i6] = (byte) cArr[i4 & 15];
        }
        return new String(bArr2, StandardCharsets.ISO_8859_1);
    }

    public static StringBuilder convertBytesToHex(StringBuilder sb, byte[] bArr) {
        return convertBytesToHex(sb, bArr, bArr.length);
    }

    public static StringBuilder convertBytesToHex(StringBuilder sb, byte[] bArr, int i) {
        char[] cArr = HEX;
        for (int i2 = 0; i2 < i; i2++) {
            int i3 = bArr[i2] & 255;
            sb.append(cArr[i3 >>> 4]).append(cArr[i3 & 15]);
        }
        return sb;
    }

    public static StringBuilder appendHex(StringBuilder sb, long j, int i) {
        char[] cArr = HEX;
        int i2 = i * 8;
        while (i2 > 0) {
            int i3 = i2 - 4;
            StringBuilder append = sb.append(cArr[((int) (j >> i3)) & 15]);
            i2 = i3 - 4;
            append.append(cArr[((int) (j >> i2)) & 15]);
        }
        return sb;
    }

    public static boolean isNumber(String str) {
        int length = str.length();
        if (length == 0) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isWhitespaceOrEmpty(String str) {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (str.charAt(i) > ' ') {
                return false;
            }
        }
        return true;
    }

    public static StringBuilder appendTwoDigits(StringBuilder sb, int i) {
        if (i < 10) {
            sb.append('0');
        }
        return sb.append(i);
    }

    public static StringBuilder appendZeroPadded(StringBuilder sb, int i, int i2) {
        String num = Integer.toString(i2);
        for (int length = i - num.length(); length > 0; length--) {
            sb.append('0');
        }
        return sb.append(num);
    }

    public static StringBuilder appendToLength(StringBuilder sb, String str, int i) {
        int length = sb.length();
        if (length < i) {
            int i2 = i - length;
            if (i2 >= str.length()) {
                sb.append(str);
            } else {
                sb.append((CharSequence) str, 0, i2);
            }
        }
        return sb;
    }

    public static String escapeMetaDataPattern(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return replaceAll(str, StrPool.BACKSLASH, "\\\\");
    }
}
