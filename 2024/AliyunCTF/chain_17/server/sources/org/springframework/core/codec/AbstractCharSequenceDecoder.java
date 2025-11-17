package org.springframework.core.codec;

import java.lang.CharSequence;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.LimitedDataBufferList;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/codec/AbstractCharSequenceDecoder.class */
public abstract class AbstractCharSequenceDecoder<T extends CharSequence> extends AbstractDataBufferDecoder<T> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final List<String> DEFAULT_DELIMITERS = List.of("\r\n", "\n");
    private final List<String> delimiters;
    private final boolean stripDelimiter;
    private Charset defaultCharset;
    private final ConcurrentMap<Charset, byte[][]> delimitersCache;

    protected abstract T decodeInternal(DataBuffer dataBuffer, Charset charset);

    @Override // org.springframework.core.codec.Decoder
    public /* bridge */ /* synthetic */ Object decode(DataBuffer dataBuffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map hints) throws DecodingException {
        return decode(dataBuffer, elementType, mimeType, (Map<String, Object>) hints);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractCharSequenceDecoder(List<String> delimiters, boolean stripDelimiter, MimeType... mimeTypes) {
        super(mimeTypes);
        this.defaultCharset = DEFAULT_CHARSET;
        this.delimitersCache = new ConcurrentHashMap();
        Assert.notEmpty(delimiters, "'delimiters' must not be empty");
        this.delimiters = new ArrayList(delimiters);
        this.stripDelimiter = stripDelimiter;
    }

    public void setDefaultCharset(Charset defaultCharset) {
        this.defaultCharset = defaultCharset;
    }

    public Charset getDefaultCharset() {
        return this.defaultCharset;
    }

    @Override // org.springframework.core.codec.AbstractDataBufferDecoder, org.springframework.core.codec.Decoder
    public final Flux<T> decode(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        byte[][] delimiterBytes = getDelimiterBytes(mimeType);
        LimitedDataBufferList chunks = new LimitedDataBufferList(getMaxInMemorySize());
        DataBufferUtils.Matcher matcher = DataBufferUtils.matcher(delimiterBytes);
        return Flux.from(input).concatMapIterable(buffer -> {
            return processDataBuffer(buffer, matcher, chunks);
        }).concatWith(Mono.defer(() -> {
            if (chunks.isEmpty()) {
                return Mono.empty();
            }
            DataBuffer lastBuffer = chunks.get(0).factory().join(chunks);
            chunks.clear();
            return Mono.just(lastBuffer);
        })).doFinally(signalType -> {
            chunks.releaseAndClear();
        }).doOnDiscard(DataBuffer.class, DataBufferUtils::release).map(buffer2 -> {
            return decode(buffer2, elementType, mimeType, (Map<String, Object>) hints);
        });
    }

    private byte[][] getDelimiterBytes(@Nullable MimeType mimeType) {
        return this.delimitersCache.computeIfAbsent(getCharset(mimeType), charset -> {
            ?? r0 = new byte[this.delimiters.size()];
            for (int i = 0; i < this.delimiters.size(); i++) {
                r0[i] = this.delimiters.get(i).getBytes(charset);
            }
            return r0;
        });
    }

    private Collection<DataBuffer> processDataBuffer(DataBuffer buffer, DataBufferUtils.Matcher matcher, LimitedDataBufferList chunks) {
        boolean release = true;
        List<DataBuffer> result = null;
        while (true) {
            try {
                int endIndex = matcher.match(buffer);
                if (endIndex == -1) {
                    chunks.add(buffer);
                    release = false;
                    break;
                }
                DataBuffer split = buffer.split(endIndex + 1);
                if (result == null) {
                    result = new ArrayList<>();
                }
                int delimiterLength = matcher.delimiter().length;
                if (chunks.isEmpty()) {
                    if (this.stripDelimiter) {
                        split.writePosition(split.writePosition() - delimiterLength);
                    }
                    result.add(split);
                } else {
                    chunks.add(split);
                    DataBuffer joined = buffer.factory().join(chunks);
                    if (this.stripDelimiter) {
                        joined.writePosition(joined.writePosition() - delimiterLength);
                    }
                    result.add(joined);
                    chunks.clear();
                }
                if (buffer.readableByteCount() <= 0) {
                    break;
                }
            } finally {
                if (release) {
                    DataBufferUtils.release(buffer);
                }
            }
        }
        return result != null ? result : Collections.emptyList();
    }

    @Override // org.springframework.core.codec.Decoder
    public final T decode(DataBuffer dataBuffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        Charset charset = getCharset(mimeType);
        T value = decodeInternal(dataBuffer, charset);
        DataBufferUtils.release(dataBuffer);
        LogFormatUtils.traceDebug(this.logger, traceOn -> {
            String formatted = LogFormatUtils.formatValue(value, !traceOn.booleanValue());
            return Hints.getLogPrefix(hints) + "Decoded " + formatted;
        });
        return value;
    }

    private Charset getCharset(@Nullable MimeType mimeType) {
        Charset charset;
        if (mimeType != null && (charset = mimeType.getCharset()) != null) {
            return charset;
        }
        return getDefaultCharset();
    }
}
