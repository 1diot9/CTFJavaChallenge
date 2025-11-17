package org.springframework.aot.hint.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ReflectionHints;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/annotation/SimpleReflectiveProcessor.class */
public class SimpleReflectiveProcessor implements ReflectiveProcessor {
    @Override // org.springframework.aot.hint.annotation.ReflectiveProcessor
    public void registerReflectionHints(ReflectionHints hints, AnnotatedElement element) {
        if (element instanceof Class) {
            Class<?> type = (Class) element;
            registerTypeHint(hints, type);
            return;
        }
        if (element instanceof Constructor) {
            Constructor<?> constructor = (Constructor) element;
            registerConstructorHint(hints, constructor);
        } else if (element instanceof Field) {
            Field field = (Field) element;
            registerFieldHint(hints, field);
        } else if (element instanceof Method) {
            Method method = (Method) element;
            registerMethodHint(hints, method);
        }
    }

    protected void registerTypeHint(ReflectionHints hints, Class<?> type) {
        hints.registerType(type, new MemberCategory[0]);
    }

    protected void registerConstructorHint(ReflectionHints hints, Constructor<?> constructor) {
        hints.registerConstructor(constructor, ExecutableMode.INVOKE);
    }

    protected void registerFieldHint(ReflectionHints hints, Field field) {
        hints.registerField(field);
    }

    protected void registerMethodHint(ReflectionHints hints, Method method) {
        hints.registerMethod(method, ExecutableMode.INVOKE);
    }
}
