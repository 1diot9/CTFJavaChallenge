package org.springframework.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/LocaleContextResolver.class */
public interface LocaleContextResolver extends LocaleResolver {
    LocaleContext resolveLocaleContext(HttpServletRequest request);

    void setLocaleContext(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable LocaleContext localeContext);

    @Override // org.springframework.web.servlet.LocaleResolver
    default Locale resolveLocale(HttpServletRequest request) {
        Locale locale = resolveLocaleContext(request).getLocale();
        return locale != null ? locale : request.getLocale();
    }

    @Override // org.springframework.web.servlet.LocaleResolver
    default void setLocale(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Locale locale) {
        setLocaleContext(request, response, locale != null ? new SimpleLocaleContext(locale) : null);
    }
}
