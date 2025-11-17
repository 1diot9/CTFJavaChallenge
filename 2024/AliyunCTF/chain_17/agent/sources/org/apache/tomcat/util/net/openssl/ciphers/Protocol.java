package org.apache.tomcat.util.net.openssl.ciphers;

import org.apache.tomcat.util.net.Constants;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/openssl/ciphers/Protocol.class */
public enum Protocol {
    SSLv3("SSLv3"),
    SSLv2("SSLv2"),
    TLSv1("TLSv1"),
    TLSv1_2("TLSv1.2"),
    TLSv1_3(Constants.SSL_PROTO_TLSv1_3);

    private final String openSSLName;

    Protocol(String openSSLName) {
        this.openSSLName = openSSLName;
    }

    String getOpenSSLName() {
        return this.openSSLName;
    }
}
