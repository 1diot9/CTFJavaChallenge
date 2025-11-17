package org.springframework.context.expression;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.expression.AccessException;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/expression/BeanFactoryResolver.class */
public class BeanFactoryResolver implements BeanResolver {
    private final BeanFactory beanFactory;

    public BeanFactoryResolver(BeanFactory beanFactory) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
    }

    @Override // org.springframework.expression.BeanResolver
    public Object resolve(EvaluationContext context, String beanName) throws AccessException {
        try {
            return this.beanFactory.getBean(beanName);
        } catch (BeansException ex) {
            throw new AccessException("Could not resolve bean reference against BeanFactory", ex);
        }
    }
}
