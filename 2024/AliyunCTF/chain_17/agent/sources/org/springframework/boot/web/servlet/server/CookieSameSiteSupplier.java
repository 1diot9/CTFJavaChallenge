package org.springframework.boot.web.servlet.server;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import org.springframework.boot.web.server.Cookie;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/server/CookieSameSiteSupplier.class */
public interface CookieSameSiteSupplier {
    Cookie.SameSite getSameSite(jakarta.servlet.http.Cookie cookie);

    default CookieSameSiteSupplier whenHasName(String name) {
        Assert.hasText(name, "Name must not be empty");
        return when(cookie -> {
            return ObjectUtils.nullSafeEquals(cookie.getName(), name);
        });
    }

    default CookieSameSiteSupplier whenHasName(Supplier<String> nameSupplier) {
        Assert.notNull(nameSupplier, "NameSupplier must not be empty");
        return when(cookie -> {
            return ObjectUtils.nullSafeEquals(cookie.getName(), nameSupplier.get());
        });
    }

    default CookieSameSiteSupplier whenHasNameMatching(String regex) {
        Assert.hasText(regex, "Regex must not be empty");
        return whenHasNameMatching(Pattern.compile(regex));
    }

    default CookieSameSiteSupplier whenHasNameMatching(Pattern pattern) {
        Assert.notNull(pattern, "Pattern must not be null");
        return when(cookie -> {
            return pattern.matcher(cookie.getName()).matches();
        });
    }

    default CookieSameSiteSupplier when(Predicate<jakarta.servlet.http.Cookie> predicate) {
        Assert.notNull(predicate, "Predicate must not be null");
        return cookie -> {
            if (predicate.test(cookie)) {
                return getSameSite(cookie);
            }
            return null;
        };
    }

    static CookieSameSiteSupplier ofNone() {
        return of(Cookie.SameSite.NONE);
    }

    static CookieSameSiteSupplier ofLax() {
        return of(Cookie.SameSite.LAX);
    }

    static CookieSameSiteSupplier ofStrict() {
        return of(Cookie.SameSite.STRICT);
    }

    static CookieSameSiteSupplier of(Cookie.SameSite sameSite) {
        Assert.notNull(sameSite, "SameSite must not be null");
        return cookie -> {
            return sameSite;
        };
    }
}
