package org.springframework.beans.factory.parsing;

import org.springframework.beans.factory.BeanDefinitionStoreException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/parsing/BeanDefinitionParsingException.class */
public class BeanDefinitionParsingException extends BeanDefinitionStoreException {
    public BeanDefinitionParsingException(Problem problem) {
        super(problem.getResourceDescription(), problem.toString(), problem.getRootCause());
    }
}
