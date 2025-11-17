package org.springframework.beans.factory;

import org.springframework.beans.FatalBeanException;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/FactoryBeanNotInitializedException.class */
public class FactoryBeanNotInitializedException extends FatalBeanException {
    public FactoryBeanNotInitializedException() {
        super("FactoryBean is not fully initialized yet");
    }

    public FactoryBeanNotInitializedException(String msg) {
        super(msg);
    }
}
