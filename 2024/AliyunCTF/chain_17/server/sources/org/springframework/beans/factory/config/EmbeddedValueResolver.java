package org.springframework.beans.factory.config;

import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/EmbeddedValueResolver.class */
public class EmbeddedValueResolver implements StringValueResolver {
    private final BeanExpressionContext exprContext;

    @Nullable
    private final BeanExpressionResolver exprResolver;

    public EmbeddedValueResolver(ConfigurableBeanFactory beanFactory) {
        this.exprContext = new BeanExpressionContext(beanFactory, null);
        this.exprResolver = beanFactory.getBeanExpressionResolver();
    }

    @Override // org.springframework.util.StringValueResolver
    @Nullable
    public String resolveStringValue(String strVal) {
        String value = this.exprContext.getBeanFactory().resolveEmbeddedValue(strVal);
        if (this.exprResolver != null && value != null) {
            Object evaluated = this.exprResolver.evaluate(value, this.exprContext);
            value = evaluated != null ? evaluated.toString() : null;
        }
        return value;
    }
}
