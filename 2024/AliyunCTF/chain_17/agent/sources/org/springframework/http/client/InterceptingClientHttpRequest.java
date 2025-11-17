package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.util.StreamUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/InterceptingClientHttpRequest.class */
public class InterceptingClientHttpRequest extends AbstractBufferingClientHttpRequest {
    private final ClientHttpRequestFactory requestFactory;
    private final List<ClientHttpRequestInterceptor> interceptors;
    private final HttpMethod method;
    private final URI uri;

    /* JADX INFO: Access modifiers changed from: protected */
    public InterceptingClientHttpRequest(ClientHttpRequestFactory requestFactory, List<ClientHttpRequestInterceptor> interceptors, URI uri, HttpMethod method) {
        this.requestFactory = requestFactory;
        this.interceptors = interceptors;
        this.method = method;
        this.uri = uri;
    }

    @Override // org.springframework.http.HttpRequest
    public HttpMethod getMethod() {
        return this.method;
    }

    @Override // org.springframework.http.HttpRequest
    public URI getURI() {
        return this.uri;
    }

    @Override // org.springframework.http.client.AbstractBufferingClientHttpRequest
    protected final ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
        InterceptingRequestExecution requestExecution = new InterceptingRequestExecution();
        return requestExecution.execute(this, bufferedOutput);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/InterceptingClientHttpRequest$InterceptingRequestExecution.class */
    private class InterceptingRequestExecution implements ClientHttpRequestExecution {
        private final Iterator<ClientHttpRequestInterceptor> iterator;

        public InterceptingRequestExecution() {
            this.iterator = InterceptingClientHttpRequest.this.interceptors.iterator();
        }

        @Override // org.springframework.http.client.ClientHttpRequestExecution
        public ClientHttpResponse execute(HttpRequest request, byte[] body) throws IOException {
            if (this.iterator.hasNext()) {
                ClientHttpRequestInterceptor nextInterceptor = this.iterator.next();
                return nextInterceptor.intercept(request, body, this);
            }
            HttpMethod method = request.getMethod();
            ClientHttpRequest delegate = InterceptingClientHttpRequest.this.requestFactory.createRequest(request.getURI(), method);
            request.getHeaders().forEach((key, value) -> {
                delegate.getHeaders().addAll(key, (List<? extends String>) value);
            });
            if (body.length > 0) {
                if (delegate instanceof StreamingHttpOutputMessage) {
                    StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) delegate;
                    streamingOutputMessage.setBody(outputStream -> {
                        StreamUtils.copy(body, outputStream);
                    });
                } else {
                    StreamUtils.copy(body, delegate.getBody());
                }
            }
            return delegate.execute();
        }
    }
}
