package org.springframework.boot;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KClass;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.function.ThrowingConsumer;

/* compiled from: SpringApplicationExtensions.kt */
@Metadata(mv = {1, 7, 0}, k = 2, xi = 48, d1 = {"��<\n��\n\u0002\u0018\u0002\n��\n\u0002\u0010��\n��\n\u0002\u0018\u0002\n��\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0015\u0010��\u001a\u00020\u0001\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0003H\u0086\b\u001a.\u0010\u0004\u001a\u00020\u0005\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0012\u0010\u0006\u001a\n\u0012\u0006\b\u0001\u0012\u00020\b0\u0007\"\u00020\bH\u0086\b¢\u0006\u0002\u0010\t\u001aJ\u0010\u0004\u001a\u00020\u0005\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0012\u0010\u0006\u001a\n\u0012\u0006\b\u0001\u0012\u00020\b0\u0007\"\u00020\b2\u0017\u0010\n\u001a\u0013\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\r0\u000b¢\u0006\u0002\b\u000eH\u0086\bø\u0001��¢\u0006\u0002\u0010\u000f\u001a+\u0010\u0010\u001a\u00020\u0001*\u00020\u00012\u001a\u0010\u0011\u001a\u000e\u0012\n\b\u0001\u0012\u0006\u0012\u0002\b\u00030\u00120\u0007\"\u0006\u0012\u0002\b\u00030\u0012¢\u0006\u0002\u0010\u0013\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006\u0014"}, d2 = {"fromApplication", "Lorg/springframework/boot/SpringApplication$Augmented;", "T", "", "runApplication", "Lorg/springframework/context/ConfigurableApplicationContext;", "args", "", "", "([Ljava/lang/String;)Lorg/springframework/context/ConfigurableApplicationContext;", "init", "Lkotlin/Function1;", "Lorg/springframework/boot/SpringApplication;", "", "Lkotlin/ExtensionFunctionType;", "([Ljava/lang/String;Lkotlin/jvm/functions/Function1;)Lorg/springframework/context/ConfigurableApplicationContext;", JsonPOJOBuilder.DEFAULT_WITH_PREFIX, "types", "Lkotlin/reflect/KClass;", "(Lorg/springframework/boot/SpringApplication$Augmented;[Lkotlin/reflect/KClass;)Lorg/springframework/boot/SpringApplication$Augmented;", "spring-boot"})
@SourceDebugExtension({"SMAP\nSpringApplicationExtensions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SpringApplicationExtensions.kt\norg/springframework/boot/SpringApplicationExtensionsKt\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,79:1\n11065#2:80\n11400#2,3:81\n37#3,2:84\n*S KotlinDebug\n*F\n+ 1 SpringApplicationExtensions.kt\norg/springframework/boot/SpringApplicationExtensionsKt\n*L\n77#1:80\n77#1:81,3\n77#1:84,2\n*E\n"})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplicationExtensionsKt.class */
public final class SpringApplicationExtensionsKt {
    public static final /* synthetic */ <T> ConfigurableApplicationContext runApplication(String... args) {
        Intrinsics.checkNotNullParameter(args, "args");
        Intrinsics.reifiedOperationMarker(4, "T");
        ConfigurableApplicationContext run = SpringApplication.run((Class<?>) Object.class, (String[]) Arrays.copyOf(args, args.length));
        Intrinsics.checkNotNullExpressionValue(run, "run(T::class.java, *args)");
        return run;
    }

    public static final /* synthetic */ <T> ConfigurableApplicationContext runApplication(String[] args, Function1<? super SpringApplication, Unit> function1) {
        Intrinsics.checkNotNullParameter(args, "args");
        Intrinsics.checkNotNullParameter(function1, "init");
        Intrinsics.reifiedOperationMarker(4, "T");
        SpringApplication springApplication = new SpringApplication(Object.class);
        function1.invoke(springApplication);
        ConfigurableApplicationContext run = springApplication.run((String[]) Arrays.copyOf(args, args.length));
        Intrinsics.checkNotNullExpressionValue(run, "SpringApplication(T::cla…a).apply(init).run(*args)");
        return run;
    }

    public static final /* synthetic */ <T> SpringApplication.Augmented fromApplication() {
        Intrinsics.reifiedOperationMarker(4, "T");
        KClass type = Reflection.getOrCreateKotlinClass(Object.class);
        String ktClassName = type.getQualifiedName() + "Kt";
        try {
            Class ktClass = ClassUtils.resolveClassName(ktClassName, JvmClassMappingKt.getJavaClass(type).getClassLoader());
            final Method mainMethod = ReflectionUtils.findMethod(ktClass, "main", String[].class);
            Assert.notNull(mainMethod, "Unable to find main method");
            SpringApplication.Augmented from = SpringApplication.from(new ThrowingConsumer() { // from class: org.springframework.boot.SpringApplicationExtensionsKt$fromApplication$1
                @Override // org.springframework.util.function.ThrowingConsumer
                public final void acceptWithException(String[] it) {
                    Method method = mainMethod;
                    Intrinsics.checkNotNull(method);
                    ReflectionUtils.invokeMethod(method, null, it);
                }
            });
            Intrinsics.checkNotNullExpressionValue(from, "mainMethod = ReflectionU…mainMethod!!, null, it) }");
            return from;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to use 'fromApplication' with " + type.getQualifiedName());
        }
    }

    @NotNull
    public static final SpringApplication.Augmented with(@NotNull SpringApplication.Augmented $this$with, @NotNull KClass<?>... kClassArr) {
        Intrinsics.checkNotNullParameter($this$with, "<this>");
        Intrinsics.checkNotNullParameter(kClassArr, "types");
        Collection destination$iv$iv = new ArrayList(kClassArr.length);
        for (KClass<?> kClass : kClassArr) {
            destination$iv$iv.add(JvmClassMappingKt.getJavaClass(kClass));
        }
        Collection $this$toTypedArray$iv = (List) destination$iv$iv;
        Class[] clsArr = (Class[]) $this$toTypedArray$iv.toArray(new Class[0]);
        SpringApplication.Augmented with = $this$with.with((Class[]) Arrays.copyOf(clsArr, clsArr.length));
        Intrinsics.checkNotNull(with);
        return with;
    }
}
