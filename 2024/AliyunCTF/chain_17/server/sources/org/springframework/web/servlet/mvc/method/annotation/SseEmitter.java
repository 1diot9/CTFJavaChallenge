package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/SseEmitter.class */
public class SseEmitter extends ResponseBodyEmitter {
    private static final MediaType TEXT_PLAIN = new MediaType("text", "plain", StandardCharsets.UTF_8);
    private final Lock writeLock;

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/SseEmitter$SseEventBuilder.class */
    public interface SseEventBuilder {
        SseEventBuilder id(String id);

        SseEventBuilder name(String eventName);

        SseEventBuilder reconnectTime(long reconnectTimeMillis);

        SseEventBuilder comment(String comment);

        SseEventBuilder data(Object object);

        SseEventBuilder data(Object object, @Nullable MediaType mediaType);

        Set<ResponseBodyEmitter.DataWithMediaType> build();
    }

    public SseEmitter() {
        this.writeLock = new ReentrantLock();
    }

    public SseEmitter(Long timeout) {
        super(timeout);
        this.writeLock = new ReentrantLock();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter
    public void extendResponse(ServerHttpResponse outputMessage) {
        super.extendResponse(outputMessage);
        HttpHeaders headers = outputMessage.getHeaders();
        if (headers.getContentType() == null) {
            headers.setContentType(MediaType.TEXT_EVENT_STREAM);
        }
    }

    @Override // org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter
    public void send(Object object) throws IOException {
        send(object, null);
    }

    @Override // org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter
    public void send(Object object, @Nullable MediaType mediaType) throws IOException {
        send(event().data(object, mediaType));
    }

    public void send(SseEventBuilder builder) throws IOException {
        Set<ResponseBodyEmitter.DataWithMediaType> dataToSend = builder.build();
        this.writeLock.lock();
        try {
            super.send(dataToSend);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override // org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter
    public String toString() {
        return "SseEmitter@" + ObjectUtils.getIdentityHexString(this);
    }

    public static SseEventBuilder event() {
        return new SseEventBuilderImpl();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/SseEmitter$SseEventBuilderImpl.class */
    public static class SseEventBuilderImpl implements SseEventBuilder {
        private final Set<ResponseBodyEmitter.DataWithMediaType> dataToSend = new LinkedHashSet(4);

        @Nullable
        private StringBuilder sb;

        private SseEventBuilderImpl() {
        }

        @Override // org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder
        public SseEventBuilder id(String id) {
            append("id:").append(id).append('\n');
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder
        public SseEventBuilder name(String name) {
            append("event:").append(name).append('\n');
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder
        public SseEventBuilder reconnectTime(long reconnectTimeMillis) {
            append("retry:").append(String.valueOf(reconnectTimeMillis)).append('\n');
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder
        public SseEventBuilder comment(String comment) {
            append(':').append(comment).append('\n');
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder
        public SseEventBuilder data(Object object) {
            return data(object, null);
        }

        @Override // org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder
        public SseEventBuilder data(Object object, @Nullable MediaType mediaType) {
            append("data:");
            saveAppendedText();
            if (object instanceof String) {
                String text = (String) object;
                object = StringUtils.replace(text, "\n", "\ndata:");
            }
            this.dataToSend.add(new ResponseBodyEmitter.DataWithMediaType(object, mediaType));
            append('\n');
            return this;
        }

        SseEventBuilderImpl append(String text) {
            if (this.sb == null) {
                this.sb = new StringBuilder();
            }
            this.sb.append(text);
            return this;
        }

        SseEventBuilderImpl append(char ch2) {
            if (this.sb == null) {
                this.sb = new StringBuilder();
            }
            this.sb.append(ch2);
            return this;
        }

        @Override // org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder
        public Set<ResponseBodyEmitter.DataWithMediaType> build() {
            if (!StringUtils.hasLength(this.sb) && this.dataToSend.isEmpty()) {
                return Collections.emptySet();
            }
            append('\n');
            saveAppendedText();
            return this.dataToSend;
        }

        private void saveAppendedText() {
            if (this.sb != null) {
                this.dataToSend.add(new ResponseBodyEmitter.DataWithMediaType(this.sb.toString(), SseEmitter.TEXT_PLAIN));
                this.sb = null;
            }
        }
    }
}
