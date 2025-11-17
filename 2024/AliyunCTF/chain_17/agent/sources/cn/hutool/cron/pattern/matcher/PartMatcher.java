package cn.hutool.cron.pattern.matcher;

import cn.hutool.core.lang.Matcher;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/pattern/matcher/PartMatcher.class */
public interface PartMatcher extends Matcher<Integer> {
    int nextAfter(int i);
}
