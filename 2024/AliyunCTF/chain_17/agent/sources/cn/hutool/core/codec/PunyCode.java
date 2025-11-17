package cn.hutool.core.codec;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/codec/PunyCode.class */
public class PunyCode {
    private static final int TMIN = 1;
    private static final int TMAX = 26;
    private static final int BASE = 36;
    private static final int INITIAL_N = 128;
    private static final int INITIAL_BIAS = 72;
    private static final int DAMP = 700;
    private static final int SKEW = 38;
    private static final char DELIMITER = '-';
    public static final String PUNY_CODE_PREFIX = "xn--";

    public static String encodeDomain(String domain) throws UtilException {
        Assert.notNull(domain, "domain must not be null!", new Object[0]);
        List<String> split = StrUtil.split((CharSequence) domain, '.');
        StringBuilder result = new StringBuilder(domain.length() * 4);
        for (String str : split) {
            if (result.length() != 0) {
                result.append('.');
            }
            result.append(encode(str, true));
        }
        return result.toString();
    }

    public static String encode(CharSequence input) throws UtilException {
        return encode(input, false);
    }

    public static String encode(CharSequence input, boolean withPrefix) throws UtilException {
        int t;
        Assert.notNull(input, "input must not be null!", new Object[0]);
        int n = 128;
        int delta = 0;
        int bias = 72;
        StringBuilder output = new StringBuilder();
        int length = input.length();
        int b = 0;
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            if (isBasic(c)) {
                output.append(c);
                b++;
            }
        }
        if (b > 0) {
            if (b == length) {
                return output.toString();
            }
            output.append('-');
        }
        int h = b;
        while (h < length) {
            int m = Integer.MAX_VALUE;
            for (int i2 = 0; i2 < length; i2++) {
                int c2 = input.charAt(i2);
                if (c2 >= n && c2 < m) {
                    m = c2;
                }
            }
            if (m - n > (Integer.MAX_VALUE - delta) / (h + 1)) {
                throw new UtilException("OVERFLOW");
            }
            int delta2 = delta + ((m - n) * (h + 1));
            int n2 = m;
            for (int j = 0; j < length; j++) {
                int c3 = input.charAt(j);
                if (c3 < n2) {
                    delta2++;
                    if (0 == delta2) {
                        throw new UtilException("OVERFLOW");
                    }
                }
                if (c3 == n2) {
                    int q = delta2;
                    int k = 36;
                    while (true) {
                        if (k <= bias) {
                            t = 1;
                        } else if (k >= bias + 26) {
                            t = 26;
                        } else {
                            t = k - bias;
                        }
                        if (q < t) {
                            break;
                        }
                        output.append((char) digit2codepoint(t + ((q - t) % (36 - t))));
                        q = (q - t) / (36 - t);
                        k += 36;
                    }
                    output.append((char) digit2codepoint(q));
                    bias = adapt(delta2, h + 1, h == b);
                    delta2 = 0;
                    h++;
                }
            }
            delta = delta2 + 1;
            n = n2 + 1;
        }
        if (withPrefix) {
            output.insert(0, PUNY_CODE_PREFIX);
        }
        return output.toString();
    }

    public static String decodeDomain(String domain) throws UtilException {
        Assert.notNull(domain, "domain must not be null!", new Object[0]);
        List<String> split = StrUtil.split((CharSequence) domain, '.');
        StringBuilder result = new StringBuilder((domain.length() / 4) + 1);
        for (String str : split) {
            if (result.length() != 0) {
                result.append('.');
            }
            result.append(StrUtil.startWithIgnoreEquals(str, PUNY_CODE_PREFIX) ? decode(str) : str);
        }
        return result.toString();
    }

    public static String decode(String input) throws UtilException {
        int d;
        int t;
        Assert.notNull(input, "input must not be null!", new Object[0]);
        String input2 = StrUtil.removePrefixIgnoreCase(input, PUNY_CODE_PREFIX);
        int n = 128;
        int i = 0;
        int bias = 72;
        StringBuilder output = new StringBuilder();
        int d2 = input2.lastIndexOf(45);
        if (d2 > 0) {
            for (int j = 0; j < d2; j++) {
                char c = input2.charAt(j);
                if (isBasic(c)) {
                    output.append(c);
                }
            }
            d = d2 + 1;
        } else {
            d = 0;
        }
        int length = input2.length();
        while (d < length) {
            int oldi = i;
            int w = 1;
            int k = 36;
            while (d != length) {
                int i2 = d;
                d++;
                int digit = codepoint2digit(input2.charAt(i2));
                if (digit > (Integer.MAX_VALUE - i) / w) {
                    throw new UtilException("OVERFLOW");
                }
                i += digit * w;
                if (k <= bias) {
                    t = 1;
                } else if (k >= bias + 26) {
                    t = 26;
                } else {
                    t = k - bias;
                }
                if (digit >= t) {
                    w *= 36 - t;
                    k += 36;
                } else {
                    bias = adapt(i - oldi, output.length() + 1, oldi == 0);
                    if (i / (output.length() + 1) > Integer.MAX_VALUE - n) {
                        throw new UtilException("OVERFLOW");
                    }
                    n += i / (output.length() + 1);
                    int i3 = i % (output.length() + 1);
                    output.insert(i3, (char) n);
                    i = i3 + 1;
                }
            }
            throw new UtilException("BAD_INPUT");
        }
        return output.toString();
    }

    private static int adapt(int delta, int numpoints, boolean first) {
        int delta2;
        if (first) {
            delta2 = delta / 700;
        } else {
            delta2 = delta / 2;
        }
        int delta3 = delta2 + (delta2 / numpoints);
        int i = 0;
        while (true) {
            int k = i;
            if (delta3 > 455) {
                delta3 /= 35;
                i = k + 36;
            } else {
                return k + ((36 * delta3) / (delta3 + 38));
            }
        }
    }

    private static boolean isBasic(char c) {
        return c < 128;
    }

    private static int digit2codepoint(int d) throws UtilException {
        Assert.checkBetween(d, 0, 35);
        if (d < 26) {
            return d + 97;
        }
        if (d < 36) {
            return (d - 26) + 48;
        }
        throw new UtilException("BAD_INPUT");
    }

    private static int codepoint2digit(int c) throws UtilException {
        if (c - 48 < 10) {
            return (c - 48) + 26;
        }
        if (c - 97 < 26) {
            return c - 97;
        }
        throw new UtilException("BAD_INPUT");
    }
}
