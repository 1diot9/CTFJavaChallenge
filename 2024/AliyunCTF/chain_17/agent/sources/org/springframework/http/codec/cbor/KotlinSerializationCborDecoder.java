package org.springframework.http.codec.cbor;

import kotlinx.serialization.cbor.Cbor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.KotlinSerializationBinaryDecoder;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/cbor/KotlinSerializationCborDecoder.class */
public class KotlinSerializationCborDecoder extends KotlinSerializationBinaryDecoder<Cbor> {
    public KotlinSerializationCborDecoder() {
        this(Cbor.Default);
    }

    public KotlinSerializationCborDecoder(Cbor cbor) {
        super(cbor, MediaType.APPLICATION_CBOR);
    }
}
