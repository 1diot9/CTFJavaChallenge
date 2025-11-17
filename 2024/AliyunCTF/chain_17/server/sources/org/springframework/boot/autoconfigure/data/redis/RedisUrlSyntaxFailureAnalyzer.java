package org.springframework.boot.autoconfigure.data.redis;

import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/RedisUrlSyntaxFailureAnalyzer.class */
class RedisUrlSyntaxFailureAnalyzer extends AbstractFailureAnalyzer<RedisUrlSyntaxException> {
    RedisUrlSyntaxFailureAnalyzer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, RedisUrlSyntaxException cause) {
        URI uri;
        try {
            uri = new URI(cause.getUrl());
        } catch (URISyntaxException e) {
        }
        if ("redis-sentinel".equals(uri.getScheme())) {
            return new FailureAnalysis(getUnsupportedSchemeDescription(cause.getUrl(), uri.getScheme()), "Use spring.data.redis.sentinel properties instead of spring.data.redis.url to configure Redis sentinel addresses.", cause);
        }
        if ("redis-socket".equals(uri.getScheme())) {
            return new FailureAnalysis(getUnsupportedSchemeDescription(cause.getUrl(), uri.getScheme()), "Configure the appropriate Spring Data Redis connection beans directly instead of setting the property 'spring.data.redis.url'.", cause);
        }
        if (!"redis".equals(uri.getScheme()) && !"rediss".equals(uri.getScheme())) {
            return new FailureAnalysis(getUnsupportedSchemeDescription(cause.getUrl(), uri.getScheme()), "Use the scheme 'redis://' for insecure or 'rediss://' for secure Redis standalone configuration.", cause);
        }
        return new FailureAnalysis(getDefaultDescription(cause.getUrl()), "Review the value of the property 'spring.data.redis.url'.", cause);
    }

    private String getDefaultDescription(String url) {
        return "The URL '" + url + "' is not valid for configuring Spring Data Redis. ";
    }

    private String getUnsupportedSchemeDescription(String url, String scheme) {
        return getDefaultDescription(url) + "The scheme '" + scheme + "' is not supported.";
    }
}
