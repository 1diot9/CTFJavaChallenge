package org.springframework.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import kotlin.Unit;
import kotlin.reflect.KFunction;
import kotlin.reflect.KParameter;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/MethodParameter.class */
public class MethodParameter {
    private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];
    private final Executable executable;
    private final int parameterIndex;

    @Nullable
    private volatile Parameter parameter;
    private int nestingLevel;

    @Nullable
    Map<Integer, Integer> typeIndexesPerLevel;

    @Nullable
    private volatile Class<?> containingClass;

    @Nullable
    private volatile Class<?> parameterType;

    @Nullable
    private volatile Type genericParameterType;

    @Nullable
    private volatile Annotation[] parameterAnnotations;

    @Nullable
    private volatile ParameterNameDiscoverer parameterNameDiscoverer;

    @Nullable
    volatile String parameterName;

    @Nullable
    private volatile MethodParameter nestedMethodParameter;

    public MethodParameter(Method method, int parameterIndex) {
        this(method, parameterIndex, 1);
    }

    public MethodParameter(Method method, int parameterIndex, int nestingLevel) {
        Assert.notNull(method, "Method must not be null");
        this.executable = method;
        this.parameterIndex = validateIndex(method, parameterIndex);
        this.nestingLevel = nestingLevel;
    }

    public MethodParameter(Constructor<?> constructor, int parameterIndex) {
        this(constructor, parameterIndex, 1);
    }

    public MethodParameter(Constructor<?> constructor, int parameterIndex, int nestingLevel) {
        Assert.notNull(constructor, "Constructor must not be null");
        this.executable = constructor;
        this.parameterIndex = validateIndex(constructor, parameterIndex);
        this.nestingLevel = nestingLevel;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MethodParameter(Executable executable, int parameterIndex, @Nullable Class<?> containingClass) {
        Assert.notNull(executable, "Executable must not be null");
        this.executable = executable;
        this.parameterIndex = validateIndex(executable, parameterIndex);
        this.nestingLevel = 1;
        this.containingClass = containingClass;
    }

    public MethodParameter(MethodParameter original) {
        Assert.notNull(original, "Original must not be null");
        this.executable = original.executable;
        this.parameterIndex = original.parameterIndex;
        this.parameter = original.parameter;
        this.nestingLevel = original.nestingLevel;
        this.typeIndexesPerLevel = original.typeIndexesPerLevel;
        this.containingClass = original.containingClass;
        this.parameterType = original.parameterType;
        this.genericParameterType = original.genericParameterType;
        this.parameterAnnotations = original.parameterAnnotations;
        this.parameterNameDiscoverer = original.parameterNameDiscoverer;
        this.parameterName = original.parameterName;
    }

    @Nullable
    public Method getMethod() {
        Executable executable = this.executable;
        if (!(executable instanceof Method)) {
            return null;
        }
        Method method = (Method) executable;
        return method;
    }

    @Nullable
    public Constructor<?> getConstructor() {
        Executable executable = this.executable;
        if (!(executable instanceof Constructor)) {
            return null;
        }
        Constructor<?> constructor = (Constructor) executable;
        return constructor;
    }

    public Class<?> getDeclaringClass() {
        return this.executable.getDeclaringClass();
    }

    public Member getMember() {
        return this.executable;
    }

    public AnnotatedElement getAnnotatedElement() {
        return this.executable;
    }

    public Executable getExecutable() {
        return this.executable;
    }

    public Parameter getParameter() {
        if (this.parameterIndex < 0) {
            throw new IllegalStateException("Cannot retrieve Parameter descriptor for method return type");
        }
        Parameter parameter = this.parameter;
        if (parameter == null) {
            parameter = getExecutable().getParameters()[this.parameterIndex];
            this.parameter = parameter;
        }
        return parameter;
    }

    public int getParameterIndex() {
        return this.parameterIndex;
    }

    @Deprecated
    public void increaseNestingLevel() {
        this.nestingLevel++;
    }

    @Deprecated
    public void decreaseNestingLevel() {
        getTypeIndexesPerLevel().remove(Integer.valueOf(this.nestingLevel));
        this.nestingLevel--;
    }

    public int getNestingLevel() {
        return this.nestingLevel;
    }

    public MethodParameter withTypeIndex(int typeIndex) {
        return nested(this.nestingLevel, Integer.valueOf(typeIndex));
    }

    @Deprecated
    public void setTypeIndexForCurrentLevel(int typeIndex) {
        getTypeIndexesPerLevel().put(Integer.valueOf(this.nestingLevel), Integer.valueOf(typeIndex));
    }

    @Nullable
    public Integer getTypeIndexForCurrentLevel() {
        return getTypeIndexForLevel(this.nestingLevel);
    }

    @Nullable
    public Integer getTypeIndexForLevel(int nestingLevel) {
        return getTypeIndexesPerLevel().get(Integer.valueOf(nestingLevel));
    }

    private Map<Integer, Integer> getTypeIndexesPerLevel() {
        if (this.typeIndexesPerLevel == null) {
            this.typeIndexesPerLevel = new HashMap(4);
        }
        return this.typeIndexesPerLevel;
    }

    public MethodParameter nested() {
        return nested(null);
    }

    public MethodParameter nested(@Nullable Integer typeIndex) {
        MethodParameter nestedParam = this.nestedMethodParameter;
        if (nestedParam != null && typeIndex == null) {
            return nestedParam;
        }
        MethodParameter nestedParam2 = nested(this.nestingLevel + 1, typeIndex);
        if (typeIndex == null) {
            this.nestedMethodParameter = nestedParam2;
        }
        return nestedParam2;
    }

    private MethodParameter nested(int nestingLevel, @Nullable Integer typeIndex) {
        MethodParameter copy = mo2365clone();
        copy.nestingLevel = nestingLevel;
        if (this.typeIndexesPerLevel != null) {
            copy.typeIndexesPerLevel = new HashMap(this.typeIndexesPerLevel);
        }
        if (typeIndex != null) {
            copy.getTypeIndexesPerLevel().put(Integer.valueOf(copy.nestingLevel), typeIndex);
        }
        copy.parameterType = null;
        copy.genericParameterType = null;
        return copy;
    }

    public boolean isOptional() {
        return getParameterType() == Optional.class || hasNullableAnnotation() || (KotlinDetector.isKotlinReflectPresent() && KotlinDetector.isKotlinType(getContainingClass()) && KotlinDelegate.isOptional(this));
    }

    private boolean hasNullableAnnotation() {
        for (Annotation ann : getParameterAnnotations()) {
            if ("Nullable".equals(ann.annotationType().getSimpleName())) {
                return true;
            }
        }
        return false;
    }

    public MethodParameter nestedIfOptional() {
        return getParameterType() == Optional.class ? nested() : this;
    }

    public MethodParameter withContainingClass(@Nullable Class<?> containingClass) {
        MethodParameter result = mo2365clone();
        result.containingClass = containingClass;
        result.parameterType = null;
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Deprecated
    public void setContainingClass(Class<?> containingClass) {
        this.containingClass = containingClass;
        this.parameterType = null;
    }

    public Class<?> getContainingClass() {
        Class<?> containingClass = this.containingClass;
        return containingClass != null ? containingClass : getDeclaringClass();
    }

    @Deprecated
    void setParameterType(@Nullable Class<?> parameterType) {
        this.parameterType = parameterType;
    }

    public Class<?> getParameterType() {
        Class<?> paramType = this.parameterType;
        if (paramType != null) {
            return paramType;
        }
        if (getContainingClass() != getDeclaringClass()) {
            paramType = ResolvableType.forMethodParameter(this, (Type) null, 1).resolve();
        }
        if (paramType == null) {
            paramType = computeParameterType();
        }
        this.parameterType = paramType;
        return paramType;
    }

    public Type getGenericParameterType() {
        Type type;
        Type paramType = this.genericParameterType;
        if (paramType == null) {
            if (this.parameterIndex < 0) {
                Method method = getMethod();
                if (method != null) {
                    type = (KotlinDetector.isKotlinReflectPresent() && KotlinDetector.isKotlinType(getContainingClass())) ? KotlinDelegate.getGenericReturnType(method) : method.getGenericReturnType();
                } else {
                    type = Void.TYPE;
                }
                paramType = type;
            } else {
                Type[] genericParameterTypes = this.executable.getGenericParameterTypes();
                int index = this.parameterIndex;
                if ((this.executable instanceof Constructor) && ClassUtils.isInnerClass(this.executable.getDeclaringClass()) && genericParameterTypes.length == this.executable.getParameterCount() - 1) {
                    index = this.parameterIndex - 1;
                }
                paramType = (index < 0 || index >= genericParameterTypes.length) ? computeParameterType() : genericParameterTypes[index];
            }
            this.genericParameterType = paramType;
        }
        return paramType;
    }

    private Class<?> computeParameterType() {
        if (this.parameterIndex < 0) {
            Method method = getMethod();
            if (method == null) {
                return Void.TYPE;
            }
            if (KotlinDetector.isKotlinReflectPresent() && KotlinDetector.isKotlinType(getContainingClass())) {
                return KotlinDelegate.getReturnType(method);
            }
            return method.getReturnType();
        }
        return this.executable.getParameterTypes()[this.parameterIndex];
    }

    public Class<?> getNestedParameterType() {
        if (this.nestingLevel > 1) {
            Type type = getGenericParameterType();
            for (int i = 2; i <= this.nestingLevel; i++) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    Type[] args = parameterizedType.getActualTypeArguments();
                    Integer index = getTypeIndexForLevel(i);
                    type = args[index != null ? index.intValue() : args.length - 1];
                }
            }
            if (type instanceof Class) {
                Class<?> clazz = (Class) type;
                return clazz;
            }
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType2 = (ParameterizedType) type;
                Type arg = parameterizedType2.getRawType();
                if (arg instanceof Class) {
                    Class<?> clazz2 = (Class) arg;
                    return clazz2;
                }
                return Object.class;
            }
            return Object.class;
        }
        return getParameterType();
    }

    public Type getNestedGenericParameterType() {
        if (this.nestingLevel > 1) {
            Type type = getGenericParameterType();
            for (int i = 2; i <= this.nestingLevel; i++) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    Type[] args = parameterizedType.getActualTypeArguments();
                    Integer index = getTypeIndexForLevel(i);
                    type = args[index != null ? index.intValue() : args.length - 1];
                }
            }
            return type;
        }
        return getGenericParameterType();
    }

    public Annotation[] getMethodAnnotations() {
        return adaptAnnotationArray(getAnnotatedElement().getAnnotations());
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Nullable
    public <A extends Annotation> A getMethodAnnotation(Class<A> cls) {
        Annotation annotation = getAnnotatedElement().getAnnotation(cls);
        if (annotation != null) {
            return (A) adaptAnnotation(annotation);
        }
        return null;
    }

    public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
        return getAnnotatedElement().isAnnotationPresent(annotationType);
    }

    public Annotation[] getParameterAnnotations() {
        Annotation[] paramAnns = this.parameterAnnotations;
        if (paramAnns == null) {
            Annotation[][] annotationArray = this.executable.getParameterAnnotations();
            int index = this.parameterIndex;
            if ((this.executable instanceof Constructor) && ClassUtils.isInnerClass(this.executable.getDeclaringClass()) && annotationArray.length == this.executable.getParameterCount() - 1) {
                index = this.parameterIndex - 1;
            }
            paramAnns = (index < 0 || index >= annotationArray.length) ? EMPTY_ANNOTATION_ARRAY : adaptAnnotationArray(annotationArray[index]);
            this.parameterAnnotations = paramAnns;
        }
        return paramAnns;
    }

    public boolean hasParameterAnnotations() {
        return getParameterAnnotations().length != 0;
    }

    @Nullable
    public <A extends Annotation> A getParameterAnnotation(Class<A> cls) {
        for (Annotation annotation : getParameterAnnotations()) {
            A a = (A) annotation;
            if (cls.isInstance(a)) {
                return a;
            }
        }
        return null;
    }

    public <A extends Annotation> boolean hasParameterAnnotation(Class<A> annotationType) {
        return getParameterAnnotation(annotationType) != null;
    }

    public void initParameterNameDiscovery(@Nullable ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    @Nullable
    public String getParameterName() {
        if (this.parameterIndex < 0) {
            return null;
        }
        ParameterNameDiscoverer discoverer = this.parameterNameDiscoverer;
        if (discoverer != null) {
            String[] parameterNames = null;
            Executable executable = this.executable;
            if (executable instanceof Method) {
                Method method = (Method) executable;
                parameterNames = discoverer.getParameterNames(method);
            } else {
                Executable executable2 = this.executable;
                if (executable2 instanceof Constructor) {
                    Constructor<?> constructor = (Constructor) executable2;
                    parameterNames = discoverer.getParameterNames(constructor);
                }
            }
            if (parameterNames != null) {
                this.parameterName = parameterNames[this.parameterIndex];
            }
            this.parameterNameDiscoverer = null;
        }
        return this.parameterName;
    }

    protected <A extends Annotation> A adaptAnnotation(A annotation) {
        return annotation;
    }

    protected Annotation[] adaptAnnotationArray(Annotation[] annotations) {
        return annotations;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof MethodParameter) {
                MethodParameter that = (MethodParameter) other;
                if (getContainingClass() != that.getContainingClass() || !ObjectUtils.nullSafeEquals(this.typeIndexesPerLevel, that.typeIndexesPerLevel) || this.nestingLevel != that.nestingLevel || this.parameterIndex != that.parameterIndex || !this.executable.equals(that.executable)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (31 * this.executable.hashCode()) + this.parameterIndex;
    }

    public String toString() {
        Method method = getMethod();
        return (method != null ? "method '" + method.getName() + "'" : BeanDefinitionParserDelegate.AUTOWIRE_CONSTRUCTOR_VALUE) + " parameter " + this.parameterIndex;
    }

    @Override // 
    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
    public MethodParameter mo2365clone() {
        return new MethodParameter(this);
    }

    @Deprecated
    public static MethodParameter forMethodOrConstructor(Object methodOrConstructor, int parameterIndex) {
        if (!(methodOrConstructor instanceof Executable)) {
            throw new IllegalArgumentException("Given object [" + methodOrConstructor + "] is neither a Method nor a Constructor");
        }
        Executable executable = (Executable) methodOrConstructor;
        return forExecutable(executable, parameterIndex);
    }

    public static MethodParameter forExecutable(Executable executable, int parameterIndex) {
        if (executable instanceof Method) {
            Method method = (Method) executable;
            return new MethodParameter(method, parameterIndex);
        }
        if (executable instanceof Constructor) {
            Constructor<?> constructor = (Constructor) executable;
            return new MethodParameter(constructor, parameterIndex);
        }
        throw new IllegalArgumentException("Not a Method/Constructor: " + executable);
    }

    public static MethodParameter forParameter(Parameter parameter) {
        return forExecutable(parameter.getDeclaringExecutable(), findParameterIndex(parameter));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int findParameterIndex(Parameter parameter) {
        Executable executable = parameter.getDeclaringExecutable();
        Parameter[] allParams = executable.getParameters();
        for (int i = 0; i < allParams.length; i++) {
            if (parameter == allParams[i]) {
                return i;
            }
        }
        for (int i2 = 0; i2 < allParams.length; i2++) {
            if (parameter.equals(allParams[i2])) {
                return i2;
            }
        }
        throw new IllegalArgumentException("Given parameter [" + parameter + "] does not match any parameter in the declaring executable");
    }

    private static int validateIndex(Executable executable, int parameterIndex) {
        int count = executable.getParameterCount();
        Assert.isTrue(parameterIndex >= -1 && parameterIndex < count, (Supplier<String>) () -> {
            return "Parameter index needs to be between -1 and " + (count - 1);
        });
        return parameterIndex;
    }

    public static MethodParameter forFieldAwareConstructor(Constructor<?> ctor, int parameterIndex, String fieldName) {
        return new FieldAwareConstructorParameter(ctor, parameterIndex, fieldName);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/MethodParameter$FieldAwareConstructorParameter.class */
    private static class FieldAwareConstructorParameter extends MethodParameter {

        @Nullable
        private volatile Annotation[] combinedAnnotations;

        @Override // org.springframework.core.MethodParameter
        /* renamed from: clone */
        public /* bridge */ /* synthetic */ Object mo2365clone() throws CloneNotSupportedException {
            return super.mo2365clone();
        }

        public FieldAwareConstructorParameter(Constructor<?> constructor, int parameterIndex, String fieldName) {
            super(constructor, parameterIndex);
            this.parameterName = fieldName;
        }

        @Override // org.springframework.core.MethodParameter
        public Annotation[] getParameterAnnotations() {
            String parameterName = this.parameterName;
            Assert.state(parameterName != null, "Parameter name not initialized");
            Annotation[] anns = this.combinedAnnotations;
            if (anns == null) {
                anns = super.getParameterAnnotations();
                try {
                    Field field = getDeclaringClass().getDeclaredField(parameterName);
                    Annotation[] fieldAnns = field.getAnnotations();
                    if (fieldAnns.length > 0) {
                        List<Annotation> merged = new ArrayList<>(anns.length + fieldAnns.length);
                        merged.addAll(Arrays.asList(anns));
                        for (Annotation fieldAnn : fieldAnns) {
                            boolean existingType = false;
                            int length = anns.length;
                            int i = 0;
                            while (true) {
                                if (i >= length) {
                                    break;
                                }
                                Annotation ann = anns[i];
                                if (ann.annotationType() != fieldAnn.annotationType()) {
                                    i++;
                                } else {
                                    existingType = true;
                                    break;
                                }
                            }
                            if (!existingType) {
                                merged.add(fieldAnn);
                            }
                        }
                        anns = (Annotation[]) merged.toArray(new Annotation[0]);
                    }
                } catch (NoSuchFieldException | SecurityException e) {
                }
                this.combinedAnnotations = anns;
            }
            return anns;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/MethodParameter$KotlinDelegate.class */
    public static class KotlinDelegate {
        private KotlinDelegate() {
        }

        public static boolean isOptional(MethodParameter param) {
            KFunction<?> function;
            Predicate<KParameter> predicate;
            Method method = param.getMethod();
            int index = param.getParameterIndex();
            if (method != null && index == -1) {
                KFunction<?> function2 = ReflectJvmMapping.getKotlinFunction(method);
                return function2 != null && function2.getReturnType().isMarkedNullable();
            }
            if (method != null) {
                if (param.getParameterType().getName().equals("kotlin.coroutines.Continuation")) {
                    return true;
                }
                function = ReflectJvmMapping.getKotlinFunction(method);
                predicate = p -> {
                    return KParameter.Kind.VALUE.equals(p.getKind());
                };
            } else {
                Constructor<?> ctor = param.getConstructor();
                Assert.state(ctor != null, "Neither method nor constructor found");
                function = ReflectJvmMapping.getKotlinFunction(ctor);
                predicate = p2 -> {
                    return KParameter.Kind.VALUE.equals(p2.getKind()) || KParameter.Kind.INSTANCE.equals(p2.getKind());
                };
            }
            if (function != null) {
                int i = 0;
                for (KParameter kParameter : function.getParameters()) {
                    if (predicate.test(kParameter)) {
                        int i2 = i;
                        i++;
                        if (index == i2) {
                            return kParameter.getType().isMarkedNullable() || kParameter.isOptional();
                        }
                    }
                }
                return false;
            }
            return false;
        }

        private static Type getGenericReturnType(Method method) {
            try {
                KFunction<?> function = ReflectJvmMapping.getKotlinFunction(method);
                if (function != null && function.isSuspend()) {
                    return ReflectJvmMapping.getJavaType(function.getReturnType());
                }
            } catch (UnsupportedOperationException e) {
            }
            return method.getGenericReturnType();
        }

        private static Class<?> getReturnType(Method method) {
            try {
                KFunction<?> function = ReflectJvmMapping.getKotlinFunction(method);
                if (function != null && function.isSuspend()) {
                    Type paramType = ReflectJvmMapping.getJavaType(function.getReturnType());
                    if (paramType == Unit.class) {
                        paramType = Void.TYPE;
                    }
                    return ResolvableType.forType(paramType).resolve(method.getReturnType());
                }
            } catch (UnsupportedOperationException e) {
            }
            return method.getReturnType();
        }
    }
}
