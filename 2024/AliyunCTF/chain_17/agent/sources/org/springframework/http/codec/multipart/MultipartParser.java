package org.springframework.http.codec.multipart;

import cn.hutool.core.text.CharSequenceUtil;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Subscription;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.util.context.Context;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/MultipartParser.class */
public final class MultipartParser extends BaseSubscriber<DataBuffer> {
    private static final byte CR = 13;
    private static final byte LF = 10;
    private static final byte HYPHEN = 45;
    private static final String HEADER_ENTRY_SEPARATOR = "\\r\\n";
    private final FluxSink<Token> sink;
    private final byte[] boundary;
    private final int maxHeadersSize;
    private final Charset headersCharset;
    private static final byte[] CR_LF = {13, 10};
    private static final byte[] TWO_HYPHENS = {45, 45};
    private static final Log logger = LogFactory.getLog((Class<?>) MultipartParser.class);
    private final AtomicBoolean requestOutstanding = new AtomicBoolean();
    private final AtomicReference<State> state = new AtomicReference<>(new PreambleState());

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/MultipartParser$Token.class */
    public static abstract class Token {
        public abstract HttpHeaders headers();

        public abstract DataBuffer buffer();

        public abstract boolean isLast();
    }

    private MultipartParser(FluxSink<Token> sink, byte[] boundary, int maxHeadersSize, Charset headersCharset) {
        this.sink = sink;
        this.boundary = boundary;
        this.maxHeadersSize = maxHeadersSize;
        this.headersCharset = headersCharset;
    }

    public static Flux<Token> parse(Flux<DataBuffer> buffers, byte[] boundary, int maxHeadersSize, Charset headersCharset) {
        return Flux.create(sink -> {
            MultipartParser parser = new MultipartParser(sink, boundary, maxHeadersSize, headersCharset);
            Objects.requireNonNull(parser);
            sink.onCancel(parser::onSinkCancel);
            sink.onRequest(n -> {
                parser.requestBuffer();
            });
            buffers.subscribe(parser);
        });
    }

    public Context currentContext() {
        return Context.of(this.sink.contextView());
    }

    protected void hookOnSubscribe(Subscription subscription) {
        requestBuffer();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void hookOnNext(DataBuffer value) {
        this.requestOutstanding.set(false);
        this.state.get().onNext(value);
    }

    protected void hookOnComplete() {
        this.state.get().onComplete();
    }

    protected void hookOnError(Throwable throwable) {
        State oldState = this.state.getAndSet(DisposedState.INSTANCE);
        oldState.dispose();
        this.sink.error(throwable);
    }

    private void onSinkCancel() {
        State oldState = this.state.getAndSet(DisposedState.INSTANCE);
        oldState.dispose();
        cancel();
    }

    boolean changeState(State oldState, State newState, @Nullable DataBuffer remainder) {
        if (this.state.compareAndSet(oldState, newState)) {
            if (logger.isTraceEnabled()) {
                logger.trace("Changed state: " + oldState + " -> " + newState);
            }
            oldState.dispose();
            if (remainder != null) {
                if (remainder.readableByteCount() > 0) {
                    newState.onNext(remainder);
                    return true;
                }
                DataBufferUtils.release(remainder);
                requestBuffer();
                return true;
            }
            return true;
        }
        DataBufferUtils.release(remainder);
        return false;
    }

    void emitHeaders(HttpHeaders headers) {
        if (logger.isTraceEnabled()) {
            logger.trace("Emitting headers: " + headers);
        }
        this.sink.next(new HeadersToken(headers));
    }

    void emitBody(DataBuffer buffer, boolean last) {
        if (logger.isTraceEnabled()) {
            logger.trace("Emitting body: " + buffer);
        }
        this.sink.next(new BodyToken(buffer, last));
    }

    void emitError(Throwable t) {
        cancel();
        this.sink.error(t);
    }

    void emitComplete() {
        cancel();
        this.sink.complete();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestBuffer() {
        if (upstream() != null && !this.sink.isCancelled() && this.sink.requestedFromDownstream() > 0 && this.requestOutstanding.compareAndSet(false, true)) {
            request(1L);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/MultipartParser$HeadersToken.class */
    public static final class HeadersToken extends Token {
        private final HttpHeaders headers;

        public HeadersToken(HttpHeaders headers) {
            this.headers = headers;
        }

        @Override // org.springframework.http.codec.multipart.MultipartParser.Token
        public HttpHeaders headers() {
            return this.headers;
        }

        @Override // org.springframework.http.codec.multipart.MultipartParser.Token
        public DataBuffer buffer() {
            throw new IllegalStateException();
        }

        @Override // org.springframework.http.codec.multipart.MultipartParser.Token
        public boolean isLast() {
            return false;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/MultipartParser$BodyToken.class */
    public static final class BodyToken extends Token {
        private final DataBuffer buffer;
        private final boolean last;

        public BodyToken(DataBuffer buffer, boolean last) {
            this.buffer = buffer;
            this.last = last;
        }

        @Override // org.springframework.http.codec.multipart.MultipartParser.Token
        public HttpHeaders headers() {
            throw new IllegalStateException();
        }

        @Override // org.springframework.http.codec.multipart.MultipartParser.Token
        public DataBuffer buffer() {
            return this.buffer;
        }

        @Override // org.springframework.http.codec.multipart.MultipartParser.Token
        public boolean isLast() {
            return this.last;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/MultipartParser$State.class */
    public interface State {
        void onNext(DataBuffer buf);

        void onComplete();

        default void dispose() {
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/MultipartParser$PreambleState.class */
    private final class PreambleState implements State {
        private final DataBufferUtils.Matcher firstBoundary;

        /* JADX WARN: Type inference failed for: r1v2, types: [byte[], byte[][]] */
        public PreambleState() {
            this.firstBoundary = DataBufferUtils.matcher(MultipartUtils.concat(new byte[]{MultipartParser.TWO_HYPHENS, MultipartParser.this.boundary}));
        }

        @Override // org.springframework.http.codec.multipart.MultipartParser.State
        public void onNext(DataBuffer buf) {
            int endIdx = this.firstBoundary.match(buf);
            if (endIdx != -1) {
                if (MultipartParser.logger.isTraceEnabled()) {
                    MultipartParser.logger.trace("First boundary found @" + endIdx + " in " + buf);
                }
                DataBuffer preambleBuffer = buf.split(endIdx + 1);
                DataBufferUtils.release(preambleBuffer);
                MultipartParser.this.changeState(this, new HeadersState(), buf);
                return;
            }
            DataBufferUtils.release(buf);
            MultipartParser.this.requestBuffer();
        }

        @Override // org.springframework.http.codec.multipart.MultipartParser.State
        public void onComplete() {
            if (MultipartParser.this.changeState(this, DisposedState.INSTANCE, null)) {
                MultipartParser.this.emitError(new DecodingException("Could not find first boundary"));
            }
        }

        public String toString() {
            return "PREAMBLE";
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/MultipartParser$HeadersState.class */
    private final class HeadersState implements State {
        private final DataBufferUtils.Matcher endHeaders = DataBufferUtils.matcher(MultipartUtils.concat(new byte[]{MultipartParser.CR_LF, MultipartParser.CR_LF}));
        private final AtomicInteger byteCount = new AtomicInteger();
        private final List<DataBuffer> buffers = new ArrayList();

        /* JADX WARN: Type inference failed for: r1v2, types: [byte[], byte[][]] */
        private HeadersState() {
        }

        @Override // org.springframework.http.codec.multipart.MultipartParser.State
        public void onNext(DataBuffer buf) {
            if (isLastBoundary(buf)) {
                if (MultipartParser.logger.isTraceEnabled()) {
                    MultipartParser.logger.trace("Last boundary found in " + buf);
                }
                if (MultipartParser.this.changeState(this, DisposedState.INSTANCE, buf)) {
                    MultipartParser.this.emitComplete();
                    return;
                }
                return;
            }
            int endIdx = this.endHeaders.match(buf);
            if (endIdx != -1) {
                if (MultipartParser.logger.isTraceEnabled()) {
                    MultipartParser.logger.trace("End of headers found @" + endIdx + " in " + buf);
                }
                long count = this.byteCount.addAndGet(endIdx);
                if (belowMaxHeaderSize(count)) {
                    DataBuffer headerBuf = buf.split(endIdx + 1);
                    this.buffers.add(headerBuf);
                    MultipartParser.this.emitHeaders(parseHeaders());
                    MultipartParser.this.changeState(this, new BodyState(), buf);
                    return;
                }
                return;
            }
            long count2 = this.byteCount.addAndGet(buf.readableByteCount());
            if (belowMaxHeaderSize(count2)) {
                this.buffers.add(buf);
                MultipartParser.this.requestBuffer();
            }
        }

        private boolean isLastBoundary(DataBuffer buf) {
            return (this.buffers.isEmpty() && buf.readableByteCount() >= 2 && buf.getByte(0) == 45 && buf.getByte(1) == 45) || (this.buffers.size() == 1 && this.buffers.get(0).readableByteCount() == 1 && this.buffers.get(0).getByte(0) == 45 && buf.readableByteCount() >= 1 && buf.getByte(0) == 45);
        }

        private boolean belowMaxHeaderSize(long count) {
            if (count <= MultipartParser.this.maxHeadersSize) {
                return true;
            }
            MultipartParser.this.emitError(new DataBufferLimitException("Part headers exceeded the memory usage limit of " + MultipartParser.this.maxHeadersSize + " bytes"));
            return false;
        }

        private HttpHeaders parseHeaders() {
            String value;
            if (this.buffers.isEmpty()) {
                return HttpHeaders.EMPTY;
            }
            DataBuffer joined = this.buffers.get(0).factory().join(this.buffers);
            this.buffers.clear();
            String string = joined.toString(MultipartParser.this.headersCharset);
            DataBufferUtils.release(joined);
            String[] lines = string.split(MultipartParser.HEADER_ENTRY_SEPARATOR);
            HttpHeaders result = new HttpHeaders();
            for (String line : lines) {
                int idx = line.indexOf(58);
                if (idx != -1) {
                    String name = line.substring(0, idx);
                    String substring = line.substring(idx + 1);
                    while (true) {
                        value = substring;
                        if (!value.startsWith(CharSequenceUtil.SPACE)) {
                            break;
                        }
                        substring = value.substring(1);
                    }
                    result.add(name, value);
                }
            }
            return result;
        }

        @Override // org.springframework.http.codec.multipart.MultipartParser.State
        public void onComplete() {
            if (MultipartParser.this.changeState(this, DisposedState.INSTANCE, null)) {
                MultipartParser.this.emitError(new DecodingException("Could not find end of headers"));
            }
        }

        @Override // org.springframework.http.codec.multipart.MultipartParser.State
        public void dispose() {
            this.buffers.forEach(DataBufferUtils::release);
        }

        public String toString() {
            return "HEADERS";
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/MultipartParser$BodyState.class */
    private final class BodyState implements State {
        private final DataBufferUtils.Matcher boundary;
        private final int boundaryLength;
        private final Deque<DataBuffer> queue = new ConcurrentLinkedDeque();

        /* JADX WARN: Type inference failed for: r0v4, types: [byte[], byte[][]] */
        public BodyState() {
            byte[] delimiter = MultipartUtils.concat(new byte[]{MultipartParser.CR_LF, MultipartParser.TWO_HYPHENS, MultipartParser.this.boundary});
            this.boundary = DataBufferUtils.matcher(delimiter);
            this.boundaryLength = delimiter.length;
        }

        @Override // org.springframework.http.codec.multipart.MultipartParser.State
        public void onNext(DataBuffer buffer) {
            int endIdx = this.boundary.match(buffer);
            if (endIdx != -1) {
                DataBuffer boundaryBuffer = buffer.split(endIdx + 1);
                if (MultipartParser.logger.isTraceEnabled()) {
                    MultipartParser.logger.trace("Boundary found @" + endIdx + " in " + buffer);
                }
                int len = ((endIdx - this.boundaryLength) + 1) - boundaryBuffer.readPosition();
                if (len > 0) {
                    DataBuffer body = boundaryBuffer.split(len);
                    DataBufferUtils.release(boundaryBuffer);
                    enqueue(body);
                    flush();
                } else if (len < 0) {
                    DataBufferUtils.release(boundaryBuffer);
                    while (true) {
                        DataBuffer prev = this.queue.pollLast();
                        if (prev == null) {
                            break;
                        }
                        int prevByteCount = prev.readableByteCount();
                        int prevLen = prevByteCount + len;
                        if (prevLen >= 0) {
                            DataBuffer body2 = prev.split(prevLen + prev.readPosition());
                            DataBufferUtils.release(prev);
                            enqueue(body2);
                            flush();
                            break;
                        }
                        DataBufferUtils.release(prev);
                        len += prevByteCount;
                    }
                } else {
                    flush();
                }
                MultipartParser.this.changeState(this, new HeadersState(), buffer);
                return;
            }
            enqueue(buffer);
            MultipartParser.this.requestBuffer();
        }

        private void enqueue(DataBuffer buf) {
            this.queue.add(buf);
            int len = 0;
            Deque<DataBuffer> emit = new ArrayDeque<>();
            Iterator<DataBuffer> iterator = this.queue.descendingIterator();
            while (iterator.hasNext()) {
                DataBuffer previous = iterator.next();
                if (len > this.boundaryLength) {
                    emit.addFirst(previous);
                    iterator.remove();
                }
                len += previous.readableByteCount();
            }
            emit.forEach(buffer -> {
                MultipartParser.this.emitBody(buffer, false);
            });
        }

        private void flush() {
            Iterator<DataBuffer> iterator = this.queue.iterator();
            while (iterator.hasNext()) {
                DataBuffer buffer = iterator.next();
                boolean last = !iterator.hasNext();
                MultipartParser.this.emitBody(buffer, last);
            }
            this.queue.clear();
        }

        @Override // org.springframework.http.codec.multipart.MultipartParser.State
        public void onComplete() {
            if (MultipartParser.this.changeState(this, DisposedState.INSTANCE, null)) {
                String msg = "Could not find end of body (␍␊--" + new String(MultipartParser.this.boundary, StandardCharsets.UTF_8) + ")";
                MultipartParser.this.emitError(new DecodingException(msg));
            }
        }

        @Override // org.springframework.http.codec.multipart.MultipartParser.State
        public void dispose() {
            this.queue.forEach(DataBufferUtils::release);
            this.queue.clear();
        }

        public String toString() {
            return "BODY";
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/MultipartParser$DisposedState.class */
    private static final class DisposedState implements State {
        public static final DisposedState INSTANCE = new DisposedState();

        private DisposedState() {
        }

        @Override // org.springframework.http.codec.multipart.MultipartParser.State
        public void onNext(DataBuffer buf) {
            DataBufferUtils.release(buf);
        }

        @Override // org.springframework.http.codec.multipart.MultipartParser.State
        public void onComplete() {
        }

        public String toString() {
            return "DISPOSED";
        }
    }
}
