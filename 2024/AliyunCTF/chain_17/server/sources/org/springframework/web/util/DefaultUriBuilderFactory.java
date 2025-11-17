package org.springframework.web.util;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/DefaultUriBuilderFactory.class */
public class DefaultUriBuilderFactory implements UriBuilderFactory {

    @Nullable
    private final UriComponentsBuilder baseUri;
    private EncodingMode encodingMode;
    private final Map<String, Object> defaultUriVariables;
    private boolean parsePath;

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/DefaultUriBuilderFactory$EncodingMode.class */
    public enum EncodingMode {
        TEMPLATE_AND_VALUES,
        VALUES_ONLY,
        URI_COMPONENT,
        NONE
    }

    public DefaultUriBuilderFactory() {
        this.encodingMode = EncodingMode.TEMPLATE_AND_VALUES;
        this.defaultUriVariables = new HashMap();
        this.parsePath = true;
        this.baseUri = null;
    }

    public DefaultUriBuilderFactory(String baseUriTemplate) {
        this.encodingMode = EncodingMode.TEMPLATE_AND_VALUES;
        this.defaultUriVariables = new HashMap();
        this.parsePath = true;
        this.baseUri = UriComponentsBuilder.fromUriString(baseUriTemplate);
    }

    public DefaultUriBuilderFactory(UriComponentsBuilder baseUri) {
        this.encodingMode = EncodingMode.TEMPLATE_AND_VALUES;
        this.defaultUriVariables = new HashMap();
        this.parsePath = true;
        this.baseUri = baseUri;
    }

    public void setEncodingMode(EncodingMode encodingMode) {
        this.encodingMode = encodingMode;
    }

    public EncodingMode getEncodingMode() {
        return this.encodingMode;
    }

    public void setDefaultUriVariables(@Nullable Map<String, ?> defaultUriVariables) {
        this.defaultUriVariables.clear();
        if (defaultUriVariables != null) {
            this.defaultUriVariables.putAll(defaultUriVariables);
        }
    }

    public Map<String, ?> getDefaultUriVariables() {
        return Collections.unmodifiableMap(this.defaultUriVariables);
    }

    public void setParsePath(boolean parsePath) {
        this.parsePath = parsePath;
    }

    public boolean shouldParsePath() {
        return this.parsePath;
    }

    @Override // org.springframework.web.util.UriTemplateHandler
    public URI expand(String uriTemplate, Map<String, ?> uriVars) {
        return uriString(uriTemplate).build(uriVars);
    }

    @Override // org.springframework.web.util.UriTemplateHandler
    public URI expand(String uriTemplate, Object... uriVars) {
        return uriString(uriTemplate).build(uriVars);
    }

    @Override // org.springframework.web.util.UriBuilderFactory
    public UriBuilder uriString(String uriTemplate) {
        return new DefaultUriBuilder(uriTemplate);
    }

    @Override // org.springframework.web.util.UriBuilderFactory
    public UriBuilder builder() {
        return new DefaultUriBuilder("");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/DefaultUriBuilderFactory$DefaultUriBuilder.class */
    public class DefaultUriBuilder implements UriBuilder {
        private final UriComponentsBuilder uriComponentsBuilder;

        @Override // org.springframework.web.util.UriBuilder
        public /* bridge */ /* synthetic */ UriBuilder replaceQueryParams(MultiValueMap params) {
            return replaceQueryParams((MultiValueMap<String, String>) params);
        }

        @Override // org.springframework.web.util.UriBuilder
        public /* bridge */ /* synthetic */ UriBuilder replaceQueryParam(String name, @Nullable Collection values) {
            return replaceQueryParam(name, (Collection<?>) values);
        }

        @Override // org.springframework.web.util.UriBuilder
        public /* bridge */ /* synthetic */ UriBuilder queryParams(MultiValueMap params) {
            return queryParams((MultiValueMap<String, String>) params);
        }

        @Override // org.springframework.web.util.UriBuilder
        public /* bridge */ /* synthetic */ UriBuilder queryParamIfPresent(String name, Optional value) {
            return queryParamIfPresent(name, (Optional<?>) value);
        }

        @Override // org.springframework.web.util.UriBuilder
        public /* bridge */ /* synthetic */ UriBuilder queryParam(String name, @Nullable Collection values) {
            return queryParam(name, (Collection<?>) values);
        }

        public DefaultUriBuilder(String uriTemplate) {
            this.uriComponentsBuilder = initUriComponentsBuilder(uriTemplate);
        }

        private UriComponentsBuilder initUriComponentsBuilder(String uriTemplate) {
            UriComponentsBuilder result;
            if (!StringUtils.hasLength(uriTemplate)) {
                result = DefaultUriBuilderFactory.this.baseUri != null ? DefaultUriBuilderFactory.this.baseUri.cloneBuilder() : UriComponentsBuilder.newInstance();
            } else if (DefaultUriBuilderFactory.this.baseUri != null) {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uriTemplate);
                UriComponents uri = builder.build();
                result = uri.getHost() == null ? DefaultUriBuilderFactory.this.baseUri.cloneBuilder().uriComponents(uri) : builder;
            } else {
                result = UriComponentsBuilder.fromUriString(uriTemplate);
            }
            if (DefaultUriBuilderFactory.this.encodingMode.equals(EncodingMode.TEMPLATE_AND_VALUES)) {
                result.encode();
            }
            parsePathIfNecessary(result);
            return result;
        }

        private void parsePathIfNecessary(UriComponentsBuilder result) {
            if (DefaultUriBuilderFactory.this.parsePath && DefaultUriBuilderFactory.this.encodingMode.equals(EncodingMode.URI_COMPONENT)) {
                UriComponents uric = result.build();
                String path = uric.getPath();
                result.replacePath((String) null);
                for (String segment : uric.getPathSegments()) {
                    result.pathSegment(segment);
                }
                if (path != null && path.endsWith("/")) {
                    result.path("/");
                }
            }
        }

        @Override // org.springframework.web.util.UriBuilder
        public DefaultUriBuilder scheme(@Nullable String scheme) {
            this.uriComponentsBuilder.scheme(scheme);
            return this;
        }

        @Override // org.springframework.web.util.UriBuilder
        public DefaultUriBuilder userInfo(@Nullable String userInfo) {
            this.uriComponentsBuilder.userInfo(userInfo);
            return this;
        }

        @Override // org.springframework.web.util.UriBuilder
        public DefaultUriBuilder host(@Nullable String host) {
            this.uriComponentsBuilder.host(host);
            return this;
        }

        @Override // org.springframework.web.util.UriBuilder
        public DefaultUriBuilder port(int port) {
            this.uriComponentsBuilder.port(port);
            return this;
        }

        @Override // org.springframework.web.util.UriBuilder
        public DefaultUriBuilder port(@Nullable String port) {
            this.uriComponentsBuilder.port(port);
            return this;
        }

        @Override // org.springframework.web.util.UriBuilder
        public DefaultUriBuilder path(String path) {
            this.uriComponentsBuilder.path(path);
            return this;
        }

        @Override // org.springframework.web.util.UriBuilder
        public DefaultUriBuilder replacePath(@Nullable String path) {
            this.uriComponentsBuilder.replacePath(path);
            return this;
        }

        @Override // org.springframework.web.util.UriBuilder
        public DefaultUriBuilder pathSegment(String... pathSegments) {
            this.uriComponentsBuilder.pathSegment(pathSegments);
            return this;
        }

        @Override // org.springframework.web.util.UriBuilder
        public DefaultUriBuilder query(String query) {
            this.uriComponentsBuilder.query(query);
            return this;
        }

        @Override // org.springframework.web.util.UriBuilder
        public DefaultUriBuilder replaceQuery(@Nullable String query) {
            this.uriComponentsBuilder.replaceQuery(query);
            return this;
        }

        @Override // org.springframework.web.util.UriBuilder
        public DefaultUriBuilder queryParam(String name, Object... values) {
            this.uriComponentsBuilder.queryParam(name, values);
            return this;
        }

        @Override // org.springframework.web.util.UriBuilder
        public DefaultUriBuilder queryParam(String name, @Nullable Collection<?> values) {
            this.uriComponentsBuilder.queryParam(name, values);
            return this;
        }

        @Override // org.springframework.web.util.UriBuilder
        public DefaultUriBuilder queryParamIfPresent(String name, Optional<?> value) {
            this.uriComponentsBuilder.queryParamIfPresent(name, value);
            return this;
        }

        @Override // org.springframework.web.util.UriBuilder
        public DefaultUriBuilder queryParams(MultiValueMap<String, String> params) {
            this.uriComponentsBuilder.queryParams(params);
            return this;
        }

        @Override // org.springframework.web.util.UriBuilder
        public DefaultUriBuilder replaceQueryParam(String name, Object... values) {
            this.uriComponentsBuilder.replaceQueryParam(name, values);
            return this;
        }

        @Override // org.springframework.web.util.UriBuilder
        public DefaultUriBuilder replaceQueryParam(String name, @Nullable Collection<?> values) {
            this.uriComponentsBuilder.replaceQueryParam(name, values);
            return this;
        }

        @Override // org.springframework.web.util.UriBuilder
        public DefaultUriBuilder replaceQueryParams(MultiValueMap<String, String> params) {
            this.uriComponentsBuilder.replaceQueryParams(params);
            return this;
        }

        @Override // org.springframework.web.util.UriBuilder
        public DefaultUriBuilder fragment(@Nullable String fragment) {
            this.uriComponentsBuilder.fragment(fragment);
            return this;
        }

        @Override // org.springframework.web.util.UriBuilder
        public URI build(Map<String, ?> uriVars) {
            if (!DefaultUriBuilderFactory.this.defaultUriVariables.isEmpty()) {
                HashMap hashMap = new HashMap();
                hashMap.putAll(DefaultUriBuilderFactory.this.defaultUriVariables);
                hashMap.putAll(uriVars);
                uriVars = hashMap;
            }
            if (DefaultUriBuilderFactory.this.encodingMode.equals(EncodingMode.VALUES_ONLY)) {
                uriVars = UriUtils.encodeUriVariables(uriVars);
            }
            UriComponents uric = this.uriComponentsBuilder.build().expand(uriVars);
            return createUri(uric);
        }

        @Override // org.springframework.web.util.UriBuilder
        public URI build(Object... uriVars) {
            if (ObjectUtils.isEmpty(uriVars) && !DefaultUriBuilderFactory.this.defaultUriVariables.isEmpty()) {
                return build(Collections.emptyMap());
            }
            if (DefaultUriBuilderFactory.this.encodingMode.equals(EncodingMode.VALUES_ONLY)) {
                uriVars = UriUtils.encodeUriVariables(uriVars);
            }
            UriComponents uric = this.uriComponentsBuilder.build().expand(uriVars);
            return createUri(uric);
        }

        private URI createUri(UriComponents uric) {
            if (DefaultUriBuilderFactory.this.encodingMode.equals(EncodingMode.URI_COMPONENT)) {
                uric = uric.encode();
            }
            return URI.create(uric.toString());
        }

        @Override // org.springframework.web.util.UriBuilder
        public String toUriString() {
            return this.uriComponentsBuilder.build().toUriString();
        }
    }
}
