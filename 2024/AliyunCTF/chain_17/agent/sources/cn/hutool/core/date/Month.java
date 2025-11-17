package cn.hutool.core.date;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import java.time.format.TextStyle;
import java.util.Locale;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/Month.class */
public enum Month {
    JANUARY(0),
    FEBRUARY(1),
    MARCH(2),
    APRIL(3),
    MAY(4),
    JUNE(5),
    JULY(6),
    AUGUST(7),
    SEPTEMBER(8),
    OCTOBER(9),
    NOVEMBER(10),
    DECEMBER(11),
    UNDECIMBER(12);

    private static final String[] ALIASES = {"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};
    private static final Month[] ENUMS = values();
    private final int value;

    Month(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public int getValueBaseOne() {
        Assert.isFalse(this == UNDECIMBER, "Unsupported UNDECIMBER Field", new Object[0]);
        return getValue() + 1;
    }

    public int getLastDay(boolean isLeapYear) {
        switch (this) {
            case FEBRUARY:
                return isLeapYear ? 29 : 28;
            case APRIL:
            case JUNE:
            case SEPTEMBER:
            case NOVEMBER:
                return 30;
            default:
                return 31;
        }
    }

    public static Month of(int calendarMonthIntValue) {
        if (calendarMonthIntValue >= ENUMS.length || calendarMonthIntValue < 0) {
            return null;
        }
        return ENUMS[calendarMonthIntValue];
    }

    public static Month of(String name) throws IllegalArgumentException {
        Assert.notBlank(name);
        Month of = of(ArrayUtil.indexOfIgnoreCase(ALIASES, name));
        if (null == of) {
            of = valueOf(name.toUpperCase());
        }
        return of;
    }

    public static Month of(java.time.Month month) {
        return of(month.ordinal());
    }

    public static int getLastDay(int month, boolean isLeapYear) {
        Month of = of(month);
        Assert.notNull(of, "Invalid Month base 0: " + month, new Object[0]);
        return of.getLastDay(isLeapYear);
    }

    public java.time.Month toJdkMonth() {
        return java.time.Month.of(getValueBaseOne());
    }

    public String getDisplayName(TextStyle style) {
        return getDisplayName(style, Locale.getDefault());
    }

    public String getDisplayName(TextStyle style, Locale locale) {
        return toJdkMonth().getDisplayName(style, locale);
    }
}
