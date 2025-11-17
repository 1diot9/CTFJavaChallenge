package org.springframework.jmx.export;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/UnableToRegisterMBeanException.class */
public class UnableToRegisterMBeanException extends MBeanExportException {
    public UnableToRegisterMBeanException(String msg) {
        super(msg);
    }

    public UnableToRegisterMBeanException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
