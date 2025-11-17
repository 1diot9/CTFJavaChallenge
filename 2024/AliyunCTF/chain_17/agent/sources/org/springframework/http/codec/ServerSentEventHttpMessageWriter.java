package org.springframework.http.codec;

import cn.hutool.core.text.StrPool;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.Encoder;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpLogging;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/ServerSentEventHttpMessageWriter.class */
public class ServerSentEventHttpMessageWriter implements HttpMessageWriter<Object> {
    private static final MediaType DEFAULT_MEDIA_TYPE = new MediaType("text", "event-stream", StandardCharsets.UTF_8);
    private static final List<MediaType> WRITABLE_MEDIA_TYPES = Collections.singletonList(MediaType.TEXT_EVENT_STREAM);
    private static final Log logger = HttpLogging.forLogName(ServerSentEventHttpMessageWriter.class);

    @Nullable
    private final Encoder<?> encoder;

    public ServerSentEventHttpMessageWriter() {
        this(null);
    }

    public ServerSentEventHttpMessageWriter(@Nullable Encoder<?> encoder) {
        this.encoder = encoder;
    }

    @Nullable
    public Encoder<?> getEncoder() {
        return this.encoder;
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public List<MediaType> getWritableMediaTypes() {
        return WRITABLE_MEDIA_TYPES;
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public boolean canWrite(ResolvableType elementType, @Nullable MediaType mediaType) {
        return mediaType == null || MediaType.TEXT_EVENT_STREAM.includes(mediaType) || ServerSentEvent.class.isAssignableFrom(elementType.toClass());
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public Mono<Void> write(Publisher<? extends Object> input, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage message, Map<String, Object> hints) {
        MediaType mediaType2 = (mediaType == null || mediaType.getCharset() == null) ? DEFAULT_MEDIA_TYPE : mediaType;
        DataBufferFactory bufferFactory = message.bufferFactory();
        message.getHeaders().setContentType(mediaType2);
        return message.writeAndFlushWith(encode(input, elementType, mediaType2, bufferFactory, hints));
    }

    private Flux<Publisher<DataBuffer>> encode(Publisher<?> input, ResolvableType elementType, MediaType mediaType, DataBufferFactory factory, Map<String, Object> hints) {
        ResolvableType dataType = ServerSentEvent.class.isAssignableFrom(elementType.toClass()) ? elementType.getGeneric(new int[0]) : elementType;
        return Flux.from(input).map(element -> {
            ServerSentEvent<?> build;
            Flux<DataBuffer> result;
            if (element instanceof ServerSentEvent) {
                ServerSentEvent<?> serverSentEvent = (ServerSentEvent) element;
                build = serverSentEvent;
            } else {
                build = ServerSentEvent.builder().data(element).build();
            }
            ServerSentEvent<?> sse = build;
            StringBuilder sb = new StringBuilder();
            String id = sse.id();
            String event = sse.event();
            Duration retry = sse.retry();
            String comment = sse.comment();
            Object data = sse.data();
            if (id != null) {
                writeField("id", id, sb);
            }
            if (event != null) {
                writeField("event", event, sb);
            }
            if (retry != null) {
                writeField("retry", Long.valueOf(retry.toMillis()), sb);
            }
            if (comment != null) {
                sb.append(':').append(StringUtils.replace(comment, StrPool.LF, "\n:")).append('\n');
            }
            if (data != null) {
                sb.append("data:");
            }
            if (data == null) {
                result = Flux.just(encodeText(sb + "\n", mediaType, factory));
            } else if (data instanceof String) {
                String text = (String) data;
                result = Flux.just(encodeText(sb + StringUtils.replace(text, StrPool.LF, "\ndata:") + "\n\n", mediaType, factory));
            } else {
                result = encodeEvent(sb, data, dataType, mediaType, factory, hints);
            }
            return result.doOnDiscard(DataBuffer.class, DataBufferUtils::release);
        });
    }

    private <T> Flux<DataBuffer> encodeEvent(StringBuilder eventContent, T data, ResolvableType dataType, MediaType mediaType, DataBufferFactory factory, Map<String, Object> hints) {
        if (this.encoder == null) {
            throw new CodecException("No SSE encoder configured and the data is not String.");
        }
        return Flux.defer(() -> {
            DataBuffer startBuffer = encodeText(eventContent, mediaType, factory);
            DataBuffer endBuffer = encodeText("\n\n", mediaType, factory);
            DataBuffer dataBuffer = this.encoder.encodeValue(data, factory, dataType, mediaType, hints);
            Hints.touchDataBuffer(dataBuffer, hints, logger);
            return Flux.just(new DataBuffer[]{startBuffer, dataBuffer, endBuffer});
        });
    }

    private void writeField(String fieldName, Object fieldValue, StringBuilder sb) {
        sb.append(fieldName).append(':').append(fieldValue).append('\n');
    }

    private DataBuffer encodeText(CharSequence text, MediaType mediaType, DataBufferFactory bufferFactory) {
        Assert.notNull(mediaType.getCharset(), "Expected MediaType with charset");
        byte[] bytes = text.toString().getBytes(mediaType.getCharset());
        return bufferFactory.wrap(bytes);
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public Mono<Void> write(Publisher<? extends Object> input, ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
        Map<String, Object> allHints = Hints.merge(hints, getEncodeHints(actualType, elementType, mediaType, request, response));
        return write(input, elementType, mediaType, response, allHints);
    }

    private Map<String, Object> getEncodeHints(ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response) {
        Encoder<?> encoder = this.encoder;
        if (encoder instanceof HttpMessageEncoder) {
            HttpMessageEncoder<?> httpMessageEncoder = (HttpMessageEncoder) encoder;
            return httpMessageEncoder.getEncodeHints(actualType, elementType, mediaType, request, response);
        }
        return Hints.none();
    }
}
