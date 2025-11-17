package org.springframework.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;
import kotlin.Unit;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;
import kotlin.reflect.KFunction;
import kotlin.reflect.KParameter;
import kotlin.reflect.full.KCallables;
import kotlin.reflect.jvm.KCallablesJvm;
import kotlin.reflect.jvm.ReflectJvmMapping;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineStart;
import kotlinx.coroutines.Deferred;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.GlobalScope;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.reactor.MonoKt;
import kotlinx.coroutines.reactor.ReactorFlowKt;
import org.reactivestreams.Publisher;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/CoroutinesUtils.class */
public abstract class CoroutinesUtils {
    private static final ReflectionUtils.MethodFilter boxImplFilter = method -> {
        return method.isSynthetic() && Modifier.isStatic(method.getModifiers()) && method.getName().equals("box-impl");
    };

    public static <T> Mono<T> deferredToMono(Deferred<T> source) {
        return MonoKt.mono(Dispatchers.getUnconfined(), (scope, continuation) -> {
            return source.await(continuation);
        });
    }

    public static <T> Deferred<T> monoToDeferred(Mono<T> source) {
        return BuildersKt.async(GlobalScope.INSTANCE, Dispatchers.getUnconfined(), CoroutineStart.DEFAULT, (scope, continuation) -> {
            return MonoKt.awaitSingleOrNull(source, continuation);
        });
    }

    public static Publisher<?> invokeSuspendingFunction(Method method, Object target, Object... args) {
        return invokeSuspendingFunction(Dispatchers.getUnconfined(), method, target, args);
    }

    public static Publisher<?> invokeSuspendingFunction(CoroutineContext context, Method method, Object target, Object... args) {
        Assert.isTrue(KotlinDetector.isSuspendingFunction(method), "'method' must be a suspending function");
        KFunction<?> function = (KFunction) Objects.requireNonNull(ReflectJvmMapping.getKotlinFunction(method));
        if (method.isAccessible() && !KCallablesJvm.isAccessible(function)) {
            KCallablesJvm.setAccessible(function, true);
        }
        Mono<Object> mono = MonoKt.mono(context, (scope, continuation) -> {
            Map<KParameter, Object> argMap = CollectionUtils.newHashMap(args.length + 1);
            int index = 0;
            for (KParameter parameter : function.getParameters()) {
                switch (AnonymousClass1.$SwitchMap$kotlin$reflect$KParameter$Kind[parameter.getKind().ordinal()]) {
                    case 1:
                        argMap.put(parameter, target);
                        break;
                    case 2:
                    case 3:
                        if (!parameter.isOptional() || args[index] != null) {
                            KClass<?> classifier = parameter.getType().getClassifier();
                            if (classifier instanceof KClass) {
                                KClass<?> kClass = classifier;
                                if (kClass.isValue()) {
                                    Class<?> javaClass = JvmClassMappingKt.getJavaClass(kClass);
                                    Method[] methods = ReflectionUtils.getUniqueDeclaredMethods(javaClass, boxImplFilter);
                                    Assert.state(methods.length == 1, "Unable to find a single box-impl synthetic static method in " + javaClass.getName());
                                    argMap.put(parameter, ReflectionUtils.invokeMethod(methods[0], null, args[index]));
                                }
                            }
                            argMap.put(parameter, args[index]);
                        }
                        index++;
                        break;
                }
            }
            return KCallables.callSuspendBy(function, argMap, continuation);
        }).filter(result -> {
            return result != Unit.INSTANCE;
        }).onErrorMap(InvocationTargetException.class, (v0) -> {
            return v0.getTargetException();
        });
        KClass<?> classifier = function.getReturnType().getClassifier();
        if (classifier != null) {
            if (classifier.equals(JvmClassMappingKt.getKotlinClass(Flow.class))) {
                return mono.flatMapMany(CoroutinesUtils::asFlux);
            }
            if (classifier.equals(JvmClassMappingKt.getKotlinClass(Mono.class))) {
                return mono.flatMap(o -> {
                    return (Mono) o;
                });
            }
            if (classifier instanceof KClass) {
                KClass<?> kClass = classifier;
                if (Publisher.class.isAssignableFrom(JvmClassMappingKt.getJavaClass(kClass))) {
                    return mono.flatMapMany(o2 -> {
                        return (Publisher) o2;
                    });
                }
            }
        }
        return mono;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.springframework.core.CoroutinesUtils$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/CoroutinesUtils$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$kotlin$reflect$KParameter$Kind = new int[KParameter.Kind.values().length];

        static {
            try {
                $SwitchMap$kotlin$reflect$KParameter$Kind[KParameter.Kind.INSTANCE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$kotlin$reflect$KParameter$Kind[KParameter.Kind.VALUE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$kotlin$reflect$KParameter$Kind[KParameter.Kind.EXTENSION_RECEIVER.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private static Flux<?> asFlux(Object flow) {
        return ReactorFlowKt.asFlux((Flow) flow);
    }
}
