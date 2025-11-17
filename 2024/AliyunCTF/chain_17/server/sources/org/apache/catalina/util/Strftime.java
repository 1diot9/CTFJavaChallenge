package org.apache.catalina.util;

import ch.qos.logback.classic.net.SyslogAppender;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.rolling.helper.DateTokenConverter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/util/Strftime.class */
public class Strftime {
    protected static final Properties translate = new Properties();
    protected final SimpleDateFormat simpleDateFormat;

    static {
        translate.put("a", "EEE");
        translate.put("A", "EEEE");
        translate.put("b", "MMM");
        translate.put("B", "MMMM");
        translate.put("c", "EEE MMM d HH:mm:ss yyyy");
        translate.put(DateTokenConverter.CONVERTER_KEY, "dd");
        translate.put("D", "MM/dd/yy");
        translate.put("e", "dd");
        translate.put("F", "yyyy-MM-dd");
        translate.put("g", "yy");
        translate.put("G", "yyyy");
        translate.put("H", "HH");
        translate.put("h", "MMM");
        translate.put("I", "hh");
        translate.put("j", "DDD");
        translate.put("k", "HH");
        translate.put("l", "hh");
        translate.put(ANSIConstants.ESC_END, "MM");
        translate.put("M", "mm");
        translate.put("n", "\n");
        translate.put("p", "a");
        translate.put("P", "a");
        translate.put("r", "hh:mm:ss a");
        translate.put("R", "HH:mm");
        translate.put("S", "ss");
        translate.put("t", SyslogAppender.DEFAULT_STACKTRACE_PATTERN);
        translate.put("T", "HH:mm:ss");
        translate.put("V", "ww");
        translate.put("X", "HH:mm:ss");
        translate.put("x", "MM/dd/yy");
        translate.put("y", "yy");
        translate.put("Y", "yyyy");
        translate.put("Z", "z");
        translate.put("z", "Z");
        translate.put(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL, QuickTargetSourceCreator.PREFIX_THREAD_LOCAL);
    }

    public Strftime(String origFormat, Locale locale) {
        String convertedFormat = convertDateFormat(origFormat);
        this.simpleDateFormat = new SimpleDateFormat(convertedFormat, locale);
    }

    public String format(Date date) {
        return this.simpleDateFormat.format(date);
    }

    public TimeZone getTimeZone() {
        return this.simpleDateFormat.getTimeZone();
    }

    public void setTimeZone(TimeZone timeZone) {
        this.simpleDateFormat.setTimeZone(timeZone);
    }

    protected String convertDateFormat(String pattern) {
        boolean inside = false;
        boolean mark = false;
        boolean modifiedCommand = false;
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == '%' && !mark) {
                mark = true;
            } else if (mark) {
                if (modifiedCommand) {
                    modifiedCommand = false;
                    mark = false;
                } else {
                    inside = translateCommand(buf, pattern, i, inside);
                    if (c == 'O' || c == 'E') {
                        modifiedCommand = true;
                    } else {
                        mark = false;
                    }
                }
            } else {
                if (!inside && c != ' ') {
                    buf.append('\'');
                    inside = true;
                }
                buf.append(c);
            }
        }
        if (buf.length() > 0) {
            char lastChar = buf.charAt(buf.length() - 1);
            if (lastChar != '\'' && inside) {
                buf.append('\'');
            }
        }
        return buf.toString();
    }

    protected String quote(String str, boolean insideQuotes) {
        String retVal = str;
        if (!insideQuotes) {
            retVal = "'" + retVal + "'";
        }
        return retVal;
    }

    protected boolean translateCommand(StringBuilder buf, String pattern, int index, boolean oldInside) {
        char firstChar = pattern.charAt(index);
        boolean newInside = oldInside;
        if (firstChar == 'O' || firstChar == 'E') {
            if (index + 1 < pattern.length()) {
                newInside = translateCommand(buf, pattern, index + 1, oldInside);
            } else {
                buf.append(quote("%" + firstChar, oldInside));
            }
        } else {
            String command = translate.getProperty(String.valueOf(firstChar));
            if (command == null) {
                buf.append(quote("%" + firstChar, oldInside));
            } else {
                if (oldInside) {
                    buf.append('\'');
                }
                buf.append(command);
                newInside = false;
            }
        }
        return newInside;
    }
}
