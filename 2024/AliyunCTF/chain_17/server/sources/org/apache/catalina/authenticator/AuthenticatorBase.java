package org.apache.catalina.authenticator;

import jakarta.security.auth.message.AuthException;
import jakarta.security.auth.message.AuthStatus;
import jakarta.security.auth.message.MessageInfo;
import jakarta.security.auth.message.config.AuthConfigFactory;
import jakarta.security.auth.message.config.AuthConfigProvider;
import jakarta.security.auth.message.config.RegistrationListener;
import jakarta.security.auth.message.config.ServerAuthConfig;
import jakarta.security.auth.message.config.ServerAuthContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import org.apache.catalina.Authenticator;
import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Realm;
import org.apache.catalina.Session;
import org.apache.catalina.TomcatPrincipal;
import org.apache.catalina.Valve;
import org.apache.catalina.authenticator.jaspic.MessageInfoImpl;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.filters.CorsFilter;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.util.SessionIdGeneratorBase;
import org.apache.catalina.util.StandardSessionIdGenerator;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.http.RequestUtil;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.WebContentGenerator;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/AuthenticatorBase.class */
public abstract class AuthenticatorBase extends ValveBase implements Authenticator, RegistrationListener {
    private final Log log;
    private static final String DATE_ONE = FastHttpDateFormat.formatDate(1);
    protected static final StringManager sm = StringManager.getManager((Class<?>) AuthenticatorBase.class);
    protected static final String AUTH_HEADER_NAME = "WWW-Authenticate";
    protected static final String REALM_NAME = "Authentication required";
    protected boolean alwaysUseSession;
    protected boolean cache;
    protected boolean changeSessionIdOnAuthentication;
    protected Context context;
    protected boolean disableProxyCaching;
    protected boolean securePagesWithPragma;
    protected String secureRandomClass;
    protected String secureRandomAlgorithm;
    protected String secureRandomProvider;
    protected String jaspicCallbackHandlerClass;
    protected boolean sendAuthInfoResponseHeaders;
    protected SessionIdGeneratorBase sessionIdGenerator;
    protected SingleSignOn sso;
    private AllowCorsPreflight allowCorsPreflight;
    private volatile String jaspicAppContextID;
    private volatile Optional<AuthConfigProvider> jaspicProvider;
    private volatile CallbackHandler jaspicCallbackHandler;

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/AuthenticatorBase$AllowCorsPreflight.class */
    public enum AllowCorsPreflight {
        NEVER,
        FILTER,
        ALWAYS
    }

    protected abstract boolean doAuthenticate(Request request, HttpServletResponse httpServletResponse) throws IOException;

    protected abstract String getAuthMethod();

    /* JADX INFO: Access modifiers changed from: protected */
    public static String getRealmName(Context context) {
        if (context == null) {
            return REALM_NAME;
        }
        LoginConfig config = context.getLoginConfig();
        if (config == null) {
            return REALM_NAME;
        }
        String result = config.getRealmName();
        if (result == null) {
            return REALM_NAME;
        }
        return result;
    }

    public AuthenticatorBase() {
        super(true);
        this.log = LogFactory.getLog((Class<?>) AuthenticatorBase.class);
        this.alwaysUseSession = false;
        this.cache = true;
        this.changeSessionIdOnAuthentication = true;
        this.context = null;
        this.disableProxyCaching = true;
        this.securePagesWithPragma = false;
        this.secureRandomClass = null;
        this.secureRandomAlgorithm = SessionIdGeneratorBase.DEFAULT_SECURE_RANDOM_ALGORITHM;
        this.secureRandomProvider = null;
        this.jaspicCallbackHandlerClass = "org.apache.catalina.authenticator.jaspic.CallbackHandlerImpl";
        this.sendAuthInfoResponseHeaders = false;
        this.sessionIdGenerator = null;
        this.sso = null;
        this.allowCorsPreflight = AllowCorsPreflight.NEVER;
        this.jaspicAppContextID = null;
        this.jaspicProvider = null;
        this.jaspicCallbackHandler = null;
    }

    public String getAllowCorsPreflight() {
        return this.allowCorsPreflight.name().toLowerCase(Locale.ENGLISH);
    }

    public void setAllowCorsPreflight(String allowCorsPreflight) {
        this.allowCorsPreflight = AllowCorsPreflight.valueOf(allowCorsPreflight.trim().toUpperCase(Locale.ENGLISH));
    }

    public boolean getAlwaysUseSession() {
        return this.alwaysUseSession;
    }

    public void setAlwaysUseSession(boolean alwaysUseSession) {
        this.alwaysUseSession = alwaysUseSession;
    }

    public boolean getCache() {
        return this.cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.Contained
    public Container getContainer() {
        return this.context;
    }

    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.Contained
    public void setContainer(Container container) {
        if (container != null && !(container instanceof Context)) {
            throw new IllegalArgumentException(sm.getString("authenticator.notContext"));
        }
        super.setContainer(container);
        this.context = (Context) container;
    }

    public boolean getDisableProxyCaching() {
        return this.disableProxyCaching;
    }

    public void setDisableProxyCaching(boolean nocache) {
        this.disableProxyCaching = nocache;
    }

    public boolean getSecurePagesWithPragma() {
        return this.securePagesWithPragma;
    }

    public void setSecurePagesWithPragma(boolean securePagesWithPragma) {
        this.securePagesWithPragma = securePagesWithPragma;
    }

    public boolean getChangeSessionIdOnAuthentication() {
        return this.changeSessionIdOnAuthentication;
    }

    public void setChangeSessionIdOnAuthentication(boolean changeSessionIdOnAuthentication) {
        this.changeSessionIdOnAuthentication = changeSessionIdOnAuthentication;
    }

    public String getSecureRandomClass() {
        return this.secureRandomClass;
    }

    public void setSecureRandomClass(String secureRandomClass) {
        this.secureRandomClass = secureRandomClass;
    }

    public String getSecureRandomAlgorithm() {
        return this.secureRandomAlgorithm;
    }

    public void setSecureRandomAlgorithm(String secureRandomAlgorithm) {
        this.secureRandomAlgorithm = secureRandomAlgorithm;
    }

    public String getSecureRandomProvider() {
        return this.secureRandomProvider;
    }

    public void setSecureRandomProvider(String secureRandomProvider) {
        this.secureRandomProvider = secureRandomProvider;
    }

    public String getJaspicCallbackHandlerClass() {
        return this.jaspicCallbackHandlerClass;
    }

    public void setJaspicCallbackHandlerClass(String jaspicCallbackHandlerClass) {
        this.jaspicCallbackHandlerClass = jaspicCallbackHandlerClass;
    }

    public boolean isSendAuthInfoResponseHeaders() {
        return this.sendAuthInfoResponseHeaders;
    }

    public void setSendAuthInfoResponseHeaders(boolean sendAuthInfoResponseHeaders) {
        this.sendAuthInfoResponseHeaders = sendAuthInfoResponseHeaders;
    }

    @Override // org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws IOException, ServletException {
        String[] roles;
        Session session;
        Principal principal;
        if (this.log.isDebugEnabled()) {
            this.log.debug("Security checking request " + request.getMethod() + " " + request.getRequestURI());
        }
        if (this.cache && request.getUserPrincipal() == null && (session = request.getSessionInternal(false)) != null && (principal = session.getPrincipal()) != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("We have cached auth type " + session.getAuthType() + " for principal " + principal);
            }
            request.setAuthType(session.getAuthType());
            request.setUserPrincipal(principal);
        }
        boolean authRequired = isContinuationRequired(request);
        Realm realm = this.context.getRealm();
        SecurityConstraint[] constraints = realm.findSecurityConstraints(request, this.context);
        AuthConfigProvider jaspicProvider = getJaspicProvider();
        if (jaspicProvider != null) {
            authRequired = true;
        }
        if (constraints == null && !this.context.getPreemptiveAuthentication() && !authRequired) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Not subject to any constraint");
            }
            getNext().invoke(request, response);
            return;
        }
        if (constraints != null && this.disableProxyCaching && !WebContentGenerator.METHOD_POST.equalsIgnoreCase(request.getMethod())) {
            if (this.securePagesWithPragma) {
                response.setHeader(HttpHeaders.PRAGMA, "No-cache");
                response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
                response.setHeader(HttpHeaders.EXPIRES, DATE_ONE);
            } else {
                response.setHeader(HttpHeaders.CACHE_CONTROL, "private");
            }
        }
        if (constraints != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Calling hasUserDataPermission()");
            }
            if (!realm.hasUserDataPermission(request, response, constraints)) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Failed hasUserDataPermission() test");
                    return;
                }
                return;
            }
        }
        boolean hasAuthConstraint = false;
        if (constraints != null) {
            hasAuthConstraint = true;
            for (int i = 0; i < constraints.length && hasAuthConstraint; i++) {
                if (!constraints[i].getAuthConstraint()) {
                    hasAuthConstraint = false;
                } else if (!constraints[i].getAllRoles() && !constraints[i].getAuthenticatedUsers() && ((roles = constraints[i].findAuthRoles()) == null || roles.length == 0)) {
                    hasAuthConstraint = false;
                }
            }
        }
        if (!authRequired && hasAuthConstraint) {
            authRequired = true;
        }
        if (!authRequired && this.context.getPreemptiveAuthentication() && isPreemptiveAuthPossible(request)) {
            authRequired = true;
        }
        JaspicState jaspicState = null;
        if ((authRequired || constraints != null) && allowCorsPreflightBypass(request)) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("CORS Preflight request bypassing authentication");
            }
            getNext().invoke(request, response);
            return;
        }
        if (authRequired) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Calling authenticate()");
            }
            if (jaspicProvider != null) {
                jaspicState = getJaspicState(jaspicProvider, request, response, hasAuthConstraint);
                if (jaspicState == null) {
                    return;
                }
            }
            if ((jaspicProvider == null && !doAuthenticate(request, response)) || (jaspicProvider != null && !authenticateJaspic(request, response, jaspicState, false))) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Failed authenticate() test");
                    return;
                }
                return;
            }
        }
        if (constraints != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Calling accessControl()");
            }
            if (!realm.hasResourcePermission(request, response, constraints, this.context)) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Failed accessControl() test");
                    return;
                }
                return;
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("Successfully passed all security constraints");
        }
        getNext().invoke(request, response);
        if (jaspicProvider != null) {
            secureResponseJspic(request, response, jaspicState);
        }
    }

    protected boolean allowCorsPreflightBypass(Request request) {
        String originHeader;
        String accessControlRequestMethodHeader;
        boolean allowBypass = false;
        if (this.allowCorsPreflight != AllowCorsPreflight.NEVER && "OPTIONS".equals(request.getMethod()) && (originHeader = request.getHeader("Origin")) != null && !originHeader.isEmpty() && RequestUtil.isValidOrigin(originHeader) && !RequestUtil.isSameOrigin(request, originHeader) && (accessControlRequestMethodHeader = request.getHeader("Access-Control-Request-Method")) != null && !accessControlRequestMethodHeader.isEmpty()) {
            if (this.allowCorsPreflight == AllowCorsPreflight.ALWAYS) {
                allowBypass = true;
            } else if (this.allowCorsPreflight == AllowCorsPreflight.FILTER && DispatcherType.REQUEST == request.getDispatcherType()) {
                FilterDef[] findFilterDefs = request.getContext().findFilterDefs();
                int length = findFilterDefs.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    FilterDef filterDef = findFilterDefs[i];
                    if (!CorsFilter.class.getName().equals(filterDef.getFilterClass())) {
                        i++;
                    } else {
                        FilterMap[] findFilterMaps = this.context.findFilterMaps();
                        int length2 = findFilterMaps.length;
                        int i2 = 0;
                        while (true) {
                            if (i2 >= length2) {
                                break;
                            }
                            FilterMap filterMap = findFilterMaps[i2];
                            if (!filterMap.getFilterName().equals(filterDef.getFilterName())) {
                                i2++;
                            } else if ((filterMap.getDispatcherMapping() & 8) > 0) {
                                String[] uRLPatterns = filterMap.getURLPatterns();
                                int length3 = uRLPatterns.length;
                                int i3 = 0;
                                while (true) {
                                    if (i3 >= length3) {
                                        break;
                                    }
                                    String urlPattern = uRLPatterns[i3];
                                    if (!"/*".equals(urlPattern)) {
                                        i3++;
                                    } else {
                                        allowBypass = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return allowBypass;
    }

    @Override // org.apache.catalina.Authenticator
    public boolean authenticate(Request request, HttpServletResponse httpResponse) throws IOException {
        AuthConfigProvider jaspicProvider = getJaspicProvider();
        if (jaspicProvider == null) {
            return doAuthenticate(request, httpResponse);
        }
        Response response = request.getResponse();
        JaspicState jaspicState = getJaspicState(jaspicProvider, request, response, true);
        if (jaspicState == null) {
            return false;
        }
        boolean result = authenticateJaspic(request, response, jaspicState, true);
        secureResponseJspic(request, response, jaspicState);
        return result;
    }

    private void secureResponseJspic(Request request, Response response, JaspicState state) {
        try {
            state.serverAuthContext.secureResponse(state.messageInfo, null);
            request.setRequest((HttpServletRequest) state.messageInfo.getRequestMessage());
            response.setResponse((HttpServletResponse) state.messageInfo.getResponseMessage());
        } catch (AuthException e) {
            this.log.warn(sm.getString("authenticator.jaspicSecureResponseFail"), e);
        }
    }

    private JaspicState getJaspicState(AuthConfigProvider jaspicProvider, Request request, Response response, boolean authMandatory) throws IOException {
        JaspicState jaspicState = new JaspicState();
        jaspicState.messageInfo = new MessageInfoImpl(request.getRequest(), response.getResponse(), authMandatory);
        try {
            CallbackHandler callbackHandler = getCallbackHandler();
            ServerAuthConfig serverAuthConfig = jaspicProvider.getServerAuthConfig("HttpServlet", this.jaspicAppContextID, callbackHandler);
            String authContextID = serverAuthConfig.getAuthContextID(jaspicState.messageInfo);
            jaspicState.serverAuthContext = serverAuthConfig.getAuthContext(authContextID, null, null);
            return jaspicState;
        } catch (AuthException e) {
            this.log.warn(sm.getString("authenticator.jaspicServerAuthContextFail"), e);
            response.sendError(500);
            return null;
        }
    }

    private CallbackHandler getCallbackHandler() {
        CallbackHandler handler = this.jaspicCallbackHandler;
        if (handler == null) {
            handler = createCallbackHandler();
        }
        return handler;
    }

    private CallbackHandler createCallbackHandler() {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(this.jaspicCallbackHandlerClass, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
        }
        if (clazz == null) {
            try {
                clazz = Class.forName(this.jaspicCallbackHandlerClass);
            } catch (ReflectiveOperationException e2) {
                throw new SecurityException(e2);
            }
        }
        CallbackHandler callbackHandler = (CallbackHandler) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        if (callbackHandler instanceof Contained) {
            ((Contained) callbackHandler).setContainer(getContainer());
        }
        this.jaspicCallbackHandler = callbackHandler;
        return callbackHandler;
    }

    protected boolean isContinuationRequired(Request request) {
        return false;
    }

    protected void associate(String ssoId, Session session) {
        if (this.sso == null) {
            return;
        }
        this.sso.associate(ssoId, session);
    }

    private boolean authenticateJaspic(Request request, Response response, JaspicState state, boolean requirePrincipal) {
        boolean cachedAuth = checkForCachedAuthentication(request, response, false);
        Subject client = new Subject();
        try {
            AuthStatus authStatus = state.serverAuthContext.validateRequest(state.messageInfo, client, null);
            request.setRequest((HttpServletRequest) state.messageInfo.getRequestMessage());
            response.setResponse((HttpServletResponse) state.messageInfo.getResponseMessage());
            if (authStatus == AuthStatus.SUCCESS) {
                GenericPrincipal principal = getPrincipal(client);
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Authenticated user: " + principal);
                }
                if (principal == null) {
                    request.setUserPrincipal(null);
                    request.setAuthType(null);
                    if (requirePrincipal) {
                        return false;
                    }
                } else if (!cachedAuth || !principal.getUserPrincipal().equals(request.getUserPrincipal())) {
                    Boolean register = null;
                    String authType = "JASPIC";
                    Map map = state.messageInfo.getMap();
                    String registerValue = (String) map.get("jakarta.servlet.http.registerSession");
                    if (registerValue != null) {
                        register = Boolean.valueOf(registerValue);
                    }
                    String authTypeValue = (String) map.get("jakarta.servlet.http.authType");
                    if (authTypeValue != null) {
                        authType = authTypeValue;
                    }
                    if (register != null) {
                        register(request, response, principal, authType, null, null, this.alwaysUseSession || register.booleanValue(), register.booleanValue());
                    } else {
                        register(request, response, principal, authType, null, null);
                    }
                }
                request.setNote(Constants.REQ_JASPIC_SUBJECT_NOTE, client);
                return true;
            }
            return false;
        } catch (AuthException e) {
            this.log.debug(sm.getString("authenticator.loginFail"), e);
            return false;
        }
    }

    private GenericPrincipal getPrincipal(Subject subject) {
        if (subject == null) {
            return null;
        }
        Set<GenericPrincipal> principals = subject.getPrivateCredentials(GenericPrincipal.class);
        if (principals.isEmpty()) {
            return null;
        }
        return principals.iterator().next();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean checkForCachedAuthentication(Request request, HttpServletResponse response, boolean useSSO) {
        String username;
        Principal principal = request.getUserPrincipal();
        String ssoId = (String) request.getNote(Constants.REQ_SSOID_NOTE);
        if (principal != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(sm.getString("authenticator.check.found", principal.getName()));
            }
            if (ssoId != null) {
                associate(ssoId, request.getSessionInternal(true));
                return true;
            }
            return true;
        }
        if (useSSO && ssoId != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(sm.getString("authenticator.check.sso", ssoId));
            }
            if (reauthenticateFromSSO(ssoId, request)) {
                return true;
            }
        }
        if (request.getCoyoteRequest().getRemoteUserNeedsAuthorization() && (username = request.getCoyoteRequest().getRemoteUser().toString()) != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(sm.getString("authenticator.check.authorize", username));
            }
            Principal authorized = this.context.getRealm().authenticate(username);
            if (authorized == null) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug(sm.getString("authenticator.check.authorizeFail", username));
                }
                authorized = new GenericPrincipal(username);
            }
            String authType = request.getAuthType();
            if (authType == null || authType.length() == 0) {
                authType = getAuthMethod();
            }
            register(request, response, authorized, authType, username, null);
            return true;
        }
        return false;
    }

    protected boolean reauthenticateFromSSO(String ssoId, Request request) {
        Realm realm;
        if (this.sso == null || ssoId == null) {
            return false;
        }
        boolean reauthenticated = false;
        Container parent = getContainer();
        if (parent != null && (realm = parent.getRealm()) != null) {
            reauthenticated = this.sso.reauthenticate(ssoId, realm, request);
        }
        if (reauthenticated) {
            associate(ssoId, request.getSessionInternal(true));
            if (this.log.isDebugEnabled()) {
                this.log.debug("Reauthenticated cached principal '" + request.getUserPrincipal().getName() + "' with auth type '" + request.getAuthType() + "'");
            }
        }
        return reauthenticated;
    }

    public void register(Request request, HttpServletResponse response, Principal principal, String authType, String username, String password) {
        register(request, response, principal, authType, username, password, this.alwaysUseSession, this.cache);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void register(Request request, HttpServletResponse response, Principal principal, String authType, String username, String password, boolean alwaysUseSession, boolean cache) {
        if (this.log.isDebugEnabled()) {
            String name = principal == null ? "none" : principal.getName();
            this.log.debug("Authenticated '" + name + "' with type '" + authType + "'");
        }
        request.setAuthType(authType);
        request.setUserPrincipal(principal);
        if (this.sendAuthInfoResponseHeaders && Boolean.TRUE.equals(request.getAttribute(Globals.REQUEST_FORWARDED_ATTRIBUTE))) {
            response.setHeader("remote-user", request.getRemoteUser());
            response.setHeader("auth-type", request.getAuthType());
        }
        Session session = request.getSessionInternal(false);
        if (session != null) {
            if (getChangeSessionIdOnAuthentication() && principal != null) {
                String newSessionId = changeSessionID(request, session);
                if (session.getNote(Constants.SESSION_ID_NOTE) != null) {
                    session.setNote(Constants.SESSION_ID_NOTE, newSessionId);
                }
            }
        } else if (alwaysUseSession) {
            session = request.getSessionInternal(true);
        }
        if (session != null && cache) {
            session.setAuthType(authType);
            session.setPrincipal(principal);
        }
        if (this.sso == null) {
            return;
        }
        String ssoId = (String) request.getNote(Constants.REQ_SSOID_NOTE);
        if (ssoId == null) {
            ssoId = this.sessionIdGenerator.generateSessionId();
            Cookie cookie = new Cookie(this.sso.getCookieName(), ssoId);
            cookie.setMaxAge(-1);
            cookie.setPath("/");
            cookie.setSecure(request.isSecure());
            String ssoDomain = this.sso.getCookieDomain();
            if (ssoDomain != null) {
                cookie.setDomain(ssoDomain);
            }
            if (request.getServletContext().getSessionCookieConfig().isHttpOnly() || request.getContext().getUseHttpOnly()) {
                cookie.setHttpOnly(true);
            }
            cookie.setAttribute("Partitioned", Boolean.toString(request.getContext().getUsePartitioned()));
            response.addCookie(cookie);
            this.sso.register(ssoId, principal, authType, username, password);
            request.setNote(Constants.REQ_SSOID_NOTE, ssoId);
        } else {
            if (principal == null) {
                this.sso.deregister(ssoId);
                request.removeNote(Constants.REQ_SSOID_NOTE);
                return;
            }
            this.sso.update(ssoId, principal, authType, username, password);
        }
        if (session == null) {
            session = request.getSessionInternal(true);
        }
        this.sso.associate(ssoId, session);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String changeSessionID(Request request, Session session) {
        String oldId = null;
        if (this.log.isDebugEnabled()) {
            oldId = session.getId();
        }
        String newId = request.changeSessionId();
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("authenticator.changeSessionId", oldId, newId));
        }
        return newId;
    }

    @Override // org.apache.catalina.Authenticator
    public void login(String username, String password, Request request) throws ServletException {
        Principal principal = doLogin(request, username, password);
        register(request, request.getResponse(), principal, getAuthMethod(), username, password);
    }

    protected Principal doLogin(Request request, String username, String password) throws ServletException {
        Principal p = this.context.getRealm().authenticate(username, password);
        if (p == null) {
            throw new ServletException(sm.getString("authenticator.loginFail"));
        }
        return p;
    }

    @Override // org.apache.catalina.Authenticator
    public void logout(Request request) {
        AuthConfigProvider provider = getJaspicProvider();
        if (provider != null) {
            MessageInfo messageInfo = new MessageInfoImpl(request, request.getResponse(), true);
            Subject client = (Subject) request.getNote(Constants.REQ_JASPIC_SUBJECT_NOTE);
            if (client != null) {
                try {
                    ServerAuthConfig serverAuthConfig = provider.getServerAuthConfig("HttpServlet", this.jaspicAppContextID, getCallbackHandler());
                    String authContextID = serverAuthConfig.getAuthContextID(messageInfo);
                    ServerAuthContext serverAuthContext = serverAuthConfig.getAuthContext(authContextID, null, null);
                    serverAuthContext.cleanSubject(messageInfo, client);
                } catch (AuthException e) {
                    this.log.debug(sm.getString("authenticator.jaspicCleanSubjectFail"), e);
                }
            }
        }
        Principal p = request.getPrincipal();
        if (p instanceof TomcatPrincipal) {
            try {
                ((TomcatPrincipal) p).logout();
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                this.log.debug(sm.getString("authenticator.tomcatPrincipalLogoutFail"), t);
            }
        }
        register(request, request.getResponse(), null, null, null, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        ServletContext servletContext = this.context.getServletContext();
        this.jaspicAppContextID = servletContext.getVirtualServerName() + " " + servletContext.getContextPath();
        Container parent = this.context.getParent();
        while (this.sso == null && parent != null) {
            Valve[] valves = parent.getPipeline().getValves();
            int length = valves.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                Valve valve = valves[i];
                if (!(valve instanceof SingleSignOn)) {
                    i++;
                } else {
                    this.sso = (SingleSignOn) valve;
                    break;
                }
            }
            if (this.sso == null) {
                parent = parent.getParent();
            }
        }
        if (this.log.isDebugEnabled()) {
            if (this.sso != null) {
                this.log.debug("Found SingleSignOn Valve at " + this.sso);
            } else {
                this.log.debug("No SingleSignOn Valve is present");
            }
        }
        this.sessionIdGenerator = new StandardSessionIdGenerator();
        this.sessionIdGenerator.setSecureRandomAlgorithm(getSecureRandomAlgorithm());
        this.sessionIdGenerator.setSecureRandomClass(getSecureRandomClass());
        this.sessionIdGenerator.setSecureRandomProvider(getSecureRandomProvider());
        super.startInternal();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleBase
    public synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        this.sso = null;
    }

    protected boolean isPreemptiveAuthPossible(Request request) {
        return false;
    }

    private AuthConfigProvider getJaspicProvider() {
        Optional<AuthConfigProvider> provider = this.jaspicProvider;
        if (provider == null) {
            provider = findJaspicProvider();
        }
        return provider.orElse(null);
    }

    private Optional<AuthConfigProvider> findJaspicProvider() {
        Optional<AuthConfigProvider> provider;
        AuthConfigFactory factory = AuthConfigFactory.getFactory();
        if (factory == null) {
            provider = Optional.empty();
        } else {
            provider = Optional.ofNullable(factory.getConfigProvider("HttpServlet", this.jaspicAppContextID, this));
        }
        this.jaspicProvider = provider;
        return provider;
    }

    @Override // jakarta.security.auth.message.config.RegistrationListener
    public void notify(String layer, String appContext) {
        findJaspicProvider();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/AuthenticatorBase$JaspicState.class */
    public static class JaspicState {
        public MessageInfo messageInfo = null;
        public ServerAuthContext serverAuthContext = null;

        private JaspicState() {
        }
    }
}
