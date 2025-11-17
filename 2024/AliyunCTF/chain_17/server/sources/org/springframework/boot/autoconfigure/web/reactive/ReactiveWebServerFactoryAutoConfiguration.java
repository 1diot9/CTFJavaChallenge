package org.springframework.boot.autoconfigure.web.reactive;

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
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.server.WebServerFactoryCustomizerBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;

@EnableConfigurationProperties({ServerProperties.class})
@AutoConfiguration
@ConditionalOnClass({ReactiveHttpInputMessage.class})
@AutoConfigureOrder(Integer.MIN_VALUE)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@Import({BeanPostProcessorsRegistrar.class, ReactiveWebServerFactoryConfiguration.EmbeddedTomcat.class, ReactiveWebServerFactoryConfiguration.EmbeddedJetty.class, ReactiveWebServerFactoryConfiguration.EmbeddedUndertow.class, ReactiveWebServerFactoryConfiguration.EmbeddedNetty.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveWebServerFactoryAutoConfiguration.class */
public class ReactiveWebServerFactoryAutoConfiguration {
    @Bean
    public ReactiveWebServerFactoryCustomizer reactiveWebServerFactoryCustomizer(ServerProperties serverProperties, ObjectProvider<SslBundles> sslBundles) {
        return new ReactiveWebServerFactoryCustomizer(serverProperties, sslBundles.getIfAvailable());
    }

    @ConditionalOnClass(name = {"org.apache.catalina.startup.Tomcat"})
    @Bean
    public TomcatReactiveWebServerFactoryCustomizer tomcatReactiveWebServerFactoryCustomizer(ServerProperties serverProperties) {
        return new TomcatReactiveWebServerFactoryCustomizer(serverProperties);
    }

    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = {"server.forward-headers-strategy"}, havingValue = "framework")
    @Bean
    public ForwardedHeaderTransformer forwardedHeaderTransformer() {
        return new ForwardedHeaderTransformer();
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveWebServerFactoryAutoConfiguration$BeanPostProcessorsRegistrar.class */
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
