package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.springframework.core.MethodParameter;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/SynthesizingMethodParameter.class */
public class SynthesizingMethodParameter extends MethodParameter {
    public SynthesizingMethodParameter(Method method, int parameterIndex) {
        super(method, parameterIndex);
    }

    public SynthesizingMethodParameter(Method method, int parameterIndex, int nestingLevel) {
        super(method, parameterIndex, nestingLevel);
    }

    public SynthesizingMethodParameter(Constructor<?> constructor, int parameterIndex) {
        super(constructor, parameterIndex);
    }

    public SynthesizingMethodParameter(Constructor<?> constructor, int parameterIndex, int nestingLevel) {
        super(constructor, parameterIndex, nestingLevel);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SynthesizingMethodParameter(SynthesizingMethodParameter original) {
        super(original);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.core.MethodParameter
    public <A extends Annotation> A adaptAnnotation(A a) {
        return (A) AnnotationUtils.synthesizeAnnotation(a, getAnnotatedElement());
    }

    @Override // org.springframework.core.MethodParameter
    protected Annotation[] adaptAnnotationArray(Annotation[] annotations) {
        return AnnotationUtils.synthesizeAnnotationArray(annotations, getAnnotatedElement());
    }

    @Override // org.springframework.core.MethodParameter
    /* renamed from: clone */
    public SynthesizingMethodParameter mo2452clone() {
        return new SynthesizingMethodParameter(this);
    }

    public static SynthesizingMethodParameter forExecutable(Executable executable, int parameterIndex) {
        if (executable instanceof Method) {
            Method method = (Method) executable;
            return new SynthesizingMethodParameter(method, parameterIndex);
        }
        if (executable instanceof Constructor) {
            Constructor<?> constructor = (Constructor) executable;
            return new SynthesizingMethodParameter(constructor, parameterIndex);
        }
        throw new IllegalArgumentException("Not a Method/Constructor: " + executable);
    }

    public static SynthesizingMethodParameter forParameter(Parameter parameter) {
        return forExecutable(parameter.getDeclaringExecutable(), findParameterIndex(parameter));
    }
}
