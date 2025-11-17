package org.springframework.boot.autoconfigure.http;

import jakarta.json.bind.Jsonb;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Jsonb.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/http/JsonbHttpMessageConvertersConfiguration.class */
class JsonbHttpMessageConvertersConfiguration {
    JsonbHttpMessageConvertersConfiguration() {
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnBean({Jsonb.class})
    @Conditional({PreferJsonbOrMissingJacksonAndGsonCondition.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/http/JsonbHttpMessageConvertersConfiguration$JsonbHttpMessageConverterConfiguration.class */
    static class JsonbHttpMessageConverterConfiguration {
        JsonbHttpMessageConverterConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        JsonbHttpMessageConverter jsonbHttpMessageConverter(Jsonb jsonb) {
            JsonbHttpMessageConverter converter = new JsonbHttpMessageConverter();
            converter.setJsonb(jsonb);
            return converter;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/http/JsonbHttpMessageConvertersConfiguration$PreferJsonbOrMissingJacksonAndGsonCondition.class */
    private static class PreferJsonbOrMissingJacksonAndGsonCondition extends AnyNestedCondition {
        PreferJsonbOrMissingJacksonAndGsonCondition() {
            super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnProperty(name = {"spring.mvc.converters.preferred-json-mapper"}, havingValue = "jsonb")
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/http/JsonbHttpMessageConvertersConfiguration$PreferJsonbOrMissingJacksonAndGsonCondition$JsonbPreferred.class */
        static class JsonbPreferred {
            JsonbPreferred() {
            }
        }

        @ConditionalOnMissingBean({MappingJackson2HttpMessageConverter.class, GsonHttpMessageConverter.class})
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/http/JsonbHttpMessageConvertersConfiguration$PreferJsonbOrMissingJacksonAndGsonCondition$JacksonAndGsonMissing.class */
        static class JacksonAndGsonMissing {
            JacksonAndGsonMissing() {
            }
        }
    }
}
