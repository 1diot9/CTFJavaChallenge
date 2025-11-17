package org.springframework.http;

import java.time.Duration;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/ResponseCookie.class */
public final class ResponseCookie extends HttpCookie {
    private final Duration maxAge;

    @Nullable
    private final String domain;

    @Nullable
    private final String path;
    private final boolean secure;
    private final boolean httpOnly;

    @Nullable
    private final String sameSite;

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/ResponseCookie$ResponseCookieBuilder.class */
    public interface ResponseCookieBuilder {
        ResponseCookieBuilder value(@Nullable String value);

        ResponseCookieBuilder maxAge(Duration maxAge);

        ResponseCookieBuilder maxAge(long maxAgeSeconds);

        ResponseCookieBuilder path(@Nullable String path);

        ResponseCookieBuilder domain(@Nullable String domain);

        ResponseCookieBuilder secure(boolean secure);

        ResponseCookieBuilder httpOnly(boolean httpOnly);

        ResponseCookieBuilder sameSite(@Nullable String sameSite);

        ResponseCookie build();
    }

    private ResponseCookie(String name, @Nullable String value, Duration maxAge, @Nullable String domain, @Nullable String path, boolean secure, boolean httpOnly, @Nullable String sameSite) {
        super(name, value);
        Assert.notNull(maxAge, "Max age must not be null");
        this.maxAge = maxAge;
        this.domain = domain;
        this.path = path;
        this.secure = secure;
        this.httpOnly = httpOnly;
        this.sameSite = sameSite;
        Rfc6265Utils.validateCookieName(name);
        Rfc6265Utils.validateCookieValue(value);
        Rfc6265Utils.validateDomain(domain);
        Rfc6265Utils.validatePath(path);
    }

    public Duration getMaxAge() {
        return this.maxAge;
    }

    @Nullable
    public String getDomain() {
        return this.domain;
    }

    @Nullable
    public String getPath() {
        return this.path;
    }

    public boolean isSecure() {
        return this.secure;
    }

    public boolean isHttpOnly() {
        return this.httpOnly;
    }

    @Nullable
    public String getSameSite() {
        return this.sameSite;
    }

    public ResponseCookieBuilder mutate() {
        return new DefaultResponseCookieBuilder(getName(), getValue(), false).maxAge(this.maxAge).domain(this.domain).path(this.path).secure(this.secure).httpOnly(this.httpOnly).sameSite(this.sameSite);
    }

    @Override // org.springframework.http.HttpCookie
    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof ResponseCookie) {
                ResponseCookie that = (ResponseCookie) other;
                if (!getName().equalsIgnoreCase(that.getName()) || !ObjectUtils.nullSafeEquals(this.path, that.getPath()) || !ObjectUtils.nullSafeEquals(this.domain, that.getDomain())) {
                }
            }
            return false;
        }
        return true;
    }

    @Override // org.springframework.http.HttpCookie
    public int hashCode() {
        int result = super.hashCode();
        return (31 * ((31 * result) + ObjectUtils.nullSafeHashCode(this.domain))) + ObjectUtils.nullSafeHashCode(this.path);
    }

    @Override // org.springframework.http.HttpCookie
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append('=').append(getValue());
        if (StringUtils.hasText(getPath())) {
            sb.append("; Path=").append(getPath());
        }
        if (StringUtils.hasText(this.domain)) {
            sb.append("; Domain=").append(this.domain);
        }
        if (!this.maxAge.isNegative()) {
            sb.append("; Max-Age=").append(this.maxAge.getSeconds());
            sb.append("; Expires=");
            long millis = this.maxAge.getSeconds() > 0 ? System.currentTimeMillis() + this.maxAge.toMillis() : 0L;
            sb.append(HttpHeaders.formatDate(millis));
        }
        if (this.secure) {
            sb.append("; Secure");
        }
        if (this.httpOnly) {
            sb.append("; HttpOnly");
        }
        if (StringUtils.hasText(this.sameSite)) {
            sb.append("; SameSite=").append(this.sameSite);
        }
        return sb.toString();
    }

    public static ResponseCookieBuilder from(final String name) {
        return new DefaultResponseCookieBuilder(name, null, false);
    }

    public static ResponseCookieBuilder from(final String name, final String value) {
        return new DefaultResponseCookieBuilder(name, value, false);
    }

    public static ResponseCookieBuilder fromClientResponse(final String name, final String value) {
        return new DefaultResponseCookieBuilder(name, value, true);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/ResponseCookie$Rfc6265Utils.class */
    private static class Rfc6265Utils {
        private static final String SEPARATOR_CHARS = "()<>@,;:\\\"/[]?={} ";
        private static final String DOMAIN_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.-";

        private Rfc6265Utils() {
        }

        public static void validateCookieName(String name) {
            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                if (c <= 31 || c == 127) {
                    throw new IllegalArgumentException(name + ": RFC2616 token cannot have control chars");
                }
                if (SEPARATOR_CHARS.indexOf(c) >= 0) {
                    throw new IllegalArgumentException(name + ": RFC2616 token cannot have separator chars such as '" + c + "'");
                }
                if (c >= 128) {
                    throw new IllegalArgumentException(name + ": RFC2616 token can only have US-ASCII: 0x" + Integer.toHexString(c));
                }
            }
        }

        public static void validateCookieValue(@Nullable String value) {
            if (value == null) {
                return;
            }
            int start = 0;
            int end = value.length();
            if (end > 1 && value.charAt(0) == '\"' && value.charAt(end - 1) == '\"') {
                start = 1;
                end--;
            }
            for (int i = start; i < end; i++) {
                char c = value.charAt(i);
                if (c < '!' || c == '\"' || c == ',' || c == ';' || c == '\\' || c == 127) {
                    throw new IllegalArgumentException("RFC2616 cookie value cannot have '" + c + "'");
                }
                if (c >= 128) {
                    throw new IllegalArgumentException("RFC2616 cookie value can only have US-ASCII chars: 0x" + Integer.toHexString(c));
                }
            }
        }

        public static void validateDomain(@Nullable String domain) {
            if (!StringUtils.hasLength(domain)) {
                return;
            }
            int char1 = domain.charAt(0);
            int charN = domain.charAt(domain.length() - 1);
            if (char1 == 45 || charN == 46 || charN == 45) {
                throw new IllegalArgumentException("Invalid first/last char in cookie domain: " + domain);
            }
            int c = -1;
            for (int i = 0; i < domain.length(); i++) {
                int p = c;
                c = domain.charAt(i);
                if (DOMAIN_CHARS.indexOf(c) == -1 || ((p == 46 && (c == 46 || c == 45)) || (p == 45 && c == 46))) {
                    throw new IllegalArgumentException(domain + ": invalid cookie domain char '" + c + "'");
                }
            }
        }

        public static void validatePath(@Nullable String path) {
            if (path == null) {
                return;
            }
            for (int i = 0; i < path.length(); i++) {
                char c = path.charAt(i);
                if (c < ' ' || c > '~' || c == ';') {
                    throw new IllegalArgumentException(path + ": Invalid cookie path char '" + c + "'");
                }
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/ResponseCookie$DefaultResponseCookieBuilder.class */
    private static class DefaultResponseCookieBuilder implements ResponseCookieBuilder {
        private final String name;

        @Nullable
        private String value;
        private final boolean lenient;
        private Duration maxAge = Duration.ofSeconds(-1);

        @Nullable
        private String domain;

        @Nullable
        private String path;
        private boolean secure;
        private boolean httpOnly;

        @Nullable
        private String sameSite;

        public DefaultResponseCookieBuilder(String name, @Nullable String value, boolean lenient) {
            this.name = name;
            this.value = value;
            this.lenient = lenient;
        }

        @Override // org.springframework.http.ResponseCookie.ResponseCookieBuilder
        public ResponseCookieBuilder value(@Nullable String value) {
            this.value = value;
            return this;
        }

        @Override // org.springframework.http.ResponseCookie.ResponseCookieBuilder
        public ResponseCookieBuilder maxAge(Duration maxAge) {
            this.maxAge = maxAge;
            return this;
        }

        @Override // org.springframework.http.ResponseCookie.ResponseCookieBuilder
        public ResponseCookieBuilder maxAge(long maxAgeSeconds) {
            this.maxAge = maxAgeSeconds >= 0 ? Duration.ofSeconds(maxAgeSeconds) : Duration.ofSeconds(-1L);
            return this;
        }

        @Override // org.springframework.http.ResponseCookie.ResponseCookieBuilder
        public ResponseCookieBuilder domain(@Nullable String domain) {
            this.domain = initDomain(domain);
            return this;
        }

        @Nullable
        private String initDomain(@Nullable String domain) {
            if (this.lenient && StringUtils.hasLength(domain)) {
                String str = domain.trim();
                if (str.startsWith("\"") && str.endsWith("\"") && str.substring(1, str.length() - 1).trim().isEmpty()) {
                    return null;
                }
            }
            return domain;
        }

        @Override // org.springframework.http.ResponseCookie.ResponseCookieBuilder
        public ResponseCookieBuilder path(@Nullable String path) {
            this.path = path;
            return this;
        }

        @Override // org.springframework.http.ResponseCookie.ResponseCookieBuilder
        public ResponseCookieBuilder secure(boolean secure) {
            this.secure = secure;
            return this;
        }

        @Override // org.springframework.http.ResponseCookie.ResponseCookieBuilder
        public ResponseCookieBuilder httpOnly(boolean httpOnly) {
            this.httpOnly = httpOnly;
            return this;
        }

        @Override // org.springframework.http.ResponseCookie.ResponseCookieBuilder
        public ResponseCookieBuilder sameSite(@Nullable String sameSite) {
            this.sameSite = sameSite;
            return this;
        }

        @Override // org.springframework.http.ResponseCookie.ResponseCookieBuilder
        public ResponseCookie build() {
            return new ResponseCookie(this.name, this.value, this.maxAge, this.domain, this.path, this.secure, this.httpOnly, this.sameSite);
        }
    }
}
