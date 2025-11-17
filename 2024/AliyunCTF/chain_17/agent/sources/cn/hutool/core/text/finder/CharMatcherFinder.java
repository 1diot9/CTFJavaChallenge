package cn.hutool.core.text.finder;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Matcher;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/text/finder/CharMatcherFinder.class */
public class CharMatcherFinder extends TextFinder {
    private static final long serialVersionUID = 1;
    private final Matcher<Character> matcher;

    public CharMatcherFinder(Matcher<Character> matcher) {
        this.matcher = matcher;
    }

    @Override // cn.hutool.core.text.finder.Finder
    public int start(int from) {
        Assert.notNull(this.text, "Text to find must be not null!", new Object[0]);
        int limit = getValidEndIndex();
        if (this.negative) {
            for (int i = from; i > limit; i--) {
                if (this.matcher.match(Character.valueOf(this.text.charAt(i)))) {
                    return i;
                }
            }
            return -1;
        }
        for (int i2 = from; i2 < limit; i2++) {
            if (this.matcher.match(Character.valueOf(this.text.charAt(i2)))) {
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
