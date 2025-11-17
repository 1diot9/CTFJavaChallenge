package org.springframework.boot.autoconfigure.data.rest;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@EnableConfigurationProperties({RepositoryRestProperties.class})
@AutoConfiguration(after = {HttpMessageConvertersAutoConfiguration.class, JacksonAutoConfiguration.class})
@ConditionalOnClass({RepositoryRestMvcConfiguration.class})
@ConditionalOnMissingBean({RepositoryRestMvcConfiguration.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import({RepositoryRestMvcConfiguration.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/rest/RepositoryRestMvcAutoConfiguration.class */
public class RepositoryRestMvcAutoConfiguration {
    @Bean
    public SpringBootRepositoryRestConfigurer springBootRepositoryRestConfigurer(ObjectProvider<Jackson2ObjectMapperBuilder> objectMapperBuilder, RepositoryRestProperties properties) {
        return new SpringBootRepositoryRestConfigurer(objectMapperBuilder.getIfAvailable(), properties);
    }
}
