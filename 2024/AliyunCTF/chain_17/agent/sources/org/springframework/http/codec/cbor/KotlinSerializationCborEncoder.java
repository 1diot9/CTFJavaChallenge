package org.springframework.http.codec.cbor;

import kotlinx.serialization.cbor.Cbor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.KotlinSerializationBinaryEncoder;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/cbor/KotlinSerializationCborEncoder.class */
public class KotlinSerializationCborEncoder extends KotlinSerializationBinaryEncoder<Cbor> {
    public KotlinSerializationCborEncoder() {
        this(Cbor.Default);
    }

    public KotlinSerializationCborEncoder(Cbor cbor) {
        super(cbor, MediaType.APPLICATION_CBOR);
    }
}
