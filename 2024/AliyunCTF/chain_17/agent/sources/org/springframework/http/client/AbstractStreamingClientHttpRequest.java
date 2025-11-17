package org.springframework.http.client;

import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.FastByteArrayOutputStream;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/AbstractStreamingClientHttpRequest.class */
abstract class AbstractStreamingClientHttpRequest extends AbstractClientHttpRequest implements StreamingHttpOutputMessage {

    @Nullable
    private StreamingHttpOutputMessage.Body body;

    @Nullable
    private FastByteArrayOutputStream bodyStream;

    protected abstract ClientHttpResponse executeInternal(HttpHeaders headers, @Nullable StreamingHttpOutputMessage.Body body) throws IOException;

    @Override // org.springframework.http.client.AbstractClientHttpRequest
    protected final OutputStream getBodyInternal(HttpHeaders headers) {
        Assert.state(this.body == null, "Invoke either getBody or setBody; not both");
        if (this.bodyStream == null) {
            this.bodyStream = new FastByteArrayOutputStream(1024);
        }
        return this.bodyStream;
    }

    @Override // org.springframework.http.StreamingHttpOutputMessage
    public final void setBody(StreamingHttpOutputMessage.Body body) {
        Assert.notNull(body, "Body must not be null");
        assertNotExecuted();
        Assert.state(this.bodyStream == null, "Invoke either getBody or setBody; not both");
        this.body = body;
    }

    @Override // org.springframework.http.client.AbstractClientHttpRequest
    protected final ClientHttpResponse executeInternal(HttpHeaders headers) throws IOException {
        if (this.body == null && this.bodyStream != null) {
            this.body = outputStream -> {
                this.bodyStream.writeTo(outputStream);
            };
        }
        return executeInternal(headers, this.body);
    }
}
