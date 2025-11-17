package org.springframework.boot.autoconfigure.http.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.codec.CodecProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.util.MimeType;
import org.springframework.web.reactive.function.client.WebClient;

@EnableConfigurationProperties({CodecProperties.class})
@AutoConfiguration(after = {JacksonAutoConfiguration.class})
@ConditionalOnClass({CodecConfigurer.class, WebClient.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/http/codec/CodecsAutoConfiguration.class */
public class CodecsAutoConfiguration {
    private static final MimeType[] EMPTY_MIME_TYPES = new MimeType[0];

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ObjectMapper.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/http/codec/CodecsAutoConfiguration$JacksonCodecConfiguration.class */
    static class JacksonCodecConfiguration {
        JacksonCodecConfiguration() {
        }

        @ConditionalOnBean({ObjectMapper.class})
        @Bean
        @Order(0)
        CodecCustomizer jacksonCodecCustomizer(ObjectMapper objectMapper) {
            return configurer -> {
                CodecConfigurer.DefaultCodecs defaults = configurer.defaultCodecs();
                defaults.jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, CodecsAutoConfiguration.EMPTY_MIME_TYPES));
                defaults.jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, CodecsAutoConfiguration.EMPTY_MIME_TYPES));
            };
        }
    }

    @Configuration(proxyBeanMethods = false)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/http/codec/CodecsAutoConfiguration$DefaultCodecsConfiguration.class */
    static class DefaultCodecsConfiguration {
        DefaultCodecsConfiguration() {
        }

        @Bean
        @Order(0)
        CodecCustomizer defaultCodecCustomizer(CodecProperties codecProperties) {
            return configurer -> {
                PropertyMapper map = PropertyMapper.get();
                CodecConfigurer.DefaultCodecs defaultCodecs = configurer.defaultCodecs();
                defaultCodecs.enableLoggingRequestDetails(codecProperties.isLogRequestDetails());
                PropertyMapper.Source<Integer> asInt = map.from((PropertyMapper) codecProperties.getMaxInMemorySize()).whenNonNull().asInt((v0) -> {
                    return v0.toBytes();
                });
                Objects.requireNonNull(defaultCodecs);
                asInt.to((v1) -> {
                    r1.maxInMemorySize(v1);
                });
            };
        }
    }
}
