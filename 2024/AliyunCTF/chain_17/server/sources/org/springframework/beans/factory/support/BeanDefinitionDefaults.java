package org.springframework.beans.factory.support;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/BeanDefinitionDefaults.class */
public class BeanDefinitionDefaults {

    @Nullable
    private Boolean lazyInit;
    private int autowireMode = 0;
    private int dependencyCheck = 0;

    @Nullable
    private String initMethodName;

    @Nullable
    private String destroyMethodName;

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = Boolean.valueOf(lazyInit);
    }

    public boolean isLazyInit() {
        return this.lazyInit != null && this.lazyInit.booleanValue();
    }

    @Nullable
    public Boolean getLazyInit() {
        return this.lazyInit;
    }

    public void setAutowireMode(int autowireMode) {
        this.autowireMode = autowireMode;
    }

    public int getAutowireMode() {
        return this.autowireMode;
    }

    public void setDependencyCheck(int dependencyCheck) {
        this.dependencyCheck = dependencyCheck;
    }

    public int getDependencyCheck() {
        return this.dependencyCheck;
    }

    public void setInitMethodName(@Nullable String initMethodName) {
        this.initMethodName = StringUtils.hasText(initMethodName) ? initMethodName : null;
    }

    @Nullable
    public String getInitMethodName() {
        return this.initMethodName;
    }

    public void setDestroyMethodName(@Nullable String destroyMethodName) {
        this.destroyMethodName = StringUtils.hasText(destroyMethodName) ? destroyMethodName : null;
    }

    @Nullable
    public String getDestroyMethodName() {
        return this.destroyMethodName;
    }
}
