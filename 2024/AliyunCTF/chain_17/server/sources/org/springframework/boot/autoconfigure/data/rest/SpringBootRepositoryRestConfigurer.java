package org.springframework.boot.autoconfigure.data.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.annotation.Order;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Order(0)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/rest/SpringBootRepositoryRestConfigurer.class */
class SpringBootRepositoryRestConfigurer implements RepositoryRestConfigurer {
    private final Jackson2ObjectMapperBuilder objectMapperBuilder;
    private final RepositoryRestProperties properties;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpringBootRepositoryRestConfigurer(Jackson2ObjectMapperBuilder objectMapperBuilder, RepositoryRestProperties properties) {
        this.objectMapperBuilder = objectMapperBuilder;
        this.properties = properties;
    }

    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        this.properties.applyTo(config);
    }

    public void configureJacksonObjectMapper(ObjectMapper objectMapper) {
        if (this.objectMapperBuilder != null) {
            this.objectMapperBuilder.configure(objectMapper);
        }
    }
}
