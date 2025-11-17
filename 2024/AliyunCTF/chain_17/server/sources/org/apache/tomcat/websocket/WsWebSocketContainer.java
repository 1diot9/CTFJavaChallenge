package org.apache.tomcat.websocket;

import ch.qos.logback.core.net.ssl.SSL;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.CloseReason;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.Extension;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.InstanceManagerBindings;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.collections.CaseInsensitiveKeyMap;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.KeyStoreUtil;
import org.springframework.asm.Opcodes;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsWebSocketContainer.class */
public class WsWebSocketContainer implements WebSocketContainer, BackgroundProcess {
    private static final StringManager sm = StringManager.getManager((Class<?>) WsWebSocketContainer.class);
    private static final Random RANDOM = new Random();
    private static final byte[] CRLF = {13, 10};
    private static final byte[] GET_BYTES = "GET ".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] ROOT_URI_BYTES = "/".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] HTTP_VERSION_BYTES = " HTTP/1.1\r\n".getBytes(StandardCharsets.ISO_8859_1);
    private volatile AsynchronousChannelGroup asynchronousChannelGroup = null;
    private final Object asynchronousChannelGroupLock = new Object();
    private final Log log = LogFactory.getLog((Class<?>) WsWebSocketContainer.class);
    private final Map<Object, Set<WsSession>> endpointSessionMap = new HashMap();
    private final Map<WsSession, WsSession> sessions = new ConcurrentHashMap();
    private final Object endPointSessionMapLock = new Object();
    private long defaultAsyncTimeout = -1;
    private int maxBinaryMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
    private int maxTextMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
    private volatile long defaultMaxSessionIdleTimeout = 0;
    private int backgroundProcessCount = 0;
    private int processPeriod = Constants.DEFAULT_PROCESS_PERIOD;
    private InstanceManager instanceManager;

    /* JADX INFO: Access modifiers changed from: protected */
    public InstanceManager getInstanceManager(ClassLoader classLoader) {
        if (this.instanceManager != null) {
            return this.instanceManager;
        }
        return InstanceManagerBindings.get(classLoader);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setInstanceManager(InstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

    @Override // jakarta.websocket.WebSocketContainer
    public Session connectToServer(Object pojo, URI path) throws DeploymentException {
        ClientEndpointConfig config = createClientEndpointConfig(pojo.getClass());
        ClientEndpointHolder holder = new PojoHolder(pojo, config);
        return connectToServerRecursive(holder, config, path, new HashSet());
    }

    @Override // jakarta.websocket.WebSocketContainer
    public Session connectToServer(Class<?> annotatedEndpointClass, URI path) throws DeploymentException {
        ClientEndpointConfig config = createClientEndpointConfig(annotatedEndpointClass);
        ClientEndpointHolder holder = new PojoClassHolder(annotatedEndpointClass, config);
        return connectToServerRecursive(holder, config, path, new HashSet());
    }

    private ClientEndpointConfig createClientEndpointConfig(Class<?> annotatedEndpointClass) throws DeploymentException {
        ClientEndpoint annotation = (ClientEndpoint) annotatedEndpointClass.getAnnotation(ClientEndpoint.class);
        if (annotation == null) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.missingAnnotation", annotatedEndpointClass.getName()));
        }
        Class<? extends ClientEndpointConfig.Configurator> configuratorClazz = annotation.configurator();
        ClientEndpointConfig.Configurator configurator = null;
        if (!ClientEndpointConfig.Configurator.class.equals(configuratorClazz)) {
            try {
                configurator = configuratorClazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (ReflectiveOperationException e) {
                throw new DeploymentException(sm.getString("wsWebSocketContainer.defaultConfiguratorFail"), e);
            }
        }
        ClientEndpointConfig.Builder builder = ClientEndpointConfig.Builder.create();
        if (configurator != null) {
            builder.configurator(configurator);
        }
        ClientEndpointConfig config = builder.decoders(Arrays.asList(annotation.decoders())).encoders(Arrays.asList(annotation.encoders())).preferredSubprotocols(Arrays.asList(annotation.subprotocols())).build();
        return config;
    }

    @Override // jakarta.websocket.WebSocketContainer
    public Session connectToServer(Class<? extends Endpoint> clazz, ClientEndpointConfig clientEndpointConfiguration, URI path) throws DeploymentException {
        ClientEndpointHolder holder = new EndpointClassHolder(clazz);
        return connectToServerRecursive(holder, clientEndpointConfiguration, path, new HashSet());
    }

    @Override // jakarta.websocket.WebSocketContainer
    public Session connectToServer(Endpoint endpoint, ClientEndpointConfig clientEndpointConfiguration, URI path) throws DeploymentException {
        ClientEndpointHolder holder = new EndpointHolder(endpoint);
        return connectToServerRecursive(holder, clientEndpointConfiguration, path, new HashSet());
    }

    private Session connectToServerRecursive(ClientEndpointHolder clientEndpointHolder, ClientEndpointConfig clientEndpointConfiguration, URI path, Set<URI> redirectSet) throws DeploymentException {
        URI proxyPath;
        String subProtocol;
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("wsWebSocketContainer.connect.entry", clientEndpointHolder.getClassName(), path));
        }
        boolean secure = false;
        ByteBuffer proxyConnect = null;
        String scheme = path.getScheme();
        if ("ws".equalsIgnoreCase(scheme)) {
            proxyPath = URI.create("http" + path.toString().substring(2));
        } else {
            if (!"wss".equalsIgnoreCase(scheme)) {
                throw new DeploymentException(sm.getString("wsWebSocketContainer.pathWrongScheme", scheme));
            }
            proxyPath = URI.create("https" + path.toString().substring(3));
            secure = true;
        }
        String host = path.getHost();
        if (host == null) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.pathNoHost"));
        }
        int port = path.getPort();
        SocketAddress sa = null;
        List<Proxy> proxies = ProxySelector.getDefault().select(proxyPath);
        Proxy selectedProxy = null;
        Iterator<Proxy> it = proxies.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Proxy proxy = it.next();
            if (proxy.type().equals(Proxy.Type.HTTP)) {
                sa = proxy.address();
                if (sa instanceof InetSocketAddress) {
                    InetSocketAddress inet = (InetSocketAddress) sa;
                    if (inet.isUnresolved()) {
                        sa = new InetSocketAddress(inet.getHostName(), inet.getPort());
                    }
                }
                selectedProxy = proxy;
            }
        }
        if (port == -1) {
            port = "ws".equalsIgnoreCase(scheme) ? 80 : 443;
        }
        Map<String, Object> userProperties = clientEndpointConfiguration.getUserProperties();
        if (sa == null) {
            sa = new InetSocketAddress(host, port);
        } else {
            proxyConnect = createProxyRequest(host, port, (String) userProperties.get("Proxy-Authorization"));
        }
        Map<String, List<String>> reqHeaders = createRequestHeaders(host, port, secure, clientEndpointConfiguration);
        clientEndpointConfiguration.getConfigurator().beforeRequest(reqHeaders);
        if (Constants.DEFAULT_ORIGIN_HEADER_VALUE != null && !reqHeaders.containsKey("Origin")) {
            List<String> originValues = new ArrayList<>(1);
            originValues.add(Constants.DEFAULT_ORIGIN_HEADER_VALUE);
            reqHeaders.put("Origin", originValues);
        }
        ByteBuffer request = createRequest(path, reqHeaders);
        String timeoutValue = (String) userProperties.get(Constants.IO_TIMEOUT_MS_PROPERTY);
        long timeout = timeoutValue != null ? Long.valueOf(timeoutValue).intValue() : 5000L;
        try {
            AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open(getAsynchronousChannelGroup());
            ByteBuffer response = ByteBuffer.allocate(getDefaultMaxBinaryMessageBufferSize());
            List<Extension> extensionsAgreed = new ArrayList<>();
            Transformation transformation = null;
            AsyncChannelWrapper channel = null;
            try {
                try {
                    Future<Void> fConnect = socketChannel.connect(sa);
                    if (proxyConnect != null) {
                        fConnect.get(timeout, TimeUnit.MILLISECONDS);
                        channel = new AsyncChannelWrapperNonSecure(socketChannel);
                        writeRequest(channel, proxyConnect, timeout);
                        HttpResponse httpResponse = processResponse(response, channel, timeout);
                        if (httpResponse.status == 407) {
                            Session processAuthenticationChallenge = processAuthenticationChallenge(clientEndpointHolder, clientEndpointConfiguration, path, redirectSet, userProperties, request, httpResponse, AuthenticationType.PROXY);
                            if (0 == 0) {
                                if (channel != null) {
                                    channel.close();
                                } else {
                                    try {
                                        socketChannel.close();
                                    } catch (IOException e) {
                                    }
                                }
                            }
                            return processAuthenticationChallenge;
                        }
                        if (httpResponse.getStatus() != 200) {
                            throw new DeploymentException(sm.getString("wsWebSocketContainer.proxyConnectFail", selectedProxy, Integer.toString(httpResponse.getStatus())));
                        }
                    }
                    if (secure) {
                        SSLEngine sslEngine = createSSLEngine(clientEndpointConfiguration, host, port);
                        channel = new AsyncChannelWrapperSecure(socketChannel, sslEngine);
                    } else if (channel == null) {
                        channel = new AsyncChannelWrapperNonSecure(socketChannel);
                    }
                    fConnect.get(timeout, TimeUnit.MILLISECONDS);
                    Future<Void> fHandshake = channel.handshake();
                    fHandshake.get(timeout, TimeUnit.MILLISECONDS);
                    if (this.log.isDebugEnabled()) {
                        SocketAddress localAddress = null;
                        try {
                            localAddress = channel.getLocalAddress();
                        } catch (IOException e2) {
                        }
                        this.log.debug(sm.getString("wsWebSocketContainer.connect.write", Integer.valueOf(request.position()), Integer.valueOf(request.limit()), localAddress));
                    }
                    writeRequest(channel, request, timeout);
                    HttpResponse httpResponse2 = processResponse(response, channel, timeout);
                    String maxRedirectsValue = (String) userProperties.get(Constants.MAX_REDIRECTIONS_PROPERTY);
                    int maxRedirects = maxRedirectsValue != null ? Integer.parseInt(maxRedirectsValue) : 20;
                    if (httpResponse2.status != 101) {
                        if (!isRedirectStatus(httpResponse2.status)) {
                            if (httpResponse2.status != 401) {
                                throw new DeploymentException(sm.getString("wsWebSocketContainer.invalidStatus", Integer.toString(httpResponse2.status)));
                            }
                            Session processAuthenticationChallenge2 = processAuthenticationChallenge(clientEndpointHolder, clientEndpointConfiguration, path, redirectSet, userProperties, request, httpResponse2, AuthenticationType.WWW);
                            if (0 == 0) {
                                if (channel != null) {
                                    channel.close();
                                } else {
                                    try {
                                        socketChannel.close();
                                    } catch (IOException e3) {
                                    }
                                }
                            }
                            return processAuthenticationChallenge2;
                        }
                        List<String> locationHeader = httpResponse2.getHandshakeResponse().getHeaders().get("Location");
                        if (locationHeader == null || locationHeader.isEmpty() || locationHeader.get(0) == null || locationHeader.get(0).isEmpty()) {
                            throw new DeploymentException(sm.getString("wsWebSocketContainer.missingLocationHeader", Integer.toString(httpResponse2.status)));
                        }
                        URI redirectLocation = URI.create(locationHeader.get(0)).normalize();
                        if (!redirectLocation.isAbsolute()) {
                            redirectLocation = path.resolve(redirectLocation);
                        }
                        String redirectScheme = redirectLocation.getScheme().toLowerCase();
                        if (redirectScheme.startsWith("http")) {
                            redirectLocation = new URI(redirectScheme.replace("http", "ws"), redirectLocation.getUserInfo(), redirectLocation.getHost(), redirectLocation.getPort(), redirectLocation.getPath(), redirectLocation.getQuery(), redirectLocation.getFragment());
                        }
                        if (!redirectSet.add(redirectLocation) || redirectSet.size() > maxRedirects) {
                            throw new DeploymentException(sm.getString("wsWebSocketContainer.redirectThreshold", redirectLocation, Integer.toString(redirectSet.size()), Integer.toString(maxRedirects)));
                        }
                        Session connectToServerRecursive = connectToServerRecursive(clientEndpointHolder, clientEndpointConfiguration, redirectLocation, redirectSet);
                        if (0 == 0) {
                            if (channel != null) {
                                channel.close();
                            } else {
                                try {
                                    socketChannel.close();
                                } catch (IOException e4) {
                                }
                            }
                        }
                        return connectToServerRecursive;
                    }
                    HandshakeResponse handshakeResponse = httpResponse2.getHandshakeResponse();
                    clientEndpointConfiguration.getConfigurator().afterResponse(handshakeResponse);
                    List<String> protocolHeaders = handshakeResponse.getHeaders().get("Sec-WebSocket-Protocol");
                    if (protocolHeaders == null || protocolHeaders.size() == 0) {
                        subProtocol = null;
                    } else {
                        if (protocolHeaders.size() != 1) {
                            throw new DeploymentException(sm.getString("wsWebSocketContainer.invalidSubProtocol"));
                        }
                        subProtocol = protocolHeaders.get(0);
                    }
                    List<String> extHeaders = handshakeResponse.getHeaders().get("Sec-WebSocket-Extensions");
                    if (extHeaders != null) {
                        for (String extHeader : extHeaders) {
                            Util.parseExtensionHeader(extensionsAgreed, extHeader);
                        }
                    }
                    TransformationFactory factory = TransformationFactory.getInstance();
                    for (Extension extension : extensionsAgreed) {
                        List<List<Extension.Parameter>> wrapper = new ArrayList<>(1);
                        wrapper.add(extension.getParameters());
                        Transformation t = factory.create(extension.getName(), wrapper, false);
                        if (t == null) {
                            throw new DeploymentException(sm.getString("wsWebSocketContainer.invalidExtensionParameters"));
                        }
                        if (transformation == null) {
                            transformation = t;
                        } else {
                            transformation.setNext(t);
                        }
                    }
                    if (1 == 0) {
                        if (channel != null) {
                            channel.close();
                        } else {
                            try {
                                socketChannel.close();
                            } catch (IOException e5) {
                            }
                        }
                    }
                    WsRemoteEndpointImplClient wsRemoteEndpointClient = new WsRemoteEndpointImplClient(channel);
                    WsSession wsSession = new WsSession(clientEndpointHolder, wsRemoteEndpointClient, this, extensionsAgreed, subProtocol, Collections.emptyMap(), secure, clientEndpointConfiguration);
                    WsFrameClient wsFrameClient = new WsFrameClient(response, channel, wsSession, transformation);
                    wsRemoteEndpointClient.setTransformation(wsFrameClient.getTransformation());
                    wsSession.getLocal().onOpen(wsSession, clientEndpointConfiguration);
                    registerSession(wsSession.getLocal(), wsSession);
                    wsFrameClient.startInputProcessing();
                    return wsSession;
                } catch (EOFException | InterruptedException | URISyntaxException | ExecutionException | TimeoutException | SSLException | AuthenticationException e6) {
                    throw new DeploymentException(sm.getString("wsWebSocketContainer.httpRequestFailed", path), e6);
                }
            } catch (Throwable th) {
                if (0 == 0) {
                    if (0 != 0) {
                        channel.close();
                    } else {
                        try {
                            socketChannel.close();
                        } catch (IOException e7) {
                        }
                    }
                }
                throw th;
            }
        } catch (IOException ioe) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.asynchronousSocketChannelFail"), ioe);
        }
    }

    private Session processAuthenticationChallenge(ClientEndpointHolder clientEndpointHolder, ClientEndpointConfig clientEndpointConfiguration, URI path, Set<URI> redirectSet, Map<String, Object> userProperties, ByteBuffer request, HttpResponse httpResponse, AuthenticationType authenticationType) throws DeploymentException, AuthenticationException {
        if (userProperties.get(authenticationType.getAuthorizationHeaderName()) != null) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.failedAuthentication", Integer.valueOf(httpResponse.status), authenticationType.getAuthorizationHeaderName()));
        }
        List<String> authenticateHeaders = httpResponse.getHandshakeResponse().getHeaders().get(authenticationType.getAuthenticateHeaderName());
        if (authenticateHeaders == null || authenticateHeaders.isEmpty() || authenticateHeaders.get(0) == null || authenticateHeaders.get(0).isEmpty()) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.missingAuthenticateHeader", Integer.toString(httpResponse.status), authenticationType.getAuthenticateHeaderName()));
        }
        String authScheme = authenticateHeaders.get(0).split("\\s+", 2)[0];
        Authenticator auth = AuthenticatorFactory.getAuthenticator(authScheme);
        if (auth == null) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.unsupportedAuthScheme", Integer.valueOf(httpResponse.status), authScheme));
        }
        String requestUri = new String(request.array(), StandardCharsets.ISO_8859_1).split("\\s", 3)[1];
        userProperties.put(authenticationType.getAuthorizationHeaderName(), auth.getAuthorization(requestUri, authenticateHeaders.get(0), (String) userProperties.get(authenticationType.getUserNameProperty()), (String) userProperties.get(authenticationType.getUserPasswordProperty()), (String) userProperties.get(authenticationType.getUserRealmProperty())));
        return connectToServerRecursive(clientEndpointHolder, clientEndpointConfiguration, path, redirectSet);
    }

    private static void writeRequest(AsyncChannelWrapper channel, ByteBuffer request, long timeout) throws TimeoutException, InterruptedException, ExecutionException {
        int toWrite = request.limit();
        Future<Integer> fWrite = channel.write(request);
        Integer thisWrite = fWrite.get(timeout, TimeUnit.MILLISECONDS);
        int i = toWrite;
        int intValue = thisWrite.intValue();
        while (true) {
            int toWrite2 = i - intValue;
            if (toWrite2 > 0) {
                Future<Integer> fWrite2 = channel.write(request);
                Integer thisWrite2 = fWrite2.get(timeout, TimeUnit.MILLISECONDS);
                i = toWrite2;
                intValue = thisWrite2.intValue();
            } else {
                return;
            }
        }
    }

    private static boolean isRedirectStatus(int httpResponseCode) {
        boolean isRedirect = false;
        switch (httpResponseCode) {
            case 300:
            case 301:
            case 302:
            case 303:
            case 305:
            case 307:
                isRedirect = true;
                break;
        }
        return isRedirect;
    }

    private static ByteBuffer createProxyRequest(String host, int port, String authorizationHeader) {
        StringBuilder request = new StringBuilder();
        request.append("CONNECT ");
        request.append(host);
        request.append(':');
        request.append(port);
        request.append(" HTTP/1.1\r\nProxy-Connection: keep-alive\r\nConnection: keepalive\r\nHost: ");
        request.append(host);
        request.append(':');
        request.append(port);
        if (authorizationHeader != null) {
            request.append("\r\n");
            request.append("Proxy-Authorization");
            request.append(':');
            request.append(authorizationHeader);
        }
        request.append("\r\n\r\n");
        byte[] bytes = request.toString().getBytes(StandardCharsets.ISO_8859_1);
        return ByteBuffer.wrap(bytes);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void registerSession(Object key, WsSession wsSession) {
        if (!wsSession.isOpen()) {
            return;
        }
        synchronized (this.endPointSessionMapLock) {
            if (this.endpointSessionMap.size() == 0) {
                BackgroundProcessManager.getInstance().register(this);
            }
            this.endpointSessionMap.computeIfAbsent(key, k -> {
                return new HashSet();
            }).add(wsSession);
        }
        this.sessions.put(wsSession, wsSession);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void unregisterSession(Object key, WsSession wsSession) {
        synchronized (this.endPointSessionMapLock) {
            Set<WsSession> wsSessions = this.endpointSessionMap.get(key);
            if (wsSessions != null) {
                wsSessions.remove(wsSession);
                if (wsSessions.size() == 0) {
                    this.endpointSessionMap.remove(key);
                }
            }
            if (this.endpointSessionMap.size() == 0) {
                BackgroundProcessManager.getInstance().unregister(this);
            }
        }
        this.sessions.remove(wsSession);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Set<Session> getOpenSessions(Object key) {
        HashSet<Session> result = new HashSet<>();
        synchronized (this.endPointSessionMapLock) {
            Set<WsSession> sessions = this.endpointSessionMap.get(key);
            if (sessions != null) {
                result.addAll(sessions);
            }
        }
        return result;
    }

    private static Map<String, List<String>> createRequestHeaders(String host, int port, boolean secure, ClientEndpointConfig clientEndpointConfiguration) {
        Map<String, List<String>> headers = new HashMap<>();
        List<Extension> extensions = clientEndpointConfiguration.getExtensions();
        List<String> subProtocols = clientEndpointConfiguration.getPreferredSubprotocols();
        Map<String, Object> userProperties = clientEndpointConfiguration.getUserProperties();
        if (userProperties.get("Authorization") != null) {
            List<String> authValues = new ArrayList<>(1);
            authValues.add((String) userProperties.get("Authorization"));
            headers.put("Authorization", authValues);
        }
        List<String> hostValues = new ArrayList<>(1);
        if ((port == 80 && !secure) || (port == 443 && secure)) {
            hostValues.add(host);
        } else {
            hostValues.add(host + ":" + port);
        }
        headers.put("Host", hostValues);
        List<String> upgradeValues = new ArrayList<>(1);
        upgradeValues.add(Constants.UPGRADE_HEADER_VALUE);
        headers.put("Upgrade", upgradeValues);
        List<String> connectionValues = new ArrayList<>(1);
        connectionValues.add(Constants.CONNECTION_HEADER_VALUE);
        headers.put("Connection", connectionValues);
        List<String> wsVersionValues = new ArrayList<>(1);
        wsVersionValues.add(Constants.WS_VERSION_HEADER_VALUE);
        headers.put("Sec-WebSocket-Version", wsVersionValues);
        List<String> wsKeyValues = new ArrayList<>(1);
        wsKeyValues.add(generateWsKeyValue());
        headers.put("Sec-WebSocket-Key", wsKeyValues);
        if (subProtocols != null && subProtocols.size() > 0) {
            headers.put("Sec-WebSocket-Protocol", subProtocols);
        }
        if (extensions != null && extensions.size() > 0) {
            headers.put("Sec-WebSocket-Extensions", generateExtensionHeaders(extensions));
        }
        return headers;
    }

    private static List<String> generateExtensionHeaders(List<Extension> extensions) {
        List<String> result = new ArrayList<>(extensions.size());
        for (Extension extension : extensions) {
            StringBuilder header = new StringBuilder();
            header.append(extension.getName());
            for (Extension.Parameter param : extension.getParameters()) {
                header.append(';');
                header.append(param.getName());
                String value = param.getValue();
                if (value != null && value.length() > 0) {
                    header.append('=');
                    header.append(value);
                }
            }
            result.add(header.toString());
        }
        return result;
    }

    private static String generateWsKeyValue() {
        byte[] keyBytes = new byte[16];
        RANDOM.nextBytes(keyBytes);
        return Base64.encodeBase64String(keyBytes);
    }

    private static ByteBuffer createRequest(URI uri, Map<String, List<String>> reqHeaders) {
        ByteBuffer result = ByteBuffer.allocate(Opcodes.ACC_SYNTHETIC);
        result.put(GET_BYTES);
        String path = uri.getPath();
        if (null == path || path.isEmpty()) {
            result.put(ROOT_URI_BYTES);
        } else {
            result.put(uri.getRawPath().getBytes(StandardCharsets.ISO_8859_1));
        }
        String query = uri.getRawQuery();
        if (query != null) {
            result.put((byte) 63);
            result.put(query.getBytes(StandardCharsets.ISO_8859_1));
        }
        result.put(HTTP_VERSION_BYTES);
        for (Map.Entry<String, List<String>> entry : reqHeaders.entrySet()) {
            result = addHeader(result, entry.getKey(), entry.getValue());
        }
        result.put(CRLF);
        result.flip();
        return result;
    }

    private static ByteBuffer addHeader(ByteBuffer result, String key, List<String> values) {
        if (values.isEmpty()) {
            return result;
        }
        return putWithExpand(putWithExpand(putWithExpand(putWithExpand(result, key.getBytes(StandardCharsets.ISO_8859_1)), ": ".getBytes(StandardCharsets.ISO_8859_1)), StringUtils.join(values).getBytes(StandardCharsets.ISO_8859_1)), CRLF);
    }

    private static ByteBuffer putWithExpand(ByteBuffer input, byte[] bytes) {
        int newSize;
        if (bytes.length > input.remaining()) {
            if (bytes.length > input.capacity()) {
                newSize = 2 * bytes.length;
            } else {
                newSize = input.capacity() * 2;
            }
            ByteBuffer expanded = ByteBuffer.allocate(newSize);
            input.flip();
            expanded.put(input);
            input = expanded;
        }
        return input.put(bytes);
    }

    private HttpResponse processResponse(ByteBuffer response, AsyncChannelWrapper channel, long timeout) throws InterruptedException, ExecutionException, DeploymentException, EOFException, TimeoutException {
        Map<String, List<String>> headers = new CaseInsensitiveKeyMap<>();
        int status = 0;
        boolean readStatus = false;
        boolean readHeaders = false;
        String line = null;
        while (!readHeaders) {
            response.clear();
            Future<Integer> read = channel.read(response);
            try {
                Integer bytesRead = read.get(timeout, TimeUnit.MILLISECONDS);
                if (bytesRead.intValue() == -1) {
                    throw new EOFException(sm.getString("wsWebSocketContainer.responseFail", Integer.toString(status), headers));
                }
                response.flip();
                while (response.hasRemaining() && !readHeaders) {
                    if (line == null) {
                        line = readLine(response);
                    } else {
                        line = line + readLine(response);
                    }
                    if ("\r\n".equals(line)) {
                        readHeaders = true;
                    } else if (line.endsWith("\r\n")) {
                        if (readStatus) {
                            parseHeaders(line, headers);
                        } else {
                            status = parseStatus(line);
                            readStatus = true;
                        }
                        line = null;
                    }
                }
            } catch (TimeoutException e) {
                TimeoutException te = new TimeoutException(sm.getString("wsWebSocketContainer.responseFail", Integer.toString(status), headers));
                te.initCause(e);
                throw te;
            }
        }
        return new HttpResponse(status, new WsHandshakeResponse(headers));
    }

    private int parseStatus(String line) throws DeploymentException {
        String[] parts = line.trim().split(" ");
        if (parts.length < 2 || (!org.apache.coyote.http11.Constants.HTTP_10.equals(parts[0]) && !org.apache.coyote.http11.Constants.HTTP_11.equals(parts[0]))) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.invalidStatus", line));
        }
        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.invalidStatus", line));
        }
    }

    private void parseHeaders(String line, Map<String, List<String>> headers) {
        int index = line.indexOf(58);
        if (index == -1) {
            this.log.warn(sm.getString("wsWebSocketContainer.invalidHeader", line));
            return;
        }
        String headerName = line.substring(0, index).trim().toLowerCase(Locale.ENGLISH);
        String headerValue = line.substring(index + 1).trim();
        List<String> values = headers.computeIfAbsent(headerName, k -> {
            return new ArrayList(1);
        });
        values.add(headerValue);
    }

    private String readLine(ByteBuffer response) {
        StringBuilder sb = new StringBuilder();
        while (response.hasRemaining()) {
            char c = (char) response.get();
            sb.append(c);
            if (c == '\n') {
                break;
            }
        }
        return sb.toString();
    }

    private SSLEngine createSSLEngine(ClientEndpointConfig clientEndpointConfig, String host, int port) throws DeploymentException {
        Map<String, Object> userProperties = clientEndpointConfig.getUserProperties();
        try {
            SSLContext sslContext = clientEndpointConfig.getSSLContext();
            if (sslContext == null) {
                sslContext = (SSLContext) userProperties.get(Constants.SSL_CONTEXT_PROPERTY);
            }
            if (sslContext == null) {
                sslContext = SSLContext.getInstance("TLS");
                String sslTrustStoreValue = (String) userProperties.get(Constants.SSL_TRUSTSTORE_PROPERTY);
                if (sslTrustStoreValue != null) {
                    String sslTrustStorePwdValue = (String) userProperties.get(Constants.SSL_TRUSTSTORE_PWD_PROPERTY);
                    if (sslTrustStorePwdValue == null) {
                        sslTrustStorePwdValue = "changeit";
                    }
                    File keyStoreFile = new File(sslTrustStoreValue);
                    KeyStore ks = KeyStore.getInstance(SSL.DEFAULT_KEYSTORE_TYPE);
                    InputStream is = new FileInputStream(keyStoreFile);
                    try {
                        KeyStoreUtil.load(ks, is, sslTrustStorePwdValue.toCharArray());
                        is.close();
                        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                        tmf.init(ks);
                        sslContext.init(null, tmf.getTrustManagers(), null);
                    } finally {
                    }
                } else {
                    sslContext.init(null, null, null);
                }
            }
            SSLEngine engine = sslContext.createSSLEngine(host, port);
            String sslProtocolsValue = (String) userProperties.get(Constants.SSL_PROTOCOLS_PROPERTY);
            if (sslProtocolsValue != null) {
                engine.setEnabledProtocols(sslProtocolsValue.split(","));
            }
            engine.setUseClientMode(true);
            SSLParameters sslParams = engine.getSSLParameters();
            sslParams.setEndpointIdentificationAlgorithm("HTTPS");
            engine.setSSLParameters(sslParams);
            return engine;
        } catch (Exception e) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.sslEngineFail"), e);
        }
    }

    @Override // jakarta.websocket.WebSocketContainer
    public long getDefaultMaxSessionIdleTimeout() {
        return this.defaultMaxSessionIdleTimeout;
    }

    @Override // jakarta.websocket.WebSocketContainer
    public void setDefaultMaxSessionIdleTimeout(long timeout) {
        this.defaultMaxSessionIdleTimeout = timeout;
    }

    @Override // jakarta.websocket.WebSocketContainer
    public int getDefaultMaxBinaryMessageBufferSize() {
        return this.maxBinaryMessageBufferSize;
    }

    @Override // jakarta.websocket.WebSocketContainer
    public void setDefaultMaxBinaryMessageBufferSize(int max) {
        this.maxBinaryMessageBufferSize = max;
    }

    @Override // jakarta.websocket.WebSocketContainer
    public int getDefaultMaxTextMessageBufferSize() {
        return this.maxTextMessageBufferSize;
    }

    @Override // jakarta.websocket.WebSocketContainer
    public void setDefaultMaxTextMessageBufferSize(int max) {
        this.maxTextMessageBufferSize = max;
    }

    @Override // jakarta.websocket.WebSocketContainer
    public Set<Extension> getInstalledExtensions() {
        return Collections.emptySet();
    }

    @Override // jakarta.websocket.WebSocketContainer
    public long getDefaultAsyncSendTimeout() {
        return this.defaultAsyncTimeout;
    }

    @Override // jakarta.websocket.WebSocketContainer
    public void setAsyncSendTimeout(long timeout) {
        this.defaultAsyncTimeout = timeout;
    }

    public void destroy() {
        CloseReason cr = new CloseReason(CloseReason.CloseCodes.GOING_AWAY, sm.getString("wsWebSocketContainer.shutdown"));
        for (WsSession session : this.sessions.keySet()) {
            try {
                session.close(cr);
            } catch (IOException ioe) {
                this.log.debug(sm.getString("wsWebSocketContainer.sessionCloseFail", session.getId()), ioe);
            }
        }
        if (this.asynchronousChannelGroup != null) {
            synchronized (this.asynchronousChannelGroupLock) {
                if (this.asynchronousChannelGroup != null) {
                    AsyncChannelGroupUtil.unregister();
                    this.asynchronousChannelGroup = null;
                }
            }
        }
    }

    private AsynchronousChannelGroup getAsynchronousChannelGroup() {
        AsynchronousChannelGroup result = this.asynchronousChannelGroup;
        if (result == null) {
            synchronized (this.asynchronousChannelGroupLock) {
                if (this.asynchronousChannelGroup == null) {
                    this.asynchronousChannelGroup = AsyncChannelGroupUtil.register();
                }
                result = this.asynchronousChannelGroup;
            }
        }
        return result;
    }

    @Override // org.apache.tomcat.websocket.BackgroundProcess
    public void backgroundProcess() {
        this.backgroundProcessCount++;
        if (this.backgroundProcessCount >= this.processPeriod) {
            this.backgroundProcessCount = 0;
            for (WsSession wsSession : this.sessions.keySet()) {
                wsSession.checkExpiration();
            }
        }
    }

    @Override // org.apache.tomcat.websocket.BackgroundProcess
    public void setProcessPeriod(int period) {
        this.processPeriod = period;
    }

    @Override // org.apache.tomcat.websocket.BackgroundProcess
    public int getProcessPeriod() {
        return this.processPeriod;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsWebSocketContainer$HttpResponse.class */
    public static class HttpResponse {
        private final int status;
        private final HandshakeResponse handshakeResponse;

        HttpResponse(int status, HandshakeResponse handshakeResponse) {
            this.status = status;
            this.handshakeResponse = handshakeResponse;
        }

        public int getStatus() {
            return this.status;
        }

        public HandshakeResponse getHandshakeResponse() {
            return this.handshakeResponse;
        }
    }
}
