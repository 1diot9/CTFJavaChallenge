package org.springframework.boot.autoconfigure.mustache;

import com.samskivert.mustache.Mustache;
import java.util.Objects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.mustache.MustacheProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.reactive.result.view.MustacheViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mustache/MustacheReactiveWebConfiguration.class */
class MustacheReactiveWebConfiguration {
    MustacheReactiveWebConfiguration() {
    }

    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.mustache", name = {"enabled"}, matchIfMissing = true)
    @Bean
    MustacheViewResolver mustacheViewResolver(Mustache.Compiler mustacheCompiler, MustacheProperties mustache) {
        MustacheViewResolver resolver = new MustacheViewResolver(mustacheCompiler);
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(mustache);
        PropertyMapper.Source from = map.from(mustache::getPrefix);
        Objects.requireNonNull(resolver);
        from.to(resolver::setPrefix);
        Objects.requireNonNull(mustache);
        PropertyMapper.Source from2 = map.from(mustache::getSuffix);
        Objects.requireNonNull(resolver);
        from2.to(resolver::setSuffix);
        Objects.requireNonNull(mustache);
        PropertyMapper.Source from3 = map.from(mustache::getViewNames);
        Objects.requireNonNull(resolver);
        from3.to(resolver::setViewNames);
        Objects.requireNonNull(mustache);
        PropertyMapper.Source from4 = map.from(mustache::getRequestContextAttribute);
        Objects.requireNonNull(resolver);
        from4.to(resolver::setRequestContextAttribute);
        Objects.requireNonNull(mustache);
        PropertyMapper.Source from5 = map.from(mustache::getCharsetName);
        Objects.requireNonNull(resolver);
        from5.to(resolver::setCharset);
        MustacheProperties.Reactive reactive = mustache.getReactive();
        Objects.requireNonNull(reactive);
        PropertyMapper.Source from6 = map.from(reactive::getMediaTypes);
        Objects.requireNonNull(resolver);
        from6.to(resolver::setSupportedMediaTypes);
        resolver.setOrder(2147483637);
        return resolver;
    }
}
