package org.apache.coyote.ajp;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.NioChannel;
import org.apache.tomcat.util.net.NioEndpoint;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/ajp/AjpNioProtocol.class */
public class AjpNioProtocol extends AbstractAjpProtocol<NioChannel> {
    private static final Log log = LogFactory.getLog((Class<?>) AjpNioProtocol.class);

    @Override // org.apache.coyote.AbstractProtocol
    protected Log getLog() {
        return log;
    }

    public AjpNioProtocol() {
        super(new NioEndpoint());
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected String getNamePrefix() {
        return "ajp-nio";
    }
}
