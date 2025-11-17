package org.springframework.boot.autoconfigure.web.servlet;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletRequest;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.ssl.SslAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.server.ErrorPageRegistrarBeanPostProcessor;
import org.springframework.boot.web.server.WebServerFactoryCustomizerBeanPostProcessor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.WebListenerRegistrar;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.ForwardedHeaderFilter;

@EnableConfigurationProperties({ServerProperties.class})
@AutoConfiguration(after = {SslAutoConfiguration.class})
@ConditionalOnClass({ServletRequest.class})
@AutoConfigureOrder(Integer.MIN_VALUE)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import({BeanPostProcessorsRegistrar.class, ServletWebServerFactoryConfiguration.EmbeddedTomcat.class, ServletWebServerFactoryConfiguration.EmbeddedJetty.class, ServletWebServerFactoryConfiguration.EmbeddedUndertow.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryAutoConfiguration.class */
public class ServletWebServerFactoryAutoConfiguration {

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryAutoConfiguration$ForwardedHeaderFilterCustomizer.class */
    public interface ForwardedHeaderFilterCustomizer {
        void customize(ForwardedHeaderFilter filter);
    }

    @Bean
    public ServletWebServerFactoryCustomizer servletWebServerFactoryCustomizer(ServerProperties serverProperties, ObjectProvider<WebListenerRegistrar> webListenerRegistrars, ObjectProvider<CookieSameSiteSupplier> cookieSameSiteSuppliers, ObjectProvider<SslBundles> sslBundles) {
        return new ServletWebServerFactoryCustomizer(serverProperties, webListenerRegistrars.orderedStream().toList(), cookieSameSiteSuppliers.orderedStream().toList(), sslBundles.getIfAvailable());
    }

    @ConditionalOnClass(name = {"org.apache.catalina.startup.Tomcat"})
    @Bean
    public TomcatServletWebServerFactoryCustomizer tomcatServletWebServerFactoryCustomizer(ServerProperties serverProperties) {
        return new TomcatServletWebServerFactoryCustomizer(serverProperties);
    }

    @ConditionalOnMissingFilterBean({ForwardedHeaderFilter.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(value = {"server.forward-headers-strategy"}, havingValue = "framework")
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryAutoConfiguration$ForwardedHeaderFilterConfiguration.class */
    static class ForwardedHeaderFilterConfiguration {
        ForwardedHeaderFilterConfiguration() {
        }

        @ConditionalOnClass(name = {"org.apache.catalina.startup.Tomcat"})
        @Bean
        ForwardedHeaderFilterCustomizer tomcatForwardedHeaderFilterCustomizer(ServerProperties serverProperties) {
            return filter -> {
                filter.setRelativeRedirects(serverProperties.getTomcat().isUseRelativeRedirects());
            };
        }

        @Bean
        FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter(ObjectProvider<ForwardedHeaderFilterCustomizer> customizerProvider) {
            ForwardedHeaderFilter filter = new ForwardedHeaderFilter();
            customizerProvider.ifAvailable(customizer -> {
                customizer.customize(filter);
            });
            FilterRegistrationBean<ForwardedHeaderFilter> registration = new FilterRegistrationBean<>(filter, new ServletRegistrationBean[0]);
            registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR);
            registration.setOrder(Integer.MIN_VALUE);
            return registration;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryAutoConfiguration$BeanPostProcessorsRegistrar.class */
    public static class BeanPostProcessorsRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware {
        private ConfigurableListableBeanFactory beanFactory;

        @Override // org.springframework.beans.factory.BeanFactoryAware
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            if (beanFactory instanceof ConfigurableListableBeanFactory) {
                ConfigurableListableBeanFactory listableBeanFactory = (ConfigurableListableBeanFactory) beanFactory;
                this.beanFactory = listableBeanFactory;
            }
        }

        @Override // org.springframework.context.annotation.ImportBeanDefinitionRegistrar
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            if (this.beanFactory == null) {
                return;
            }
            registerSyntheticBeanIfMissing(registry, "webServerFactoryCustomizerBeanPostProcessor", WebServerFactoryCustomizerBeanPostProcessor.class);
            registerSyntheticBeanIfMissing(registry, "errorPageRegistrarBeanPostProcessor", ErrorPageRegistrarBeanPostProcessor.class);
        }

        private <T> void registerSyntheticBeanIfMissing(BeanDefinitionRegistry registry, String name, Class<T> beanClass) {
            if (ObjectUtils.isEmpty((Object[]) this.beanFactory.getBeanNamesForType((Class<?>) beanClass, true, false))) {
                RootBeanDefinition beanDefinition = new RootBeanDefinition((Class<?>) beanClass);
                beanDefinition.setSynthetic(true);
                registry.registerBeanDefinition(name, beanDefinition);
            }
        }
    }
}
