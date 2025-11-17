package org.springframework.ui.context.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.Nullable;
import org.springframework.ui.context.HierarchicalThemeSource;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;

@Deprecated(since = "6.0")
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/ui/context/support/ResourceBundleThemeSource.class */
public class ResourceBundleThemeSource implements HierarchicalThemeSource, BeanClassLoaderAware {

    @Nullable
    private ThemeSource parentThemeSource;

    @Nullable
    private String defaultEncoding;

    @Nullable
    private Boolean fallbackToSystemLocale;

    @Nullable
    private ClassLoader beanClassLoader;
    protected final Log logger = LogFactory.getLog(getClass());
    private String basenamePrefix = "";
    private final Map<String, Theme> themeCache = new ConcurrentHashMap();

    @Override // org.springframework.ui.context.HierarchicalThemeSource
    public void setParentThemeSource(@Nullable ThemeSource parent) {
        this.parentThemeSource = parent;
        synchronized (this.themeCache) {
            for (Theme theme : this.themeCache.values()) {
                initParent(theme);
            }
        }
    }

    @Override // org.springframework.ui.context.HierarchicalThemeSource
    @Nullable
    public ThemeSource getParentThemeSource() {
        return this.parentThemeSource;
    }

    public void setBasenamePrefix(@Nullable String basenamePrefix) {
        this.basenamePrefix = basenamePrefix != null ? basenamePrefix : "";
    }

    public void setDefaultEncoding(@Nullable String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    public void setFallbackToSystemLocale(boolean fallbackToSystemLocale) {
        this.fallbackToSystemLocale = Boolean.valueOf(fallbackToSystemLocale);
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(@Nullable ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    @Override // org.springframework.ui.context.ThemeSource
    @Nullable
    public Theme getTheme(String themeName) {
        Theme theme = this.themeCache.get(themeName);
        if (theme == null) {
            synchronized (this.themeCache) {
                theme = this.themeCache.get(themeName);
                if (theme == null) {
                    String basename = this.basenamePrefix + themeName;
                    MessageSource messageSource = createMessageSource(basename);
                    theme = new SimpleTheme(themeName, messageSource);
                    initParent(theme);
                    this.themeCache.put(themeName, theme);
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Theme created: name '" + themeName + "', basename [" + basename + "]");
                    }
                }
            }
        }
        return theme;
    }

    protected MessageSource createMessageSource(String basename) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(basename);
        if (this.defaultEncoding != null) {
            messageSource.setDefaultEncoding(this.defaultEncoding);
        }
        if (this.fallbackToSystemLocale != null) {
            messageSource.setFallbackToSystemLocale(this.fallbackToSystemLocale.booleanValue());
        }
        if (this.beanClassLoader != null) {
            messageSource.setBeanClassLoader(this.beanClassLoader);
        }
        return messageSource;
    }

    protected void initParent(Theme theme) {
        Theme parentTheme;
        MessageSource messageSource = theme.getMessageSource();
        if (messageSource instanceof HierarchicalMessageSource) {
            HierarchicalMessageSource messageSource2 = (HierarchicalMessageSource) messageSource;
            if (getParentThemeSource() != null && messageSource2.getParentMessageSource() == null && (parentTheme = getParentThemeSource().getTheme(theme.getName())) != null) {
                messageSource2.setParentMessageSource(parentTheme.getMessageSource());
            }
        }
    }
}
