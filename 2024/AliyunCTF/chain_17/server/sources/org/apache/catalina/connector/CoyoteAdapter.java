package org.apache.catalina.connector;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.WriteListener;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.catalina.Authenticator;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.Wrapper;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.core.AsyncContextImpl;
import org.apache.catalina.util.ServerInfo;
import org.apache.catalina.util.SessionConfig;
import org.apache.catalina.util.URLEncoder;
import org.apache.coyote.ActionCode;
import org.apache.coyote.Adapter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.CharChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.ServerCookie;
import org.apache.tomcat.util.http.ServerCookies;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.http.HttpHeaders;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/CoyoteAdapter.class */
public class CoyoteAdapter implements Adapter {
    public static final int ADAPTER_NOTES = 1;
    private final Connector connector;
    private static final Log log = LogFactory.getLog((Class<?>) CoyoteAdapter.class);
    private static final String POWERED_BY = "Servlet/6.0 JSP/3.1 (" + ServerInfo.getServerInfo() + " Java/" + System.getProperty("java.vm.vendor") + "/" + System.getProperty("java.runtime.version") + ")";
    private static final EnumSet<SessionTrackingMode> SSL_ONLY = EnumSet.of(SessionTrackingMode.SSL);
    protected static final StringManager sm = StringManager.getManager((Class<?>) CoyoteAdapter.class);

    public CoyoteAdapter(Connector connector) {
        this.connector = connector;
    }

    /* JADX WARN: Finally extract failed */
    @Override // org.apache.coyote.Adapter
    public boolean asyncDispatch(org.apache.coyote.Request req, org.apache.coyote.Response res, SocketEvent status) throws Exception {
        Context context;
        Request request = (Request) req.getNote(1);
        Response response = (Response) res.getNote(1);
        if (request == null) {
            throw new IllegalStateException(sm.getString("coyoteAdapter.nullRequest"));
        }
        boolean success = true;
        AsyncContextImpl asyncConImpl = request.getAsyncContextInternal();
        req.setRequestThread();
        try {
            try {
                if (!request.isAsync()) {
                    response.setSuspended(false);
                }
                if (status == SocketEvent.TIMEOUT) {
                    if (!asyncConImpl.timeout()) {
                        asyncConImpl.setErrorState(null, false);
                    }
                } else if (status == SocketEvent.ERROR) {
                    success = false;
                    Throwable t = (Throwable) req.getAttribute("jakarta.servlet.error.exception");
                    context = request.getContext();
                    ClassLoader oldCL = null;
                    try {
                        oldCL = context.bind(false, null);
                        if (req.getReadListener() != null) {
                            req.getReadListener().onError(t);
                        }
                        if (res.getWriteListener() != null) {
                            res.getWriteListener().onError(t);
                        }
                        res.action(ActionCode.CLOSE_NOW, t);
                        asyncConImpl.setErrorState(t, true);
                        context.unbind(false, oldCL);
                    } finally {
                    }
                }
                if (!request.isAsyncDispatching() && request.isAsync()) {
                    WriteListener writeListener = res.getWriteListener();
                    ReadListener readListener = req.getReadListener();
                    if (writeListener != null && status == SocketEvent.OPEN_WRITE) {
                        Context context2 = request.getContext();
                        ClassLoader oldCL2 = null;
                        try {
                            try {
                                oldCL2 = context2.bind(false, null);
                                res.onWritePossible();
                                if (request.isFinished() && req.sendAllDataReadEvent() && readListener != null) {
                                    readListener.onAllDataRead();
                                }
                            } catch (Throwable t2) {
                                ExceptionUtils.handleThrowable(t2);
                                writeListener.onError(t2);
                                res.action(ActionCode.CLOSE_NOW, t2);
                                asyncConImpl.setErrorState(t2, true);
                                context2.unbind(false, oldCL2);
                            }
                            if (response.getCoyoteResponse().isExceptionPresent()) {
                                throw response.getCoyoteResponse().getErrorException();
                            }
                            context2.unbind(false, oldCL2);
                        } catch (Throwable th) {
                            context2.unbind(false, null);
                            throw th;
                        }
                    } else if (readListener != null && status == SocketEvent.OPEN_READ) {
                        context = request.getContext();
                        ClassLoader oldCL3 = null;
                        try {
                            try {
                                oldCL3 = context.bind(false, null);
                                if (!request.isFinished()) {
                                    req.onDataAvailable();
                                }
                                if (request.isFinished() && req.sendAllDataReadEvent()) {
                                    readListener.onAllDataRead();
                                }
                                if (request.getCoyoteRequest().isExceptionPresent()) {
                                    throw request.getCoyoteRequest().getErrorException();
                                }
                                context.unbind(false, oldCL3);
                            } finally {
                            }
                        } catch (Throwable t3) {
                            ExceptionUtils.handleThrowable(t3);
                            readListener.onError(t3);
                            res.action(ActionCode.CLOSE_NOW, t3);
                            asyncConImpl.setErrorState(t3, true);
                            context.unbind(false, oldCL3);
                        }
                    }
                }
                if (!request.isAsyncDispatching() && request.isAsync() && response.isErrorReportRequired()) {
                    this.connector.getService().getContainer().getPipeline().getFirst().invoke(request, response);
                }
                if (request.isAsyncDispatching()) {
                    this.connector.getService().getContainer().getPipeline().getFirst().invoke(request, response);
                    if (response.isError()) {
                        asyncConImpl.setErrorState((Throwable) request.getAttribute("jakarta.servlet.error.exception"), true);
                    }
                }
                if (!request.isAsync()) {
                    request.finishRequest();
                    response.finishResponse();
                }
                AtomicBoolean error = new AtomicBoolean(false);
                res.action(ActionCode.IS_ERROR, error);
                if (error.get()) {
                    if (request.isAsyncCompleting() || request.isAsyncDispatching()) {
                        res.action(ActionCode.ASYNC_POST_PROCESS, null);
                    }
                    success = false;
                }
                if (!success) {
                    res.setStatus(500);
                }
                if (!success || !request.isAsync()) {
                    long time = 0;
                    if (req.getStartTimeNanos() != -1) {
                        time = System.nanoTime() - req.getStartTimeNanos();
                    }
                    Context context3 = request.getContext();
                    if (context3 != null) {
                        context3.logAccess(request, response, time, false);
                    } else {
                        log(req, res, time);
                    }
                }
                req.getRequestProcessor().setWorkerThreadName(null);
                req.clearRequestThread();
                if (!success || !request.isAsync()) {
                    updateWrapperErrorCount(request, response);
                    request.recycle();
                    response.recycle();
                }
            } catch (IOException e) {
                success = false;
                if (0 == 0) {
                    res.setStatus(500);
                }
                if (0 == 0 || !request.isAsync()) {
                    long time2 = 0;
                    if (req.getStartTimeNanos() != -1) {
                        time2 = System.nanoTime() - req.getStartTimeNanos();
                    }
                    Context context4 = request.getContext();
                    if (context4 != null) {
                        context4.logAccess(request, response, time2, false);
                    } else {
                        log(req, res, time2);
                    }
                }
                req.getRequestProcessor().setWorkerThreadName(null);
                req.clearRequestThread();
                if (0 == 0 || !request.isAsync()) {
                    updateWrapperErrorCount(request, response);
                    request.recycle();
                    response.recycle();
                }
            } catch (Throwable t4) {
                ExceptionUtils.handleThrowable(t4);
                success = false;
                log.error(sm.getString("coyoteAdapter.asyncDispatch"), t4);
                if (0 == 0) {
                    res.setStatus(500);
                }
                if (0 == 0 || !request.isAsync()) {
                    long time3 = 0;
                    if (req.getStartTimeNanos() != -1) {
                        time3 = System.nanoTime() - req.getStartTimeNanos();
                    }
                    Context context5 = request.getContext();
                    if (context5 != null) {
                        context5.logAccess(request, response, time3, false);
                    } else {
                        log(req, res, time3);
                    }
                }
                req.getRequestProcessor().setWorkerThreadName(null);
                req.clearRequestThread();
                if (0 == 0 || !request.isAsync()) {
                    updateWrapperErrorCount(request, response);
                    request.recycle();
                    response.recycle();
                }
            }
            return success;
        } catch (Throwable th2) {
            if (1 == 0) {
                res.setStatus(500);
            }
            if (1 == 0 || !request.isAsync()) {
                long time4 = 0;
                if (req.getStartTimeNanos() != -1) {
                    time4 = System.nanoTime() - req.getStartTimeNanos();
                }
                Context context6 = request.getContext();
                if (context6 != null) {
                    context6.logAccess(request, response, time4, false);
                } else {
                    log(req, res, time4);
                }
            }
            req.getRequestProcessor().setWorkerThreadName(null);
            req.clearRequestThread();
            if (1 == 0 || !request.isAsync()) {
                updateWrapperErrorCount(request, response);
                request.recycle();
                response.recycle();
            }
            throw th2;
        }
    }

    /* JADX WARN: Finally extract failed */
    @Override // org.apache.coyote.Adapter
    public void service(org.apache.coyote.Request req, org.apache.coyote.Response res) throws Exception {
        Request request = (Request) req.getNote(1);
        Response response = (Response) res.getNote(1);
        if (request == null) {
            request = this.connector.createRequest();
            request.setCoyoteRequest(req);
            response = this.connector.createResponse();
            response.setCoyoteResponse(res);
            request.setResponse(response);
            response.setRequest(request);
            req.setNote(1, request);
            res.setNote(1, response);
            req.getParameters().setQueryStringCharset(this.connector.getURICharset());
        }
        if (this.connector.getXpoweredBy()) {
            response.addHeader("X-Powered-By", POWERED_BY);
        }
        boolean async = false;
        req.setRequestThread();
        try {
            try {
                boolean postParseSuccess = postParseRequest(req, request, res, response);
                if (postParseSuccess) {
                    request.setAsyncSupported(this.connector.getService().getContainer().getPipeline().isAsyncSupported());
                    this.connector.getService().getContainer().getPipeline().getFirst().invoke(request, response);
                }
                if (request.isAsync()) {
                    async = true;
                    ReadListener readListener = req.getReadListener();
                    if (readListener != null && request.isFinished()) {
                        ClassLoader oldCL = null;
                        try {
                            oldCL = request.getContext().bind(false, null);
                            if (req.sendAllDataReadEvent()) {
                                req.getReadListener().onAllDataRead();
                            }
                            request.getContext().unbind(false, oldCL);
                        } catch (Throwable th) {
                            request.getContext().unbind(false, oldCL);
                            throw th;
                        }
                    }
                    Throwable throwable = (Throwable) request.getAttribute("jakarta.servlet.error.exception");
                    if (!request.isAsyncCompleting() && throwable != null) {
                        request.getAsyncContextInternal().setErrorState(throwable, true);
                    }
                } else {
                    request.finishRequest();
                    response.finishResponse();
                }
                AtomicBoolean error = new AtomicBoolean(false);
                res.action(ActionCode.IS_ERROR, error);
                if (request.isAsyncCompleting() && error.get()) {
                    res.action(ActionCode.ASYNC_POST_PROCESS, null);
                    async = false;
                }
                if (!async && postParseSuccess) {
                    Context context = request.getContext();
                    Host host = request.getHost();
                    long time = System.nanoTime() - req.getStartTimeNanos();
                    if (context != null) {
                        context.logAccess(request, response, time, false);
                    } else if (response.isError()) {
                        if (host != null) {
                            host.logAccess(request, response, time, false);
                        } else {
                            this.connector.getService().getContainer().logAccess(request, response, time, false);
                        }
                    }
                }
                req.getRequestProcessor().setWorkerThreadName(null);
                req.clearRequestThread();
                if (!async) {
                    updateWrapperErrorCount(request, response);
                    request.recycle();
                    response.recycle();
                }
            } catch (IOException e) {
                AtomicBoolean error2 = new AtomicBoolean(false);
                res.action(ActionCode.IS_ERROR, error2);
                if (request.isAsyncCompleting() && error2.get()) {
                    res.action(ActionCode.ASYNC_POST_PROCESS, null);
                    async = false;
                }
                if (!async && 0 != 0) {
                    Context context2 = request.getContext();
                    Host host2 = request.getHost();
                    long time2 = System.nanoTime() - req.getStartTimeNanos();
                    if (context2 != null) {
                        context2.logAccess(request, response, time2, false);
                    } else if (response.isError()) {
                        if (host2 != null) {
                            host2.logAccess(request, response, time2, false);
                        } else {
                            this.connector.getService().getContainer().logAccess(request, response, time2, false);
                        }
                    }
                }
                req.getRequestProcessor().setWorkerThreadName(null);
                req.clearRequestThread();
                if (!async) {
                    updateWrapperErrorCount(request, response);
                    request.recycle();
                    response.recycle();
                }
            }
        } catch (Throwable th2) {
            AtomicBoolean error3 = new AtomicBoolean(false);
            res.action(ActionCode.IS_ERROR, error3);
            if (request.isAsyncCompleting() && error3.get()) {
                res.action(ActionCode.ASYNC_POST_PROCESS, null);
                async = false;
            }
            if (!async && 0 != 0) {
                Context context3 = request.getContext();
                Host host3 = request.getHost();
                long time3 = System.nanoTime() - req.getStartTimeNanos();
                if (context3 != null) {
                    context3.logAccess(request, response, time3, false);
                } else if (response.isError()) {
                    if (host3 != null) {
                        host3.logAccess(request, response, time3, false);
                    } else {
                        this.connector.getService().getContainer().logAccess(request, response, time3, false);
                    }
                }
            }
            req.getRequestProcessor().setWorkerThreadName(null);
            req.clearRequestThread();
            if (!async) {
                updateWrapperErrorCount(request, response);
                request.recycle();
                response.recycle();
            }
            throw th2;
        }
    }

    private void updateWrapperErrorCount(Request request, Response response) {
        Wrapper wrapper;
        if (response.isError() && (wrapper = request.getWrapper()) != null) {
            wrapper.incrementErrorCount();
        }
    }

    @Override // org.apache.coyote.Adapter
    public boolean prepare(org.apache.coyote.Request req, org.apache.coyote.Response res) throws IOException, ServletException {
        Request request = (Request) req.getNote(1);
        Response response = (Response) res.getNote(1);
        return postParseRequest(req, request, res, response);
    }

    @Override // org.apache.coyote.Adapter
    public void log(org.apache.coyote.Request req, org.apache.coyote.Response res, long time) {
        Request request = (Request) req.getNote(1);
        Response response = (Response) res.getNote(1);
        if (request == null) {
            request = this.connector.createRequest();
            request.setCoyoteRequest(req);
            response = this.connector.createResponse();
            response.setCoyoteResponse(res);
            request.setResponse(response);
            response.setRequest(request);
            req.setNote(1, request);
            res.setNote(1, response);
            req.getParameters().setQueryStringCharset(this.connector.getURICharset());
        }
        try {
            try {
                boolean logged = false;
                Context context = request.mappingData.context;
                Host host = request.mappingData.host;
                if (context != null) {
                    logged = true;
                    context.logAccess(request, response, time, true);
                } else if (host != null) {
                    logged = true;
                    host.logAccess(request, response, time, true);
                }
                if (!logged) {
                    this.connector.getService().getContainer().logAccess(request, response, time, true);
                }
                updateWrapperErrorCount(request, response);
                request.recycle();
                response.recycle();
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                log.warn(sm.getString("coyoteAdapter.accesslogFail"), t);
                updateWrapperErrorCount(request, response);
                request.recycle();
                response.recycle();
            }
        } catch (Throwable th) {
            updateWrapperErrorCount(request, response);
            request.recycle();
            response.recycle();
            throw th;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/CoyoteAdapter$RecycleRequiredException.class */
    private static class RecycleRequiredException extends Exception {
        private static final long serialVersionUID = 1;

        private RecycleRequiredException() {
        }
    }

    @Override // org.apache.coyote.Adapter
    public void checkRecycled(org.apache.coyote.Request req, org.apache.coyote.Response res) {
        Request request = (Request) req.getNote(1);
        Response response = (Response) res.getNote(1);
        String messageKey = null;
        if (request != null && request.getHost() != null) {
            messageKey = "coyoteAdapter.checkRecycled.request";
        } else if (response != null && response.getContentWritten() != 0) {
            messageKey = "coyoteAdapter.checkRecycled.response";
        }
        if (messageKey != null) {
            log(req, res, 0L);
            if (this.connector.getState().isAvailable()) {
                if (log.isInfoEnabled()) {
                    log.info(sm.getString(messageKey), new RecycleRequiredException());
                }
            } else if (log.isDebugEnabled()) {
                log.debug(sm.getString(messageKey), new RecycleRequiredException());
            }
        }
    }

    @Override // org.apache.coyote.Adapter
    public String getDomain() {
        return this.connector.getDomain();
    }

    protected boolean postParseRequest(org.apache.coyote.Request req, Request request, org.apache.coyote.Response res, Response response) throws IOException, ServletException {
        MessageBytes serverName;
        String[] methods;
        String sessionID;
        if (req.scheme().isNull()) {
            req.scheme().setString(this.connector.getScheme());
            request.setSecure(this.connector.getSecure());
        } else {
            request.setSecure(req.scheme().equals("https"));
        }
        String proxyName = this.connector.getProxyName();
        int proxyPort = this.connector.getProxyPort();
        if (proxyPort != 0) {
            req.setServerPort(proxyPort);
        } else if (req.getServerPort() == -1) {
            if (req.scheme().equals("https")) {
                req.setServerPort(443);
            } else {
                req.setServerPort(80);
            }
        }
        if (proxyName != null) {
            req.serverName().setString(proxyName);
        }
        MessageBytes undecodedURI = req.requestURI();
        if (undecodedURI.equals("*")) {
            if (req.method().equals("OPTIONS")) {
                StringBuilder allow = new StringBuilder();
                allow.append("GET, HEAD, POST, PUT, DELETE, OPTIONS");
                if (this.connector.getAllowTrace()) {
                    allow.append(", TRACE");
                }
                res.setHeader(HttpHeaders.ALLOW, allow.toString());
                this.connector.getService().getContainer().logAccess(request, response, 0L, true);
                return false;
            }
            response.sendError(400, sm.getString("coyoteAdapter.invalidURI"));
        }
        MessageBytes decodedURI = req.decodedURI();
        if (req.method().equals("CONNECT")) {
            response.sendError(501, sm.getString("coyoteAdapter.connect"));
        } else if (undecodedURI.getType() == 2) {
            if (this.connector.getRejectSuspiciousURIs() && checkSuspiciousURIs(undecodedURI.getByteChunk())) {
                response.sendError(400, sm.getString("coyoteAdapter.invalidURI"));
            }
            decodedURI.duplicate(undecodedURI);
            parsePathParameters(req, request);
            try {
                req.getURLDecoder().convert(decodedURI.getByteChunk(), this.connector.getEncodedSolidusHandlingInternal());
            } catch (IOException ioe) {
                response.sendError(400, sm.getString("coyoteAdapter.invalidURIWithMessage", ioe.getMessage()));
            }
            if (normalize(req.decodedURI(), this.connector.getAllowBackslash())) {
                convertURI(decodedURI, request);
            } else {
                response.sendError(400, sm.getString("coyoteAdapter.invalidURI"));
            }
        } else {
            decodedURI.toChars();
            CharChunk uriCC = decodedURI.getCharChunk();
            int semicolon = uriCC.indexOf(';');
            if (semicolon > 0) {
                decodedURI.setChars(uriCC.getBuffer(), uriCC.getStart(), semicolon);
            }
        }
        if (this.connector.getUseIPVHosts()) {
            serverName = req.localName();
            if (serverName.isNull()) {
                res.action(ActionCode.REQ_LOCAL_NAME_ATTRIBUTE, null);
            }
        } else {
            serverName = req.serverName();
        }
        String version = null;
        Context versionContext = null;
        boolean mapRequired = true;
        if (response.isError()) {
            decodedURI.recycle();
        }
        while (mapRequired) {
            this.connector.getService().getMapper().map(serverName, decodedURI, version, request.getMappingData());
            if (request.getContext() == null) {
                return true;
            }
            if (request.getServletContext().getEffectiveSessionTrackingModes().contains(SessionTrackingMode.URL) && (sessionID = request.getPathParameter(SessionConfig.getSessionUriParamName(request.getContext()))) != null) {
                request.setRequestedSessionId(sessionID);
                request.setRequestedSessionURL(true);
            }
            try {
                parseSessionCookiesId(request);
                parseSessionSslId(request);
                String sessionID2 = request.getRequestedSessionId();
                mapRequired = false;
                if (version == null || request.getContext() != versionContext) {
                    version = null;
                    versionContext = null;
                    Context[] contexts = request.getMappingData().contexts;
                    if (contexts != null && sessionID2 != null) {
                        int i = contexts.length;
                        while (true) {
                            if (i <= 0) {
                                break;
                            }
                            Context ctxt = contexts[i - 1];
                            if (ctxt.getManager().findSession(sessionID2) == null) {
                                i--;
                            } else if (!ctxt.equals(request.getMappingData().context)) {
                                version = ctxt.getWebappVersion();
                                versionContext = ctxt;
                                request.getMappingData().recycle();
                                mapRequired = true;
                                request.recycleSessionInfo();
                                request.recycleCookieInfo(true);
                            }
                        }
                    }
                }
                if (!mapRequired && request.getContext().getPaused()) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                    }
                    request.getMappingData().recycle();
                    mapRequired = true;
                }
            } catch (IllegalArgumentException e2) {
                if (!response.isError()) {
                    response.setError();
                    response.sendError(400, e2.getMessage());
                    return true;
                }
                return true;
            }
        }
        MessageBytes redirectPathMB = request.getMappingData().redirectPath;
        if (!redirectPathMB.isNull()) {
            String redirectPath = URLEncoder.DEFAULT.encode(redirectPathMB.toString(), StandardCharsets.UTF_8);
            String query = request.getQueryString();
            if (request.isRequestedSessionIdFromURL()) {
                redirectPath = redirectPath + ";" + SessionConfig.getSessionUriParamName(request.getContext()) + "=" + request.getRequestedSessionId();
            }
            if (query != null) {
                redirectPath = redirectPath + "?" + query;
            }
            response.sendRedirect(redirectPath);
            request.getContext().logAccess(request, response, 0L, true);
            return false;
        }
        if (!this.connector.getAllowTrace() && req.method().equals("TRACE")) {
            Wrapper wrapper = request.getWrapper();
            String header = null;
            if (wrapper != null && (methods = wrapper.getServletMethods()) != null) {
                for (String method : methods) {
                    if (!"TRACE".equals(method)) {
                        if (header == null) {
                            header = method;
                        } else {
                            header = header + ", " + method;
                        }
                    }
                }
            }
            if (header != null) {
                res.addHeader(HttpHeaders.ALLOW, header);
            }
            response.sendError(405, sm.getString("coyoteAdapter.trace"));
            return true;
        }
        doConnectorAuthenticationAuthorization(req, request);
        return true;
    }

    private void doConnectorAuthenticationAuthorization(org.apache.coyote.Request req, Request request) {
        String username = req.getRemoteUser().toString();
        if (username != null) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("coyoteAdapter.authenticate", username));
            }
            if (req.getRemoteUserNeedsAuthorization()) {
                Authenticator authenticator = request.getContext().getAuthenticator();
                if (!(authenticator instanceof AuthenticatorBase)) {
                    if (log.isDebugEnabled()) {
                        log.debug(sm.getString("coyoteAdapter.authorize", username));
                    }
                    request.setUserPrincipal(request.getContext().getRealm().authenticate(username));
                }
            } else {
                request.setUserPrincipal(new CoyotePrincipal(username));
            }
        }
        String authType = req.getAuthType().toString();
        if (authType != null) {
            request.setAuthType(authType);
        }
    }

    protected void parsePathParameters(org.apache.coyote.Request req, Request request) {
        int equals;
        req.decodedURI().toBytes();
        ByteChunk uriBC = req.decodedURI().getByteChunk();
        int semicolon = uriBC.indexOf(';', 1);
        if (semicolon == -1) {
            return;
        }
        Charset charset = this.connector.getURICharset();
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("coyoteAdapter.debug", "uriBC", uriBC.toString()));
            log.debug(sm.getString("coyoteAdapter.debug", "semicolon", String.valueOf(semicolon)));
            log.debug(sm.getString("coyoteAdapter.debug", "enc", charset.name()));
        }
        while (semicolon > -1) {
            int start = uriBC.getStart();
            int end = uriBC.getEnd();
            int pathParamStart = semicolon + 1;
            int pathParamEnd = ByteChunk.findBytes(uriBC.getBuffer(), start + pathParamStart, end, new byte[]{59, 47});
            String pv = null;
            if (pathParamEnd >= 0) {
                if (charset != null) {
                    pv = new String(uriBC.getBuffer(), start + pathParamStart, pathParamEnd - pathParamStart, charset);
                }
                byte[] buf = uriBC.getBuffer();
                for (int i = 0; i < (end - start) - pathParamEnd; i++) {
                    buf[start + semicolon + i] = buf[start + i + pathParamEnd];
                }
                uriBC.setBytes(buf, start, ((end - start) - pathParamEnd) + semicolon);
            } else {
                if (charset != null) {
                    pv = new String(uriBC.getBuffer(), start + pathParamStart, (end - start) - pathParamStart, charset);
                }
                uriBC.setEnd(start + semicolon);
            }
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("coyoteAdapter.debug", "pathParamStart", String.valueOf(pathParamStart)));
                log.debug(sm.getString("coyoteAdapter.debug", "pathParamEnd", String.valueOf(pathParamEnd)));
                log.debug(sm.getString("coyoteAdapter.debug", "pv", pv));
            }
            if (pv != null && (equals = pv.indexOf(61)) > -1) {
                String name = pv.substring(0, equals);
                String value = pv.substring(equals + 1);
                request.addPathParameter(name, value);
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("coyoteAdapter.debug", "equals", String.valueOf(equals)));
                    log.debug(sm.getString("coyoteAdapter.debug", "name", name));
                    log.debug(sm.getString("coyoteAdapter.debug", "value", value));
                }
            }
            semicolon = uriBC.indexOf(';', semicolon);
        }
    }

    protected void parseSessionSslId(Request request) {
        String sessionId;
        if (request.getRequestedSessionId() == null && SSL_ONLY.equals(request.getServletContext().getEffectiveSessionTrackingModes()) && request.connector.secure && (sessionId = (String) request.getAttribute("jakarta.servlet.request.ssl_session_id")) != null) {
            request.setRequestedSessionId(sessionId);
            request.setRequestedSessionSSL(true);
        }
    }

    protected void parseSessionCookiesId(Request request) {
        ServerCookies serverCookies;
        int count;
        Context context = request.getMappingData().context;
        if ((context != null && !context.getServletContext().getEffectiveSessionTrackingModes().contains(SessionTrackingMode.COOKIE)) || (count = (serverCookies = request.getServerCookies()).getCookieCount()) <= 0) {
            return;
        }
        String sessionCookieName = SessionConfig.getSessionCookieName(context);
        for (int i = 0; i < count; i++) {
            ServerCookie scookie = serverCookies.getCookie(i);
            if (scookie.getName().equals(sessionCookieName)) {
                if (!request.isRequestedSessionIdFromCookie()) {
                    convertMB(scookie.getValue());
                    request.setRequestedSessionId(scookie.getValue().toString());
                    request.setRequestedSessionCookie(true);
                    request.setRequestedSessionURL(false);
                    if (log.isDebugEnabled()) {
                        log.debug(" Requested cookie session id is " + request.getRequestedSessionId());
                    }
                } else if (!request.isRequestedSessionIdValid()) {
                    convertMB(scookie.getValue());
                    request.setRequestedSessionId(scookie.getValue().toString());
                }
            }
        }
    }

    protected void convertURI(MessageBytes uri, Request request) throws IOException {
        ByteChunk bc = uri.getByteChunk();
        int length = bc.getLength();
        CharChunk cc = uri.getCharChunk();
        cc.allocate(length, -1);
        Charset charset = this.connector.getURICharset();
        B2CConverter conv = request.getURIConverter();
        if (conv == null) {
            conv = new B2CConverter(charset, false);
            request.setURIConverter(conv);
        } else {
            conv.recycle();
        }
        try {
            conv.convert(bc, cc, true);
            uri.setChars(cc.getBuffer(), cc.getStart(), cc.getLength());
        } catch (IOException e) {
            request.getResponse().sendError(400);
        }
    }

    protected void convertMB(MessageBytes mb) {
        if (mb.getType() != 2) {
            return;
        }
        ByteChunk bc = mb.getByteChunk();
        CharChunk cc = mb.getCharChunk();
        int length = bc.getLength();
        cc.allocate(length, -1);
        byte[] bbuf = bc.getBuffer();
        char[] cbuf = cc.getBuffer();
        int start = bc.getStart();
        for (int i = 0; i < length; i++) {
            cbuf[i] = (char) (bbuf[i + start] & 255);
        }
        mb.setChars(cbuf, 0, length);
    }

    public static boolean normalize(MessageBytes uriMB, boolean allowBackslash) {
        ByteChunk uriBC = uriMB.getByteChunk();
        byte[] b = uriBC.getBytes();
        int start = uriBC.getStart();
        int end = uriBC.getEnd();
        boolean appendedSlash = false;
        if (start == end) {
            return false;
        }
        if (b[start] != 47 && b[start] != 92) {
            return false;
        }
        for (int pos = start; pos < end; pos++) {
            if (b[pos] == 92) {
                if (allowBackslash) {
                    b[pos] = 47;
                } else {
                    return false;
                }
            } else if (b[pos] == 0) {
                return false;
            }
        }
        for (int pos2 = start; pos2 < end - 1; pos2++) {
            if (b[pos2] == 47) {
                while (pos2 + 1 < end && b[pos2 + 1] == 47) {
                    copyBytes(b, pos2, pos2 + 1, (end - pos2) - 1);
                    end--;
                }
            }
        }
        if (end - start >= 2 && b[end - 1] == 46 && (b[end - 2] == 47 || (b[end - 2] == 46 && b[end - 3] == 47))) {
            b[end] = 47;
            end++;
            appendedSlash = true;
        }
        uriBC.setEnd(end);
        int index = 0;
        while (true) {
            index = uriBC.indexOf("/./", 0, 3, index);
            if (index < 0) {
                break;
            }
            copyBytes(b, start + index, start + index + 2, ((end - start) - index) - 2);
            end -= 2;
            uriBC.setEnd(end);
        }
        int i = 0;
        while (true) {
            int index2 = i;
            int index3 = uriBC.indexOf("/../", 0, 4, index2);
            if (index3 >= 0) {
                if (index3 == 0) {
                    return false;
                }
                int index22 = -1;
                for (int pos3 = (start + index3) - 1; pos3 >= 0 && index22 < 0; pos3--) {
                    if (b[pos3] == 47) {
                        index22 = pos3;
                    }
                }
                copyBytes(b, start + index22, start + index3 + 3, ((end - start) - index3) - 3);
                end = ((end + index22) - index3) - 3;
                uriBC.setEnd(end);
                i = index22;
            } else {
                if (appendedSlash && end > 1 && b[end - 1] == 47) {
                    uriBC.setEnd(end - 1);
                    return true;
                }
                return true;
            }
        }
    }

    protected static void copyBytes(byte[] b, int dest, int src, int len) {
        System.arraycopy(b, src, b, dest, len);
    }

    /* JADX WARN: Removed duplicated region for block: B:44:0x00e6  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x0145 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:78:0x013f A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static boolean checkSuspiciousURIs(org.apache.tomcat.util.buf.ByteChunk r5) {
        /*
            Method dump skipped, instructions count: 352
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.catalina.connector.CoyoteAdapter.checkSuspiciousURIs(org.apache.tomcat.util.buf.ByteChunk):boolean");
    }
}
