package org.springframework.aop.scope;

import java.io.Serializable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/scope/DefaultScopedObject.class */
public class DefaultScopedObject implements ScopedObject, Serializable {
    private final ConfigurableBeanFactory beanFactory;
    private final String targetBeanName;

    public DefaultScopedObject(ConfigurableBeanFactory beanFactory, String targetBeanName) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        Assert.hasText(targetBeanName, "'targetBeanName' must not be empty");
        this.beanFactory = beanFactory;
        this.targetBeanName = targetBeanName;
    }

    @Override // org.springframework.aop.scope.ScopedObject
    public Object getTargetObject() {
        return this.beanFactory.getBean(this.targetBeanName);
    }

    @Override // org.springframework.aop.scope.ScopedObject
    public void removeFromScope() {
        this.beanFactory.destroyScopedBean(this.targetBeanName);
    }
}
