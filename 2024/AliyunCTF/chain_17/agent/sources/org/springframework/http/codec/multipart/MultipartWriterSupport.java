package org.springframework.http.codec.multipart;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.tomcat.websocket.BasicAuthenticator;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/MultipartWriterSupport.class */
public class MultipartWriterSupport extends LoggingCodecSupport {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final List<MediaType> supportedMediaTypes;
    private Charset charset = DEFAULT_CHARSET;

    /* JADX INFO: Access modifiers changed from: protected */
    public MultipartWriterSupport(List<MediaType> supportedMediaTypes) {
        this.supportedMediaTypes = supportedMediaTypes;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public void setCharset(Charset charset) {
        Assert.notNull(charset, "Charset must not be null");
        this.charset = charset;
    }

    public List<MediaType> getWritableMediaTypes() {
        return this.supportedMediaTypes;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public byte[] generateMultipartBoundary() {
        return MimeTypeUtils.generateMultipartBoundary();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MediaType getMultipartMediaType(@Nullable MediaType mediaType, byte[] boundary) {
        Map<String, String> params = new HashMap<>();
        if (mediaType != null) {
            params.putAll(mediaType.getParameters());
        }
        params.put("boundary", new String(boundary, StandardCharsets.US_ASCII));
        Charset charset = getCharset();
        if (!charset.equals(StandardCharsets.UTF_8) && !charset.equals(StandardCharsets.US_ASCII)) {
            params.put(BasicAuthenticator.charsetparam, charset.name());
        }
        return new MediaType(mediaType != null ? mediaType : MediaType.MULTIPART_FORM_DATA, params);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Mono<DataBuffer> generateBoundaryLine(byte[] boundary, DataBufferFactory bufferFactory) {
        return Mono.fromCallable(() -> {
            DataBuffer buffer = bufferFactory.allocateBuffer(boundary.length + 4);
            buffer.write((byte) 45);
            buffer.write((byte) 45);
            buffer.write(boundary);
            buffer.write((byte) 13);
            buffer.write((byte) 10);
            return buffer;
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Mono<DataBuffer> generateNewLine(DataBufferFactory bufferFactory) {
        return Mono.fromCallable(() -> {
            DataBuffer buffer = bufferFactory.allocateBuffer(2);
            buffer.write((byte) 13);
            buffer.write((byte) 10);
            return buffer;
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Mono<DataBuffer> generateLastLine(byte[] boundary, DataBufferFactory bufferFactory) {
        return Mono.fromCallable(() -> {
            DataBuffer buffer = bufferFactory.allocateBuffer(boundary.length + 6);
            buffer.write((byte) 45);
            buffer.write((byte) 45);
            buffer.write(boundary);
            buffer.write((byte) 45);
            buffer.write((byte) 45);
            buffer.write((byte) 13);
            buffer.write((byte) 10);
            return buffer;
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Mono<DataBuffer> generatePartHeaders(HttpHeaders headers, DataBufferFactory bufferFactory) {
        return Mono.fromCallable(() -> {
            FastByteArrayOutputStream bos = new FastByteArrayOutputStream();
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                byte[] headerName = entry.getKey().getBytes(getCharset());
                for (String headerValueString : entry.getValue()) {
                    byte[] headerValue = headerValueString.getBytes(getCharset());
                    bos.write(headerName);
                    bos.write(58);
                    bos.write(32);
                    bos.write(headerValue);
                    bos.write(13);
                    bos.write(10);
                }
            }
            bos.write(13);
            bos.write(10);
            byte[] bytes = bos.toByteArrayUnsafe();
            return bufferFactory.wrap(bytes);
        });
    }
}
