package org.springframework.http.server.reactive;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import org.apache.tomcat.util.descriptor.web.Constants;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ServletServerHttpResponse.class */
public class ServletServerHttpResponse extends AbstractListenerServerHttpResponse {
    private final HttpServletResponse response;
    private final ServletOutputStream outputStream;
    private final int bufferSize;

    @Nullable
    private volatile ResponseBodyFlushProcessor bodyFlushProcessor;

    @Nullable
    private volatile ResponseBodyProcessor bodyProcessor;
    private volatile boolean flushOnNext;
    private final ServletServerHttpRequest request;
    private final ResponseAsyncListener asyncListener;

    public ServletServerHttpResponse(HttpServletResponse response, AsyncContext asyncContext, DataBufferFactory bufferFactory, int bufferSize, ServletServerHttpRequest request) throws IOException {
        this(new HttpHeaders(), response, asyncContext, bufferFactory, bufferSize, request);
    }

    public ServletServerHttpResponse(HttpHeaders headers, HttpServletResponse response, AsyncContext asyncContext, DataBufferFactory bufferFactory, int bufferSize, ServletServerHttpRequest request) throws IOException {
        super(bufferFactory, headers);
        Assert.notNull(response, "HttpServletResponse must not be null");
        Assert.notNull(bufferFactory, "DataBufferFactory must not be null");
        Assert.isTrue(bufferSize > 0, "Buffer size must be greater than 0");
        this.response = response;
        this.outputStream = response.getOutputStream();
        this.bufferSize = bufferSize;
        this.request = request;
        this.asyncListener = new ResponseAsyncListener();
        response.getOutputStream().setWriteListener(new ResponseBodyWriteListener());
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    public <T> T getNativeResponse() {
        return (T) this.response;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse, org.springframework.http.server.reactive.ServerHttpResponse
    public HttpStatusCode getStatusCode() {
        HttpStatusCode status = super.getStatusCode();
        return status != null ? status : HttpStatusCode.valueOf(this.response.getStatus());
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse, org.springframework.http.server.reactive.ServerHttpResponse
    @Deprecated
    public Integer getRawStatusCode() {
        Integer status = super.getRawStatusCode();
        return Integer.valueOf(status != null ? status.intValue() : this.response.getStatus());
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyStatusCode() {
        HttpStatusCode status = super.getStatusCode();
        if (status != null) {
            this.response.setStatus(status.value());
        }
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyHeaders() {
        getHeaders().forEach((headerName, headerValues) -> {
            Iterator it = headerValues.iterator();
            while (it.hasNext()) {
                String headerValue = (String) it.next();
                this.response.addHeader(headerName, headerValue);
            }
        });
        adaptHeaders(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void adaptHeaders(boolean removeAdaptedHeaders) {
        MediaType contentType = null;
        try {
            contentType = getHeaders().getContentType();
        } catch (Exception e) {
            String rawContentType = getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
            this.response.setContentType(rawContentType);
        }
        if (this.response.getContentType() == null && contentType != null) {
            this.response.setContentType(contentType.toString());
        }
        Charset charset = contentType != null ? contentType.getCharset() : null;
        if (this.response.getCharacterEncoding() == null && charset != null) {
            this.response.setCharacterEncoding(charset.name());
        }
        long contentLength = getHeaders().getContentLength();
        if (contentLength != -1) {
            this.response.setContentLengthLong(contentLength);
        }
        if (removeAdaptedHeaders) {
            getHeaders().remove(HttpHeaders.CONTENT_TYPE);
            getHeaders().remove(HttpHeaders.CONTENT_LENGTH);
        }
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyCookies() {
        for (String name : getCookies().keySet()) {
            for (ResponseCookie httpCookie : (List) getCookies().get(name)) {
                Cookie cookie = new Cookie(name, httpCookie.getValue());
                if (!httpCookie.getMaxAge().isNegative()) {
                    cookie.setMaxAge((int) httpCookie.getMaxAge().getSeconds());
                }
                if (httpCookie.getDomain() != null) {
                    cookie.setDomain(httpCookie.getDomain());
                }
                if (httpCookie.getPath() != null) {
                    cookie.setPath(httpCookie.getPath());
                }
                if (httpCookie.getSameSite() != null) {
                    cookie.setAttribute(Constants.COOKIE_SAME_SITE_ATTR, httpCookie.getSameSite());
                }
                cookie.setSecure(httpCookie.isSecure());
                cookie.setHttpOnly(httpCookie.isHttpOnly());
                this.response.addCookie(cookie);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AsyncListener getAsyncListener() {
        return this.asyncListener;
    }

    @Override // org.springframework.http.server.reactive.AbstractListenerServerHttpResponse
    protected Processor<? super Publisher<? extends DataBuffer>, Void> createBodyFlushProcessor() {
        ResponseBodyFlushProcessor processor = new ResponseBodyFlushProcessor();
        this.bodyFlushProcessor = processor;
        return processor;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ServletOutputStream getOutputStream() {
        return this.outputStream;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int writeToOutputStream(DataBuffer dataBuffer) throws IOException {
        int bytesRead;
        ServletOutputStream outputStream = this.outputStream;
        InputStream input = dataBuffer.asInputStream();
        int bytesWritten = 0;
        byte[] buffer = new byte[this.bufferSize];
        while (outputStream.isReady() && (bytesRead = input.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
            bytesWritten += bytesRead;
        }
        return bytesWritten;
    }

    private void flush() throws IOException {
        ServletOutputStream outputStream = this.outputStream;
        if (outputStream.isReady()) {
            try {
                outputStream.flush();
                this.flushOnNext = false;
                return;
            } catch (IOException ex) {
                this.flushOnNext = true;
                throw ex;
            }
        }
        this.flushOnNext = true;
    }

    private boolean isWritePossible() {
        return this.outputStream.isReady();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ServletServerHttpResponse$ResponseAsyncListener.class */
    public final class ResponseAsyncListener implements AsyncListener {
        private ResponseAsyncListener() {
        }

        @Override // jakarta.servlet.AsyncListener
        public void onStartAsync(AsyncEvent event) {
        }

        @Override // jakarta.servlet.AsyncListener
        public void onTimeout(AsyncEvent event) {
            Throwable ex = event.getThrowable();
            handleError(ex != null ? ex : new IllegalStateException("Async operation timeout."));
        }

        @Override // jakarta.servlet.AsyncListener
        public void onError(AsyncEvent event) {
            handleError(event.getThrowable());
        }

        public void handleError(Throwable ex) {
            ResponseBodyFlushProcessor flushProcessor = ServletServerHttpResponse.this.bodyFlushProcessor;
            ResponseBodyProcessor processor = ServletServerHttpResponse.this.bodyProcessor;
            if (flushProcessor != null) {
                flushProcessor.cancel();
                if (processor != null) {
                    processor.cancel();
                    processor.onError(ex);
                }
                flushProcessor.onError(ex);
            }
        }

        @Override // jakarta.servlet.AsyncListener
        public void onComplete(AsyncEvent event) {
            ResponseBodyFlushProcessor flushProcessor = ServletServerHttpResponse.this.bodyFlushProcessor;
            ResponseBodyProcessor processor = ServletServerHttpResponse.this.bodyProcessor;
            if (flushProcessor != null) {
                flushProcessor.cancel();
                if (processor != null) {
                    processor.cancel();
                    processor.onComplete();
                }
                flushProcessor.onComplete();
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ServletServerHttpResponse$ResponseBodyWriteListener.class */
    private class ResponseBodyWriteListener implements WriteListener {
        private ResponseBodyWriteListener() {
        }

        @Override // jakarta.servlet.WriteListener
        public void onWritePossible() {
            ResponseBodyProcessor processor = ServletServerHttpResponse.this.bodyProcessor;
            if (processor != null) {
                processor.onWritePossible();
                return;
            }
            ResponseBodyFlushProcessor flushProcessor = ServletServerHttpResponse.this.bodyFlushProcessor;
            if (flushProcessor != null) {
                flushProcessor.onFlushPossible();
            }
        }

        @Override // jakarta.servlet.WriteListener
        public void onError(Throwable ex) {
            ServletServerHttpResponse.this.asyncListener.handleError(ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ServletServerHttpResponse$ResponseBodyFlushProcessor.class */
    public class ResponseBodyFlushProcessor extends AbstractListenerWriteFlushProcessor<DataBuffer> {
        public ResponseBodyFlushProcessor() {
            super(ServletServerHttpResponse.this.request.getLogPrefix());
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor
        protected Processor<? super DataBuffer, Void> createWriteProcessor() {
            ResponseBodyProcessor processor = new ResponseBodyProcessor();
            ServletServerHttpResponse.this.bodyProcessor = processor;
            return processor;
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor
        protected void flush() throws IOException {
            if (rsWriteFlushLogger.isTraceEnabled()) {
                rsWriteFlushLogger.trace(getLogPrefix() + "flushing");
            }
            ServletServerHttpResponse.this.flush();
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor
        protected boolean isWritePossible() {
            return ServletServerHttpResponse.this.isWritePossible();
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor
        protected boolean isFlushPending() {
            return ServletServerHttpResponse.this.flushOnNext;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ServletServerHttpResponse$ResponseBodyProcessor.class */
    public class ResponseBodyProcessor extends AbstractListenerWriteProcessor<DataBuffer> {
        public ResponseBodyProcessor() {
            super(ServletServerHttpResponse.this.request.getLogPrefix());
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        protected boolean isWritePossible() {
            return ServletServerHttpResponse.this.isWritePossible();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        public boolean isDataEmpty(DataBuffer dataBuffer) {
            return dataBuffer.readableByteCount() == 0;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        public boolean write(DataBuffer dataBuffer) throws IOException {
            if (ServletServerHttpResponse.this.flushOnNext) {
                if (rsWriteLogger.isTraceEnabled()) {
                    rsWriteLogger.trace(getLogPrefix() + "flushing");
                }
                ServletServerHttpResponse.this.flush();
            }
            boolean ready = ServletServerHttpResponse.this.isWritePossible();
            int remaining = dataBuffer.readableByteCount();
            if (ready && remaining > 0) {
                int written = ServletServerHttpResponse.this.writeToOutputStream(dataBuffer);
                if (rsWriteLogger.isTraceEnabled()) {
                    rsWriteLogger.trace(getLogPrefix() + "Wrote " + written + " of " + remaining + " bytes");
                }
                if (written == remaining) {
                    DataBufferUtils.release(dataBuffer);
                    return true;
                }
                return false;
            }
            if (rsWriteLogger.isTraceEnabled()) {
                rsWriteLogger.trace(getLogPrefix() + "ready: " + ready + ", remaining: " + remaining);
                return false;
            }
            return false;
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        protected void writingComplete() {
            ServletServerHttpResponse.this.bodyProcessor = null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        public void discardData(DataBuffer dataBuffer) {
            DataBufferUtils.release(dataBuffer);
        }
    }
}
