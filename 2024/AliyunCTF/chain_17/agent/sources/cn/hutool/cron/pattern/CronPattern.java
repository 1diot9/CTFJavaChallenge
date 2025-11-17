package cn.hutool.cron.pattern;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.CalendarUtil;
import cn.hutool.cron.pattern.matcher.PatternMatcher;
import cn.hutool.cron.pattern.parser.PatternParser;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/pattern/CronPattern.class */
public class CronPattern {
    private final String pattern;
    private final List<PatternMatcher> matchers;

    public static CronPattern of(String pattern) {
        return new CronPattern(pattern);
    }

    public CronPattern(String pattern) {
        this.pattern = pattern;
        this.matchers = PatternParser.parse(pattern);
    }

    public boolean match(long millis, boolean isMatchSecond) {
        return match(TimeZone.getDefault(), millis, isMatchSecond);
    }

    public boolean match(TimeZone timezone, long millis, boolean isMatchSecond) {
        GregorianCalendar calendar = new GregorianCalendar(timezone);
        calendar.setTimeInMillis(millis);
        return match(calendar, isMatchSecond);
    }

    public boolean match(Calendar calendar, boolean isMatchSecond) {
        return match(PatternUtil.getFields(calendar, isMatchSecond));
    }

    public boolean match(LocalDateTime dateTime, boolean isMatchSecond) {
        return match(PatternUtil.getFields(dateTime, isMatchSecond));
    }

    public Calendar nextMatchAfter(Calendar calendar) {
        Calendar next = nextMatchAfter(PatternUtil.getFields(calendar, true), calendar.getTimeZone());
        if (false == match(next, true)) {
            next.set(5, next.get(5) + 1);
            return nextMatchAfter(CalendarUtil.beginOfDay(next));
        }
        return next;
    }

    public String toString() {
        return this.pattern;
    }

    private boolean match(int[] fields) {
        for (PatternMatcher matcher : this.matchers) {
            if (matcher.match(fields)) {
                return true;
            }
        }
        return false;
    }

    private Calendar nextMatchAfter(int[] values, TimeZone zone) {
        List<Calendar> nextMatches = new ArrayList<>(this.matchers.size());
        for (PatternMatcher matcher : this.matchers) {
            nextMatches.add(matcher.nextMatchAfter(values, zone));
        }
        return (Calendar) CollUtil.min(nextMatches);
    }
}
