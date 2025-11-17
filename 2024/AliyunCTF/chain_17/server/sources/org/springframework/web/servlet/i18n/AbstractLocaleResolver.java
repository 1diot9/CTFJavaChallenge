package org.springframework.web.servlet.i18n;

import java.util.Locale;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.LocaleResolver;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/i18n/AbstractLocaleResolver.class */
public abstract class AbstractLocaleResolver implements LocaleResolver {

    @Nullable
    private Locale defaultLocale;

    public void setDefaultLocale(@Nullable Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Locale getDefaultLocale() {
        return this.defaultLocale;
    }
}
