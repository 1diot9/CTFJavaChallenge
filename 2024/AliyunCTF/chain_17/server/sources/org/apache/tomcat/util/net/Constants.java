package org.apache.tomcat.util.net;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/Constants.class */
public class Constants {
    public static final String CATALINA_BASE_PROP = "catalina.base";
    public static final String SSL_PROTO_ALL = "all";
    public static final String SSL_PROTO_TLS = "TLS";
    public static final String SSL_PROTO_TLSv1_3 = "TLSv1.3";
    public static final String SSL_PROTO_TLSv1_2 = "TLSv1.2";
    public static final String SSL_PROTO_TLSv1_1 = "TLSv1.1";
    public static final String SSL_PROTO_TLSv1_0 = "TLSv1.0";
    public static final String SSL_PROTO_TLSv1 = "TLSv1";
    public static final String SSL_PROTO_SSLv3 = "SSLv3";
    public static final String SSL_PROTO_SSLv2 = "SSLv2";
    public static final String SSL_PROTO_SSLv2Hello = "SSLv2Hello";
    public static final boolean IS_SECURITY_ENABLED;

    static {
        IS_SECURITY_ENABLED = System.getSecurityManager() != null;
    }
}
