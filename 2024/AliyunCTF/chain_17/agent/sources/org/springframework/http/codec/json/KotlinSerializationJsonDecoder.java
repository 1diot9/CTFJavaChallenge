package org.springframework.http.codec.json;

import kotlinx.serialization.json.Json;
import org.springframework.http.MediaType;
import org.springframework.http.codec.KotlinSerializationStringDecoder;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/json/KotlinSerializationJsonDecoder.class */
public class KotlinSerializationJsonDecoder extends KotlinSerializationStringDecoder<Json> {
    public KotlinSerializationJsonDecoder() {
        this(Json.Default);
    }

    public KotlinSerializationJsonDecoder(Json json) {
        super(json, MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
    }
}
