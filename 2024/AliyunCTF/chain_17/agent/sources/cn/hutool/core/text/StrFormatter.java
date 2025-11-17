package cn.hutool.core.text;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/text/StrFormatter.class */
public class StrFormatter {
    public static String format(String strPattern, Object... argArray) {
        return formatWith(strPattern, StrPool.EMPTY_JSON, argArray);
    }

    public static String formatWith(String strPattern, String placeHolder, Object... argArray) {
        int i;
        int i2;
        if (StrUtil.isBlank(strPattern) || StrUtil.isBlank(placeHolder) || ArrayUtil.isEmpty(argArray)) {
            return strPattern;
        }
        int strPatternLength = strPattern.length();
        int placeHolderLength = placeHolder.length();
        StringBuilder sbuf = new StringBuilder(strPatternLength + 50);
        int handledPosition = 0;
        int argIndex = 0;
        while (argIndex < argArray.length) {
            int delimIndex = strPattern.indexOf(placeHolder, handledPosition);
            if (delimIndex == -1) {
                if (handledPosition == 0) {
                    return strPattern;
                }
                sbuf.append((CharSequence) strPattern, handledPosition, strPatternLength);
                return sbuf.toString();
            }
            if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == '\\') {
                if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == '\\') {
                    sbuf.append((CharSequence) strPattern, handledPosition, delimIndex - 1);
                    sbuf.append(StrUtil.utf8Str(argArray[argIndex]));
                    i = delimIndex;
                    i2 = placeHolderLength;
                } else {
                    argIndex--;
                    sbuf.append((CharSequence) strPattern, handledPosition, delimIndex - 1);
                    sbuf.append(placeHolder.charAt(0));
                    i = delimIndex;
                    i2 = 1;
                }
            } else {
                sbuf.append((CharSequence) strPattern, handledPosition, delimIndex);
                sbuf.append(StrUtil.utf8Str(argArray[argIndex]));
                i = delimIndex;
                i2 = placeHolderLength;
            }
            handledPosition = i + i2;
            argIndex++;
        }
        sbuf.append((CharSequence) strPattern, handledPosition, strPatternLength);
        return sbuf.toString();
    }

    public static String format(CharSequence template, Map<?, ?> map, boolean ignoreNull) {
        if (null == template) {
            return null;
        }
        if (null == map || map.isEmpty()) {
            return template.toString();
        }
        String template2 = template.toString();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String value = StrUtil.utf8Str(entry.getValue());
            if (null != value || !ignoreNull) {
                template2 = StrUtil.replace(template2, StrPool.DELIM_START + entry.getKey() + "}", value);
            }
        }
        return template2;
    }
}
