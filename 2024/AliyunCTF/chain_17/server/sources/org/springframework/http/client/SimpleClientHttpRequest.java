package org.springframework.http.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/SimpleClientHttpRequest.class */
final class SimpleClientHttpRequest extends AbstractStreamingClientHttpRequest {
    private final HttpURLConnection connection;
    private final int chunkSize;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleClientHttpRequest(HttpURLConnection connection, int chunkSize) {
        this.connection = connection;
        this.chunkSize = chunkSize;
    }

    @Override // org.springframework.http.HttpRequest
    public HttpMethod getMethod() {
        return HttpMethod.valueOf(this.connection.getRequestMethod());
    }

    @Override // org.springframework.http.HttpRequest
    public URI getURI() {
        try {
            return this.connection.getURL().toURI();
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Could not get HttpURLConnection URI: " + ex.getMessage(), ex);
        }
    }

    @Override // org.springframework.http.client.AbstractStreamingClientHttpRequest
    protected ClientHttpResponse executeInternal(HttpHeaders headers, @Nullable StreamingHttpOutputMessage.Body body) throws IOException {
        if (this.connection.getDoOutput()) {
            long contentLength = headers.getContentLength();
            if (contentLength >= 0) {
                this.connection.setFixedLengthStreamingMode(contentLength);
            } else {
                this.connection.setChunkedStreamingMode(this.chunkSize);
            }
        }
        addHeaders(this.connection, headers);
        this.connection.connect();
        if (this.connection.getDoOutput() && body != null) {
            OutputStream os = this.connection.getOutputStream();
            try {
                body.writeTo(os);
                if (os != null) {
                    os.close();
                }
            } catch (Throwable th) {
                if (os != null) {
                    try {
                        os.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } else {
            this.connection.getResponseCode();
        }
        return new SimpleClientHttpResponse(this.connection);
    }

    static void addHeaders(HttpURLConnection connection, HttpHeaders headers) {
        String method = connection.getRequestMethod();
        if ((method.equals("PUT") || method.equals("DELETE")) && !StringUtils.hasText(headers.getFirst(HttpHeaders.ACCEPT))) {
            headers.set(HttpHeaders.ACCEPT, "*/*");
        }
        headers.forEach((headerName, headerValues) -> {
            if (HttpHeaders.COOKIE.equalsIgnoreCase(headerName)) {
                connection.setRequestProperty(headerName, StringUtils.collectionToDelimitedString(headerValues, "; "));
                return;
            }
            Iterator it = headerValues.iterator();
            while (it.hasNext()) {
                String headerValue = (String) it.next();
                String actualHeaderValue = headerValue != null ? headerValue : "";
                connection.addRequestProperty(headerName, actualHeaderValue);
            }
        });
    }
}
