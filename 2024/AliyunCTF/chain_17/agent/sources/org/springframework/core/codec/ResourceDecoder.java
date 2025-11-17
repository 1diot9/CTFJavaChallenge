package org.springframework.core.codec;

import java.io.ByteArrayInputStream;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/codec/ResourceDecoder.class */
public class ResourceDecoder extends AbstractDataBufferDecoder<Resource> {
    public static final String FILENAME_HINT = ResourceDecoder.class.getName() + ".filename";

    @Override // org.springframework.core.codec.Decoder
    public /* bridge */ /* synthetic */ Object decode(DataBuffer dataBuffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map hints) throws DecodingException {
        return decode(dataBuffer, elementType, mimeType, (Map<String, Object>) hints);
    }

    public ResourceDecoder() {
        super(MimeTypeUtils.ALL);
    }

    @Override // org.springframework.core.codec.AbstractDecoder, org.springframework.core.codec.Decoder
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        return Resource.class.isAssignableFrom(elementType.toClass()) && super.canDecode(elementType, mimeType);
    }

    @Override // org.springframework.core.codec.AbstractDataBufferDecoder, org.springframework.core.codec.Decoder
    public Flux<Resource> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.from(decodeToMono(inputStream, elementType, mimeType, hints));
    }

    @Override // org.springframework.core.codec.Decoder
    public Resource decode(DataBuffer dataBuffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        final byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(Hints.getLogPrefix(hints) + "Read " + bytes.length + " bytes");
        }
        Class<?> clazz = elementType.toClass();
        final String filename = hints != null ? (String) hints.get(FILENAME_HINT) : null;
        if (clazz == InputStreamResource.class) {
            return new InputStreamResource(new ByteArrayInputStream(bytes)) { // from class: org.springframework.core.codec.ResourceDecoder.1
                @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
                public String getFilename() {
                    return filename;
                }

                @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
                public long contentLength() {
                    return bytes.length;
                }
            };
        }
        if (Resource.class.isAssignableFrom(clazz)) {
            return new ByteArrayResource(bytes) { // from class: org.springframework.core.codec.ResourceDecoder.2
                @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
                public String getFilename() {
                    return filename;
                }
            };
        }
        throw new IllegalStateException("Unsupported resource class: " + clazz);
    }
}
