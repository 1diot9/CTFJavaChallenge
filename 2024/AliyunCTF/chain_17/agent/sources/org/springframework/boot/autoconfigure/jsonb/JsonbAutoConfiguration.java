package org.springframework.boot.autoconfigure.jsonb;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass({Jsonb.class})
@ConditionalOnResource(resources = {"classpath:META-INF/services/jakarta.json.bind.spi.JsonbProvider", "classpath:META-INF/services/jakarta.json.spi.JsonProvider"})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jsonb/JsonbAutoConfiguration.class */
public class JsonbAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public Jsonb jsonb() {
        return JsonbBuilder.create();
    }
}
