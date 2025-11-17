package org.apache.tomcat.util.net.openssl.ciphers;

import org.apache.tomcat.util.net.Constants;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/openssl/ciphers/Protocol.class */
public enum Protocol {
    SSLv3(Constants.SSL_PROTO_SSLv3),
    SSLv2(Constants.SSL_PROTO_SSLv2),
    TLSv1(Constants.SSL_PROTO_TLSv1),
    TLSv1_2(Constants.SSL_PROTO_TLSv1_2),
    TLSv1_3(Constants.SSL_PROTO_TLSv1_3);

    private final String openSSLName;

    Protocol(String openSSLName) {
        this.openSSLName = openSSLName;
    }

    String getOpenSSLName() {
        return this.openSSLName;
    }
}
