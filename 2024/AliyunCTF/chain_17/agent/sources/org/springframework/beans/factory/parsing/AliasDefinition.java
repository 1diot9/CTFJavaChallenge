package org.springframework.beans.factory.parsing;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/parsing/AliasDefinition.class */
public class AliasDefinition implements BeanMetadataElement {
    private final String beanName;
    private final String alias;

    @Nullable
    private final Object source;

    public AliasDefinition(String beanName, String alias) {
        this(beanName, alias, null);
    }

    public AliasDefinition(String beanName, String alias, @Nullable Object source) {
        Assert.notNull(beanName, "Bean name must not be null");
        Assert.notNull(alias, "Alias must not be null");
        this.beanName = beanName;
        this.alias = alias;
        this.source = source;
    }

    public final String getBeanName() {
        return this.beanName;
    }

    public final String getAlias() {
        return this.alias;
    }

    @Override // org.springframework.beans.BeanMetadataElement
    @Nullable
    public final Object getSource() {
        return this.source;
    }
}
