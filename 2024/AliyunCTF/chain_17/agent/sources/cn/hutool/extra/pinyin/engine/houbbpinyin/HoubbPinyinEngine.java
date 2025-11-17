package cn.hutool.extra.pinyin.engine.houbbpinyin;

import cn.hutool.extra.pinyin.PinyinEngine;
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/pinyin/engine/houbbpinyin/HoubbPinyinEngine.class */
public class HoubbPinyinEngine implements PinyinEngine {
    PinyinStyleEnum format;

    public HoubbPinyinEngine() {
        this(null);
    }

    public HoubbPinyinEngine(PinyinStyleEnum format) {
        init(format);
    }

    public void init(PinyinStyleEnum format) {
        if (null == format) {
            format = PinyinStyleEnum.NORMAL;
        }
        this.format = format;
    }

    @Override // cn.hutool.extra.pinyin.PinyinEngine
    public String getPinyin(char c) {
        String result = PinyinHelper.toPinyin(String.valueOf(c), this.format);
        return result;
    }

    @Override // cn.hutool.extra.pinyin.PinyinEngine
    public String getPinyin(String str, String separator) {
        String result = PinyinHelper.toPinyin(str, this.format, separator);
        return result;
    }
}
