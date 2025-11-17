package org.springframework.boot.autoconfigure.rsocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import io.netty.buffer.PooledByteBufAllocator;
import io.rsocket.RSocket;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.http.MediaType;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.ClassUtils;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;

@AutoConfiguration(after = {JacksonAutoConfiguration.class})
@ConditionalOnClass({RSocket.class, RSocketStrategies.class, PooledByteBufAllocator.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/rsocket/RSocketStrategiesAutoConfiguration.class */
public class RSocketStrategiesAutoConfiguration {
    private static final String PATHPATTERN_ROUTEMATCHER_CLASS = "org.springframework.web.util.pattern.PathPatternRouteMatcher";

    @ConditionalOnMissingBean
    @Bean
    public RSocketStrategies rSocketStrategies(ObjectProvider<RSocketStrategiesCustomizer> customizers) {
        RSocketStrategies.Builder builder = RSocketStrategies.builder();
        if (ClassUtils.isPresent(PATHPATTERN_ROUTEMATCHER_CLASS, null)) {
            builder.routeMatcher(new PathPatternRouteMatcher());
        }
        customizers.orderedStream().forEach(customizer -> {
            customizer.customize(builder);
        });
        return builder.build();
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ObjectMapper.class, CBORFactory.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/rsocket/RSocketStrategiesAutoConfiguration$JacksonCborStrategyConfiguration.class */
    protected static class JacksonCborStrategyConfiguration {
        private static final MediaType[] SUPPORTED_TYPES = {MediaType.APPLICATION_CBOR};

        protected JacksonCborStrategyConfiguration() {
        }

        @ConditionalOnBean({Jackson2ObjectMapperBuilder.class})
        @Bean
        @Order(0)
        public RSocketStrategiesCustomizer jacksonCborRSocketStrategyCustomizer(Jackson2ObjectMapperBuilder builder) {
            return strategy -> {
                ObjectMapper objectMapper = builder.createXmlMapper(false).factory(new CBORFactory()).build();
                strategy.decoder(new Decoder[]{new Jackson2CborDecoder(objectMapper, SUPPORTED_TYPES)});
                strategy.encoder(new Encoder[]{new Jackson2CborEncoder(objectMapper, SUPPORTED_TYPES)});
            };
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ObjectMapper.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/rsocket/RSocketStrategiesAutoConfiguration$JacksonJsonStrategyConfiguration.class */
    protected static class JacksonJsonStrategyConfiguration {
        private static final MediaType[] SUPPORTED_TYPES = {MediaType.APPLICATION_JSON, new MediaType("application", "*+json")};

        protected JacksonJsonStrategyConfiguration() {
        }

        @ConditionalOnBean({ObjectMapper.class})
        @Bean
        @Order(1)
        public RSocketStrategiesCustomizer jacksonJsonRSocketStrategyCustomizer(ObjectMapper objectMapper) {
            return strategy -> {
                strategy.decoder(new Decoder[]{new Jackson2JsonDecoder(objectMapper, SUPPORTED_TYPES)});
                strategy.encoder(new Encoder[]{new Jackson2JsonEncoder(objectMapper, SUPPORTED_TYPES)});
            };
        }
    }
}
