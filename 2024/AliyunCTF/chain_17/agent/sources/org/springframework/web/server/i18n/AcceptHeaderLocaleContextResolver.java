package org.springframework.web.server.i18n;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/i18n/AcceptHeaderLocaleContextResolver.class */
public class AcceptHeaderLocaleContextResolver implements LocaleContextResolver {
    private final List<Locale> supportedLocales = new ArrayList(4);

    @Nullable
    private Locale defaultLocale;

    public void setSupportedLocales(List<Locale> locales) {
        this.supportedLocales.clear();
        this.supportedLocales.addAll(locales);
    }

    public List<Locale> getSupportedLocales() {
        return this.supportedLocales;
    }

    public void setDefaultLocale(@Nullable Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Nullable
    public Locale getDefaultLocale() {
        return this.defaultLocale;
    }

    @Override // org.springframework.web.server.i18n.LocaleContextResolver
    public LocaleContext resolveLocaleContext(ServerWebExchange exchange) {
        List<Locale> requestLocales = null;
        try {
            requestLocales = exchange.getRequest().getHeaders().getAcceptLanguageAsLocales();
        } catch (IllegalArgumentException e) {
        }
        return new SimpleLocaleContext(resolveSupportedLocale(requestLocales));
    }

    @Nullable
    private Locale resolveSupportedLocale(@Nullable List<Locale> requestLocales) {
        if (CollectionUtils.isEmpty(requestLocales)) {
            return getDefaultLocale();
        }
        List<Locale> supportedLocales = getSupportedLocales();
        if (supportedLocales.isEmpty()) {
            return requestLocales.get(0);
        }
        Locale languageMatch = null;
        for (Locale locale : requestLocales) {
            if (supportedLocales.contains(locale)) {
                if (languageMatch == null || languageMatch.getLanguage().equals(locale.getLanguage())) {
                    return locale;
                }
            } else if (languageMatch == null) {
                Iterator<Locale> it = supportedLocales.iterator();
                while (true) {
                    if (it.hasNext()) {
                        Locale supportedLocale = it.next();
                        if (!StringUtils.hasLength(supportedLocale.getCountry()) && supportedLocale.getLanguage().equals(locale.getLanguage())) {
                            languageMatch = supportedLocale;
                            break;
                        }
                    }
                }
            }
        }
        if (languageMatch != null) {
            return languageMatch;
        }
        Locale defaultLocale = getDefaultLocale();
        return defaultLocale != null ? defaultLocale : requestLocales.get(0);
    }

    @Override // org.springframework.web.server.i18n.LocaleContextResolver
    public void setLocaleContext(ServerWebExchange exchange, @Nullable LocaleContext locale) {
        throw new UnsupportedOperationException("Cannot change HTTP accept header - use a different locale context resolution strategy");
    }
}
