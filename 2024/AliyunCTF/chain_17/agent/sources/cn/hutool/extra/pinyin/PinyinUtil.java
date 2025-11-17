package cn.hutool.extra.pinyin;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.pinyin.engine.PinyinFactory;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/pinyin/PinyinUtil.class */
public class PinyinUtil {
    private static final String CHINESE_REGEX = "[\\u4e00-\\u9fa5]";

    public static PinyinEngine getEngine() {
        return PinyinFactory.get();
    }

    public static String getPinyin(char c) {
        return getEngine().getPinyin(c);
    }

    public static String getPinyin(String str) {
        return getPinyin(str, CharSequenceUtil.SPACE);
    }

    public static String getPinyin(String str, String separator) {
        return getEngine().getPinyin(str, separator);
    }

    public static char getFirstLetter(char c) {
        return getEngine().getFirstLetter(c);
    }

    public static String getFirstLetter(String str, String separator) {
        return getEngine().getFirstLetter(str, separator);
    }

    public static boolean isChinese(char c) {
        return 12295 == c || String.valueOf(c).matches(CHINESE_REGEX);
    }
}
