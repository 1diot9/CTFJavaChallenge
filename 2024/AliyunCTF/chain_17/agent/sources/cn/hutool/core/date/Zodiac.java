package cn.hutool.core.date;

import cn.hutool.core.date.chinese.LunarInfo;
import java.util.Calendar;
import java.util.Date;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/Zodiac.class */
public class Zodiac {
    private static final int[] DAY_ARR = {20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22};
    private static final String[] ZODIACS = {"摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座"};
    private static final String[] CHINESE_ZODIACS = {"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};

    public static String getZodiac(Date date) {
        return getZodiac(DateUtil.calendar(date));
    }

    public static String getZodiac(Calendar calendar) {
        if (null == calendar) {
            return null;
        }
        return getZodiac(calendar.get(2), calendar.get(5));
    }

    public static String getZodiac(Month month, int day) {
        return getZodiac(month.getValue(), day);
    }

    public static String getZodiac(int month, int day) {
        return day < DAY_ARR[month] ? ZODIACS[month] : ZODIACS[month + 1];
    }

    public static String getChineseZodiac(Date date) {
        return getChineseZodiac(DateUtil.calendar(date));
    }

    public static String getChineseZodiac(Calendar calendar) {
        if (null == calendar) {
            return null;
        }
        return getChineseZodiac(calendar.get(1));
    }

    public static String getChineseZodiac(int year) {
        if (year < 1900) {
            return null;
        }
        return CHINESE_ZODIACS[(year - LunarInfo.BASE_YEAR) % CHINESE_ZODIACS.length];
    }
}
