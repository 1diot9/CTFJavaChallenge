package org.springframework.web.server.i18n;

import java.util.Locale;
import java.util.TimeZone;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/i18n/FixedLocaleContextResolver.class */
public class FixedLocaleContextResolver implements LocaleContextResolver {
    private final Locale locale;

    @Nullable
    private final TimeZone timeZone;

    public FixedLocaleContextResolver() {
        this(Locale.getDefault());
    }

    public FixedLocaleContextResolver(Locale locale) {
        this(locale, null);
    }

    public FixedLocaleContextResolver(Locale locale, @Nullable TimeZone timeZone) {
        Assert.notNull(locale, "Locale must not be null");
        this.locale = locale;
        this.timeZone = timeZone;
    }

    @Override // org.springframework.web.server.i18n.LocaleContextResolver
    public LocaleContext resolveLocaleContext(ServerWebExchange exchange) {
        return new TimeZoneAwareLocaleContext() { // from class: org.springframework.web.server.i18n.FixedLocaleContextResolver.1
            @Override // org.springframework.context.i18n.LocaleContext
            public Locale getLocale() {
                return FixedLocaleContextResolver.this.locale;
            }

            @Override // org.springframework.context.i18n.TimeZoneAwareLocaleContext
            @Nullable
            public TimeZone getTimeZone() {
                return FixedLocaleContextResolver.this.timeZone;
            }
        };
    }

    @Override // org.springframework.web.server.i18n.LocaleContextResolver
    public void setLocaleContext(ServerWebExchange exchange, @Nullable LocaleContext localeContext) {
        throw new UnsupportedOperationException("Cannot change fixed locale - use a different locale context resolution strategy");
    }
}
