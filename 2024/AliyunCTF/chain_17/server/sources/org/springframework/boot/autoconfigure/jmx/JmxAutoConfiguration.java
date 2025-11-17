package org.springframework.boot.autoconfigure.jmx;

import javax.management.MBeanServer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.export.naming.ObjectNamingStrategy;
import org.springframework.jmx.support.MBeanServerFactoryBean;
import org.springframework.util.StringUtils;

@EnableConfigurationProperties({JmxProperties.class})
@AutoConfiguration
@ConditionalOnClass({MBeanExporter.class})
@ConditionalOnProperty(prefix = "spring.jmx", name = {"enabled"}, havingValue = "true")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jmx/JmxAutoConfiguration.class */
public class JmxAutoConfiguration {
    private final JmxProperties properties;

    public JmxAutoConfiguration(JmxProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnMissingBean(value = {MBeanExporter.class}, search = SearchStrategy.CURRENT)
    @Bean
    @Primary
    public AnnotationMBeanExporter mbeanExporter(ObjectNamingStrategy namingStrategy, BeanFactory beanFactory) {
        AnnotationMBeanExporter exporter = new AnnotationMBeanExporter();
        exporter.setRegistrationPolicy(this.properties.getRegistrationPolicy());
        exporter.setNamingStrategy(namingStrategy);
        String serverBean = this.properties.getServer();
        if (StringUtils.hasLength(serverBean)) {
            exporter.setServer((MBeanServer) beanFactory.getBean(serverBean, MBeanServer.class));
        }
        exporter.setEnsureUniqueRuntimeObjectNames(this.properties.isUniqueNames());
        return exporter;
    }

    @ConditionalOnMissingBean(value = {ObjectNamingStrategy.class}, search = SearchStrategy.CURRENT)
    @Bean
    public ParentAwareNamingStrategy objectNamingStrategy() {
        ParentAwareNamingStrategy namingStrategy = new ParentAwareNamingStrategy(new AnnotationJmxAttributeSource());
        String defaultDomain = this.properties.getDefaultDomain();
        if (StringUtils.hasLength(defaultDomain)) {
            namingStrategy.setDefaultDomain(defaultDomain);
        }
        namingStrategy.setEnsureUniqueRuntimeObjectNames(this.properties.isUniqueNames());
        return namingStrategy;
    }

    @ConditionalOnMissingBean
    @Bean
    public MBeanServer mbeanServer() {
        MBeanServerFactoryBean factory = new MBeanServerFactoryBean();
        factory.setLocateExistingServerIfPossible(true);
        factory.afterPropertiesSet();
        return factory.getObject();
    }
}
