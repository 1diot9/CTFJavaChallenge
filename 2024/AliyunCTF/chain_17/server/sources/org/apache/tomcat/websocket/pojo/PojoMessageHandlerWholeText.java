package org.apache.tomcat.websocket.pojo;

import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.List;
import javax.naming.NamingException;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.Util;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/pojo/PojoMessageHandlerWholeText.class */
public class PojoMessageHandlerWholeText extends PojoMessageHandlerWholeBase<String> {
    private static final StringManager sm = StringManager.getManager((Class<?>) PojoMessageHandlerWholeText.class);
    private final Class<?> primitiveType;

    public PojoMessageHandlerWholeText(Object pojo, Method method, Session session, EndpointConfig config, List<Class<? extends Decoder>> decoderClazzes, Object[] params, int indexPayload, boolean convert, int indexSession, long maxMessageSize) {
        super(pojo, method, session, params, indexPayload, convert, indexSession, maxMessageSize);
        if (maxMessageSize > -1 && maxMessageSize > session.getMaxTextMessageBufferSize()) {
            if (maxMessageSize > 2147483647L) {
                throw new IllegalArgumentException(sm.getString("pojoMessageHandlerWhole.maxBufferSize"));
            }
            session.setMaxTextMessageBufferSize((int) maxMessageSize);
        }
        Class<?> type = method.getParameterTypes()[indexPayload];
        if (Util.isPrimitive(type)) {
            this.primitiveType = type;
            return;
        }
        this.primitiveType = null;
        if (decoderClazzes != null) {
            try {
                for (Class<? extends Decoder> decoderClazz : decoderClazzes) {
                    if (Decoder.Text.class.isAssignableFrom(decoderClazz)) {
                        Decoder.Text<?> decoder = (Decoder.Text) createDecoderInstance(decoderClazz);
                        decoder.init(config);
                        this.decoders.add(decoder);
                    } else if (Decoder.TextStream.class.isAssignableFrom(decoderClazz)) {
                        Decoder.TextStream<?> decoder2 = (Decoder.TextStream) createDecoderInstance(decoderClazz);
                        decoder2.init(config);
                        this.decoders.add(decoder2);
                    }
                }
            } catch (ReflectiveOperationException | NamingException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.websocket.pojo.PojoMessageHandlerWholeBase
    public Object decode(String message) throws DecodeException {
        if (this.primitiveType != null) {
            return Util.coerceToType(this.primitiveType, message);
        }
        for (Decoder decoder : this.decoders) {
            if (decoder instanceof Decoder.Text) {
                if (((Decoder.Text) decoder).willDecode(message)) {
                    return ((Decoder.Text) decoder).decode(message);
                }
            } else {
                StringReader r = new StringReader(message);
                try {
                    return ((Decoder.TextStream) decoder).decode(r);
                } catch (IOException ioe) {
                    throw new DecodeException(message, sm.getString("pojoMessageHandlerWhole.decodeIoFail"), ioe);
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.websocket.pojo.PojoMessageHandlerWholeBase
    public Object convert(String message) {
        return new StringReader(message);
    }
}
