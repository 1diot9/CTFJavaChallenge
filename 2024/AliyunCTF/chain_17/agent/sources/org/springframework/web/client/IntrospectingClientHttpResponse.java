package org.springframework.web.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/IntrospectingClientHttpResponse.class */
public class IntrospectingClientHttpResponse extends ClientHttpResponseDecorator {

    @Nullable
    private PushbackInputStream pushbackInputStream;

    public IntrospectingClientHttpResponse(ClientHttpResponse response) {
        super(response);
    }

    public boolean hasMessageBody() throws IOException {
        HttpStatusCode statusCode = getStatusCode();
        if (statusCode.is1xxInformational() || statusCode == HttpStatus.NO_CONTENT || statusCode == HttpStatus.NOT_MODIFIED || getHeaders().getContentLength() == 0) {
            return false;
        }
        return true;
    }

    public boolean hasEmptyMessageBody() throws IOException {
        InputStream body = getDelegate().getBody();
        if (body == null) {
            return true;
        }
        if (body.markSupported()) {
            body.mark(1);
            if (body.read() == -1) {
                return true;
            }
            body.reset();
            return false;
        }
        this.pushbackInputStream = new PushbackInputStream(body);
        int b = this.pushbackInputStream.read();
        if (b == -1) {
            return true;
        }
        this.pushbackInputStream.unread(b);
        return false;
    }

    @Override // org.springframework.web.client.ClientHttpResponseDecorator, org.springframework.http.HttpInputMessage
    public InputStream getBody() throws IOException {
        return this.pushbackInputStream != null ? this.pushbackInputStream : getDelegate().getBody();
    }
}
