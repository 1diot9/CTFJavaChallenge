package org.springframework.web.servlet.i18n;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Function;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.util.WebUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/i18n/SessionLocaleResolver.class */
public class SessionLocaleResolver extends AbstractLocaleContextResolver {
    public static final String LOCALE_SESSION_ATTRIBUTE_NAME = SessionLocaleResolver.class.getName() + ".LOCALE";
    public static final String TIME_ZONE_SESSION_ATTRIBUTE_NAME = SessionLocaleResolver.class.getName() + ".TIME_ZONE";
    private String localeAttributeName = LOCALE_SESSION_ATTRIBUTE_NAME;
    private String timeZoneAttributeName = TIME_ZONE_SESSION_ATTRIBUTE_NAME;
    private Function<HttpServletRequest, Locale> defaultLocaleFunction = request -> {
        Locale defaultLocale = getDefaultLocale();
        return defaultLocale != null ? defaultLocale : request.getLocale();
    };
    private Function<HttpServletRequest, TimeZone> defaultTimeZoneFunction = request -> {
        return getDefaultTimeZone();
    };

    public void setLocaleAttributeName(String localeAttributeName) {
        this.localeAttributeName = localeAttributeName;
    }

    public void setTimeZoneAttributeName(String timeZoneAttributeName) {
        this.timeZoneAttributeName = timeZoneAttributeName;
    }

    public void setDefaultLocaleFunction(Function<HttpServletRequest, Locale> defaultLocaleFunction) {
        Assert.notNull(defaultLocaleFunction, "defaultLocaleFunction must not be null");
        this.defaultLocaleFunction = defaultLocaleFunction;
    }

    public void setDefaultTimeZoneFunction(Function<HttpServletRequest, TimeZone> defaultTimeZoneFunction) {
        Assert.notNull(defaultTimeZoneFunction, "defaultTimeZoneFunction must not be null");
        this.defaultTimeZoneFunction = defaultTimeZoneFunction;
    }

    @Override // org.springframework.web.servlet.LocaleResolver
    public Locale resolveLocale(HttpServletRequest request) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, this.localeAttributeName);
        if (locale == null) {
            locale = this.defaultLocaleFunction.apply(request);
        }
        return locale;
    }

    @Override // org.springframework.web.servlet.LocaleContextResolver
    public LocaleContext resolveLocaleContext(final HttpServletRequest request) {
        return new TimeZoneAwareLocaleContext() { // from class: org.springframework.web.servlet.i18n.SessionLocaleResolver.1
            @Override // org.springframework.context.i18n.LocaleContext
            public Locale getLocale() {
                Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.this.localeAttributeName);
                if (locale == null) {
                    locale = SessionLocaleResolver.this.defaultLocaleFunction.apply(request);
                }
                return locale;
            }

            @Override // org.springframework.context.i18n.TimeZoneAwareLocaleContext
            @Nullable
            public TimeZone getTimeZone() {
                TimeZone timeZone = (TimeZone) WebUtils.getSessionAttribute(request, SessionLocaleResolver.this.timeZoneAttributeName);
                if (timeZone == null) {
                    timeZone = SessionLocaleResolver.this.defaultTimeZoneFunction.apply(request);
                }
                return timeZone;
            }
        };
    }

    @Override // org.springframework.web.servlet.LocaleContextResolver
    public void setLocaleContext(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable LocaleContext localeContext) {
        Locale locale = null;
        TimeZone timeZone = null;
        if (localeContext != null) {
            locale = localeContext.getLocale();
            if (localeContext instanceof TimeZoneAwareLocaleContext) {
                TimeZoneAwareLocaleContext timeZoneAwareLocaleContext = (TimeZoneAwareLocaleContext) localeContext;
                timeZone = timeZoneAwareLocaleContext.getTimeZone();
            }
        }
        WebUtils.setSessionAttribute(request, this.localeAttributeName, locale);
        WebUtils.setSessionAttribute(request, this.timeZoneAttributeName, timeZone);
    }

    @Deprecated(since = "6.0")
    protected Locale determineDefaultLocale(HttpServletRequest request) {
        return this.defaultLocaleFunction.apply(request);
    }

    @Nullable
    @Deprecated(since = "6.0")
    protected TimeZone determineDefaultTimeZone(HttpServletRequest request) {
        return this.defaultTimeZoneFunction.apply(request);
    }
}
