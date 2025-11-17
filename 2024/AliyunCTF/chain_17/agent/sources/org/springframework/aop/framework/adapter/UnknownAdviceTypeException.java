package org.springframework.aop.framework.adapter;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/adapter/UnknownAdviceTypeException.class */
public class UnknownAdviceTypeException extends IllegalArgumentException {
    public UnknownAdviceTypeException(Object advice) {
        super("Advice object [" + advice + "] is neither a supported subinterface of [org.aopalliance.aop.Advice] nor an [org.springframework.aop.Advisor]");
    }

    public UnknownAdviceTypeException(String message) {
        super(message);
    }
}
