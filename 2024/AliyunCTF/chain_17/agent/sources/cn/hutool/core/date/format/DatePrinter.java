package cn.hutool.core.date.format;

import java.util.Calendar;
import java.util.Date;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/DatePrinter.class */
public interface DatePrinter extends DateBasic {
    String format(long j);

    String format(Date date);

    String format(Calendar calendar);

    <B extends Appendable> B format(long j, B b);

    <B extends Appendable> B format(Date date, B b);

    <B extends Appendable> B format(Calendar calendar, B b);
}
