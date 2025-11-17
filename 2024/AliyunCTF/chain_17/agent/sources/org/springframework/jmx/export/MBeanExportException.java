package org.springframework.jmx.export;

import org.springframework.jmx.JmxException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/MBeanExportException.class */
public class MBeanExportException extends JmxException {
    public MBeanExportException(String msg) {
        super(msg);
    }

    public MBeanExportException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
