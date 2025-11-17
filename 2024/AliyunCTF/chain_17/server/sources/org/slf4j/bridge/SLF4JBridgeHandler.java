package org.slf4j.bridge;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

/* loaded from: server.jar:BOOT-INF/lib/jul-to-slf4j-2.0.11.jar:org/slf4j/bridge/SLF4JBridgeHandler.class */
public class SLF4JBridgeHandler extends Handler {
    private static final String UNKNOWN_LOGGER_NAME = "unknown.jul.logger";
    private static final String FQCN = Logger.class.getName();
    private static final int TRACE_LEVEL_THRESHOLD = Level.FINEST.intValue();
    private static final int DEBUG_LEVEL_THRESHOLD = Level.FINE.intValue();
    private static final int INFO_LEVEL_THRESHOLD = Level.INFO.intValue();
    private static final int WARN_LEVEL_THRESHOLD = Level.WARNING.intValue();

    public static void install() {
        LogManager.getLogManager().getLogger("").addHandler(new SLF4JBridgeHandler());
    }

    private static Logger getRootLogger() {
        return LogManager.getLogManager().getLogger("");
    }

    public static void uninstall() throws SecurityException {
        Logger rootLogger = getRootLogger();
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            if (handler instanceof SLF4JBridgeHandler) {
                rootLogger.removeHandler(handler);
            }
        }
    }

    public static boolean isInstalled() {
        Logger rootLogger = getRootLogger();
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            if (handler instanceof SLF4JBridgeHandler) {
                return true;
            }
        }
        return false;
    }

    public static void removeHandlersForRootLogger() {
        Logger rootLogger = getRootLogger();
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }
    }

    @Override // java.util.logging.Handler
    public void close() {
    }

    @Override // java.util.logging.Handler
    public void flush() {
    }

    protected org.slf4j.Logger getSLF4JLogger(LogRecord record) {
        String name = record.getLoggerName();
        if (name == null) {
            name = UNKNOWN_LOGGER_NAME;
        }
        return LoggerFactory.getLogger(name);
    }

    protected void callLocationAwareLogger(LocationAwareLogger lal, LogRecord record) {
        int slf4jLevel;
        int julLevelValue = record.getLevel().intValue();
        if (julLevelValue <= TRACE_LEVEL_THRESHOLD) {
            slf4jLevel = 0;
        } else if (julLevelValue <= DEBUG_LEVEL_THRESHOLD) {
            slf4jLevel = 10;
        } else if (julLevelValue <= INFO_LEVEL_THRESHOLD) {
            slf4jLevel = 20;
        } else if (julLevelValue <= WARN_LEVEL_THRESHOLD) {
            slf4jLevel = 30;
        } else {
            slf4jLevel = 40;
        }
        String i18nMessage = getMessageI18N(record);
        lal.log(null, FQCN, slf4jLevel, i18nMessage, null, record.getThrown());
    }

    protected void callPlainSLF4JLogger(org.slf4j.Logger slf4jLogger, LogRecord record) {
        String i18nMessage = getMessageI18N(record);
        int julLevelValue = record.getLevel().intValue();
        if (julLevelValue <= TRACE_LEVEL_THRESHOLD) {
            slf4jLogger.trace(i18nMessage, record.getThrown());
            return;
        }
        if (julLevelValue <= DEBUG_LEVEL_THRESHOLD) {
            slf4jLogger.debug(i18nMessage, record.getThrown());
            return;
        }
        if (julLevelValue <= INFO_LEVEL_THRESHOLD) {
            slf4jLogger.info(i18nMessage, record.getThrown());
        } else if (julLevelValue <= WARN_LEVEL_THRESHOLD) {
            slf4jLogger.warn(i18nMessage, record.getThrown());
        } else {
            slf4jLogger.error(i18nMessage, record.getThrown());
        }
    }

    private String getMessageI18N(LogRecord record) {
        String message = record.getMessage();
        if (message == null) {
            return null;
        }
        ResourceBundle bundle = record.getResourceBundle();
        if (bundle != null) {
            try {
                message = bundle.getString(message);
            } catch (MissingResourceException e) {
            }
        }
        Object[] params = record.getParameters();
        if (params != null && params.length > 0) {
            try {
                message = MessageFormat.format(message, params);
            } catch (IllegalArgumentException e2) {
                return message;
            }
        }
        return message;
    }

    @Override // java.util.logging.Handler
    public void publish(LogRecord record) {
        if (record == null) {
            return;
        }
        org.slf4j.Logger slf4jLogger = getSLF4JLogger(record);
        if (record.getMessage() == null) {
            record.setMessage("");
        }
        if (slf4jLogger instanceof LocationAwareLogger) {
            callLocationAwareLogger((LocationAwareLogger) slf4jLogger, record);
        } else {
            callPlainSLF4JLogger(slf4jLogger, record);
        }
    }
}
