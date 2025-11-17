package org.springframework.http.server.reactive;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.eclipse.jetty.ee10.servlet.HttpOutput;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/JettyHttpHandlerAdapter.class */
public class JettyHttpHandlerAdapter extends ServletHttpHandlerAdapter {
    public JettyHttpHandlerAdapter(HttpHandler httpHandler) {
        super(httpHandler);
    }

    @Override // org.springframework.http.server.reactive.ServletHttpHandlerAdapter
    protected ServletServerHttpResponse createResponse(HttpServletResponse response, AsyncContext context, ServletServerHttpRequest request) throws IOException {
        return new Jetty12ServerHttpResponse(response, context, getDataBufferFactory(), getBufferSize(), request);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/JettyHttpHandlerAdapter$Jetty12ServerHttpResponse.class */
    private static final class Jetty12ServerHttpResponse extends ServletServerHttpResponse {
        Jetty12ServerHttpResponse(HttpServletResponse response, AsyncContext asyncContext, DataBufferFactory bufferFactory, int bufferSize, ServletServerHttpRequest request) throws IOException {
            super(response, asyncContext, bufferFactory, bufferSize, request);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.http.server.reactive.ServletServerHttpResponse
        public int writeToOutputStream(DataBuffer dataBuffer) throws IOException {
            HttpOutput outputStream = getOutputStream();
            if (outputStream instanceof HttpOutput) {
                HttpOutput httpOutput = outputStream;
                int len = 0;
                DataBuffer.ByteBufferIterator iterator = dataBuffer.readableByteBuffers();
                while (iterator.hasNext() && httpOutput.isReady()) {
                    try {
                        ByteBuffer byteBuffer = iterator.next();
                        len += byteBuffer.remaining();
                        httpOutput.write(byteBuffer);
                    } catch (Throwable th) {
                        if (iterator != null) {
                            try {
                                iterator.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                }
                if (iterator != null) {
                    iterator.close();
                }
                return len;
            }
            return super.writeToOutputStream(dataBuffer);
        }
    }
}
