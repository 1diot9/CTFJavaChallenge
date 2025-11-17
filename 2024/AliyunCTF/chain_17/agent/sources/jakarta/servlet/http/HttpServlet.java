package jakarta.servlet.http;

import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.GenericServlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.WriteListener;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import org.springframework.http.HttpHeaders;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/HttpServlet.class */
public abstract class HttpServlet extends GenericServlet {
    private static final long serialVersionUID = 1;
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_HEAD = "HEAD";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_TRACE = "TRACE";
    private static final String HEADER_IFMODSINCE = "If-Modified-Since";
    private static final String HEADER_LASTMOD = "Last-Modified";
    private static final String LSTRING_FILE = "jakarta.servlet.http.LocalStrings";
    private static final ResourceBundle lStrings = ResourceBundle.getBundle(LSTRING_FILE);
    private static final List<String> SENSITIVE_HTTP_HEADERS = Arrays.asList("authorization", "cookie", "x-forwarded", "forwarded", "proxy-authorization");

    @Deprecated(forRemoval = true, since = "Servlet 6.0")
    public static final String LEGACY_DO_HEAD = "jakarta.servlet.http.legacyDoHead";
    private final transient Object cachedAllowHeaderValueLock = new Object();
    private volatile String cachedAllowHeaderValue = null;
    private volatile boolean cachedUseLegacyDoHead;

    @Override // jakarta.servlet.GenericServlet, jakarta.servlet.Servlet
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.cachedUseLegacyDoHead = Boolean.parseBoolean(config.getInitParameter(LEGACY_DO_HEAD));
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String msg = lStrings.getString("http.method_get_not_supported");
        sendMethodNotAllowed(req, resp, msg);
    }

    protected long getLastModified(HttpServletRequest req) {
        return -1L;
    }

    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (DispatcherType.INCLUDE.equals(req.getDispatcherType()) || !this.cachedUseLegacyDoHead) {
            doGet(req, resp);
            return;
        }
        NoBodyResponse response = new NoBodyResponse(resp);
        doGet(req, response);
        if (req.isAsyncStarted()) {
            req.getAsyncContext().addListener(new NoBodyAsyncContextListener(response));
        } else {
            response.setContentLength();
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String msg = lStrings.getString("http.method_post_not_supported");
        sendMethodNotAllowed(req, resp, msg);
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String msg = lStrings.getString("http.method_put_not_supported");
        sendMethodNotAllowed(req, resp, msg);
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String msg = lStrings.getString("http.method_delete_not_supported");
        sendMethodNotAllowed(req, resp, msg);
    }

    private void sendMethodNotAllowed(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        String protocol = req.getProtocol();
        if (protocol.length() == 0 || protocol.endsWith("0.9") || protocol.endsWith("1.0")) {
            resp.sendError(400, msg);
        } else {
            resp.sendError(405, msg);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x00d8  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x00e0  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x00e6  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00ec  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x00f2 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private java.lang.String getCachedAllowHeaderValue() {
        /*
            Method dump skipped, instructions count: 395
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: jakarta.servlet.http.HttpServlet.getCachedAllowHeaderValue():java.lang.String");
    }

    private static Method[] getAllDeclaredMethods(Class<?> c) {
        if (c.equals(HttpServlet.class)) {
            return null;
        }
        Method[] parentMethods = getAllDeclaredMethods(c.getSuperclass());
        Method[] thisMethods = c.getDeclaredMethods();
        if (parentMethods != null && parentMethods.length > 0) {
            Method[] allMethods = new Method[parentMethods.length + thisMethods.length];
            System.arraycopy(parentMethods, 0, allMethods, 0, parentMethods.length);
            System.arraycopy(thisMethods, 0, allMethods, parentMethods.length, thisMethods.length);
            thisMethods = allMethods;
        }
        return thisMethods;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String allow = getCachedAllowHeaderValue();
        if (TomcatHack.getAllowTrace(req)) {
            if (allow.length() == 0) {
                allow = "TRACE";
            } else {
                allow = allow + ", TRACE";
            }
        }
        resp.setHeader(HttpHeaders.ALLOW, allow);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StringBuilder buffer = new StringBuilder("TRACE ").append(req.getRequestURI()).append(' ').append(req.getProtocol());
        Enumeration<String> reqHeaderNames = req.getHeaderNames();
        while (reqHeaderNames.hasMoreElements()) {
            String headerName = reqHeaderNames.nextElement();
            if (!isSensitiveHeader(headerName)) {
                Enumeration<String> headerValues = req.getHeaders(headerName);
                while (headerValues.hasMoreElements()) {
                    String headerValue = headerValues.nextElement();
                    buffer.append("\r\n").append(headerName).append(": ").append(headerValue);
                }
            }
        }
        buffer.append("\r\n");
        int responseLength = buffer.length();
        resp.setContentType("message/http");
        resp.setContentLength(responseLength);
        ServletOutputStream out = resp.getOutputStream();
        out.print(buffer.toString());
        out.close();
    }

    private boolean isSensitiveHeader(String headerName) {
        String lcHeaderName = headerName.toLowerCase(Locale.ENGLISH);
        for (String sensitiveHeaderName : SENSITIVE_HTTP_HEADERS) {
            if (lcHeaderName.startsWith(sensitiveHeaderName)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long ifModifiedSince;
        String method = req.getMethod();
        if (method.equals("GET")) {
            long lastModified = getLastModified(req);
            if (lastModified == -1) {
                doGet(req, resp);
                return;
            }
            try {
                ifModifiedSince = req.getDateHeader("If-Modified-Since");
            } catch (IllegalArgumentException e) {
                ifModifiedSince = -1;
            }
            if (ifModifiedSince < (lastModified / 1000) * 1000) {
                maybeSetLastModified(resp, lastModified);
                doGet(req, resp);
                return;
            } else {
                resp.setStatus(304);
                return;
            }
        }
        if (method.equals("HEAD")) {
            maybeSetLastModified(resp, getLastModified(req));
            doHead(req, resp);
            return;
        }
        if (method.equals("POST")) {
            doPost(req, resp);
            return;
        }
        if (method.equals("PUT")) {
            doPut(req, resp);
            return;
        }
        if (method.equals("DELETE")) {
            doDelete(req, resp);
            return;
        }
        if (method.equals("OPTIONS")) {
            doOptions(req, resp);
        } else {
            if (method.equals("TRACE")) {
                doTrace(req, resp);
                return;
            }
            String errMsg = lStrings.getString("http.method_not_implemented");
            Object[] errArgs = {method};
            resp.sendError(501, MessageFormat.format(errMsg, errArgs));
        }
    }

    private void maybeSetLastModified(HttpServletResponse resp, long lastModified) {
        if (!resp.containsHeader("Last-Modified") && lastModified >= 0) {
            resp.setDateHeader("Last-Modified", lastModified);
        }
    }

    @Override // jakarta.servlet.GenericServlet, jakarta.servlet.Servlet
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        try {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;
            service(request, response);
        } catch (ClassCastException e) {
            throw new ServletException(lStrings.getString("http.non_http"));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/HttpServlet$TomcatHack.class */
    public static class TomcatHack {
        private static final Class<?> REQUEST_FACADE_CLAZZ;
        private static final Method GET_ALLOW_TRACE;

        private TomcatHack() {
        }

        static {
            Method m1 = null;
            Class<?> c1 = null;
            try {
                c1 = Class.forName("org.apache.catalina.connector.RequestFacade");
                m1 = c1.getMethod("getAllowTrace", (Class[]) null);
            } catch (IllegalArgumentException | ReflectiveOperationException | SecurityException e) {
            }
            REQUEST_FACADE_CLAZZ = c1;
            GET_ALLOW_TRACE = m1;
        }

        public static boolean getAllowTrace(HttpServletRequest req) {
            if (REQUEST_FACADE_CLAZZ != null && GET_ALLOW_TRACE != null && REQUEST_FACADE_CLAZZ.isAssignableFrom(req.getClass())) {
                try {
                    return ((Boolean) GET_ALLOW_TRACE.invoke(req, (Object[]) null)).booleanValue();
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    return true;
                }
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/HttpServlet$NoBodyResponse.class */
    public static class NoBodyResponse extends HttpServletResponseWrapper {
        private final NoBodyOutputStream noBodyOutputStream;
        private ServletOutputStream originalOutputStream;
        private NoBodyPrintWriter noBodyWriter;
        private boolean didSetContentLength;

        private NoBodyResponse(HttpServletResponse r) {
            super(r);
            this.noBodyOutputStream = new NoBodyOutputStream(this);
        }

        private void setContentLength() {
            if (!this.didSetContentLength) {
                if (this.noBodyWriter != null) {
                    this.noBodyWriter.flush();
                }
                super.setContentLengthLong(this.noBodyOutputStream.getWrittenByteCount());
            }
        }

        @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
        public void setContentLength(int len) {
            super.setContentLength(len);
            this.didSetContentLength = true;
        }

        @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
        public void setContentLengthLong(long len) {
            super.setContentLengthLong(len);
            this.didSetContentLength = true;
        }

        @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
        public void setHeader(String name, String value) {
            super.setHeader(name, value);
            checkHeader(name);
        }

        @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
        public void addHeader(String name, String value) {
            super.addHeader(name, value);
            checkHeader(name);
        }

        @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
        public void setIntHeader(String name, int value) {
            super.setIntHeader(name, value);
            checkHeader(name);
        }

        @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
        public void addIntHeader(String name, int value) {
            super.addIntHeader(name, value);
            checkHeader(name);
        }

        private void checkHeader(String name) {
            if ("content-length".equalsIgnoreCase(name)) {
                this.didSetContentLength = true;
            }
        }

        @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
        public ServletOutputStream getOutputStream() throws IOException {
            this.originalOutputStream = getResponse().getOutputStream();
            return this.noBodyOutputStream;
        }

        @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
        public PrintWriter getWriter() throws UnsupportedEncodingException {
            if (this.noBodyWriter == null) {
                this.noBodyWriter = new NoBodyPrintWriter(this.noBodyOutputStream, getCharacterEncoding());
            }
            return this.noBodyWriter;
        }

        @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
        public void reset() {
            super.reset();
            resetBuffer();
            this.originalOutputStream = null;
        }

        @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
        public void resetBuffer() {
            this.noBodyOutputStream.resetBuffer();
            if (this.noBodyWriter != null) {
                this.noBodyWriter.resetBuffer();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/HttpServlet$NoBodyOutputStream.class */
    public static class NoBodyOutputStream extends ServletOutputStream {
        private static final String LSTRING_FILE = "jakarta.servlet.http.LocalStrings";
        private static final ResourceBundle lStrings = ResourceBundle.getBundle(LSTRING_FILE);
        private final NoBodyResponse response;
        private boolean flushed = false;
        private long writtenByteCount = 0;

        private NoBodyOutputStream(NoBodyResponse response) {
            this.response = response;
        }

        private long getWrittenByteCount() {
            return this.writtenByteCount;
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            this.writtenByteCount += HttpServlet.serialVersionUID;
            checkCommit();
        }

        @Override // java.io.OutputStream
        public void write(byte[] buf, int offset, int len) throws IOException {
            if (buf == null) {
                throw new NullPointerException(lStrings.getString("err.io.nullArray"));
            }
            if (offset < 0 || len < 0 || offset + len > buf.length) {
                String msg = lStrings.getString("err.io.indexOutOfBounds");
                Object[] msgArgs = {Integer.valueOf(offset), Integer.valueOf(len), Integer.valueOf(buf.length)};
                throw new IndexOutOfBoundsException(MessageFormat.format(msg, msgArgs));
            }
            this.writtenByteCount += len;
            checkCommit();
        }

        @Override // jakarta.servlet.ServletOutputStream
        public boolean isReady() {
            return true;
        }

        @Override // jakarta.servlet.ServletOutputStream
        public void setWriteListener(WriteListener listener) {
            this.response.originalOutputStream.setWriteListener(listener);
        }

        private void checkCommit() throws IOException {
            if (!this.flushed && this.writtenByteCount > this.response.getBufferSize()) {
                this.response.flushBuffer();
                this.flushed = true;
            }
        }

        private void resetBuffer() {
            if (this.flushed) {
                throw new IllegalStateException(lStrings.getString("err.state.commit"));
            }
            this.writtenByteCount = 0L;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/HttpServlet$NoBodyPrintWriter.class */
    public static class NoBodyPrintWriter extends PrintWriter {
        private final NoBodyOutputStream out;
        private final String encoding;
        private PrintWriter pw;

        NoBodyPrintWriter(NoBodyOutputStream out, String encoding) throws UnsupportedEncodingException {
            super(out);
            this.out = out;
            this.encoding = encoding;
            Writer osw = new OutputStreamWriter(out, encoding);
            this.pw = new PrintWriter(osw);
        }

        private void resetBuffer() {
            this.out.resetBuffer();
            Writer osw = null;
            try {
                osw = new OutputStreamWriter(this.out, this.encoding);
            } catch (UnsupportedEncodingException e) {
            }
            this.pw = new PrintWriter(osw);
        }

        @Override // java.io.PrintWriter, java.io.Writer, java.io.Flushable
        public void flush() {
            this.pw.flush();
        }

        @Override // java.io.PrintWriter, java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            this.pw.close();
        }

        @Override // java.io.PrintWriter
        public boolean checkError() {
            return this.pw.checkError();
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(int c) {
            this.pw.write(c);
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(char[] buf, int off, int len) {
            this.pw.write(buf, off, len);
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(char[] buf) {
            this.pw.write(buf);
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(String s, int off, int len) {
            this.pw.write(s, off, len);
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(String s) {
            this.pw.write(s);
        }

        @Override // java.io.PrintWriter
        public void print(boolean b) {
            this.pw.print(b);
        }

        @Override // java.io.PrintWriter
        public void print(char c) {
            this.pw.print(c);
        }

        @Override // java.io.PrintWriter
        public void print(int i) {
            this.pw.print(i);
        }

        @Override // java.io.PrintWriter
        public void print(long l) {
            this.pw.print(l);
        }

        @Override // java.io.PrintWriter
        public void print(float f) {
            this.pw.print(f);
        }

        @Override // java.io.PrintWriter
        public void print(double d) {
            this.pw.print(d);
        }

        @Override // java.io.PrintWriter
        public void print(char[] s) {
            this.pw.print(s);
        }

        @Override // java.io.PrintWriter
        public void print(String s) {
            this.pw.print(s);
        }

        @Override // java.io.PrintWriter
        public void print(Object obj) {
            this.pw.print(obj);
        }

        @Override // java.io.PrintWriter
        public void println() {
            this.pw.println();
        }

        @Override // java.io.PrintWriter
        public void println(boolean x) {
            this.pw.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(char x) {
            this.pw.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(int x) {
            this.pw.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(long x) {
            this.pw.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(float x) {
            this.pw.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(double x) {
            this.pw.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(char[] x) {
            this.pw.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(String x) {
            this.pw.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(Object x) {
            this.pw.println(x);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/HttpServlet$NoBodyAsyncContextListener.class */
    public static class NoBodyAsyncContextListener implements AsyncListener {
        private final NoBodyResponse noBodyResponse;

        NoBodyAsyncContextListener(NoBodyResponse noBodyResponse) {
            this.noBodyResponse = noBodyResponse;
        }

        @Override // jakarta.servlet.AsyncListener
        public void onComplete(AsyncEvent event) throws IOException {
            this.noBodyResponse.setContentLength();
        }

        @Override // jakarta.servlet.AsyncListener
        public void onTimeout(AsyncEvent event) throws IOException {
        }

        @Override // jakarta.servlet.AsyncListener
        public void onError(AsyncEvent event) throws IOException {
        }

        @Override // jakarta.servlet.AsyncListener
        public void onStartAsync(AsyncEvent event) throws IOException {
        }
    }
}
