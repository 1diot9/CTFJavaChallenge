package org.springframework.http.client;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.reactivestreams.Publisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.Part;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/MultipartBodyBuilder.class */
public final class MultipartBodyBuilder {
    private final LinkedMultiValueMap<String, DefaultPartBuilder> parts = new LinkedMultiValueMap<>();

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/MultipartBodyBuilder$PartBuilder.class */
    public interface PartBuilder {
        PartBuilder contentType(MediaType contentType);

        PartBuilder filename(String filename);

        PartBuilder header(String headerName, String... headerValues);

        PartBuilder headers(Consumer<HttpHeaders> headersConsumer);
    }

    public PartBuilder part(String name, Object part) {
        return part(name, part, null);
    }

    public PartBuilder part(String name, Object part, @Nullable MediaType contentType) {
        Object partBody;
        Assert.hasLength(name, "'name' must not be empty");
        Assert.notNull(part, "'part' must not be null");
        if (part instanceof Part) {
            Part partObject = (Part) part;
            PartBuilder builder = asyncPart(name, (String) partObject.content(), DataBuffer.class);
            if (!partObject.headers().isEmpty()) {
                builder.headers(headers -> {
                    headers.putAll(partObject.headers());
                    String filename = headers.getContentDisposition().getFilename();
                    headers.setContentDispositionFormData(name, filename);
                });
            }
            if (contentType != null) {
                builder.contentType(contentType);
            }
            return builder;
        }
        if (part instanceof PublisherEntity) {
            PublisherEntity<?, ?> publisherEntity = (PublisherEntity) part;
            PublisherPartBuilder<?, ?> builder2 = new PublisherPartBuilder<>(name, publisherEntity);
            if (contentType != null) {
                builder2.contentType(contentType);
            }
            this.parts.add(name, builder2);
            return builder2;
        }
        HttpHeaders partHeaders = null;
        if (part instanceof HttpEntity) {
            HttpEntity<?> httpEntity = (HttpEntity) part;
            partBody = httpEntity.getBody();
            partHeaders = new HttpHeaders();
            partHeaders.putAll(httpEntity.getHeaders());
        } else {
            partBody = part;
        }
        if (partBody instanceof Publisher) {
            throw new IllegalArgumentException("Use asyncPart(String, Publisher, Class) or asyncPart(String, Publisher, ParameterizedTypeReference) or MultipartBodyBuilder.PublisherEntity");
        }
        DefaultPartBuilder builder3 = new DefaultPartBuilder(name, partHeaders, partBody);
        if (contentType != null) {
            builder3.contentType(contentType);
        }
        this.parts.add(name, builder3);
        return builder3;
    }

    public <T, P extends Publisher<T>> PartBuilder asyncPart(String name, P publisher, Class<T> elementClass) {
        Assert.hasLength(name, "'name' must not be empty");
        Assert.notNull(publisher, "'publisher' must not be null");
        Assert.notNull(elementClass, "'elementClass' must not be null");
        PublisherPartBuilder<T, P> builder = new PublisherPartBuilder<>(name, (HttpHeaders) null, publisher, elementClass);
        this.parts.add(name, builder);
        return builder;
    }

    public <T, P extends Publisher<T>> PartBuilder asyncPart(String name, P publisher, ParameterizedTypeReference<T> typeReference) {
        Assert.hasLength(name, "'name' must not be empty");
        Assert.notNull(publisher, "'publisher' must not be null");
        Assert.notNull(typeReference, "'typeReference' must not be null");
        PublisherPartBuilder<T, P> builder = new PublisherPartBuilder<>(name, (HttpHeaders) null, publisher, typeReference);
        this.parts.add(name, builder);
        return builder;
    }

    public MultiValueMap<String, HttpEntity<?>> build() {
        MultiValueMap<String, HttpEntity<?>> result = new LinkedMultiValueMap<>(this.parts.size());
        for (Map.Entry<String, List<DefaultPartBuilder>> entry : this.parts.entrySet()) {
            for (DefaultPartBuilder builder : entry.getValue()) {
                HttpEntity<?> entity = builder.build();
                result.add(entry.getKey(), entity);
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/MultipartBodyBuilder$DefaultPartBuilder.class */
    public static class DefaultPartBuilder implements PartBuilder {
        private final String name;

        @Nullable
        protected HttpHeaders headers;

        @Nullable
        protected final Object body;

        public DefaultPartBuilder(String name, @Nullable HttpHeaders headers, @Nullable Object body) {
            this.name = name;
            this.headers = headers;
            this.body = body;
        }

        @Override // org.springframework.http.client.MultipartBodyBuilder.PartBuilder
        public PartBuilder contentType(MediaType contentType) {
            initHeadersIfNecessary().setContentType(contentType);
            return this;
        }

        @Override // org.springframework.http.client.MultipartBodyBuilder.PartBuilder
        public PartBuilder filename(String filename) {
            initHeadersIfNecessary().setContentDispositionFormData(this.name, filename);
            return this;
        }

        @Override // org.springframework.http.client.MultipartBodyBuilder.PartBuilder
        public PartBuilder header(String headerName, String... headerValues) {
            initHeadersIfNecessary().addAll(headerName, Arrays.asList(headerValues));
            return this;
        }

        @Override // org.springframework.http.client.MultipartBodyBuilder.PartBuilder
        public PartBuilder headers(Consumer<HttpHeaders> headersConsumer) {
            headersConsumer.accept(initHeadersIfNecessary());
            return this;
        }

        private HttpHeaders initHeadersIfNecessary() {
            if (this.headers == null) {
                this.headers = new HttpHeaders();
            }
            return this.headers;
        }

        public HttpEntity<?> build() {
            return new HttpEntity<>(this.body, this.headers);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/MultipartBodyBuilder$PublisherPartBuilder.class */
    public static class PublisherPartBuilder<S, P extends Publisher<S>> extends DefaultPartBuilder {
        private final ResolvableType resolvableType;

        public PublisherPartBuilder(String name, @Nullable HttpHeaders headers, P body, Class<S> elementClass) {
            super(name, headers, body);
            this.resolvableType = ResolvableType.forClass(elementClass);
        }

        public PublisherPartBuilder(String name, @Nullable HttpHeaders headers, P body, ParameterizedTypeReference<S> typeRef) {
            super(name, headers, body);
            this.resolvableType = ResolvableType.forType((ParameterizedTypeReference<?>) typeRef);
        }

        public PublisherPartBuilder(String name, PublisherEntity<S, P> other) {
            super(name, other.getHeaders(), other.getBody());
            this.resolvableType = other.getResolvableType();
        }

        @Override // org.springframework.http.client.MultipartBodyBuilder.DefaultPartBuilder
        public HttpEntity<?> build() {
            Publisher publisher = (Publisher) this.body;
            Assert.state(publisher != null, "Publisher must not be null");
            return new PublisherEntity(this.headers, publisher, this.resolvableType);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/MultipartBodyBuilder$PublisherEntity.class */
    public static final class PublisherEntity<T, P extends Publisher<T>> extends HttpEntity<P> implements ResolvableTypeProvider {
        private final ResolvableType resolvableType;

        PublisherEntity(@Nullable MultiValueMap<String, String> headers, P publisher, ResolvableType resolvableType) {
            super(publisher, headers);
            Assert.notNull(publisher, "'publisher' must not be null");
            Assert.notNull(resolvableType, "'resolvableType' must not be null");
            this.resolvableType = resolvableType;
        }

        @Override // org.springframework.core.ResolvableTypeProvider
        @NonNull
        public ResolvableType getResolvableType() {
            return this.resolvableType;
        }
    }
}
