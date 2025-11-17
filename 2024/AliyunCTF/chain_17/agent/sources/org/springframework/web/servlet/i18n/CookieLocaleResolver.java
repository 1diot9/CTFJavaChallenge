package org.springframework.web.servlet.i18n;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Function;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/i18n/CookieLocaleResolver.class */
public class CookieLocaleResolver extends AbstractLocaleContextResolver {
    public static final String LOCALE_REQUEST_ATTRIBUTE_NAME = CookieLocaleResolver.class.getName() + ".LOCALE";
    public static final String TIME_ZONE_REQUEST_ATTRIBUTE_NAME = CookieLocaleResolver.class.getName() + ".TIME_ZONE";
    public static final String DEFAULT_COOKIE_NAME = CookieLocaleResolver.class.getName() + ".LOCALE";
    private static final Log logger = LogFactory.getLog((Class<?>) CookieLocaleResolver.class);
    private ResponseCookie cookie;
    private boolean languageTagCompliant;
    private boolean rejectInvalidCookies;
    private Function<HttpServletRequest, Locale> defaultLocaleFunction;
    private Function<HttpServletRequest, TimeZone> defaultTimeZoneFunction;

    public CookieLocaleResolver(String cookieName) {
        this.languageTagCompliant = true;
        this.rejectInvalidCookies = true;
        this.defaultLocaleFunction = request -> {
            Locale defaultLocale = getDefaultLocale();
            return defaultLocale != null ? defaultLocale : request.getLocale();
        };
        this.defaultTimeZoneFunction = request2 -> {
            return getDefaultTimeZone();
        };
        Assert.notNull(cookieName, "'cookieName' must not be null");
        this.cookie = ResponseCookie.from(cookieName).path("/").sameSite("Lax").build();
    }

    public CookieLocaleResolver() {
        this(DEFAULT_COOKIE_NAME);
    }

    @Deprecated
    public void setCookieName(String cookieName) {
        Assert.notNull(cookieName, "cookieName must not be null");
        this.cookie = ResponseCookie.from(cookieName).maxAge(this.cookie.getMaxAge()).domain(this.cookie.getDomain()).path(this.cookie.getPath()).secure(this.cookie.isSecure()).httpOnly(this.cookie.isHttpOnly()).sameSite(this.cookie.getSameSite()).build();
    }

    public void setCookieMaxAge(Duration cookieMaxAge) {
        Assert.notNull(cookieMaxAge, "'cookieMaxAge' must not be null");
        this.cookie = this.cookie.mutate().maxAge(cookieMaxAge).build();
    }

    @Deprecated
    public void setCookieMaxAge(@Nullable Integer cookieMaxAge) {
        setCookieMaxAge(Duration.ofSeconds(cookieMaxAge != null ? cookieMaxAge.intValue() : -1L));
    }

    public void setCookiePath(@Nullable String cookiePath) {
        this.cookie = this.cookie.mutate().path(cookiePath).build();
    }

    public void setCookieDomain(@Nullable String cookieDomain) {
        this.cookie = this.cookie.mutate().domain(cookieDomain).build();
    }

    public void setCookieSecure(boolean cookieSecure) {
        this.cookie = this.cookie.mutate().secure(cookieSecure).build();
    }

    public void setCookieHttpOnly(boolean cookieHttpOnly) {
        this.cookie = this.cookie.mutate().httpOnly(cookieHttpOnly).build();
    }

    public void setCookieSameSite(String cookieSameSite) {
        Assert.notNull(cookieSameSite, "cookieSameSite must not be null");
        this.cookie = this.cookie.mutate().sameSite(cookieSameSite).build();
    }

    public void setLanguageTagCompliant(boolean languageTagCompliant) {
        this.languageTagCompliant = languageTagCompliant;
    }

    public boolean isLanguageTagCompliant() {
        return this.languageTagCompliant;
    }

    public void setRejectInvalidCookies(boolean rejectInvalidCookies) {
        this.rejectInvalidCookies = rejectInvalidCookies;
    }

    public boolean isRejectInvalidCookies() {
        return this.rejectInvalidCookies;
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
        parseLocaleCookieIfNecessary(request);
        return (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
    }

    @Override // org.springframework.web.servlet.LocaleContextResolver
    public LocaleContext resolveLocaleContext(final HttpServletRequest request) {
        parseLocaleCookieIfNecessary(request);
        return new TimeZoneAwareLocaleContext() { // from class: org.springframework.web.servlet.i18n.CookieLocaleResolver.1
            @Override // org.springframework.context.i18n.LocaleContext
            @Nullable
            public Locale getLocale() {
                return (Locale) request.getAttribute(CookieLocaleResolver.LOCALE_REQUEST_ATTRIBUTE_NAME);
            }

            @Override // org.springframework.context.i18n.TimeZoneAwareLocaleContext
            @Nullable
            public TimeZone getTimeZone() {
                return (TimeZone) request.getAttribute(CookieLocaleResolver.TIME_ZONE_REQUEST_ATTRIBUTE_NAME);
            }
        };
    }

    private void parseLocaleCookieIfNecessary(HttpServletRequest request) {
        if (request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME) == null) {
            Locale locale = null;
            TimeZone timeZone = null;
            Cookie cookie = WebUtils.getCookie(request, this.cookie.getName());
            if (cookie != null) {
                String value = cookie.getValue();
                String localePart = value;
                String timeZonePart = null;
                int separatorIndex = localePart.indexOf(47);
                if (separatorIndex == -1) {
                    separatorIndex = localePart.indexOf(32);
                }
                if (separatorIndex >= 0) {
                    localePart = value.substring(0, separatorIndex);
                    timeZonePart = value.substring(separatorIndex + 1);
                }
                try {
                    locale = !"-".equals(localePart) ? parseLocaleValue(localePart) : null;
                    if (timeZonePart != null) {
                        timeZone = StringUtils.parseTimeZoneString(timeZonePart);
                    }
                } catch (IllegalArgumentException ex) {
                    if (isRejectInvalidCookies() && request.getAttribute("jakarta.servlet.error.exception") == null) {
                        throw new IllegalStateException("Encountered invalid locale cookie '" + this.cookie.getName() + "': [" + value + "] due to: " + ex.getMessage());
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("Ignoring invalid locale cookie '" + this.cookie.getName() + "': [" + value + "] due to: " + ex.getMessage());
                    }
                }
                if (logger.isTraceEnabled()) {
                    logger.trace("Parsed cookie value [" + cookie.getValue() + "] into locale '" + locale + "'" + (timeZone != null ? " and time zone '" + timeZone.getID() + "'" : ""));
                }
            }
            request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, locale != null ? locale : this.defaultLocaleFunction.apply(request));
            request.setAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME, timeZone != null ? timeZone : this.defaultTimeZoneFunction.apply(request));
        }
    }

    @Override // org.springframework.web.servlet.LocaleContextResolver
    public void setLocaleContext(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable LocaleContext localeContext) {
        Assert.notNull(response, "HttpServletResponse is required for CookieLocaleResolver");
        Locale locale = null;
        TimeZone zone = null;
        if (localeContext != null) {
            locale = localeContext.getLocale();
            if (localeContext instanceof TimeZoneAwareLocaleContext) {
                TimeZoneAwareLocaleContext timeZoneAwareLocaleContext = (TimeZoneAwareLocaleContext) localeContext;
                zone = timeZoneAwareLocaleContext.getTimeZone();
            }
            String value = (locale != null ? toLocaleValue(locale) : "-") + (zone != null ? "/" + zone.getID() : "");
            this.cookie = this.cookie.mutate().value(value).build();
        }
        response.addHeader(HttpHeaders.SET_COOKIE, this.cookie.toString());
        request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, locale != null ? locale : this.defaultLocaleFunction.apply(request));
        request.setAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME, zone != null ? zone : this.defaultTimeZoneFunction.apply(request));
    }

    @Nullable
    protected Locale parseLocaleValue(String localeValue) {
        return StringUtils.parseLocale(localeValue);
    }

    protected String toLocaleValue(Locale locale) {
        return isLanguageTagCompliant() ? locale.toLanguageTag() : locale.toString();
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
