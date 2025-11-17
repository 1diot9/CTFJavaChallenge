package org.apache.coyote.http11;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.NioChannel;
import org.apache.tomcat.util.net.NioEndpoint;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/Http11NioProtocol.class */
public class Http11NioProtocol extends AbstractHttp11JsseProtocol<NioChannel> {
    private static final Log log = LogFactory.getLog((Class<?>) Http11NioProtocol.class);

    public Http11NioProtocol() {
        this(new NioEndpoint());
    }

    public Http11NioProtocol(NioEndpoint endpoint) {
        super(endpoint);
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected Log getLog() {
        return log;
    }

    public void setSelectorTimeout(long timeout) {
        ((NioEndpoint) getEndpoint()).setSelectorTimeout(timeout);
    }

    public long getSelectorTimeout() {
        return ((NioEndpoint) getEndpoint()).getSelectorTimeout();
    }

    public void setPollerThreadPriority(int threadPriority) {
        ((NioEndpoint) getEndpoint()).setPollerThreadPriority(threadPriority);
    }

    public int getPollerThreadPriority() {
        return ((NioEndpoint) getEndpoint()).getPollerThreadPriority();
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected String getNamePrefix() {
        if (isSSLEnabled()) {
            return "https-" + getSslImplementationShortName() + "-nio";
        }
        return "http-nio";
    }
}
