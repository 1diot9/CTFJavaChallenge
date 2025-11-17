package ch.qos.logback.classic.jul;

import java.util.logging.Level;
import java.util.logging.Logger;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/jul/JULHelper.class */
public class JULHelper {
    public static final boolean isRegularNonRootLogger(Logger julLogger) {
        return (julLogger == null || julLogger.getName().equals("")) ? false : true;
    }

    public static final boolean isRoot(Logger julLogger) {
        if (julLogger == null) {
            return false;
        }
        return julLogger.getName().equals("");
    }

    public static Level asJULLevel(ch.qos.logback.classic.Level lbLevel) {
        if (lbLevel == null) {
            throw new IllegalArgumentException("Unexpected level [null]");
        }
        switch (lbLevel.levelInt) {
            case Integer.MIN_VALUE:
                return Level.ALL;
            case ch.qos.logback.classic.Level.TRACE_INT /* 5000 */:
                return Level.FINEST;
            case 10000:
                return Level.FINE;
            case 20000:
                return Level.INFO;
            case 30000:
                return Level.WARNING;
            case ch.qos.logback.classic.Level.ERROR_INT /* 40000 */:
                return Level.SEVERE;
            case Integer.MAX_VALUE:
                return Level.OFF;
            default:
                throw new IllegalArgumentException("Unexpected level [" + String.valueOf(lbLevel) + "]");
        }
    }

    public static String asJULLoggerName(String loggerName) {
        if ("ROOT".equals(loggerName)) {
            return "";
        }
        return loggerName;
    }

    public static Logger asJULLogger(String loggerName) {
        String julLoggerName = asJULLoggerName(loggerName);
        return Logger.getLogger(julLoggerName);
    }

    public static Logger asJULLogger(ch.qos.logback.classic.Logger logger) {
        return asJULLogger(logger.getName());
    }
}
