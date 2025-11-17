package cn.hutool.extra.pinyin.engine.jpinyin;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.extra.pinyin.PinyinEngine;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/pinyin/engine/jpinyin/JPinyinEngine.class */
public class JPinyinEngine implements PinyinEngine {
    PinyinFormat format;

    public JPinyinEngine() {
        this(null);
    }

    public JPinyinEngine(PinyinFormat format) {
        init(format);
    }

    public void init(PinyinFormat format) {
        if (null == format) {
            format = PinyinFormat.WITHOUT_TONE;
        }
        this.format = format;
    }

    @Override // cn.hutool.extra.pinyin.PinyinEngine
    public String getPinyin(char c) {
        String[] results = PinyinHelper.convertToPinyinArray(c, this.format);
        return ArrayUtil.isEmpty((Object[]) results) ? String.valueOf(c) : results[0];
    }

    @Override // cn.hutool.extra.pinyin.PinyinEngine
    public String getPinyin(String str, String separator) {
        try {
            return PinyinHelper.convertToPinyinString(str, separator, this.format);
        } catch (PinyinException e) {
            throw new cn.hutool.extra.pinyin.PinyinException((Throwable) e);
        }
    }
}
