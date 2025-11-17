package org.apache.logging.log4j.status;

import java.io.PrintStream;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.simple.SimpleLogger;
import org.apache.logging.log4j.util.Strings;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/status/SimpleLoggerFactory.class */
public final class SimpleLoggerFactory {
    private static final SimpleLoggerFactory INSTANCE = new SimpleLoggerFactory();

    private SimpleLoggerFactory() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SimpleLoggerFactory getInstance() {
        return INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleLogger createSimpleLogger(final String name, final Level level, final MessageFactory messageFactory, final PrintStream stream) {
        String dateFormat = StatusLogger.PROPS.getStringProperty(StatusLogger.STATUS_DATE_FORMAT);
        boolean dateFormatProvided = Strings.isNotBlank(dateFormat);
        return new SimpleLogger(name, level, false, true, dateFormatProvided, false, dateFormat, messageFactory, StatusLogger.PROPS, stream);
    }
}
