package com.fasterxml.jackson.core.io;

import com.fasterxml.jackson.core.io.doubleparser.JavaBigDecimalParser;
import java.math.BigDecimal;
import java.util.Arrays;
import org.apache.el.parser.ELParserConstants;
import org.springframework.asm.TypeReference;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/BigDecimalParser.class */
public final class BigDecimalParser {
    static final int MAX_CHARS_TO_REPORT = 1000;

    private BigDecimalParser() {
    }

    public static BigDecimal parse(String valueStr) {
        return parse(valueStr.toCharArray());
    }

    public static BigDecimal parse(char[] chars, int off, int len) {
        String stringToReport;
        try {
            if (len < 500) {
                return new BigDecimal(chars, off, len);
            }
            return parseBigDecimal(chars, off, len, len / 10);
        } catch (ArithmeticException | NumberFormatException e) {
            String desc = e.getMessage();
            if (desc == null) {
                desc = "Not a valid number representation";
            }
            if (len <= 1000) {
                stringToReport = new String(chars, off, len);
            } else {
                stringToReport = new String(Arrays.copyOfRange(chars, off, 1000)) + "(truncated, full length is " + chars.length + " chars)";
            }
            throw new NumberFormatException("Value \"" + stringToReport + "\" can not be represented as `java.math.BigDecimal`, reason: " + desc);
        }
    }

    public static BigDecimal parse(char[] chars) {
        return parse(chars, 0, chars.length);
    }

    public static BigDecimal parseWithFastParser(String valueStr) {
        try {
            return JavaBigDecimalParser.parseBigDecimal(valueStr);
        } catch (NumberFormatException nfe) {
            String reportNum = valueStr.length() <= 1000 ? valueStr : valueStr.substring(0, 1000) + " [truncated]";
            throw new NumberFormatException("Value \"" + reportNum + "\" can not be represented as `java.math.BigDecimal`, reason: " + nfe.getMessage());
        }
    }

    public static BigDecimal parseWithFastParser(char[] ch2, int off, int len) {
        try {
            return JavaBigDecimalParser.parseBigDecimal(ch2, off, len);
        } catch (NumberFormatException nfe) {
            String reportNum = len <= 1000 ? new String(ch2, off, len) : new String(ch2, off, 1000) + " [truncated]";
            throw new NumberFormatException("Value \"" + reportNum + "\" can not be represented as `java.math.BigDecimal`, reason: " + nfe.getMessage());
        }
    }

    private static BigDecimal parseBigDecimal(char[] chars, int off, int len, int splitLen) {
        int numEndIdx;
        BigDecimal res;
        boolean numHasSign = false;
        boolean expHasSign = false;
        boolean neg = false;
        int numIdx = off;
        int expIdx = -1;
        int dotIdx = -1;
        int scale = 0;
        int endIdx = off + len;
        for (int i = off; i < endIdx; i++) {
            char c = chars[i];
            switch (c) {
                case ELParserConstants.EMPTY /* 43 */:
                    if (expIdx >= 0) {
                        if (expHasSign) {
                            throw new NumberFormatException("Multiple signs in exponent");
                        }
                        expHasSign = true;
                        break;
                    } else {
                        if (numHasSign) {
                            throw new NumberFormatException("Multiple signs in number");
                        }
                        numHasSign = true;
                        numIdx = i + 1;
                        break;
                    }
                case '-':
                    if (expIdx >= 0) {
                        if (expHasSign) {
                            throw new NumberFormatException("Multiple signs in exponent");
                        }
                        expHasSign = true;
                        break;
                    } else {
                        if (numHasSign) {
                            throw new NumberFormatException("Multiple signs in number");
                        }
                        numHasSign = true;
                        neg = true;
                        numIdx = i + 1;
                        break;
                    }
                case '.':
                    if (dotIdx >= 0) {
                        throw new NumberFormatException("Multiple decimal points");
                    }
                    dotIdx = i;
                    break;
                case TypeReference.CONSTRUCTOR_REFERENCE /* 69 */:
                case 'e':
                    if (expIdx >= 0) {
                        throw new NumberFormatException("Multiple exponent markers");
                    }
                    expIdx = i;
                    break;
                default:
                    if (dotIdx >= 0 && expIdx == -1) {
                        scale++;
                        break;
                    }
                    break;
            }
        }
        int exp = 0;
        if (expIdx >= 0) {
            numEndIdx = expIdx;
            String expStr = new String(chars, expIdx + 1, (endIdx - expIdx) - 1);
            exp = Integer.parseInt(expStr);
            scale = adjustScale(scale, exp);
        } else {
            numEndIdx = endIdx;
        }
        if (dotIdx >= 0) {
            int leftLen = dotIdx - numIdx;
            BigDecimal left = toBigDecimalRec(chars, numIdx, leftLen, exp, splitLen);
            int rightLen = (numEndIdx - dotIdx) - 1;
            BigDecimal right = toBigDecimalRec(chars, dotIdx + 1, rightLen, exp - rightLen, splitLen);
            res = left.add(right);
        } else {
            res = toBigDecimalRec(chars, numIdx, numEndIdx - numIdx, exp, splitLen);
        }
        if (scale != 0) {
            res = res.setScale(scale);
        }
        if (neg) {
            res = res.negate();
        }
        return res;
    }

    private static int adjustScale(int scale, long exp) {
        long adjScale = scale - exp;
        if (adjScale > 2147483647L || adjScale < -2147483648L) {
            throw new NumberFormatException("Scale out of range: " + adjScale + " while adjusting scale " + scale + " to exponent " + exp);
        }
        return (int) adjScale;
    }

    private static BigDecimal toBigDecimalRec(char[] chars, int off, int len, int scale, int splitLen) {
        if (len > splitLen) {
            int mid = len / 2;
            BigDecimal left = toBigDecimalRec(chars, off, mid, (scale + len) - mid, splitLen);
            BigDecimal right = toBigDecimalRec(chars, off + mid, len - mid, scale, splitLen);
            return left.add(right);
        }
        if (len == 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(chars, off, len).scaleByPowerOfTen(scale);
    }
}
