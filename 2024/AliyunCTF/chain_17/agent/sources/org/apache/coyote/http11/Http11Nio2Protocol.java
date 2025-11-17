package org.apache.coyote.http11;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.Nio2Channel;
import org.apache.tomcat.util.net.Nio2Endpoint;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/Http11Nio2Protocol.class */
public class Http11Nio2Protocol extends AbstractHttp11JsseProtocol<Nio2Channel> {
    private static final Log log = LogFactory.getLog((Class<?>) Http11Nio2Protocol.class);

    public Http11Nio2Protocol() {
        this(new Nio2Endpoint());
    }

    public Http11Nio2Protocol(Nio2Endpoint endpoint) {
        super(endpoint);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.coyote.AbstractProtocol
    public Log getLog() {
        return log;
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected String getNamePrefix() {
        if (isSSLEnabled()) {
            return "https-" + getSslImplementationShortName() + "-nio2";
        }
        return "http-nio2";
    }
}
