package org.springframework.boot.autoconfigure.http;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.NoneNestedConditions;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration;
import org.springframework.boot.context.properties.bind.BindableRuntimeHintsRegistrar;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.web.servlet.server.Encoding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

@AutoConfiguration(after = {GsonAutoConfiguration.class, JacksonAutoConfiguration.class, JsonbAutoConfiguration.class})
@ConditionalOnClass({HttpMessageConverter.class})
@ImportRuntimeHints({HttpMessageConvertersAutoConfigurationRuntimeHints.class})
@Conditional({NotReactiveWebApplicationCondition.class})
@Import({JacksonHttpMessageConvertersConfiguration.class, GsonHttpMessageConvertersConfiguration.class, JsonbHttpMessageConvertersConfiguration.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/http/HttpMessageConvertersAutoConfiguration.class */
public class HttpMessageConvertersAutoConfiguration {
    static final String PREFERRED_MAPPER_PROPERTY = "spring.mvc.converters.preferred-json-mapper";

    @ConditionalOnMissingBean
    @Bean
    public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
        return new HttpMessageConverters(converters.orderedStream().toList());
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({StringHttpMessageConverter.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/http/HttpMessageConvertersAutoConfiguration$StringHttpMessageConverterConfiguration.class */
    protected static class StringHttpMessageConverterConfiguration {
        protected StringHttpMessageConverterConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        public StringHttpMessageConverter stringHttpMessageConverter(Environment environment) {
            Encoding encoding = (Encoding) Binder.get(environment).bindOrCreate("server.servlet.encoding", Encoding.class);
            StringHttpMessageConverter converter = new StringHttpMessageConverter(encoding.getCharset());
            converter.setWriteAcceptCharset(false);
            return converter;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/http/HttpMessageConvertersAutoConfiguration$NotReactiveWebApplicationCondition.class */
    static class NotReactiveWebApplicationCondition extends NoneNestedConditions {
        NotReactiveWebApplicationCondition() {
            super(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/http/HttpMessageConvertersAutoConfiguration$NotReactiveWebApplicationCondition$ReactiveWebApplication.class */
        private static final class ReactiveWebApplication {
            private ReactiveWebApplication() {
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/http/HttpMessageConvertersAutoConfiguration$HttpMessageConvertersAutoConfigurationRuntimeHints.class */
    static class HttpMessageConvertersAutoConfigurationRuntimeHints extends BindableRuntimeHintsRegistrar {
        HttpMessageConvertersAutoConfigurationRuntimeHints() {
            super((Class<?>[]) new Class[]{Encoding.class});
        }
    }
}
