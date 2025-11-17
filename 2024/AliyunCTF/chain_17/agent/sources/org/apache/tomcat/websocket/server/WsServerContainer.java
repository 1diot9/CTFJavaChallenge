package org.apache.tomcat.websocket.server;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.CloseReason;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Encoder;
import jakarta.websocket.server.ServerContainer;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.server.ServerEndpointConfig;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import javax.naming.NamingException;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.WsSession;
import org.apache.tomcat.websocket.WsWebSocketContainer;
import org.apache.tomcat.websocket.pojo.PojoMethodMapping;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/server/WsServerContainer.class */
public class WsServerContainer extends WsWebSocketContainer implements ServerContainer {
    private static final StringManager sm = StringManager.getManager((Class<?>) WsServerContainer.class);
    private static final CloseReason AUTHENTICATED_HTTP_SESSION_CLOSED = new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "This connection was established under an authenticated HTTP session that has ended.");
    private final ServletContext servletContext;
    private final WsWriteTimeout wsWriteTimeout = new WsWriteTimeout();
    private final Map<String, ExactPathMatch> configExactMatchMap = new ConcurrentHashMap();
    private final Map<Integer, ConcurrentSkipListMap<String, TemplatePathMatch>> configTemplateMatchMap = new ConcurrentHashMap();
    private final Map<String, Set<WsSession>> authenticatedSessions = new ConcurrentHashMap();
    private volatile boolean endpointsRegistered = false;
    private volatile boolean deploymentFailed = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WsServerContainer(ServletContext servletContext) {
        this.servletContext = servletContext;
        setInstanceManager((InstanceManager) servletContext.getAttribute(InstanceManager.class.getName()));
        String value = servletContext.getInitParameter(Constants.BINARY_BUFFER_SIZE_SERVLET_CONTEXT_INIT_PARAM);
        if (value != null) {
            setDefaultMaxBinaryMessageBufferSize(Integer.parseInt(value));
        }
        String value2 = servletContext.getInitParameter(Constants.TEXT_BUFFER_SIZE_SERVLET_CONTEXT_INIT_PARAM);
        if (value2 != null) {
            setDefaultMaxTextMessageBufferSize(Integer.parseInt(value2));
        }
        FilterRegistration.Dynamic fr = servletContext.addFilter("Tomcat WebSocket (JSR356) Filter", new WsFilter());
        if (fr != null) {
            fr.setAsyncSupported(true);
            EnumSet<DispatcherType> types = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
            fr.addMappingForUrlPatterns(types, true, "/*");
        }
    }

    @Override // jakarta.websocket.server.ServerContainer
    public void addEndpoint(ServerEndpointConfig sec) throws DeploymentException {
        addEndpoint(sec, false);
    }

    void addEndpoint(ServerEndpointConfig sec, boolean fromAnnotatedPojo) throws DeploymentException {
        if (this.servletContext == null) {
            throw new DeploymentException(sm.getString("serverContainer.servletContextMissing"));
        }
        if (this.deploymentFailed) {
            throw new DeploymentException(sm.getString("serverContainer.failedDeployment", this.servletContext.getContextPath(), this.servletContext.getVirtualServerName()));
        }
        try {
            String path = sec.getPath();
            PojoMethodMapping methodMapping = new PojoMethodMapping(sec.getEndpointClass(), sec.getDecoders(), path, getInstanceManager(Thread.currentThread().getContextClassLoader()));
            if (methodMapping.getOnClose() != null || methodMapping.getOnOpen() != null || methodMapping.getOnError() != null || methodMapping.hasMessageHandlers()) {
                sec.getUserProperties().put(org.apache.tomcat.websocket.pojo.Constants.POJO_METHOD_MAPPING_KEY, methodMapping);
            }
            UriTemplate uriTemplate = new UriTemplate(path);
            if (uriTemplate.hasParameters()) {
                Integer key = Integer.valueOf(uriTemplate.getSegmentCount());
                ConcurrentSkipListMap<String, TemplatePathMatch> templateMatches = this.configTemplateMatchMap.get(key);
                if (templateMatches == null) {
                    this.configTemplateMatchMap.putIfAbsent(key, new ConcurrentSkipListMap<>());
                    templateMatches = this.configTemplateMatchMap.get(key);
                }
                TemplatePathMatch newMatch = new TemplatePathMatch(sec, uriTemplate, fromAnnotatedPojo);
                TemplatePathMatch oldMatch = templateMatches.putIfAbsent(uriTemplate.getNormalizedPath(), newMatch);
                if (oldMatch != null) {
                    if (oldMatch.isFromAnnotatedPojo() && !newMatch.isFromAnnotatedPojo() && oldMatch.getConfig().getEndpointClass() == newMatch.getConfig().getEndpointClass()) {
                        templateMatches.put(path, oldMatch);
                    } else {
                        throw new DeploymentException(sm.getString("serverContainer.duplicatePaths", path, sec.getEndpointClass(), sec.getEndpointClass()));
                    }
                }
            } else {
                ExactPathMatch newMatch2 = new ExactPathMatch(sec, fromAnnotatedPojo);
                ExactPathMatch oldMatch2 = this.configExactMatchMap.put(path, newMatch2);
                if (oldMatch2 != null) {
                    if (oldMatch2.isFromAnnotatedPojo() && !newMatch2.isFromAnnotatedPojo() && oldMatch2.getConfig().getEndpointClass() == newMatch2.getConfig().getEndpointClass()) {
                        this.configExactMatchMap.put(path, oldMatch2);
                    } else {
                        throw new DeploymentException(sm.getString("serverContainer.duplicatePaths", path, oldMatch2.getConfig().getEndpointClass(), sec.getEndpointClass()));
                    }
                }
            }
            this.endpointsRegistered = true;
        } catch (DeploymentException de) {
            failDeployment();
            throw de;
        }
    }

    @Override // jakarta.websocket.server.ServerContainer
    public void addEndpoint(Class<?> pojo) throws DeploymentException {
        addEndpoint(pojo, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addEndpoint(Class<?> pojo, boolean fromAnnotatedPojo) throws DeploymentException {
        if (this.deploymentFailed) {
            throw new DeploymentException(sm.getString("serverContainer.failedDeployment", this.servletContext.getContextPath(), this.servletContext.getVirtualServerName()));
        }
        try {
            ServerEndpoint annotation = (ServerEndpoint) pojo.getAnnotation(ServerEndpoint.class);
            if (annotation == null) {
                throw new DeploymentException(sm.getString("serverContainer.missingAnnotation", pojo.getName()));
            }
            String path = annotation.value();
            validateEncoders(annotation.encoders(), getInstanceManager(Thread.currentThread().getContextClassLoader()));
            Class<? extends ServerEndpointConfig.Configurator> configuratorClazz = annotation.configurator();
            ServerEndpointConfig.Configurator configurator = null;
            if (!configuratorClazz.equals(ServerEndpointConfig.Configurator.class)) {
                try {
                    configurator = annotation.configurator().getConstructor(new Class[0]).newInstance(new Object[0]);
                } catch (ReflectiveOperationException e) {
                    throw new DeploymentException(sm.getString("serverContainer.configuratorFail", annotation.configurator().getName(), pojo.getClass().getName()), e);
                }
            }
            ServerEndpointConfig sec = ServerEndpointConfig.Builder.create(pojo, path).decoders(Arrays.asList(annotation.decoders())).encoders(Arrays.asList(annotation.encoders())).subprotocols(Arrays.asList(annotation.subprotocols())).configurator(configurator).build();
            addEndpoint(sec, fromAnnotatedPojo);
        } catch (DeploymentException de) {
            failDeployment();
            throw de;
        }
    }

    void failDeployment() {
        this.deploymentFailed = true;
        this.endpointsRegistered = false;
        this.configExactMatchMap.clear();
        this.configTemplateMatchMap.clear();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean areEndpointsRegistered() {
        return this.endpointsRegistered;
    }

    @Override // jakarta.websocket.server.ServerContainer
    public void upgradeHttpToWebSocket(Object httpServletRequest, Object httpServletResponse, ServerEndpointConfig sec, Map<String, String> pathParameters) throws IOException, DeploymentException {
        try {
            UpgradeUtil.doUpgrade(this, (HttpServletRequest) httpServletRequest, (HttpServletResponse) httpServletResponse, sec, pathParameters);
        } catch (ServletException e) {
            throw new DeploymentException(e.getMessage(), e);
        }
    }

    public WsMappingResult findMapping(String path) {
        ExactPathMatch match = this.configExactMatchMap.get(path);
        if (match != null) {
            return new WsMappingResult(match.getConfig(), Collections.emptyMap());
        }
        try {
            UriTemplate pathUriTemplate = new UriTemplate(path);
            Integer key = Integer.valueOf(pathUriTemplate.getSegmentCount());
            ConcurrentSkipListMap<String, TemplatePathMatch> templateMatches = this.configTemplateMatchMap.get(key);
            if (templateMatches == null) {
                return null;
            }
            ServerEndpointConfig sec = null;
            Map<String, String> pathParams = null;
            Iterator<TemplatePathMatch> it = templateMatches.values().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                TemplatePathMatch templateMatch = it.next();
                pathParams = templateMatch.getUriTemplate().match(pathUriTemplate);
                if (pathParams != null) {
                    sec = templateMatch.getConfig();
                    break;
                }
            }
            if (sec == null) {
                return null;
            }
            return new WsMappingResult(sec, pathParams);
        } catch (DeploymentException e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public WsWriteTimeout getTimeout() {
        return this.wsWriteTimeout;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.websocket.WsWebSocketContainer
    public InstanceManager getInstanceManager(ClassLoader classLoader) {
        return super.getInstanceManager(classLoader);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.websocket.WsWebSocketContainer
    public void registerSession(Object key, WsSession wsSession) {
        super.registerSession(key, wsSession);
        if (wsSession.isOpen() && wsSession.getUserPrincipal() != null && wsSession.getHttpSessionId() != null) {
            registerAuthenticatedSession(wsSession, wsSession.getHttpSessionId());
        }
    }

    @Override // org.apache.tomcat.websocket.WsWebSocketContainer
    protected void unregisterSession(Object key, WsSession wsSession) {
        if (wsSession.getUserPrincipal() != null && wsSession.getHttpSessionId() != null) {
            unregisterAuthenticatedSession(wsSession, wsSession.getHttpSessionId());
        }
        super.unregisterSession(key, wsSession);
    }

    private void registerAuthenticatedSession(WsSession wsSession, String httpSessionId) {
        Set<WsSession> wsSessions = this.authenticatedSessions.get(httpSessionId);
        if (wsSessions == null) {
            this.authenticatedSessions.putIfAbsent(httpSessionId, ConcurrentHashMap.newKeySet());
            wsSessions = this.authenticatedSessions.get(httpSessionId);
        }
        wsSessions.add(wsSession);
    }

    private void unregisterAuthenticatedSession(WsSession wsSession, String httpSessionId) {
        Set<WsSession> wsSessions = this.authenticatedSessions.get(httpSessionId);
        if (wsSessions != null) {
            wsSessions.remove(wsSession);
        }
    }

    public void closeAuthenticatedSession(String httpSessionId) {
        Set<WsSession> wsSessions = this.authenticatedSessions.remove(httpSessionId);
        if (wsSessions != null && !wsSessions.isEmpty()) {
            for (WsSession wsSession : wsSessions) {
                try {
                    wsSession.close(AUTHENTICATED_HTTP_SESSION_CLOSED);
                } catch (IOException e) {
                }
            }
        }
    }

    private static void validateEncoders(Class<? extends Encoder>[] encoders, InstanceManager instanceManager) throws DeploymentException {
        for (Class<? extends Encoder> encoder : encoders) {
            if (instanceManager == null) {
                try {
                    encoder.getConstructor(new Class[0]).newInstance(new Object[0]);
                } catch (ReflectiveOperationException | NamingException e) {
                    throw new DeploymentException(sm.getString("serverContainer.encoderFail", encoder.getName()), e);
                }
            } else {
                Encoder instance = (Encoder) instanceManager.newInstance((Class<?>) encoder);
                instanceManager.destroyInstance(instance);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/server/WsServerContainer$TemplatePathMatch.class */
    public static class TemplatePathMatch {
        private final ServerEndpointConfig config;
        private final UriTemplate uriTemplate;
        private final boolean fromAnnotatedPojo;

        TemplatePathMatch(ServerEndpointConfig config, UriTemplate uriTemplate, boolean fromAnnotatedPojo) {
            this.config = config;
            this.uriTemplate = uriTemplate;
            this.fromAnnotatedPojo = fromAnnotatedPojo;
        }

        public ServerEndpointConfig getConfig() {
            return this.config;
        }

        public UriTemplate getUriTemplate() {
            return this.uriTemplate;
        }

        public boolean isFromAnnotatedPojo() {
            return this.fromAnnotatedPojo;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/server/WsServerContainer$ExactPathMatch.class */
    public static class ExactPathMatch {
        private final ServerEndpointConfig config;
        private final boolean fromAnnotatedPojo;

        ExactPathMatch(ServerEndpointConfig config, boolean fromAnnotatedPojo) {
            this.config = config;
            this.fromAnnotatedPojo = fromAnnotatedPojo;
        }

        public ServerEndpointConfig getConfig() {
            return this.config;
        }

        public boolean isFromAnnotatedPojo() {
            return this.fromAnnotatedPojo;
        }
    }
}
