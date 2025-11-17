package cn.hutool.core.annotation.scanner;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/scanner/AnnotationScanner.class */
public interface AnnotationScanner {
    public static final AnnotationScanner NOTHING = new EmptyAnnotationScanner();
    public static final AnnotationScanner DIRECTLY = new GenericAnnotationScanner(false, false, false);
    public static final AnnotationScanner DIRECTLY_AND_META_ANNOTATION = new GenericAnnotationScanner(true, false, false);
    public static final AnnotationScanner SUPERCLASS = new GenericAnnotationScanner(false, true, false);
    public static final AnnotationScanner SUPERCLASS_AND_META_ANNOTATION = new GenericAnnotationScanner(true, true, false);
    public static final AnnotationScanner INTERFACE = new GenericAnnotationScanner(false, false, true);
    public static final AnnotationScanner INTERFACE_AND_META_ANNOTATION = new GenericAnnotationScanner(true, false, true);
    public static final AnnotationScanner TYPE_HIERARCHY = new GenericAnnotationScanner(false, true, true);
    public static final AnnotationScanner TYPE_HIERARCHY_AND_META_ANNOTATION = new GenericAnnotationScanner(true, true, true);

    static List<Annotation> scanByAnySupported(AnnotatedElement annotatedEle, AnnotationScanner... scanners) {
        if (ObjectUtil.isNull(annotatedEle) && ArrayUtil.isNotEmpty((Object[]) scanners)) {
            return Collections.emptyList();
        }
        return (List) Stream.of((Object[]) scanners).filter(scanner -> {
            return scanner.support(annotatedEle);
        }).findFirst().map(scanner2 -> {
            return scanner2.getAnnotations(annotatedEle);
        }).orElseGet(Collections::emptyList);
    }

    static List<Annotation> scanByAllSupported(AnnotatedElement annotatedEle, AnnotationScanner... scanners) {
        if (ObjectUtil.isNull(annotatedEle) && ArrayUtil.isNotEmpty((Object[]) scanners)) {
            return Collections.emptyList();
        }
        return (List) Stream.of((Object[]) scanners).map(scanner -> {
            return scanner.getAnnotationsIfSupport(annotatedEle);
        }).flatMap((v0) -> {
            return v0.stream();
        }).collect(Collectors.toList());
    }

    default boolean support(AnnotatedElement annotatedEle) {
        return false;
    }

    default List<Annotation> getAnnotations(AnnotatedElement annotatedEle) {
        List<Annotation> annotations = new ArrayList<>();
        scan((index, annotation) -> {
            annotations.add(annotation);
        }, annotatedEle, null);
        return annotations;
    }

    default List<Annotation> getAnnotationsIfSupport(AnnotatedElement annotatedEle) {
        return support(annotatedEle) ? getAnnotations(annotatedEle) : Collections.emptyList();
    }

    default void scan(BiConsumer<Integer, Annotation> consumer, AnnotatedElement annotatedEle, Predicate<Annotation> filter) {
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

    default void scanIfSupport(BiConsumer<Integer, Annotation> consumer, AnnotatedElement annotatedEle, Predicate<Annotation> filter) {
        if (support(annotatedEle)) {
            scan(consumer, annotatedEle, filter);
        }
    }
}
