package org.springframework.context.i18n;

import java.util.TimeZone;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/i18n/TimeZoneAwareLocaleContext.class */
public interface TimeZoneAwareLocaleContext extends LocaleContext {
    @Nullable
    TimeZone getTimeZone();
}
