package org.springframework.aot.hint.predicate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.springframework.aot.hint.ExecutableHint;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.TypeHint;
import org.springframework.aot.hint.TypeReference;
import org.springframework.cglib.core.Constants;
import org.springframework.core.MethodIntrospector;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/predicate/ReflectionHintsPredicates.class */
public class ReflectionHintsPredicates {
    public TypeHintPredicate onType(TypeReference typeReference) {
        Assert.notNull(typeReference, "'typeReference' must not be null");
        return new TypeHintPredicate(typeReference);
    }

    public TypeHintPredicate onType(Class<?> type) {
        Assert.notNull(type, "'type' must not be null");
        return new TypeHintPredicate(TypeReference.of(type));
    }

    public ConstructorHintPredicate onConstructor(Constructor<?> constructor) {
        Assert.notNull(constructor, "'constructor' must not be null");
        return new ConstructorHintPredicate(constructor);
    }

    public MethodHintPredicate onMethod(Method method) {
        Assert.notNull(method, "'method' must not be null");
        return new MethodHintPredicate(method);
    }

    public MethodHintPredicate onMethod(Class<?> type, String methodName) {
        Assert.notNull(type, "'type' must not be null");
        Assert.hasText(methodName, "'methodName' must not be empty");
        return new MethodHintPredicate(getMethod(type, methodName));
    }

    public MethodHintPredicate onMethod(String className, String methodName) throws ClassNotFoundException {
        Assert.hasText(className, "'className' must not be empty");
        Assert.hasText(methodName, "'methodName' must not be empty");
        return onMethod(Class.forName(className), methodName);
    }

    private Method getMethod(Class<?> type, String methodName) {
        ReflectionUtils.MethodFilter selector = method -> {
            return methodName.equals(method.getName());
        };
        Set<Method> methods = MethodIntrospector.selectMethods(type, selector);
        if (methods.size() == 1) {
            return methods.iterator().next();
        }
        if (methods.size() > 1) {
            throw new IllegalArgumentException("Found multiple methods named '%s' on class %s".formatted(methodName, type.getName()));
        }
        throw new IllegalArgumentException("No method named '%s' on class %s".formatted(methodName, type.getName()));
    }

    public FieldHintPredicate onField(Class<?> type, String fieldName) {
        Assert.notNull(type, "'type' must not be null");
        Assert.hasText(fieldName, "'fieldName' must not be empty");
        Field field = ReflectionUtils.findField(type, fieldName);
        if (field == null) {
            throw new IllegalArgumentException("No field named '%s' on class %s".formatted(fieldName, type.getName()));
        }
        return new FieldHintPredicate(field);
    }

    public FieldHintPredicate onField(String className, String fieldName) throws ClassNotFoundException {
        Assert.hasText(className, "'className' must not be empty");
        Assert.hasText(fieldName, "'fieldName' must not be empty");
        return onField(Class.forName(className), fieldName);
    }

    public FieldHintPredicate onField(Field field) {
        Assert.notNull(field, "'field' must not be null");
        return new FieldHintPredicate(field);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/predicate/ReflectionHintsPredicates$TypeHintPredicate.class */
    public static class TypeHintPredicate implements Predicate<RuntimeHints> {
        private final TypeReference type;

        TypeHintPredicate(TypeReference type) {
            this.type = type;
        }

        @Nullable
        private TypeHint getTypeHint(RuntimeHints hints) {
            return hints.reflection().getTypeHint(this.type);
        }

        @Override // java.util.function.Predicate
        public boolean test(RuntimeHints hints) {
            return getTypeHint(hints) != null;
        }

        public Predicate<RuntimeHints> withMemberCategory(MemberCategory memberCategory) {
            Assert.notNull(memberCategory, "'memberCategory' must not be null");
            return and(hints -> {
                return getTypeHint(hints).getMemberCategories().contains(memberCategory);
            });
        }

        public Predicate<RuntimeHints> withMemberCategories(MemberCategory... memberCategories) {
            Assert.notEmpty(memberCategories, "'memberCategories' must not be empty");
            return and(hints -> {
                return getTypeHint(hints).getMemberCategories().containsAll(Arrays.asList(memberCategories));
            });
        }

        public Predicate<RuntimeHints> withAnyMemberCategory(MemberCategory... memberCategories) {
            Assert.notEmpty(memberCategories, "'memberCategories' must not be empty");
            return and(hints -> {
                return Arrays.stream(memberCategories).anyMatch(memberCategory -> {
                    return getTypeHint(hints).getMemberCategories().contains(memberCategory);
                });
            });
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/predicate/ReflectionHintsPredicates$ExecutableHintPredicate.class */
    public static abstract class ExecutableHintPredicate<T extends Executable> implements Predicate<RuntimeHints> {
        protected final T executable;
        protected ExecutableMode executableMode = ExecutableMode.INTROSPECT;

        abstract Predicate<RuntimeHints> exactMatch();

        ExecutableHintPredicate(T executable) {
            this.executable = executable;
        }

        public ExecutableHintPredicate<T> introspect() {
            this.executableMode = ExecutableMode.INTROSPECT;
            return this;
        }

        public ExecutableHintPredicate<T> invoke() {
            this.executableMode = ExecutableMode.INVOKE;
            return this;
        }

        static boolean includes(ExecutableHint hint, String name, List<TypeReference> parameterTypes, ExecutableMode executableModes) {
            return hint.getName().equals(name) && hint.getParameterTypes().equals(parameterTypes) && (hint.getMode().equals(ExecutableMode.INVOKE) || !executableModes.equals(ExecutableMode.INVOKE));
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/predicate/ReflectionHintsPredicates$ConstructorHintPredicate.class */
    public static class ConstructorHintPredicate extends ExecutableHintPredicate<Constructor<?>> {
        ConstructorHintPredicate(Constructor<?> constructor) {
            super(constructor);
        }

        @Override // java.util.function.Predicate
        public boolean test(RuntimeHints runtimeHints) {
            return new TypeHintPredicate(TypeReference.of((Class<?>) ((Constructor) this.executable).getDeclaringClass())).withAnyMemberCategory(getPublicMemberCategories()).and(hints -> {
                return Modifier.isPublic(((Constructor) this.executable).getModifiers());
            }).or(new TypeHintPredicate(TypeReference.of((Class<?>) ((Constructor) this.executable).getDeclaringClass())).withAnyMemberCategory(getDeclaredMemberCategories())).or(exactMatch()).test(runtimeHints);
        }

        MemberCategory[] getPublicMemberCategories() {
            if (this.executableMode == ExecutableMode.INTROSPECT) {
                return new MemberCategory[]{MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS};
            }
            return new MemberCategory[]{MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS};
        }

        MemberCategory[] getDeclaredMemberCategories() {
            if (this.executableMode == ExecutableMode.INTROSPECT) {
                return new MemberCategory[]{MemberCategory.INTROSPECT_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS};
            }
            return new MemberCategory[]{MemberCategory.INVOKE_DECLARED_CONSTRUCTORS};
        }

        @Override // org.springframework.aot.hint.predicate.ReflectionHintsPredicates.ExecutableHintPredicate
        Predicate<RuntimeHints> exactMatch() {
            return hints -> {
                return hints.reflection().getTypeHint(((Constructor) this.executable).getDeclaringClass()) != null && hints.reflection().getTypeHint(((Constructor) this.executable).getDeclaringClass()).constructors().anyMatch(executableHint -> {
                    List<TypeReference> parameters = TypeReference.listOf(((Constructor) this.executable).getParameterTypes());
                    return includes(executableHint, Constants.CONSTRUCTOR_NAME, parameters, this.executableMode);
                });
            };
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/predicate/ReflectionHintsPredicates$MethodHintPredicate.class */
    public static class MethodHintPredicate extends ExecutableHintPredicate<Method> {
        MethodHintPredicate(Method method) {
            super(method);
        }

        @Override // java.util.function.Predicate
        public boolean test(RuntimeHints runtimeHints) {
            return new TypeHintPredicate(TypeReference.of(((Method) this.executable).getDeclaringClass())).withAnyMemberCategory(getPublicMemberCategories()).and(hints -> {
                return Modifier.isPublic(((Method) this.executable).getModifiers());
            }).or(new TypeHintPredicate(TypeReference.of(((Method) this.executable).getDeclaringClass())).withAnyMemberCategory(getDeclaredMemberCategories()).and(hints2 -> {
                return !Modifier.isPublic(((Method) this.executable).getModifiers());
            })).or(exactMatch()).test(runtimeHints);
        }

        MemberCategory[] getPublicMemberCategories() {
            if (this.executableMode == ExecutableMode.INTROSPECT) {
                return new MemberCategory[]{MemberCategory.INTROSPECT_PUBLIC_METHODS, MemberCategory.INVOKE_PUBLIC_METHODS};
            }
            return new MemberCategory[]{MemberCategory.INVOKE_PUBLIC_METHODS};
        }

        MemberCategory[] getDeclaredMemberCategories() {
            if (this.executableMode == ExecutableMode.INTROSPECT) {
                return new MemberCategory[]{MemberCategory.INTROSPECT_DECLARED_METHODS, MemberCategory.INVOKE_DECLARED_METHODS};
            }
            return new MemberCategory[]{MemberCategory.INVOKE_DECLARED_METHODS};
        }

        @Override // org.springframework.aot.hint.predicate.ReflectionHintsPredicates.ExecutableHintPredicate
        Predicate<RuntimeHints> exactMatch() {
            return hints -> {
                return hints.reflection().getTypeHint(((Method) this.executable).getDeclaringClass()) != null && hints.reflection().getTypeHint(((Method) this.executable).getDeclaringClass()).methods().anyMatch(executableHint -> {
                    List<TypeReference> parameters = TypeReference.listOf(((Method) this.executable).getParameterTypes());
                    return includes(executableHint, ((Method) this.executable).getName(), parameters, this.executableMode);
                });
            };
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/predicate/ReflectionHintsPredicates$FieldHintPredicate.class */
    public static class FieldHintPredicate implements Predicate<RuntimeHints> {
        private final Field field;

        FieldHintPredicate(Field field) {
            this.field = field;
        }

        @Override // java.util.function.Predicate
        public boolean test(RuntimeHints runtimeHints) {
            TypeHint typeHint = runtimeHints.reflection().getTypeHint(this.field.getDeclaringClass());
            if (typeHint == null) {
                return false;
            }
            return memberCategoryMatch(typeHint) || exactMatch(typeHint);
        }

        private boolean memberCategoryMatch(TypeHint typeHint) {
            if (Modifier.isPublic(this.field.getModifiers())) {
                return typeHint.getMemberCategories().contains(MemberCategory.PUBLIC_FIELDS);
            }
            return typeHint.getMemberCategories().contains(MemberCategory.DECLARED_FIELDS);
        }

        private boolean exactMatch(TypeHint typeHint) {
            return typeHint.fields().anyMatch(fieldHint -> {
                return this.field.getName().equals(fieldHint.getName());
            });
        }
    }
}
