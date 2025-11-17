package org.springframework.beans.factory.parsing;

import org.springframework.beans.factory.parsing.ParseState;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/parsing/BeanEntry.class */
public class BeanEntry implements ParseState.Entry {
    private final String beanDefinitionName;

    public BeanEntry(String beanDefinitionName) {
        this.beanDefinitionName = beanDefinitionName;
    }

    public String toString() {
        return "Bean '" + this.beanDefinitionName + "'";
    }
}
