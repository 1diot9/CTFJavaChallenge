package org.springframework.context.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/FullyQualifiedAnnotationBeanNameGenerator.class */
public class FullyQualifiedAnnotationBeanNameGenerator extends AnnotationBeanNameGenerator {
    public static final FullyQualifiedAnnotationBeanNameGenerator INSTANCE = new FullyQualifiedAnnotationBeanNameGenerator();

    @Override // org.springframework.context.annotation.AnnotationBeanNameGenerator
    protected String buildDefaultBeanName(BeanDefinition definition) {
        String beanClassName = definition.getBeanClassName();
        Assert.state(beanClassName != null, "No bean class name set");
        return beanClassName;
    }
}
