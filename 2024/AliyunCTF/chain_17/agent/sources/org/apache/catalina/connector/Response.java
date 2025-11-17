package org.apache.catalina.connector;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.catalina.Context;
import org.apache.catalina.Session;
import org.apache.catalina.security.SecurityUtil;
import org.apache.catalina.util.SessionConfig;
import org.apache.coyote.ActionCode;
import org.apache.coyote.Constants;
import org.apache.coyote.ContinueResponseTiming;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.CharChunk;
import org.apache.tomcat.util.buf.UEncoder;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.MediaTypeCache;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.Escape;
import org.springframework.http.HttpHeaders;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/Response.class */
public class Response implements HttpServletResponse {
    private static final Log log = LogFactory.getLog((Class<?>) Response.class);
    protected static final StringManager sm = StringManager.getManager((Class<?>) Response.class);
    private static final MediaTypeCache MEDIA_TYPE_CACHE = new MediaTypeCache(100);
    protected org.apache.coyote.Response coyoteResponse;
    protected final OutputBuffer outputBuffer;
    protected CoyoteOutputStream outputStream;
    protected CoyoteWriter writer;
    protected boolean appCommitted;
    protected boolean included;
    private boolean isCharacterEncodingSet;
    protected boolean usingOutputStream;
    protected boolean usingWriter;
    protected final UEncoder urlEncoder;
    protected final CharChunk redirectURLCC;
    private final List<Cookie> cookies;
    private HttpServletResponse applicationResponse;
    protected Request request;
    protected ResponseFacade facade;

    public Response() {
        this(8192);
    }

    public Response(int outputBufferSize) {
        this.appCommitted = false;
        this.included = false;
        this.isCharacterEncodingSet = false;
        this.usingOutputStream = false;
        this.usingWriter = false;
        this.urlEncoder = new UEncoder(UEncoder.SafeCharsSet.WITH_SLASH);
        this.redirectURLCC = new CharChunk();
        this.cookies = new ArrayList();
        this.applicationResponse = null;
        this.request = null;
        this.facade = null;
        this.outputBuffer = new OutputBuffer(outputBufferSize);
    }

    public void setCoyoteResponse(org.apache.coyote.Response coyoteResponse) {
        this.coyoteResponse = coyoteResponse;
        this.outputBuffer.setResponse(coyoteResponse);
    }

    public org.apache.coyote.Response getCoyoteResponse() {
        return this.coyoteResponse;
    }

    public Context getContext() {
        return this.request.getContext();
    }

    public void recycle() {
        this.cookies.clear();
        this.outputBuffer.recycle();
        this.usingOutputStream = false;
        this.usingWriter = false;
        this.appCommitted = false;
        this.included = false;
        this.isCharacterEncodingSet = false;
        this.applicationResponse = null;
        if (getRequest().getDiscardFacades()) {
            if (this.facade != null) {
                this.facade.clear();
                this.facade = null;
            }
            if (this.outputStream != null) {
                this.outputStream.clear();
                this.outputStream = null;
            }
            if (this.writer != null) {
                this.writer.clear();
                this.writer = null;
                return;
            }
            return;
        }
        if (this.writer != null) {
            this.writer.recycle();
        }
    }

    public List<Cookie> getCookies() {
        return this.cookies;
    }

    public long getContentWritten() {
        return this.outputBuffer.getContentWritten();
    }

    public long getBytesWritten(boolean flush) {
        if (flush) {
            try {
                this.outputBuffer.flush();
            } catch (IOException e) {
            }
        }
        return getCoyoteResponse().getBytesWritten(flush);
    }

    public void setAppCommitted(boolean appCommitted) {
        this.appCommitted = appCommitted;
    }

    public boolean isAppCommitted() {
        return this.appCommitted || isCommitted() || isSuspended() || (getContentLength() > 0 && getContentWritten() >= ((long) getContentLength()));
    }

    public Request getRequest() {
        return this.request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        if (this.facade == null) {
            this.facade = new ResponseFacade(this);
        }
        if (this.applicationResponse == null) {
            this.applicationResponse = this.facade;
        }
        return this.applicationResponse;
    }

    public void setResponse(HttpServletResponse applicationResponse) {
        ServletResponse r;
        ServletResponse servletResponse = applicationResponse;
        while (true) {
            r = servletResponse;
            if (!(r instanceof HttpServletResponseWrapper)) {
                break;
            } else {
                servletResponse = ((HttpServletResponseWrapper) r).getResponse();
            }
        }
        if (r != this.facade) {
            throw new IllegalArgumentException(sm.getString("response.illegalWrap"));
        }
        this.applicationResponse = applicationResponse;
    }

    public void setSuspended(boolean suspended) {
        this.outputBuffer.setSuspended(suspended);
    }

    public boolean isSuspended() {
        return this.outputBuffer.isSuspended();
    }

    public boolean isClosed() {
        return this.outputBuffer.isClosed();
    }

    @Deprecated
    public boolean setError() {
        return getCoyoteResponse().setError();
    }

    public boolean isError() {
        return getCoyoteResponse().isError();
    }

    public boolean isErrorReportRequired() {
        return getCoyoteResponse().isErrorReportRequired();
    }

    public boolean setErrorReported() {
        return getCoyoteResponse().setErrorReported();
    }

    public void finishResponse() throws IOException {
        this.outputBuffer.close();
    }

    public int getContentLength() {
        return getCoyoteResponse().getContentLength();
    }

    @Override // jakarta.servlet.ServletResponse
    public String getContentType() {
        return getCoyoteResponse().getContentType();
    }

    public PrintWriter getReporter() throws IOException {
        if (this.outputBuffer.isNew()) {
            this.outputBuffer.checkConverter();
            if (this.writer == null) {
                this.writer = new CoyoteWriter(this.outputBuffer);
            }
            return this.writer;
        }
        return null;
    }

    @Override // jakarta.servlet.ServletResponse
    public void flushBuffer() throws IOException {
        this.outputBuffer.flush();
    }

    @Override // jakarta.servlet.ServletResponse
    public int getBufferSize() {
        return this.outputBuffer.getBufferSize();
    }

    @Override // jakarta.servlet.ServletResponse
    public String getCharacterEncoding() {
        String charset = getCoyoteResponse().getCharacterEncoding();
        if (charset != null) {
            return charset;
        }
        Context context = getContext();
        String result = null;
        if (context != null) {
            result = context.getResponseCharacterEncoding();
        }
        if (result == null) {
            result = Constants.DEFAULT_BODY_CHARSET.name();
        }
        return result;
    }

    @Override // jakarta.servlet.ServletResponse
    public ServletOutputStream getOutputStream() throws IOException {
        if (this.usingWriter) {
            throw new IllegalStateException(sm.getString("coyoteResponse.getOutputStream.ise"));
        }
        this.usingOutputStream = true;
        if (this.outputStream == null) {
            this.outputStream = new CoyoteOutputStream(this.outputBuffer);
        }
        return this.outputStream;
    }

    @Override // jakarta.servlet.ServletResponse
    public Locale getLocale() {
        return getCoyoteResponse().getLocale();
    }

    @Override // jakarta.servlet.ServletResponse
    public PrintWriter getWriter() throws IOException {
        if (this.usingOutputStream) {
            throw new IllegalStateException(sm.getString("coyoteResponse.getWriter.ise"));
        }
        if (this.request.getConnector().getEnforceEncodingInGetWriter()) {
            setCharacterEncoding(getCharacterEncoding());
        }
        this.usingWriter = true;
        this.outputBuffer.checkConverter();
        if (this.writer == null) {
            this.writer = new CoyoteWriter(this.outputBuffer);
        }
        return this.writer;
    }

    @Override // jakarta.servlet.ServletResponse
    public boolean isCommitted() {
        return getCoyoteResponse().isCommitted();
    }

    @Override // jakarta.servlet.ServletResponse
    public void reset() {
        if (this.included) {
            return;
        }
        getCoyoteResponse().reset();
        this.outputBuffer.reset();
        this.usingOutputStream = false;
        this.usingWriter = false;
        this.isCharacterEncodingSet = false;
    }

    @Override // jakarta.servlet.ServletResponse
    public void resetBuffer() {
        resetBuffer(false);
    }

    public void resetBuffer(boolean resetWriterStreamFlags) {
        if (isCommitted()) {
            throw new IllegalStateException(sm.getString("coyoteResponse.resetBuffer.ise"));
        }
        this.outputBuffer.reset(resetWriterStreamFlags);
        if (resetWriterStreamFlags) {
            this.usingOutputStream = false;
            this.usingWriter = false;
            this.isCharacterEncodingSet = false;
        }
    }

    @Override // jakarta.servlet.ServletResponse
    public void setBufferSize(int size) {
        if (isCommitted() || !this.outputBuffer.isNew()) {
            throw new IllegalStateException(sm.getString("coyoteResponse.setBufferSize.ise"));
        }
        this.outputBuffer.setBufferSize(size);
    }

    @Override // jakarta.servlet.ServletResponse
    public void setContentLength(int length) {
        setContentLengthLong(length);
    }

    @Override // jakarta.servlet.ServletResponse
    public void setContentLengthLong(long length) {
        if (isCommitted() || this.included) {
            return;
        }
        getCoyoteResponse().setContentLength(length);
    }

    @Override // jakarta.servlet.ServletResponse
    public void setContentType(String type) {
        if (isCommitted() || this.included) {
            return;
        }
        if (type == null) {
            getCoyoteResponse().setContentType(null);
            try {
                getCoyoteResponse().setCharacterEncoding(null);
            } catch (UnsupportedEncodingException e) {
            }
            this.isCharacterEncodingSet = false;
            return;
        }
        String[] m = MEDIA_TYPE_CACHE.parse(type);
        if (m == null) {
            getCoyoteResponse().setContentTypeNoCharset(type);
            return;
        }
        if (m[1] == null) {
            getCoyoteResponse().setContentTypeNoCharset(type);
            return;
        }
        getCoyoteResponse().setContentTypeNoCharset(m[0]);
        if (!this.usingWriter) {
            try {
                getCoyoteResponse().setCharacterEncoding(m[1]);
            } catch (UnsupportedEncodingException e2) {
                log.warn(sm.getString("coyoteResponse.encoding.invalid", m[1]), e2);
            }
            this.isCharacterEncodingSet = true;
        }
    }

    @Override // jakarta.servlet.ServletResponse
    public void setCharacterEncoding(String encoding) {
        if (isCommitted() || this.included || this.usingWriter) {
            return;
        }
        try {
            getCoyoteResponse().setCharacterEncoding(encoding);
            if (encoding == null) {
                this.isCharacterEncodingSet = false;
            } else {
                this.isCharacterEncodingSet = true;
            }
        } catch (UnsupportedEncodingException e) {
            log.warn(sm.getString("coyoteResponse.encoding.invalid", encoding), e);
        }
    }

    @Override // jakarta.servlet.ServletResponse
    public void setLocale(Locale locale) {
        String charset;
        if (isCommitted() || this.included) {
            return;
        }
        getCoyoteResponse().setLocale(locale);
        if (this.usingWriter || this.isCharacterEncodingSet) {
            return;
        }
        if (locale == null) {
            try {
                getCoyoteResponse().setCharacterEncoding(null);
                return;
            } catch (UnsupportedEncodingException e) {
                return;
            }
        }
        Context context = getContext();
        if (context != null && (charset = context.getCharset(locale)) != null) {
            try {
                getCoyoteResponse().setCharacterEncoding(charset);
            } catch (UnsupportedEncodingException e2) {
                log.warn(sm.getString("coyoteResponse.encoding.invalid", charset), e2);
            }
        }
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public String getHeader(String name) {
        return getCoyoteResponse().getMimeHeaders().getHeader(name);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public Collection<String> getHeaderNames() {
        MimeHeaders headers = getCoyoteResponse().getMimeHeaders();
        int n = headers.size();
        List<String> result = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            result.add(headers.getName(i).toString());
        }
        return result;
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public Collection<String> getHeaders(String name) {
        Enumeration<String> enumeration = getCoyoteResponse().getMimeHeaders().values(name);
        Set<String> result = new LinkedHashSet<>();
        while (enumeration.hasMoreElements()) {
            result.add(enumeration.nextElement());
        }
        return result;
    }

    public String getMessage() {
        return getCoyoteResponse().getMessage();
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public int getStatus() {
        return getCoyoteResponse().getStatus();
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void addCookie(Cookie cookie) {
        if (this.included || isCommitted()) {
            return;
        }
        this.cookies.add(cookie);
        String header = generateCookieString(cookie);
        addHeader(HttpHeaders.SET_COOKIE, header, getContext().getCookieProcessor().getCharset());
    }

    public void addSessionCookieInternal(Cookie cookie) {
        if (isCommitted()) {
            return;
        }
        String name = cookie.getName();
        String startsWith = name + "=";
        String header = generateCookieString(cookie);
        boolean set = false;
        MimeHeaders headers = getCoyoteResponse().getMimeHeaders();
        int n = headers.size();
        for (int i = 0; i < n; i++) {
            if (headers.getName(i).toString().equals(HttpHeaders.SET_COOKIE) && headers.getValue(i).toString().startsWith(startsWith)) {
                headers.getValue(i).setString(header);
                set = true;
            }
        }
        if (!set) {
            addHeader(HttpHeaders.SET_COOKIE, header);
        }
    }

    public String generateCookieString(Cookie cookie) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (String) AccessController.doPrivileged(new PrivilegedGenerateCookieString(getContext(), cookie, this.request.getRequest()));
        }
        return getContext().getCookieProcessor().generateHeader(cookie, this.request.getRequest());
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void addDateHeader(String name, long value) {
        if (name == null || name.length() == 0 || isCommitted() || this.included) {
            return;
        }
        addHeader(name, FastHttpDateFormat.formatDate(value));
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void addHeader(String name, String value) {
        addHeader(name, value, null);
    }

    private void addHeader(String name, String value, Charset charset) {
        if (name == null || name.length() == 0 || value == null || isCommitted() || this.included) {
            return;
        }
        char cc = name.charAt(0);
        if ((cc == 'C' || cc == 'c') && checkSpecialHeader(name, value)) {
            return;
        }
        getCoyoteResponse().addHeader(name, value, charset);
    }

    private boolean checkSpecialHeader(String name, String value) {
        if (name.equalsIgnoreCase(HttpHeaders.CONTENT_TYPE)) {
            setContentType(value);
            return true;
        }
        return false;
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void addIntHeader(String name, int value) {
        if (name == null || name.length() == 0 || isCommitted() || this.included) {
            return;
        }
        addHeader(name, value);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public boolean containsHeader(String name) {
        char cc = name.charAt(0);
        if (cc == 'C' || cc == 'c') {
            if (name.equalsIgnoreCase(HttpHeaders.CONTENT_TYPE)) {
                return getCoyoteResponse().getContentType() != null;
            }
            if (name.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
                return getCoyoteResponse().getContentLengthLong() != -1;
            }
        }
        return getCoyoteResponse().containsHeader(name);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void setTrailerFields(Supplier<Map<String, String>> supplier) {
        getCoyoteResponse().setTrailerFields(supplier);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public Supplier<Map<String, String>> getTrailerFields() {
        return getCoyoteResponse().getTrailerFields();
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public String encodeRedirectURL(String url) {
        if (isEncodeable(toAbsolute(url))) {
            return toEncoded(url, this.request.getSessionInternal().getIdInternal());
        }
        return url;
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public String encodeURL(String url) {
        try {
            String absolute = toAbsolute(url);
            if (isEncodeable(absolute)) {
                if (url.equalsIgnoreCase("")) {
                    url = absolute;
                } else if (url.equals(absolute) && !hasPath(url)) {
                    url = url + "/";
                }
                return toEncoded(url, this.request.getSessionInternal().getIdInternal());
            }
            return url;
        } catch (IllegalArgumentException e) {
            return url;
        }
    }

    public void sendAcknowledgement(ContinueResponseTiming continueResponseTiming) throws IOException {
        if (isCommitted() || this.included) {
            return;
        }
        getCoyoteResponse().action(ActionCode.ACK, continueResponseTiming);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void sendError(int status) throws IOException {
        sendError(status, null);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void sendError(int status, String message) throws IOException {
        if (isCommitted()) {
            throw new IllegalStateException(sm.getString("coyoteResponse.sendError.ise"));
        }
        if (this.included) {
            return;
        }
        setError();
        getCoyoteResponse().setStatus(status);
        getCoyoteResponse().setMessage(message);
        resetBuffer();
        setSuspended(true);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void sendRedirect(String location) throws IOException {
        sendRedirect(location, 302);
    }

    public void sendRedirect(String location, int status) throws IOException {
        String locationUri;
        if (isCommitted()) {
            throw new IllegalStateException(sm.getString("coyoteResponse.sendRedirect.ise"));
        }
        if (this.included) {
            return;
        }
        resetBuffer(true);
        try {
            Context context = getContext();
            if (getRequest().getCoyoteRequest().getSupportsRelativeRedirects() && (context == null || context.getUseRelativeRedirects())) {
                locationUri = location;
            } else {
                locationUri = toAbsolute(location);
            }
            setStatus(status);
            setHeader("Location", locationUri);
            if (context != null && context.getSendRedirectBody()) {
                PrintWriter writer = getWriter();
                writer.print(sm.getString("coyoteResponse.sendRedirect.note", Escape.htmlElementContent(locationUri)));
                flushBuffer();
            }
        } catch (IllegalArgumentException e) {
            log.warn(sm.getString("response.sendRedirectFail", location), e);
            setStatus(404);
        }
        setSuspended(true);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void setDateHeader(String name, long value) {
        if (name == null || name.length() == 0 || isCommitted() || this.included) {
            return;
        }
        setHeader(name, FastHttpDateFormat.formatDate(value));
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void setHeader(String name, String value) {
        if (name == null || name.length() == 0 || value == null || isCommitted() || this.included) {
            return;
        }
        char cc = name.charAt(0);
        if ((cc == 'C' || cc == 'c') && checkSpecialHeader(name, value)) {
            return;
        }
        getCoyoteResponse().setHeader(name, value);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void setIntHeader(String name, int value) {
        if (name == null || name.length() == 0 || isCommitted() || this.included) {
            return;
        }
        setHeader(name, value);
    }

    @Override // jakarta.servlet.http.HttpServletResponse
    public void setStatus(int status) {
        if (isCommitted() || this.included) {
            return;
        }
        getCoyoteResponse().setStatus(status);
    }

    protected boolean isEncodeable(String location) {
        Request hreq;
        Session session;
        if (location == null || location.startsWith("#") || (session = (hreq = this.request).getSessionInternal(false)) == null || hreq.isRequestedSessionIdFromCookie() || !hreq.getServletContext().getEffectiveSessionTrackingModes().contains(SessionTrackingMode.URL)) {
            return false;
        }
        if (SecurityUtil.isPackageProtectionEnabled()) {
            Boolean result = (Boolean) AccessController.doPrivileged(new PrivilegedDoIsEncodable(getContext(), hreq, session, location));
            return result.booleanValue();
        }
        return doIsEncodeable(getContext(), hreq, session, location);
    }

    private static boolean doIsEncodeable(Context context, Request hreq, Session session, String location) {
        try {
            URI uri = new URI(location);
            URL url = uri.toURL();
            if (!hreq.getScheme().equalsIgnoreCase(url.getProtocol()) || !hreq.getServerName().equalsIgnoreCase(url.getHost())) {
                return false;
            }
            int serverPort = hreq.getServerPort();
            if (serverPort == -1) {
                if ("https".equals(hreq.getScheme())) {
                    serverPort = 443;
                } else {
                    serverPort = 80;
                }
            }
            int urlPort = url.getPort();
            if (urlPort == -1) {
                if ("https".equals(url.getProtocol())) {
                    urlPort = 443;
                } else {
                    urlPort = 80;
                }
            }
            if (serverPort != urlPort) {
                return false;
            }
            String contextPath = context.getPath();
            if (contextPath != null) {
                String file = url.getFile();
                if (!file.startsWith(contextPath)) {
                    return false;
                }
                String tok = ";" + SessionConfig.getSessionUriParamName(context) + "=" + session.getIdInternal();
                if (file.indexOf(tok, contextPath.length()) >= 0) {
                    return false;
                }
                return true;
            }
            return true;
        } catch (IllegalArgumentException | MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    protected String toAbsolute(String location) {
        CharChunk encodedURI;
        if (location == null) {
            return location;
        }
        boolean leadingSlash = location.startsWith("/");
        if (location.startsWith("//")) {
            this.redirectURLCC.recycle();
            String scheme = this.request.getScheme();
            try {
                this.redirectURLCC.append(scheme, 0, scheme.length());
                this.redirectURLCC.append(':');
                this.redirectURLCC.append(location, 0, location.length());
                return this.redirectURLCC.toString();
            } catch (IOException e) {
                throw new IllegalArgumentException(location, e);
            }
        }
        if (leadingSlash || !UriUtil.hasScheme(location)) {
            this.redirectURLCC.recycle();
            String scheme2 = this.request.getScheme();
            String name = this.request.getServerName();
            int port = this.request.getServerPort();
            try {
                this.redirectURLCC.append(scheme2, 0, scheme2.length());
                this.redirectURLCC.append("://", 0, 3);
                this.redirectURLCC.append(name, 0, name.length());
                if ((scheme2.equals("http") && port != 80) || (scheme2.equals("https") && port != 443)) {
                    this.redirectURLCC.append(':');
                    String portS = port;
                    this.redirectURLCC.append(portS, 0, portS.length());
                }
                if (!leadingSlash) {
                    String relativePath = this.request.getDecodedRequestURI();
                    int pos = relativePath.lastIndexOf(47);
                    if (SecurityUtil.isPackageProtectionEnabled()) {
                        try {
                            encodedURI = (CharChunk) AccessController.doPrivileged(new PrivilegedEncodeUrl(this.urlEncoder, relativePath, pos));
                        } catch (PrivilegedActionException pae) {
                            throw new IllegalArgumentException(location, pae.getException());
                        }
                    } else {
                        encodedURI = this.urlEncoder.encodeURL(relativePath, 0, pos);
                    }
                    this.redirectURLCC.append(encodedURI);
                    encodedURI.recycle();
                    this.redirectURLCC.append('/');
                }
                this.redirectURLCC.append(location, 0, location.length());
                normalize(this.redirectURLCC);
                return this.redirectURLCC.toString();
            } catch (IOException e2) {
                throw new IllegalArgumentException(location, e2);
            }
        }
        return location;
    }

    private void normalize(CharChunk cc) {
        int truncate = cc.indexOf('?');
        if (truncate == -1) {
            truncate = cc.indexOf('#');
        }
        char[] truncateCC = null;
        if (truncate > -1) {
            truncateCC = Arrays.copyOfRange(cc.getBuffer(), cc.getStart() + truncate, cc.getEnd());
            cc.setEnd(cc.getStart() + truncate);
        }
        if (cc.endsWith("/.") || cc.endsWith("/..")) {
            try {
                cc.append('/');
            } catch (IOException e) {
                throw new IllegalArgumentException(cc.toString(), e);
            }
        }
        char[] c = cc.getChars();
        int start = cc.getStart();
        int end = cc.getEnd();
        int startIndex = 0;
        for (int i = 0; i < 3; i++) {
            startIndex = cc.indexOf('/', startIndex + 1);
        }
        int index = startIndex;
        while (true) {
            index = cc.indexOf("/./", 0, 3, index);
            if (index < 0) {
                break;
            }
            copyChars(c, start + index, start + index + 2, ((end - start) - index) - 2);
            end -= 2;
            cc.setEnd(end);
        }
        int i2 = startIndex;
        while (true) {
            int index2 = i2;
            int index3 = cc.indexOf("/../", 0, 4, index2);
            if (index3 >= 0) {
                if (index3 == startIndex) {
                    throw new IllegalArgumentException();
                }
                int index22 = -1;
                for (int pos = (start + index3) - 1; pos >= 0 && index22 < 0; pos--) {
                    if (c[pos] == '/') {
                        index22 = pos;
                    }
                }
                copyChars(c, start + index22, start + index3 + 3, ((end - start) - index3) - 3);
                end = ((end + index22) - index3) - 3;
                cc.setEnd(end);
                i2 = index22;
            } else {
                if (truncateCC != null) {
                    try {
                        cc.append(truncateCC, 0, truncateCC.length);
                        return;
                    } catch (IOException ioe) {
                        throw new IllegalArgumentException(ioe);
                    }
                }
                return;
            }
        }
    }

    private void copyChars(char[] c, int dest, int src, int len) {
        System.arraycopy(c, src, c, dest, len);
    }

    private boolean hasPath(String uri) {
        int pos = uri.indexOf("://");
        if (pos < 0 || uri.indexOf(47, pos + 3) < 0) {
            return false;
        }
        return true;
    }

    protected String toEncoded(String url, String sessionId) {
        if (url == null || sessionId == null) {
            return url;
        }
        String path = url;
        String query = "";
        String anchor = "";
        int question = url.indexOf(63);
        if (question >= 0) {
            path = url.substring(0, question);
            query = url.substring(question);
        }
        int pound = path.indexOf(35);
        if (pound >= 0) {
            anchor = path.substring(pound);
            path = path.substring(0, pound);
        }
        StringBuilder sb = new StringBuilder(path);
        if (sb.length() > 0) {
            sb.append(';');
            sb.append(SessionConfig.getSessionUriParamName(this.request.getContext()));
            sb.append('=');
            sb.append(sessionId);
        }
        sb.append(anchor);
        sb.append(query);
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/Response$PrivilegedGenerateCookieString.class */
    public static class PrivilegedGenerateCookieString implements PrivilegedAction<String> {
        private final Context context;
        private final Cookie cookie;
        private final HttpServletRequest request;

        PrivilegedGenerateCookieString(Context context, Cookie cookie, HttpServletRequest request) {
            this.context = context;
            this.cookie = cookie;
            this.request = request;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public String run() {
            return this.context.getCookieProcessor().generateHeader(this.cookie, this.request);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/Response$PrivilegedDoIsEncodable.class */
    public static class PrivilegedDoIsEncodable implements PrivilegedAction<Boolean> {
        private final Context context;
        private final Request hreq;
        private final Session session;
        private final String location;

        PrivilegedDoIsEncodable(Context context, Request hreq, Session session, String location) {
            this.context = context;
            this.hreq = hreq;
            this.session = session;
            this.location = location;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public Boolean run() {
            return Boolean.valueOf(Response.doIsEncodeable(this.context, this.hreq, this.session, this.location));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/Response$PrivilegedEncodeUrl.class */
    public static class PrivilegedEncodeUrl implements PrivilegedExceptionAction<CharChunk> {
        private final UEncoder urlEncoder;
        private final String relativePath;
        private final int end;

        PrivilegedEncodeUrl(UEncoder urlEncoder, String relativePath, int end) {
            this.urlEncoder = urlEncoder;
            this.relativePath = relativePath;
            this.end = end;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public CharChunk run() throws IOException {
            return this.urlEncoder.encodeURL(this.relativePath, 0, this.end);
        }
    }
}
