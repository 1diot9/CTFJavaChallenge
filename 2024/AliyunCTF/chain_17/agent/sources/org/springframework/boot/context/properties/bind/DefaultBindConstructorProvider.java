package org.springframework.boot.context.properties.bind;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.KotlinDetector;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/DefaultBindConstructorProvider.class */
class DefaultBindConstructorProvider implements BindConstructorProvider {
    @Override // org.springframework.boot.context.properties.bind.BindConstructorProvider
    public Constructor<?> getBindConstructor(Bindable<?> bindable, boolean isNestedConstructorBinding) {
        Constructors constructors = Constructors.getConstructors(bindable.getType().resolve(), isNestedConstructorBinding);
        if (constructors.getBind() != null && constructors.isDeducedBindConstructor() && !constructors.isImmutableType() && bindable.getValue() != null && bindable.getValue().get() != null) {
            return null;
        }
        return constructors.getBind();
    }

    @Override // org.springframework.boot.context.properties.bind.BindConstructorProvider
    public Constructor<?> getBindConstructor(Class<?> type, boolean isNestedConstructorBinding) {
        Constructors constructors = Constructors.getConstructors(type, isNestedConstructorBinding);
        return constructors.getBind();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/DefaultBindConstructorProvider$Constructors.class */
    static final class Constructors {
        private static final Constructors NONE = new Constructors(false, null, false, false);
        private final boolean hasAutowired;
        private final Constructor<?> bind;
        private final boolean deducedBindConstructor;
        private final boolean immutableType;

        private Constructors(boolean hasAutowired, Constructor<?> bind, boolean deducedBindConstructor, boolean immutableType) {
            this.hasAutowired = hasAutowired;
            this.bind = bind;
            this.deducedBindConstructor = deducedBindConstructor;
            this.immutableType = immutableType;
        }

        boolean hasAutowired() {
            return this.hasAutowired;
        }

        Constructor<?> getBind() {
            return this.bind;
        }

        boolean isDeducedBindConstructor() {
            return this.deducedBindConstructor;
        }

        boolean isImmutableType() {
            return this.immutableType;
        }

        static Constructors getConstructors(Class<?> type, boolean isNestedConstructorBinding) {
            if (type == null) {
                return NONE;
            }
            boolean hasAutowiredConstructor = isAutowiredPresent(type);
            Constructor<?>[] candidates = getCandidateConstructors(type);
            MergedAnnotations[] candidateAnnotations = getAnnotations(candidates);
            boolean deducedBindConstructor = false;
            boolean immutableType = type.isRecord();
            Constructor<?> bind = getConstructorBindingAnnotated(type, candidates, candidateAnnotations);
            if (bind == null && !hasAutowiredConstructor) {
                bind = deduceBindConstructor(type, candidates);
                deducedBindConstructor = bind != null;
            }
            if (bind == null && !hasAutowiredConstructor && isKotlinType(type)) {
                bind = deduceKotlinBindConstructor(type);
                deducedBindConstructor = bind != null;
            }
            if (bind != null || isNestedConstructorBinding) {
                Assert.state(!hasAutowiredConstructor, (Supplier<String>) () -> {
                    return type.getName() + " declares @ConstructorBinding and @Autowired constructor";
                });
            }
            return new Constructors(hasAutowiredConstructor, bind, deducedBindConstructor, immutableType);
        }

        private static boolean isAutowiredPresent(Class<?> type) {
            if (Stream.of((Object[]) type.getDeclaredConstructors()).map((v0) -> {
                return MergedAnnotations.from(v0);
            }).anyMatch(annotations -> {
                return annotations.isPresent(Autowired.class);
            })) {
                return true;
            }
            Class<?> userClass = ClassUtils.getUserClass(type);
            if (userClass != type) {
                return isAutowiredPresent(userClass);
            }
            return false;
        }

        private static Constructor<?>[] getCandidateConstructors(Class<?> type) {
            if (isInnerClass(type)) {
                return new Constructor[0];
            }
            return (Constructor[]) Arrays.stream(type.getDeclaredConstructors()).filter(Constructors::isNonSynthetic).toArray(x$0 -> {
                return new Constructor[x$0];
            });
        }

        private static boolean isInnerClass(Class<?> type) {
            try {
                return type.getDeclaredField("this$0").isSynthetic();
            } catch (NoSuchFieldException e) {
                return false;
            }
        }

        private static boolean isNonSynthetic(Constructor<?> constructor) {
            return !constructor.isSynthetic();
        }

        private static MergedAnnotations[] getAnnotations(Constructor<?>[] candidates) {
            MergedAnnotations[] candidateAnnotations = new MergedAnnotations[candidates.length];
            for (int i = 0; i < candidates.length; i++) {
                candidateAnnotations[i] = MergedAnnotations.from(candidates[i], MergedAnnotations.SearchStrategy.SUPERCLASS);
            }
            return candidateAnnotations;
        }

        private static Constructor<?> getConstructorBindingAnnotated(Class<?> type, Constructor<?>[] candidates, MergedAnnotations[] mergedAnnotations) {
            Constructor<?> result = null;
            for (int i = 0; i < candidates.length; i++) {
                if (mergedAnnotations[i].isPresent(ConstructorBinding.class)) {
                    Assert.state(candidates[i].getParameterCount() > 0, (Supplier<String>) () -> {
                        return type.getName() + " declares @ConstructorBinding on a no-args constructor";
                    });
                    Assert.state(result == null, (Supplier<String>) () -> {
                        return type.getName() + " has more than one @ConstructorBinding constructor";
                    });
                    result = candidates[i];
                }
            }
            return result;
        }

        private static Constructor<?> deduceBindConstructor(Class<?> type, Constructor<?>[] candidates) {
            if (candidates.length == 1 && candidates[0].getParameterCount() > 0) {
                if (type.isMemberClass() && Modifier.isPrivate(candidates[0].getModifiers())) {
                    return null;
                }
                return candidates[0];
            }
            Constructor<?> result = null;
            for (Constructor<?> candidate : candidates) {
                if (!Modifier.isPrivate(candidate.getModifiers())) {
                    if (result != null) {
                        return null;
                    }
                    result = candidate;
                }
            }
            if (result == null || result.getParameterCount() <= 0) {
                return null;
            }
            return result;
        }

        private static boolean isKotlinType(Class<?> type) {
            return KotlinDetector.isKotlinPresent() && KotlinDetector.isKotlinType(type);
        }

        private static Constructor<?> deduceKotlinBindConstructor(Class<?> type) {
            Constructor<?> primaryConstructor = BeanUtils.findPrimaryConstructor(type);
            if (primaryConstructor != null && primaryConstructor.getParameterCount() > 0) {
                return primaryConstructor;
            }
            return null;
        }
    }
}
