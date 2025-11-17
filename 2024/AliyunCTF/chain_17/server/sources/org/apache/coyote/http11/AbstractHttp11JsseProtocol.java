package org.apache.coyote.http11;

import org.apache.tomcat.util.net.AbstractJsseEndpoint;
import org.apache.tomcat.util.net.openssl.OpenSSLImplementation;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/AbstractHttp11JsseProtocol.class */
public abstract class AbstractHttp11JsseProtocol<S> extends AbstractHttp11Protocol<S> {
    public AbstractHttp11JsseProtocol(AbstractJsseEndpoint<S, ?> endpoint) {
        super(endpoint);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.coyote.http11.AbstractHttp11Protocol, org.apache.coyote.AbstractProtocol
    public AbstractJsseEndpoint<S, ?> getEndpoint() {
        return (AbstractJsseEndpoint) super.getEndpoint();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getSslImplementationShortName() {
        if (OpenSSLImplementation.class.getName().equals(getSslImplementationName())) {
            return "openssl";
        }
        if (getSslImplementationName() != null && getSslImplementationName().endsWith(".panama.OpenSSLImplementation")) {
            return "opensslffm";
        }
        return "jsse";
    }

    public String getSslImplementationName() {
        return getEndpoint().getSslImplementationName();
    }

    public void setSslImplementationName(String s) {
        getEndpoint().setSslImplementationName(s);
    }

    public int getSniParseLimit() {
        return getEndpoint().getSniParseLimit();
    }

    public void setSniParseLimit(int sniParseLimit) {
        getEndpoint().setSniParseLimit(sniParseLimit);
    }
}
