package org.springframework.aop.aspectj.annotation;

import java.io.Serializable;
import org.springframework.aop.aspectj.SingletonAspectInstanceFactory;
import org.springframework.core.annotation.OrderUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/annotation/SingletonMetadataAwareAspectInstanceFactory.class */
public class SingletonMetadataAwareAspectInstanceFactory extends SingletonAspectInstanceFactory implements MetadataAwareAspectInstanceFactory, Serializable {
    private final AspectMetadata metadata;

    public SingletonMetadataAwareAspectInstanceFactory(Object aspectInstance, String aspectName) {
        super(aspectInstance);
        this.metadata = new AspectMetadata(aspectInstance.getClass(), aspectName);
    }

    @Override // org.springframework.aop.aspectj.annotation.MetadataAwareAspectInstanceFactory
    public final AspectMetadata getAspectMetadata() {
        return this.metadata;
    }

    @Override // org.springframework.aop.aspectj.annotation.MetadataAwareAspectInstanceFactory
    public Object getAspectCreationMutex() {
        return this;
    }

    @Override // org.springframework.aop.aspectj.SingletonAspectInstanceFactory
    protected int getOrderForAspectClass(Class<?> aspectClass) {
        return OrderUtils.getOrder(aspectClass, Integer.MAX_VALUE);
    }
}
