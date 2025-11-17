package org.springframework.format.datetime.standard;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeFormatterUtils.class */
abstract class DateTimeFormatterUtils {
    DateTimeFormatterUtils() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DateTimeFormatter createStrictDateTimeFormatter(String pattern) {
        String patternToUse = StringUtils.replace(pattern, "yy", "uu");
        return DateTimeFormatter.ofPattern(patternToUse).withResolverStyle(ResolverStyle.STRICT);
    }
}
