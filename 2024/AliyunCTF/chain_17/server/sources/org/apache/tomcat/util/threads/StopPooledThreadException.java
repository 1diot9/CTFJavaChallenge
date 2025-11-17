package org.apache.tomcat.util.threads;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/threads/StopPooledThreadException.class */
public class StopPooledThreadException extends RuntimeException {
    private static final long serialVersionUID = 1;

    public StopPooledThreadException(String msg) {
        super(msg, null, false, false);
    }
}
