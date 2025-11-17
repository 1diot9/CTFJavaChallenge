package org.springframework.http.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.support.WebContentGenerator;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/SimpleClientHttpRequestFactory.class */
public class SimpleClientHttpRequestFactory implements ClientHttpRequestFactory {
    private static final int DEFAULT_CHUNK_SIZE = 4096;

    @Nullable
    private Proxy proxy;
    private int chunkSize = 4096;
    private int connectTimeout = -1;
    private int readTimeout = -1;

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    @Deprecated(since = "6.1", forRemoval = true)
    public void setBufferRequestBody(boolean bufferRequestBody) {
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        Assert.notNull(connectTimeout, "ConnectTimeout must not be null");
        this.connectTimeout = (int) connectTimeout.toMillis();
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        Assert.notNull(readTimeout, "ReadTimeout must not be null");
        this.readTimeout = (int) readTimeout.toMillis();
    }

    @Deprecated(since = "6.1", forRemoval = true)
    public void setOutputStreaming(boolean outputStreaming) {
    }

    @Override // org.springframework.http.client.ClientHttpRequestFactory
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        HttpURLConnection connection = openConnection(uri.toURL(), this.proxy);
        prepareConnection(connection, httpMethod.name());
        return new SimpleClientHttpRequest(connection, this.chunkSize);
    }

    protected HttpURLConnection openConnection(URL url, @Nullable Proxy proxy) throws IOException {
        URLConnection urlConnection = proxy != null ? url.openConnection(proxy) : url.openConnection();
        if (!(urlConnection instanceof HttpURLConnection)) {
            throw new IllegalStateException("HttpURLConnection required for [" + url + "] but got: " + urlConnection);
        }
        HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
        return httpUrlConnection;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
        if (this.connectTimeout >= 0) {
            connection.setConnectTimeout(this.connectTimeout);
        }
        if (this.readTimeout >= 0) {
            connection.setReadTimeout(this.readTimeout);
        }
        boolean mayWrite = WebContentGenerator.METHOD_POST.equals(httpMethod) || "PUT".equals(httpMethod) || "PATCH".equals(httpMethod) || "DELETE".equals(httpMethod);
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects("GET".equals(httpMethod));
        connection.setDoOutput(mayWrite);
        connection.setRequestMethod(httpMethod);
    }
}
