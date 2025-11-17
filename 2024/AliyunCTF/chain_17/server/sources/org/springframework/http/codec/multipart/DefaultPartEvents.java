package org.springframework.http.codec.multipart;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/DefaultPartEvents.class */
public abstract class DefaultPartEvents {
    DefaultPartEvents() {
    }

    public static FormPartEvent form(HttpHeaders headers) {
        Assert.notNull(headers, "Headers must not be null");
        return new DefaultFormFieldPartEvent(headers);
    }

    public static FormPartEvent form(HttpHeaders headers, String value) {
        Assert.notNull(headers, "Headers must not be null");
        Assert.notNull(value, "Value must not be null");
        return new DefaultFormFieldPartEvent(headers, value);
    }

    public static FilePartEvent file(HttpHeaders headers, DataBuffer dataBuffer, boolean isLast) {
        Assert.notNull(headers, "Headers must not be null");
        Assert.notNull(dataBuffer, "DataBuffer must not be null");
        return new DefaultFilePartEvent(headers, dataBuffer, isLast);
    }

    public static FilePartEvent file(HttpHeaders headers) {
        Assert.notNull(headers, "Headers must not be null");
        return new DefaultFilePartEvent(headers);
    }

    public static PartEvent create(HttpHeaders headers, DataBuffer dataBuffer, boolean isLast) {
        Assert.notNull(headers, "Headers must not be null");
        Assert.notNull(dataBuffer, "DataBuffer must not be null");
        if (headers.getContentDisposition().getFilename() != null) {
            return file(headers, dataBuffer, isLast);
        }
        return new DefaultPartEvent(headers, dataBuffer, isLast);
    }

    public static PartEvent create(HttpHeaders headers) {
        Assert.notNull(headers, "Headers must not be null");
        if (headers.getContentDisposition().getFilename() != null) {
            return file(headers);
        }
        return new DefaultPartEvent(headers);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/DefaultPartEvents$AbstractPartEvent.class */
    private static abstract class AbstractPartEvent implements PartEvent {
        private final HttpHeaders headers;

        protected AbstractPartEvent(HttpHeaders headers) {
            this.headers = HttpHeaders.readOnlyHttpHeaders(headers);
        }

        @Override // org.springframework.http.codec.multipart.PartEvent
        public HttpHeaders headers() {
            return this.headers;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/DefaultPartEvents$DefaultPartEvent.class */
    private static class DefaultPartEvent extends AbstractPartEvent {
        private static final DataBuffer EMPTY = DefaultDataBufferFactory.sharedInstance.allocateBuffer(0);
        private final DataBuffer content;
        private final boolean last;

        public DefaultPartEvent(HttpHeaders headers) {
            this(headers, EMPTY, true);
        }

        public DefaultPartEvent(HttpHeaders headers, DataBuffer content, boolean last) {
            super(headers);
            this.content = content;
            this.last = last;
        }

        @Override // org.springframework.http.codec.multipart.PartEvent
        public DataBuffer content() {
            return this.content;
        }

        @Override // org.springframework.http.codec.multipart.PartEvent
        public boolean isLast() {
            return this.last;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/DefaultPartEvents$DefaultFormFieldPartEvent.class */
    public static final class DefaultFormFieldPartEvent extends AbstractPartEvent implements FormPartEvent {
        private static final String EMPTY = "";
        private final String value;

        public DefaultFormFieldPartEvent(HttpHeaders headers) {
            this(headers, "");
        }

        public DefaultFormFieldPartEvent(HttpHeaders headers, String value) {
            super(headers);
            this.value = value;
        }

        @Override // org.springframework.http.codec.multipart.FormPartEvent
        public String value() {
            return this.value;
        }

        @Override // org.springframework.http.codec.multipart.PartEvent
        public DataBuffer content() {
            byte[] bytes = this.value.getBytes(MultipartUtils.charset(headers()));
            return DefaultDataBufferFactory.sharedInstance.wrap(bytes);
        }

        @Override // org.springframework.http.codec.multipart.PartEvent
        public boolean isLast() {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/DefaultPartEvents$DefaultFilePartEvent.class */
    public static class DefaultFilePartEvent extends DefaultPartEvent implements FilePartEvent {
        public DefaultFilePartEvent(HttpHeaders headers) {
            super(headers);
        }

        public DefaultFilePartEvent(HttpHeaders headers, DataBuffer content, boolean last) {
            super(headers, content, last);
        }
    }
}
