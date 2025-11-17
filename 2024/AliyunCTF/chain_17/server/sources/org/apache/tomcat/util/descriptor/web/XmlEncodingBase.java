package org.apache.tomcat.util.descriptor.web;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/XmlEncodingBase.class */
public abstract class XmlEncodingBase {
    private Charset charset = StandardCharsets.UTF_8;

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public Charset getCharset() {
        return this.charset;
    }
}
