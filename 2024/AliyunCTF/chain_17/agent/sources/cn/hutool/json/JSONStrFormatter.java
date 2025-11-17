package cn.hutool.json;

import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/json/JSONStrFormatter.class */
public class JSONStrFormatter {
    private static final String SPACE = "    ";
    private static final char NEW_LINE = '\n';

    public static String format(String json) {
        StringBuilder result = new StringBuilder();
        Character wrapChar = null;
        boolean isEscapeMode = false;
        int length = json.length();
        int number = 0;
        for (int i = 0; i < length; i++) {
            char key = json.charAt(i);
            if ('\"' == key || '\'' == key) {
                if (null == wrapChar) {
                    wrapChar = Character.valueOf(key);
                } else if (isEscapeMode) {
                    isEscapeMode = false;
                } else if (wrapChar.equals(Character.valueOf(key))) {
                    wrapChar = null;
                }
                if (i > 1 && json.charAt(i - 1) == ':') {
                    result.append(' ');
                }
                result.append(key);
            } else {
                if ('\\' == key) {
                    if (null != wrapChar) {
                        isEscapeMode = !isEscapeMode;
                        result.append(key);
                    } else {
                        result.append(key);
                    }
                }
                if (null != wrapChar) {
                    result.append(key);
                } else if (key == '[' || key == '{') {
                    if (i > 1 && json.charAt(i - 1) == ':') {
                        result.append('\n');
                        result.append(indent(number));
                    }
                    result.append(key);
                    result.append('\n');
                    number++;
                    result.append(indent(number));
                } else if (key == ']' || key == '}') {
                    result.append('\n');
                    number--;
                    result.append(indent(number));
                    result.append(key);
                } else if (key == ',') {
                    result.append(key);
                    result.append('\n');
                    result.append(indent(number));
                } else {
                    if (i > 1 && json.charAt(i - 1) == ':') {
                        result.append(' ');
                    }
                    result.append(key);
                }
            }
        }
        return result.toString();
    }

    private static String indent(int number) {
        return StrUtil.repeat(SPACE, number);
    }
}
