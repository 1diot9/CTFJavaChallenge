package org.springframework.beans.factory.aot;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.springframework.javapoet.CodeBlock;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/AutowiredArgumentsCodeGenerator.class */
public class AutowiredArgumentsCodeGenerator {
    private final Class<?> target;
    private final Executable executable;

    public AutowiredArgumentsCodeGenerator(Class<?> target, Executable executable) {
        this.target = target;
        this.executable = executable;
    }

    public CodeBlock generateCode(Class<?>[] parameterTypes) {
        return generateCode(parameterTypes, 0, "args");
    }

    public CodeBlock generateCode(Class<?>[] parameterTypes, int startIndex) {
        return generateCode(parameterTypes, startIndex, "args");
    }

    public CodeBlock generateCode(Class<?>[] parameterTypes, int startIndex, String variableName) {
        Assert.notNull(parameterTypes, "'parameterTypes' must not be null");
        Assert.notNull(variableName, "'variableName' must not be null");
        boolean ambiguous = isAmbiguous();
        CodeBlock.Builder code = CodeBlock.builder();
        int i = startIndex;
        while (i < parameterTypes.length) {
            code.add(i != startIndex ? ", " : "", new Object[0]);
            if (!ambiguous) {
                code.add("$L.get($L)", variableName, Integer.valueOf(i - startIndex));
            } else {
                code.add("$L.get($L, $T.class)", variableName, Integer.valueOf(i - startIndex), parameterTypes[i]);
            }
            i++;
        }
        return code.build();
    }

    private boolean isAmbiguous() {
        Executable executable = this.executable;
        if (executable instanceof Constructor) {
            Constructor<?> constructor = (Constructor) executable;
            Stream stream = Arrays.stream(this.target.getDeclaredConstructors());
            Objects.requireNonNull(constructor);
            return stream.filter(Predicate.not((v1) -> {
                return r1.equals(v1);
            })).anyMatch((v1) -> {
                return hasSameParameterCount(v1);
            });
        }
        Executable executable2 = this.executable;
        if (executable2 instanceof Method) {
            Method method = (Method) executable2;
            Stream stream2 = Arrays.stream(ReflectionUtils.getAllDeclaredMethods(this.target));
            Objects.requireNonNull(method);
            return stream2.filter(Predicate.not((v1) -> {
                return r1.equals(v1);
            })).filter(candidate -> {
                return candidate.getName().equals(method.getName());
            }).anyMatch((v1) -> {
                return hasSameParameterCount(v1);
            });
        }
        return true;
    }

    private boolean hasSameParameterCount(Executable executable) {
        return this.executable.getParameterCount() == executable.getParameterCount();
    }
}
