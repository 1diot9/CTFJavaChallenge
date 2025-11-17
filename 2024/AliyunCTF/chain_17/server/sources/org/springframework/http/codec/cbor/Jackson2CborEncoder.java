package org.springframework.http.codec.cbor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.AbstractJackson2Encoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/cbor/Jackson2CborEncoder.class */
public class Jackson2CborEncoder extends AbstractJackson2Encoder {
    public Jackson2CborEncoder() {
        this(Jackson2ObjectMapperBuilder.cbor().build(), MediaType.APPLICATION_CBOR);
    }

    public Jackson2CborEncoder(ObjectMapper mapper, MimeType... mimeTypes) {
        super(mapper, mimeTypes);
        Assert.isAssignable(CBORFactory.class, mapper.getFactory().getClass());
    }

    @Override // org.springframework.http.codec.json.AbstractJackson2Encoder, org.springframework.core.codec.Encoder
    public Flux<DataBuffer> encode(Publisher<?> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, MimeType mimeType, Map<String, Object> hints) {
        throw new UnsupportedOperationException("Does not support stream encoding yet");
    }
}
