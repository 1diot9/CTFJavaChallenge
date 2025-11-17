package org.apache.tomcat.websocket.pojo;

import jakarta.websocket.PongMessage;
import jakarta.websocket.Session;
import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/pojo/PojoMessageHandlerWholePong.class */
public class PojoMessageHandlerWholePong extends PojoMessageHandlerWholeBase<PongMessage> {
    public PojoMessageHandlerWholePong(Object pojo, Method method, Session session, Object[] params, int indexPayload, boolean convert, int indexSession) {
        super(pojo, method, session, params, indexPayload, convert, indexSession, -1L);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.websocket.pojo.PojoMessageHandlerWholeBase
    public Object decode(PongMessage message) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.websocket.pojo.PojoMessageHandlerWholeBase
    public void onClose() {
    }
}
