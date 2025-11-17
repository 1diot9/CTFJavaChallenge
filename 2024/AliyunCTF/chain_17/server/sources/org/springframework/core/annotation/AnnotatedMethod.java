package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/AnnotatedMethod.class */
public class AnnotatedMethod {
    private final Method method;
    private final Method bridgedMethod;
    private final MethodParameter[] parameters;

    @Nullable
    private volatile List<Annotation[][]> inheritedParameterAnnotations;

    public AnnotatedMethod(Method method) {
        Assert.notNull(method, "Method is required");
        this.method = method;
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
        ReflectionUtils.makeAccessible(this.bridgedMethod);
        this.parameters = initMethodParameters();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AnnotatedMethod(AnnotatedMethod annotatedMethod) {
        Assert.notNull(annotatedMethod, "AnnotatedMethod is required");
        this.method = annotatedMethod.method;
        this.bridgedMethod = annotatedMethod.bridgedMethod;
        this.parameters = annotatedMethod.parameters;
        this.inheritedParameterAnnotations = annotatedMethod.inheritedParameterAnnotations;
    }

    public final Method getMethod() {
        return this.method;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Method getBridgedMethod() {
        return this.bridgedMethod;
    }

    protected Class<?> getContainingClass() {
        return this.method.getDeclaringClass();
    }

    public final MethodParameter[] getMethodParameters() {
        return this.parameters;
    }

    private MethodParameter[] initMethodParameters() {
        int count = this.bridgedMethod.getParameterCount();
        MethodParameter[] result = new MethodParameter[count];
        for (int i = 0; i < count; i++) {
            result[i] = new AnnotatedMethodParameter(i);
        }
        return result;
    }

    public MethodParameter getReturnType() {
        return new AnnotatedMethodParameter(-1);
    }

    public MethodParameter getReturnValueType(@Nullable Object returnValue) {
        return new ReturnValueMethodParameter(returnValue);
    }

    public boolean isVoid() {
        return Void.TYPE.equals(getReturnType().getParameterType());
    }

    @Nullable
    public <A extends Annotation> A getMethodAnnotation(Class<A> cls) {
        return (A) AnnotatedElementUtils.findMergedAnnotation(this.method, cls);
    }

    public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
        return AnnotatedElementUtils.hasAnnotation(this.method, annotationType);
    }

    private List<Annotation[][]> getInheritedParameterAnnotations() {
        List<Annotation[][]> parameterAnnotations = this.inheritedParameterAnnotations;
        if (parameterAnnotations == null) {
            parameterAnnotations = new ArrayList();
            Class<?> clazz = this.method.getDeclaringClass();
            while (clazz != null) {
                for (Class<?> ifc : clazz.getInterfaces()) {
                    for (Method candidate : ifc.getMethods()) {
                        if (isOverrideFor(candidate)) {
                            parameterAnnotations.add(candidate.getParameterAnnotations());
                        }
                    }
                }
                clazz = clazz.getSuperclass();
                if (clazz == Object.class) {
                    clazz = null;
                }
                if (clazz != null) {
                    for (Method candidate2 : clazz.getMethods()) {
                        if (isOverrideFor(candidate2)) {
                            parameterAnnotations.add(candidate2.getParameterAnnotations());
                        }
                    }
                }
            }
            this.inheritedParameterAnnotations = parameterAnnotations;
        }
        return parameterAnnotations;
    }

    private boolean isOverrideFor(Method candidate) {
        if (!candidate.getName().equals(this.method.getName()) || candidate.getParameterCount() != this.method.getParameterCount()) {
            return false;
        }
        Class<?>[] paramTypes = this.method.getParameterTypes();
        if (Arrays.equals(candidate.getParameterTypes(), paramTypes)) {
            return true;
        }
        for (int i = 0; i < paramTypes.length; i++) {
            if (paramTypes[i] != ResolvableType.forMethodParameter(candidate, i, this.method.getDeclaringClass()).resolve()) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(@Nullable Object other) {
        return this == other || (other != null && getClass() == other.getClass() && this.method.equals(((AnnotatedMethod) other).method));
    }

    public int hashCode() {
        return this.method.hashCode();
    }

    public String toString() {
        return this.method.toGenericString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public static Object findProvidedArgument(MethodParameter parameter, @Nullable Object... providedArgs) {
        if (!ObjectUtils.isEmpty(providedArgs)) {
            for (Object providedArg : providedArgs) {
                if (parameter.getParameterType().isInstance(providedArg)) {
                    return providedArg;
                }
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String formatArgumentError(MethodParameter param, String message) {
        return "Could not resolve parameter [" + param.getParameterIndex() + "] in " + param.getExecutable().toGenericString() + (StringUtils.hasText(message) ? ": " + message : "");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/AnnotatedMethod$AnnotatedMethodParameter.class */
    public class AnnotatedMethodParameter extends SynthesizingMethodParameter {

        @Nullable
        private volatile Annotation[] combinedAnnotations;

        public AnnotatedMethodParameter(int index) {
            super(AnnotatedMethod.this.getBridgedMethod(), index);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public AnnotatedMethodParameter(AnnotatedMethodParameter original) {
            super(original);
            this.combinedAnnotations = original.combinedAnnotations;
        }

        @Override // org.springframework.core.MethodParameter
        @NonNull
        public Method getMethod() {
            return AnnotatedMethod.this.getBridgedMethod();
        }

        @Override // org.springframework.core.MethodParameter
        public Class<?> getContainingClass() {
            return AnnotatedMethod.this.getContainingClass();
        }

        @Override // org.springframework.core.MethodParameter
        public <T extends Annotation> T getMethodAnnotation(Class<T> cls) {
            return (T) AnnotatedMethod.this.getMethodAnnotation(cls);
        }

        @Override // org.springframework.core.MethodParameter
        public <T extends Annotation> boolean hasMethodAnnotation(Class<T> annotationType) {
            return AnnotatedMethod.this.hasMethodAnnotation(annotationType);
        }

        @Override // org.springframework.core.MethodParameter
        public Annotation[] getParameterAnnotations() {
            Annotation[] anns = this.combinedAnnotations;
            if (anns == null) {
                anns = super.getParameterAnnotations();
                int index = getParameterIndex();
                if (index >= 0) {
                    for (Annotation[][] ifcAnns : AnnotatedMethod.this.getInheritedParameterAnnotations()) {
                        if (index < ifcAnns.length) {
                            Annotation[] paramAnns = ifcAnns[index];
                            if (paramAnns.length > 0) {
                                List<Annotation> merged = new ArrayList<>(anns.length + paramAnns.length);
                                merged.addAll(Arrays.asList(anns));
                                for (Annotation paramAnn : paramAnns) {
                                    boolean existingType = false;
                                    Annotation[] annotationArr = anns;
                                    int length = annotationArr.length;
                                    int i = 0;
                                    while (true) {
                                        if (i >= length) {
                                            break;
                                        }
                                        Annotation ann = annotationArr[i];
                                        if (ann.annotationType() != paramAnn.annotationType()) {
                                            i++;
                                        } else {
                                            existingType = true;
                                            break;
                                        }
                                    }
                                    if (!existingType) {
                                        merged.add(adaptAnnotation(paramAnn));
                                    }
                                }
                                anns = (Annotation[]) merged.toArray(new Annotation[0]);
                            }
                        }
                    }
                }
                this.combinedAnnotations = anns;
            }
            return anns;
        }

        @Override // org.springframework.core.annotation.SynthesizingMethodParameter, org.springframework.core.MethodParameter
        /* renamed from: clone */
        public AnnotatedMethodParameter mo2452clone() {
            return new AnnotatedMethodParameter(this);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/AnnotatedMethod$ReturnValueMethodParameter.class */
    private class ReturnValueMethodParameter extends AnnotatedMethodParameter {

        @Nullable
        private final Class<?> returnValueType;

        public ReturnValueMethodParameter(@Nullable Object returnValue) {
            super(-1);
            this.returnValueType = returnValue != null ? returnValue.getClass() : null;
        }

        protected ReturnValueMethodParameter(ReturnValueMethodParameter original) {
            super(original);
            this.returnValueType = original.returnValueType;
        }

        @Override // org.springframework.core.MethodParameter
        public Class<?> getParameterType() {
            return this.returnValueType != null ? this.returnValueType : super.getParameterType();
        }

        @Override // org.springframework.core.annotation.AnnotatedMethod.AnnotatedMethodParameter, org.springframework.core.annotation.SynthesizingMethodParameter, org.springframework.core.MethodParameter
        /* renamed from: clone */
        public ReturnValueMethodParameter mo2452clone() {
            return new ReturnValueMethodParameter(this);
        }
    }
}
