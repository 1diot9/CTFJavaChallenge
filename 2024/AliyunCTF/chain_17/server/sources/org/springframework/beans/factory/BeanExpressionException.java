package org.springframework.beans.factory;

import org.springframework.beans.FatalBeanException;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/BeanExpressionException.class */
public class BeanExpressionException extends FatalBeanException {
    public BeanExpressionException(String msg) {
        super(msg);
    }

    public BeanExpressionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
