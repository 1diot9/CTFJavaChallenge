package org.springframework.web.server.session;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/session/CookieWebSessionIdResolver.class */
public class CookieWebSessionIdResolver implements WebSessionIdResolver {
    private String cookieName = HeaderWebSessionIdResolver.DEFAULT_HEADER_NAME;
    private Duration cookieMaxAge = Duration.ofSeconds(-1);

    @Nullable
    private Consumer<ResponseCookie.ResponseCookieBuilder> initializer = null;

    public void setCookieName(String cookieName) {
        Assert.hasText(cookieName, "'cookieName' must not be empty");
        this.cookieName = cookieName;
    }

    public String getCookieName() {
        return this.cookieName;
    }

    public void setCookieMaxAge(Duration maxAge) {
        this.cookieMaxAge = maxAge;
    }

    public Duration getCookieMaxAge() {
        return this.cookieMaxAge;
    }

    public void addCookieInitializer(Consumer<ResponseCookie.ResponseCookieBuilder> initializer) {
        this.initializer = this.initializer != null ? this.initializer.andThen(initializer) : initializer;
    }

    @Override // org.springframework.web.server.session.WebSessionIdResolver
    public List<String> resolveSessionIds(ServerWebExchange exchange) {
        MultiValueMap<String, HttpCookie> cookieMap = exchange.getRequest().getCookies();
        List<HttpCookie> cookies = (List) cookieMap.get(getCookieName());
        if (cookies == null) {
            return Collections.emptyList();
        }
        return cookies.stream().map((v0) -> {
            return v0.getValue();
        }).toList();
    }

    @Override // org.springframework.web.server.session.WebSessionIdResolver
    public void setSessionId(ServerWebExchange exchange, String id) {
        Assert.notNull(id, "'id' is required");
        ResponseCookie cookie = initCookie(exchange, id).build();
        exchange.getResponse().getCookies().set(this.cookieName, cookie);
    }

    @Override // org.springframework.web.server.session.WebSessionIdResolver
    public void expireSession(ServerWebExchange exchange) {
        ResponseCookie cookie = initCookie(exchange, "").maxAge(0L).build();
        exchange.getResponse().getCookies().set(this.cookieName, cookie);
    }

    private ResponseCookie.ResponseCookieBuilder initCookie(ServerWebExchange exchange, String id) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(this.cookieName, id).path(exchange.getRequest().getPath().contextPath().value() + "/").maxAge(getCookieMaxAge()).httpOnly(true).secure("https".equalsIgnoreCase(exchange.getRequest().getURI().getScheme())).sameSite("Lax");
        if (this.initializer != null) {
            this.initializer.accept(builder);
        }
        return builder;
    }
}
