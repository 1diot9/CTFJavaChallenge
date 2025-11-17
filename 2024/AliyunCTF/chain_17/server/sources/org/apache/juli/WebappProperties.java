package org.apache.juli;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/juli/WebappProperties.class */
public interface WebappProperties {
    String getWebappName();

    String getHostName();

    String getServiceName();

    boolean hasLoggingConfig();
}
