package org.springframework.jmx.export.annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;
import org.springframework.jmx.export.naming.MetadataNamingStrategy;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/annotation/AnnotationMBeanExporter.class */
public class AnnotationMBeanExporter extends MBeanExporter {
    private final AnnotationJmxAttributeSource annotationSource = new AnnotationJmxAttributeSource();
    private final MetadataNamingStrategy metadataNamingStrategy = new MetadataNamingStrategy(this.annotationSource);
    private final MetadataMBeanInfoAssembler metadataAssembler = new MetadataMBeanInfoAssembler(this.annotationSource);

    public AnnotationMBeanExporter() {
        setNamingStrategy(this.metadataNamingStrategy);
        setAssembler(this.metadataAssembler);
        setAutodetect(true);
    }

    public void setDefaultDomain(String defaultDomain) {
        this.metadataNamingStrategy.setDefaultDomain(defaultDomain);
    }

    @Override // org.springframework.jmx.export.MBeanExporter, org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);
        this.annotationSource.setBeanFactory(beanFactory);
    }
}
