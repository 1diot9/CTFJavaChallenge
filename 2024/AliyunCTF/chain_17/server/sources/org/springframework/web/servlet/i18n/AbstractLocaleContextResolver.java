package org.springframework.web.servlet.i18n;

import java.util.TimeZone;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.LocaleContextResolver;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/i18n/AbstractLocaleContextResolver.class */
public abstract class AbstractLocaleContextResolver extends AbstractLocaleResolver implements LocaleContextResolver {

    @Nullable
    private TimeZone defaultTimeZone;

    public void setDefaultTimeZone(@Nullable TimeZone defaultTimeZone) {
        this.defaultTimeZone = defaultTimeZone;
    }

    @Nullable
    public TimeZone getDefaultTimeZone() {
        return this.defaultTimeZone;
    }
}
