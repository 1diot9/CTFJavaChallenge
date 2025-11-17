package org.springframework.web.servlet.support;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.jstl.core.Config;
import jakarta.servlet.jsp.jstl.fmt.LocalizationContext;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceResourceBundle;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/support/JstlUtils.class */
public abstract class JstlUtils {
    public static MessageSource getJstlAwareMessageSource(@Nullable ServletContext servletContext, MessageSource messageSource) {
        String jstlInitParam;
        if (servletContext != null && (jstlInitParam = servletContext.getInitParameter("jakarta.servlet.jsp.jstl.fmt.localizationContext")) != null) {
            ResourceBundleMessageSource jstlBundleWrapper = new ResourceBundleMessageSource();
            jstlBundleWrapper.setBasename(jstlInitParam);
            jstlBundleWrapper.setParentMessageSource(messageSource);
            return jstlBundleWrapper;
        }
        return messageSource;
    }

    public static void exposeLocalizationContext(HttpServletRequest request, @Nullable MessageSource messageSource) {
        Locale jstlLocale = RequestContextUtils.getLocale(request);
        Config.set(request, "jakarta.servlet.jsp.jstl.fmt.locale", jstlLocale);
        TimeZone timeZone = RequestContextUtils.getTimeZone(request);
        if (timeZone != null) {
            Config.set(request, "jakarta.servlet.jsp.jstl.fmt.timeZone", timeZone);
        }
        if (messageSource != null) {
            LocalizationContext jstlContext = new SpringLocalizationContext(messageSource, request);
            Config.set(request, "jakarta.servlet.jsp.jstl.fmt.localizationContext", jstlContext);
        }
    }

    public static void exposeLocalizationContext(RequestContext requestContext) {
        Config.set(requestContext.getRequest(), "jakarta.servlet.jsp.jstl.fmt.locale", requestContext.getLocale());
        TimeZone timeZone = requestContext.getTimeZone();
        if (timeZone != null) {
            Config.set(requestContext.getRequest(), "jakarta.servlet.jsp.jstl.fmt.timeZone", timeZone);
        }
        MessageSource messageSource = getJstlAwareMessageSource(requestContext.getServletContext(), requestContext.getMessageSource());
        LocalizationContext jstlContext = new SpringLocalizationContext(messageSource, requestContext.getRequest());
        Config.set(requestContext.getRequest(), "jakarta.servlet.jsp.jstl.fmt.localizationContext", jstlContext);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/support/JstlUtils$SpringLocalizationContext.class */
    private static class SpringLocalizationContext extends LocalizationContext {
        private final MessageSource messageSource;
        private final HttpServletRequest request;

        public SpringLocalizationContext(MessageSource messageSource, HttpServletRequest request) {
            this.messageSource = messageSource;
            this.request = request;
        }

        public ResourceBundle getResourceBundle() {
            HttpSession session = this.request.getSession(false);
            if (session != null) {
                Object lcObject = Config.get(session, "jakarta.servlet.jsp.jstl.fmt.localizationContext");
                if (lcObject instanceof LocalizationContext) {
                    LocalizationContext localizationContext = (LocalizationContext) lcObject;
                    ResourceBundle lcBundle = localizationContext.getResourceBundle();
                    return new MessageSourceResourceBundle(this.messageSource, getLocale(), lcBundle);
                }
            }
            return new MessageSourceResourceBundle(this.messageSource, getLocale());
        }

        public Locale getLocale() {
            HttpSession session = this.request.getSession(false);
            if (session != null) {
                Object localeObject = Config.get(session, "jakarta.servlet.jsp.jstl.fmt.locale");
                if (localeObject instanceof Locale) {
                    Locale locale = (Locale) localeObject;
                    return locale;
                }
            }
            return RequestContextUtils.getLocale(this.request);
        }
    }
}
