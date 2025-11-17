package org.apache.tomcat.util.net.openssl;

import java.io.Serializable;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/openssl/OpenSSLConfCmd.class */
public class OpenSSLConfCmd implements Serializable {
    private static final long serialVersionUID = 1;
    private String name = null;
    private String value = null;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
