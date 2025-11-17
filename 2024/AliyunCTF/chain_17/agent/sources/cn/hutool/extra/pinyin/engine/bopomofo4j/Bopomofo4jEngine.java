package cn.hutool.extra.pinyin.engine.bopomofo4j;

import cn.hutool.extra.pinyin.PinyinEngine;
import com.rnkrsoft.bopomofo4j.Bopomofo4j;
import com.rnkrsoft.bopomofo4j.ToneType;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/pinyin/engine/bopomofo4j/Bopomofo4jEngine.class */
public class Bopomofo4jEngine implements PinyinEngine {
    public Bopomofo4jEngine() {
        Bopomofo4j.local();
    }

    @Override // cn.hutool.extra.pinyin.PinyinEngine
    public String getPinyin(char c) {
        return Bopomofo4j.pinyin(String.valueOf(c), ToneType.WITHOUT_TONE, false, false, "");
    }

    @Override // cn.hutool.extra.pinyin.PinyinEngine
    public String getPinyin(String str, String separator) {
        return Bopomofo4j.pinyin(str, ToneType.WITHOUT_TONE, false, false, separator);
    }
}
