package cn.hutool.core.annotation.scanner;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ObjectUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/scanner/FieldAnnotationScanner.class */
public class FieldAnnotationScanner implements AnnotationScanner {
    @Override // cn.hutool.core.annotation.scanner.AnnotationScanner
    public boolean support(AnnotatedElement annotatedEle) {
        return annotatedEle instanceof Field;
    }

    @Override // cn.hutool.core.annotation.scanner.AnnotationScanner
    public void scan(BiConsumer<Integer, Annotation> consumer, AnnotatedElement annotatedEle, Predicate<Annotation> filter) {
        Predicate<Annotation> filter2 = (Predicate) ObjectUtil.defaultIfNull(filter, (Function<Predicate<Annotation>, ? extends Predicate<Annotation>>) a -> {
            return annotation -> {
                return true;
            };
        });
        for (Annotation annotation : annotatedEle.getAnnotations()) {
            if (AnnotationUtil.isNotJdkMateAnnotation(annotation.annotationType()) && filter2.test(annotation)) {
                consumer.accept(0, annotation);
            }
        }
    }
}
