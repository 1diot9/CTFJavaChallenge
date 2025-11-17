package org.springframework.http.converter.cbor;

import kotlinx.serialization.cbor.Cbor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.KotlinSerializationBinaryHttpMessageConverter;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/cbor/KotlinSerializationCborHttpMessageConverter.class */
public class KotlinSerializationCborHttpMessageConverter extends KotlinSerializationBinaryHttpMessageConverter<Cbor> {
    public KotlinSerializationCborHttpMessageConverter() {
        this(Cbor.Default);
    }

    public KotlinSerializationCborHttpMessageConverter(Cbor cbor) {
        super(cbor, MediaType.APPLICATION_CBOR);
    }
}
