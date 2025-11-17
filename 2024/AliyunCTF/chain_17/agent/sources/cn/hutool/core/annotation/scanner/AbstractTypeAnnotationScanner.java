package cn.hutool.core.annotation.scanner;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.annotation.scanner.AbstractTypeAnnotationScanner;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/scanner/AbstractTypeAnnotationScanner.class */
public abstract class AbstractTypeAnnotationScanner<T extends AbstractTypeAnnotationScanner<T>> implements AnnotationScanner {
    private boolean includeSuperClass;
    private boolean includeInterfaces;
    private Predicate<Class<?>> filter;
    private final Set<Class<?>> excludeTypes;
    private final List<UnaryOperator<Class<?>>> converters;
    private boolean hasConverters;
    private final T typedThis;

    protected abstract Class<?> getClassFormAnnotatedElement(AnnotatedElement annotatedElement);

    protected abstract Annotation[] getAnnotationsFromTargetClass(AnnotatedElement annotatedElement, int i, Class<?> cls);

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractTypeAnnotationScanner(boolean includeSuperClass, boolean includeInterfaces, Predicate<Class<?>> filter, Set<Class<?>> excludeTypes) {
        Assert.notNull(filter, "filter must not null", new Object[0]);
        Assert.notNull(excludeTypes, "excludeTypes must not null", new Object[0]);
        this.includeSuperClass = includeSuperClass;
        this.includeInterfaces = includeInterfaces;
        this.filter = filter;
        this.excludeTypes = excludeTypes;
        this.converters = new ArrayList();
        this.typedThis = this;
    }

    public boolean isIncludeSuperClass() {
        return this.includeSuperClass;
    }

    public boolean isIncludeInterfaces() {
        return this.includeInterfaces;
    }

    public T setFilter(Predicate<Class<?>> filter) {
        Assert.notNull(filter, "filter must not null", new Object[0]);
        this.filter = filter;
        return this.typedThis;
    }

    public T addExcludeTypes(Class<?>... excludeTypes) {
        CollUtil.addAll((Collection) this.excludeTypes, (Object[]) excludeTypes);
        return this.typedThis;
    }

    public T addConverters(UnaryOperator<Class<?>> converter) {
        Assert.notNull(converter, "converter must not null", new Object[0]);
        this.converters.add(converter);
        if (!this.hasConverters) {
            this.hasConverters = CollUtil.isNotEmpty((Collection<?>) this.converters);
        }
        return this.typedThis;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public T setIncludeSuperClass(boolean includeSuperClass) {
        this.includeSuperClass = includeSuperClass;
        return this.typedThis;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public T setIncludeInterfaces(boolean includeInterfaces) {
        this.includeInterfaces = includeInterfaces;
        return this.typedThis;
    }

    @Override // cn.hutool.core.annotation.scanner.AnnotationScanner
    public void scan(BiConsumer<Integer, Annotation> consumer, AnnotatedElement annotatedEle, Predicate<Annotation> filter) {
        Predicate<Annotation> filter2 = (Predicate) ObjectUtil.defaultIfNull(filter, (Function<Predicate<Annotation>, ? extends Predicate<Annotation>>) a -> {
            return annotation -> {
                return true;
            };
        });
        Class<?> sourceClass = getClassFormAnnotatedElement(annotatedEle);
        Deque<List<Class<?>>> classDeque = CollUtil.newLinkedList(CollUtil.newArrayList(sourceClass));
        Set<Class<?>> accessedTypes = new LinkedHashSet<>();
        int index = 0;
        while (!classDeque.isEmpty()) {
            List<Class<?>> currClassQueue = classDeque.removeFirst();
            List<Class<?>> nextClassQueue = new ArrayList<>();
            Iterator<Class<?>> it = currClassQueue.iterator();
            while (it.hasNext()) {
                Class<?> targetClass = convert(it.next());
                if (!isNotNeedProcess(accessedTypes, targetClass)) {
                    accessedTypes.add(targetClass);
                    scanSuperClassIfNecessary(nextClassQueue, targetClass);
                    scanInterfaceIfNecessary(nextClassQueue, targetClass);
                    Annotation[] targetAnnotations = getAnnotationsFromTargetClass(annotatedEle, index, targetClass);
                    for (Annotation annotation : targetAnnotations) {
                        if (AnnotationUtil.isNotJdkMateAnnotation(annotation.annotationType()) && filter2.test(annotation)) {
                            consumer.accept(Integer.valueOf(index), annotation);
                        }
                    }
                    index++;
                }
            }
            if (CollUtil.isNotEmpty((Collection<?>) nextClassQueue)) {
                classDeque.addLast(nextClassQueue);
            }
        }
    }

    protected boolean isNotNeedProcess(Set<Class<?>> accessedTypes, Class<?> targetClass) {
        return ObjectUtil.isNull(targetClass) || accessedTypes.contains(targetClass) || this.excludeTypes.contains(targetClass) || this.filter.negate().test(targetClass);
    }

    protected void scanInterfaceIfNecessary(List<Class<?>> nextClasses, Class<?> targetClass) {
        if (this.includeInterfaces) {
            Class<?>[] interfaces = targetClass.getInterfaces();
            if (ArrayUtil.isNotEmpty((Object[]) interfaces)) {
                CollUtil.addAll((Collection) nextClasses, (Object[]) interfaces);
            }
        }
    }

    protected void scanSuperClassIfNecessary(List<Class<?>> nextClassQueue, Class<?> targetClass) {
        if (this.includeSuperClass) {
            Class<?> superClass = targetClass.getSuperclass();
            if (!ObjectUtil.equals(superClass, Object.class) && ObjectUtil.isNotNull(superClass)) {
                nextClassQueue.add(superClass);
            }
        }
    }

    protected Class<?> convert(Class<?> target) {
        if (this.hasConverters) {
            for (UnaryOperator<Class<?>> converter : this.converters) {
                target = (Class) converter.apply(target);
            }
        }
        return target;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/scanner/AbstractTypeAnnotationScanner$JdkProxyClassConverter.class */
    public static class JdkProxyClassConverter implements UnaryOperator<Class<?>> {
        @Override // java.util.function.Function
        public Class<?> apply(Class<?> sourceClass) {
            return Proxy.isProxyClass(sourceClass) ? apply((Class<?>) sourceClass.getSuperclass()) : sourceClass;
        }
    }
}
