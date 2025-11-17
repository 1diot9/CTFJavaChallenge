package cn.hutool.core.annotation.scanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/scanner/EmptyAnnotationScanner.class */
public class EmptyAnnotationScanner implements AnnotationScanner {
    @Override // cn.hutool.core.annotation.scanner.AnnotationScanner
    public boolean support(AnnotatedElement annotatedEle) {
        return true;
    }

    @Override // cn.hutool.core.annotation.scanner.AnnotationScanner
    public List<Annotation> getAnnotations(AnnotatedElement annotatedEle) {
        return Collections.emptyList();
    }

    @Override // cn.hutool.core.annotation.scanner.AnnotationScanner
    public void scan(BiConsumer<Integer, Annotation> consumer, AnnotatedElement annotatedEle, Predicate<Annotation> filter) {
    }
}
