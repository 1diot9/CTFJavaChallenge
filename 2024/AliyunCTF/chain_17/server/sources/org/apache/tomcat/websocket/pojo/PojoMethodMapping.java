package org.apache.tomcat.websocket.pojo;

import jakarta.websocket.CloseReason;
import jakarta.websocket.DecodeException;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.OnMessage;
import jakarta.websocket.PongMessage;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import java.io.InputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.DecoderEntry;
import org.apache.tomcat.websocket.Util;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/pojo/PojoMethodMapping.class */
public class PojoMethodMapping {
    private static final StringManager sm = StringManager.getManager((Class<?>) PojoMethodMapping.class);
    private final Method onOpen;
    private final Method onClose;
    private final Method onError;
    private final PojoPathParam[] onOpenParams;
    private final PojoPathParam[] onCloseParams;
    private final PojoPathParam[] onErrorParams;
    private final List<MessageHandlerInfo> onMessage = new ArrayList();
    private final String wsPath;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/pojo/PojoMethodMapping$MethodType.class */
    public enum MethodType {
        ON_OPEN,
        ON_CLOSE,
        ON_ERROR
    }

    /* JADX WARN: Code restructure failed: missing block: B:25:0x00b5, code lost:            throw new jakarta.websocket.DeploymentException(org.apache.tomcat.websocket.pojo.PojoMethodMapping.sm.getString("pojoMethodMapping.duplicateAnnotation", jakarta.websocket.OnOpen.class, r19));     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x014b, code lost:            throw new jakarta.websocket.DeploymentException(org.apache.tomcat.websocket.pojo.PojoMethodMapping.sm.getString("pojoMethodMapping.duplicateAnnotation", jakarta.websocket.OnError.class, r19));     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x01ce, code lost:            throw new jakarta.websocket.DeploymentException(org.apache.tomcat.websocket.pojo.PojoMethodMapping.sm.getString("pojoMethodMapping.duplicateAnnotation", jakarta.websocket.OnMessage.class, r19));     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public PojoMethodMapping(java.lang.Class<?> r10, java.util.List<java.lang.Class<? extends jakarta.websocket.Decoder>> r11, java.lang.String r12, org.apache.tomcat.InstanceManager r13) throws jakarta.websocket.DeploymentException {
        /*
            Method dump skipped, instructions count: 779
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.websocket.pojo.PojoMethodMapping.<init>(java.lang.Class, java.util.List, java.lang.String, org.apache.tomcat.InstanceManager):void");
    }

    private void checkPublic(Method m) throws DeploymentException {
        if (!Modifier.isPublic(m.getModifiers())) {
            throw new DeploymentException(sm.getString("pojoMethodMapping.methodNotPublic", m.getName()));
        }
    }

    private boolean isMethodOverride(Method method1, Method method2) {
        return method1.getName().equals(method2.getName()) && method1.getReturnType().equals(method2.getReturnType()) && Arrays.equals(method1.getParameterTypes(), method2.getParameterTypes());
    }

    private boolean isOverridenWithoutAnnotation(Method[] methods, Method superclazzMethod, Class<? extends Annotation> annotation) {
        for (Method method : methods) {
            if (isMethodOverride(method, superclazzMethod) && method.getAnnotation(annotation) == null) {
                return true;
            }
        }
        return false;
    }

    public String getWsPath() {
        return this.wsPath;
    }

    public Method getOnOpen() {
        return this.onOpen;
    }

    public Object[] getOnOpenArgs(Map<String, String> pathParameters, Session session, EndpointConfig config) throws DecodeException {
        return buildArgs(this.onOpenParams, pathParameters, session, config, null, null);
    }

    public Method getOnClose() {
        return this.onClose;
    }

    public Object[] getOnCloseArgs(Map<String, String> pathParameters, Session session, CloseReason closeReason) throws DecodeException {
        return buildArgs(this.onCloseParams, pathParameters, session, null, null, closeReason);
    }

    public Method getOnError() {
        return this.onError;
    }

    public Object[] getOnErrorArgs(Map<String, String> pathParameters, Session session, Throwable throwable) throws DecodeException {
        return buildArgs(this.onErrorParams, pathParameters, session, null, throwable, null);
    }

    public boolean hasMessageHandlers() {
        return !this.onMessage.isEmpty();
    }

    public Set<MessageHandler> getMessageHandlers(Object pojo, Map<String, String> pathParameters, Session session, EndpointConfig config) {
        Set<MessageHandler> result = new HashSet<>();
        for (MessageHandlerInfo messageMethod : this.onMessage) {
            result.addAll(messageMethod.getMessageHandlers(pojo, pathParameters, session, config));
        }
        return result;
    }

    private static PojoPathParam[] getPathParams(Method m, MethodType methodType) throws DeploymentException {
        if (m == null) {
            return new PojoPathParam[0];
        }
        boolean foundThrowable = false;
        Class<?>[] types = m.getParameterTypes();
        Annotation[][] paramsAnnotations = m.getParameterAnnotations();
        PojoPathParam[] result = new PojoPathParam[types.length];
        for (int i = 0; i < types.length; i++) {
            Class<?> type = types[i];
            if (type.equals(Session.class)) {
                result[i] = new PojoPathParam(type, null);
            } else if (methodType == MethodType.ON_OPEN && type.equals(EndpointConfig.class)) {
                result[i] = new PojoPathParam(type, null);
            } else if (methodType == MethodType.ON_ERROR && type.equals(Throwable.class)) {
                foundThrowable = true;
                result[i] = new PojoPathParam(type, null);
            } else if (methodType == MethodType.ON_CLOSE && type.equals(CloseReason.class)) {
                result[i] = new PojoPathParam(type, null);
            } else {
                Annotation[] paramAnnotations = paramsAnnotations[i];
                int length = paramAnnotations.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        break;
                    }
                    Annotation paramAnnotation = paramAnnotations[i2];
                    if (!paramAnnotation.annotationType().equals(PathParam.class)) {
                        i2++;
                    } else {
                        result[i] = new PojoPathParam(type, ((PathParam) paramAnnotation).value());
                        break;
                    }
                }
                if (result[i] == null) {
                    throw new DeploymentException(sm.getString("pojoMethodMapping.paramWithoutAnnotation", type, m.getName(), m.getClass().getName()));
                }
            }
        }
        if (methodType == MethodType.ON_ERROR && !foundThrowable) {
            throw new DeploymentException(sm.getString("pojoMethodMapping.onErrorNoThrowable", m.getName(), m.getDeclaringClass().getName()));
        }
        return result;
    }

    private static Object[] buildArgs(PojoPathParam[] pathParams, Map<String, String> pathParameters, Session session, EndpointConfig config, Throwable throwable, CloseReason closeReason) throws DecodeException {
        Object[] result = new Object[pathParams.length];
        for (int i = 0; i < pathParams.length; i++) {
            Class<?> type = pathParams[i].getType();
            if (type.equals(Session.class)) {
                result[i] = session;
            } else if (type.equals(EndpointConfig.class)) {
                result[i] = config;
            } else if (type.equals(Throwable.class)) {
                result[i] = throwable;
            } else if (type.equals(CloseReason.class)) {
                result[i] = closeReason;
            } else {
                String name = pathParams[i].getName();
                String value = pathParameters.get(name);
                try {
                    result[i] = Util.coerceToType(type, value);
                } catch (Exception e) {
                    throw new DecodeException(value, sm.getString("pojoMethodMapping.decodePathParamFail", value, type), e);
                }
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/pojo/PojoMethodMapping$MessageHandlerInfo.class */
    public static class MessageHandlerInfo {
        private final Method m;
        private int indexString;
        private int indexByteArray;
        private int indexByteBuffer;
        private int indexPong;
        private int indexBoolean;
        private int indexSession;
        private int indexInputStream;
        private int indexReader;
        private int indexPrimitive;
        private Map<Integer, PojoPathParam> indexPathParams = new HashMap();
        private int indexPayload;
        private Util.DecoderMatch decoderMatch;
        private long maxMessageSize;

        MessageHandlerInfo(Method m, List<DecoderEntry> decoderEntries) throws DeploymentException {
            this.indexString = -1;
            this.indexByteArray = -1;
            this.indexByteBuffer = -1;
            this.indexPong = -1;
            this.indexBoolean = -1;
            this.indexSession = -1;
            this.indexInputStream = -1;
            this.indexReader = -1;
            this.indexPrimitive = -1;
            this.indexPayload = -1;
            this.decoderMatch = null;
            this.maxMessageSize = -1L;
            this.m = m;
            Class<?>[] types = m.getParameterTypes();
            Annotation[][] paramsAnnotations = m.getParameterAnnotations();
            for (int i = 0; i < types.length; i++) {
                boolean paramFound = false;
                Annotation[] paramAnnotations = paramsAnnotations[i];
                int length = paramAnnotations.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        break;
                    }
                    Annotation paramAnnotation = paramAnnotations[i2];
                    if (!paramAnnotation.annotationType().equals(PathParam.class)) {
                        i2++;
                    } else {
                        this.indexPathParams.put(Integer.valueOf(i), new PojoPathParam(types[i], ((PathParam) paramAnnotation).value()));
                        paramFound = true;
                        break;
                    }
                }
                if (!paramFound) {
                    if (String.class.isAssignableFrom(types[i])) {
                        if (this.indexString == -1) {
                            this.indexString = i;
                        } else {
                            throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.duplicateMessageParam", m.getName(), m.getDeclaringClass().getName()));
                        }
                    } else if (Reader.class.isAssignableFrom(types[i])) {
                        if (this.indexReader == -1) {
                            this.indexReader = i;
                        } else {
                            throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.duplicateMessageParam", m.getName(), m.getDeclaringClass().getName()));
                        }
                    } else if (Boolean.TYPE == types[i]) {
                        if (this.indexBoolean == -1) {
                            this.indexBoolean = i;
                        } else {
                            throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.duplicateLastParam", m.getName(), m.getDeclaringClass().getName()));
                        }
                    } else if (ByteBuffer.class.isAssignableFrom(types[i])) {
                        if (this.indexByteBuffer == -1) {
                            this.indexByteBuffer = i;
                        } else {
                            throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.duplicateMessageParam", m.getName(), m.getDeclaringClass().getName()));
                        }
                    } else if (byte[].class == types[i]) {
                        if (this.indexByteArray == -1) {
                            this.indexByteArray = i;
                        } else {
                            throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.duplicateMessageParam", m.getName(), m.getDeclaringClass().getName()));
                        }
                    } else if (InputStream.class.isAssignableFrom(types[i])) {
                        if (this.indexInputStream == -1) {
                            this.indexInputStream = i;
                        } else {
                            throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.duplicateMessageParam", m.getName(), m.getDeclaringClass().getName()));
                        }
                    } else if (Util.isPrimitive(types[i])) {
                        if (this.indexPrimitive == -1) {
                            this.indexPrimitive = i;
                        } else {
                            throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.duplicateMessageParam", m.getName(), m.getDeclaringClass().getName()));
                        }
                    } else if (Session.class.isAssignableFrom(types[i])) {
                        if (this.indexSession == -1) {
                            this.indexSession = i;
                        } else {
                            throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.duplicateSessionParam", m.getName(), m.getDeclaringClass().getName()));
                        }
                    } else if (PongMessage.class.isAssignableFrom(types[i])) {
                        if (this.indexPong == -1) {
                            this.indexPong = i;
                        } else {
                            throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.duplicatePongMessageParam", m.getName(), m.getDeclaringClass().getName()));
                        }
                    } else {
                        if (this.decoderMatch != null && this.decoderMatch.hasMatches()) {
                            throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.duplicateMessageParam", m.getName(), m.getDeclaringClass().getName()));
                        }
                        this.decoderMatch = new Util.DecoderMatch(types[i], decoderEntries);
                        if (this.decoderMatch.hasMatches()) {
                            this.indexPayload = i;
                        } else {
                            throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.noDecoder", m.getName(), m.getDeclaringClass().getName()));
                        }
                    }
                }
            }
            if (this.indexString != -1) {
                if (this.indexPayload != -1) {
                    throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.duplicateMessageParam", m.getName(), m.getDeclaringClass().getName()));
                }
                this.indexPayload = this.indexString;
            }
            if (this.indexReader != -1) {
                if (this.indexPayload != -1) {
                    throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.duplicateMessageParam", m.getName(), m.getDeclaringClass().getName()));
                }
                this.indexPayload = this.indexReader;
            }
            if (this.indexByteArray != -1) {
                if (this.indexPayload != -1) {
                    throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.duplicateMessageParam", m.getName(), m.getDeclaringClass().getName()));
                }
                this.indexPayload = this.indexByteArray;
            }
            if (this.indexByteBuffer != -1) {
                if (this.indexPayload != -1) {
                    throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.duplicateMessageParam", m.getName(), m.getDeclaringClass().getName()));
                }
                this.indexPayload = this.indexByteBuffer;
            }
            if (this.indexInputStream != -1) {
                if (this.indexPayload != -1) {
                    throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.duplicateMessageParam", m.getName(), m.getDeclaringClass().getName()));
                }
                this.indexPayload = this.indexInputStream;
            }
            if (this.indexPrimitive != -1) {
                if (this.indexPayload != -1) {
                    throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.duplicateMessageParam", m.getName(), m.getDeclaringClass().getName()));
                }
                this.indexPayload = this.indexPrimitive;
            }
            if (this.indexPong != -1) {
                if (this.indexPayload != -1) {
                    throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.pongWithPayload", m.getName(), m.getDeclaringClass().getName()));
                }
                this.indexPayload = this.indexPong;
            }
            if (this.indexPayload == -1 && this.indexPrimitive == -1 && this.indexBoolean != -1) {
                this.indexPayload = this.indexBoolean;
                this.indexPrimitive = this.indexBoolean;
                this.indexBoolean = -1;
            }
            if (this.indexPayload == -1) {
                throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.noPayload", m.getName(), m.getDeclaringClass().getName()));
            }
            if (this.indexPong != -1 && this.indexBoolean != -1) {
                throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.partialPong", m.getName(), m.getDeclaringClass().getName()));
            }
            if (this.indexReader != -1 && this.indexBoolean != -1) {
                throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.partialReader", m.getName(), m.getDeclaringClass().getName()));
            }
            if (this.indexInputStream != -1 && this.indexBoolean != -1) {
                throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.partialInputStream", m.getName(), m.getDeclaringClass().getName()));
            }
            if (this.decoderMatch != null && this.decoderMatch.hasMatches() && this.indexBoolean != -1) {
                throw new DeploymentException(PojoMethodMapping.sm.getString("pojoMethodMapping.partialObject", m.getName(), m.getDeclaringClass().getName()));
            }
            this.maxMessageSize = ((OnMessage) m.getAnnotation(OnMessage.class)).maxMessageSize();
        }

        public boolean targetsSameWebSocketMessageType(MessageHandlerInfo otherHandler) {
            if (otherHandler == null) {
                return false;
            }
            return (isPong() && otherHandler.isPong()) || (isBinary() && otherHandler.isBinary()) || (isText() && otherHandler.isText());
        }

        private boolean isPong() {
            return this.indexPong >= 0;
        }

        private boolean isText() {
            return this.indexString >= 0 || this.indexPrimitive >= 0 || this.indexReader >= 0 || (this.decoderMatch != null && this.decoderMatch.getTextDecoders().size() > 0);
        }

        private boolean isBinary() {
            return this.indexByteArray >= 0 || this.indexByteBuffer >= 0 || this.indexInputStream >= 0 || (this.decoderMatch != null && this.decoderMatch.getBinaryDecoders().size() > 0);
        }

        public Set<MessageHandler> getMessageHandlers(Object pojo, Map<String, String> pathParameters, Session session, EndpointConfig config) {
            Object[] params = new Object[this.m.getParameterTypes().length];
            for (Map.Entry<Integer, PojoPathParam> entry : this.indexPathParams.entrySet()) {
                PojoPathParam pathParam = entry.getValue();
                String valueString = pathParameters.get(pathParam.getName());
                try {
                    Object value = Util.coerceToType(pathParam.getType(), valueString);
                    params[entry.getKey().intValue()] = value;
                } catch (Exception e) {
                    DecodeException de = new DecodeException(valueString, PojoMethodMapping.sm.getString("pojoMethodMapping.decodePathParamFail", valueString, pathParam.getType()), e);
                    params = new Object[]{de};
                }
            }
            Set<MessageHandler> results = new HashSet<>(2);
            if (this.indexBoolean == -1) {
                if (this.indexString != -1 || this.indexPrimitive != -1) {
                    MessageHandler mh = new PojoMessageHandlerWholeText(pojo, this.m, session, config, null, params, this.indexPayload, false, this.indexSession, this.maxMessageSize);
                    results.add(mh);
                } else if (this.indexReader != -1) {
                    MessageHandler mh2 = new PojoMessageHandlerWholeText(pojo, this.m, session, config, null, params, this.indexReader, true, this.indexSession, this.maxMessageSize);
                    results.add(mh2);
                } else if (this.indexByteArray != -1) {
                    MessageHandler mh3 = new PojoMessageHandlerWholeBinary(pojo, this.m, session, config, null, params, this.indexByteArray, true, this.indexSession, false, this.maxMessageSize);
                    results.add(mh3);
                } else if (this.indexByteBuffer != -1) {
                    MessageHandler mh4 = new PojoMessageHandlerWholeBinary(pojo, this.m, session, config, null, params, this.indexByteBuffer, false, this.indexSession, false, this.maxMessageSize);
                    results.add(mh4);
                } else if (this.indexInputStream != -1) {
                    MessageHandler mh5 = new PojoMessageHandlerWholeBinary(pojo, this.m, session, config, null, params, this.indexInputStream, true, this.indexSession, true, this.maxMessageSize);
                    results.add(mh5);
                } else if (this.decoderMatch != null && this.decoderMatch.hasMatches()) {
                    if (this.decoderMatch.getBinaryDecoders().size() > 0) {
                        MessageHandler mh6 = new PojoMessageHandlerWholeBinary(pojo, this.m, session, config, this.decoderMatch.getBinaryDecoders(), params, this.indexPayload, true, this.indexSession, true, this.maxMessageSize);
                        results.add(mh6);
                    }
                    if (this.decoderMatch.getTextDecoders().size() > 0) {
                        MessageHandler mh7 = new PojoMessageHandlerWholeText(pojo, this.m, session, config, this.decoderMatch.getTextDecoders(), params, this.indexPayload, true, this.indexSession, this.maxMessageSize);
                        results.add(mh7);
                    }
                } else {
                    MessageHandler mh8 = new PojoMessageHandlerWholePong(pojo, this.m, session, params, this.indexPong, false, this.indexSession);
                    results.add(mh8);
                }
            } else if (this.indexString != -1) {
                MessageHandler mh9 = new PojoMessageHandlerPartialText(pojo, this.m, session, params, this.indexString, false, this.indexBoolean, this.indexSession, this.maxMessageSize);
                results.add(mh9);
            } else if (this.indexByteArray != -1) {
                MessageHandler mh10 = new PojoMessageHandlerPartialBinary(pojo, this.m, session, params, this.indexByteArray, true, this.indexBoolean, this.indexSession, this.maxMessageSize);
                results.add(mh10);
            } else {
                MessageHandler mh11 = new PojoMessageHandlerPartialBinary(pojo, this.m, session, params, this.indexByteBuffer, false, this.indexBoolean, this.indexSession, this.maxMessageSize);
                results.add(mh11);
            }
            return results;
        }
    }
}
