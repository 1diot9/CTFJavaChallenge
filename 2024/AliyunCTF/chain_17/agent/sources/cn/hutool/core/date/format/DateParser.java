package cn.hutool.core.date.format;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/DateParser.class */
public interface DateParser extends DateBasic {
    Date parse(String str) throws ParseException;

    Date parse(String str, ParsePosition parsePosition);

    boolean parse(String str, ParsePosition parsePosition, Calendar calendar);

    default Object parseObject(String source) throws ParseException {
        return parse(source);
    }

    default Object parseObject(String source, ParsePosition pos) {
        return parse(source, pos);
    }
}
