package org.springframework.beans.factory.config;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/BeanDefinitionHolder.class */
public class BeanDefinitionHolder implements BeanMetadataElement {
    private final BeanDefinition beanDefinition;
    private final String beanName;

    @Nullable
    private final String[] aliases;

    public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName) {
        this(beanDefinition, beanName, null);
    }

    public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName, @Nullable String[] aliases) {
        Assert.notNull(beanDefinition, "BeanDefinition must not be null");
        Assert.notNull(beanName, "Bean name must not be null");
        this.beanDefinition = beanDefinition;
        this.beanName = beanName;
        this.aliases = aliases;
    }

    public BeanDefinitionHolder(BeanDefinitionHolder beanDefinitionHolder) {
        Assert.notNull(beanDefinitionHolder, "BeanDefinitionHolder must not be null");
        this.beanDefinition = beanDefinitionHolder.getBeanDefinition();
        this.beanName = beanDefinitionHolder.getBeanName();
        this.aliases = beanDefinitionHolder.getAliases();
    }

    public BeanDefinition getBeanDefinition() {
        return this.beanDefinition;
    }

    public String getBeanName() {
        return this.beanName;
    }

    @Nullable
    public String[] getAliases() {
        return this.aliases;
    }

    @Override // org.springframework.beans.BeanMetadataElement
    @Nullable
    public Object getSource() {
        return this.beanDefinition.getSource();
    }

    public boolean matchesName(@Nullable String candidateName) {
        return candidateName != null && (candidateName.equals(this.beanName) || candidateName.equals(BeanFactoryUtils.transformedBeanName(this.beanName)) || ObjectUtils.containsElement(this.aliases, candidateName));
    }

    public String getShortDescription() {
        if (this.aliases == null) {
            return "Bean definition with name '" + this.beanName + "'";
        }
        return "Bean definition with name '" + this.beanName + "' and aliases [" + StringUtils.arrayToCommaDelimitedString(this.aliases) + "]";
    }

    public String getLongDescription() {
        return getShortDescription() + ": " + this.beanDefinition;
    }

    public String toString() {
        return getLongDescription();
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof BeanDefinitionHolder) {
                BeanDefinitionHolder that = (BeanDefinitionHolder) other;
                if (!this.beanDefinition.equals(that.beanDefinition) || !this.beanName.equals(that.beanName) || !ObjectUtils.nullSafeEquals(this.aliases, that.aliases)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHash(this.beanDefinition, this.beanName, this.aliases);
    }
}
