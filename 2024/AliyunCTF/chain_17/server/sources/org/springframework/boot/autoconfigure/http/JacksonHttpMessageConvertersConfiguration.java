package org.springframework.boot.autoconfigure.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;

@Configuration(proxyBeanMethods = false)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/http/JacksonHttpMessageConvertersConfiguration.class */
class JacksonHttpMessageConvertersConfiguration {
    JacksonHttpMessageConvertersConfiguration() {
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ObjectMapper.class})
    @ConditionalOnBean({ObjectMapper.class})
    @ConditionalOnProperty(name = {"spring.mvc.converters.preferred-json-mapper"}, havingValue = "jackson", matchIfMissing = true)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/http/JacksonHttpMessageConvertersConfiguration$MappingJackson2HttpMessageConverterConfiguration.class */
    static class MappingJackson2HttpMessageConverterConfiguration {
        MappingJackson2HttpMessageConverterConfiguration() {
        }

        @ConditionalOnMissingBean(value = {MappingJackson2HttpMessageConverter.class}, ignoredType = {"org.springframework.hateoas.server.mvc.TypeConstrainedMappingJackson2HttpMessageConverter", "org.springframework.data.rest.webmvc.alps.AlpsJsonHttpMessageConverter"})
        @Bean
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
            return new MappingJackson2HttpMessageConverter(objectMapper);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({XmlMapper.class})
    @ConditionalOnBean({Jackson2ObjectMapperBuilder.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/http/JacksonHttpMessageConvertersConfiguration$MappingJackson2XmlHttpMessageConverterConfiguration.class */
    protected static class MappingJackson2XmlHttpMessageConverterConfiguration {
        protected MappingJackson2XmlHttpMessageConverterConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        public MappingJackson2XmlHttpMessageConverter mappingJackson2XmlHttpMessageConverter(Jackson2ObjectMapperBuilder builder) {
            return new MappingJackson2XmlHttpMessageConverter(builder.createXmlMapper(true).build());
        }
    }
}
