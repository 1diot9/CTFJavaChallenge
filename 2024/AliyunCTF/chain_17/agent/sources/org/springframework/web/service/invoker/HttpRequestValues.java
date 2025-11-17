package org.springframework.web.service.invoker;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/HttpRequestValues.class */
public class HttpRequestValues {
    private static final MultiValueMap<String, String> EMPTY_COOKIES_MAP = CollectionUtils.toMultiValueMap(Collections.emptyMap());

    @Nullable
    private final HttpMethod httpMethod;

    @Nullable
    private final URI uri;

    @Nullable
    private final UriBuilderFactory uriBuilderFactory;

    @Nullable
    private final String uriTemplate;
    private final Map<String, String> uriVariables;
    private final HttpHeaders headers;
    private final MultiValueMap<String, String> cookies;
    private final Map<String, Object> attributes;

    @Nullable
    private final Object bodyValue;

    @Deprecated(since = "6.1", forRemoval = true)
    protected HttpRequestValues(@Nullable HttpMethod httpMethod, @Nullable URI uri, @Nullable String uriTemplate, Map<String, String> uriVariables, HttpHeaders headers, MultiValueMap<String, String> cookies, Map<String, Object> attributes, @Nullable Object bodyValue) {
        this(httpMethod, uri, null, uriTemplate, uriVariables, headers, cookies, attributes, bodyValue);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HttpRequestValues(@Nullable HttpMethod httpMethod, @Nullable URI uri, @Nullable UriBuilderFactory uriBuilderFactory, @Nullable String uriTemplate, Map<String, String> uriVariables, HttpHeaders headers, MultiValueMap<String, String> cookies, Map<String, Object> attributes, @Nullable Object bodyValue) {
        Assert.isTrue((uri == null && uriTemplate == null) ? false : true, "Neither URI nor URI template");
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.uriBuilderFactory = uriBuilderFactory;
        this.uriTemplate = uriTemplate;
        this.uriVariables = uriVariables;
        this.headers = headers;
        this.cookies = cookies;
        this.attributes = attributes;
        this.bodyValue = bodyValue;
    }

    @Nullable
    public HttpMethod getHttpMethod() {
        return this.httpMethod;
    }

    @Nullable
    public URI getUri() {
        return this.uri;
    }

    @Nullable
    public UriBuilderFactory getUriBuilderFactory() {
        return this.uriBuilderFactory;
    }

    @Nullable
    public String getUriTemplate() {
        return this.uriTemplate;
    }

    public Map<String, String> getUriVariables() {
        return this.uriVariables;
    }

    public HttpHeaders getHeaders() {
        return this.headers;
    }

    public MultiValueMap<String, String> getCookies() {
        return this.cookies;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Nullable
    public Object getBodyValue() {
        return this.bodyValue;
    }

    @Nullable
    @Deprecated(since = "6.1", forRemoval = true)
    public Publisher<?> getBody() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Deprecated(since = "6.1", forRemoval = true)
    public ParameterizedTypeReference<?> getBodyElementType() {
        throw new UnsupportedOperationException();
    }

    public static Builder builder() {
        return new Builder();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/HttpRequestValues$Builder.class */
    public static class Builder {

        @Nullable
        private HttpMethod httpMethod;

        @Nullable
        private URI uri;

        @Nullable
        private UriBuilderFactory uriBuilderFactory;

        @Nullable
        private String uriTemplate;

        @Nullable
        private Map<String, String> uriVars;

        @Nullable
        private HttpHeaders headers;

        @Nullable
        private MultiValueMap<String, String> cookies;

        @Nullable
        private MultiValueMap<String, String> requestParams;

        @Nullable
        private MultiValueMap<String, Object> parts;

        @Nullable
        private Map<String, Object> attributes;

        @Nullable
        private Object bodyValue;

        public Builder setHttpMethod(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder setUri(URI uri) {
            this.uri = uri;
            return this;
        }

        public Builder setUriBuilderFactory(@Nullable UriBuilderFactory uriBuilderFactory) {
            this.uriBuilderFactory = uriBuilderFactory;
            return this;
        }

        public Builder setUriTemplate(String uriTemplate) {
            this.uriTemplate = uriTemplate;
            return this;
        }

        public Builder setUriVariable(String name, String value) {
            this.uriVars = this.uriVars != null ? this.uriVars : new LinkedHashMap<>();
            this.uriVars.put(name, value);
            return this;
        }

        public Builder setAccept(List<MediaType> acceptableMediaTypes) {
            initHeaders().setAccept(acceptableMediaTypes);
            return this;
        }

        public Builder setContentType(MediaType contentType) {
            initHeaders().setContentType(contentType);
            return this;
        }

        public Builder addHeader(String headerName, String... headerValues) {
            for (String headerValue : headerValues) {
                initHeaders().add(headerName, headerValue);
            }
            return this;
        }

        private HttpHeaders initHeaders() {
            this.headers = this.headers != null ? this.headers : new HttpHeaders();
            return this.headers;
        }

        public Builder addCookie(String name, String... values) {
            this.cookies = this.cookies != null ? this.cookies : new LinkedMultiValueMap<>();
            for (String value : values) {
                this.cookies.add(name, value);
            }
            return this;
        }

        public Builder addRequestParameter(String name, String... values) {
            this.requestParams = this.requestParams != null ? this.requestParams : new LinkedMultiValueMap<>();
            for (String value : values) {
                this.requestParams.add(name, value);
            }
            return this;
        }

        public Builder addRequestPart(String name, Object part) {
            this.parts = this.parts != null ? this.parts : new LinkedMultiValueMap<>();
            this.parts.add(name, part);
            return this;
        }

        @Deprecated(since = "6.1", forRemoval = true)
        public <T, P extends Publisher<T>> Builder addRequestPart(String name, P publisher, ResolvableType type) {
            throw new UnsupportedOperationException();
        }

        public Builder addAttribute(String name, Object value) {
            this.attributes = this.attributes != null ? this.attributes : new HashMap<>();
            this.attributes.put(name, value);
            return this;
        }

        public void setBodyValue(Object bodyValue) {
            this.bodyValue = bodyValue;
        }

        @Deprecated(since = "6.1", forRemoval = true)
        public <T, P extends Publisher<T>> void setBody(P body, ParameterizedTypeReference<T> elementTye) {
            throw new UnsupportedOperationException();
        }

        public HttpRequestValues build() {
            URI uri = this.uri;
            UriBuilderFactory uriBuilderFactory = this.uriBuilderFactory;
            String uriTemplate = this.uriTemplate != null ? this.uriTemplate : "";
            Map<String, String> uriVars = this.uriVars != null ? new HashMap<>(this.uriVars) : Collections.emptyMap();
            Object bodyValue = this.bodyValue;
            if (hasParts()) {
                Assert.isTrue(!hasBody(), "Expected body or request parts, not both");
                bodyValue = buildMultipartBody();
            }
            if (!CollectionUtils.isEmpty(this.requestParams)) {
                if (hasFormDataContentType()) {
                    Assert.isTrue(!hasParts(), "Request parts not expected for a form data request");
                    Assert.isTrue(!hasBody(), "Body not expected for a form data request");
                    bodyValue = new LinkedMultiValueMap(this.requestParams);
                } else if (uri != null) {
                    uri = UriComponentsBuilder.fromUri(uri).queryParams(UriUtils.encodeQueryParams(this.requestParams)).build(true).toUri();
                } else {
                    uriVars = uriVars.isEmpty() ? new HashMap<>() : uriVars;
                    uriTemplate = appendQueryParams(uriTemplate, uriVars, this.requestParams);
                }
            }
            HttpHeaders headers = HttpHeaders.EMPTY;
            if (this.headers != null) {
                headers = new HttpHeaders();
                headers.putAll(this.headers);
            }
            MultiValueMap<String, String> cookies = this.cookies != null ? new LinkedMultiValueMap<>(this.cookies) : HttpRequestValues.EMPTY_COOKIES_MAP;
            Map<String, Object> attributes = this.attributes != null ? new HashMap<>(this.attributes) : Collections.emptyMap();
            return createRequestValues(this.httpMethod, uri, uriBuilderFactory, uriTemplate, uriVars, headers, cookies, attributes, bodyValue);
        }

        protected boolean hasParts() {
            return this.parts != null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public boolean hasBody() {
            return this.bodyValue != null;
        }

        protected Object buildMultipartBody() {
            Assert.notNull(this.parts, "`parts` is null, was hasParts() not called?");
            return this.parts;
        }

        private boolean hasFormDataContentType() {
            return this.headers != null && MediaType.APPLICATION_FORM_URLENCODED.equals(this.headers.getContentType());
        }

        private String appendQueryParams(String uriTemplate, Map<String, String> uriVars, MultiValueMap<String, String> requestParams) {
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(uriTemplate);
            int i = 0;
            for (Map.Entry<String, String> entry : requestParams.entrySet()) {
                String nameVar = "queryParam" + i;
                uriVars.put(nameVar, entry.getKey());
                for (int j = 0; j < ((List) entry.getValue()).size(); j++) {
                    String valueVar = nameVar + "[" + j + "]";
                    uriVars.put(valueVar, (String) ((List) entry.getValue()).get(j));
                    uriComponentsBuilder.queryParam("{" + nameVar + "}", "{" + valueVar + "}");
                }
                i++;
            }
            return uriComponentsBuilder.build().toUriString();
        }

        @Deprecated(since = "6.1", forRemoval = true)
        protected HttpRequestValues createRequestValues(@Nullable HttpMethod httpMethod, @Nullable URI uri, @Nullable String uriTemplate, Map<String, String> uriVars, HttpHeaders headers, MultiValueMap<String, String> cookies, Map<String, Object> attributes, @Nullable Object bodyValue) {
            return createRequestValues(httpMethod, uri, null, uriTemplate, uriVars, headers, cookies, attributes, bodyValue);
        }

        protected HttpRequestValues createRequestValues(@Nullable HttpMethod httpMethod, @Nullable URI uri, @Nullable UriBuilderFactory uriBuilderFactory, @Nullable String uriTemplate, Map<String, String> uriVars, HttpHeaders headers, MultiValueMap<String, String> cookies, Map<String, Object> attributes, @Nullable Object bodyValue) {
            return new HttpRequestValues(this.httpMethod, uri, uriBuilderFactory, uriTemplate, uriVars, headers, cookies, attributes, bodyValue);
        }
    }
}
