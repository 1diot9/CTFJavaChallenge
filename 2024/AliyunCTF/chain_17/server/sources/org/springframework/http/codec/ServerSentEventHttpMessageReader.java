package org.springframework.http.codec;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.StringDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/ServerSentEventHttpMessageReader.class */
public class ServerSentEventHttpMessageReader implements HttpMessageReader<Object> {
    private static final ResolvableType STRING_TYPE = ResolvableType.forClass(String.class);

    @Nullable
    private final Decoder<?> decoder;
    private final StringDecoder lineDecoder;

    public ServerSentEventHttpMessageReader() {
        this(null);
    }

    public ServerSentEventHttpMessageReader(@Nullable Decoder<?> decoder) {
        this.lineDecoder = StringDecoder.textPlainOnly();
        this.decoder = decoder;
    }

    @Nullable
    public Decoder<?> getDecoder() {
        return this.decoder;
    }

    public void setMaxInMemorySize(int byteCount) {
        this.lineDecoder.setMaxInMemorySize(byteCount);
    }

    public int getMaxInMemorySize() {
        return this.lineDecoder.getMaxInMemorySize();
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public List<MediaType> getReadableMediaTypes() {
        return Collections.singletonList(MediaType.TEXT_EVENT_STREAM);
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
        return MediaType.TEXT_EVENT_STREAM.includes(mediaType) || isServerSentEvent(elementType);
    }

    private boolean isServerSentEvent(ResolvableType elementType) {
        return ServerSentEvent.class.isAssignableFrom(elementType.toClass());
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public Flux<Object> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        LimitTracker limitTracker = new LimitTracker();
        boolean shouldWrap = isServerSentEvent(elementType);
        ResolvableType valueType = shouldWrap ? elementType.getGeneric(new int[0]) : elementType;
        Flux<String> decode = this.lineDecoder.decode((Publisher<DataBuffer>) message.getBody(), STRING_TYPE, (MimeType) null, hints);
        Objects.requireNonNull(limitTracker);
        return decode.doOnNext(limitTracker::afterLineParsed).bufferUntil((v0) -> {
            return v0.isEmpty();
        }).concatMap(lines -> {
            Object event = buildEvent(lines, valueType, shouldWrap, hints);
            return event != null ? Mono.just(event) : Mono.empty();
        });
    }

    @Nullable
    private Object buildEvent(List<String> lines, ResolvableType valueType, boolean shouldWrap, Map<String, Object> hints) {
        ServerSentEvent.Builder<Object> sseBuilder = shouldWrap ? ServerSentEvent.builder() : null;
        StringBuilder data = null;
        StringBuilder comment = null;
        for (String line : lines) {
            if (line.startsWith("data:")) {
                int length = line.length();
                if (length > 5) {
                    int index = line.charAt(5) != ' ' ? 5 : 6;
                    if (length > index) {
                        data = data != null ? data : new StringBuilder();
                        data.append((CharSequence) line, index, line.length());
                        data.append('\n');
                    }
                }
            } else if (shouldWrap) {
                if (line.startsWith("id:")) {
                    sseBuilder.id(line.substring(3).trim());
                } else if (line.startsWith("event:")) {
                    sseBuilder.event(line.substring(6).trim());
                } else if (line.startsWith("retry:")) {
                    sseBuilder.retry(Duration.ofMillis(Long.parseLong(line.substring(6).trim())));
                } else if (line.startsWith(":")) {
                    comment = comment != null ? comment : new StringBuilder();
                    comment.append(line.substring(1).trim()).append('\n');
                }
            }
        }
        Object decodedData = data != null ? decodeData(data, valueType, hints) : null;
        if (shouldWrap) {
            if (comment != null) {
                sseBuilder.comment(comment.substring(0, comment.length() - 1));
            }
            if (decodedData != null) {
                sseBuilder.data(decodedData);
            }
            return sseBuilder.build();
        }
        return decodedData;
    }

    @Nullable
    private Object decodeData(StringBuilder data, ResolvableType dataType, Map<String, Object> hints) {
        if (String.class == dataType.resolve()) {
            return data.substring(0, data.length() - 1);
        }
        if (this.decoder == null) {
            throw new CodecException("No SSE decoder configured and the data is not String.");
        }
        byte[] bytes = data.toString().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = DefaultDataBufferFactory.sharedInstance.wrap(bytes);
        return this.decoder.decode(buffer, dataType, MediaType.TEXT_EVENT_STREAM, hints);
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public Mono<Object> readMono(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        if (elementType.resolve() == String.class) {
            return this.lineDecoder.decodeToMono(message.getBody(), elementType, null, null).cast(Object.class);
        }
        return Mono.error(new UnsupportedOperationException("ServerSentEventHttpMessageReader only supports reading stream of events as a Flux"));
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/ServerSentEventHttpMessageReader$LimitTracker.class */
    private class LimitTracker {
        private int accumulated = 0;

        private LimitTracker() {
        }

        public void afterLineParsed(String line) {
            if (ServerSentEventHttpMessageReader.this.getMaxInMemorySize() < 0) {
                return;
            }
            if (line.isEmpty()) {
                this.accumulated = 0;
            }
            if (line.length() > Integer.MAX_VALUE - this.accumulated) {
                raiseLimitException();
                return;
            }
            this.accumulated += line.length();
            if (this.accumulated > ServerSentEventHttpMessageReader.this.getMaxInMemorySize()) {
                raiseLimitException();
            }
        }

        private void raiseLimitException() {
            throw new DataBufferLimitException("Exceeded limit on max bytes to buffer : " + ServerSentEventHttpMessageReader.this.getMaxInMemorySize());
        }
    }
}
