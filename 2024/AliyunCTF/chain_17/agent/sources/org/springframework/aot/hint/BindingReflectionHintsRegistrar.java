package org.springframework.aot.hint;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/BindingReflectionHintsRegistrar.class */
public class BindingReflectionHintsRegistrar {
    private static final String KOTLIN_COMPANION_SUFFIX = "$Companion";
    private static final String JACKSON_ANNOTATION = "com.fasterxml.jackson.annotation.JacksonAnnotation";
    private static final boolean jacksonAnnotationPresent = ClassUtils.isPresent(JACKSON_ANNOTATION, BindingReflectionHintsRegistrar.class.getClassLoader());

    public void registerReflectionHints(ReflectionHints hints, Type... types) {
        Set<Type> seen = new LinkedHashSet<>();
        for (Type type : types) {
            registerReflectionHints(hints, seen, type);
        }
    }

    private boolean shouldSkipType(Class<?> type) {
        return type.isPrimitive() || type == Object.class;
    }

    private boolean shouldSkipMembers(Class<?> type) {
        return type.getCanonicalName().startsWith("java.") || type.isArray();
    }

    private void registerReflectionHints(ReflectionHints hints, Set<Type> seen, Type type) {
        if (seen.contains(type)) {
            return;
        }
        seen.add(type);
        if (type instanceof Class) {
            Class<?> clazz = (Class) type;
            if (shouldSkipType(clazz)) {
                return;
            } else {
                hints.registerType(clazz, typeHint -> {
                    if (!shouldSkipMembers(clazz)) {
                        if (clazz.isRecord()) {
                            typeHint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
                            for (RecordComponent recordComponent : clazz.getRecordComponents()) {
                                registerRecordHints(hints, seen, recordComponent.getAccessor());
                            }
                        }
                        if (clazz.isEnum()) {
                            typeHint.withMembers(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS);
                        }
                        typeHint.withMembers(MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
                        for (Method method : clazz.getMethods()) {
                            String methodName = method.getName();
                            if (methodName.startsWith("set") && method.getParameterCount() == 1) {
                                registerPropertyHints(hints, seen, method, 0);
                            } else if ((methodName.startsWith(BeanUtil.PREFIX_GETTER_GET) && method.getParameterCount() == 0 && method.getReturnType() != Void.TYPE) || (methodName.startsWith(BeanUtil.PREFIX_GETTER_IS) && method.getParameterCount() == 0 && method.getReturnType() == Boolean.TYPE)) {
                                registerPropertyHints(hints, seen, method, -1);
                            }
                        }
                        if (jacksonAnnotationPresent) {
                            registerJacksonHints(hints, clazz);
                        }
                    }
                    if (KotlinDetector.isKotlinType(clazz)) {
                        KotlinDelegate.registerComponentHints(hints, clazz);
                        registerKotlinSerializationHints(hints, clazz);
                        typeHint.withMembers(MemberCategory.INTROSPECT_DECLARED_METHODS);
                    }
                });
            }
        }
        Set<Class<?>> referencedTypes = new LinkedHashSet<>();
        collectReferencedTypes(referencedTypes, ResolvableType.forType(type));
        referencedTypes.forEach(referencedType -> {
            registerReflectionHints(hints, seen, referencedType);
        });
    }

    private void registerRecordHints(ReflectionHints hints, Set<Type> seen, Method method) {
        hints.registerMethod(method, ExecutableMode.INVOKE);
        MethodParameter methodParameter = MethodParameter.forExecutable(method, -1);
        Type methodParameterType = methodParameter.getGenericParameterType();
        registerReflectionHints(hints, seen, methodParameterType);
    }

    private void registerPropertyHints(ReflectionHints hints, Set<Type> seen, @Nullable Method method, int parameterIndex) {
        if (method != null && method.getDeclaringClass() != Object.class && method.getDeclaringClass() != Enum.class) {
            hints.registerMethod(method, ExecutableMode.INVOKE);
            MethodParameter methodParameter = MethodParameter.forExecutable(method, parameterIndex);
            Type methodParameterType = methodParameter.getGenericParameterType();
            registerReflectionHints(hints, seen, methodParameterType);
        }
    }

    private void registerKotlinSerializationHints(ReflectionHints hints, Class<?> clazz) {
        String companionClassName = clazz.getCanonicalName() + "$Companion";
        if (ClassUtils.isPresent(companionClassName, null)) {
            Class<?> companionClass = ClassUtils.resolveClassName(companionClassName, null);
            Method serializerMethod = ClassUtils.getMethodIfAvailable(companionClass, "serializer", new Class[0]);
            if (serializerMethod != null) {
                hints.registerMethod(serializerMethod, ExecutableMode.INVOKE);
            }
        }
    }

    private void collectReferencedTypes(Set<Class<?>> types, ResolvableType resolvableType) {
        Class<?> clazz = resolvableType.resolve();
        if (clazz != null && !types.contains(clazz)) {
            types.add(clazz);
            for (ResolvableType genericResolvableType : resolvableType.getGenerics()) {
                collectReferencedTypes(types, genericResolvableType);
            }
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && superClass != Object.class && superClass != Record.class && superClass != Enum.class) {
                types.add(superClass);
            }
        }
    }

    private void registerJacksonHints(ReflectionHints hints, Class<?> clazz) {
        ReflectionUtils.doWithFields(clazz, field -> {
            forEachJacksonAnnotation(field, annotation -> {
                Field sourceField = (Field) annotation.getSource();
                if (sourceField != null) {
                    hints.registerField(sourceField);
                }
                registerHintsForClassAttributes(hints, annotation);
            });
        });
        ReflectionUtils.doWithMethods(clazz, method -> {
            forEachJacksonAnnotation(method, annotation -> {
                Method sourceMethod = (Method) annotation.getSource();
                if (sourceMethod != null) {
                    hints.registerMethod(sourceMethod, ExecutableMode.INVOKE);
                }
                registerHintsForClassAttributes(hints, annotation);
            });
        });
        forEachJacksonAnnotation(clazz, annotation -> {
            registerHintsForClassAttributes(hints, annotation);
        });
    }

    private void forEachJacksonAnnotation(AnnotatedElement element, Consumer<MergedAnnotation<Annotation>> action) {
        MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).stream(JACKSON_ANNOTATION).filter((v0) -> {
            return v0.isMetaPresent();
        }).forEach(action);
    }

    private void registerHintsForClassAttributes(ReflectionHints hints, MergedAnnotation<Annotation> annotation) {
        annotation.getRoot().asMap(new MergedAnnotation.Adapt[0]).values().forEach(value -> {
            if (value instanceof Class) {
                Class<?> classValue = (Class) value;
                if (value != Void.class) {
                    hints.registerType(classValue, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
                }
            }
        });
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/BindingReflectionHintsRegistrar$KotlinDelegate.class */
    private static class KotlinDelegate {
        private KotlinDelegate() {
        }

        public static void registerComponentHints(ReflectionHints hints, Class<?> type) {
            KClass<?> kClass = JvmClassMappingKt.getKotlinClass(type);
            if (kClass.isData()) {
                for (Method method : type.getMethods()) {
                    String methodName = method.getName();
                    if (methodName.startsWith("component") || methodName.equals("copy") || methodName.equals("copy$default")) {
                        hints.registerMethod(method, ExecutableMode.INVOKE);
                    }
                }
            }
        }
    }
}
