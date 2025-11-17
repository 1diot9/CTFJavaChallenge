package cn.hutool.core.annotation.scanner;

import cn.hutool.core.collection.CollUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/scanner/TypeAnnotationScanner.class */
public class TypeAnnotationScanner extends AbstractTypeAnnotationScanner<TypeAnnotationScanner> implements AnnotationScanner {
    public TypeAnnotationScanner(boolean includeSupperClass, boolean includeInterfaces, Predicate<Class<?>> filter, Set<Class<?>> excludeTypes) {
        super(includeSupperClass, includeInterfaces, filter, excludeTypes);
    }

    public TypeAnnotationScanner() {
        this(true, true, t -> {
            return true;
        }, CollUtil.newLinkedHashSet(new Class[0]));
    }

    @Override // cn.hutool.core.annotation.scanner.AnnotationScanner
    public boolean support(AnnotatedElement annotatedEle) {
        return annotatedEle instanceof Class;
    }

    @Override // cn.hutool.core.annotation.scanner.AbstractTypeAnnotationScanner
    protected Class<?> getClassFormAnnotatedElement(AnnotatedElement annotatedEle) {
        return (Class) annotatedEle;
    }

    @Override // cn.hutool.core.annotation.scanner.AbstractTypeAnnotationScanner
    protected Annotation[] getAnnotationsFromTargetClass(AnnotatedElement source, int index, Class<?> targetClass) {
        return targetClass.getAnnotations();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.annotation.scanner.AbstractTypeAnnotationScanner
    public TypeAnnotationScanner setIncludeSuperClass(boolean includeSuperClass) {
        return (TypeAnnotationScanner) super.setIncludeSuperClass(includeSuperClass);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.annotation.scanner.AbstractTypeAnnotationScanner
    public TypeAnnotationScanner setIncludeInterfaces(boolean includeInterfaces) {
        return (TypeAnnotationScanner) super.setIncludeInterfaces(includeInterfaces);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/scanner/TypeAnnotationScanner$JdkProxyClassConverter.class */
    public static class JdkProxyClassConverter implements UnaryOperator<Class<?>> {
        @Override // java.util.function.Function
        public Class<?> apply(Class<?> sourceClass) {
            return Proxy.isProxyClass(sourceClass) ? apply((Class<?>) sourceClass.getSuperclass()) : sourceClass;
        }
    }
}
