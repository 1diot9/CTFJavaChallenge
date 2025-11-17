package org.springframework.scheduling.annotation;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.CoroutinesUtils;
import org.springframework.core.KotlinDetector;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.SchedulingAwareRunnable;
import org.springframework.scheduling.support.DefaultScheduledTaskObservationConvention;
import org.springframework.scheduling.support.ScheduledTaskObservationContext;
import org.springframework.scheduling.support.ScheduledTaskObservationConvention;
import org.springframework.scheduling.support.ScheduledTaskObservationDocumentation;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/annotation/ScheduledAnnotationReactiveSupport.class */
public abstract class ScheduledAnnotationReactiveSupport {
    static final boolean reactorPresent = ClassUtils.isPresent("reactor.core.publisher.Flux", ScheduledAnnotationReactiveSupport.class.getClassLoader());
    static final boolean coroutinesReactorPresent = ClassUtils.isPresent("kotlinx.coroutines.reactor.MonoKt", ScheduledAnnotationReactiveSupport.class.getClassLoader());
    private static final Log logger = LogFactory.getLog((Class<?>) ScheduledAnnotationReactiveSupport.class);

    ScheduledAnnotationReactiveSupport() {
    }

    public static boolean isReactive(Method method) {
        if (KotlinDetector.isKotlinPresent() && KotlinDetector.isSuspendingFunction(method)) {
            Assert.isTrue(method.getParameterCount() == 1, "Kotlin suspending functions may only be annotated with @Scheduled if declared without arguments");
            Assert.isTrue(coroutinesReactorPresent, "Kotlin suspending functions may only be annotated with @Scheduled if the Coroutine-Reactor bridge (kotlinx.coroutines.reactor) is present at runtime");
            return true;
        }
        ReactiveAdapterRegistry registry = ReactiveAdapterRegistry.getSharedInstance();
        if (!registry.hasAdapters()) {
            return false;
        }
        Class<?> returnType = method.getReturnType();
        ReactiveAdapter candidateAdapter = registry.getAdapter(returnType);
        if (candidateAdapter == null) {
            return false;
        }
        Assert.isTrue(method.getParameterCount() == 0, "Reactive methods may only be annotated with @Scheduled if declared without arguments");
        Assert.isTrue(candidateAdapter.getDescriptor().isDeferred(), "Reactive methods may only be annotated with @Scheduled if the return type supports deferred execution");
        return true;
    }

    public static Runnable createSubscriptionRunnable(Method method, Object targetBean, Scheduled scheduled, Supplier<ObservationRegistry> observationRegistrySupplier, List<Runnable> subscriptionTrackerRegistry) {
        boolean shouldBlock = scheduled.fixedDelay() > 0 || StringUtils.hasText(scheduled.fixedDelayString());
        Publisher<?> publisher = getPublisherFor(method, targetBean);
        Supplier<ScheduledTaskObservationContext> contextSupplier = () -> {
            return new ScheduledTaskObservationContext(targetBean, method);
        };
        return new SubscribingRunnable(publisher, shouldBlock, scheduled.scheduler(), subscriptionTrackerRegistry, observationRegistrySupplier, contextSupplier);
    }

    static Publisher<?> getPublisherFor(Method method, Object bean) {
        if (KotlinDetector.isKotlinPresent() && KotlinDetector.isSuspendingFunction(method)) {
            return CoroutinesUtils.invokeSuspendingFunction(method, bean, method.getParameters());
        }
        ReactiveAdapterRegistry registry = ReactiveAdapterRegistry.getSharedInstance();
        Class<?> returnType = method.getReturnType();
        ReactiveAdapter adapter = registry.getAdapter(returnType);
        if (adapter == null) {
            throw new IllegalArgumentException("Cannot convert @Scheduled reactive method return type to Publisher");
        }
        if (!adapter.getDescriptor().isDeferred()) {
            throw new IllegalArgumentException("Cannot convert @Scheduled reactive method return type to Publisher: " + returnType.getSimpleName() + " is not a deferred reactive type");
        }
        Method invocableMethod = AopUtils.selectInvocableMethod(method, bean.getClass());
        try {
            ReflectionUtils.makeAccessible(invocableMethod);
            Object returnValue = invocableMethod.invoke(bean, new Object[0]);
            Publisher<?> publisher = adapter.toPublisher(returnValue);
            if (reactorPresent) {
                return Flux.from(publisher).checkpoint("@Scheduled '" + method.getName() + "()' in '" + method.getDeclaringClass().getName() + "'");
            }
            return publisher;
        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException("Cannot obtain a Publisher-convertible value from the @Scheduled reactive method", ex);
        } catch (InvocationTargetException ex2) {
            throw new IllegalArgumentException("Cannot obtain a Publisher-convertible value from the @Scheduled reactive method", ex2.getTargetException());
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/annotation/ScheduledAnnotationReactiveSupport$SubscribingRunnable.class */
    static final class SubscribingRunnable implements SchedulingAwareRunnable {
        private static final ScheduledTaskObservationConvention DEFAULT_CONVENTION = new DefaultScheduledTaskObservationConvention();
        private final Publisher<?> publisher;
        final boolean shouldBlock;

        @Nullable
        private final String qualifier;
        private final List<Runnable> subscriptionTrackerRegistry;
        final Supplier<ObservationRegistry> observationRegistrySupplier;
        final Supplier<ScheduledTaskObservationContext> contextSupplier;

        SubscribingRunnable(Publisher<?> publisher, boolean shouldBlock, @Nullable String qualifier, List<Runnable> subscriptionTrackerRegistry, Supplier<ObservationRegistry> observationRegistrySupplier, Supplier<ScheduledTaskObservationContext> contextSupplier) {
            this.publisher = publisher;
            this.shouldBlock = shouldBlock;
            this.qualifier = qualifier;
            this.subscriptionTrackerRegistry = subscriptionTrackerRegistry;
            this.observationRegistrySupplier = observationRegistrySupplier;
            this.contextSupplier = contextSupplier;
        }

        @Override // org.springframework.scheduling.SchedulingAwareRunnable
        @Nullable
        public String getQualifier() {
            return this.qualifier;
        }

        @Override // java.lang.Runnable
        public void run() {
            Observation observation = ScheduledTaskObservationDocumentation.TASKS_SCHEDULED_EXECUTION.observation(null, DEFAULT_CONVENTION, this.contextSupplier, this.observationRegistrySupplier.get());
            if (this.shouldBlock) {
                CountDownLatch latch = new CountDownLatch(1);
                TrackingSubscriber subscriber = new TrackingSubscriber(this.subscriptionTrackerRegistry, observation, latch);
                subscribe(subscriber, observation);
                try {
                    latch.await();
                    return;
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
            TrackingSubscriber subscriber2 = new TrackingSubscriber(this.subscriptionTrackerRegistry, observation);
            subscribe(subscriber2, observation);
        }

        private void subscribe(TrackingSubscriber subscriber, Observation observation) {
            this.subscriptionTrackerRegistry.add(subscriber);
            if (ScheduledAnnotationReactiveSupport.reactorPresent) {
                Flux.from(this.publisher).contextWrite(context -> {
                    return context.put(ObservationThreadLocalAccessor.KEY, observation);
                }).subscribe(subscriber);
            } else {
                this.publisher.subscribe(subscriber);
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/annotation/ScheduledAnnotationReactiveSupport$TrackingSubscriber.class */
    private static final class TrackingSubscriber implements Subscriber<Object>, Runnable {
        private final List<Runnable> subscriptionTrackerRegistry;
        private final Observation observation;

        @Nullable
        private final CountDownLatch blockingLatch;

        @Nullable
        private Subscription subscription;

        TrackingSubscriber(List<Runnable> subscriptionTrackerRegistry, Observation observation) {
            this(subscriptionTrackerRegistry, observation, null);
        }

        TrackingSubscriber(List<Runnable> subscriptionTrackerRegistry, Observation observation, @Nullable CountDownLatch latch) {
            this.subscriptionTrackerRegistry = subscriptionTrackerRegistry;
            this.observation = observation;
            this.blockingLatch = latch;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.subscription != null) {
                this.subscription.cancel();
                this.observation.stop();
            }
            if (this.blockingLatch != null) {
                this.blockingLatch.countDown();
            }
        }

        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            this.observation.start();
            subscription.request(2147483647L);
        }

        public void onNext(Object obj) {
        }

        public void onError(Throwable ex) {
            this.subscriptionTrackerRegistry.remove(this);
            ScheduledAnnotationReactiveSupport.logger.warn("Unexpected error occurred in scheduled reactive task", ex);
            this.observation.error(ex);
            this.observation.stop();
            if (this.blockingLatch != null) {
                this.blockingLatch.countDown();
            }
        }

        public void onComplete() {
            this.subscriptionTrackerRegistry.remove(this);
            Observation.Context context = this.observation.getContext();
            if (context instanceof ScheduledTaskObservationContext) {
                ScheduledTaskObservationContext context2 = (ScheduledTaskObservationContext) context;
                context2.setComplete(true);
            }
            this.observation.stop();
            if (this.blockingLatch != null) {
                this.blockingLatch.countDown();
            }
        }
    }
}
