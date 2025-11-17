package org.springframework.http.codec.multipart;

import java.util.Collections;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/PartEventHttpMessageWriter.class */
public class PartEventHttpMessageWriter extends MultipartWriterSupport implements HttpMessageWriter<PartEvent> {
    public PartEventHttpMessageWriter() {
        super(Collections.singletonList(MediaType.MULTIPART_FORM_DATA));
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public boolean canWrite(ResolvableType elementType, @Nullable MediaType mediaType) {
        if (PartEvent.class.isAssignableFrom(elementType.toClass())) {
            if (mediaType == null) {
                return true;
            }
            for (MediaType supportedMediaType : getWritableMediaTypes()) {
                if (supportedMediaType.isCompatibleWith(mediaType)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public Mono<Void> write(Publisher<? extends PartEvent> partDataStream, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage outputMessage, Map<String, Object> hints) {
        byte[] boundary = generateMultipartBoundary();
        outputMessage.getHeaders().setContentType(getMultipartMediaType(mediaType, boundary));
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(Hints.getLogPrefix(hints) + "Encoding Publisher<PartEvent>");
        }
        Flux<DataBuffer> body = Flux.from(partDataStream).windowUntil((v0) -> {
            return v0.isLast();
        }).concatMap(partData -> {
            return partData.switchOnFirst((signal, flux) -> {
                if (signal.hasValue()) {
                    PartEvent value = (PartEvent) signal.get();
                    Assert.state(value != null, "Null value");
                    Flux<DataBuffer> dataBuffers = flux.map((v0) -> {
                        return v0.content();
                    }).filter(buffer -> {
                        return buffer.readableByteCount() > 0;
                    });
                    return encodePartData(boundary, outputMessage.bufferFactory(), value.headers(), dataBuffers);
                }
                return flux.cast(DataBuffer.class);
            });
        }).concatWith(generateLastLine(boundary, outputMessage.bufferFactory())).doOnDiscard(DataBuffer.class, DataBufferUtils::release);
        if (this.logger.isDebugEnabled()) {
            body = body.doOnNext(buffer -> {
                Hints.touchDataBuffer(buffer, hints, this.logger);
            });
        }
        return outputMessage.writeWith(body);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Flux<DataBuffer> encodePartData(byte[] boundary, DataBufferFactory bufferFactory, HttpHeaders headers, Flux<DataBuffer> body) {
        return Flux.concat(new Publisher[]{generateBoundaryLine(boundary, bufferFactory), generatePartHeaders(headers, bufferFactory), body, generateNewLine(bufferFactory)});
    }
}
