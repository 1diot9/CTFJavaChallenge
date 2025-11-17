package org.springframework.http.client;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.client.InputStreamResponseListener;
import org.eclipse.jetty.client.OutputStreamRequestContent;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/JettyClientHttpRequest.class */
class JettyClientHttpRequest extends AbstractStreamingClientHttpRequest {
    private static final int CHUNK_SIZE = 1024;
    private final Request request;
    private final long readTimeout;

    public JettyClientHttpRequest(Request request, long readTimeout) {
        this.request = request;
        this.readTimeout = readTimeout;
    }

    @Override // org.springframework.http.HttpRequest
    public HttpMethod getMethod() {
        return HttpMethod.valueOf(this.request.getMethod());
    }

    @Override // org.springframework.http.HttpRequest
    public URI getURI() {
        return this.request.getURI();
    }

    @Override // org.springframework.http.client.AbstractStreamingClientHttpRequest
    protected ClientHttpResponse executeInternal(HttpHeaders headers, @Nullable StreamingHttpOutputMessage.Body body) throws IOException {
        if (!headers.isEmpty()) {
            this.request.headers(httpFields -> {
                headers.forEach((headerName, headerValues) -> {
                    Iterator it = headerValues.iterator();
                    while (it.hasNext()) {
                        String headerValue = (String) it.next();
                        httpFields.add(headerName, headerValue);
                    }
                });
            });
        }
        String contentType = null;
        if (headers.getContentType() != null) {
            contentType = headers.getContentType().toString();
        }
        try {
            InputStreamResponseListener responseListener = new InputStreamResponseListener();
            if (body != null) {
                OutputStreamRequestContent requestContent = new OutputStreamRequestContent(contentType);
                this.request.body(requestContent).send(responseListener);
                OutputStream outputStream = new BufferedOutputStream(requestContent.getOutputStream(), 1024);
                try {
                    body.writeTo(StreamUtils.nonClosing(outputStream));
                    outputStream.close();
                } catch (Throwable th) {
                    try {
                        outputStream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } else {
                this.request.send(responseListener);
            }
            Response response = responseListener.get(this.readTimeout, TimeUnit.MILLISECONDS);
            return new JettyClientHttpResponse(response, responseListener.getInputStream());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IOException("Request was interrupted: " + ex.getMessage(), ex);
        } catch (ExecutionException ex2) {
            Throwable cause = ex2.getCause();
            if (cause instanceof UncheckedIOException) {
                UncheckedIOException uioEx = (UncheckedIOException) cause;
                throw uioEx.getCause();
            }
            if (cause instanceof RuntimeException) {
                RuntimeException rtEx = (RuntimeException) cause;
                throw rtEx;
            }
            if (cause instanceof IOException) {
                IOException ioEx = (IOException) cause;
                throw ioEx;
            }
            throw new IOException(cause.getMessage(), cause);
        } catch (TimeoutException ex3) {
            throw new IOException("Request timed out: " + ex3.getMessage(), ex3);
        }
    }
}
