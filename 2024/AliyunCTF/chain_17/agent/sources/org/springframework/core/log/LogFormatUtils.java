package org.springframework.core.log;

import ch.qos.logback.core.CoreConstants;
import java.util.function.Function;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/log/LogFormatUtils.class */
public abstract class LogFormatUtils {
    private static final Pattern NEWLINE_PATTERN = Pattern.compile("[\n\r]");
    private static final Pattern CONTROL_CHARACTER_PATTERN = Pattern.compile("\\p{Cc}");

    public static String formatValue(@Nullable Object value, boolean limitLength) {
        return formatValue(value, limitLength ? 100 : -1, limitLength);
    }

    public static String formatValue(@Nullable Object value, int maxLength, boolean replaceNewlinesAndControlCharacters) {
        String result;
        if (value == null) {
            return "";
        }
        try {
            result = ObjectUtils.nullSafeToString(value);
        } catch (Throwable ex) {
            result = ObjectUtils.nullSafeToString(ex);
        }
        if (maxLength != -1) {
            result = StringUtils.truncate(result, maxLength);
        }
        if (replaceNewlinesAndControlCharacters) {
            result = CONTROL_CHARACTER_PATTERN.matcher(NEWLINE_PATTERN.matcher(result).replaceAll("<EOL>")).replaceAll(CoreConstants.NA);
        }
        if (value instanceof CharSequence) {
            result = "\"" + result + "\"";
        }
        return result;
    }

    public static void traceDebug(Log logger, Function<Boolean, String> messageFactory) {
        if (logger.isDebugEnabled()) {
            boolean traceEnabled = logger.isTraceEnabled();
            String logMessage = messageFactory.apply(Boolean.valueOf(traceEnabled));
            if (traceEnabled) {
                logger.trace(logMessage);
            } else {
                logger.debug(logMessage);
            }
        }
    }
}
