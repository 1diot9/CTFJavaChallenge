package org.springframework.http.codec.multipart;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.http.codec.multipart.MultipartParser;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/DefaultPartHttpMessageReader.class */
public class DefaultPartHttpMessageReader extends LoggingCodecSupport implements HttpMessageReader<Part> {
    private int maxInMemorySize = 262144;
    private int maxHeadersSize = 10240;
    private long maxDiskUsagePerPart = -1;
    private int maxParts = -1;
    private Scheduler blockingOperationScheduler = Schedulers.boundedElastic();
    private FileStorage fileStorage = FileStorage.tempDirectory(this::getBlockingOperationScheduler);
    private Charset headersCharset = StandardCharsets.UTF_8;

    public void setMaxHeadersSize(int byteCount) {
        this.maxHeadersSize = byteCount;
    }

    public int getMaxInMemorySize() {
        return this.maxInMemorySize;
    }

    public void setMaxInMemorySize(int maxInMemorySize) {
        this.maxInMemorySize = maxInMemorySize;
    }

    public void setMaxDiskUsagePerPart(long maxDiskUsagePerPart) {
        this.maxDiskUsagePerPart = maxDiskUsagePerPart;
    }

    public void setMaxParts(int maxParts) {
        this.maxParts = maxParts;
    }

    public void setFileStorageDirectory(Path fileStorageDirectory) throws IOException {
        Assert.notNull(fileStorageDirectory, "FileStorageDirectory must not be null");
        this.fileStorage = FileStorage.fromPath(fileStorageDirectory);
    }

    public void setBlockingOperationScheduler(Scheduler blockingOperationScheduler) {
        Assert.notNull(blockingOperationScheduler, "'blockingOperationScheduler' must not be null");
        this.blockingOperationScheduler = blockingOperationScheduler;
    }

    private Scheduler getBlockingOperationScheduler() {
        return this.blockingOperationScheduler;
    }

    public void setHeadersCharset(Charset headersCharset) {
        Assert.notNull(headersCharset, "HeadersCharset must not be null");
        this.headersCharset = headersCharset;
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public List<MediaType> getReadableMediaTypes() {
        return Collections.singletonList(MediaType.MULTIPART_FORM_DATA);
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
        return Part.class.equals(elementType.toClass()) && (mediaType == null || MediaType.MULTIPART_FORM_DATA.isCompatibleWith(mediaType));
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public Mono<Part> readMono(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        return Mono.error(new UnsupportedOperationException("Cannot read multipart request body into single Part"));
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public Flux<Part> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        return Flux.defer(() -> {
            byte[] boundary = MultipartUtils.boundary(message, this.headersCharset);
            if (boundary == null) {
                return Flux.error(new DecodingException("No multipart boundary found in Content-Type: \"" + message.getHeaders().getContentType() + "\""));
            }
            Flux<MultipartParser.Token> allPartsTokens = MultipartParser.parse(message.getBody(), boundary, this.maxHeadersSize, this.headersCharset);
            AtomicInteger partCount = new AtomicInteger();
            return allPartsTokens.windowUntil((v0) -> {
                return v0.isLast();
            }).concatMap(partsTokens -> {
                if (tooManyParts(partCount)) {
                    return Mono.error(new DecodingException("Too many parts (" + partCount.get() + "/" + this.maxParts + " allowed)"));
                }
                return PartGenerator.createPart(partsTokens, this.maxInMemorySize, this.maxDiskUsagePerPart, this.fileStorage.directory(), this.blockingOperationScheduler);
            });
        });
    }

    private boolean tooManyParts(AtomicInteger partCount) {
        int count = partCount.incrementAndGet();
        return this.maxParts > 0 && count > this.maxParts;
    }
}
