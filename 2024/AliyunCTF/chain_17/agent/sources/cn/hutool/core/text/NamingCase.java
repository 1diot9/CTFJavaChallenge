package cn.hutool.core.text;

import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/text/NamingCase.class */
public class NamingCase {
    public static String toUnderlineCase(CharSequence str) {
        return toSymbolCase(str, '_');
    }

    public static String toKebabCase(CharSequence str) {
        return toSymbolCase(str, '-');
    }

    public static String toSymbolCase(CharSequence str, char symbol) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        StrBuilder sb = new StrBuilder();
        int i = 0;
        while (i < length) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                Character preChar = i > 0 ? Character.valueOf(str.charAt(i - 1)) : null;
                Character nextChar = i < str.length() - 1 ? Character.valueOf(str.charAt(i + 1)) : null;
                if (null != preChar) {
                    if (symbol == preChar.charValue()) {
                        if (null == nextChar || Character.isLowerCase(nextChar.charValue())) {
                            c = Character.toLowerCase(c);
                        }
                    } else if (Character.isLowerCase(preChar.charValue())) {
                        sb.append(symbol);
                        if (null == nextChar || Character.isLowerCase(nextChar.charValue()) || CharUtil.isNumber(nextChar.charValue())) {
                            c = Character.toLowerCase(c);
                        }
                    } else if (null != nextChar && Character.isLowerCase(nextChar.charValue())) {
                        sb.append(symbol);
                        c = Character.toLowerCase(c);
                    }
                } else if (null == nextChar || Character.isLowerCase(nextChar.charValue())) {
                    c = Character.toLowerCase(c);
                }
            }
            sb.append(c);
            i++;
        }
        return sb.toString();
    }

    public static String toPascalCase(CharSequence name) {
        return StrUtil.upperFirst(toCamelCase(name));
    }

    public static String toCamelCase(CharSequence name) {
        return toCamelCase(name, '_');
    }

    public static String toCamelCase(CharSequence name, char symbol) {
        if (null == name) {
            return null;
        }
        String name2 = name.toString();
        if (StrUtil.contains(name2, symbol)) {
            int length = name2.length();
            StringBuilder sb = new StringBuilder(length);
            boolean upperCase = false;
            for (int i = 0; i < length; i++) {
                char c = name2.charAt(i);
                if (c == symbol) {
                    upperCase = true;
                } else if (upperCase) {
                    sb.append(Character.toUpperCase(c));
                    upperCase = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            }
            return sb.toString();
        }
        return name2;
    }
}
