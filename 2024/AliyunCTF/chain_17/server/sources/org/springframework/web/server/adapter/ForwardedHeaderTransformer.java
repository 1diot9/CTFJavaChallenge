package org.springframework.web.server.adapter;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ForwardedHeaderUtils;
import org.springframework.web.util.UriComponents;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/adapter/ForwardedHeaderTransformer.class */
public class ForwardedHeaderTransformer implements Function<ServerHttpRequest, ServerHttpRequest> {
    static final Set<String> FORWARDED_HEADER_NAMES = Collections.newSetFromMap(new LinkedCaseInsensitiveMap(10, Locale.ENGLISH));
    private boolean removeOnly;

    static {
        FORWARDED_HEADER_NAMES.add("Forwarded");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Host");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Port");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Proto");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Prefix");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Ssl");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-For");
    }

    public void setRemoveOnly(boolean removeOnly) {
        this.removeOnly = removeOnly;
    }

    public boolean isRemoveOnly() {
        return this.removeOnly;
    }

    @Override // java.util.function.Function
    public ServerHttpRequest apply(ServerHttpRequest request) {
        if (hasForwardedHeaders(request)) {
            ServerHttpRequest.Builder builder = request.mutate();
            if (!this.removeOnly) {
                URI originalUri = request.getURI();
                HttpHeaders headers = request.getHeaders();
                URI uri = adaptFromForwardedHeaders(originalUri, headers);
                builder.uri(uri);
                String prefix = getForwardedPrefix(request);
                if (prefix != null) {
                    builder.path(prefix + uri.getRawPath());
                    builder.contextPath(prefix);
                }
                InetSocketAddress remoteAddress = ForwardedHeaderUtils.parseForwardedFor(originalUri, headers, request.getRemoteAddress());
                if (remoteAddress != null) {
                    builder.remoteAddress(remoteAddress);
                }
            }
            removeForwardedHeaders(builder);
            request = builder.build();
        }
        return request;
    }

    private static URI adaptFromForwardedHeaders(URI uri, HttpHeaders headers) {
        UriComponents components = ForwardedHeaderUtils.adaptFromForwardedHeaders(uri, headers).build();
        try {
            return new URI(components.toUriString());
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Could not create URI object: " + ex.getMessage(), ex);
        }
    }

    protected boolean hasForwardedHeaders(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        for (String headerName : FORWARDED_HEADER_NAMES) {
            if (headers.containsKey(headerName)) {
                return true;
            }
        }
        return false;
    }

    private void removeForwardedHeaders(ServerHttpRequest.Builder builder) {
        builder.headers(map -> {
            Set<String> set = FORWARDED_HEADER_NAMES;
            Objects.requireNonNull(map);
            set.forEach((v1) -> {
                r1.remove(v1);
            });
        });
    }

    @Nullable
    private static String getForwardedPrefix(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String header = headers.getFirst("X-Forwarded-Prefix");
        if (header == null) {
            return null;
        }
        StringBuilder prefix = new StringBuilder(header.length());
        String[] rawPrefixes = StringUtils.tokenizeToStringArray(header, ",");
        for (String rawPrefix : rawPrefixes) {
            int endIndex = rawPrefix.length();
            while (endIndex > 1 && rawPrefix.charAt(endIndex - 1) == '/') {
                endIndex--;
            }
            prefix.append(endIndex != rawPrefix.length() ? rawPrefix.substring(0, endIndex) : rawPrefix);
        }
        return prefix.toString();
    }
}
