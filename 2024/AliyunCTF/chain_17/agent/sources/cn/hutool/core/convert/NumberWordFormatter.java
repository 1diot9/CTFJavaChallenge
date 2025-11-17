package cn.hutool.core.convert;

import ch.qos.logback.core.pattern.color.ANSIConstants;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/NumberWordFormatter.class */
public class NumberWordFormatter {
    private static final String[] NUMBER = {"", "ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE"};
    private static final String[] NUMBER_TEEN = {"TEN", "ELEVEN", "TWELVE", "THIRTEEN", "FOURTEEN", "FIFTEEN", "SIXTEEN", "SEVENTEEN", "EIGHTEEN", "NINETEEN"};
    private static final String[] NUMBER_TEN = {"TEN", "TWENTY", "THIRTY", "FORTY", "FIFTY", "SIXTY", "SEVENTY", "EIGHTY", "NINETY"};
    private static final String[] NUMBER_MORE = {"", "THOUSAND", "MILLION", "BILLION"};
    private static final String[] NUMBER_SUFFIX = {"k", "w", "", ANSIConstants.ESC_END, "", "", "b", "", "", "t", "", "", "p", "", "", "e"};

    public static String format(Object x) {
        if (x != null) {
            return format(x.toString());
        }
        return "";
    }

    public static String formatSimple(long value) {
        return formatSimple(value, true);
    }

    public static String formatSimple(long value, boolean isTwo) {
        if (value < 1000) {
            return String.valueOf(value);
        }
        int index = -1;
        double res = value;
        while (res > 10.0d && (false == isTwo || index < 1)) {
            if (res >= 1000.0d) {
                res /= 1000.0d;
                index++;
            }
            if (res > 10.0d) {
                res /= 10.0d;
                index++;
            }
        }
        return String.format("%s%s", NumberUtil.decimalFormat("#.##", res), NUMBER_SUFFIX[index]);
    }

    private static String format(String x) {
        String lstr;
        int z = x.indexOf(".");
        String rstr = "";
        if (z > -1) {
            lstr = x.substring(0, z);
            rstr = x.substring(z + 1);
        } else {
            lstr = x;
        }
        String lstrrev = StrUtil.reverse(lstr);
        String[] a = new String[5];
        switch (lstrrev.length() % 3) {
            case 1:
                lstrrev = lstrrev + "00";
                break;
            case 2:
                lstrrev = lstrrev + CustomBooleanEditor.VALUE_0;
                break;
        }
        StringBuilder lm = new StringBuilder();
        for (int i = 0; i < lstrrev.length() / 3; i++) {
            a[i] = StrUtil.reverse(lstrrev.substring(3 * i, (3 * i) + 3));
            if (false == "000".equals(a[i])) {
                if (i != 0) {
                    lm.insert(0, transThree(a[i]) + CharSequenceUtil.SPACE + parseMore(i) + CharSequenceUtil.SPACE);
                } else {
                    lm = new StringBuilder(transThree(a[i]));
                }
            } else {
                lm.append(transThree(a[i]));
            }
        }
        String xs = "";
        if (z > -1) {
            xs = "AND CENTS " + transTwo(rstr) + CharSequenceUtil.SPACE;
        }
        return lm.toString().trim() + CharSequenceUtil.SPACE + xs + "ONLY";
    }

    private static String parseFirst(String s) {
        return NUMBER[Integer.parseInt(s.substring(s.length() - 1))];
    }

    private static String parseTeen(String s) {
        return NUMBER_TEEN[Integer.parseInt(s) - 10];
    }

    private static String parseTen(String s) {
        return NUMBER_TEN[Integer.parseInt(s.substring(0, 1)) - 1];
    }

    private static String parseMore(int i) {
        return NUMBER_MORE[i];
    }

    private static String transTwo(String s) {
        String value;
        if (s.length() > 2) {
            s = s.substring(0, 2);
        } else if (s.length() < 2) {
            s = CustomBooleanEditor.VALUE_0 + s;
        }
        if (s.startsWith(CustomBooleanEditor.VALUE_0)) {
            value = parseFirst(s);
        } else if (s.startsWith(CustomBooleanEditor.VALUE_1)) {
            value = parseTeen(s);
        } else if (s.endsWith(CustomBooleanEditor.VALUE_0)) {
            value = parseTen(s);
        } else {
            value = parseTen(s) + CharSequenceUtil.SPACE + parseFirst(s);
        }
        return value;
    }

    private static String transThree(String s) {
        String value;
        if (s.startsWith(CustomBooleanEditor.VALUE_0)) {
            value = transTwo(s.substring(1));
        } else if ("00".equals(s.substring(1))) {
            value = parseFirst(s.substring(0, 1)) + " HUNDRED";
        } else {
            value = parseFirst(s.substring(0, 1)) + " HUNDRED AND " + transTwo(s.substring(1));
        }
        return value;
    }
}
