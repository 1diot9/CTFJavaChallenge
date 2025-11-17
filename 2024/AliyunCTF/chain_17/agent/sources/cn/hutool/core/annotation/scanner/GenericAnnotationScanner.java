package cn.hutool.core.annotation.scanner;

import cn.hutool.core.map.multi.ListValueMap;
import cn.hutool.core.util.ObjectUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/scanner/GenericAnnotationScanner.class */
public class GenericAnnotationScanner implements AnnotationScanner {
    private final AnnotationScanner typeScanner;
    private final AnnotationScanner methodScanner;
    private final AnnotationScanner metaScanner;
    private final AnnotationScanner elementScanner;

    @Override // cn.hutool.core.annotation.scanner.AnnotationScanner
    public boolean support(AnnotatedElement annotatedEle) {
        return true;
    }

    public GenericAnnotationScanner(boolean enableScanMetaAnnotation, boolean enableScanSupperClass, boolean enableScanSupperInterface) {
        this.metaScanner = enableScanMetaAnnotation ? new MetaAnnotationScanner() : new EmptyAnnotationScanner();
        this.typeScanner = new TypeAnnotationScanner(enableScanSupperClass, enableScanSupperInterface, a -> {
            return true;
        }, Collections.emptySet());
        this.methodScanner = new MethodAnnotationScanner(enableScanSupperClass, enableScanSupperInterface, a2 -> {
            return true;
        }, Collections.emptySet());
        this.elementScanner = new ElementAnnotationScanner();
    }

    @Override // cn.hutool.core.annotation.scanner.AnnotationScanner
    public void scan(BiConsumer<Integer, Annotation> consumer, AnnotatedElement annotatedEle, Predicate<Annotation> filter) {
        Predicate<Annotation> filter2 = (Predicate) ObjectUtil.defaultIfNull(filter, (Function<Predicate<Annotation>, ? extends Predicate<Annotation>>) a -> {
            return t -> {
                return true;
            };
        });
        if (ObjectUtil.isNull(annotatedEle)) {
            return;
        }
        if (annotatedEle instanceof Class) {
            scanElements(this.typeScanner, consumer, annotatedEle, filter2);
        } else if (annotatedEle instanceof Method) {
            scanElements(this.methodScanner, consumer, annotatedEle, filter2);
        } else {
            scanElements(this.elementScanner, consumer, annotatedEle, filter2);
        }
    }

    private void scanElements(AnnotationScanner scanner, BiConsumer<Integer, Annotation> consumer, AnnotatedElement annotatedEle, Predicate<Annotation> filter) {
        ListValueMap<Integer, Annotation> classAnnotations = new ListValueMap<>(new LinkedHashMap());
        scanner.scan((index, annotation) -> {
            if (filter.test(annotation)) {
                classAnnotations.putValue(index, annotation);
            }
        }, annotatedEle, filter);
        classAnnotations.forEach((index2, annotations) -> {
            annotations.forEach(annotation2 -> {
                consumer.accept(index2, annotation2);
                this.metaScanner.scan(consumer, annotation2.annotationType(), filter);
            });
        });
    }
}
