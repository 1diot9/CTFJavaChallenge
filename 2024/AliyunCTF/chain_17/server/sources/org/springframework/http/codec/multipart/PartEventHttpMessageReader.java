package org.springframework.http.codec.multipart;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.http.codec.multipart.MultipartParser;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/PartEventHttpMessageReader.class */
public class PartEventHttpMessageReader extends LoggingCodecSupport implements HttpMessageReader<PartEvent> {
    private int maxInMemorySize = 262144;
    private int maxHeadersSize = 10240;
    private int maxParts = -1;
    private long maxPartSize = -1;
    private Charset headersCharset = StandardCharsets.UTF_8;

    public int getMaxInMemorySize() {
        return this.maxInMemorySize;
    }

    public void setMaxInMemorySize(int maxInMemorySize) {
        this.maxInMemorySize = maxInMemorySize;
    }

    public void setMaxHeadersSize(int byteCount) {
        this.maxHeadersSize = byteCount;
    }

    public void setMaxParts(int maxParts) {
        this.maxParts = maxParts;
    }

    public void setMaxPartSize(long maxPartSize) {
        this.maxPartSize = maxPartSize;
    }

    public void setHeadersCharset(Charset headersCharset) {
        Assert.notNull(headersCharset, "Charset must not be null");
        this.headersCharset = headersCharset;
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public List<MediaType> getReadableMediaTypes() {
        return Collections.singletonList(MediaType.MULTIPART_FORM_DATA);
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
        return PartEvent.class.equals(elementType.toClass()) && (mediaType == null || MediaType.MULTIPART_FORM_DATA.isCompatibleWith(mediaType));
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public Mono<PartEvent> readMono(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        return Mono.error(new UnsupportedOperationException("Cannot read multipart request body into single PartEvent"));
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public Flux<PartEvent> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        return Flux.defer(() -> {
            byte[] boundary = MultipartUtils.boundary(message, this.headersCharset);
            if (boundary == null) {
                return Flux.error(new DecodingException("No multipart boundary found in Content-Type: \"" + message.getHeaders().getContentType() + "\""));
            }
            Flux<MultipartParser.Token> allPartsTokens = MultipartParser.parse(message.getBody(), boundary, this.maxHeadersSize, this.headersCharset);
            AtomicInteger partCount = new AtomicInteger();
            return allPartsTokens.windowUntil(t -> {
                return t instanceof MultipartParser.HeadersToken;
            }, true).concatMap(partTokens -> {
                if (tooManyParts(partCount)) {
                    return Mono.error(new DecodingException("Too many parts (" + partCount.get() + "/" + this.maxParts + " allowed)"));
                }
                return partTokens.switchOnFirst((signal, flux) -> {
                    if (signal.hasValue()) {
                        MultipartParser.HeadersToken headersToken = (MultipartParser.HeadersToken) signal.get();
                        Assert.state(headersToken != null, "Signal should be headers token");
                        HttpHeaders headers = headersToken.headers();
                        Flux<MultipartParser.BodyToken> bodyTokens = flux.filter(t2 -> {
                            return t2 instanceof MultipartParser.BodyToken;
                        }).cast(MultipartParser.BodyToken.class);
                        return createEvents(headers, bodyTokens);
                    }
                    return flux.cast(PartEvent.class);
                });
            });
        });
    }

    private boolean tooManyParts(AtomicInteger partCount) {
        int count = partCount.incrementAndGet();
        return this.maxParts > 0 && count > this.maxParts;
    }

    private Publisher<? extends PartEvent> createEvents(HttpHeaders headers, Flux<MultipartParser.BodyToken> bodyTokens) {
        int maxSize;
        if (MultipartUtils.isFormField(headers)) {
            Flux<DataBuffer> contents = bodyTokens.map((v0) -> {
                return v0.buffer();
            });
            if (this.maxPartSize == -1) {
                maxSize = this.maxInMemorySize;
            } else {
                maxSize = (int) Math.min(this.maxInMemorySize, this.maxPartSize);
            }
            return DataBufferUtils.join(contents, maxSize).map(content -> {
                String value = content.toString(MultipartUtils.charset(headers));
                DataBufferUtils.release(content);
                return DefaultPartEvents.form(headers, value);
            }).switchIfEmpty(Mono.fromCallable(() -> {
                return DefaultPartEvents.form(headers);
            }));
        }
        boolean isFilePart = headers.getContentDisposition().getFilename() != null;
        AtomicLong partSize = new AtomicLong();
        return bodyTokens.concatMap(body -> {
            DataBuffer buffer = body.buffer();
            if (!tooLarge(partSize, buffer)) {
                return isFilePart ? Mono.just(DefaultPartEvents.file(headers, buffer, body.isLast())) : Mono.just(DefaultPartEvents.create(headers, body.buffer(), body.isLast()));
            }
            DataBufferUtils.release(buffer);
            return Mono.error(new DataBufferLimitException("Part exceeded the limit of " + this.maxPartSize + " bytes"));
        }).switchIfEmpty(Mono.fromCallable(() -> {
            return isFilePart ? DefaultPartEvents.file(headers) : DefaultPartEvents.create(headers);
        }));
    }

    private boolean tooLarge(AtomicLong partSize, DataBuffer buffer) {
        if (this.maxPartSize != -1) {
            long size = partSize.addAndGet(buffer.readableByteCount());
            return size > this.maxPartSize;
        }
        return false;
    }
}
