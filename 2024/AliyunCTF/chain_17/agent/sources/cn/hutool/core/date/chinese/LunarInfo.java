package cn.hutool.core.date.chinese;

import java.time.LocalDate;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/chinese/LunarInfo.class */
public class LunarInfo {
    public static final int BASE_YEAR = 1900;
    public static final long BASE_DAY = LocalDate.of(BASE_YEAR, 1, 31).toEpochDay();
    private static final long[] LUNAR_CODE = {19416, 19168, 42352, 21717, 53856, 55632, 91476, 22176, 39632, 21970, 19168, 42422, 42192, 53840, 119381, 46400, 54944, 44450, 38320, 84343, 18800, 42160, 46261, 27216, 27968, 109396, 11104, 38256, 21234, 18800, 25958, 54432, 59984, 92821, 23248, 11104, 100067, 37600, 116951, 51536, 54432, 120998, 46416, 22176, 107956, 9680, 37584, 53938, 43344, 46423, 27808, 46416, 86869, 19872, 42416, 83315, 21168, 43432, 59728, 27296, 44710, 43856, 19296, 43748, 42352, 21088, 62051, 55632, 23383, 22176, 38608, 19925, 19152, 42192, 54484, 53840, 54616, 46400, 46752, 103846, 38320, 18864, 43380, 42160, 45690, 27216, 27968, 44870, 43872, 38256, 19189, 18800, 25776, 29859, 59984, 27480, 23232, 43872, 38613, 37600, 51552, 55636, 54432, 55888, 30034, 22176, 43959, 9680, 37584, 51893, 43344, 46240, 47780, 44368, 21977, 19360, 42416, 86390, 21168, 43312, 31060, 27296, 44368, 23378, 19296, 42726, 42208, 53856, 60005, 54576, 23200, 30371, 38608, 19195, 19152, 42192, 118966, 53840, 54560, 56645, 46496, 22224, 21938, 18864, 42359, 42160, 43600, 111189, 27936, 44448, 84835, 37744, 18936, 18800, 25776, 92326, 59984, 27424, 108228, 43744, 37600, 53987, 51552, 54615, 54432, 55888, 23893, 22176, 42704, 21972, 21200, 43448, 43344, 46240, 46758, 44368, 21920, 43940, 42416, 21168, 45683, 26928, 29495, 27296, 44368, 84821, 19296, 42352, 21732, 53600, 59752, 54560, 55968, 92838, 22224, 19168, 43476, 41680, 53584, 62034};
    public static final int MAX_YEAR = (BASE_YEAR + LUNAR_CODE.length) - 1;

    public static int yearDays(int y) {
        int sum = 348;
        int i = 32768;
        while (true) {
            int i2 = i;
            if (i2 > 8) {
                if ((getCode(y) & i2) != 0) {
                    sum++;
                }
                i = i2 >> 1;
            } else {
                return sum + leapDays(y);
            }
        }
    }

    public static int leapDays(int y) {
        if (leapMonth(y) != 0) {
            return (getCode(y) & 65536) != 0 ? 30 : 29;
        }
        return 0;
    }

    public static int monthDays(int y, int m) {
        return (getCode(y) & ((long) (65536 >> m))) == 0 ? 29 : 30;
    }

    public static int leapMonth(int y) {
        return (int) (getCode(y) & 15);
    }

    private static long getCode(int year) {
        return LUNAR_CODE[year - BASE_YEAR];
    }
}
