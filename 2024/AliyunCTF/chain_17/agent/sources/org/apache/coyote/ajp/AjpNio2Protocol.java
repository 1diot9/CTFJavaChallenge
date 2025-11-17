package org.apache.coyote.ajp;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.Nio2Channel;
import org.apache.tomcat.util.net.Nio2Endpoint;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/ajp/AjpNio2Protocol.class */
public class AjpNio2Protocol extends AbstractAjpProtocol<Nio2Channel> {
    private static final Log log = LogFactory.getLog((Class<?>) AjpNio2Protocol.class);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.coyote.AbstractProtocol
    public Log getLog() {
        return log;
    }

    public AjpNio2Protocol() {
        super(new Nio2Endpoint());
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected String getNamePrefix() {
        return "ajp-nio2";
    }
}
