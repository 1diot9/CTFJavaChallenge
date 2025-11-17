package org.springframework.jmx;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/MBeanServerNotFoundException.class */
public class MBeanServerNotFoundException extends JmxException {
    public MBeanServerNotFoundException(String msg) {
        super(msg);
    }

    public MBeanServerNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
