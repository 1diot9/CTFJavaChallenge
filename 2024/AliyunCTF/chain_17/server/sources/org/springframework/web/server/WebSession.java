package org.springframework.web.server;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/WebSession.class */
public interface WebSession {
    String getId();

    Map<String, Object> getAttributes();

    void start();

    boolean isStarted();

    Mono<Void> changeSessionId();

    Mono<Void> invalidate();

    Mono<Void> save();

    boolean isExpired();

    Instant getCreationTime();

    Instant getLastAccessTime();

    void setMaxIdleTime(Duration maxIdleTime);

    Duration getMaxIdleTime();

    @Nullable
    default <T> T getAttribute(String str) {
        return (T) getAttributes().get(str);
    }

    default <T> T getRequiredAttribute(String str) {
        T t = (T) getAttribute(str);
        Assert.notNull(t, (Supplier<String>) () -> {
            return "Required attribute '" + str + "' is missing.";
        });
        return t;
    }

    default <T> T getAttributeOrDefault(String str, T t) {
        return (T) getAttributes().getOrDefault(str, t);
    }
}
