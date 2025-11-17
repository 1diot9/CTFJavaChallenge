package org.springframework.context.support;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/AbstractResourceBasedMessageSource.class */
public abstract class AbstractResourceBasedMessageSource extends AbstractMessageSource {

    @Nullable
    private String defaultEncoding;

    @Nullable
    private Locale defaultLocale;
    private final Set<String> basenameSet = new LinkedHashSet(4);
    private boolean fallbackToSystemLocale = true;
    private long cacheMillis = -1;

    public void setBasename(String basename) {
        setBasenames(basename);
    }

    public void setBasenames(String... basenames) {
        this.basenameSet.clear();
        addBasenames(basenames);
    }

    public void addBasenames(String... basenames) {
        if (!ObjectUtils.isEmpty((Object[]) basenames)) {
            for (String basename : basenames) {
                Assert.hasText(basename, "Basename must not be empty");
                this.basenameSet.add(basename.trim());
            }
        }
    }

    public Set<String> getBasenameSet() {
        return this.basenameSet;
    }

    public void setDefaultEncoding(@Nullable String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public String getDefaultEncoding() {
        return this.defaultEncoding;
    }

    public void setFallbackToSystemLocale(boolean fallbackToSystemLocale) {
        this.fallbackToSystemLocale = fallbackToSystemLocale;
    }

    @Deprecated
    protected boolean isFallbackToSystemLocale() {
        return this.fallbackToSystemLocale;
    }

    public void setDefaultLocale(@Nullable Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Locale getDefaultLocale() {
        if (this.defaultLocale != null) {
            return this.defaultLocale;
        }
        if (this.fallbackToSystemLocale) {
            return Locale.getDefault();
        }
        return null;
    }

    public void setCacheSeconds(int cacheSeconds) {
        this.cacheMillis = cacheSeconds * 1000;
    }

    public void setCacheMillis(long cacheMillis) {
        this.cacheMillis = cacheMillis;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public long getCacheMillis() {
        return this.cacheMillis;
    }
}
