package org.springframework.aot.hint;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.springframework.aot.hint.TypeHint;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/ReflectionHints.class */
public class ReflectionHints {
    private final Map<TypeReference, TypeHint.Builder> types = new HashMap();

    public Stream<TypeHint> typeHints() {
        return this.types.values().stream().map((v0) -> {
            return v0.build();
        });
    }

    @Nullable
    public TypeHint getTypeHint(TypeReference type) {
        TypeHint.Builder typeHintBuilder = this.types.get(type);
        if (typeHintBuilder != null) {
            return typeHintBuilder.build();
        }
        return null;
    }

    @Nullable
    public TypeHint getTypeHint(Class<?> type) {
        return getTypeHint(TypeReference.of(type));
    }

    public ReflectionHints registerType(TypeReference type, Consumer<TypeHint.Builder> typeHint) {
        TypeHint.Builder builder = this.types.computeIfAbsent(type, TypeHint.Builder::new);
        typeHint.accept(builder);
        return this;
    }

    public ReflectionHints registerType(TypeReference type, MemberCategory... memberCategories) {
        return registerType(type, TypeHint.builtWith(memberCategories));
    }

    public ReflectionHints registerType(Class<?> type, Consumer<TypeHint.Builder> typeHint) {
        Assert.notNull(type, "'type' must not be null");
        if (type.getCanonicalName() != null) {
            registerType(TypeReference.of(type), typeHint);
        }
        return this;
    }

    public ReflectionHints registerType(Class<?> type, MemberCategory... memberCategories) {
        Assert.notNull(type, "'type' must not be null");
        if (type.getCanonicalName() != null) {
            registerType(TypeReference.of(type), memberCategories);
        }
        return this;
    }

    public ReflectionHints registerTypeIfPresent(@Nullable ClassLoader classLoader, String typeName, Consumer<TypeHint.Builder> typeHint) {
        if (ClassUtils.isPresent(typeName, classLoader)) {
            registerType(TypeReference.of(typeName), typeHint);
        }
        return this;
    }

    public ReflectionHints registerTypeIfPresent(@Nullable ClassLoader classLoader, String typeName, MemberCategory... memberCategories) {
        return registerTypeIfPresent(classLoader, typeName, TypeHint.builtWith(memberCategories));
    }

    public ReflectionHints registerTypes(Iterable<TypeReference> types, Consumer<TypeHint.Builder> typeHint) {
        types.forEach(type -> {
            registerType(type, (Consumer<TypeHint.Builder>) typeHint);
        });
        return this;
    }

    public ReflectionHints registerField(Field field) {
        return registerType(TypeReference.of(field.getDeclaringClass()), typeHint -> {
            typeHint.withField(field.getName());
        });
    }

    public ReflectionHints registerConstructor(Constructor<?> constructor, ExecutableMode mode) {
        return registerType(TypeReference.of(constructor.getDeclaringClass()), typeHint -> {
            typeHint.withConstructor(mapParameters(constructor), mode);
        });
    }

    public ReflectionHints registerMethod(Method method, ExecutableMode mode) {
        return registerType(TypeReference.of(method.getDeclaringClass()), typeHint -> {
            typeHint.withMethod(method.getName(), mapParameters(method), mode);
        });
    }

    private List<TypeReference> mapParameters(Executable executable) {
        return TypeReference.listOf(executable.getParameterTypes());
    }
}
