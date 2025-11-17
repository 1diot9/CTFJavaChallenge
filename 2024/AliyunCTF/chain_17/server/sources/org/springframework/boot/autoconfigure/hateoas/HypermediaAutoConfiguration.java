package org.springframework.boot.autoconfigure.hateoas;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.client.LinkDiscoverers;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.mediatype.hal.HalConfiguration;
import org.springframework.http.MediaType;
import org.springframework.plugin.core.Plugin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@EnableConfigurationProperties({HateoasProperties.class})
@AutoConfiguration(after = {WebMvcAutoConfiguration.class, JacksonAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class})
@ConditionalOnClass({EntityModel.class, RequestMapping.class, RequestMappingHandlerAdapter.class, Plugin.class})
@ConditionalOnWebApplication
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/hateoas/HypermediaAutoConfiguration.class */
public class HypermediaAutoConfiguration {
    @ConditionalOnClass(name = {"com.fasterxml.jackson.databind.ObjectMapper"})
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.hateoas", name = {"use-hal-as-default-json-media-type"}, matchIfMissing = true)
    @Bean
    HalConfiguration applicationJsonHalConfiguration() {
        return new HalConfiguration().withMediaType(MediaType.APPLICATION_JSON);
    }

    @EnableHypermediaSupport(type = {EnableHypermediaSupport.HypermediaType.HAL})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ObjectMapper.class})
    @ConditionalOnMissingBean({LinkDiscoverers.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/hateoas/HypermediaAutoConfiguration$HypermediaConfiguration.class */
    protected static class HypermediaConfiguration {
        protected HypermediaConfiguration() {
        }
    }
}
