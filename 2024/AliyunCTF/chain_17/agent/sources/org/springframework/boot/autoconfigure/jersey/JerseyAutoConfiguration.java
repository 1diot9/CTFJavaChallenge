package org.springframework.boot.autoconfigure.jersey;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationIntrospector;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.SpringComponentProvider;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ConditionalOnMissingFilterBean;
import org.springframework.boot.autoconfigure.web.servlet.DefaultJerseyApplicationPath;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.JerseyApplicationPath;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.DynamicRegistrationBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.ClassUtils;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.filter.RequestContextFilter;

@EnableConfigurationProperties({JerseyProperties.class})
@AutoConfiguration(before = {DispatcherServletAutoConfiguration.class}, after = {JacksonAutoConfiguration.class})
@ConditionalOnClass({SpringComponentProvider.class, ServletRegistration.class})
@ConditionalOnBean(type = {"org.glassfish.jersey.server.ResourceConfig"})
@AutoConfigureOrder(Integer.MIN_VALUE)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jersey/JerseyAutoConfiguration.class */
public class JerseyAutoConfiguration implements ServletContextAware {
    private static final Log logger = LogFactory.getLog((Class<?>) JerseyAutoConfiguration.class);
    private final JerseyProperties jersey;
    private final ResourceConfig config;

    public JerseyAutoConfiguration(JerseyProperties jersey, ResourceConfig config, ObjectProvider<ResourceConfigCustomizer> customizers) {
        this.jersey = jersey;
        this.config = config;
        customizers.orderedStream().forEach(customizer -> {
            customizer.customize(this.config);
        });
    }

    @ConditionalOnMissingFilterBean({RequestContextFilter.class})
    @Bean
    public FilterRegistrationBean<RequestContextFilter> requestContextFilter() {
        FilterRegistrationBean<RequestContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RequestContextFilter());
        registration.setOrder(this.jersey.getFilter().getOrder() - 1);
        registration.setName("requestContextFilter");
        return registration;
    }

    @ConditionalOnMissingBean
    @Bean
    public JerseyApplicationPath jerseyApplicationPath() {
        return new DefaultJerseyApplicationPath(this.jersey.getApplicationPath(), this.config);
    }

    @ConditionalOnMissingBean(name = {"jerseyFilterRegistration"})
    @ConditionalOnProperty(prefix = "spring.jersey", name = {"type"}, havingValue = "filter")
    @Bean
    public FilterRegistrationBean<ServletContainer> jerseyFilterRegistration(JerseyApplicationPath applicationPath) {
        FilterRegistrationBean<ServletContainer> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ServletContainer(this.config));
        registration.setUrlPatterns(Collections.singletonList(applicationPath.getUrlMapping()));
        registration.setOrder(this.jersey.getFilter().getOrder());
        registration.addInitParameter("jersey.config.servlet.filter.contextPath", stripPattern(applicationPath.getPath()));
        addInitParameters(registration);
        registration.setName("jerseyFilter");
        registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
        return registration;
    }

    private String stripPattern(String path) {
        if (path.endsWith("/*")) {
            path = path.substring(0, path.lastIndexOf("/*"));
        }
        return path;
    }

    @ConditionalOnMissingBean(name = {"jerseyServletRegistration"})
    @ConditionalOnProperty(prefix = "spring.jersey", name = {"type"}, havingValue = "servlet", matchIfMissing = true)
    @Bean
    public ServletRegistrationBean<ServletContainer> jerseyServletRegistration(JerseyApplicationPath applicationPath) {
        ServletRegistrationBean<ServletContainer> registration = new ServletRegistrationBean<>(new ServletContainer(this.config), applicationPath.getUrlMapping());
        addInitParameters(registration);
        registration.setName(getServletRegistrationName());
        registration.setLoadOnStartup(this.jersey.getServlet().getLoadOnStartup());
        registration.setIgnoreRegistrationFailure(true);
        return registration;
    }

    private String getServletRegistrationName() {
        return ClassUtils.getUserClass(this.config.getClass()).getName();
    }

    private void addInitParameters(DynamicRegistrationBean<?> registration) {
        Map<String, String> init = this.jersey.getInit();
        Objects.requireNonNull(registration);
        init.forEach(registration::addInitParameter);
    }

    @Override // org.springframework.web.context.ServletContextAware
    public void setServletContext(ServletContext servletContext) {
        String servletRegistrationName = getServletRegistrationName();
        ServletRegistration registration = servletContext.getServletRegistration(servletRegistrationName);
        if (registration != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Configuring existing registration for Jersey servlet '" + servletRegistrationName + "'");
            }
            registration.setInitParameters(this.jersey.getInit());
        }
    }

    @Order(Integer.MIN_VALUE)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jersey/JerseyAutoConfiguration$JerseyWebApplicationInitializer.class */
    public static final class JerseyWebApplicationInitializer implements WebApplicationInitializer {
        @Override // org.springframework.web.WebApplicationInitializer
        public void onStartup(ServletContext servletContext) throws ServletException {
            servletContext.setInitParameter(ContextLoader.CONFIG_LOCATION_PARAM, "<NONE>");
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({JacksonFeature.class})
    @ConditionalOnSingleCandidate(ObjectMapper.class)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jersey/JerseyAutoConfiguration$JacksonResourceConfigCustomizer.class */
    static class JacksonResourceConfigCustomizer {
        JacksonResourceConfigCustomizer() {
        }

        @Bean
        ResourceConfigCustomizer jacksonResourceConfigCustomizer(ObjectMapper objectMapper) {
            return config -> {
                config.register(JacksonFeature.class);
                config.register(new ObjectMapperContextResolver(objectMapper), new Class[]{ContextResolver.class});
            };
        }

        @Configuration(proxyBeanMethods = false)
        @ConditionalOnClass({JakartaXmlBindAnnotationIntrospector.class, XmlElement.class})
        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jersey/JerseyAutoConfiguration$JacksonResourceConfigCustomizer$JaxbObjectMapperCustomizer.class */
        static class JaxbObjectMapperCustomizer {
            JaxbObjectMapperCustomizer() {
            }

            @Autowired
            void addJaxbAnnotationIntrospector(ObjectMapper objectMapper) {
                JakartaXmlBindAnnotationIntrospector jaxbAnnotationIntrospector = new JakartaXmlBindAnnotationIntrospector(objectMapper.getTypeFactory());
                objectMapper.setAnnotationIntrospectors(createPair(objectMapper.getSerializationConfig(), jaxbAnnotationIntrospector), createPair(objectMapper.getDeserializationConfig(), jaxbAnnotationIntrospector));
            }

            private AnnotationIntrospector createPair(MapperConfig<?> config, JakartaXmlBindAnnotationIntrospector jaxbAnnotationIntrospector) {
                return AnnotationIntrospector.pair(config.getAnnotationIntrospector(), jaxbAnnotationIntrospector);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jersey/JerseyAutoConfiguration$JacksonResourceConfigCustomizer$ObjectMapperContextResolver.class */
        public static final class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {
            private final ObjectMapper objectMapper;

            /* renamed from: getContext, reason: collision with other method in class */
            public /* bridge */ /* synthetic */ Object m1947getContext(Class type) {
                return getContext((Class<?>) type);
            }

            private ObjectMapperContextResolver(ObjectMapper objectMapper) {
                this.objectMapper = objectMapper;
            }

            public ObjectMapper getContext(Class<?> type) {
                return this.objectMapper;
            }
        }
    }
}
