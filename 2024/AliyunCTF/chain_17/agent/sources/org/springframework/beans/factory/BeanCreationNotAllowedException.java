package org.springframework.beans.factory;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/BeanCreationNotAllowedException.class */
public class BeanCreationNotAllowedException extends BeanCreationException {
    public BeanCreationNotAllowedException(String beanName, String msg) {
        super(beanName, msg);
    }
}
