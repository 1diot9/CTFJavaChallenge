package org.apache.tomcat.util.http;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Queue;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentLinkedQueue;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/ConcurrentDateFormat.class */
public class ConcurrentDateFormat {
    private final String format;
    private final Locale locale;
    private final TimeZone timezone;
    private final Queue<SimpleDateFormat> queue = new ConcurrentLinkedQueue();

    public ConcurrentDateFormat(String format, Locale locale, TimeZone timezone) {
        this.format = format;
        this.locale = locale;
        this.timezone = timezone;
        SimpleDateFormat initial = createInstance();
        this.queue.add(initial);
    }

    public String format(Date date) {
        SimpleDateFormat sdf = this.queue.poll();
        if (sdf == null) {
            sdf = createInstance();
        }
        String result = sdf.format(date);
        this.queue.add(sdf);
        return result;
    }

    public Date parse(String source) throws ParseException {
        SimpleDateFormat sdf = this.queue.poll();
        if (sdf == null) {
            sdf = createInstance();
        }
        Date result = sdf.parse(source);
        sdf.setTimeZone(this.timezone);
        this.queue.add(sdf);
        return result;
    }

    private SimpleDateFormat createInstance() {
        SimpleDateFormat sdf = new SimpleDateFormat(this.format, this.locale);
        sdf.setTimeZone(this.timezone);
        return sdf;
    }
}
