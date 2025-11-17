package cn.hutool.cron.pattern.matcher;

import cn.hutool.cron.pattern.Part;
import java.time.Year;
import java.util.Calendar;
import java.util.TimeZone;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/pattern/matcher/PatternMatcher.class */
public class PatternMatcher {
    private final PartMatcher[] matchers;

    public PatternMatcher(PartMatcher secondMatcher, PartMatcher minuteMatcher, PartMatcher hourMatcher, PartMatcher dayOfMonthMatcher, PartMatcher monthMatcher, PartMatcher dayOfWeekMatcher, PartMatcher yearMatcher) {
        this.matchers = new PartMatcher[]{secondMatcher, minuteMatcher, hourMatcher, dayOfMonthMatcher, monthMatcher, dayOfWeekMatcher, yearMatcher};
    }

    public PartMatcher get(Part part) {
        return this.matchers[part.ordinal()];
    }

    public boolean match(int[] fields) {
        return match(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6]);
    }

    public boolean matchWeek(int dayOfWeekValue) {
        return this.matchers[5].match(Integer.valueOf(dayOfWeekValue));
    }

    private boolean match(int second, int minute, int hour, int dayOfMonth, int month, int dayOfWeek, int year) {
        return (second < 0 || this.matchers[0].match(Integer.valueOf(second))) && this.matchers[1].match(Integer.valueOf(minute)) && this.matchers[2].match(Integer.valueOf(hour)) && matchDayOfMonth(this.matchers[3], dayOfMonth, month, Year.isLeap((long) year)) && this.matchers[4].match(Integer.valueOf(month)) && this.matchers[5].match(Integer.valueOf(dayOfWeek)) && this.matchers[6].match(Integer.valueOf(year));
    }

    private static boolean matchDayOfMonth(PartMatcher matcher, int dayOfMonth, int month, boolean isLeapYear) {
        if (matcher instanceof DayOfMonthMatcher) {
            return ((DayOfMonthMatcher) matcher).match(dayOfMonth, month, isLeapYear);
        }
        return matcher.match(Integer.valueOf(dayOfMonth));
    }

    public Calendar nextMatchAfter(int[] values, TimeZone zone) {
        Calendar calendar = Calendar.getInstance(zone);
        calendar.set(14, 0);
        int[] newValues = nextMatchValuesAfter(values);
        for (int i = 0; i < newValues.length; i++) {
            if (i != Part.DAY_OF_WEEK.ordinal()) {
                setValue(calendar, Part.of(i), newValues[i]);
            }
        }
        return calendar;
    }

    private int[] nextMatchValuesAfter(int[] values) {
        int[] newValues = (int[]) values.clone();
        int i = Part.YEAR.ordinal();
        int nextValue = 0;
        while (true) {
            if (i < 0) {
                break;
            }
            if (i == Part.DAY_OF_WEEK.ordinal()) {
                i--;
            } else {
                nextValue = this.matchers[i].nextAfter(values[i]);
                if (nextValue > values[i]) {
                    newValues[i] = nextValue;
                    i--;
                    break;
                }
                if (nextValue < values[i]) {
                    i++;
                    nextValue = -1;
                    break;
                }
                i--;
            }
        }
        if (-1 == nextValue) {
            while (true) {
                if (i > Part.YEAR.ordinal()) {
                    break;
                }
                if (i == Part.DAY_OF_WEEK.ordinal()) {
                    i++;
                } else {
                    int nextValue2 = this.matchers[i].nextAfter(values[i] + 1);
                    if (nextValue2 > values[i]) {
                        newValues[i] = nextValue2;
                        i--;
                        break;
                    }
                    i++;
                }
            }
        }
        setToMin(newValues, i);
        return newValues;
    }

    private void setToMin(int[] values, int toPart) {
        for (int i = 0; i <= toPart; i++) {
            Part part = Part.of(i);
            values[i] = getMin(part);
        }
    }

    private int getMin(Part part) {
        int min;
        PartMatcher matcher = get(part);
        if (matcher instanceof AlwaysTrueMatcher) {
            min = part.getMin();
        } else if (matcher instanceof BoolArrayMatcher) {
            min = ((BoolArrayMatcher) matcher).getMinValue();
        } else {
            throw new IllegalArgumentException("Invalid matcher: " + matcher.getClass().getName());
        }
        return min;
    }

    private Calendar setValue(Calendar calendar, Part part, int value) {
        switch (part) {
            case MONTH:
                value--;
                break;
            case DAY_OF_WEEK:
                value++;
                break;
        }
        calendar.set(part.getCalendarField(), value);
        return calendar;
    }
}
