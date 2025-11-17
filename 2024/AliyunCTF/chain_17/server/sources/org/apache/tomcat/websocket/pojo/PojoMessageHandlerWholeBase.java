package org.apache.tomcat.websocket.pojo;

import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.WsSession;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/pojo/PojoMessageHandlerWholeBase.class */
public abstract class PojoMessageHandlerWholeBase<T> extends PojoMessageHandlerBase<T> implements MessageHandler.Whole<T> {
    private final Log log;
    private static final StringManager sm = StringManager.getManager((Class<?>) PojoMessageHandlerWholeBase.class);
    protected final List<Decoder> decoders;

    protected abstract Object decode(T t) throws DecodeException;

    public PojoMessageHandlerWholeBase(Object pojo, Method method, Session session, Object[] params, int indexPayload, boolean convert, int indexSession, long maxMessageSize) {
        super(pojo, method, session, params, indexPayload, convert, indexSession, maxMessageSize);
        this.log = LogFactory.getLog((Class<?>) PojoMessageHandlerWholeBase.class);
        this.decoders = new ArrayList();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Decoder createDecoderInstance(Class<? extends Decoder> clazz) throws ReflectiveOperationException, NamingException {
        InstanceManager instanceManager = ((WsSession) this.session).getInstanceManager();
        if (instanceManager == null) {
            return clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        return (Decoder) instanceManager.newInstance((Class<?>) clazz);
    }

    @Override // jakarta.websocket.MessageHandler.Whole
    public final void onMessage(T message) {
        if (this.params.length == 1 && (this.params[0] instanceof DecodeException)) {
            ((WsSession) this.session).getLocal().onError(this.session, (DecodeException) this.params[0]);
            return;
        }
        try {
            Object payload = decode(message);
            if (payload == null) {
                if (this.convert) {
                    payload = convert(message);
                } else {
                    payload = message;
                }
            }
            Object[] parameters = (Object[]) this.params.clone();
            if (this.indexSession != -1) {
                parameters[this.indexSession] = this.session;
            }
            parameters[this.indexPayload] = payload;
            Object result = null;
            try {
                result = this.method.invoke(this.pojo, parameters);
            } catch (IllegalAccessException | InvocationTargetException e) {
                handlePojoMethodException(e);
            }
            processResult(result);
        } catch (DecodeException de) {
            ((WsSession) this.session).getLocal().onError(this.session, de);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onClose() {
        InstanceManager instanceManager = ((WsSession) this.session).getInstanceManager();
        for (Decoder decoder : this.decoders) {
            decoder.destroy();
            if (instanceManager != null) {
                try {
                    instanceManager.destroyInstance(decoder);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    this.log.warn(sm.getString("pojoMessageHandlerWholeBase.decodeDestoryFailed", decoder.getClass()), e);
                }
            }
        }
    }

    protected Object convert(T message) {
        return message;
    }
}
