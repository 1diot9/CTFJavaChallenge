package org.springframework.boot.autoconfigure.admin;

import javax.management.MalformedObjectNameException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.admin.SpringApplicationAdminMXBeanRegistrar;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jmx.export.MBeanExporter;

@AutoConfiguration(after = {JmxAutoConfiguration.class})
@ConditionalOnProperty(prefix = "spring.application.admin", value = {"enabled"}, havingValue = "true", matchIfMissing = false)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/admin/SpringApplicationAdminJmxAutoConfiguration.class */
public class SpringApplicationAdminJmxAutoConfiguration {
    private static final String JMX_NAME_PROPERTY = "spring.application.admin.jmx-name";
    private static final String DEFAULT_JMX_NAME = "org.springframework.boot:type=Admin,name=SpringApplication";

    @ConditionalOnMissingBean
    @Bean
    public SpringApplicationAdminMXBeanRegistrar springApplicationAdminRegistrar(ObjectProvider<MBeanExporter> mbeanExporters, Environment environment) throws MalformedObjectNameException {
        String jmxName = environment.getProperty(JMX_NAME_PROPERTY, DEFAULT_JMX_NAME);
        if (mbeanExporters != null) {
            for (MBeanExporter mbeanExporter : mbeanExporters) {
                mbeanExporter.addExcludedBean(jmxName);
            }
        }
        return new SpringApplicationAdminMXBeanRegistrar(jmxName);
    }
}
