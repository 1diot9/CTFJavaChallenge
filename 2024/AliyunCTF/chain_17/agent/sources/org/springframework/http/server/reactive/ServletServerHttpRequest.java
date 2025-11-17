package org.springframework.http.server.reactive;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.tomcat.websocket.BasicAuthenticator;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ServletServerHttpRequest.class */
public class ServletServerHttpRequest extends AbstractServerHttpRequest {
    static final DataBuffer EOF_BUFFER = DefaultDataBufferFactory.sharedInstance.allocateBuffer(0);
    private final HttpServletRequest request;
    private final ServletInputStream inputStream;
    private final RequestBodyPublisher bodyPublisher;
    private final Object cookieLock;
    private final DataBufferFactory bufferFactory;
    private final byte[] buffer;
    private final AsyncListener asyncListener;

    public ServletServerHttpRequest(HttpServletRequest request, AsyncContext asyncContext, String servletPath, DataBufferFactory bufferFactory, int bufferSize) throws IOException, URISyntaxException {
        this(createDefaultHttpHeaders(request), request, asyncContext, servletPath, bufferFactory, bufferSize);
    }

    public ServletServerHttpRequest(MultiValueMap<String, String> headers, HttpServletRequest request, AsyncContext asyncContext, String servletPath, DataBufferFactory bufferFactory, int bufferSize) throws IOException, URISyntaxException {
        super(HttpMethod.valueOf(request.getMethod()), initUri(request), request.getContextPath() + servletPath, initHeaders(headers, request));
        this.cookieLock = new Object();
        Assert.notNull(bufferFactory, "'bufferFactory' must not be null");
        Assert.isTrue(bufferSize > 0, "'bufferSize' must be greater than 0");
        this.request = request;
        this.bufferFactory = bufferFactory;
        this.buffer = new byte[bufferSize];
        this.asyncListener = new RequestAsyncListener();
        this.inputStream = request.getInputStream();
        this.bodyPublisher = new RequestBodyPublisher(this.inputStream);
        this.bodyPublisher.registerReadListener();
    }

    private static MultiValueMap<String, String> createDefaultHttpHeaders(HttpServletRequest request) {
        MultiValueMap<String, String> headers = CollectionUtils.toMultiValueMap(new LinkedCaseInsensitiveMap(8, Locale.ENGLISH));
        Enumeration<?> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            Enumeration<?> values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                headers.add(name, values.nextElement());
            }
        }
        return headers;
    }

    private static URI initUri(HttpServletRequest request) throws URISyntaxException {
        Assert.notNull(request, "'request' must not be null");
        StringBuffer url = request.getRequestURL();
        String query = request.getQueryString();
        if (StringUtils.hasText(query)) {
            url.append('?').append(query);
        }
        return new URI(url.toString());
    }

    private static MultiValueMap<String, String> initHeaders(MultiValueMap<String, String> headerValues, HttpServletRequest request) {
        int contentLength;
        HttpHeaders headers = null;
        MediaType contentType = null;
        if (!StringUtils.hasLength(headerValues.getFirst(HttpHeaders.CONTENT_TYPE))) {
            String requestContentType = request.getContentType();
            if (StringUtils.hasLength(requestContentType)) {
                contentType = MediaType.parseMediaType(requestContentType);
                headers = new HttpHeaders(headerValues);
                headers.setContentType(contentType);
            }
        }
        if (contentType != null && contentType.getCharset() == null) {
            String encoding = request.getCharacterEncoding();
            if (StringUtils.hasLength(encoding)) {
                Map<String, String> params = new LinkedCaseInsensitiveMap<>();
                params.putAll(contentType.getParameters());
                params.put(BasicAuthenticator.charsetparam, Charset.forName(encoding).toString());
                headers.setContentType(new MediaType(contentType, params));
            }
        }
        if (headerValues.getFirst(HttpHeaders.CONTENT_TYPE) == null && (contentLength = request.getContentLength()) != -1) {
            headers = headers != null ? headers : new HttpHeaders(headerValues);
            headers.setContentLength(contentLength);
        }
        return headers != null ? headers : headerValues;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    protected MultiValueMap<String, HttpCookie> initCookies() {
        Cookie[] cookies;
        MultiValueMap<String, HttpCookie> httpCookies = new LinkedMultiValueMap<>();
        synchronized (this.cookieLock) {
            cookies = this.request.getCookies();
        }
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                HttpCookie httpCookie = new HttpCookie(name, cookie.getValue());
                httpCookies.add(name, httpCookie);
            }
        }
        return httpCookies;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    @NonNull
    public InetSocketAddress getLocalAddress() {
        return new InetSocketAddress(this.request.getLocalAddr(), this.request.getLocalPort());
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    @NonNull
    public InetSocketAddress getRemoteAddress() {
        return new InetSocketAddress(this.request.getRemoteHost(), this.request.getRemotePort());
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    @Nullable
    protected SslInfo initSslInfo() {
        X509Certificate[] certificates = getX509Certificates();
        if (certificates != null) {
            return new DefaultSslInfo(getSslSessionId(), certificates);
        }
        return null;
    }

    @Nullable
    private String getSslSessionId() {
        return (String) this.request.getAttribute("jakarta.servlet.request.ssl_session_id");
    }

    @Nullable
    private X509Certificate[] getX509Certificates() {
        return (X509Certificate[]) this.request.getAttribute("jakarta.servlet.request.X509Certificate");
    }

    @Override // org.springframework.http.ReactiveHttpInputMessage
    public Flux<DataBuffer> getBody() {
        return Flux.from(this.bodyPublisher);
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    public <T> T getNativeRequest() {
        return (T) this.request;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AsyncListener getAsyncListener() {
        return this.asyncListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ServletInputStream getInputStream() {
        return this.inputStream;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DataBuffer readFromInputStream() throws IOException {
        int read = this.inputStream.read(this.buffer);
        logBytesRead(read);
        if (read > 0) {
            DataBuffer dataBuffer = this.bufferFactory.allocateBuffer(read);
            dataBuffer.write(this.buffer, 0, read);
            return dataBuffer;
        }
        if (read == -1) {
            return EOF_BUFFER;
        }
        return AbstractListenerReadPublisher.EMPTY_BUFFER;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void logBytesRead(int read) {
        Log rsReadLogger = AbstractListenerReadPublisher.rsReadLogger;
        if (rsReadLogger.isTraceEnabled()) {
            rsReadLogger.trace(getLogPrefix() + "Read " + read + (read != -1 ? " bytes" : ""));
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ServletServerHttpRequest$RequestAsyncListener.class */
    private final class RequestAsyncListener implements AsyncListener {
        private RequestAsyncListener() {
        }

        @Override // jakarta.servlet.AsyncListener
        public void onStartAsync(AsyncEvent event) {
        }

        @Override // jakarta.servlet.AsyncListener
        public void onTimeout(AsyncEvent event) {
            Throwable ex = event.getThrowable();
            ServletServerHttpRequest.this.bodyPublisher.onError(ex != null ? ex : new IllegalStateException("Async operation timeout."));
        }

        @Override // jakarta.servlet.AsyncListener
        public void onError(AsyncEvent event) {
            ServletServerHttpRequest.this.bodyPublisher.onError(event.getThrowable());
        }

        @Override // jakarta.servlet.AsyncListener
        public void onComplete(AsyncEvent event) {
            ServletServerHttpRequest.this.bodyPublisher.onAllDataRead();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ServletServerHttpRequest$RequestBodyPublisher.class */
    private class RequestBodyPublisher extends AbstractListenerReadPublisher<DataBuffer> {
        private final ServletInputStream inputStream;

        public RequestBodyPublisher(ServletInputStream inputStream) {
            super(ServletServerHttpRequest.this.getLogPrefix());
            this.inputStream = inputStream;
        }

        public void registerReadListener() throws IOException {
            this.inputStream.setReadListener(new RequestBodyPublisherReadListener());
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher
        protected void checkOnDataAvailable() {
            if (this.inputStream.isReady() && !this.inputStream.isFinished()) {
                onDataAvailable();
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher
        @Nullable
        public DataBuffer read() throws IOException {
            if (this.inputStream.isReady()) {
                DataBuffer dataBuffer = ServletServerHttpRequest.this.readFromInputStream();
                if (dataBuffer == ServletServerHttpRequest.EOF_BUFFER) {
                    onAllDataRead();
                    dataBuffer = null;
                }
                return dataBuffer;
            }
            return null;
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher
        protected void readingPaused() {
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher
        protected void discardData() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ServletServerHttpRequest$RequestBodyPublisher$RequestBodyPublisherReadListener.class */
        public class RequestBodyPublisherReadListener implements ReadListener {
            private RequestBodyPublisherReadListener() {
            }

            @Override // jakarta.servlet.ReadListener
            public void onDataAvailable() throws IOException {
                RequestBodyPublisher.this.onDataAvailable();
            }

            @Override // jakarta.servlet.ReadListener
            public void onAllDataRead() throws IOException {
                RequestBodyPublisher.this.onAllDataRead();
            }

            @Override // jakarta.servlet.ReadListener
            public void onError(Throwable throwable) {
                RequestBodyPublisher.this.onError(throwable);
            }
        }
    }
}
