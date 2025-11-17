package cn.hutool.cron.pattern.matcher;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/pattern/matcher/YearValueMatcher.class */
public class YearValueMatcher implements PartMatcher {
    private final LinkedHashSet<Integer> valueList;

    public YearValueMatcher(Collection<Integer> intValueList) {
        this.valueList = new LinkedHashSet<>(intValueList);
    }

    @Override // cn.hutool.core.lang.Matcher
    public boolean match(Integer t) {
        return this.valueList.contains(t);
    }

    @Override // cn.hutool.cron.pattern.matcher.PartMatcher
    public int nextAfter(int value) {
        Iterator<Integer> it = this.valueList.iterator();
        while (it.hasNext()) {
            Integer year = it.next();
            if (year.intValue() >= value) {
                return year.intValue();
            }
        }
        return -1;
    }
}
