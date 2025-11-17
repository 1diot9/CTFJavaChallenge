package org.springframework.web.servlet.function;

import cn.hutool.core.text.StrPool;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.DelegatingServerHttpResponse;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.ServerResponse;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/SseServerResponse.class */
final class SseServerResponse extends AbstractServerResponse {
    private final Consumer<ServerResponse.SseBuilder> sseConsumer;

    @Nullable
    private final Duration timeout;

    private SseServerResponse(Consumer<ServerResponse.SseBuilder> sseConsumer, @Nullable Duration timeout) {
        super(HttpStatus.OK, createHeaders(), emptyCookies());
        this.sseConsumer = sseConsumer;
        this.timeout = timeout;
    }

    private static HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_EVENT_STREAM);
        headers.setCacheControl(CacheControl.noCache());
        return headers;
    }

    private static MultiValueMap<String, Cookie> emptyCookies() {
        return CollectionUtils.toMultiValueMap(Collections.emptyMap());
    }

    @Override // org.springframework.web.servlet.function.AbstractServerResponse
    @Nullable
    protected ModelAndView writeToInternal(HttpServletRequest request, HttpServletResponse response, ServerResponse.Context context) throws ServletException, IOException {
        DeferredResult<?> result;
        if (this.timeout != null) {
            result = new DeferredResult<>(Long.valueOf(this.timeout.toMillis()));
        } else {
            result = new DeferredResult<>();
        }
        DefaultAsyncServerResponse.writeAsync(request, response, result);
        this.sseConsumer.accept(new DefaultSseBuilder(response, context, result, headers()));
        return null;
    }

    public static ServerResponse create(Consumer<ServerResponse.SseBuilder> sseConsumer, @Nullable Duration timeout) {
        Assert.notNull(sseConsumer, "SseConsumer must not be null");
        return new SseServerResponse(sseConsumer, timeout);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/SseServerResponse$DefaultSseBuilder.class */
    private static final class DefaultSseBuilder implements ServerResponse.SseBuilder {
        private static final byte[] NL_NL = {10, 10};
        private final ServerHttpResponse outputMessage;
        private final DeferredResult<?> deferredResult;
        private final List<HttpMessageConverter<?>> messageConverters;
        private final HttpHeaders httpHeaders;
        private final StringBuilder builder = new StringBuilder();
        private boolean sendFailed;

        public DefaultSseBuilder(HttpServletResponse response, ServerResponse.Context context, DeferredResult<?> deferredResult, HttpHeaders httpHeaders) {
            this.outputMessage = new ServletServerHttpResponse(response);
            this.deferredResult = deferredResult;
            this.messageConverters = context.messageConverters();
            this.httpHeaders = httpHeaders;
        }

        @Override // org.springframework.web.servlet.function.ServerResponse.SseBuilder
        public void send(Object object) throws IOException {
            data(object);
        }

        @Override // org.springframework.web.servlet.function.ServerResponse.SseBuilder
        public ServerResponse.SseBuilder id(String id) {
            Assert.hasLength(id, "Id must not be empty");
            return field("id", id);
        }

        @Override // org.springframework.web.servlet.function.ServerResponse.SseBuilder
        public ServerResponse.SseBuilder event(String eventName) {
            Assert.hasLength(eventName, "Name must not be empty");
            return field("event", eventName);
        }

        @Override // org.springframework.web.servlet.function.ServerResponse.SseBuilder
        public ServerResponse.SseBuilder retry(Duration duration) {
            Assert.notNull(duration, "Duration must not be null");
            String millis = Long.toString(duration.toMillis());
            return field("retry", millis);
        }

        @Override // org.springframework.web.servlet.function.ServerResponse.SseBuilder
        public ServerResponse.SseBuilder comment(String comment) {
            Assert.hasLength(comment, "Comment must not be empty");
            String[] lines = comment.split(StrPool.LF);
            for (String line : lines) {
                field("", line);
            }
            return this;
        }

        private ServerResponse.SseBuilder field(String name, String value) {
            this.builder.append(name).append(':').append(value).append('\n');
            return this;
        }

        @Override // org.springframework.web.servlet.function.ServerResponse.SseBuilder
        public void data(Object object) throws IOException {
            Assert.notNull(object, "Object must not be null");
            if (object instanceof String) {
                String text = (String) object;
                writeString(text);
            } else {
                writeObject(object);
            }
        }

        private void writeString(String string) throws IOException {
            String[] lines = string.split(StrPool.LF);
            for (String line : lines) {
                field("data", line);
            }
            this.builder.append('\n');
            try {
                try {
                    OutputStream body = this.outputMessage.getBody();
                    body.write(builderBytes());
                    body.flush();
                    this.builder.setLength(0);
                } catch (IOException ex) {
                    this.sendFailed = true;
                    throw ex;
                }
            } catch (Throwable th) {
                this.builder.setLength(0);
                throw th;
            }
        }

        private void writeObject(Object data) throws IOException {
            this.builder.append("data:");
            try {
                try {
                    this.outputMessage.getBody().write(builderBytes());
                    Class<?> dataClass = data.getClass();
                    for (HttpMessageConverter<?> converter : this.messageConverters) {
                        if (converter.canWrite(dataClass, MediaType.APPLICATION_JSON)) {
                            ServerHttpResponse response = new MutableHeadersServerHttpResponse(this.outputMessage, this.httpHeaders);
                            converter.write(data, MediaType.APPLICATION_JSON, response);
                            this.outputMessage.getBody().write(NL_NL);
                            this.outputMessage.flush();
                            this.builder.setLength(0);
                            return;
                        }
                    }
                } catch (IOException ex) {
                    this.sendFailed = true;
                    throw ex;
                }
            } finally {
                this.builder.setLength(0);
            }
        }

        private byte[] builderBytes() {
            return this.builder.toString().getBytes(StandardCharsets.UTF_8);
        }

        @Override // org.springframework.web.servlet.function.ServerResponse.SseBuilder
        public void error(Throwable t) {
            if (this.sendFailed) {
                return;
            }
            this.deferredResult.setErrorResult(t);
        }

        @Override // org.springframework.web.servlet.function.ServerResponse.SseBuilder
        public void complete() {
            if (this.sendFailed) {
                return;
            }
            try {
                this.outputMessage.flush();
                this.deferredResult.setResult(null);
            } catch (IOException ex) {
                this.deferredResult.setErrorResult(ex);
            }
        }

        @Override // org.springframework.web.servlet.function.ServerResponse.SseBuilder
        public ServerResponse.SseBuilder onTimeout(Runnable onTimeout) {
            this.deferredResult.onTimeout(onTimeout);
            return this;
        }

        @Override // org.springframework.web.servlet.function.ServerResponse.SseBuilder
        public ServerResponse.SseBuilder onError(Consumer<Throwable> onError) {
            this.deferredResult.onError(onError);
            return this;
        }

        @Override // org.springframework.web.servlet.function.ServerResponse.SseBuilder
        public ServerResponse.SseBuilder onComplete(Runnable onCompletion) {
            this.deferredResult.onCompletion(onCompletion);
            return this;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/SseServerResponse$DefaultSseBuilder$MutableHeadersServerHttpResponse.class */
        public static final class MutableHeadersServerHttpResponse extends DelegatingServerHttpResponse {
            private final HttpHeaders mutableHeaders;

            public MutableHeadersServerHttpResponse(ServerHttpResponse delegate, HttpHeaders headers) {
                super(delegate);
                this.mutableHeaders = new HttpHeaders();
                this.mutableHeaders.putAll(delegate.getHeaders());
                this.mutableHeaders.putAll(headers);
            }

            @Override // org.springframework.http.server.DelegatingServerHttpResponse, org.springframework.http.HttpMessage
            public HttpHeaders getHeaders() {
                return this.mutableHeaders;
            }
        }
    }
}
