package org.springframework.http.converter.json;

import kotlinx.serialization.json.Json;
import org.springframework.http.MediaType;
import org.springframework.http.converter.KotlinSerializationStringHttpMessageConverter;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/json/KotlinSerializationJsonHttpMessageConverter.class */
public class KotlinSerializationJsonHttpMessageConverter extends KotlinSerializationStringHttpMessageConverter<Json> {
    public KotlinSerializationJsonHttpMessageConverter() {
        this(Json.Default);
    }

    public KotlinSerializationJsonHttpMessageConverter(Json json) {
        super(json, MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
    }
}
