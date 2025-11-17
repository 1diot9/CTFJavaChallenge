package org.springframework.http.codec.multipart;

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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/PartHttpMessageWriter.class */
public class PartHttpMessageWriter extends MultipartWriterSupport implements HttpMessageWriter<Part> {
    public PartHttpMessageWriter() {
        super(MultipartHttpMessageReader.MIME_TYPES);
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public boolean canWrite(ResolvableType elementType, @Nullable MediaType mediaType) {
        if (Part.class.isAssignableFrom(elementType.toClass())) {
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
    public Mono<Void> write(Publisher<? extends Part> parts, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage outputMessage, Map<String, Object> hints) {
        byte[] boundary = generateMultipartBoundary();
        outputMessage.getHeaders().setContentType(getMultipartMediaType(mediaType, boundary));
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(Hints.getLogPrefix(hints) + "Encoding Publisher<Part>");
        }
        Flux<DataBuffer> body = Flux.from(parts).concatMap(part -> {
            return encodePart(boundary, part, outputMessage.bufferFactory());
        }).concatWith(generateLastLine(boundary, outputMessage.bufferFactory())).doOnDiscard(DataBuffer.class, DataBufferUtils::release);
        if (this.logger.isDebugEnabled()) {
            body = body.doOnNext(buffer -> {
                Hints.touchDataBuffer(buffer, hints, this.logger);
            });
        }
        return outputMessage.writeWith(body);
    }

    private <T> Flux<DataBuffer> encodePart(byte[] boundary, Part part, DataBufferFactory bufferFactory) {
        String str;
        HttpHeaders headers = new HttpHeaders(part.headers());
        String name = part.name();
        if (!headers.containsKey(HttpHeaders.CONTENT_DISPOSITION)) {
            if (part instanceof FilePart) {
                FilePart filePart = (FilePart) part;
                str = filePart.filename();
            } else {
                str = null;
            }
            headers.setContentDispositionFormData(name, str);
        }
        return Flux.concat(new Publisher[]{generateBoundaryLine(boundary, bufferFactory), generatePartHeaders(headers, bufferFactory), part.content(), generateNewLine(bufferFactory)});
    }
}
