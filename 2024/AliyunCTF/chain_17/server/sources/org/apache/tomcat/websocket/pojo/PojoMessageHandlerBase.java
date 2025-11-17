package org.apache.tomcat.websocket.pojo;

import jakarta.websocket.EncodeException;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.Session;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.websocket.WrappedMessageHandler;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/pojo/PojoMessageHandlerBase.class */
public abstract class PojoMessageHandlerBase<T> implements WrappedMessageHandler {
    protected final Object pojo;
    protected final Method method;
    protected final Session session;
    protected final Object[] params;
    protected final int indexPayload;
    protected final boolean convert;
    protected final int indexSession;
    protected final long maxMessageSize;

    public PojoMessageHandlerBase(Object pojo, Method method, Session session, Object[] params, int indexPayload, boolean convert, int indexSession, long maxMessageSize) {
        this.pojo = pojo;
        this.method = method;
        try {
            this.method.setAccessible(true);
        } catch (Exception e) {
        }
        this.session = session;
        this.params = params;
        this.indexPayload = indexPayload;
        this.convert = convert;
        this.indexSession = indexSession;
        this.maxMessageSize = maxMessageSize;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void processResult(Object result) {
        if (result == null) {
            return;
        }
        RemoteEndpoint.Basic remoteEndpoint = this.session.getBasicRemote();
        try {
            if (result instanceof String) {
                remoteEndpoint.sendText((String) result);
            } else if (result instanceof ByteBuffer) {
                remoteEndpoint.sendBinary((ByteBuffer) result);
            } else if (result instanceof byte[]) {
                remoteEndpoint.sendBinary(ByteBuffer.wrap((byte[]) result));
            } else {
                remoteEndpoint.sendObject(result);
            }
        } catch (EncodeException | IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    @Override // org.apache.tomcat.websocket.WrappedMessageHandler
    public final MessageHandler getWrappedHandler() {
        if (this.pojo instanceof MessageHandler) {
            return (MessageHandler) this.pojo;
        }
        return null;
    }

    @Override // org.apache.tomcat.websocket.WrappedMessageHandler
    public final long getMaxMessageSize() {
        return this.maxMessageSize;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void handlePojoMethodException(Throwable t) {
        Throwable t2 = ExceptionUtils.unwrapInvocationTargetException(t);
        ExceptionUtils.handleThrowable(t2);
        if (t2 instanceof RuntimeException) {
            throw ((RuntimeException) t2);
        }
        throw new RuntimeException(t2.getMessage(), t2);
    }
}
