package org.springframework.http;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/CacheControl.class */
public class CacheControl {

    @Nullable
    private Duration maxAge;

    @Nullable
    private Duration staleWhileRevalidate;

    @Nullable
    private Duration staleIfError;

    @Nullable
    private Duration sMaxAge;
    private boolean noCache = false;
    private boolean noStore = false;
    private boolean mustRevalidate = false;
    private boolean noTransform = false;
    private boolean cachePublic = false;
    private boolean cachePrivate = false;
    private boolean proxyRevalidate = false;
    private boolean immutable = false;

    protected CacheControl() {
    }

    public static CacheControl empty() {
        return new CacheControl();
    }

    public static CacheControl maxAge(long maxAge, TimeUnit unit) {
        return maxAge(Duration.ofSeconds(unit.toSeconds(maxAge)));
    }

    public static CacheControl maxAge(Duration maxAge) {
        CacheControl cc = new CacheControl();
        cc.maxAge = maxAge;
        return cc;
    }

    public static CacheControl noCache() {
        CacheControl cc = new CacheControl();
        cc.noCache = true;
        return cc;
    }

    public static CacheControl noStore() {
        CacheControl cc = new CacheControl();
        cc.noStore = true;
        return cc;
    }

    public CacheControl mustRevalidate() {
        this.mustRevalidate = true;
        return this;
    }

    public CacheControl noTransform() {
        this.noTransform = true;
        return this;
    }

    public CacheControl cachePublic() {
        this.cachePublic = true;
        return this;
    }

    public CacheControl cachePrivate() {
        this.cachePrivate = true;
        return this;
    }

    public CacheControl proxyRevalidate() {
        this.proxyRevalidate = true;
        return this;
    }

    public CacheControl sMaxAge(long sMaxAge, TimeUnit unit) {
        return sMaxAge(Duration.ofSeconds(unit.toSeconds(sMaxAge)));
    }

    public CacheControl sMaxAge(Duration sMaxAge) {
        this.sMaxAge = sMaxAge;
        return this;
    }

    public CacheControl staleWhileRevalidate(long staleWhileRevalidate, TimeUnit unit) {
        return staleWhileRevalidate(Duration.ofSeconds(unit.toSeconds(staleWhileRevalidate)));
    }

    public CacheControl staleWhileRevalidate(Duration staleWhileRevalidate) {
        this.staleWhileRevalidate = staleWhileRevalidate;
        return this;
    }

    public CacheControl staleIfError(long staleIfError, TimeUnit unit) {
        return staleIfError(Duration.ofSeconds(unit.toSeconds(staleIfError)));
    }

    public CacheControl staleIfError(Duration staleIfError) {
        this.staleIfError = staleIfError;
        return this;
    }

    public CacheControl immutable() {
        this.immutable = true;
        return this;
    }

    @Nullable
    public String getHeaderValue() {
        String headerValue = toHeaderValue();
        if (StringUtils.hasText(headerValue)) {
            return headerValue;
        }
        return null;
    }

    private String toHeaderValue() {
        StringBuilder headerValue = new StringBuilder();
        if (this.maxAge != null) {
            appendDirective(headerValue, "max-age=" + this.maxAge.getSeconds());
        }
        if (this.noCache) {
            appendDirective(headerValue, "no-cache");
        }
        if (this.noStore) {
            appendDirective(headerValue, "no-store");
        }
        if (this.mustRevalidate) {
            appendDirective(headerValue, "must-revalidate");
        }
        if (this.noTransform) {
            appendDirective(headerValue, "no-transform");
        }
        if (this.cachePublic) {
            appendDirective(headerValue, "public");
        }
        if (this.cachePrivate) {
            appendDirective(headerValue, "private");
        }
        if (this.proxyRevalidate) {
            appendDirective(headerValue, "proxy-revalidate");
        }
        if (this.sMaxAge != null) {
            appendDirective(headerValue, "s-maxage=" + this.sMaxAge.getSeconds());
        }
        if (this.staleIfError != null) {
            appendDirective(headerValue, "stale-if-error=" + this.staleIfError.getSeconds());
        }
        if (this.staleWhileRevalidate != null) {
            appendDirective(headerValue, "stale-while-revalidate=" + this.staleWhileRevalidate.getSeconds());
        }
        if (this.immutable) {
            appendDirective(headerValue, "immutable");
        }
        return headerValue.toString();
    }

    private void appendDirective(StringBuilder builder, String value) {
        if (builder.length() > 0) {
            builder.append(", ");
        }
        builder.append(value);
    }

    public String toString() {
        return "CacheControl [" + toHeaderValue() + "]";
    }
}
