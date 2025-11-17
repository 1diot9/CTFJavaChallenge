package org.springframework.http.codec.json;

import kotlinx.serialization.json.Json;
import org.springframework.http.MediaType;
import org.springframework.http.codec.KotlinSerializationStringEncoder;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/json/KotlinSerializationJsonEncoder.class */
public class KotlinSerializationJsonEncoder extends KotlinSerializationStringEncoder<Json> {
    public KotlinSerializationJsonEncoder() {
        this(Json.Default);
    }

    public KotlinSerializationJsonEncoder(Json json) {
        super(json, MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
    }
}
