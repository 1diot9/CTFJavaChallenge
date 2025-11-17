package org.springframework.boot.autoconfigure.jackson;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jackson/Jackson2ObjectMapperBuilderCustomizer.class */
public interface Jackson2ObjectMapperBuilderCustomizer {
    void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder);
}
