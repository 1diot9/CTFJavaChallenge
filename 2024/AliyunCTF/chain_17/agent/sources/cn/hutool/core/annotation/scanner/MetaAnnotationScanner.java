package cn.hutool.core.annotation.scanner;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/scanner/MetaAnnotationScanner.class */
public class MetaAnnotationScanner implements AnnotationScanner {
    private final boolean includeSupperMetaAnnotation;

    public MetaAnnotationScanner(boolean includeSupperMetaAnnotation) {
        this.includeSupperMetaAnnotation = includeSupperMetaAnnotation;
    }

    public MetaAnnotationScanner() {
        this(true);
    }

    @Override // cn.hutool.core.annotation.scanner.AnnotationScanner
    public boolean support(AnnotatedElement annotatedEle) {
        return (annotatedEle instanceof Class) && ClassUtil.isAssignable(Annotation.class, (Class) annotatedEle);
    }

    @Override // cn.hutool.core.annotation.scanner.AnnotationScanner
    public List<Annotation> getAnnotations(AnnotatedElement annotatedEle) {
        List<Annotation> annotations = new ArrayList<>();
        scan((index, annotation) -> {
            annotations.add(annotation);
        }, annotatedEle, annotation2 -> {
            return ObjectUtil.notEqual(annotation2, annotatedEle);
        });
        return annotations;
    }

    @Override // cn.hutool.core.annotation.scanner.AnnotationScanner
    public void scan(BiConsumer<Integer, Annotation> consumer, AnnotatedElement annotatedEle, Predicate<Annotation> filter) {
        Predicate<Annotation> filter2 = (Predicate) ObjectUtil.defaultIfNull(filter, (Function<Predicate<Annotation>, ? extends Predicate<Annotation>>) a -> {
            return t -> {
                return true;
            };
        });
        Set<Class<? extends Annotation>> accessed = new HashSet<>();
        Deque<List<Class<? extends Annotation>>> deque = CollUtil.newLinkedList(CollUtil.newArrayList((Class) annotatedEle));
        int distance = 0;
        do {
            List<Class<? extends Annotation>> annotationTypes = deque.removeFirst();
            for (Class<? extends Annotation> type : annotationTypes) {
                List<Annotation> metaAnnotations = (List) Stream.of((Object[]) type.getAnnotations()).filter(a2 -> {
                    return !AnnotationUtil.isJdkMetaAnnotation(a2.annotationType());
                }).filter(filter2).collect(Collectors.toList());
                for (Annotation metaAnnotation : metaAnnotations) {
                    consumer.accept(Integer.valueOf(distance), metaAnnotation);
                }
                accessed.add(type);
                List<Class<? extends Annotation>> next = (List) metaAnnotations.stream().map((v0) -> {
                    return v0.annotationType();
                }).filter(t -> {
                    return !accessed.contains(t);
                }).collect(Collectors.toList());
                if (CollUtil.isNotEmpty((Collection<?>) next)) {
                    deque.addLast(next);
                }
            }
            distance++;
            if (!this.includeSupperMetaAnnotation) {
                return;
            }
        } while (!deque.isEmpty());
    }
}
