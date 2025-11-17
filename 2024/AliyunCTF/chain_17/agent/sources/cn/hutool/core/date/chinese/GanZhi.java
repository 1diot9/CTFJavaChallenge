package cn.hutool.core.date.chinese;

import java.time.LocalDate;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/chinese/GanZhi.class */
public class GanZhi {
    private static final String[] GAN = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    private static final String[] ZHI = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};

    public static String cyclicalm(int num) {
        return GAN[num % 10] + ZHI[num % 12];
    }

    public static String getGanzhiOfYear(int year) {
        return cyclicalm((year - LunarInfo.BASE_YEAR) + 36);
    }

    public static String getGanzhiOfMonth(int year, int month, int day) {
        int firstNode = SolarTerms.getTerm(year, (month * 2) - 1);
        int monthOffset = ((year - LunarInfo.BASE_YEAR) * 12) + month + 11;
        if (day >= firstNode) {
            monthOffset++;
        }
        return cyclicalm(monthOffset);
    }

    public static String getGanzhiOfDay(int year, int month, int day) {
        long days = LocalDate.of(year, month, day).toEpochDay() - 1;
        return cyclicalm((int) ((days - LunarInfo.BASE_DAY) + 41));
    }
}
