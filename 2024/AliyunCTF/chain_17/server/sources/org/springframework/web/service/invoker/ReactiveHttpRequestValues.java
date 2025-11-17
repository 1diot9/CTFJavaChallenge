package org.springframework.web.service.invoker;

import java.net.URI;
import java.util.List;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.service.invoker.HttpRequestValues;
import org.springframework.web.util.UriBuilderFactory;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/ReactiveHttpRequestValues.class */
public final class ReactiveHttpRequestValues extends HttpRequestValues {

    @Nullable
    private final Publisher<?> body;

    @Nullable
    private final ParameterizedTypeReference<?> bodyElementType;

    private ReactiveHttpRequestValues(@Nullable HttpMethod httpMethod, @Nullable URI uri, @Nullable UriBuilderFactory uriBuilderFactory, @Nullable String uriTemplate, Map<String, String> uriVars, HttpHeaders headers, MultiValueMap<String, String> cookies, Map<String, Object> attributes, @Nullable Object bodyValue, @Nullable Publisher<?> body, @Nullable ParameterizedTypeReference<?> elementType) {
        super(httpMethod, uri, uriBuilderFactory, uriTemplate, uriVars, headers, cookies, attributes, bodyValue);
        this.body = body;
        this.bodyElementType = elementType;
    }

    @Nullable
    public Publisher<?> getBodyPublisher() {
        return this.body;
    }

    @Nullable
    public ParameterizedTypeReference<?> getBodyPublisherElementType() {
        return this.bodyElementType;
    }

    @Override // org.springframework.web.service.invoker.HttpRequestValues
    @Nullable
    public Publisher<?> getBody() {
        return getBodyPublisher();
    }

    @Override // org.springframework.web.service.invoker.HttpRequestValues
    @Nullable
    public ParameterizedTypeReference<?> getBodyElementType() {
        return getBodyPublisherElementType();
    }

    public static Builder builder() {
        return new Builder();
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/ReactiveHttpRequestValues$Builder.class */
    public static final class Builder extends HttpRequestValues.Builder {

        @Nullable
        private MultipartBodyBuilder multipartBuilder;

        @Nullable
        private Publisher<?> body;

        @Nullable
        private ParameterizedTypeReference<?> bodyElementType;

        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        protected /* bridge */ /* synthetic */ HttpRequestValues createRequestValues(@Nullable HttpMethod httpMethod, @Nullable URI uri, @Nullable UriBuilderFactory uriBuilderFactory, @Nullable String uriTemplate, Map uriVars, HttpHeaders headers, MultiValueMap cookies, Map attributes, @Nullable Object bodyValue) {
            return createRequestValues(httpMethod, uri, uriBuilderFactory, uriTemplate, (Map<String, String>) uriVars, headers, (MultiValueMap<String, String>) cookies, (Map<String, Object>) attributes, bodyValue);
        }

        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        public /* bridge */ /* synthetic */ HttpRequestValues.Builder addRequestPart(String name, Publisher publisher, ResolvableType type) {
            return addRequestPart(name, (String) publisher, type);
        }

        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        public /* bridge */ /* synthetic */ HttpRequestValues.Builder setAccept(List acceptableMediaTypes) {
            return setAccept((List<MediaType>) acceptableMediaTypes);
        }

        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        public Builder setHttpMethod(HttpMethod httpMethod) {
            super.setHttpMethod(httpMethod);
            return this;
        }

        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        public Builder setUri(URI uri) {
            super.setUri(uri);
            return this;
        }

        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        public Builder setUriBuilderFactory(@Nullable UriBuilderFactory uriBuilderFactory) {
            super.setUriBuilderFactory(uriBuilderFactory);
            return this;
        }

        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        public Builder setUriTemplate(String uriTemplate) {
            super.setUriTemplate(uriTemplate);
            return this;
        }

        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        public Builder setUriVariable(String name, String value) {
            super.setUriVariable(name, value);
            return this;
        }

        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        public Builder setAccept(List<MediaType> acceptableMediaTypes) {
            super.setAccept(acceptableMediaTypes);
            return this;
        }

        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        public Builder setContentType(MediaType contentType) {
            super.setContentType(contentType);
            return this;
        }

        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        public Builder addHeader(String headerName, String... headerValues) {
            super.addHeader(headerName, headerValues);
            return this;
        }

        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        public Builder addCookie(String name, String... values) {
            super.addCookie(name, values);
            return this;
        }

        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        public Builder addRequestParameter(String name, String... values) {
            super.addRequestParameter(name, values);
            return this;
        }

        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        public Builder addAttribute(String name, Object value) {
            super.addAttribute(name, value);
            return this;
        }

        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        public Builder addRequestPart(String name, Object part) {
            this.multipartBuilder = this.multipartBuilder != null ? this.multipartBuilder : new MultipartBodyBuilder();
            this.multipartBuilder.part(name, part);
            return this;
        }

        public <T, P extends Publisher<T>> Builder addRequestPartPublisher(String name, P publisher, ParameterizedTypeReference<T> elementTye) {
            this.multipartBuilder = this.multipartBuilder != null ? this.multipartBuilder : new MultipartBodyBuilder();
            this.multipartBuilder.asyncPart(name, (String) publisher, (ParameterizedTypeReference) elementTye);
            return this;
        }

        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        public <T, P extends Publisher<T>> Builder addRequestPart(String name, P publisher, ResolvableType type) {
            return addRequestPartPublisher(name, publisher, ParameterizedTypeReference.forType(type.getType()));
        }

        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        public void setBodyValue(Object bodyValue) {
            super.setBodyValue(bodyValue);
            this.body = null;
            this.bodyElementType = null;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public <T, P extends Publisher<T>> void setBodyPublisher(P body, ParameterizedTypeReference<T> elementTye) {
            this.body = body;
            this.bodyElementType = elementTye;
            super.setBodyValue(null);
        }

        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        public <T, P extends Publisher<T>> void setBody(P body, ParameterizedTypeReference<T> elementTye) {
            setBodyPublisher(body, elementTye);
        }

        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        public ReactiveHttpRequestValues build() {
            return (ReactiveHttpRequestValues) super.build();
        }

        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        protected boolean hasParts() {
            return this.multipartBuilder != null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        public boolean hasBody() {
            return super.hasBody() || this.body != null;
        }

        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        protected Object buildMultipartBody() {
            Assert.notNull(this.multipartBuilder, "`multipartBuilder` is null, was hasParts() not called?");
            return this.multipartBuilder.build();
        }

        @Override // org.springframework.web.service.invoker.HttpRequestValues.Builder
        protected ReactiveHttpRequestValues createRequestValues(@Nullable HttpMethod httpMethod, @Nullable URI uri, @Nullable UriBuilderFactory uriBuilderFactory, @Nullable String uriTemplate, Map<String, String> uriVars, HttpHeaders headers, MultiValueMap<String, String> cookies, Map<String, Object> attributes, @Nullable Object bodyValue) {
            return new ReactiveHttpRequestValues(httpMethod, uri, uriBuilderFactory, uriTemplate, uriVars, headers, cookies, attributes, bodyValue, this.body, this.bodyElementType);
        }
    }
}
