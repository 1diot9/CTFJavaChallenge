package cn.hutool.extra.pinyin.engine.pinyin4j;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinEngine;
import cn.hutool.extra.pinyin.PinyinException;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/pinyin/engine/pinyin4j/Pinyin4jEngine.class */
public class Pinyin4jEngine implements PinyinEngine {
    HanyuPinyinOutputFormat format;

    public Pinyin4jEngine() {
        this(null);
    }

    public Pinyin4jEngine(HanyuPinyinOutputFormat format) {
        init(format);
    }

    public void init(HanyuPinyinOutputFormat format) {
        if (null == format) {
            format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            format.setVCharType(HanyuPinyinVCharType.WITH_V);
        }
        this.format = format;
    }

    @Override // cn.hutool.extra.pinyin.PinyinEngine
    public String getPinyin(char c) {
        String result;
        try {
            String[] results = PinyinHelper.toHanyuPinyinStringArray(c, this.format);
            result = ArrayUtil.isEmpty((Object[]) results) ? String.valueOf(c) : results[0];
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            result = String.valueOf(c);
        }
        return result;
    }

    @Override // cn.hutool.extra.pinyin.PinyinEngine
    public String getPinyin(String str, String separator) {
        StrBuilder result = StrUtil.strBuilder();
        boolean isFirst = true;
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (isFirst) {
                isFirst = false;
            } else {
                result.append((CharSequence) separator);
            }
            String[] pinyinStringArray = PinyinHelper.toHanyuPinyinStringArray(str.charAt(i), this.format);
            if (ArrayUtil.isEmpty((Object[]) pinyinStringArray)) {
                try {
                    result.append(str.charAt(i));
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    throw new PinyinException((Throwable) e);
                }
            } else {
                result.append((CharSequence) pinyinStringArray[0]);
            }
        }
        return result.toString();
    }
}
