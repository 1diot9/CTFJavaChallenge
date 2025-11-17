package org.apache.catalina;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/CredentialHandler.class */
public interface CredentialHandler {
    boolean matches(String str, String str2);

    String mutate(String str);
}
