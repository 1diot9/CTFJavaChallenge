package cn.hutool.cron.pattern;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/pattern/CronPatternUtil.class */
public class CronPatternUtil {
    public static Date nextDateAfter(CronPattern pattern, Date start, boolean isMatchSecond) {
        List<Date> matchedDates = matchedDates(pattern, start.getTime(), DateUtil.endOfYear(start).getTime(), 1, isMatchSecond);
        if (CollUtil.isNotEmpty((Collection<?>) matchedDates)) {
            return matchedDates.get(0);
        }
        return null;
    }

    public static List<Date> matchedDates(String patternStr, Date start, int count, boolean isMatchSecond) {
        return matchedDates(patternStr, start, DateUtil.endOfYear(start), count, isMatchSecond);
    }

    public static List<Date> matchedDates(String patternStr, Date start, Date end, int count, boolean isMatchSecond) {
        return matchedDates(patternStr, start.getTime(), end.getTime(), count, isMatchSecond);
    }

    public static List<Date> matchedDates(String patternStr, long start, long end, int count, boolean isMatchSecond) {
        return matchedDates(new CronPattern(patternStr), start, end, count, isMatchSecond);
    }

    public static List<Date> matchedDates(CronPattern pattern, long start, long end, int count, boolean isMatchSecond) {
        Assert.isTrue(start < end, "Start date is later than end !", new Object[0]);
        List<Date> result = new ArrayList<>(count);
        long step = isMatchSecond ? DateUnit.SECOND.getMillis() : DateUnit.MINUTE.getMillis();
        long j = start;
        while (true) {
            long i = j;
            if (i >= end) {
                break;
            }
            if (pattern.match(i, isMatchSecond)) {
                result.add(DateUtil.date(i));
                if (result.size() >= count) {
                    break;
                }
            }
            j = i + step;
        }
        return result;
    }
}
