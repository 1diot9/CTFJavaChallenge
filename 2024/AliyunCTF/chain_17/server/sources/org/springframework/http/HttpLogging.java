package org.springframework.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogDelegateFactory;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/HttpLogging.class */
public abstract class HttpLogging {
    private static final Log fallbackLogger = LogFactory.getLog("org.springframework.web." + HttpLogging.class.getSimpleName());

    public static Log forLogName(Class<?> primaryLoggerClass) {
        Log primaryLogger = LogFactory.getLog(primaryLoggerClass);
        return forLog(primaryLogger);
    }

    public static Log forLog(Log primaryLogger) {
        return LogDelegateFactory.getCompositeLog(primaryLogger, fallbackLogger, new Log[0]);
    }
}
