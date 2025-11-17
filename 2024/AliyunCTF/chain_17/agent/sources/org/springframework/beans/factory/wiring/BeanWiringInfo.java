package org.springframework.beans.factory.wiring;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/wiring/BeanWiringInfo.class */
public class BeanWiringInfo {
    public static final int AUTOWIRE_BY_NAME = 1;
    public static final int AUTOWIRE_BY_TYPE = 2;

    @Nullable
    private String beanName;
    private boolean isDefaultBeanName;
    private int autowireMode;
    private boolean dependencyCheck;

    public BeanWiringInfo() {
        this.isDefaultBeanName = false;
        this.autowireMode = 0;
        this.dependencyCheck = false;
    }

    public BeanWiringInfo(String beanName) {
        this(beanName, false);
    }

    public BeanWiringInfo(String beanName, boolean isDefaultBeanName) {
        this.isDefaultBeanName = false;
        this.autowireMode = 0;
        this.dependencyCheck = false;
        Assert.hasText(beanName, "'beanName' must not be empty");
        this.beanName = beanName;
        this.isDefaultBeanName = isDefaultBeanName;
    }

    public BeanWiringInfo(int autowireMode, boolean dependencyCheck) {
        this.isDefaultBeanName = false;
        this.autowireMode = 0;
        this.dependencyCheck = false;
        if (autowireMode != 1 && autowireMode != 2) {
            throw new IllegalArgumentException("Only constants AUTOWIRE_BY_NAME and AUTOWIRE_BY_TYPE supported");
        }
        this.autowireMode = autowireMode;
        this.dependencyCheck = dependencyCheck;
    }

    public boolean indicatesAutowiring() {
        return this.beanName == null;
    }

    @Nullable
    public String getBeanName() {
        return this.beanName;
    }

    public boolean isDefaultBeanName() {
        return this.isDefaultBeanName;
    }

    public int getAutowireMode() {
        return this.autowireMode;
    }

    public boolean getDependencyCheck() {
        return this.dependencyCheck;
    }
}
