package org.springframework.context.annotation;

import java.util.Map;
import javax.management.MBeanServer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Configuration(proxyBeanMethods = false)
@Role(2)
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/MBeanExportConfiguration.class */
public class MBeanExportConfiguration implements ImportAware, EnvironmentAware, BeanFactoryAware {
    private static final String MBEAN_EXPORTER_BEAN_NAME = "mbeanExporter";

    @Nullable
    private AnnotationAttributes enableMBeanExport;

    @Nullable
    private Environment environment;

    @Nullable
    private BeanFactory beanFactory;

    @Override // org.springframework.context.annotation.ImportAware
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        Map<String, Object> map = importMetadata.getAnnotationAttributes(EnableMBeanExport.class.getName());
        this.enableMBeanExport = AnnotationAttributes.fromMap(map);
        if (this.enableMBeanExport == null) {
            throw new IllegalArgumentException("@EnableMBeanExport is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Override // org.springframework.context.EnvironmentAware
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Bean(name = {MBEAN_EXPORTER_BEAN_NAME})
    @Role(2)
    public AnnotationMBeanExporter mbeanExporter() {
        AnnotationMBeanExporter exporter = new AnnotationMBeanExporter();
        Assert.state(this.enableMBeanExport != null, "No EnableMBeanExport annotation found");
        setupDomain(exporter, this.enableMBeanExport);
        setupServer(exporter, this.enableMBeanExport);
        setupRegistrationPolicy(exporter, this.enableMBeanExport);
        return exporter;
    }

    private void setupDomain(AnnotationMBeanExporter exporter, AnnotationAttributes enableMBeanExport) {
        String defaultDomain = enableMBeanExport.getString("defaultDomain");
        if (StringUtils.hasLength(defaultDomain) && this.environment != null) {
            defaultDomain = this.environment.resolvePlaceholders(defaultDomain);
        }
        if (StringUtils.hasText(defaultDomain)) {
            exporter.setDefaultDomain(defaultDomain);
        }
    }

    private void setupServer(AnnotationMBeanExporter exporter, AnnotationAttributes enableMBeanExport) {
        String server = enableMBeanExport.getString("server");
        if (StringUtils.hasLength(server) && this.environment != null) {
            server = this.environment.resolvePlaceholders(server);
        }
        if (StringUtils.hasText(server)) {
            Assert.state(this.beanFactory != null, "No BeanFactory set");
            exporter.setServer((MBeanServer) this.beanFactory.getBean(server, MBeanServer.class));
        }
    }

    private void setupRegistrationPolicy(AnnotationMBeanExporter exporter, AnnotationAttributes enableMBeanExport) {
        RegistrationPolicy registrationPolicy = (RegistrationPolicy) enableMBeanExport.getEnum("registration");
        exporter.setRegistrationPolicy(registrationPolicy);
    }
}
