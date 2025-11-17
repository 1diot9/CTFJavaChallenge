package cn.hutool.cron.pattern.matcher;

import cn.hutool.core.date.Month;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/pattern/matcher/DayOfMonthMatcher.class */
public class DayOfMonthMatcher extends BoolArrayMatcher {
    public DayOfMonthMatcher(List<Integer> intValueList) {
        super(intValueList);
    }

    public boolean match(int value, int month, boolean isLeapYear) {
        return super.match(Integer.valueOf(value)) || (value > 27 && match((Integer) 31) && isLastDayOfMonth(value, month, isLeapYear));
    }

    private static boolean isLastDayOfMonth(int value, int month, boolean isLeapYear) {
        return value == Month.getLastDay(month - 1, isLeapYear);
    }
}
