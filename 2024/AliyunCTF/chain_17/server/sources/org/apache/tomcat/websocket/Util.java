package org.apache.tomcat.websocket;

import ch.qos.logback.core.net.ssl.SSL;
import jakarta.websocket.CloseReason;
import jakarta.websocket.Decoder;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Extension;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.PongMessage;
import jakarta.websocket.Session;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.naming.NamingException;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.pojo.PojoMessageHandlerPartialBinary;
import org.apache.tomcat.websocket.pojo.PojoMessageHandlerWholeBinary;
import org.apache.tomcat.websocket.pojo.PojoMessageHandlerWholeText;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/Util.class */
public class Util {
    private static final StringManager sm = StringManager.getManager((Class<?>) Util.class);
    private static final Queue<SecureRandom> randoms = new ConcurrentLinkedQueue();

    private Util() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isControl(byte opCode) {
        return (opCode & 8) != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isText(byte opCode) {
        return opCode == 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isContinuation(byte opCode) {
        return opCode == 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CloseReason.CloseCode getCloseCode(int code) {
        if (code > 2999 && code < 5000) {
            return CloseReason.CloseCodes.getCloseCode(code);
        }
        switch (code) {
            case 1000:
                return CloseReason.CloseCodes.NORMAL_CLOSURE;
            case 1001:
                return CloseReason.CloseCodes.GOING_AWAY;
            case 1002:
                return CloseReason.CloseCodes.PROTOCOL_ERROR;
            case 1003:
                return CloseReason.CloseCodes.CANNOT_ACCEPT;
            case 1004:
                return CloseReason.CloseCodes.PROTOCOL_ERROR;
            case 1005:
                return CloseReason.CloseCodes.PROTOCOL_ERROR;
            case 1006:
                return CloseReason.CloseCodes.PROTOCOL_ERROR;
            case 1007:
                return CloseReason.CloseCodes.NOT_CONSISTENT;
            case 1008:
                return CloseReason.CloseCodes.VIOLATED_POLICY;
            case 1009:
                return CloseReason.CloseCodes.TOO_BIG;
            case 1010:
                return CloseReason.CloseCodes.NO_EXTENSION;
            case 1011:
                return CloseReason.CloseCodes.UNEXPECTED_CONDITION;
            case 1012:
                return CloseReason.CloseCodes.PROTOCOL_ERROR;
            case 1013:
                return CloseReason.CloseCodes.PROTOCOL_ERROR;
            case 1014:
            default:
                return CloseReason.CloseCodes.PROTOCOL_ERROR;
            case 1015:
                return CloseReason.CloseCodes.PROTOCOL_ERROR;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static byte[] generateMask() {
        SecureRandom sr = randoms.poll();
        if (sr == null) {
            try {
                sr = SecureRandom.getInstance(SSL.DEFAULT_SECURE_RANDOM_ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                sr = new SecureRandom();
            }
        }
        byte[] result = new byte[4];
        sr.nextBytes(result);
        randoms.add(sr);
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Class<?> getMessageType(MessageHandler listener) {
        return getGenericType(MessageHandler.class, listener.getClass()).getClazz();
    }

    private static Class<?> getDecoderType(Class<? extends Decoder> decoder) {
        return getGenericType(Decoder.class, decoder).getClazz();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Class<?> getEncoderType(Class<? extends Encoder> encoder) {
        return getGenericType(Encoder.class, encoder).getClazz();
    }

    private static <T> TypeResult getGenericType(Class<T> type, Class<? extends T> clazz) {
        Type[] interfaces = clazz.getGenericInterfaces();
        for (Type iface : interfaces) {
            if (iface instanceof ParameterizedType) {
                ParameterizedType pi = (ParameterizedType) iface;
                if ((pi.getRawType() instanceof Class) && type.isAssignableFrom((Class) pi.getRawType())) {
                    return getTypeParameter(clazz, pi.getActualTypeArguments()[0]);
                }
            }
        }
        Class<? super Object> superclass = clazz.getSuperclass();
        if (superclass == null) {
            return null;
        }
        TypeResult superClassTypeResult = getGenericType(type, superclass);
        int dimension = superClassTypeResult.getDimension();
        if (superClassTypeResult.getIndex() == -1 && dimension == 0) {
            return superClassTypeResult;
        }
        if (superClassTypeResult.getIndex() > -1) {
            ParameterizedType superClassType = (ParameterizedType) clazz.getGenericSuperclass();
            TypeResult result = getTypeParameter(clazz, superClassType.getActualTypeArguments()[superClassTypeResult.getIndex()]);
            result.incrementDimension(superClassTypeResult.getDimension());
            if (result.getClazz() != null && result.getDimension() > 0) {
                superClassTypeResult = result;
            } else {
                return result;
            }
        }
        if (superClassTypeResult.getDimension() > 0) {
            StringBuilder className = new StringBuilder();
            for (int i = 0; i < dimension; i++) {
                className.append('[');
            }
            className.append('L');
            className.append(superClassTypeResult.getClazz().getCanonicalName());
            className.append(';');
            try {
                Class<?> arrayClazz = Class.forName(className.toString());
                return new TypeResult(arrayClazz, -1, 0);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return null;
    }

    private static TypeResult getTypeParameter(Class<?> clazz, Type argType) {
        if (argType instanceof Class) {
            return new TypeResult((Class) argType, -1, 0);
        }
        if (argType instanceof ParameterizedType) {
            return new TypeResult((Class) ((ParameterizedType) argType).getRawType(), -1, 0);
        }
        if (argType instanceof GenericArrayType) {
            Type arrayElementType = ((GenericArrayType) argType).getGenericComponentType();
            TypeResult result = getTypeParameter(clazz, arrayElementType);
            result.incrementDimension(1);
            return result;
        }
        TypeVariable<?>[] tvs = clazz.getTypeParameters();
        for (int i = 0; i < tvs.length; i++) {
            if (tvs[i].equals(argType)) {
                return new TypeResult(null, i, 0);
            }
        }
        return null;
    }

    public static boolean isPrimitive(Class<?> clazz) {
        if (clazz.isPrimitive() || clazz.equals(Boolean.class) || clazz.equals(Byte.class) || clazz.equals(Character.class) || clazz.equals(Double.class) || clazz.equals(Float.class) || clazz.equals(Integer.class) || clazz.equals(Long.class) || clazz.equals(Short.class)) {
            return true;
        }
        return false;
    }

    public static Object coerceToType(Class<?> type, String value) {
        if (type.equals(String.class)) {
            return value;
        }
        if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
            return Boolean.valueOf(value);
        }
        if (type.equals(Byte.TYPE) || type.equals(Byte.class)) {
            return Byte.valueOf(value);
        }
        if (type.equals(Character.TYPE) || type.equals(Character.class)) {
            return Character.valueOf(value.charAt(0));
        }
        if (type.equals(Double.TYPE) || type.equals(Double.class)) {
            return Double.valueOf(value);
        }
        if (type.equals(Float.TYPE) || type.equals(Float.class)) {
            return Float.valueOf(value);
        }
        if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
            return Integer.valueOf(value);
        }
        if (type.equals(Long.TYPE) || type.equals(Long.class)) {
            return Long.valueOf(value);
        }
        if (type.equals(Short.TYPE) || type.equals(Short.class)) {
            return Short.valueOf(value);
        }
        throw new IllegalArgumentException(sm.getString("util.invalidType", value, type.getName()));
    }

    public static List<DecoderEntry> getDecoders(List<Class<? extends Decoder>> decoderClazzes, InstanceManager instanceManager) throws DeploymentException {
        List<DecoderEntry> result = new ArrayList<>();
        if (decoderClazzes != null) {
            for (Class<? extends Decoder> decoderClazz : decoderClazzes) {
                if (instanceManager == null) {
                    try {
                        decoderClazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                    } catch (IllegalArgumentException | ReflectiveOperationException | SecurityException | NamingException e) {
                        throw new DeploymentException(sm.getString("pojoMethodMapping.invalidDecoder", decoderClazz.getName()), e);
                    }
                } else {
                    Decoder instance = (Decoder) instanceManager.newInstance((Class<?>) decoderClazz);
                    instanceManager.destroyInstance(instance);
                }
                DecoderEntry entry = new DecoderEntry(getDecoderType(decoderClazz), decoderClazz);
                result.add(entry);
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Set<MessageHandlerResult> getMessageHandlers(Class<?> target, MessageHandler listener, EndpointConfig endpointConfig, Session session) {
        MessageHandler pojoMessageHandlerPartialBinary;
        Set<MessageHandlerResult> results = new HashSet<>(2);
        if (String.class.isAssignableFrom(target)) {
            MessageHandlerResult result = new MessageHandlerResult(listener, MessageHandlerResultType.TEXT);
            results.add(result);
        } else if (ByteBuffer.class.isAssignableFrom(target)) {
            MessageHandlerResult result2 = new MessageHandlerResult(listener, MessageHandlerResultType.BINARY);
            results.add(result2);
        } else if (PongMessage.class.isAssignableFrom(target)) {
            MessageHandlerResult result3 = new MessageHandlerResult(listener, MessageHandlerResultType.PONG);
            results.add(result3);
        } else if (byte[].class.isAssignableFrom(target)) {
            boolean whole = MessageHandler.Whole.class.isAssignableFrom(listener.getClass());
            if (whole) {
                pojoMessageHandlerPartialBinary = new PojoMessageHandlerWholeBinary(listener, getOnMessageMethod(listener), session, endpointConfig, matchDecoders(target, endpointConfig, true, ((WsSession) session).getInstanceManager()), new Object[1], 0, true, -1, false, -1L);
            } else {
                pojoMessageHandlerPartialBinary = new PojoMessageHandlerPartialBinary(listener, getOnMessagePartialMethod(listener), session, new Object[2], 0, true, 1, -1, -1L);
            }
            MessageHandlerResult result4 = new MessageHandlerResult(pojoMessageHandlerPartialBinary, MessageHandlerResultType.BINARY);
            results.add(result4);
        } else if (InputStream.class.isAssignableFrom(target)) {
            MessageHandlerResult result5 = new MessageHandlerResult(new PojoMessageHandlerWholeBinary(listener, getOnMessageMethod(listener), session, endpointConfig, matchDecoders(target, endpointConfig, true, ((WsSession) session).getInstanceManager()), new Object[1], 0, true, -1, true, -1L), MessageHandlerResultType.BINARY);
            results.add(result5);
        } else if (Reader.class.isAssignableFrom(target)) {
            MessageHandlerResult result6 = new MessageHandlerResult(new PojoMessageHandlerWholeText(listener, getOnMessageMethod(listener), session, endpointConfig, matchDecoders(target, endpointConfig, false, ((WsSession) session).getInstanceManager()), new Object[1], 0, true, -1, -1L), MessageHandlerResultType.TEXT);
            results.add(result6);
        } else {
            DecoderMatch decoderMatch = matchDecoders(target, endpointConfig, ((WsSession) session).getInstanceManager());
            Method m = getOnMessageMethod(listener);
            if (decoderMatch.getBinaryDecoders().size() > 0) {
                MessageHandlerResult result7 = new MessageHandlerResult(new PojoMessageHandlerWholeBinary(listener, m, session, endpointConfig, decoderMatch.getBinaryDecoders(), new Object[1], 0, false, -1, false, -1L), MessageHandlerResultType.BINARY);
                results.add(result7);
            }
            if (decoderMatch.getTextDecoders().size() > 0) {
                MessageHandlerResult result8 = new MessageHandlerResult(new PojoMessageHandlerWholeText(listener, m, session, endpointConfig, decoderMatch.getTextDecoders(), new Object[1], 0, false, -1, -1L), MessageHandlerResultType.TEXT);
                results.add(result8);
            }
        }
        if (results.size() == 0) {
            throw new IllegalArgumentException(sm.getString("wsSession.unknownHandler", listener, target));
        }
        return results;
    }

    private static List<Class<? extends Decoder>> matchDecoders(Class<?> target, EndpointConfig endpointConfig, boolean binary, InstanceManager instanceManager) {
        DecoderMatch decoderMatch = matchDecoders(target, endpointConfig, instanceManager);
        if (binary) {
            if (decoderMatch.getBinaryDecoders().size() > 0) {
                return decoderMatch.getBinaryDecoders();
            }
            return null;
        }
        if (decoderMatch.getTextDecoders().size() > 0) {
            return decoderMatch.getTextDecoders();
        }
        return null;
    }

    private static DecoderMatch matchDecoders(Class<?> target, EndpointConfig endpointConfig, InstanceManager instanceManager) {
        try {
            List<Class<? extends Decoder>> decoders = endpointConfig.getDecoders();
            List<DecoderEntry> decoderEntries = getDecoders(decoders, instanceManager);
            DecoderMatch decoderMatch = new DecoderMatch(target, decoderEntries);
            return decoderMatch;
        } catch (DeploymentException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void parseExtensionHeader(List<Extension> extensions, String header) {
        String name;
        String value;
        String[] unparsedExtensions = header.split(",");
        for (String unparsedExtension : unparsedExtensions) {
            String[] unparsedParameters = unparsedExtension.split(";");
            WsExtension extension = new WsExtension(unparsedParameters[0].trim());
            for (int i = 1; i < unparsedParameters.length; i++) {
                int equalsPos = unparsedParameters[i].indexOf(61);
                if (equalsPos == -1) {
                    name = unparsedParameters[i].trim();
                    value = null;
                } else {
                    name = unparsedParameters[i].substring(0, equalsPos).trim();
                    value = unparsedParameters[i].substring(equalsPos + 1).trim();
                    int len = value.length();
                    if (len > 1 && value.charAt(0) == '\"' && value.charAt(len - 1) == '\"') {
                        value = value.substring(1, value.length() - 1);
                    }
                }
                if (containsDelims(name) || containsDelims(value)) {
                    throw new IllegalArgumentException(sm.getString("util.notToken", name, value));
                }
                if (value != null && (value.indexOf(44) > -1 || value.indexOf(59) > -1 || value.indexOf(34) > -1 || value.indexOf(61) > -1)) {
                    throw new IllegalArgumentException(sm.getString("", value));
                }
                extension.addParameter(new WsExtensionParameter(name, value));
            }
            extensions.add(extension);
        }
    }

    private static boolean containsDelims(String input) {
        if (input == null || input.length() == 0) {
            return false;
        }
        for (char c : input.toCharArray()) {
            switch (c) {
                case '\"':
                case ',':
                case ';':
                case '=':
                    return true;
                default:
            }
        }
        return false;
    }

    private static Method getOnMessageMethod(MessageHandler listener) {
        try {
            return listener.getClass().getMethod("onMessage", Object.class);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalArgumentException(sm.getString("util.invalidMessageHandler"), e);
        }
    }

    private static Method getOnMessagePartialMethod(MessageHandler listener) {
        try {
            return listener.getClass().getMethod("onMessage", Object.class, Boolean.TYPE);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalArgumentException(sm.getString("util.invalidMessageHandler"), e);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/Util$DecoderMatch.class */
    public static class DecoderMatch {
        private final List<Class<? extends Decoder>> textDecoders = new ArrayList();
        private final List<Class<? extends Decoder>> binaryDecoders = new ArrayList();
        private final Class<?> target;

        public DecoderMatch(Class<?> target, List<DecoderEntry> decoderEntries) {
            this.target = target;
            for (DecoderEntry decoderEntry : decoderEntries) {
                if (decoderEntry.getClazz().isAssignableFrom(target)) {
                    if (Decoder.Binary.class.isAssignableFrom(decoderEntry.getDecoderClazz())) {
                        this.binaryDecoders.add(decoderEntry.getDecoderClazz());
                    } else if (Decoder.BinaryStream.class.isAssignableFrom(decoderEntry.getDecoderClazz())) {
                        this.binaryDecoders.add(decoderEntry.getDecoderClazz());
                        return;
                    } else if (Decoder.Text.class.isAssignableFrom(decoderEntry.getDecoderClazz())) {
                        this.textDecoders.add(decoderEntry.getDecoderClazz());
                    } else {
                        if (Decoder.TextStream.class.isAssignableFrom(decoderEntry.getDecoderClazz())) {
                            this.textDecoders.add(decoderEntry.getDecoderClazz());
                            return;
                        }
                        throw new IllegalArgumentException(Util.sm.getString("util.unknownDecoderType"));
                    }
                }
            }
        }

        public List<Class<? extends Decoder>> getTextDecoders() {
            return this.textDecoders;
        }

        public List<Class<? extends Decoder>> getBinaryDecoders() {
            return this.binaryDecoders;
        }

        public Class<?> getTarget() {
            return this.target;
        }

        public boolean hasMatches() {
            return this.textDecoders.size() > 0 || this.binaryDecoders.size() > 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/Util$TypeResult.class */
    public static class TypeResult {
        private final Class<?> clazz;
        private final int index;
        private int dimension;

        TypeResult(Class<?> clazz, int index, int dimension) {
            this.clazz = clazz;
            this.index = index;
            this.dimension = dimension;
        }

        public Class<?> getClazz() {
            return this.clazz;
        }

        public int getIndex() {
            return this.index;
        }

        public int getDimension() {
            return this.dimension;
        }

        public void incrementDimension(int inc) {
            this.dimension += inc;
        }
    }
}
