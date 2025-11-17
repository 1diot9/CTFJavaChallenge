package org.springframework.http.codec.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.EncodingException;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageEncoder;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/json/AbstractJackson2Encoder.class */
public abstract class AbstractJackson2Encoder extends Jackson2CodecSupport implements HttpMessageEncoder<Object> {
    private static final byte[] NEWLINE_SEPARATOR = {10};
    private static final byte[] EMPTY_BYTES = new byte[0];
    private static final Map<String, JsonEncoding> ENCODINGS = CollectionUtils.newHashMap(JsonEncoding.values().length);
    private final List<MediaType> streamingMediaTypes;

    static {
        for (JsonEncoding encoding : JsonEncoding.values()) {
            ENCODINGS.put(encoding.getJavaName(), encoding);
        }
        ENCODINGS.put("US-ASCII", JsonEncoding.UTF8);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractJackson2Encoder(ObjectMapper mapper, MimeType... mimeTypes) {
        super(mapper, mimeTypes);
        this.streamingMediaTypes = new ArrayList(1);
    }

    public void setStreamingMediaTypes(List<MediaType> mediaTypes) {
        this.streamingMediaTypes.clear();
        this.streamingMediaTypes.addAll(mediaTypes);
    }

    @Override // org.springframework.core.codec.Encoder
    public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
        if (!supportsMimeType(mimeType)) {
            return false;
        }
        if (mimeType != null && mimeType.getCharset() != null) {
            Charset charset = mimeType.getCharset();
            if (!ENCODINGS.containsKey(charset.name())) {
                return false;
            }
        }
        ObjectMapper mapper = selectObjectMapper(elementType, mimeType);
        if (mapper == null) {
            return false;
        }
        Class<?> clazz = elementType.toClass();
        if (String.class.isAssignableFrom(elementType.resolve(clazz))) {
            return false;
        }
        if (Object.class == clazz) {
            return true;
        }
        if (!this.logger.isDebugEnabled()) {
            return mapper.canSerialize(clazz);
        }
        AtomicReference<Throwable> causeRef = new AtomicReference<>();
        if (mapper.canSerialize(clazz, causeRef)) {
            return true;
        }
        logWarningIfNecessary(clazz, causeRef.get());
        return false;
    }

    public Flux<DataBuffer> encode(Publisher<?> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        Assert.notNull(inputStream, "'inputStream' must not be null");
        Assert.notNull(bufferFactory, "'bufferFactory' must not be null");
        Assert.notNull(elementType, "'elementType' must not be null");
        return Flux.deferContextual(contextView -> {
            Flux<DataBuffer> dataBufferFlux;
            Map merge = contextView.isEmpty() ? hints : Hints.merge(hints, ContextView.class.getName(), contextView);
            if (inputStream instanceof Mono) {
                return Mono.from(inputStream).map(value -> {
                    return encodeValue(value, bufferFactory, elementType, mimeType, merge);
                }).flux();
            }
            try {
                ObjectMapper mapper = selectObjectMapper(elementType, mimeType);
                if (mapper == null) {
                    throw new IllegalStateException("No ObjectMapper for " + elementType);
                }
                ObjectWriter writer = createObjectWriter(mapper, elementType, mimeType, null, merge);
                ByteArrayBuilder byteBuilder = new ByteArrayBuilder(writer.getFactory()._getBufferRecycler());
                JsonEncoding encoding = getJsonEncoding(mimeType);
                JsonGenerator generator = mapper.getFactory().createGenerator(byteBuilder, encoding);
                SequenceWriter sequenceWriter = writer.writeValues(generator);
                byte[] separator = getStreamingMediaTypeSeparator(mimeType);
                if (separator != null) {
                    dataBufferFlux = Flux.from(inputStream).map(value2 -> {
                        return encodeStreamingValue(value2, bufferFactory, merge, sequenceWriter, byteBuilder, EMPTY_BYTES, separator);
                    });
                } else {
                    JsonArrayJoinHelper helper = new JsonArrayJoinHelper();
                    dataBufferFlux = Flux.from(inputStream).map(value3 -> {
                        byte[] prefix = helper.getPrefix();
                        byte[] delimiter = helper.getDelimiter();
                        DataBuffer dataBuffer = encodeStreamingValue(value3, bufferFactory, merge, sequenceWriter, byteBuilder, delimiter, EMPTY_BYTES);
                        if (prefix.length > 0) {
                            return bufferFactory.join(List.of(bufferFactory.wrap(prefix), dataBuffer));
                        }
                        return dataBuffer;
                    }).switchIfEmpty(Mono.fromCallable(() -> {
                        return bufferFactory.wrap(helper.getPrefix());
                    })).concatWith(Mono.fromCallable(() -> {
                        return bufferFactory.wrap(helper.getSuffix());
                    }));
                }
                return dataBufferFlux.doOnNext(dataBuffer -> {
                    Hints.touchDataBuffer(dataBuffer, merge, this.logger);
                }).doAfterTerminate(() -> {
                    try {
                        generator.close();
                        byteBuilder.release();
                    } catch (IOException ex) {
                        this.logger.error("Could not close Encoder resources", ex);
                    }
                });
            } catch (IOException ex) {
                return Flux.error(ex);
            }
        });
    }

    @Override // org.springframework.core.codec.Encoder
    public DataBuffer encodeValue(Object value, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        Class<?> jsonView = null;
        FilterProvider filters = null;
        if (value instanceof MappingJacksonValue) {
            MappingJacksonValue mappingJacksonValue = (MappingJacksonValue) value;
            value = mappingJacksonValue.getValue();
            valueType = ResolvableType.forInstance(value);
            jsonView = mappingJacksonValue.getSerializationView();
            filters = mappingJacksonValue.getFilters();
        }
        ObjectMapper mapper = selectObjectMapper(valueType, mimeType);
        if (mapper == null) {
            throw new IllegalStateException("No ObjectMapper for " + valueType);
        }
        ObjectWriter writer = createObjectWriter(mapper, valueType, mimeType, jsonView, hints);
        if (filters != null) {
            writer = writer.with(filters);
        }
        ByteArrayBuilder byteBuilder = new ByteArrayBuilder(writer.getFactory()._getBufferRecycler());
        try {
            JsonEncoding encoding = getJsonEncoding(mimeType);
            logValue(hints, value);
            try {
                JsonGenerator generator = mapper.getFactory().createGenerator(byteBuilder, encoding);
                try {
                    writer.writeValue(generator, value);
                    generator.flush();
                    if (generator != null) {
                        generator.close();
                    }
                    byte[] bytes = byteBuilder.toByteArray();
                    DataBuffer buffer = bufferFactory.allocateBuffer(bytes.length);
                    buffer.write(bytes);
                    Hints.touchDataBuffer(buffer, hints, this.logger);
                    byteBuilder.release();
                    return buffer;
                } catch (Throwable th) {
                    if (generator != null) {
                        try {
                            generator.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (InvalidDefinitionException ex) {
                throw new CodecException("Type definition error: " + ex.getType(), ex);
            } catch (JsonProcessingException ex2) {
                throw new EncodingException("JSON encoding error: " + ex2.getOriginalMessage(), ex2);
            } catch (IOException ex3) {
                throw new IllegalStateException("Unexpected I/O error while writing to byte array builder", ex3);
            }
        } catch (Throwable th3) {
            byteBuilder.release();
            throw th3;
        }
    }

    private DataBuffer encodeStreamingValue(Object value, DataBufferFactory bufferFactory, @Nullable Map<String, Object> hints, SequenceWriter sequenceWriter, ByteArrayBuilder byteArrayBuilder, byte[] prefix, byte[] suffix) {
        int offset;
        int length;
        logValue(hints, value);
        try {
            sequenceWriter.write(value);
            sequenceWriter.flush();
            byte[] bytes = byteArrayBuilder.toByteArray();
            byteArrayBuilder.reset();
            if (bytes.length > 0 && bytes[0] == 32) {
                offset = 1;
                length = bytes.length - 1;
            } else {
                offset = 0;
                length = bytes.length;
            }
            DataBuffer buffer = bufferFactory.allocateBuffer(length + prefix.length + suffix.length);
            if (prefix.length != 0) {
                buffer.write(prefix);
            }
            buffer.write(bytes, offset, length);
            if (suffix.length != 0) {
                buffer.write(suffix);
            }
            Hints.touchDataBuffer(buffer, hints, this.logger);
            return buffer;
        } catch (InvalidDefinitionException ex) {
            throw new CodecException("Type definition error: " + ex.getType(), ex);
        } catch (JsonProcessingException ex2) {
            throw new EncodingException("JSON encoding error: " + ex2.getOriginalMessage(), ex2);
        } catch (IOException ex3) {
            throw new IllegalStateException("Unexpected I/O error while writing to byte array builder", ex3);
        }
    }

    private void logValue(@Nullable Map<String, Object> hints, Object value) {
        if (!Hints.isLoggingSuppressed(hints)) {
            LogFormatUtils.traceDebug(this.logger, traceOn -> {
                String formatted = LogFormatUtils.formatValue(value, !traceOn.booleanValue());
                return Hints.getLogPrefix(hints) + "Encoding [" + formatted + "]";
            });
        }
    }

    private ObjectWriter createObjectWriter(ObjectMapper mapper, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Class<?> jsonView, @Nullable Map<String, Object> hints) {
        JavaType javaType = getJavaType(valueType.getType(), null);
        if (jsonView == null && hints != null) {
            jsonView = (Class) hints.get(Jackson2CodecSupport.JSON_VIEW_HINT);
        }
        ObjectWriter writer = jsonView != null ? mapper.writerWithView(jsonView) : mapper.writer();
        if (javaType.isContainerType()) {
            writer = writer.forType(javaType);
        }
        return customizeWriter(writer, mimeType, valueType, hints);
    }

    protected ObjectWriter customizeWriter(ObjectWriter writer, @Nullable MimeType mimeType, ResolvableType elementType, @Nullable Map<String, Object> hints) {
        return writer;
    }

    @Nullable
    protected byte[] getStreamingMediaTypeSeparator(@Nullable MimeType mimeType) {
        for (MediaType streamingMediaType : this.streamingMediaTypes) {
            if (streamingMediaType.isCompatibleWith(mimeType)) {
                return NEWLINE_SEPARATOR;
            }
        }
        return null;
    }

    protected JsonEncoding getJsonEncoding(@Nullable MimeType mimeType) {
        if (mimeType != null && mimeType.getCharset() != null) {
            Charset charset = mimeType.getCharset();
            JsonEncoding result = ENCODINGS.get(charset.name());
            if (result != null) {
                return result;
            }
        }
        return JsonEncoding.UTF8;
    }

    @Override // org.springframework.core.codec.Encoder
    public List<MimeType> getEncodableMimeTypes() {
        return getMimeTypes();
    }

    @Override // org.springframework.core.codec.Encoder
    public List<MimeType> getEncodableMimeTypes(ResolvableType elementType) {
        return getMimeTypes(elementType);
    }

    @Override // org.springframework.http.codec.HttpMessageEncoder
    public List<MediaType> getStreamingMediaTypes() {
        return Collections.unmodifiableList(this.streamingMediaTypes);
    }

    @Override // org.springframework.http.codec.HttpMessageEncoder
    public Map<String, Object> getEncodeHints(@Nullable ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response) {
        return actualType != null ? getHints(actualType) : Hints.none();
    }

    @Override // org.springframework.http.codec.json.Jackson2CodecSupport
    protected <A extends Annotation> A getAnnotation(MethodParameter methodParameter, Class<A> cls) {
        return (A) methodParameter.getMethodAnnotation(cls);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/json/AbstractJackson2Encoder$JsonArrayJoinHelper.class */
    public static class JsonArrayJoinHelper {
        private static final byte[] COMMA_SEPARATOR = {44};
        private static final byte[] OPEN_BRACKET = {91};
        private static final byte[] CLOSE_BRACKET = {93};
        private boolean firstItemEmitted;

        private JsonArrayJoinHelper() {
        }

        public byte[] getDelimiter() {
            if (this.firstItemEmitted) {
                return COMMA_SEPARATOR;
            }
            this.firstItemEmitted = true;
            return AbstractJackson2Encoder.EMPTY_BYTES;
        }

        public byte[] getPrefix() {
            return this.firstItemEmitted ? AbstractJackson2Encoder.EMPTY_BYTES : OPEN_BRACKET;
        }

        public byte[] getSuffix() {
            return CLOSE_BRACKET;
        }
    }
}
