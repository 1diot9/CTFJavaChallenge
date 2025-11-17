package org.springframework.context;

import org.springframework.beans.factory.Aware;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/MessageSourceAware.class */
public interface MessageSourceAware extends Aware {
    void setMessageSource(MessageSource messageSource);
}
