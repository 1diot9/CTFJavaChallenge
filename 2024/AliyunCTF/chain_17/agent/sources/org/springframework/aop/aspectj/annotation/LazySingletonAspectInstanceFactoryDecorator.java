package org.springframework.aop.aspectj.annotation;

import java.io.Serializable;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/annotation/LazySingletonAspectInstanceFactoryDecorator.class */
public class LazySingletonAspectInstanceFactoryDecorator implements MetadataAwareAspectInstanceFactory, Serializable {
    private final MetadataAwareAspectInstanceFactory maaif;

    @Nullable
    private volatile Object materialized;

    public LazySingletonAspectInstanceFactoryDecorator(MetadataAwareAspectInstanceFactory maaif) {
        Assert.notNull(maaif, "AspectInstanceFactory must not be null");
        this.maaif = maaif;
    }

    @Override // org.springframework.aop.aspectj.AspectInstanceFactory
    public Object getAspectInstance() {
        Object aspectInstance = this.materialized;
        if (aspectInstance == null) {
            Object mutex = this.maaif.getAspectCreationMutex();
            if (mutex == null) {
                aspectInstance = this.maaif.getAspectInstance();
                this.materialized = aspectInstance;
            } else {
                synchronized (mutex) {
                    aspectInstance = this.materialized;
                    if (aspectInstance == null) {
                        aspectInstance = this.maaif.getAspectInstance();
                        this.materialized = aspectInstance;
                    }
                }
            }
        }
        return aspectInstance;
    }

    public boolean isMaterialized() {
        return this.materialized != null;
    }

    @Override // org.springframework.aop.aspectj.AspectInstanceFactory
    @Nullable
    public ClassLoader getAspectClassLoader() {
        return this.maaif.getAspectClassLoader();
    }

    @Override // org.springframework.aop.aspectj.annotation.MetadataAwareAspectInstanceFactory
    public AspectMetadata getAspectMetadata() {
        return this.maaif.getAspectMetadata();
    }

    @Override // org.springframework.aop.aspectj.annotation.MetadataAwareAspectInstanceFactory
    @Nullable
    public Object getAspectCreationMutex() {
        return this.maaif.getAspectCreationMutex();
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.maaif.getOrder();
    }

    public String toString() {
        return "LazySingletonAspectInstanceFactoryDecorator: decorating " + this.maaif;
    }
}
