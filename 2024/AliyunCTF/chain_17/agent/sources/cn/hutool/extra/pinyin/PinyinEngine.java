package cn.hutool.extra.pinyin;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/pinyin/PinyinEngine.class */
public interface PinyinEngine {
    String getPinyin(char c);

    String getPinyin(String str, String str2);

    default char getFirstLetter(char c) {
        return getPinyin(c).charAt(0);
    }

    default String getFirstLetter(String str, String separator) {
        String splitSeparator = StrUtil.isEmpty(separator) ? "#" : separator;
        List<String> split = StrUtil.split(getPinyin(str, splitSeparator), splitSeparator);
        return CollUtil.join(split, separator, s -> {
            return String.valueOf(s.length() > 0 ? Character.valueOf(s.charAt(0)) : "");
        });
    }
}
