package org.springframework.boot.autoconfigure.reactor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.reactor.ReactorProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

@EnableConfigurationProperties({ReactorProperties.class})
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Hooks.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/reactor/ReactorAutoConfiguration.class */
public class ReactorAutoConfiguration {
    ReactorAutoConfiguration(ReactorProperties properties) {
        if (properties.getContextPropagation() == ReactorProperties.ContextPropagationMode.AUTO) {
            Hooks.enableAutomaticContextPropagation();
        }
    }
}
