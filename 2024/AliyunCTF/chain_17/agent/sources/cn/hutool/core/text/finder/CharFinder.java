package cn.hutool.core.text.finder;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/text/finder/CharFinder.class */
public class CharFinder extends TextFinder {
    private static final long serialVersionUID = 1;
    private final char c;
    private final boolean caseInsensitive;

    public CharFinder(char c) {
        this(c, false);
    }

    public CharFinder(char c, boolean caseInsensitive) {
        this.c = c;
        this.caseInsensitive = caseInsensitive;
    }

    @Override // cn.hutool.core.text.finder.Finder
    public int start(int from) {
        Assert.notNull(this.text, "Text to find must be not null!", new Object[0]);
        int limit = getValidEndIndex();
        if (this.negative) {
            for (int i = from; i > limit; i--) {
                if (NumberUtil.equals(this.c, this.text.charAt(i), this.caseInsensitive)) {
                    return i;
                }
            }
            return -1;
        }
        for (int i2 = from; i2 < limit; i2++) {
            if (NumberUtil.equals(this.c, this.text.charAt(i2), this.caseInsensitive)) {
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
        return start + 1;
    }
}
