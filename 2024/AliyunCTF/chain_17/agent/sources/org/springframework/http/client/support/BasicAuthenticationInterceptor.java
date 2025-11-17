package org.springframework.http.client.support;

import java.io.IOException;
import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/support/BasicAuthenticationInterceptor.class */
public class BasicAuthenticationInterceptor implements ClientHttpRequestInterceptor {
    private final String encodedCredentials;

    public BasicAuthenticationInterceptor(String username, String password) {
        this(username, password, null);
    }

    public BasicAuthenticationInterceptor(String username, String password, @Nullable Charset charset) {
        this.encodedCredentials = HttpHeaders.encodeBasicAuth(username, password, charset);
    }

    @Override // org.springframework.http.client.ClientHttpRequestInterceptor
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        if (!headers.containsKey("Authorization")) {
            headers.setBasicAuth(this.encodedCredentials);
        }
        return execution.execute(request, body);
    }
}
