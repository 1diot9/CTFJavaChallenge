package org.apache.tomcat.websocket;

import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.CloseReason;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Extension;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.PongMessage;
import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.SendResult;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import jakarta.websocket.server.ServerEndpointConfig;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.server.DefaultServerEndpointConfigurator;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsSession.class */
public class WsSession implements Session {
    private static final boolean SEC_CONFIGURATOR_USES_IMPL_DEFAULT;
    private final Endpoint localEndpoint;
    private final WsRemoteEndpointImplBase wsRemoteEndpoint;
    private final RemoteEndpoint.Async remoteEndpointAsync;
    private final RemoteEndpoint.Basic remoteEndpointBasic;
    private final ClassLoader applicationClassLoader;
    private final WsWebSocketContainer webSocketContainer;
    private final URI requestUri;
    private final Map<String, List<String>> requestParameterMap;
    private final String queryString;
    private final Principal userPrincipal;
    private final EndpointConfig endpointConfig;
    private final List<Extension> negotiatedExtensions;
    private final String subProtocol;
    private final Map<String, String> pathParameters;
    private final boolean secure;
    private final String httpSessionId;
    private final String id;
    private volatile int maxBinaryMessageBufferSize;
    private volatile int maxTextMessageBufferSize;
    private volatile long maxIdleTimeout;
    private WsFrameBase wsFrame;
    private static final StringManager sm = StringManager.getManager((Class<?>) WsSession.class);
    private static final byte[] ELLIPSIS_BYTES = "…".getBytes(StandardCharsets.UTF_8);
    private static final int ELLIPSIS_BYTES_LEN = ELLIPSIS_BYTES.length;
    private static AtomicLong ids = new AtomicLong(0);
    private final Log log = LogFactory.getLog((Class<?>) WsSession.class);
    private volatile MessageHandler textMessageHandler = null;
    private volatile MessageHandler binaryMessageHandler = null;
    private volatile MessageHandler.Whole<PongMessage> pongMessageHandler = null;
    private AtomicReference<State> state = new AtomicReference<>(State.OPEN);
    private final Map<String, Object> userProperties = new ConcurrentHashMap();
    private volatile long lastActiveRead = System.currentTimeMillis();
    private volatile long lastActiveWrite = System.currentTimeMillis();
    private Map<FutureToSendHandler, FutureToSendHandler> futures = new ConcurrentHashMap();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsSession$State.class */
    public enum State {
        OPEN,
        OUTPUT_CLOSING,
        OUTPUT_CLOSED,
        CLOSING,
        CLOSED
    }

    static {
        ServerEndpointConfig.Builder builder = ServerEndpointConfig.Builder.create(Object.class, "/");
        ServerEndpointConfig sec = builder.build();
        SEC_CONFIGURATOR_USES_IMPL_DEFAULT = sec.getConfigurator().getClass().equals(DefaultServerEndpointConfigurator.class);
    }

    public WsSession(ClientEndpointHolder clientEndpointHolder, WsRemoteEndpointImplBase wsRemoteEndpoint, WsWebSocketContainer wsWebSocketContainer, List<Extension> negotiatedExtensions, String subProtocol, Map<String, String> pathParameters, boolean secure, ClientEndpointConfig clientEndpointConfig) throws DeploymentException {
        this.maxBinaryMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
        this.maxTextMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
        this.maxIdleTimeout = 0L;
        this.wsRemoteEndpoint = wsRemoteEndpoint;
        this.wsRemoteEndpoint.setSession(this);
        this.remoteEndpointAsync = new WsRemoteEndpointAsync(wsRemoteEndpoint);
        this.remoteEndpointBasic = new WsRemoteEndpointBasic(wsRemoteEndpoint);
        this.webSocketContainer = wsWebSocketContainer;
        this.applicationClassLoader = Thread.currentThread().getContextClassLoader();
        wsRemoteEndpoint.setSendTimeout(wsWebSocketContainer.getDefaultAsyncSendTimeout());
        this.maxBinaryMessageBufferSize = this.webSocketContainer.getDefaultMaxBinaryMessageBufferSize();
        this.maxTextMessageBufferSize = this.webSocketContainer.getDefaultMaxTextMessageBufferSize();
        this.maxIdleTimeout = this.webSocketContainer.getDefaultMaxSessionIdleTimeout();
        this.requestUri = null;
        this.requestParameterMap = Collections.emptyMap();
        this.queryString = null;
        this.userPrincipal = null;
        this.httpSessionId = null;
        this.negotiatedExtensions = negotiatedExtensions;
        if (subProtocol == null) {
            this.subProtocol = "";
        } else {
            this.subProtocol = subProtocol;
        }
        this.pathParameters = pathParameters;
        this.secure = secure;
        this.wsRemoteEndpoint.setEncoders(clientEndpointConfig);
        this.endpointConfig = clientEndpointConfig;
        this.userProperties.putAll(this.endpointConfig.getUserProperties());
        this.id = Long.toHexString(ids.getAndIncrement());
        this.localEndpoint = clientEndpointHolder.getInstance(getInstanceManager());
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("wsSession.created", this.id));
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x01be  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x01e5  */
    /* JADX WARN: Removed duplicated region for block: B:21:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:22:0x01ca  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public WsSession(org.apache.tomcat.websocket.WsRemoteEndpointImplBase r9, org.apache.tomcat.websocket.WsWebSocketContainer r10, java.net.URI r11, java.util.Map<java.lang.String, java.util.List<java.lang.String>> r12, java.lang.String r13, java.security.Principal r14, java.lang.String r15, java.util.List<jakarta.websocket.Extension> r16, java.lang.String r17, java.util.Map<java.lang.String, java.lang.String> r18, boolean r19, jakarta.websocket.server.ServerEndpointConfig r20) throws jakarta.websocket.DeploymentException {
        /*
            Method dump skipped, instructions count: 514
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.websocket.WsSession.<init>(org.apache.tomcat.websocket.WsRemoteEndpointImplBase, org.apache.tomcat.websocket.WsWebSocketContainer, java.net.URI, java.util.Map, java.lang.String, java.security.Principal, java.lang.String, java.util.List, java.lang.String, java.util.Map, boolean, jakarta.websocket.server.ServerEndpointConfig):void");
    }

    private boolean isDefaultConfigurator(ServerEndpointConfig.Configurator configurator) {
        if (configurator.getClass().equals(DefaultServerEndpointConfigurator.class)) {
            return true;
        }
        if (SEC_CONFIGURATOR_USES_IMPL_DEFAULT && configurator.getClass().equals(ServerEndpointConfig.Configurator.class)) {
            return true;
        }
        return false;
    }

    public InstanceManager getInstanceManager() {
        return this.webSocketContainer.getInstanceManager(this.applicationClassLoader);
    }

    @Override // jakarta.websocket.Session
    public WebSocketContainer getContainer() {
        checkState();
        return this.webSocketContainer;
    }

    @Override // jakarta.websocket.Session
    public void addMessageHandler(MessageHandler listener) {
        Class<?> target = Util.getMessageType(listener);
        doAddMessageHandler(target, listener);
    }

    @Override // jakarta.websocket.Session
    public <T> void addMessageHandler(Class<T> clazz, MessageHandler.Partial<T> handler) throws IllegalStateException {
        doAddMessageHandler(clazz, handler);
    }

    @Override // jakarta.websocket.Session
    public <T> void addMessageHandler(Class<T> clazz, MessageHandler.Whole<T> handler) throws IllegalStateException {
        doAddMessageHandler(clazz, handler);
    }

    private void doAddMessageHandler(Class<?> target, MessageHandler listener) {
        checkState();
        Set<MessageHandlerResult> mhResults = Util.getMessageHandlers(target, listener, this.endpointConfig, this);
        for (MessageHandlerResult mhResult : mhResults) {
            switch (mhResult.getType()) {
                case TEXT:
                    if (this.textMessageHandler != null) {
                        throw new IllegalStateException(sm.getString("wsSession.duplicateHandlerText"));
                    }
                    this.textMessageHandler = mhResult.getHandler();
                    break;
                case BINARY:
                    if (this.binaryMessageHandler != null) {
                        throw new IllegalStateException(sm.getString("wsSession.duplicateHandlerBinary"));
                    }
                    this.binaryMessageHandler = mhResult.getHandler();
                    break;
                case PONG:
                    if (this.pongMessageHandler != null) {
                        throw new IllegalStateException(sm.getString("wsSession.duplicateHandlerPong"));
                    }
                    MessageHandler handler = mhResult.getHandler();
                    if (handler instanceof MessageHandler.Whole) {
                        this.pongMessageHandler = (MessageHandler.Whole) handler;
                        break;
                    } else {
                        throw new IllegalStateException(sm.getString("wsSession.invalidHandlerTypePong"));
                    }
                default:
                    throw new IllegalArgumentException(sm.getString("wsSession.unknownHandlerType", listener, mhResult.getType()));
            }
        }
    }

    @Override // jakarta.websocket.Session
    public Set<MessageHandler> getMessageHandlers() {
        checkState();
        Set<MessageHandler> result = new HashSet<>();
        if (this.binaryMessageHandler != null) {
            result.add(this.binaryMessageHandler);
        }
        if (this.textMessageHandler != null) {
            result.add(this.textMessageHandler);
        }
        if (this.pongMessageHandler != null) {
            result.add(this.pongMessageHandler);
        }
        return result;
    }

    @Override // jakarta.websocket.Session
    public void removeMessageHandler(MessageHandler listener) {
        checkState();
        if (listener == null) {
            return;
        }
        MessageHandler wrapped = null;
        if (listener instanceof WrappedMessageHandler) {
            wrapped = ((WrappedMessageHandler) listener).getWrappedHandler();
        }
        if (wrapped == null) {
            wrapped = listener;
        }
        boolean removed = false;
        if (wrapped.equals(this.textMessageHandler) || listener.equals(this.textMessageHandler)) {
            this.textMessageHandler = null;
            removed = true;
        }
        if (wrapped.equals(this.binaryMessageHandler) || listener.equals(this.binaryMessageHandler)) {
            this.binaryMessageHandler = null;
            removed = true;
        }
        if (wrapped.equals(this.pongMessageHandler) || listener.equals(this.pongMessageHandler)) {
            this.pongMessageHandler = null;
            removed = true;
        }
        if (!removed) {
            throw new IllegalStateException(sm.getString("wsSession.removeHandlerFailed", listener));
        }
    }

    @Override // jakarta.websocket.Session
    public String getProtocolVersion() {
        checkState();
        return Constants.WS_VERSION_HEADER_VALUE;
    }

    @Override // jakarta.websocket.Session
    public String getNegotiatedSubprotocol() {
        checkState();
        return this.subProtocol;
    }

    @Override // jakarta.websocket.Session
    public List<Extension> getNegotiatedExtensions() {
        checkState();
        return this.negotiatedExtensions;
    }

    @Override // jakarta.websocket.Session
    public boolean isSecure() {
        checkState();
        return this.secure;
    }

    @Override // jakarta.websocket.Session
    public boolean isOpen() {
        return this.state.get() == State.OPEN || this.state.get() == State.OUTPUT_CLOSING || this.state.get() == State.CLOSING;
    }

    public boolean isClosed() {
        return this.state.get() == State.CLOSED;
    }

    @Override // jakarta.websocket.Session
    public long getMaxIdleTimeout() {
        checkState();
        return this.maxIdleTimeout;
    }

    @Override // jakarta.websocket.Session
    public void setMaxIdleTimeout(long timeout) {
        checkState();
        this.maxIdleTimeout = timeout;
    }

    @Override // jakarta.websocket.Session
    public void setMaxBinaryMessageBufferSize(int max) {
        checkState();
        this.maxBinaryMessageBufferSize = max;
    }

    @Override // jakarta.websocket.Session
    public int getMaxBinaryMessageBufferSize() {
        checkState();
        return this.maxBinaryMessageBufferSize;
    }

    @Override // jakarta.websocket.Session
    public void setMaxTextMessageBufferSize(int max) {
        checkState();
        this.maxTextMessageBufferSize = max;
    }

    @Override // jakarta.websocket.Session
    public int getMaxTextMessageBufferSize() {
        checkState();
        return this.maxTextMessageBufferSize;
    }

    @Override // jakarta.websocket.Session
    public Set<Session> getOpenSessions() {
        checkState();
        return this.webSocketContainer.getOpenSessions(getSessionMapKey());
    }

    @Override // jakarta.websocket.Session
    public RemoteEndpoint.Async getAsyncRemote() {
        checkState();
        return this.remoteEndpointAsync;
    }

    @Override // jakarta.websocket.Session
    public RemoteEndpoint.Basic getBasicRemote() {
        checkState();
        return this.remoteEndpointBasic;
    }

    @Override // jakarta.websocket.Session, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, ""));
    }

    @Override // jakarta.websocket.Session
    public void close(CloseReason closeReason) throws IOException {
        doClose(closeReason, closeReason);
    }

    public void doClose(CloseReason closeReasonMessage, CloseReason closeReasonLocal) {
        doClose(closeReasonMessage, closeReasonLocal, false);
    }

    public void doClose(CloseReason closeReasonMessage, CloseReason closeReasonLocal, boolean closeSocket) {
        if (!this.state.compareAndSet(State.OPEN, State.OUTPUT_CLOSING)) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("wsSession.doClose", this.id));
        }
        try {
            this.wsRemoteEndpoint.setBatchingAllowed(false);
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            this.log.warn(sm.getString("wsSession.flushFailOnClose"), t);
            fireEndpointOnError(t);
        }
        sendCloseMessage(closeReasonMessage);
        fireEndpointOnClose(closeReasonLocal);
        if (!this.state.compareAndSet(State.OUTPUT_CLOSING, State.OUTPUT_CLOSED) || closeSocket) {
            this.state.set(State.CLOSED);
            this.wsRemoteEndpoint.close();
        }
        IOException ioe = new IOException(sm.getString("wsSession.messageFailed"));
        SendResult sr = new SendResult(ioe);
        for (FutureToSendHandler f2sh : this.futures.keySet()) {
            f2sh.onResult(sr);
        }
    }

    public void onClose(CloseReason closeReason) {
        if (this.state.compareAndSet(State.OPEN, State.CLOSING)) {
            try {
                this.wsRemoteEndpoint.setBatchingAllowed(false);
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                this.log.warn(sm.getString("wsSession.flushFailOnClose"), t);
                fireEndpointOnError(t);
            }
            sendCloseMessage(closeReason);
            fireEndpointOnClose(closeReason);
            this.state.set(State.CLOSED);
            this.wsRemoteEndpoint.close();
            return;
        }
        if (!this.state.compareAndSet(State.OUTPUT_CLOSING, State.CLOSING) && this.state.compareAndSet(State.OUTPUT_CLOSED, State.CLOSED)) {
            this.wsRemoteEndpoint.close();
        }
    }

    private void fireEndpointOnClose(CloseReason closeReason) {
        Throwable throwable = null;
        InstanceManager instanceManager = getInstanceManager();
        Thread t = Thread.currentThread();
        ClassLoader cl = t.getContextClassLoader();
        t.setContextClassLoader(this.applicationClassLoader);
        try {
            try {
                this.localEndpoint.onClose(this, closeReason);
                if (instanceManager != null) {
                    try {
                        instanceManager.destroyInstance(this.localEndpoint);
                    } catch (Throwable t2) {
                        ExceptionUtils.handleThrowable(t2);
                        if (0 == 0) {
                            throwable = t2;
                        }
                    }
                }
                t.setContextClassLoader(cl);
            } catch (Throwable t1) {
                ExceptionUtils.handleThrowable(t1);
                throwable = t1;
                if (instanceManager != null) {
                    try {
                        instanceManager.destroyInstance(this.localEndpoint);
                    } catch (Throwable t22) {
                        ExceptionUtils.handleThrowable(t22);
                        if (throwable == null) {
                            throwable = t22;
                        }
                    }
                }
                t.setContextClassLoader(cl);
            }
            if (throwable != null) {
                fireEndpointOnError(throwable);
            }
        } catch (Throwable th) {
            if (instanceManager != null) {
                try {
                    instanceManager.destroyInstance(this.localEndpoint);
                } catch (Throwable t23) {
                    ExceptionUtils.handleThrowable(t23);
                    if (throwable == null) {
                    }
                }
            }
            t.setContextClassLoader(cl);
            throw th;
        }
    }

    private void fireEndpointOnError(Throwable throwable) {
        Thread t = Thread.currentThread();
        ClassLoader cl = t.getContextClassLoader();
        t.setContextClassLoader(this.applicationClassLoader);
        try {
            this.localEndpoint.onError(this, throwable);
            t.setContextClassLoader(cl);
        } catch (Throwable th) {
            t.setContextClassLoader(cl);
            throw th;
        }
    }

    private void sendCloseMessage(CloseReason closeReason) {
        ByteBuffer msg = ByteBuffer.allocate(125);
        CloseReason.CloseCode closeCode = closeReason.getCloseCode();
        if (closeCode == CloseReason.CloseCodes.CLOSED_ABNORMALLY) {
            msg.putShort((short) CloseReason.CloseCodes.PROTOCOL_ERROR.getCode());
        } else {
            msg.putShort((short) closeCode.getCode());
        }
        String reason = closeReason.getReasonPhrase();
        if (reason != null && reason.length() > 0) {
            appendCloseReasonWithTruncation(msg, reason);
        }
        msg.flip();
        try {
            try {
                this.wsRemoteEndpoint.sendMessageBlock((byte) 8, msg, true);
                this.webSocketContainer.unregisterSession(getSessionMapKey(), this);
            } catch (IOException | IllegalStateException e) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug(sm.getString("wsSession.sendCloseFail", this.id), e);
                }
                this.wsRemoteEndpoint.close();
                if (closeCode != CloseReason.CloseCodes.CLOSED_ABNORMALLY) {
                    this.localEndpoint.onError(this, e);
                }
                this.webSocketContainer.unregisterSession(getSessionMapKey(), this);
            }
        } catch (Throwable th) {
            this.webSocketContainer.unregisterSession(getSessionMapKey(), this);
            throw th;
        }
    }

    private Object getSessionMapKey() {
        if (this.endpointConfig instanceof ServerEndpointConfig) {
            return ((ServerEndpointConfig) this.endpointConfig).getPath();
        }
        return this.localEndpoint;
    }

    protected static void appendCloseReasonWithTruncation(ByteBuffer msg, String reason) {
        byte[] reasonBytes = reason.getBytes(StandardCharsets.UTF_8);
        if (reasonBytes.length <= 123) {
            msg.put(reasonBytes);
            return;
        }
        int remaining = 123 - ELLIPSIS_BYTES_LEN;
        int pos = 0;
        byte[] bytes = reason.substring(0, 0 + 1).getBytes(StandardCharsets.UTF_8);
        while (true) {
            byte[] bytesNext = bytes;
            if (remaining >= bytesNext.length) {
                msg.put(bytesNext);
                remaining -= bytesNext.length;
                pos++;
                bytes = reason.substring(pos, pos + 1).getBytes(StandardCharsets.UTF_8);
            } else {
                msg.put(ELLIPSIS_BYTES);
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void registerFuture(FutureToSendHandler f2sh) {
        this.futures.put(f2sh, f2sh);
        if (isOpen() || f2sh.isDone()) {
            return;
        }
        IOException ioe = new IOException(sm.getString("wsSession.messageFailed"));
        SendResult sr = new SendResult(ioe);
        f2sh.onResult(sr);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void unregisterFuture(FutureToSendHandler f2sh) {
        this.futures.remove(f2sh);
    }

    @Override // jakarta.websocket.Session
    public URI getRequestURI() {
        checkState();
        return this.requestUri;
    }

    @Override // jakarta.websocket.Session
    public Map<String, List<String>> getRequestParameterMap() {
        checkState();
        return this.requestParameterMap;
    }

    @Override // jakarta.websocket.Session
    public String getQueryString() {
        checkState();
        return this.queryString;
    }

    @Override // jakarta.websocket.Session
    public Principal getUserPrincipal() {
        checkState();
        return this.userPrincipal;
    }

    @Override // jakarta.websocket.Session
    public Map<String, String> getPathParameters() {
        checkState();
        return this.pathParameters;
    }

    @Override // jakarta.websocket.Session
    public String getId() {
        return this.id;
    }

    @Override // jakarta.websocket.Session
    public Map<String, Object> getUserProperties() {
        checkState();
        return this.userProperties;
    }

    public Endpoint getLocal() {
        return this.localEndpoint;
    }

    public String getHttpSessionId() {
        return this.httpSessionId;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MessageHandler getTextMessageHandler() {
        return this.textMessageHandler;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MessageHandler getBinaryMessageHandler() {
        return this.binaryMessageHandler;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MessageHandler.Whole<PongMessage> getPongMessageHandler() {
        return this.pongMessageHandler;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateLastActiveRead() {
        this.lastActiveRead = System.currentTimeMillis();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateLastActiveWrite() {
        this.lastActiveWrite = System.currentTimeMillis();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void checkExpiration() {
        long timeout = this.maxIdleTimeout;
        long timeoutRead = getMaxIdleTimeoutRead();
        long timeoutWrite = getMaxIdleTimeoutWrite();
        long currentTime = System.currentTimeMillis();
        String key = null;
        if (timeoutRead > 0 && currentTime - this.lastActiveRead > timeoutRead) {
            key = "wsSession.timeoutRead";
        } else if (timeoutWrite > 0 && currentTime - this.lastActiveWrite > timeoutWrite) {
            key = "wsSession.timeoutWrite";
        } else if (timeout > 0 && currentTime - this.lastActiveRead > timeout && currentTime - this.lastActiveWrite > timeout) {
            key = "wsSession.timeout";
        }
        if (key != null) {
            String msg = sm.getString(key, getId());
            if (this.log.isDebugEnabled()) {
                this.log.debug(msg);
            }
            doClose(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, msg), new CloseReason(CloseReason.CloseCodes.CLOSED_ABNORMALLY, msg));
        }
    }

    private long getMaxIdleTimeoutRead() {
        Object timeout = this.userProperties.get(Constants.READ_IDLE_TIMEOUT_MS);
        if (timeout instanceof Long) {
            return ((Long) timeout).longValue();
        }
        return 0L;
    }

    private long getMaxIdleTimeoutWrite() {
        Object timeout = this.userProperties.get(Constants.WRITE_IDLE_TIMEOUT_MS);
        if (timeout instanceof Long) {
            return ((Long) timeout).longValue();
        }
        return 0L;
    }

    private void checkState() {
        if (isClosed()) {
            throw new IllegalStateException(sm.getString("wsSession.closed", this.id));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setWsFrame(WsFrameBase wsFrame) {
        this.wsFrame = wsFrame;
    }

    public void suspend() {
        this.wsFrame.suspend();
    }

    public void resume() {
        this.wsFrame.resume();
    }
}
