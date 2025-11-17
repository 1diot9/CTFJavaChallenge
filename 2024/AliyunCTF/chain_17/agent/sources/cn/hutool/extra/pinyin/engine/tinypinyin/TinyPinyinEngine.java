package cn.hutool.extra.pinyin.engine.tinypinyin;

import cn.hutool.extra.pinyin.PinyinEngine;
import com.github.promeg.pinyinhelper.Pinyin;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/pinyin/engine/tinypinyin/TinyPinyinEngine.class */
public class TinyPinyinEngine implements PinyinEngine {
    public TinyPinyinEngine() {
        this(null);
    }

    public TinyPinyinEngine(Pinyin.Config config) {
        Pinyin.init(config);
    }

    @Override // cn.hutool.extra.pinyin.PinyinEngine
    public String getPinyin(char c) {
        if (false == Pinyin.isChinese(c)) {
            return String.valueOf(c);
        }
        return Pinyin.toPinyin(c).toLowerCase();
    }

    @Override // cn.hutool.extra.pinyin.PinyinEngine
    public String getPinyin(String str, String separator) {
        return Pinyin.toPinyin(str, separator).toLowerCase();
    }
}
