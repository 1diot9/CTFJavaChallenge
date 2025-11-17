package cn.hutool.core.date;

import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.date.chinese.ChineseMonth;
import cn.hutool.core.date.chinese.GanZhi;
import cn.hutool.core.date.chinese.LunarFestival;
import cn.hutool.core.date.chinese.LunarInfo;
import cn.hutool.core.date.chinese.SolarTerms;
import cn.hutool.core.util.StrUtil;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/ChineseDate.class */
public class ChineseDate {
    private final int year;
    private final int month;
    private final boolean isLeapMonth;
    private final int day;
    private final int gyear;
    private final int gmonthBase1;
    private final int gday;

    public ChineseDate(Date date) {
        this(LocalDateTimeUtil.ofDate(date.toInstant()));
    }

    public ChineseDate(LocalDate localDate) {
        int daysOfMonth;
        int daysOfYear;
        this.gyear = localDate.getYear();
        this.gmonthBase1 = localDate.getMonthValue();
        this.gday = localDate.getDayOfMonth();
        int offset = (int) (localDate.toEpochDay() - LunarInfo.BASE_DAY);
        int iYear = 1900;
        while (iYear <= LunarInfo.MAX_YEAR && offset >= (daysOfYear = LunarInfo.yearDays(iYear))) {
            offset -= daysOfYear;
            iYear++;
        }
        this.year = iYear;
        int leapMonth = LunarInfo.leapMonth(iYear);
        boolean hasLeapMonth = false;
        int month = 1;
        while (month < 13) {
            if (leapMonth > 0 && month == leapMonth + 1) {
                daysOfMonth = LunarInfo.leapDays(this.year);
                hasLeapMonth = true;
            } else {
                daysOfMonth = LunarInfo.monthDays(this.year, hasLeapMonth ? month - 1 : month);
            }
            if (offset < daysOfMonth) {
                break;
            }
            offset -= daysOfMonth;
            month++;
        }
        this.isLeapMonth = leapMonth > 0 && month == leapMonth + 1;
        if (hasLeapMonth && false == this.isLeapMonth) {
            month--;
        }
        this.month = month;
        this.day = offset + 1;
    }

    public ChineseDate(int chineseYear, int chineseMonth, int chineseDay) {
        this(chineseYear, chineseMonth, chineseDay, chineseMonth == LunarInfo.leapMonth(chineseYear));
    }

    public ChineseDate(int chineseYear, int chineseMonth, int chineseDay, boolean isLeapMonth) {
        isLeapMonth = chineseMonth != LunarInfo.leapMonth(chineseYear) ? false : isLeapMonth;
        this.day = chineseDay;
        this.isLeapMonth = isLeapMonth;
        this.month = isLeapMonth ? chineseMonth + 1 : chineseMonth;
        this.year = chineseYear;
        DateTime dateTime = lunar2solar(chineseYear, chineseMonth, chineseDay, isLeapMonth);
        if (null != dateTime) {
            this.gday = dateTime.dayOfMonth();
            this.gmonthBase1 = dateTime.month() + 1;
            this.gyear = dateTime.year();
        } else {
            this.gday = -1;
            this.gmonthBase1 = -1;
            this.gyear = -1;
        }
    }

    public int getChineseYear() {
        return this.year;
    }

    public int getGregorianYear() {
        return this.gyear;
    }

    public int getMonth() {
        return this.month;
    }

    public int getGregorianMonthBase1() {
        return this.gmonthBase1;
    }

    public int getGregorianMonth() {
        return this.gmonthBase1 - 1;
    }

    public boolean isLeapMonth() {
        return this.isLeapMonth;
    }

    public String getChineseMonth() {
        return getChineseMonth(false);
    }

    public String getChineseMonthName() {
        return getChineseMonth(true);
    }

    public String getChineseMonth(boolean isTraditional) {
        return ChineseMonth.getChineseMonthName(isLeapMonth(), isLeapMonth() ? this.month - 1 : this.month, isTraditional);
    }

    public int getDay() {
        return this.day;
    }

    public int getGregorianDay() {
        return this.gday;
    }

    public String getChineseDay() {
        String[] chineseTen = {"初", "十", "廿", "卅"};
        int n = this.day % 10 == 0 ? 9 : (this.day % 10) - 1;
        if (this.day > 30) {
            return "";
        }
        switch (this.day) {
            case 10:
                return "初十";
            case 20:
                return "二十";
            case 30:
                return "三十";
            default:
                return chineseTen[this.day / 10] + NumberChineseFormatter.format(n + 1, false);
        }
    }

    public Date getGregorianDate() {
        return DateUtil.date(getGregorianCalendar());
    }

    public Calendar getGregorianCalendar() {
        Calendar calendar = CalendarUtil.calendar();
        calendar.set(this.gyear, getGregorianMonth(), this.gday, 0, 0, 0);
        return calendar;
    }

    public String getFestivals() {
        return StrUtil.join(",", LunarFestival.getFestivals(this.year, this.month, this.day));
    }

    public String getChineseZodiac() {
        return Zodiac.getChineseZodiac(this.year);
    }

    public String getCyclical() {
        return GanZhi.getGanzhiOfYear(this.year);
    }

    public String getCyclicalYMD() {
        if (this.gyear >= 1900 && this.gmonthBase1 > 0 && this.gday > 0) {
            return cyclicalm(this.gyear, this.gmonthBase1, this.gday);
        }
        return null;
    }

    public String getTerm() {
        return SolarTerms.getTerm(this.gyear, this.gmonthBase1, this.gday);
    }

    public String toStringNormal() {
        Object[] objArr = new Object[3];
        objArr[0] = Integer.valueOf(this.year);
        objArr[1] = Integer.valueOf(isLeapMonth() ? this.month - 1 : this.month);
        objArr[2] = Integer.valueOf(this.day);
        return String.format("%04d-%02d-%02d", objArr);
    }

    public String toString() {
        return String.format("%s%s年 %s%s", getCyclical(), getChineseZodiac(), getChineseMonthName(), getChineseDay());
    }

    private String cyclicalm(int year, int month, int day) {
        return StrUtil.format("{}年{}月{}日", GanZhi.getGanzhiOfYear(this.year), GanZhi.getGanzhiOfMonth(year, month, day), GanZhi.getGanzhiOfDay(year, month, day));
    }

    private DateTime lunar2solar(int chineseYear, int chineseMonth, int chineseDay, boolean isLeapMonth) {
        if (chineseYear == 2100 && chineseMonth == 12 && chineseDay > 1) {
            return null;
        }
        if (chineseYear == 1900 && chineseMonth == 1 && chineseDay < 31) {
            return null;
        }
        int day = LunarInfo.monthDays(chineseYear, chineseMonth);
        int _day = day;
        if (isLeapMonth) {
            _day = LunarInfo.leapDays(chineseYear);
        }
        if (chineseYear < 1900 || chineseYear > 2100 || chineseDay > _day) {
            return null;
        }
        int offset = 0;
        for (int i = 1900; i < chineseYear; i++) {
            offset += LunarInfo.yearDays(i);
        }
        boolean isAdd = false;
        for (int i2 = 1; i2 < chineseMonth; i2++) {
            int leap = LunarInfo.leapMonth(chineseYear);
            if (false == isAdd && leap <= i2 && leap > 0) {
                offset += LunarInfo.leapDays(chineseYear);
                isAdd = true;
            }
            offset += LunarInfo.monthDays(chineseYear, i2);
        }
        if (isLeapMonth) {
            offset += day;
        }
        return DateUtil.date((((offset + chineseDay) - 31) * 86400000) - 2203804800000L);
    }
}
