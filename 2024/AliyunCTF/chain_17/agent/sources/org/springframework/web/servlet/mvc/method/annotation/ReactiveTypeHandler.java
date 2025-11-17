package org.springframework.web.servlet.mvc.method.annotation;

import cn.hutool.core.text.StrPool;
import io.micrometer.context.ContextSnapshot;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.ResolvableType;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/ReactiveTypeHandler.class */
class ReactiveTypeHandler {
    private static final long STREAMING_TIMEOUT_VALUE = -1;
    private static final MediaType WILDCARD_SUBTYPE_SUFFIXED_BY_NDJSON = MediaType.valueOf("application/*+x-ndjson");
    private static final boolean isContextPropagationPresent = ClassUtils.isPresent("io.micrometer.context.ContextSnapshot", ReactiveTypeHandler.class.getClassLoader());
    private static final Log logger = LogFactory.getLog((Class<?>) ReactiveTypeHandler.class);
    private final ReactiveAdapterRegistry adapterRegistry;
    private final TaskExecutor taskExecutor;
    private final ContentNegotiationManager contentNegotiationManager;

    public ReactiveTypeHandler() {
        this(ReactiveAdapterRegistry.getSharedInstance(), new SyncTaskExecutor(), new ContentNegotiationManager());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReactiveTypeHandler(ReactiveAdapterRegistry registry, TaskExecutor executor, ContentNegotiationManager manager) {
        Assert.notNull(registry, "ReactiveAdapterRegistry is required");
        Assert.notNull(executor, "TaskExecutor is required");
        Assert.notNull(manager, "ContentNegotiationManager is required");
        this.adapterRegistry = registry;
        this.taskExecutor = executor;
        this.contentNegotiationManager = manager;
    }

    public boolean isReactiveType(Class<?> type) {
        return this.adapterRegistry.getAdapter(type) != null;
    }

    @Nullable
    public ResponseBodyEmitter handleValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mav, NativeWebRequest request) throws Exception {
        Assert.notNull(returnValue, "Expected return value");
        Class<?> clazz = returnValue.getClass();
        ReactiveAdapter adapter = this.adapterRegistry.getAdapter(clazz);
        Assert.state(adapter != null, (Supplier<String>) () -> {
            return "Unexpected return value type: " + clazz;
        });
        if (isContextPropagationPresent) {
            returnValue = ContextSnapshotHelper.writeReactorContext(returnValue);
        }
        ResolvableType elementType = ResolvableType.forMethodParameter(returnType).getGeneric(new int[0]);
        Class<?> elementClass = elementType.toClass();
        Collection<MediaType> mediaTypes = getMediaTypes(request);
        Optional<MediaType> mediaType = mediaTypes.stream().filter((v0) -> {
            return v0.isConcrete();
        }).findFirst();
        if (adapter.isMultiValue()) {
            Stream<MediaType> stream = mediaTypes.stream();
            MediaType mediaType2 = MediaType.TEXT_EVENT_STREAM;
            Objects.requireNonNull(mediaType2);
            if (stream.anyMatch(mediaType2::includes) || ServerSentEvent.class.isAssignableFrom(elementClass)) {
                SseEmitter emitter = new SseEmitter(-1L);
                new SseEmitterSubscriber(emitter, this.taskExecutor).connect(adapter, returnValue);
                return emitter;
            }
            if (CharSequence.class.isAssignableFrom(elementClass)) {
                ResponseBodyEmitter emitter2 = getEmitter(mediaType.orElse(MediaType.TEXT_PLAIN));
                new TextEmitterSubscriber(emitter2, this.taskExecutor).connect(adapter, returnValue);
                return emitter2;
            }
            MediaType streamingResponseType = findConcreteStreamingMediaType(mediaTypes);
            if (streamingResponseType != null) {
                ResponseBodyEmitter emitter3 = getEmitter(streamingResponseType);
                new JsonEmitterSubscriber(emitter3, this.taskExecutor).connect(adapter, returnValue);
                return emitter3;
            }
        }
        DeferredResult<Object> result = new DeferredResult<>();
        new DeferredResultSubscriber(result, adapter, elementType).connect(adapter, returnValue);
        WebAsyncUtils.getAsyncManager(request).startDeferredResultProcessing(result, mav);
        return null;
    }

    @Nullable
    static MediaType findConcreteStreamingMediaType(Collection<MediaType> acceptedMediaTypes) {
        for (MediaType acceptedType : acceptedMediaTypes) {
            if (WILDCARD_SUBTYPE_SUFFIXED_BY_NDJSON.includes(acceptedType)) {
                if (acceptedType.isConcrete()) {
                    return acceptedType;
                }
                return MediaType.APPLICATION_NDJSON;
            }
            if (MediaType.APPLICATION_NDJSON.includes(acceptedType)) {
                return MediaType.APPLICATION_NDJSON;
            }
            if (MediaType.APPLICATION_STREAM_JSON.includes(acceptedType)) {
                return MediaType.APPLICATION_STREAM_JSON;
            }
        }
        return null;
    }

    private Collection<MediaType> getMediaTypes(NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
        Collection<MediaType> mediaTypes = (Collection) request.getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, 0);
        return CollectionUtils.isEmpty(mediaTypes) ? this.contentNegotiationManager.resolveMediaTypes(request) : mediaTypes;
    }

    private ResponseBodyEmitter getEmitter(final MediaType mediaType) {
        return new ResponseBodyEmitter(-1L) { // from class: org.springframework.web.servlet.mvc.method.annotation.ReactiveTypeHandler.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter
            public void extendResponse(ServerHttpResponse outputMessage) {
                outputMessage.getHeaders().setContentType(mediaType);
            }
        };
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/ReactiveTypeHandler$AbstractEmitterSubscriber.class */
    private static abstract class AbstractEmitterSubscriber implements Subscriber<Object>, Runnable {
        private final ResponseBodyEmitter emitter;
        private final TaskExecutor taskExecutor;

        @Nullable
        private Subscription subscription;

        @Nullable
        private Throwable error;
        private volatile boolean terminated;
        private volatile boolean done;
        private final AtomicReference<Object> elementRef = new AtomicReference<>();
        private final AtomicLong executing = new AtomicLong();

        protected abstract void send(Object element) throws IOException;

        protected AbstractEmitterSubscriber(ResponseBodyEmitter emitter, TaskExecutor executor) {
            this.emitter = emitter;
            this.taskExecutor = executor;
        }

        public void connect(ReactiveAdapter adapter, Object returnValue) {
            Publisher<Object> publisher = adapter.toPublisher(returnValue);
            publisher.subscribe(this);
        }

        protected ResponseBodyEmitter getEmitter() {
            return this.emitter;
        }

        public final void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            this.emitter.onTimeout(() -> {
                if (ReactiveTypeHandler.logger.isTraceEnabled()) {
                    ReactiveTypeHandler.logger.trace("Connection timeout for " + this.emitter);
                }
                terminate();
                this.emitter.complete();
            });
            ResponseBodyEmitter responseBodyEmitter = this.emitter;
            ResponseBodyEmitter responseBodyEmitter2 = this.emitter;
            Objects.requireNonNull(responseBodyEmitter2);
            responseBodyEmitter.onError(responseBodyEmitter2::completeWithError);
            subscription.request(1L);
        }

        public final void onNext(Object element) {
            this.elementRef.lazySet(element);
            trySchedule();
        }

        public final void onError(Throwable ex) {
            this.error = ex;
            this.terminated = true;
            trySchedule();
        }

        public final void onComplete() {
            this.terminated = true;
            trySchedule();
        }

        private void trySchedule() {
            if (this.executing.getAndIncrement() == 0) {
                schedule();
            }
        }

        private void schedule() {
            try {
                this.taskExecutor.execute(this);
            } catch (Throwable th) {
                try {
                    terminate();
                } finally {
                    this.executing.decrementAndGet();
                    this.elementRef.lazySet(null);
                }
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.done) {
                this.elementRef.lazySet(null);
                return;
            }
            boolean isTerminated = this.terminated;
            Object element = this.elementRef.get();
            if (element != null) {
                this.elementRef.lazySet(null);
                Assert.state(this.subscription != null, "No subscription");
                try {
                    send(element);
                    this.subscription.request(1L);
                } catch (Throwable ex) {
                    if (ReactiveTypeHandler.logger.isTraceEnabled()) {
                        ReactiveTypeHandler.logger.trace("Send for " + this.emitter + " failed: " + ex);
                    }
                    terminate();
                    return;
                }
            }
            if (!isTerminated) {
                if (this.executing.decrementAndGet() != 0) {
                    schedule();
                    return;
                }
                return;
            }
            this.done = true;
            Throwable ex2 = this.error;
            this.error = null;
            if (ex2 != null) {
                if (ReactiveTypeHandler.logger.isTraceEnabled()) {
                    ReactiveTypeHandler.logger.trace("Publisher for " + this.emitter + " failed: " + ex2);
                }
                this.emitter.completeWithError(ex2);
            } else {
                if (ReactiveTypeHandler.logger.isTraceEnabled()) {
                    ReactiveTypeHandler.logger.trace("Publisher for " + this.emitter + " completed");
                }
                this.emitter.complete();
            }
        }

        private void terminate() {
            this.done = true;
            if (this.subscription != null) {
                this.subscription.cancel();
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/ReactiveTypeHandler$SseEmitterSubscriber.class */
    private static class SseEmitterSubscriber extends AbstractEmitterSubscriber {
        SseEmitterSubscriber(SseEmitter sseEmitter, TaskExecutor executor) {
            super(sseEmitter, executor);
        }

        @Override // org.springframework.web.servlet.mvc.method.annotation.ReactiveTypeHandler.AbstractEmitterSubscriber
        protected void send(Object element) throws IOException {
            if (element instanceof ServerSentEvent) {
                ServerSentEvent<?> event = (ServerSentEvent) element;
                ((SseEmitter) getEmitter()).send(adapt(event));
            } else {
                getEmitter().send(element, MediaType.APPLICATION_JSON);
            }
        }

        private SseEmitter.SseEventBuilder adapt(ServerSentEvent<?> sse) {
            SseEmitter.SseEventBuilder builder = SseEmitter.event();
            String id = sse.id();
            String event = sse.event();
            Duration retry = sse.retry();
            String comment = sse.comment();
            Object data = sse.data();
            if (id != null) {
                builder.id(id);
            }
            if (event != null) {
                builder.name(event);
            }
            if (data != null) {
                builder.data(data);
            }
            if (retry != null) {
                builder.reconnectTime(retry.toMillis());
            }
            if (comment != null) {
                builder.comment(comment);
            }
            return builder;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/ReactiveTypeHandler$JsonEmitterSubscriber.class */
    private static class JsonEmitterSubscriber extends AbstractEmitterSubscriber {
        JsonEmitterSubscriber(ResponseBodyEmitter emitter, TaskExecutor executor) {
            super(emitter, executor);
        }

        @Override // org.springframework.web.servlet.mvc.method.annotation.ReactiveTypeHandler.AbstractEmitterSubscriber
        protected void send(Object element) throws IOException {
            getEmitter().send(element, MediaType.APPLICATION_JSON);
            getEmitter().send(StrPool.LF, MediaType.TEXT_PLAIN);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/ReactiveTypeHandler$TextEmitterSubscriber.class */
    private static class TextEmitterSubscriber extends AbstractEmitterSubscriber {
        TextEmitterSubscriber(ResponseBodyEmitter emitter, TaskExecutor executor) {
            super(emitter, executor);
        }

        @Override // org.springframework.web.servlet.mvc.method.annotation.ReactiveTypeHandler.AbstractEmitterSubscriber
        protected void send(Object element) throws IOException {
            getEmitter().send(element, MediaType.TEXT_PLAIN);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/ReactiveTypeHandler$DeferredResultSubscriber.class */
    private static class DeferredResultSubscriber implements Subscriber<Object> {
        private final DeferredResult<Object> result;
        private final boolean multiValueSource;
        private final CollectedValuesList values;

        DeferredResultSubscriber(DeferredResult<Object> result, ReactiveAdapter adapter, ResolvableType elementType) {
            this.result = result;
            this.multiValueSource = adapter.isMultiValue();
            this.values = new CollectedValuesList(elementType);
        }

        public void connect(ReactiveAdapter adapter, Object returnValue) {
            Publisher<Object> publisher = adapter.toPublisher(returnValue);
            publisher.subscribe(this);
        }

        public void onSubscribe(Subscription subscription) {
            DeferredResult<Object> deferredResult = this.result;
            Objects.requireNonNull(subscription);
            deferredResult.onTimeout(subscription::cancel);
            subscription.request(Long.MAX_VALUE);
        }

        public void onNext(Object element) {
            this.values.add(element);
        }

        public void onError(Throwable ex) {
            this.result.setErrorResult(ex);
        }

        public void onComplete() {
            if (this.values.size() > 1 || this.multiValueSource) {
                this.result.setResult(this.values);
            } else if (this.values.size() == 1) {
                this.result.setResult(this.values.get(0));
            } else {
                this.result.setResult(null);
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/ReactiveTypeHandler$CollectedValuesList.class */
    static class CollectedValuesList extends ArrayList<Object> {
        private final ResolvableType elementType;

        CollectedValuesList(ResolvableType elementType) {
            this.elementType = elementType;
        }

        public ResolvableType getReturnType() {
            return ResolvableType.forClassWithGenerics((Class<?>) List.class, this.elementType);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/ReactiveTypeHandler$ContextSnapshotHelper.class */
    private static class ContextSnapshotHelper {
        private ContextSnapshotHelper() {
        }

        public static Object writeReactorContext(Object returnValue) {
            if (Mono.class.isAssignableFrom(returnValue.getClass())) {
                ContextSnapshot snapshot = ContextSnapshot.captureAll(new Object[0]);
                Objects.requireNonNull(snapshot);
                return ((Mono) returnValue).contextWrite((v1) -> {
                    return r1.updateContext(v1);
                });
            }
            if (Flux.class.isAssignableFrom(returnValue.getClass())) {
                ContextSnapshot snapshot2 = ContextSnapshot.captureAll(new Object[0]);
                Objects.requireNonNull(snapshot2);
                return ((Flux) returnValue).contextWrite((v1) -> {
                    return r1.updateContext(v1);
                });
            }
            return returnValue;
        }
    }
}
