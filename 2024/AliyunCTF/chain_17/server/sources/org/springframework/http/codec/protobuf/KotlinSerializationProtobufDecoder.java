package org.springframework.http.codec.protobuf;

import kotlinx.serialization.protobuf.ProtoBuf;
import org.springframework.http.codec.KotlinSerializationBinaryDecoder;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/protobuf/KotlinSerializationProtobufDecoder.class */
public class KotlinSerializationProtobufDecoder extends KotlinSerializationBinaryDecoder<ProtoBuf> {
    public KotlinSerializationProtobufDecoder() {
        this(ProtoBuf.Default);
    }

    public KotlinSerializationProtobufDecoder(ProtoBuf protobuf) {
        super(protobuf, ProtobufCodecSupport.MIME_TYPES);
    }
}
