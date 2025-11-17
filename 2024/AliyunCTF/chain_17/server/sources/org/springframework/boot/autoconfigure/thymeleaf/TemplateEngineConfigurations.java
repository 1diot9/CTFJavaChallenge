package org.springframework.boot.autoconfigure.thymeleaf;

import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring6.ISpringTemplateEngine;
import org.thymeleaf.spring6.ISpringWebFluxTemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.SpringWebFluxTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/thymeleaf/TemplateEngineConfigurations.class */
class TemplateEngineConfigurations {
    TemplateEngineConfigurations() {
    }

    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/thymeleaf/TemplateEngineConfigurations$DefaultTemplateEngineConfiguration.class */
    static class DefaultTemplateEngineConfiguration {
        DefaultTemplateEngineConfiguration() {
        }

        @ConditionalOnMissingBean({ISpringTemplateEngine.class})
        @Bean
        SpringTemplateEngine templateEngine(ThymeleafProperties properties, ObjectProvider<ITemplateResolver> templateResolvers, ObjectProvider<IDialect> dialects) {
            SpringTemplateEngine engine = new SpringTemplateEngine();
            engine.setEnableSpringELCompiler(properties.isEnableSpringElCompiler());
            engine.setRenderHiddenMarkersBeforeCheckboxes(properties.isRenderHiddenMarkersBeforeCheckboxes());
            Stream<ITemplateResolver> orderedStream = templateResolvers.orderedStream();
            Objects.requireNonNull(engine);
            orderedStream.forEach(engine::addTemplateResolver);
            Stream<IDialect> orderedStream2 = dialects.orderedStream();
            Objects.requireNonNull(engine);
            orderedStream2.forEach(engine::addDialect);
            return engine;
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = {"spring.thymeleaf.enabled"}, matchIfMissing = true)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/thymeleaf/TemplateEngineConfigurations$ReactiveTemplateEngineConfiguration.class */
    static class ReactiveTemplateEngineConfiguration {
        ReactiveTemplateEngineConfiguration() {
        }

        @ConditionalOnMissingBean({ISpringWebFluxTemplateEngine.class})
        @Bean
        SpringWebFluxTemplateEngine templateEngine(ThymeleafProperties properties, ObjectProvider<ITemplateResolver> templateResolvers, ObjectProvider<IDialect> dialects) {
            SpringWebFluxTemplateEngine engine = new SpringWebFluxTemplateEngine();
            engine.setEnableSpringELCompiler(properties.isEnableSpringElCompiler());
            engine.setRenderHiddenMarkersBeforeCheckboxes(properties.isRenderHiddenMarkersBeforeCheckboxes());
            Stream<ITemplateResolver> orderedStream = templateResolvers.orderedStream();
            Objects.requireNonNull(engine);
            orderedStream.forEach(engine::addTemplateResolver);
            Stream<IDialect> orderedStream2 = dialects.orderedStream();
            Objects.requireNonNull(engine);
            orderedStream2.forEach(engine::addDialect);
            return engine;
        }
    }
}
