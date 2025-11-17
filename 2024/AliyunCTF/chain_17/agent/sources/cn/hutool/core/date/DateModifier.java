package cn.hutool.core.date;

import cn.hutool.core.util.ArrayUtil;
import java.util.Calendar;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/DateModifier.class */
public class DateModifier {
    private static final int[] IGNORE_FIELDS = {11, 9, 8, 6, 4, 3};

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/DateModifier$ModifyType.class */
    public enum ModifyType {
        TRUNCATE,
        ROUND,
        CEILING
    }

    public static Calendar modify(Calendar calendar, int dateField, ModifyType modifyType) {
        return modify(calendar, dateField, modifyType, false);
    }

    public static Calendar modify(Calendar calendar, int dateField, ModifyType modifyType, boolean truncateMillisecond) {
        if (9 == dateField) {
            boolean isAM = DateUtil.isAM(calendar);
            switch (modifyType) {
                case TRUNCATE:
                    calendar.set(11, isAM ? 0 : 12);
                    break;
                case CEILING:
                    calendar.set(11, isAM ? 11 : 23);
                    break;
                case ROUND:
                    int min = isAM ? 0 : 12;
                    int max = isAM ? 11 : 23;
                    int href = ((max - min) / 2) + 1;
                    int value = calendar.get(11);
                    calendar.set(11, value < href ? min : max);
                    break;
            }
            return modify(calendar, dateField + 1, modifyType);
        }
        int endField = truncateMillisecond ? 13 : 14;
        for (int i = dateField + 1; i <= endField; i++) {
            if (!ArrayUtil.contains(IGNORE_FIELDS, i)) {
                if (4 == dateField || 3 == dateField) {
                    if (5 == i) {
                    }
                    modifyField(calendar, i, modifyType);
                } else {
                    if (7 == i) {
                    }
                    modifyField(calendar, i, modifyType);
                }
            }
        }
        if (truncateMillisecond) {
            calendar.set(14, 0);
        }
        return calendar;
    }

    private static void modifyField(Calendar calendar, int field, ModifyType modifyType) {
        int href;
        if (10 == field) {
            field = 11;
        }
        switch (modifyType) {
            case TRUNCATE:
                calendar.set(field, DateUtil.getBeginValue(calendar, field));
                return;
            case CEILING:
                calendar.set(field, DateUtil.getEndValue(calendar, field));
                return;
            case ROUND:
                int min = DateUtil.getBeginValue(calendar, field);
                int max = DateUtil.getEndValue(calendar, field);
                if (7 == field) {
                    href = (min + 3) % 7;
                } else {
                    href = ((max - min) / 2) + 1;
                }
                int value = calendar.get(field);
                calendar.set(field, value < href ? min : max);
                return;
            default:
                return;
        }
    }
}
