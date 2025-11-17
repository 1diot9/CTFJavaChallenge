package org.springframework.web.util;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/UriBuilder.class */
public interface UriBuilder {
    UriBuilder scheme(@Nullable String scheme);

    UriBuilder userInfo(@Nullable String userInfo);

    UriBuilder host(@Nullable String host);

    UriBuilder port(int port);

    UriBuilder port(@Nullable String port);

    UriBuilder path(String path);

    UriBuilder replacePath(@Nullable String path);

    UriBuilder pathSegment(String... pathSegments) throws IllegalArgumentException;

    UriBuilder query(String query);

    UriBuilder replaceQuery(@Nullable String query);

    UriBuilder queryParam(String name, Object... values);

    UriBuilder queryParam(String name, @Nullable Collection<?> values);

    UriBuilder queryParamIfPresent(String name, Optional<?> value);

    UriBuilder queryParams(MultiValueMap<String, String> params);

    UriBuilder replaceQueryParam(String name, Object... values);

    UriBuilder replaceQueryParam(String name, @Nullable Collection<?> values);

    UriBuilder replaceQueryParams(MultiValueMap<String, String> params);

    UriBuilder fragment(@Nullable String fragment);

    URI build(Object... uriVariables);

    URI build(Map<String, ?> uriVariables);

    String toUriString();
}
