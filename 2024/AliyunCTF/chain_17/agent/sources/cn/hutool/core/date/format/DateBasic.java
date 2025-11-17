package cn.hutool.core.date.format;

import java.util.Locale;
import java.util.TimeZone;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/DateBasic.class */
public interface DateBasic {
    String getPattern();

    TimeZone getTimeZone();

    Locale getLocale();
}
