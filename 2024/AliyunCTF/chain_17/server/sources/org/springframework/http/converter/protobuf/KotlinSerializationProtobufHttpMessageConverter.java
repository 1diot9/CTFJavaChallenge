package org.springframework.http.converter.protobuf;

import kotlinx.serialization.protobuf.ProtoBuf;
import org.springframework.http.MediaType;
import org.springframework.http.converter.KotlinSerializationBinaryHttpMessageConverter;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/protobuf/KotlinSerializationProtobufHttpMessageConverter.class */
public class KotlinSerializationProtobufHttpMessageConverter extends KotlinSerializationBinaryHttpMessageConverter<ProtoBuf> {
    public KotlinSerializationProtobufHttpMessageConverter() {
        this(ProtoBuf.Default);
    }

    public KotlinSerializationProtobufHttpMessageConverter(ProtoBuf protobuf) {
        super(protobuf, MediaType.APPLICATION_PROTOBUF, MediaType.APPLICATION_OCTET_STREAM, new MediaType("application", "vnd.google.protobuf"));
    }
}
