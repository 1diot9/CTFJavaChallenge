package org.apache.el;

import aQute.bnd.annotation.spi.ServiceProvider;
import jakarta.el.ELContext;
import jakarta.el.ELResolver;
import jakarta.el.ExpressionFactory;
import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;
import org.apache.el.lang.ELSupport;
import org.apache.el.lang.ExpressionBuilder;
import org.apache.el.stream.StreamELResolverImpl;
import org.apache.el.util.ExceptionUtils;
import org.apache.el.util.MessageFactory;

@ServiceProvider(ExpressionFactory.class)
/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/ExpressionFactoryImpl.class */
public class ExpressionFactoryImpl extends ExpressionFactory {
    static {
        ExceptionUtils.preload();
    }

    @Override // jakarta.el.ExpressionFactory
    public <T> T coerceToType(Object obj, Class<T> cls) {
        return (T) ELSupport.coerceToType(null, obj, cls);
    }

    @Override // jakarta.el.ExpressionFactory
    public MethodExpression createMethodExpression(ELContext context, String expression, Class<?> expectedReturnType, Class<?>[] expectedParamTypes) {
        ExpressionBuilder builder = new ExpressionBuilder(expression, context);
        return builder.createMethodExpression(expectedReturnType, expectedParamTypes);
    }

    @Override // jakarta.el.ExpressionFactory
    public ValueExpression createValueExpression(ELContext context, String expression, Class<?> expectedType) {
        if (expectedType == null) {
            throw new NullPointerException(MessageFactory.get("error.value.expectedType"));
        }
        ExpressionBuilder builder = new ExpressionBuilder(expression, context);
        return builder.createValueExpression(expectedType);
    }

    @Override // jakarta.el.ExpressionFactory
    public ValueExpression createValueExpression(Object instance, Class<?> expectedType) {
        if (expectedType == null) {
            throw new NullPointerException(MessageFactory.get("error.value.expectedType"));
        }
        return new ValueExpressionLiteral(instance, expectedType);
    }

    @Override // jakarta.el.ExpressionFactory
    public ELResolver getStreamELResolver() {
        return new StreamELResolverImpl();
    }
}
