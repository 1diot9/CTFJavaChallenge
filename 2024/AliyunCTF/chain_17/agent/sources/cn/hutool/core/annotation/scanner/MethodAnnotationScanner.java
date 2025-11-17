package cn.hutool.core.annotation.scanner;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/scanner/MethodAnnotationScanner.class */
public class MethodAnnotationScanner extends AbstractTypeAnnotationScanner<MethodAnnotationScanner> implements AnnotationScanner {
    public MethodAnnotationScanner() {
        this(false);
    }

    public MethodAnnotationScanner(boolean scanSameSignatureMethod) {
        this(scanSameSignatureMethod, targetClass -> {
            return true;
        }, CollUtil.newLinkedHashSet(new Class[0]));
    }

    public MethodAnnotationScanner(boolean scanSameSignatureMethod, Predicate<Class<?>> filter, Set<Class<?>> excludeTypes) {
        super(scanSameSignatureMethod, scanSameSignatureMethod, filter, excludeTypes);
    }

    public MethodAnnotationScanner(boolean includeSuperClass, boolean includeInterfaces, Predicate<Class<?>> filter, Set<Class<?>> excludeTypes) {
        super(includeSuperClass, includeInterfaces, filter, excludeTypes);
    }

    @Override // cn.hutool.core.annotation.scanner.AnnotationScanner
    public boolean support(AnnotatedElement annotatedEle) {
        return annotatedEle instanceof Method;
    }

    @Override // cn.hutool.core.annotation.scanner.AbstractTypeAnnotationScanner
    protected Class<?> getClassFormAnnotatedElement(AnnotatedElement annotatedElement) {
        return ((Method) annotatedElement).getDeclaringClass();
    }

    @Override // cn.hutool.core.annotation.scanner.AbstractTypeAnnotationScanner
    protected Annotation[] getAnnotationsFromTargetClass(AnnotatedElement source, int index, Class<?> targetClass) {
        Method sourceMethod = (Method) source;
        return (Annotation[]) Stream.of((Object[]) ClassUtil.getDeclaredMethods(targetClass)).filter(superMethod -> {
            return !superMethod.isBridge();
        }).filter(superMethod2 -> {
            return hasSameSignature(sourceMethod, superMethod2);
        }).map((v0) -> {
            return v0.getAnnotations();
        }).flatMap((v0) -> {
            return Stream.of(v0);
        }).toArray(x$0 -> {
            return new Annotation[x$0];
        });
    }

    public MethodAnnotationScanner setScanSameSignatureMethod(boolean scanSuperMethodIfOverride) {
        setIncludeInterfaces(scanSuperMethodIfOverride);
        setIncludeSuperClass(scanSuperMethodIfOverride);
        return this;
    }

    private boolean hasSameSignature(Method sourceMethod, Method superMethod) {
        if (false == StrUtil.equals(sourceMethod.getName(), superMethod.getName())) {
            return false;
        }
        Class<?>[] sourceParameterTypes = sourceMethod.getParameterTypes();
        Class<?>[] targetParameterTypes = superMethod.getParameterTypes();
        if (sourceParameterTypes.length != targetParameterTypes.length || !ArrayUtil.containsAll(sourceParameterTypes, targetParameterTypes)) {
            return false;
        }
        return ClassUtil.isAssignable(superMethod.getReturnType(), sourceMethod.getReturnType());
    }
}
