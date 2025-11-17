package org.springframework.context;

import org.springframework.beans.FatalBeanException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/ApplicationContextException.class */
public class ApplicationContextException extends FatalBeanException {
    public ApplicationContextException(String msg) {
        super(msg);
    }

    public ApplicationContextException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
