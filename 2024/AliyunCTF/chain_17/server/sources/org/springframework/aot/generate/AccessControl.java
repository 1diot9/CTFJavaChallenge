package org.springframework.aot.generate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.IntFunction;
import org.springframework.core.ResolvableType;
import org.springframework.javapoet.ClassName;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/AccessControl.class */
public final class AccessControl {
    private final Class<?> target;
    private final Visibility visibility;

    AccessControl(Class<?> target, Visibility visibility) {
        this.target = target;
        this.visibility = visibility;
    }

    public static AccessControl forMember(Member member) {
        return new AccessControl(member.getDeclaringClass(), Visibility.forMember(member));
    }

    public static AccessControl forResolvableType(ResolvableType resolvableType) {
        return new AccessControl(resolvableType.toClass(), Visibility.forResolvableType(resolvableType));
    }

    public static AccessControl forClass(Class<?> type) {
        return new AccessControl(type, Visibility.forClass(type));
    }

    public static AccessControl lowest(AccessControl... candidates) {
        int index = Visibility.lowestIndex((Visibility[]) Arrays.stream(candidates).map((v0) -> {
            return v0.getVisibility();
        }).toArray(x$0 -> {
            return new Visibility[x$0];
        }));
        return candidates[index];
    }

    public Visibility getVisibility() {
        return this.visibility;
    }

    public boolean isPublic() {
        return this.visibility == Visibility.PUBLIC;
    }

    public boolean isAccessibleFrom(ClassName type) {
        if (this.visibility == Visibility.PRIVATE) {
            return false;
        }
        if (this.visibility == Visibility.PUBLIC) {
            return true;
        }
        return this.target.getPackageName().equals(type.packageName());
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/AccessControl$Visibility.class */
    public enum Visibility {
        PUBLIC,
        PROTECTED,
        PACKAGE_PRIVATE,
        PRIVATE;

        private static Visibility forMember(Member member) {
            Assert.notNull(member, "'member' must not be null");
            Visibility visibility = forModifiers(member.getModifiers());
            Visibility declaringClassVisibility = forClass(member.getDeclaringClass());
            Visibility visibility2 = lowest(visibility, declaringClassVisibility);
            if (visibility2 != PRIVATE) {
                if (member instanceof Field) {
                    Field field = (Field) member;
                    Visibility fieldVisibility = forResolvableType(ResolvableType.forField(field));
                    return lowest(visibility2, fieldVisibility);
                }
                if (member instanceof Constructor) {
                    Constructor<?> constructor = (Constructor) member;
                    Visibility parameterVisibility = forParameterTypes(constructor, i -> {
                        return ResolvableType.forConstructorParameter(constructor, i);
                    });
                    return lowest(visibility2, parameterVisibility);
                }
                if (member instanceof Method) {
                    Method method = (Method) member;
                    Visibility parameterVisibility2 = forParameterTypes(method, i2 -> {
                        return ResolvableType.forMethodParameter(method, i2);
                    });
                    Visibility returnTypeVisibility = forResolvableType(ResolvableType.forMethodReturnType(method));
                    return lowest(visibility2, parameterVisibility2, returnTypeVisibility);
                }
            }
            return PRIVATE;
        }

        private static Visibility forResolvableType(ResolvableType resolvableType) {
            return forResolvableType(resolvableType, new HashSet());
        }

        private static Visibility forResolvableType(ResolvableType resolvableType, Set<ResolvableType> seen) {
            if (!seen.add(resolvableType)) {
                return PUBLIC;
            }
            Class<?> userClass = ClassUtils.getUserClass(resolvableType.toClass());
            ResolvableType userType = resolvableType.as(userClass);
            Visibility visibility = forClass(userType.toClass());
            for (ResolvableType generic : userType.getGenerics()) {
                visibility = lowest(visibility, forResolvableType(generic, seen));
            }
            return visibility;
        }

        private static Visibility forParameterTypes(Executable executable, IntFunction<ResolvableType> resolvableTypeFactory) {
            Visibility visibility = PUBLIC;
            Class<?>[] parameterTypes = executable.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                ResolvableType type = resolvableTypeFactory.apply(i);
                visibility = lowest(visibility, forResolvableType(type));
            }
            return visibility;
        }

        private static Visibility forClass(Class<?> clazz) {
            Class<?> clazz2 = ClassUtils.getUserClass(clazz);
            Visibility visibility = forModifiers(clazz2.getModifiers());
            if (clazz2.isArray()) {
                visibility = lowest(visibility, forClass(clazz2.componentType()));
            }
            Class<?> enclosingClass = clazz2.getEnclosingClass();
            if (enclosingClass != null) {
                visibility = lowest(visibility, forClass(clazz2.getEnclosingClass()));
            }
            return visibility;
        }

        private static Visibility forModifiers(int modifiers) {
            if (Modifier.isPublic(modifiers)) {
                return PUBLIC;
            }
            if (Modifier.isProtected(modifiers)) {
                return PROTECTED;
            }
            if (Modifier.isPrivate(modifiers)) {
                return PRIVATE;
            }
            return PACKAGE_PRIVATE;
        }

        static Visibility lowest(Visibility... candidates) {
            Visibility visibility = PUBLIC;
            for (Visibility candidate : candidates) {
                if (candidate.ordinal() > visibility.ordinal()) {
                    visibility = candidate;
                }
            }
            return visibility;
        }

        static int lowestIndex(Visibility... candidates) {
            Visibility visibility = PUBLIC;
            int index = 0;
            for (int i = 0; i < candidates.length; i++) {
                Visibility candidate = candidates[i];
                if (candidate.ordinal() > visibility.ordinal()) {
                    visibility = candidate;
                    index = i;
                }
            }
            return index;
        }
    }
}
