package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.parsing.DefaultsDefinition;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/xml/DocumentDefaultsDefinition.class */
public class DocumentDefaultsDefinition implements DefaultsDefinition {

    @Nullable
    private String lazyInit;

    @Nullable
    private String merge;

    @Nullable
    private String autowire;

    @Nullable
    private String autowireCandidates;

    @Nullable
    private String initMethod;

    @Nullable
    private String destroyMethod;

    @Nullable
    private Object source;

    public void setLazyInit(@Nullable String lazyInit) {
        this.lazyInit = lazyInit;
    }

    @Nullable
    public String getLazyInit() {
        return this.lazyInit;
    }

    public void setMerge(@Nullable String merge) {
        this.merge = merge;
    }

    @Nullable
    public String getMerge() {
        return this.merge;
    }

    public void setAutowire(@Nullable String autowire) {
        this.autowire = autowire;
    }

    @Nullable
    public String getAutowire() {
        return this.autowire;
    }

    public void setAutowireCandidates(@Nullable String autowireCandidates) {
        this.autowireCandidates = autowireCandidates;
    }

    @Nullable
    public String getAutowireCandidates() {
        return this.autowireCandidates;
    }

    public void setInitMethod(@Nullable String initMethod) {
        this.initMethod = initMethod;
    }

    @Nullable
    public String getInitMethod() {
        return this.initMethod;
    }

    public void setDestroyMethod(@Nullable String destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    @Nullable
    public String getDestroyMethod() {
        return this.destroyMethod;
    }

    public void setSource(@Nullable Object source) {
        this.source = source;
    }

    @Override // org.springframework.beans.BeanMetadataElement
    @Nullable
    public Object getSource() {
        return this.source;
    }
}
