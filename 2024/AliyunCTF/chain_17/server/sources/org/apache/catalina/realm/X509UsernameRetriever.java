package org.apache.catalina.realm;

import java.security.cert.X509Certificate;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/realm/X509UsernameRetriever.class */
public interface X509UsernameRetriever {
    String getUsername(X509Certificate x509Certificate);
}
