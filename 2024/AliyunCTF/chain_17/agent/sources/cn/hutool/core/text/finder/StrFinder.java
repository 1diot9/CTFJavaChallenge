package cn.hutool.core.text.finder;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/text/finder/StrFinder.class */
public class StrFinder extends TextFinder {
    private static final long serialVersionUID = 1;
    private final CharSequence strToFind;
    private final boolean caseInsensitive;

    public StrFinder(CharSequence strToFind, boolean caseInsensitive) {
        Assert.notEmpty(strToFind);
        this.strToFind = strToFind;
        this.caseInsensitive = caseInsensitive;
    }

    @Override // cn.hutool.core.text.finder.Finder
    public int start(int from) {
        Assert.notNull(this.text, "Text to find must be not null!", new Object[0]);
        int subLen = this.strToFind.length();
        if (from < 0) {
            from = 0;
        }
        int endLimit = getValidEndIndex();
        if (this.negative) {
            for (int i = from; i > endLimit; i--) {
                if (CharSequenceUtil.isSubEquals(this.text, i, this.strToFind, 0, subLen, this.caseInsensitive)) {
                    return i;
                }
            }
            return -1;
        }
        int endLimit2 = (endLimit - subLen) + 1;
        for (int i2 = from; i2 < endLimit2; i2++) {
            if (CharSequenceUtil.isSubEquals(this.text, i2, this.strToFind, 0, subLen, this.caseInsensitive)) {
                return i2;
            }
        }
        return -1;
    }

    @Override // cn.hutool.core.text.finder.Finder
    public int end(int start) {
        if (start < 0) {
            return -1;
        }
        return start + this.strToFind.length();
    }
}
