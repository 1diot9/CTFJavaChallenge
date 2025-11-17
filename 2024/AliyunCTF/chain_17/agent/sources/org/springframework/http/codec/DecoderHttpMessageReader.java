package org.springframework.http.codec;

import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractDecoder;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/DecoderHttpMessageReader.class */
public class DecoderHttpMessageReader<T> implements HttpMessageReader<T> {
    private final Decoder<T> decoder;
    private final List<MediaType> mediaTypes;

    public DecoderHttpMessageReader(Decoder<T> decoder) {
        Assert.notNull(decoder, "Decoder is required");
        initLogger(decoder);
        this.decoder = decoder;
        this.mediaTypes = MediaType.asMediaTypes(decoder.getDecodableMimeTypes());
    }

    private static void initLogger(Decoder<?> decoder) {
        if (decoder instanceof AbstractDecoder) {
            AbstractDecoder<?> abstractDecoder = (AbstractDecoder) decoder;
            if (decoder.getClass().getName().startsWith("org.springframework.core.codec")) {
                Log logger = HttpLogging.forLog(abstractDecoder.getLogger());
                abstractDecoder.setLogger(logger);
            }
        }
    }

    public Decoder<T> getDecoder() {
        return this.decoder;
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public List<MediaType> getReadableMediaTypes() {
        return this.mediaTypes;
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public List<MediaType> getReadableMediaTypes(ResolvableType elementType) {
        return MediaType.asMediaTypes(this.decoder.getDecodableMimeTypes(elementType));
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
        return this.decoder.canDecode(elementType, mediaType);
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public Flux<T> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        MediaType contentType = getContentType(message);
        Map<String, Object> allHints = Hints.merge(hints, getReadHints(elementType, message));
        return this.decoder.decode((Publisher<DataBuffer>) message.getBody(), elementType, (MimeType) contentType, allHints);
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public Mono<T> readMono(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        MediaType contentType = getContentType(message);
        Map<String, Object> allHints = Hints.merge(hints, getReadHints(elementType, message));
        return this.decoder.decodeToMono(message.getBody(), elementType, contentType, allHints);
    }

    @Nullable
    protected MediaType getContentType(HttpMessage inputMessage) {
        MediaType contentType = inputMessage.getHeaders().getContentType();
        return contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM;
    }

    protected Map<String, Object> getReadHints(ResolvableType elementType, ReactiveHttpInputMessage message) {
        return Hints.none();
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public Flux<T> read(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
        Map<String, Object> allHints = Hints.merge(hints, getReadHints(actualType, elementType, request, response));
        return read(elementType, request, allHints);
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public Mono<T> readMono(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
        Map<String, Object> allHints = Hints.merge(hints, getReadHints(actualType, elementType, request, response));
        return readMono(elementType, request, allHints);
    }

    protected Map<String, Object> getReadHints(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request, ServerHttpResponse response) {
        Decoder<T> decoder = this.decoder;
        if (decoder instanceof HttpMessageDecoder) {
            HttpMessageDecoder<?> httpMessageDecoder = (HttpMessageDecoder) decoder;
            return httpMessageDecoder.getDecodeHints(actualType, elementType, request, response);
        }
        return Hints.none();
    }
}
