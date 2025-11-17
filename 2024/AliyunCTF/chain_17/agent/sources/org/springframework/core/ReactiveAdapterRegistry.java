package org.springframework.core;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.groups.MultiCreate;
import io.smallrye.mutiny.groups.UniConvert;
import io.smallrye.mutiny.groups.UniCreate;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;
import java.util.function.Function;
import kotlinx.coroutines.CompletableDeferredKt;
import kotlinx.coroutines.Deferred;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.reactive.ReactiveFlowKt;
import kotlinx.coroutines.reactor.ReactorFlowKt;
import org.reactivestreams.FlowAdapters;
import org.reactivestreams.Publisher;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import reactor.adapter.JdkFlowAdapter;
import reactor.blockhound.BlockHound;
import reactor.blockhound.integration.BlockHoundIntegration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/ReactiveAdapterRegistry.class */
public class ReactiveAdapterRegistry {

    @Nullable
    private static volatile ReactiveAdapterRegistry sharedInstance;
    private static final boolean reactiveStreamsPresent;
    private static final boolean reactorPresent;
    private static final boolean rxjava3Present;
    private static final boolean kotlinCoroutinesPresent;
    private static final boolean mutinyPresent;
    private final List<ReactiveAdapter> adapters = new ArrayList();

    static {
        ClassLoader classLoader = ReactiveAdapterRegistry.class.getClassLoader();
        reactiveStreamsPresent = ClassUtils.isPresent("org.reactivestreams.Publisher", classLoader);
        reactorPresent = ClassUtils.isPresent("reactor.core.publisher.Flux", classLoader);
        rxjava3Present = ClassUtils.isPresent("io.reactivex.rxjava3.core.Flowable", classLoader);
        kotlinCoroutinesPresent = ClassUtils.isPresent("kotlinx.coroutines.reactor.MonoKt", classLoader);
        mutinyPresent = ClassUtils.isPresent("io.smallrye.mutiny.Multi", classLoader);
    }

    public ReactiveAdapterRegistry() {
        if (!reactiveStreamsPresent) {
            return;
        }
        if (reactorPresent) {
            new ReactorRegistrar().registerAdapters(this);
        }
        if (rxjava3Present) {
            new RxJava3Registrar().registerAdapters(this);
        }
        if (reactorPresent && kotlinCoroutinesPresent) {
            new CoroutinesRegistrar().registerAdapters(this);
        }
        if (mutinyPresent) {
            new MutinyRegistrar().registerAdapters(this);
        }
        if (!reactorPresent) {
            new FlowAdaptersRegistrar().registerAdapters(this);
        }
    }

    public void registerReactiveType(ReactiveTypeDescriptor descriptor, Function<Object, Publisher<?>> toAdapter, Function<Publisher<?>, Object> fromAdapter) {
        this.adapters.add(buildAdapter(descriptor, toAdapter, fromAdapter));
    }

    public void registerReactiveTypeOverride(ReactiveTypeDescriptor descriptor, Function<Object, Publisher<?>> toAdapter, Function<Publisher<?>, Object> fromAdapter) {
        this.adapters.add(0, buildAdapter(descriptor, toAdapter, fromAdapter));
    }

    private ReactiveAdapter buildAdapter(ReactiveTypeDescriptor descriptor, Function<Object, Publisher<?>> toAdapter, Function<Publisher<?>, Object> fromAdapter) {
        return reactorPresent ? new ReactorAdapter(descriptor, toAdapter, fromAdapter) : new ReactiveAdapter(descriptor, toAdapter, fromAdapter);
    }

    public boolean hasAdapters() {
        return !this.adapters.isEmpty();
    }

    @Nullable
    public ReactiveAdapter getAdapter(Class<?> reactiveType) {
        return getAdapter(reactiveType, null);
    }

    @Nullable
    public ReactiveAdapter getAdapter(@Nullable Class<?> reactiveType, @Nullable Object source) {
        Object obj;
        if (this.adapters.isEmpty()) {
            return null;
        }
        if (source instanceof Optional) {
            Optional<?> optional = (Optional) source;
            obj = optional.orElse(null);
        } else {
            obj = source;
        }
        Object sourceToUse = obj;
        Class<?> clazz = sourceToUse != null ? sourceToUse.getClass() : reactiveType;
        if (clazz == null) {
            return null;
        }
        for (ReactiveAdapter adapter : this.adapters) {
            if (adapter.getReactiveType() == clazz) {
                return adapter;
            }
        }
        for (ReactiveAdapter adapter2 : this.adapters) {
            if (adapter2.getReactiveType().isAssignableFrom(clazz)) {
                return adapter2;
            }
        }
        return null;
    }

    public static ReactiveAdapterRegistry getSharedInstance() {
        ReactiveAdapterRegistry registry = sharedInstance;
        if (registry == null) {
            synchronized (ReactiveAdapterRegistry.class) {
                registry = sharedInstance;
                if (registry == null) {
                    registry = new ReactiveAdapterRegistry();
                    sharedInstance = registry;
                }
            }
        }
        return registry;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/ReactiveAdapterRegistry$ReactorAdapter.class */
    public static class ReactorAdapter extends ReactiveAdapter {
        ReactorAdapter(ReactiveTypeDescriptor descriptor, Function<Object, Publisher<?>> toPublisherFunction, Function<Publisher<?>, Object> fromPublisherFunction) {
            super(descriptor, toPublisherFunction, fromPublisherFunction);
        }

        @Override // org.springframework.core.ReactiveAdapter
        public <T> Publisher<T> toPublisher(@Nullable Object source) {
            Publisher<T> publisher = super.toPublisher(source);
            return isMultiValue() ? Flux.from(publisher) : Mono.from(publisher);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/ReactiveAdapterRegistry$ReactorRegistrar.class */
    private static class ReactorRegistrar {
        private static final Flow.Publisher<?> EMPTY_FLOW = JdkFlowAdapter.publisherToFlowPublisher(Flux.empty());

        private ReactorRegistrar() {
        }

        void registerAdapters(ReactiveAdapterRegistry registry) {
            registry.registerReactiveType(ReactiveTypeDescriptor.singleOptionalValue(Mono.class, Mono::empty), source -> {
                return (Mono) source;
            }, Mono::from);
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(Flux.class, Flux::empty), source2 -> {
                return (Flux) source2;
            }, Flux::from);
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(Publisher.class, Flux::empty), source3 -> {
                return (Publisher) source3;
            }, source4 -> {
                return source4;
            });
            registry.registerReactiveType(ReactiveTypeDescriptor.nonDeferredAsyncValue(CompletionStage.class, EmptyCompletableFuture::new), source5 -> {
                return Mono.fromCompletionStage((CompletionStage) source5);
            }, source6 -> {
                return Mono.from(source6).toFuture();
            });
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(Flow.Publisher.class, () -> {
                return EMPTY_FLOW;
            }), source7 -> {
                return JdkFlowAdapter.flowPublisherToFlux((Flow.Publisher) source7);
            }, JdkFlowAdapter::publisherToFlowPublisher);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/ReactiveAdapterRegistry$EmptyCompletableFuture.class */
    public static class EmptyCompletableFuture<T> extends CompletableFuture<T> {
        EmptyCompletableFuture() {
            complete(null);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/ReactiveAdapterRegistry$RxJava3Registrar.class */
    private static class RxJava3Registrar {
        private RxJava3Registrar() {
        }

        void registerAdapters(ReactiveAdapterRegistry registry) {
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(Flowable.class, Flowable::empty), source -> {
                return (Flowable) source;
            }, Flowable::fromPublisher);
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(Observable.class, Observable::empty), source2 -> {
                return ((Observable) source2).toFlowable(BackpressureStrategy.BUFFER);
            }, Observable::fromPublisher);
            registry.registerReactiveType(ReactiveTypeDescriptor.singleRequiredValue(Single.class), source3 -> {
                return ((Single) source3).toFlowable();
            }, Single::fromPublisher);
            registry.registerReactiveType(ReactiveTypeDescriptor.singleOptionalValue(Maybe.class, Maybe::empty), source4 -> {
                return ((Maybe) source4).toFlowable();
            }, Maybe::fromPublisher);
            registry.registerReactiveType(ReactiveTypeDescriptor.noValue(Completable.class, Completable::complete), source5 -> {
                return ((Completable) source5).toFlowable();
            }, Completable::fromPublisher);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/ReactiveAdapterRegistry$CoroutinesRegistrar.class */
    private static class CoroutinesRegistrar {
        private CoroutinesRegistrar() {
        }

        void registerAdapters(ReactiveAdapterRegistry registry) {
            registry.registerReactiveType(ReactiveTypeDescriptor.singleOptionalValue(Deferred.class, () -> {
                return CompletableDeferredKt.CompletableDeferred((Job) null);
            }), source -> {
                return CoroutinesUtils.deferredToMono((Deferred) source);
            }, source2 -> {
                return CoroutinesUtils.monoToDeferred(Mono.from(source2));
            });
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(kotlinx.coroutines.flow.Flow.class, FlowKt::emptyFlow), source3 -> {
                return ReactorFlowKt.asFlux((kotlinx.coroutines.flow.Flow) source3);
            }, ReactiveFlowKt::asFlow);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/ReactiveAdapterRegistry$MutinyRegistrar.class */
    private static class MutinyRegistrar {
        private static final Method uniToPublisher = ClassUtils.getMethod(UniConvert.class, "toPublisher", new Class[0]);

        private MutinyRegistrar() {
        }

        void registerAdapters(ReactiveAdapterRegistry registry) {
            ReactiveTypeDescriptor uniDesc = ReactiveTypeDescriptor.singleOptionalValue(Uni.class, () -> {
                return Uni.createFrom().nothing();
            });
            ReactiveTypeDescriptor multiDesc = ReactiveTypeDescriptor.multiValue(Multi.class, () -> {
                return Multi.createFrom().empty();
            });
            if (Flow.Publisher.class.isAssignableFrom(uniToPublisher.getReturnType())) {
                Method uniPublisher = ClassUtils.getMethod(UniCreate.class, "publisher", Flow.Publisher.class);
                Method multiPublisher = ClassUtils.getMethod(MultiCreate.class, "publisher", Flow.Publisher.class);
                registry.registerReactiveType(uniDesc, uni -> {
                    return FlowAdapters.toPublisher((Flow.Publisher) Objects.requireNonNull(ReflectionUtils.invokeMethod(uniToPublisher, ((Uni) uni).convert())));
                }, publisher -> {
                    return ReflectionUtils.invokeMethod(uniPublisher, Uni.createFrom(), FlowAdapters.toFlowPublisher(publisher));
                });
                registry.registerReactiveType(multiDesc, multi -> {
                    return FlowAdapters.toPublisher((Flow.Publisher) multi);
                }, publisher2 -> {
                    return ReflectionUtils.invokeMethod(multiPublisher, Multi.createFrom(), FlowAdapters.toFlowPublisher(publisher2));
                });
                return;
            }
            registry.registerReactiveType(uniDesc, uni2 -> {
                return ((Uni) uni2).convert().toPublisher();
            }, publisher3 -> {
                return Uni.createFrom().publisher(publisher3);
            });
            registry.registerReactiveType(multiDesc, multi2 -> {
                return (Multi) multi2;
            }, publisher4 -> {
                return Multi.createFrom().publisher(publisher4);
            });
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/ReactiveAdapterRegistry$FlowAdaptersRegistrar.class */
    private static class FlowAdaptersRegistrar {
        private static final Flow.Subscription EMPTY_SUBSCRIPTION = new Flow.Subscription() { // from class: org.springframework.core.ReactiveAdapterRegistry.FlowAdaptersRegistrar.1
            @Override // java.util.concurrent.Flow.Subscription
            public void request(long n) {
            }

            @Override // java.util.concurrent.Flow.Subscription
            public void cancel() {
            }
        };
        private static final Flow.Publisher<Object> EMPTY_PUBLISHER = subscriber -> {
            subscriber.onSubscribe(EMPTY_SUBSCRIPTION);
            subscriber.onComplete();
        };

        private FlowAdaptersRegistrar() {
        }

        void registerAdapters(ReactiveAdapterRegistry registry) {
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(Flow.Publisher.class, () -> {
                return EMPTY_PUBLISHER;
            }), source -> {
                return FlowAdapters.toPublisher((Flow.Publisher) source);
            }, source2 -> {
                return FlowAdapters.toFlowPublisher(source2);
            });
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/ReactiveAdapterRegistry$SpringCoreBlockHoundIntegration.class */
    public static class SpringCoreBlockHoundIntegration implements BlockHoundIntegration {
        public void applyTo(BlockHound.Builder builder) {
            builder.allowBlockingCallsInside("org.springframework.util.ConcurrentReferenceHashMap$Segment", "doTask");
            builder.allowBlockingCallsInside("org.springframework.util.ConcurrentReferenceHashMap$Segment", "clear");
            builder.allowBlockingCallsInside("org.springframework.util.ConcurrentReferenceHashMap$Segment", "restructure");
        }
    }
}
