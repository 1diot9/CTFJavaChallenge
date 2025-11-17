package cn.hutool.cron.pattern.matcher;

import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/pattern/matcher/AlwaysTrueMatcher.class */
public class AlwaysTrueMatcher implements PartMatcher {
    public static AlwaysTrueMatcher INSTANCE = new AlwaysTrueMatcher();

    @Override // cn.hutool.core.lang.Matcher
    public boolean match(Integer t) {
        return true;
    }

    @Override // cn.hutool.cron.pattern.matcher.PartMatcher
    public int nextAfter(int value) {
        return value;
    }

    public String toString() {
        return StrUtil.format("[Matcher]: always true.", new Object[0]);
    }
}
