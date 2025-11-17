package org.springframework.http.codec.protobuf;

import kotlinx.serialization.protobuf.ProtoBuf;
import org.springframework.http.codec.KotlinSerializationBinaryEncoder;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/protobuf/KotlinSerializationProtobufEncoder.class */
public class KotlinSerializationProtobufEncoder extends KotlinSerializationBinaryEncoder<ProtoBuf> {
    public KotlinSerializationProtobufEncoder() {
        this(ProtoBuf.Default);
    }

    public KotlinSerializationProtobufEncoder(ProtoBuf protobuf) {
        super(protobuf, ProtobufCodecSupport.MIME_TYPES);
    }
}
