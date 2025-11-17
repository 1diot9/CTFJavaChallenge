package org.springframework.expression;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/BeanResolver.class */
public interface BeanResolver {
    Object resolve(EvaluationContext context, String beanName) throws AccessException;
}
