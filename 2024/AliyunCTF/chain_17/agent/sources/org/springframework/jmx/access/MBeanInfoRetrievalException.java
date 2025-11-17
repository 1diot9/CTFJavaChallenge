package org.springframework.jmx.access;

import org.springframework.jmx.JmxException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/access/MBeanInfoRetrievalException.class */
public class MBeanInfoRetrievalException extends JmxException {
    public MBeanInfoRetrievalException(String msg) {
        super(msg);
    }

    public MBeanInfoRetrievalException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
