package org.springframework.http.server.reactive;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.CookieImpl;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.xnio.ChannelListener;
import org.xnio.channels.StreamSinkChannel;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/UndertowServerHttpResponse.class */
class UndertowServerHttpResponse extends AbstractListenerServerHttpResponse implements ZeroCopyHttpOutputMessage {
    private final HttpServerExchange exchange;
    private final UndertowServerHttpRequest request;

    @Nullable
    private StreamSinkChannel responseChannel;

    /* JADX INFO: Access modifiers changed from: package-private */
    public UndertowServerHttpResponse(HttpServerExchange exchange, DataBufferFactory bufferFactory, UndertowServerHttpRequest request) {
        super(bufferFactory, createHeaders(exchange));
        this.exchange = exchange;
        this.request = request;
    }

    private static HttpHeaders createHeaders(HttpServerExchange exchange) {
        Assert.notNull(exchange, "HttpServerExchange must not be null");
        UndertowHeadersAdapter headersMap = new UndertowHeadersAdapter(exchange.getResponseHeaders());
        return new HttpHeaders(headersMap);
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    public <T> T getNativeResponse() {
        return (T) this.exchange;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse, org.springframework.http.server.reactive.ServerHttpResponse
    public HttpStatusCode getStatusCode() {
        HttpStatusCode status = super.getStatusCode();
        return status != null ? status : HttpStatusCode.valueOf(this.exchange.getStatusCode());
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse, org.springframework.http.server.reactive.ServerHttpResponse
    @Deprecated
    public Integer getRawStatusCode() {
        Integer status = super.getRawStatusCode();
        return Integer.valueOf(status != null ? status.intValue() : this.exchange.getStatusCode());
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyStatusCode() {
        HttpStatusCode status = super.getStatusCode();
        if (status != null) {
            this.exchange.setStatusCode(status.value());
        }
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyHeaders() {
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyCookies() {
        for (String name : getCookies().keySet()) {
            for (ResponseCookie httpCookie : (List) getCookies().get(name)) {
                CookieImpl cookieImpl = new CookieImpl(name, httpCookie.getValue());
                if (!httpCookie.getMaxAge().isNegative()) {
                    cookieImpl.setMaxAge(Integer.valueOf((int) httpCookie.getMaxAge().getSeconds()));
                }
                if (httpCookie.getDomain() != null) {
                    cookieImpl.setDomain(httpCookie.getDomain());
                }
                if (httpCookie.getPath() != null) {
                    cookieImpl.setPath(httpCookie.getPath());
                }
                cookieImpl.setSecure(httpCookie.isSecure());
                cookieImpl.setHttpOnly(httpCookie.isHttpOnly());
                cookieImpl.setSameSiteMode(httpCookie.getSameSite());
                this.exchange.setResponseCookie(cookieImpl);
            }
        }
    }

    @Override // org.springframework.http.ZeroCopyHttpOutputMessage
    public Mono<Void> writeWith(Path file, long position, long count) {
        return doCommit(() -> {
            return Mono.create(sink -> {
                try {
                    FileChannel source = FileChannel.open(file, StandardOpenOption.READ);
                    TransferBodyListener listener = new TransferBodyListener(source, position, count, sink);
                    Objects.requireNonNull(listener);
                    sink.onDispose(listener::closeSource);
                    StreamSinkChannel destination = this.exchange.getResponseChannel();
                    ChannelListener.Setter writeSetter = destination.getWriteSetter();
                    Objects.requireNonNull(listener);
                    writeSetter.set(listener::transfer);
                    listener.transfer(destination);
                } catch (IOException ex) {
                    sink.error(ex);
                }
            });
        });
    }

    @Override // org.springframework.http.server.reactive.AbstractListenerServerHttpResponse
    protected Processor<? super Publisher<? extends DataBuffer>, Void> createBodyFlushProcessor() {
        return new ResponseBodyFlushProcessor();
    }

    private ResponseBodyProcessor createBodyProcessor() {
        if (this.responseChannel == null) {
            this.responseChannel = this.exchange.getResponseChannel();
        }
        return new ResponseBodyProcessor(this.responseChannel);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/UndertowServerHttpResponse$ResponseBodyProcessor.class */
    public class ResponseBodyProcessor extends AbstractListenerWriteProcessor<DataBuffer> {
        private final StreamSinkChannel channel;

        @Nullable
        private volatile ByteBuffer byteBuffer;
        private volatile boolean writePossible;

        public ResponseBodyProcessor(StreamSinkChannel channel) {
            super(UndertowServerHttpResponse.this.request.getLogPrefix());
            Assert.notNull(channel, "StreamSinkChannel must not be null");
            this.channel = channel;
            this.channel.getWriteSetter().set(c -> {
                this.writePossible = true;
                onWritePossible();
            });
            this.channel.suspendWrites();
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        protected boolean isWritePossible() {
            this.channel.resumeWrites();
            return this.writePossible;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        public boolean write(DataBuffer dataBuffer) throws IOException {
            ByteBuffer buffer = this.byteBuffer;
            if (buffer == null) {
                return false;
            }
            this.writePossible = false;
            int total = buffer.remaining();
            int written = writeByteBuffer(buffer);
            if (rsWriteLogger.isTraceEnabled()) {
                rsWriteLogger.trace(getLogPrefix() + "Wrote " + written + " of " + total + " bytes");
            }
            if (written != total) {
                return false;
            }
            this.writePossible = true;
            DataBufferUtils.release(dataBuffer);
            this.byteBuffer = null;
            return true;
        }

        private int writeByteBuffer(ByteBuffer byteBuffer) throws IOException {
            int written;
            int totalWritten = 0;
            do {
                written = this.channel.write(byteBuffer);
                totalWritten += written;
                if (!byteBuffer.hasRemaining()) {
                    break;
                }
            } while (written > 0);
            return totalWritten;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        public void dataReceived(DataBuffer dataBuffer) {
            super.dataReceived((ResponseBodyProcessor) dataBuffer);
            ByteBuffer byteBuffer = ByteBuffer.allocate(dataBuffer.readableByteCount());
            dataBuffer.toByteBuffer(byteBuffer);
            this.byteBuffer = byteBuffer;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        public boolean isDataEmpty(DataBuffer dataBuffer) {
            return dataBuffer.readableByteCount() == 0;
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        protected void writingComplete() {
            this.channel.getWriteSetter().set((ChannelListener) null);
            this.channel.resumeWrites();
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        protected void writingFailed(Throwable ex) {
            cancel();
            onError(ex);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        public void discardData(DataBuffer dataBuffer) {
            DataBufferUtils.release(dataBuffer);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/UndertowServerHttpResponse$ResponseBodyFlushProcessor.class */
    private class ResponseBodyFlushProcessor extends AbstractListenerWriteFlushProcessor<DataBuffer> {
        public ResponseBodyFlushProcessor() {
            super(UndertowServerHttpResponse.this.request.getLogPrefix());
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor
        protected Processor<? super DataBuffer, Void> createWriteProcessor() {
            return UndertowServerHttpResponse.this.createBodyProcessor();
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor
        protected void flush() throws IOException {
            StreamSinkChannel channel = UndertowServerHttpResponse.this.responseChannel;
            if (channel != null) {
                if (rsWriteFlushLogger.isTraceEnabled()) {
                    rsWriteFlushLogger.trace(getLogPrefix() + "flush");
                }
                channel.flush();
            }
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor
        protected boolean isWritePossible() {
            StreamSinkChannel channel = UndertowServerHttpResponse.this.responseChannel;
            if (channel != null) {
                channel.resumeWrites();
                return true;
            }
            return false;
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor
        protected boolean isFlushPending() {
            return false;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/UndertowServerHttpResponse$TransferBodyListener.class */
    private static class TransferBodyListener {
        private final FileChannel source;
        private final MonoSink<Void> sink;
        private long position;
        private long count;

        public TransferBodyListener(FileChannel source, long position, long count, MonoSink<Void> sink) {
            this.source = source;
            this.sink = sink;
            this.position = position;
            this.count = count;
        }

        public void transfer(StreamSinkChannel destination) {
            while (this.count > 0) {
                try {
                    long len = destination.transferFrom(this.source, this.position, this.count);
                    if (len != 0) {
                        this.position += len;
                        this.count -= len;
                    } else {
                        destination.resumeWrites();
                        return;
                    }
                } catch (IOException ex) {
                    this.sink.error(ex);
                    return;
                }
            }
            this.sink.success();
        }

        public void closeSource() {
            try {
                this.source.close();
            } catch (IOException e) {
            }
        }
    }
}
