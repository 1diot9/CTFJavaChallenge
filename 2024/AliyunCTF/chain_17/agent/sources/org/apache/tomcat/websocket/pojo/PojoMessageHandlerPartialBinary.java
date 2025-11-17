package org.apache.tomcat.websocket.pojo;

import jakarta.websocket.Session;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/pojo/PojoMessageHandlerPartialBinary.class */
public class PojoMessageHandlerPartialBinary extends PojoMessageHandlerPartialBase<ByteBuffer> {
    public PojoMessageHandlerPartialBinary(Object pojo, Method method, Session session, Object[] params, int indexPayload, boolean convert, int indexBoolean, int indexSession, long maxMessageSize) {
        super(pojo, method, session, params, indexPayload, convert, indexBoolean, indexSession, maxMessageSize);
    }
}
