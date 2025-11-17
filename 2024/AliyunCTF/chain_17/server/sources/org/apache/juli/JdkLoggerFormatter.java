package org.apache.juli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/juli/JdkLoggerFormatter.class */
public class JdkLoggerFormatter extends Formatter {
    public static final int LOG_LEVEL_TRACE = 400;
    public static final int LOG_LEVEL_DEBUG = 500;
    public static final int LOG_LEVEL_INFO = 800;
    public static final int LOG_LEVEL_WARN = 900;
    public static final int LOG_LEVEL_ERROR = 1000;
    public static final int LOG_LEVEL_FATAL = 1000;

    @Override // java.util.logging.Formatter
    public String format(LogRecord record) {
        Throwable t = record.getThrown();
        int level = record.getLevel().intValue();
        String name = record.getLoggerName();
        long time = record.getMillis();
        String message = formatMessage(record);
        if (name.indexOf(46) >= 0) {
            name = name.substring(name.lastIndexOf(46) + 1);
        }
        StringBuilder buf = new StringBuilder();
        buf.append(time);
        for (int i = 0; i < 8 - buf.length(); i++) {
            buf.append(' ');
        }
        switch (level) {
            case 400:
                buf.append(" T ");
                break;
            case 500:
                buf.append(" D ");
                break;
            case LOG_LEVEL_INFO /* 800 */:
                buf.append(" I ");
                break;
            case LOG_LEVEL_WARN /* 900 */:
                buf.append(" W ");
                break;
            case 1000:
                buf.append(" E ");
                break;
            default:
                buf.append("   ");
                break;
        }
        buf.append(name);
        buf.append(' ');
        for (int i2 = 0; i2 < 8 - buf.length(); i2++) {
            buf.append(' ');
        }
        buf.append(message);
        if (t != null) {
            buf.append(System.lineSeparator());
            StringWriter sw = new StringWriter(1024);
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            buf.append(sw.toString());
        }
        buf.append(System.lineSeparator());
        return buf.toString();
    }
}
