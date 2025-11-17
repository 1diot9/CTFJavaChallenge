package org.springframework.web.servlet.support;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.jstl.core.Config;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/support/JspAwareRequestContext.class */
public class JspAwareRequestContext extends RequestContext {
    private final PageContext pageContext;

    public JspAwareRequestContext(PageContext pageContext) {
        this(pageContext, null);
    }

    public JspAwareRequestContext(PageContext pageContext, @Nullable Map<String, Object> model) {
        super((HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse(), pageContext.getServletContext(), model);
        this.pageContext = pageContext;
    }

    protected final PageContext getPageContext() {
        return this.pageContext;
    }

    @Override // org.springframework.web.servlet.support.RequestContext
    protected Locale getFallbackLocale() {
        Locale locale;
        if (jstlPresent && (locale = JstlPageLocaleResolver.getJstlLocale(getPageContext())) != null) {
            return locale;
        }
        return getRequest().getLocale();
    }

    @Override // org.springframework.web.servlet.support.RequestContext
    protected TimeZone getFallbackTimeZone() {
        TimeZone timeZone;
        if (jstlPresent && (timeZone = JstlPageLocaleResolver.getJstlTimeZone(getPageContext())) != null) {
            return timeZone;
        }
        return null;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/support/JspAwareRequestContext$JstlPageLocaleResolver.class */
    private static class JstlPageLocaleResolver {
        private JstlPageLocaleResolver() {
        }

        @Nullable
        public static Locale getJstlLocale(PageContext pageContext) {
            Object localeObject = Config.find(pageContext, "jakarta.servlet.jsp.jstl.fmt.locale");
            if (!(localeObject instanceof Locale)) {
                return null;
            }
            Locale locale = (Locale) localeObject;
            return locale;
        }

        @Nullable
        public static TimeZone getJstlTimeZone(PageContext pageContext) {
            Object timeZoneObject = Config.find(pageContext, "jakarta.servlet.jsp.jstl.fmt.timeZone");
            if (!(timeZoneObject instanceof TimeZone)) {
                return null;
            }
            TimeZone timeZone = (TimeZone) timeZoneObject;
            return timeZone;
        }
    }
}
