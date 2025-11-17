package org.springframework.beans.factory;

import org.springframework.beans.FatalBeanException;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/BeanInitializationException.class */
public class BeanInitializationException extends FatalBeanException {
    public BeanInitializationException(String msg) {
        super(msg);
    }

    public BeanInitializationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
