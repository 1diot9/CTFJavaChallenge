package org.apache.tomcat.websocket.pojo;

import jakarta.websocket.DecodeException;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import org.apache.tomcat.websocket.WsSession;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/pojo/PojoMessageHandlerPartialBase.class */
public abstract class PojoMessageHandlerPartialBase<T> extends PojoMessageHandlerBase<T> implements MessageHandler.Partial<T> {
    private final int indexBoolean;

    public PojoMessageHandlerPartialBase(Object pojo, Method method, Session session, Object[] params, int indexPayload, boolean convert, int indexBoolean, int indexSession, long maxMessageSize) {
        super(pojo, method, session, params, indexPayload, convert, indexSession, maxMessageSize);
        this.indexBoolean = indexBoolean;
    }

    @Override // jakarta.websocket.MessageHandler.Partial
    public final void onMessage(T t, boolean last) {
        if (this.params.length == 1 && (this.params[0] instanceof DecodeException)) {
            ((WsSession) this.session).getLocal().onError(this.session, (DecodeException) this.params[0]);
            return;
        }
        Object[] objArr = (Object[]) this.params.clone();
        if (this.indexBoolean != -1) {
            objArr[this.indexBoolean] = Boolean.valueOf(last);
        }
        if (this.indexSession != -1) {
            objArr[this.indexSession] = this.session;
        }
        if (this.convert) {
            objArr[this.indexPayload] = ((ByteBuffer) t).array();
        } else {
            objArr[this.indexPayload] = t;
        }
        Object result = null;
        try {
            result = this.method.invoke(this.pojo, objArr);
        } catch (IllegalAccessException | InvocationTargetException e) {
            handlePojoMethodException(e);
        }
        processResult(result);
    }
}
